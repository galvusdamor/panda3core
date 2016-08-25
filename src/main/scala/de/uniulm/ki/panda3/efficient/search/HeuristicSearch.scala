package de.uniulm.ki.panda3.efficient.search

import java.io.FileInputStream
import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.configuration.{EfficientSearchAlgorithm, AbortFunction, ResultFunction}
import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.heuristic.{EfficientNumberOfFlaws, EfficientHeuristic}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientOpenPrecondition, EfficientCausalThreat}
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.compiler.pruning.PruneHierarchy
import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, SHOPMethodCompiler, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{LiftedForwardSearchReachabilityAnalysis, GroundedForwardSearchReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.search.SearchNode
import de.uniulm.ki.util.{TimeCapsule, InformationCapsule, Dot2PdfCompiler}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class HeuristicSearch[Payload](heuristic: EfficientHeuristic[Payload], addNumberOfPlanSteps: Boolean, addDepth: Boolean, continueOnSolution: Boolean, invertCosts: Boolean = false)
  extends EfficientSearchAlgorithm[Payload] {

  override def startSearch(domain: EfficientDomain, initialPlan: EfficientPlan, nodeLimit: Option[Int], timeLimit: Option[Int], releaseEvery: Option[Int], printSearchInfo: Boolean,
                           buildTree: Boolean, informationCapsule: InformationCapsule, timeCapsule: TimeCapsule):
  (EfficientSearchNode[Payload], Semaphore, ResultFunction[EfficientPlan], AbortFunction) = {
    import de.uniulm.ki.panda3.configuration.Timings._
    import de.uniulm.ki.panda3.configuration.Information._
    import scala.math.Ordering.Implicits._


    val semaphore: Semaphore = new Semaphore(0)
    val root = new EfficientSearchNode[Payload](0, initialPlan, null, Double.MaxValue)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var lastReportTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes
    var recentNodes: Int = 0 // count the nodes

    var abort = false

    val searchQueue = new mutable.PriorityQueue[(EfficientSearchNode[Payload], Int)]()
    var result: Seq[EfficientPlan] = Nil
    searchQueue.enqueue((root, 0))

    var lowestHeuristicFound = Double.MaxValue
    var minFlaw = Integer.MAX_VALUE
    var minHeuristicCurrentInterval = Double.MaxValue
    var maxHeuristicCurrentInterval = -Double.MaxValue

    informationCapsule increment NUMBER_OF_NODES
    informationCapsule.addToDistribution(PLAN_SIZE, initialPlan.numberOfPlanSteps)

    def heuristicSearch() = {
      println("CONT " + continueOnSolution)
      while (searchQueue.nonEmpty && (continueOnSolution || result.isEmpty) && nodeLimit.getOrElse(Int.MaxValue) >= nodes &&
        initTime + timeLimit.getOrElse(Int.MaxValue).toLong * 1000 >= System.currentTimeMillis()) {
        val (myNode, depth) = searchQueue.dequeue()
        val plan = myNode.plan
        timeCapsule start SEARCH_FLAW_COMPUTATION
        val flaws = plan.flaws
        timeCapsule stop SEARCH_FLAW_COMPUTATION
        minFlaw = Math.min(minFlaw, flaws.length)

        informationCapsule increment NUMBER_OF_EXPANDED_NODES

        //println("PLAN " + plan.numberOfPlanSteps + "/" + plan.numberOfAllPlanSteps + " @ " + myNode.heuristic)

        // heuristic statistics
        if (myNode.heuristic < lowestHeuristicFound) {
          lowestHeuristicFound = myNode.heuristic
          if (printSearchInfo) println("Found new lowest heuristic value: " + lowestHeuristicFound + " @ plan #" + nodes)
        }
        minHeuristicCurrentInterval = Math.min(minHeuristicCurrentInterval, myNode.heuristic)
        maxHeuristicCurrentInterval = Math.max(maxHeuristicCurrentInterval, myNode.heuristic)


        //if (nodes % 300 == 0 && nodes > 0) {
        if (lastReportTime + 333 < System.currentTimeMillis()) {
          val nTime = System.currentTimeMillis()
          val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
          val npsRecent = recentNodes.asInstanceOf[Double] / (nTime - lastReportTime) * 1000
          if (printSearchInfo) println("Plans Expanded: " + nodes + " node/sec avg: " + nps.toInt + " recent: " + npsRecent.toInt + " Queue size " + searchQueue.length + " Recently lowest" +
                                         " " +
                                         "Heuristic " + minHeuristicCurrentInterval + " Recently highest Heuristic " + maxHeuristicCurrentInterval)
          minHeuristicCurrentInterval = Double.MaxValue
          maxHeuristicCurrentInterval = -Double.MaxValue
          lastReportTime = nTime
          recentNodes = 0
        }
        nodes += 1
        recentNodes += 1


        if (flaws.length == 0) {
          result = result :+ plan
          println("\t\t\t\t SOL")
          myNode.setNotDirty()
        } else {
          if (buildTree) myNode.modifications = new Array[Array[EfficientModification]](flaws.length)
          var flawnum = 0
          myNode.selectedFlaw = 0
          var smallFlawNumMod = Integer.MAX_VALUE
          var smallFlawActionNumber = -1
          var smallFlawIsCausalThreat = false

          var zeroFound = false

          timeCapsule start (if (buildTree) SEARCH_FLAW_RESOLVER else SEARCH_FLAW_RESOLVER_ESTIMATION)
          while (flawnum < flaws.length) {
            if (buildTree) {
              myNode.modifications(flawnum) = flaws(flawnum).resolver
              if (myNode.modifications(flawnum).length < smallFlawNumMod) {
                smallFlawNumMod = myNode.modifications(flawnum).length
                myNode.selectedFlaw = flawnum
              }
            } else {
              val numberOfModifiactions = flaws(flawnum).estimatedNumberOfResolvers

              if (numberOfModifiactions == 0)
                zeroFound = true

              if (flaws(flawnum).isInstanceOf[EfficientCausalThreat]) {
                smallFlawIsCausalThreat = true
                smallFlawNumMod = numberOfModifiactions
                myNode.selectedFlaw = flawnum
              } else if (!smallFlawIsCausalThreat) {
                val actionNumber = if (flaws(flawnum).isInstanceOf[EfficientOpenPrecondition]) flaws(flawnum).asInstanceOf[EfficientOpenPrecondition].planStep else -1

                smallFlawActionNumber = actionNumber

                if (actionNumber > smallFlawActionNumber || (actionNumber == smallFlawActionNumber && numberOfModifiactions <= smallFlawNumMod)) {
                  //if (numberOfModifiactions < smallFlawNumMod) {
                  smallFlawNumMod = numberOfModifiactions
                  smallFlawActionNumber = actionNumber
                  myNode.selectedFlaw = flawnum
                }
              }
            }
            //assert(numberOfModifiactions == flaws(flawnum).resolver.length)
            flawnum += 1
          }
          timeCapsule stop (if (buildTree) SEARCH_FLAW_RESOLVER else SEARCH_FLAW_RESOLVER_ESTIMATION)

          if (zeroFound) smallFlawNumMod = 0

          val children = new ArrayBuffer[(EfficientSearchNode[Payload], Int)]()

          if (smallFlawNumMod != 0) {
            if (buildTree) timeCapsule start SEARCH_FLAW_RESOLVER
            val actualModifications = if (buildTree) myNode.modifications(myNode.selectedFlaw) else flaws(myNode.selectedFlaw).resolver
            if (buildTree) timeCapsule stop SEARCH_FLAW_RESOLVER


            //assert(actualModifications.length == smallFlawNumMod, "Estimation of number of modifications was incorrect (" + actualModifications.length + " and " + smallFlawNumMod + ")")
            var modNum = 0
            while (modNum < actualModifications.length) {
              // apply modification
              //val newPlan: EfficientPlan = plan.modify(myNode.modifications(myNode.selectedFlaw)(modNum))
              val newPlan: EfficientPlan = plan.modify(actualModifications(modNum))

              if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent) {
                informationCapsule increment NUMBER_OF_NODES
                informationCapsule.addToDistribution(PLAN_SIZE, newPlan.numberOfPlanSteps)

                timeCapsule start SEARCH_COMPUTE_HEURISTIC
                //val heuristicValue = (if (addCosts) depth + 1 else 0) + heuristic.computeHeuristic(newPlan)
                val distanceValue = ((if (addNumberOfPlanSteps) newPlan.numberOfPlanSteps else 0) + (if (addDepth) depth + 1 else 0)) * (if (invertCosts) -1 else 1)
                val (h, newPayload) = heuristic.computeHeuristic(newPlan, myNode.payload, actualModifications(modNum))
                val heuristicValue = distanceValue + h
                timeCapsule stop SEARCH_COMPUTE_HEURISTIC

                //println("HEURISTIC " + heuristicValue)

                assert(newPlan.numberOfPlanSteps >= plan.numberOfPlanSteps)

                val nodeNumber = informationCapsule(NUMBER_OF_NODES)
                val searchNode: EfficientSearchNode[Payload] = if (buildTree) new EfficientSearchNode[Payload](nodeNumber, newPlan, myNode, heuristicValue)
                else new EfficientSearchNode[Payload](nodeNumber, newPlan, null, heuristicValue)

                if (h < Double.MaxValue)
                  searchQueue enqueue ((searchNode, depth + 1))
                children append ((searchNode, modNum))
              }
              modNum += 1
            }
          }
          if (buildTree) myNode.children = children.toArray
        }
        // now the node is processed
        if (buildTree) myNode.setNotDirty()
      }
      semaphore.release()
    }

    val resultSemaphore = new Semaphore(0)


    new Thread(new Runnable {
      override def run(): Unit = {
        timeCapsule start SEARCH
        heuristicSearch() // run the search, it will produce its results as side effects
        timeCapsule stop SEARCH


        // notify waiting threads
        resultSemaphore.release()
        semaphore.release()

      }
    }).start()

    (root, semaphore, ResultFunction({ _ => resultSemaphore.acquire(); result }), AbortFunction({ _ => abort = true }))
  }
}

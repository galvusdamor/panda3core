package de.uniulm.ki.panda3.efficient.search


import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.configuration.{AbortFunction, ResultFunction, EfficientSearchAlgorithm}
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util._

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object BFS extends EfficientSearchAlgorithm[Unit] {

  override def startSearch(domain: EfficientDomain, initialPlan: EfficientPlan, nodeLimit: Option[Int], timeLimit: Option[Int], releaseEvery: Option[Int], printSearchInfo: Boolean,
                           buildTree: Boolean, informationCapsule: InformationCapsule, timeCapsule: TimeCapsule):
  (EfficientSearchNode[Unit], Semaphore, ResultFunction[EfficientPlan], AbortFunction) = {
    import de.uniulm.ki.panda3.configuration.Timings._
    import de.uniulm.ki.panda3.configuration.Information._

    val semaphore: Semaphore = new Semaphore(0)
    val root = new EfficientSearchNode[Unit](0, initialPlan, null, Double.MaxValue)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes
    var d: Int = 0 // the depth
    var crap: Int = 0 // and how many dead ends we have encountered

    var abort = false

    val stack = new util.ArrayDeque[(EfficientPlan, EfficientSearchNode[Unit], Int)]()
    var result: Option[EfficientPlan] = None
    stack.add((initialPlan, root, 0))

    var lastDepth = -1
    var minFlaw = Integer.MAX_VALUE
    var layerNumberOfNodes = 0
    var total = 0

    informationCapsule increment NUMBER_OF_NODES

    def bfs() = {
      while (!stack.isEmpty && result.isEmpty && nodeLimit.getOrElse(Int.MaxValue) >= nodes &&
        initTime + timeLimit.getOrElse(Int.MaxValue).toLong * 1000 >= System.currentTimeMillis()) {
        val (plan, myNode, depth) = stack.pop()
        informationCapsule increment NUMBER_OF_EXPANDED_NODES

        assert(depth >= lastDepth)
        if (depth != lastDepth) {
          if (printSearchInfo) println("Completed Depth " + lastDepth + " Number Of Nodes: " + (nodes - layerNumberOfNodes) + " Minimal flaw count " + minFlaw)

          lastDepth = depth
          minFlaw = Integer.MAX_VALUE
          layerNumberOfNodes = nodes
        }

        timeCapsule start SEARCH_FLAW_COMPUTATION
        val flaws = plan.flaws
        timeCapsule stop SEARCH_FLAW_COMPUTATION
        minFlaw = Math.min(minFlaw, flaws.length)

        if (nodes % 500 == 0 && nodes > 0) {
          val nTime = System.currentTimeMillis()
          val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
          if (printSearchInfo) println("Plans Expanded: " + nodes + " " + nps + " Depth " + depth + " Mods/plan " + total / nodes)
        }
        nodes += 1

        if (flaws.length == 0) {
          result = Some(plan)
          myNode.setNotDirty()
        } else {
          if (buildTree) myNode.modifications = new Array[Array[EfficientModification]](flaws.length)
          var flawnum = 0
          myNode.selectedFlaw = 0
          var smallFlawNumMod = Integer.MAX_VALUE
          timeCapsule start (if (buildTree) SEARCH_FLAW_RESOLVER else SEARCH_FLAW_RESOLVER_ESTIMATION)
          while (flawnum < flaws.length) {
            if (buildTree) {
              myNode.modifications(flawnum) = flaws(flawnum).resolver
              total += myNode.modifications(flawnum).length
              if (myNode.modifications(flawnum).length < smallFlawNumMod) {
                smallFlawNumMod = myNode.modifications(flawnum).length
                myNode.selectedFlaw = flawnum
              }
            } else {
              val numberOfModifiactions = flaws(flawnum).estimatedNumberOfResolvers
              total += numberOfModifiactions
              if (numberOfModifiactions < smallFlawNumMod) {
                smallFlawNumMod = numberOfModifiactions
                myNode.selectedFlaw = flawnum
              }
            }
            //assert(numberOfModifiactions == flaws(flawnum).resolver.length)
            flawnum += 1
          }
          timeCapsule stop (if (buildTree) SEARCH_FLAW_RESOLVER else SEARCH_FLAW_RESOLVER_ESTIMATION)

          val children = new ArrayBuffer[(EfficientSearchNode[Unit], Int)]()
          if (smallFlawNumMod != 0) {
            if (buildTree) timeCapsule start SEARCH_FLAW_RESOLVER
            val actualModifications = if (buildTree) myNode.modifications(myNode.selectedFlaw) else flaws(myNode.selectedFlaw).resolver
            if (buildTree) timeCapsule stop SEARCH_FLAW_RESOLVER


            //assert(actualModifications.length == smallFlawNumMod, "Estimation of number of modifications was incorrect (" + actualModifications.length + " and " + smallFlawNumMod + ")")
            timeCapsule start SEARCH_GENERATE_SUCCESSORS
            var modNum = 0
            while (modNum < actualModifications.length && result.isEmpty) {
              // apply modification
              val newPlan: EfficientPlan = plan.modify(actualModifications(modNum))

              if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent) {
                informationCapsule increment NUMBER_OF_NODES
                val nodeNumber = informationCapsule(NUMBER_OF_NODES)
                val searchNode = if (buildTree) new EfficientSearchNode[Unit](nodeNumber, newPlan, myNode, 0) else new EfficientSearchNode[Unit](nodeNumber, newPlan, null, 0)

                stack add(newPlan, searchNode, depth + 1)
                children append ((searchNode, modNum))
              }
              modNum += 1
            }
            timeCapsule stop SEARCH_GENERATE_SUCCESSORS
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
        bfs() // run the search, it will produce its results as side effects
        timeCapsule stop SEARCH


        // notify waiting threads
        resultSemaphore.release()
        semaphore.release()

      }
    }).start()

    (root, semaphore, ResultFunction({ _ => resultSemaphore.acquire(); result match {case Some(p) => p :: Nil; case _ => Nil}}), AbortFunction({ _ => abort = true }))
  }
}
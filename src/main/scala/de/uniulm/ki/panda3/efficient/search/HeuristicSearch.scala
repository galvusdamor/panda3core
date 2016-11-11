package de.uniulm.ki.panda3.efficient.search

import java.util
import java.util.concurrent.{TimeUnit, Executors, Semaphore}

import de.uniulm.ki.panda3.configuration.{AbortFunction, EfficientSearchAlgorithm, ResultFunction}
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.heuristic._
import de.uniulm.ki.panda3.efficient.heuristic.filter.{Filter, TreeFF}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.efficient.search.flawSelector.EfficientFlawSelector
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class HeuristicSearch[Payload <: AnyVal](heuristic: Array[EfficientHeuristic[Payload]], weight: Double, pruning: Array[Filter], flawSelector: EfficientFlawSelector,
                                              addNumberOfPlanSteps: Boolean,
                                              addDepth: Boolean, continueOnSolution: Boolean, invertCosts: Boolean = false)(implicit m: ClassTag[Payload])
  extends EfficientSearchAlgorithm[Payload] {

  override def startSearch(domain: EfficientDomain, initialPlan: EfficientPlan, nodeLimit: Option[Int], timeLimit: Option[Int], releaseEvery: Option[Int], printSearchInfo: Boolean,
                           buildTree: Boolean, informationCapsule: InformationCapsule, timeCapsule: TimeCapsule):
  (EfficientSearchNode[Payload], Semaphore, ResultFunction[EfficientPlan], AbortFunction) = {
    import de.uniulm.ki.panda3.configuration.Information._
    import de.uniulm.ki.panda3.configuration.Timings._

    val semaphore: Semaphore = new Semaphore(0)
    val initialPlanHeuristic = new Array[Double](heuristic.length)
    val initialPlanPayload = new Array[Payload](heuristic.length)
    util.Arrays.fill(initialPlanHeuristic, Double.MaxValue)
    val root = new EfficientSearchNode[Payload](0, initialPlan, null, initialPlanHeuristic)
    root.payload = initialPlanPayload

    // variables for the search
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
      val initTime: Long = System.currentTimeMillis()
      while (searchQueue.nonEmpty && (continueOnSolution || result.isEmpty) && nodeLimit.getOrElse(Int.MaxValue) >= nodes &&
        initTime + timeLimit.getOrElse(Int.MaxValue).toLong * 1000 >= System.currentTimeMillis() - 50) {
        val (myNode, depth) = searchQueue.dequeue()
        val plan = myNode.plan
        timeCapsule start SEARCH_FLAW_COMPUTATION
        val flaws = plan.flaws
        timeCapsule stop SEARCH_FLAW_COMPUTATION
        minFlaw = Math.min(minFlaw, flaws.length)

        //println("PLAN")
        //plan.remainingAccessiblePrimitiveTasks.length
        //if (!plan.allContainedApplicable) println("DEAD CONTAINED")
        //if (!plan.allLandmarksApplicable) println("DEAD LANDMARKS")
        //if (!plan.allAbstractTasksAllowed) println("DEAD ALLOWED")
        //println(plan.allLandmarks filter { domain.tasks(_).isPrimitive } size)

        //println("LM " + plan.allLandmarks.size + "/" + plan.simpleLandMark.size + " all " + plan.taskAllowed.count(x => x))

        informationCapsule increment NUMBER_OF_EXPANDED_NODES

        // heuristic statistics
        if (myNode.heuristic(0) < lowestHeuristicFound) {
          lowestHeuristicFound = myNode.heuristic(0)
          if (printSearchInfo) println("Found new lowest heuristic value: " + lowestHeuristicFound + " @ plan #" + nodes)
        }
        minHeuristicCurrentInterval = Math.min(minHeuristicCurrentInterval, myNode.heuristic(0))
        maxHeuristicCurrentInterval = Math.max(maxHeuristicCurrentInterval, myNode.heuristic(0))


        //if (nodes % 300 == 0 && nodes > 0) {
        if (lastReportTime + 333 < System.currentTimeMillis()) {
          val nTime = System.currentTimeMillis()
          val nps = nodes.asInstanceOf[Double] / (nTime - initTime) * 1000
          val npsRecent = recentNodes.asInstanceOf[Double] / (nTime - lastReportTime) * 1000
          if (printSearchInfo) println("Plans Expanded: " + nodes + " node/sec avg: " + nps.toInt + " recent: " + npsRecent.toInt + " Queue size " + searchQueue.length + " Recently lowest" +
                                         " Heuristic " + minHeuristicCurrentInterval + " Recently highest Heuristic " + maxHeuristicCurrentInterval)
          minHeuristicCurrentInterval = Double.MaxValue
          maxHeuristicCurrentInterval = -Double.MaxValue
          lastReportTime = nTime
          recentNodes = 0
        }
        nodes += 1
        recentNodes += 1


        if (flaws.length == 0) {
          result = result :+ plan
          myNode.setNotDirty()
          println("Found solution at depth " + depth + " with " + (plan.numberOfPlanSteps - 2) + " actions and heuristic " + myNode.heuristic)
        } else {
          timeCapsule start (if (buildTree) SEARCH_FLAW_RESOLVER else SEARCH_FLAW_RESOLVER_ESTIMATION)

          // build the length array
          val numberOfModificationsPerFlaw = new Array[Int](flaws.length)
          if (buildTree) {
            myNode.modifications = new Array[Array[EfficientModification]](flaws.length)
          }

          var flawnum = 0
          while (flawnum < flaws.length) {
            if (buildTree) {
              myNode.modifications(flawnum) = flaws(flawnum).resolver
              numberOfModificationsPerFlaw(flawnum) = myNode.modifications(flawnum).length
            } else numberOfModificationsPerFlaw(flawnum) = flaws(flawnum).estimatedNumberOfResolvers
            //assert(numberOfModifiactions == flaws(flawnum).resolver.length)
            flawnum += 1
          }
          timeCapsule stop (if (buildTree) SEARCH_FLAW_RESOLVER else SEARCH_FLAW_RESOLVER_ESTIMATION)


          timeCapsule start SEARCH_FLAW_SELECTOR
          myNode.selectedFlaw = flawSelector.selectFlaw(plan, flaws, numberOfModificationsPerFlaw)
          timeCapsule stop SEARCH_FLAW_SELECTOR

          val children = new ArrayBuffer[(EfficientSearchNode[Payload], Int)]()

          if (numberOfModificationsPerFlaw(myNode.selectedFlaw) != 0) {
            if (buildTree) timeCapsule start SEARCH_FLAW_RESOLVER
            val actualModifications = if (buildTree) myNode.modifications(myNode.selectedFlaw) else flaws(myNode.selectedFlaw).resolver
            if (buildTree) timeCapsule stop SEARCH_FLAW_RESOLVER


            //assert(actualModifications.length == smallFlawNumMod, "Estimation of number of modifications was incorrect (" + actualModifications.length + " and " + smallFlawNumMod +
            // ")")
            var modNum = 0
            while (modNum < actualModifications.length) {
              // apply modification
              //val newPlan: EfficientPlan = plan.modify(myNode.modifications(myNode.selectedFlaw)(modNum))
              val newPlan: EfficientPlan = plan.modify(actualModifications(modNum))

              timeCapsule start SEARCH_COMPUTE_FILTER
              var planAllowed = true
              var filterIndex = 0
              while (filterIndex < pruning.length) {
                planAllowed &= pruning(filterIndex).isPossiblySolvable(newPlan)
                filterIndex += 1
              }
              timeCapsule stop SEARCH_COMPUTE_FILTER

              //if (!newPlan.allContainedApplicable) println("DEAD CONTAINED")
              //if (!newPlan.allLandmarksApplicable) println("DEAD LANDMARKS")
              //if (!newPlan.allAbstractTasksAllowed) println("DEAD ALLOWED")

              if (newPlan.variableConstraints.potentiallyConsistent && newPlan.ordering.isConsistent && planAllowed) {
                informationCapsule increment NUMBER_OF_NODES
                informationCapsule.addToDistribution(PLAN_SIZE, newPlan.numberOfPlanSteps)

                timeCapsule start SEARCH_COMPUTE_HEURISTIC
                //val heuristicValue = (if (addCosts) depth + 1 else 0) + heuristic.computeHeuristic(newPlan)
                val distanceValue = ((if (addNumberOfPlanSteps) newPlan.numberOfPrimitivePlanSteps else 0) + (if (addDepth) depth + 1 else 0)) * (if (invertCosts) -1 else 1)

                // compute the heuristic array
                val h = new Array[Double](heuristic.length)
                val newPayload = new Array[Payload](heuristic.length)
                var hPos = 0
                var anyInfinity = false
                while (hPos < h.length) {
                  val (hVal, pay) = heuristic(hPos).computeHeuristic(newPlan, myNode.payload(hPos), actualModifications(modNum), depth)
                  h(hPos) = distanceValue + weight * hVal
                  newPayload(hPos) = pay
                  anyInfinity &= h(hPos) < Int.MaxValue

                  hPos += 1
                }
                timeCapsule stop SEARCH_COMPUTE_HEURISTIC

                assert(newPlan.numberOfPlanSteps >= plan.numberOfPlanSteps)

                val nodeNumber = informationCapsule(NUMBER_OF_NODES)
                val searchNode: EfficientSearchNode[Payload] = if (buildTree) new EfficientSearchNode[Payload](nodeNumber, newPlan, myNode, h)
                else new EfficientSearchNode[Payload](nodeNumber, newPlan, null, h)
                searchNode.payload = newPayload

                if (!anyInfinity) searchQueue enqueue ((searchNode, depth + 1)) else informationCapsule increment NUMBER_OF_DISCARDED_NODES

                children append ((searchNode, modNum))
              } else informationCapsule increment NUMBER_OF_DISCARDED_NODES
              modNum += 1
            }
          } /*else {
            val flaw = myNode.plan.flaws(myNode.selectedFlaw)
            println(flaw)
            flaw match {
              case EfficientOpenPrecondition(p,ps,precIndex) =>
                val taskIndex = p.planStepTasks(ps)
                val task = PlanningConfiguration.wrapper.wrapTask(taskIndex)
                println(task.name)
                val prec = p.taskOfPlanStep(ps).precondition(precIndex)
                println(PlanningConfiguration.wrapper.wrapPredicate(prec.predicate).name)


              case _ => ()
            }
          }*/
          if (buildTree) myNode.children = children.toArray
        }
        // now the node is processed
        if (buildTree) myNode.setNotDirty()
      }
      semaphore.release()
    }

    val resultSemaphore = new Semaphore(0)


    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        timeCapsule start SEARCH
        try {
          heuristicSearch() // run the search, it will produce its results as side effects
        } catch {
          case t: Throwable =>
            t.printStackTrace()
            informationCapsule.set(ERROR, "true")
        }
        timeCapsule stopOrIgnore SEARCH


        // notify waiting threads
        semaphore.release()
        resultSemaphore.release()

      }
    })

    val resultFunction = ResultFunction(
      { _ =>
        // start the main worker thread which does the actual planning
        thread.start()

        val killerThread = new Thread(new Runnable {
          override def run(): Unit = {
            // wait timelimit + 10 seconds
            Thread.sleep((timeLimit.getOrElse(Int.MaxValue).toLong + 10) * 1000)
            resultSemaphore.release()
            thread.stop()
          }
        })
        killerThread.start()


        resultSemaphore.acquire()

        // just to be on the safe side stop all worker and utility threads
        killerThread.stop()
        thread.stop()

        timeCapsule stopOrIgnore SEARCH

        result
      })



    (root, semaphore, resultFunction, AbortFunction({ _ => abort = true }))
  }
}

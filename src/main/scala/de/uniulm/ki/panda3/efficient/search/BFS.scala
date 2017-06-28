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
    val root = new EfficientSearchNode[Unit](0, initialPlan, null, Array(Double.MaxValue), 0)

    // variables for the search
    var nodes: Int = 0 // count the nodes
    var d: Int = 0 // the depth
    var crap: Int = 0 // and how many dead ends we have encountered

    var abort = false

    val queue = new util.ArrayDeque[(EfficientPlan, EfficientSearchNode[Unit], Int)]()
    var result: Option[EfficientPlan] = None
    queue.add((initialPlan, root, 0))

    var lastDepth = -1
    var minFlaw = Integer.MAX_VALUE
    var layerNumberOfNodes = 0
    var total = 0

    informationCapsule increment NUMBER_OF_NODES
    val timeLimitInMilliSeconds = timeLimit.getOrElse(Int.MaxValue).toLong * 1000

    def bfs() = {
      val initTime: Long = System.currentTimeMillis()
      while (!queue.isEmpty && result.isEmpty && nodeLimit.getOrElse(Int.MaxValue) >= nodes &&
        timeLimitInMilliSeconds >= timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME) - 50) {
        val (plan, myNode, depth) = queue.pop()
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
          informationCapsule.set(SOLUTION_LENGTH, plan.numberOfPrimitivePlanSteps - 2)
          println("Found solution at depth " + depth + " with " + (plan.numberOfPlanSteps - 2) + " actions and heuristic " + myNode.heuristic)
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
                val searchNode = if (buildTree) new EfficientSearchNode[Unit](nodeNumber, newPlan, myNode, Array(0),0) else
                  new EfficientSearchNode[Unit](nodeNumber, newPlan, null, Array(0),0)

                queue add(newPlan, searchNode, depth + 1)
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

      if (queue.isEmpty){
        // if we reached this point and the queue is empty, we have proven the problem to be unsolvable
        informationCapsule.set(SEARCH_SPACE_FULLY_EXPLORED, "true")
      }

      semaphore.release()
    }


    val resultSemaphore = new Semaphore(0)
    val timerSemaphore = new Semaphore(0)


    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        timeCapsule switchTimerToCurrentThread TOTAL_TIME
        timeCapsule start SEARCH
        try {
          bfs() // run the search, it will produce its results as side effects
        } catch {
          case t: Throwable =>
            t.printStackTrace()
            informationCapsule.set(ERROR, "true")
        }

        timeCapsule stopOrIgnore SEARCH

        // notify waiting threads
        semaphore.release()
        resultSemaphore.release()

        timerSemaphore.acquire()
      }
    })

    val resultFunction = ResultFunction(
      { _ =>
        val timeLimitToReach = (timeLimit.getOrElse(Int.MaxValue).toLong + 10) * 1000
        // start the main worker thread which does the actual planning
        thread.start()

        val killerThread = new Thread(new Runnable {
          override def run(): Unit = {
            // wait timelimit + 10 seconds
            var timeLimitReached = false
            while (!timeLimitReached && thread.isAlive) {
              Thread.sleep(250)
              // test if we have reached the timelimit
              timeLimitReached = timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME) > timeLimitToReach
            }
            resultSemaphore.release()
            thread.stop()
          }
        })

        // this thread is necessary in case we get an OOME - then both the main thread and the killer thread will show an exception (which I - apparently - cannot catch in scala)
        val rescueThread = new Thread(new Runnable {
          override def run(): Unit = {
            // wait timelimit + 10 seconds
            var timeLimitReached = false
            while (killerThread.isAlive || thread.isAlive) {
              Thread.sleep(250)
            }
            resultSemaphore.release()
            thread.stop()
          }
        }, "Thread - RESCUE")


        killerThread.start()
        rescueThread.start()

        resultSemaphore.acquire()
        timeCapsule.switchTimerToCurrentThreadOrIgnore(TOTAL_TIME, Some(timeLimitToReach))
        timeCapsule.switchTimerToCurrentThreadOrIgnore(SEARCH, Some(timeLimitToReach))
        timerSemaphore.release()

        // just to be on the safe side stop all worker and utility threads
        killerThread.stop()
        thread.stop()

        timeCapsule stopOrIgnore SEARCH

        result match {case Some(p) => p :: Nil; case _ => Nil}
      })



    (root, semaphore, resultFunction, AbortFunction({ _ => abort = true }))
  }
}
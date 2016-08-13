package de.uniulm.ki.panda3.symbolic.search

import java.util
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.configuration.{AbortFunction, ResultFunction, SymbolicSearchAlgorithm}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

/**
  * This is a very simple BFS planner
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object BFS extends SymbolicSearchAlgorithm {

  /**
    * This functions starts the (asynchronious) search for a solution to a given planning problem.
    * It returns a pointer to the search space and a semaphore signaling that new search nodes have been inserted into the tree.
    *
    * The semaphore will not be released for every search node, but after a couple releaseEvery ones.
    */
  override def startSearch(domain: Domain, initialPlan: Plan,
                           nodeLimit: Option[Int], timeLimit: Option[Int], releaseEvery: Option[Int], printSearchInfo: Boolean, buildSearchTree: Boolean,
                           informationCapsule: InformationCapsule, timeCapsule: TimeCapsule):
  (SearchNode, Semaphore, ResultFunction[Plan], AbortFunction) = {
    import de.uniulm.ki.panda3.configuration.Information._
    import de.uniulm.ki.panda3.configuration.Timings._

    val semaphore: Semaphore = new Semaphore(0)
    val rootnode: SearchNode = new SearchNode(0, { _ => initialPlan }, null, -1)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes

    var abort = false
    informationCapsule increment NUMBER_OF_NODES

    def search(domain: Domain): Option[Plan] = {
      val queue = new util.ArrayDeque[(Plan, SearchNode, Int)]()
      var result: Option[Plan] = None

      queue push ((initialPlan, rootnode, 0))

      while (!queue.isEmpty && result.isEmpty && nodeLimit.getOrElse(Int.MaxValue) >= nodes &&
        initTime + timeLimit.getOrElse(Int.MaxValue).toLong * 1000 >= System.currentTimeMillis()) {

        val (plan, myNode, depth) = queue.pop()

        timeCapsule start SEARCH_FLAW_COMPUTATION
        val flaws = plan.flaws
        timeCapsule stop SEARCH_FLAW_COMPUTATION

        if (flaws.isEmpty) {
          myNode.dirty = false
          result = Some(plan)
        } else {
          informationCapsule increment NUMBER_OF_EXPANDED_NODES
          nodes = nodes + 1
          if (printSearchInfo && nodes % 10 == 0) println((nodes * 1000.0 / (System.currentTimeMillis() - initTime)) + " node/sec " + nodes + " nodes ")

          if (releaseEvery.isDefined && nodes % releaseEvery.get == 0) semaphore.release()

          timeCapsule start SEARCH_FLAW_RESOLVER
          myNode setModifications (flaws map { _.resolvents(domain) })
          timeCapsule stop SEARCH_FLAW_RESOLVER

          // check whether we are at a dead end in the search space
          if (myNode.modifications exists { _.isEmpty }) {
            myNode.dirty = false
            myNode.setSelectedFlaw(-1)
          } else {
            timeCapsule start SEARCH_FLAW_SELECTOR
            val selectedResolvers: Seq[Modification] = (myNode.modifications sortBy { _.size }).head
            // set the selected flaw
            myNode setSelectedFlaw (myNode.modifications indexOf selectedResolvers)
            timeCapsule stop SEARCH_FLAW_SELECTOR

            // create all children
            timeCapsule start SEARCH_GENERATE_SUCCESSORS
            myNode setChildren (selectedResolvers.zipWithIndex map { case (m, i) =>
              (new SearchNode(informationCapsule(NUMBER_OF_NODES + i), { _ => myNode.plan.modify(m) }, myNode, -1), i)
            } filterNot { _._1.plan.isSolvable contains false })
            informationCapsule.add(NUMBER_OF_NODES, myNode.children.size)
            myNode.dirty = false
            timeCapsule stop SEARCH_GENERATE_SUCCESSORS

            // add to queue
            myNode.children map { _._1 } foreach { n => queue push(n.plan, n, depth + 1) }
          }
        }
      }

      result
    }

    val resultSemaphore = new Semaphore(0)
    var result: Option[Plan] = None

    new Thread(new Runnable {
      override def run(): Unit = {
        timeCapsule start SEARCH
        result = search(domain)
        timeCapsule stop SEARCH
        // notify waiting threads
        resultSemaphore.release()
        semaphore.release()
      }
    }).start()
    (rootnode, semaphore, ResultFunction({ _ => resultSemaphore.acquire(); result }), AbortFunction({ _ => abort = true }))
  }
}
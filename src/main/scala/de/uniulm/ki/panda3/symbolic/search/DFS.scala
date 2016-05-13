package de.uniulm.ki.panda3.symbolic.search

import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.configuration.{ResultFunction, AbortFunction, SymbolicSearchAlgorithm}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

/**
  * This is a very simple DFS planner
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object DFS extends SymbolicSearchAlgorithm {

  /**
    * This functions starts the (asynchronious) search for a solution to a given planning problem.
    * It returns a pointer to the search space and a semaphore signaling that new search nodes have been inserted into the tree.
    *
    * The semaphore will not be released for every search node, but after a couple releaseEvery ones.
    */
  override def startSearch(domain: Domain, initialPlan: Plan,
                           nodeLimit: Option[Int], releaseEvery: Option[Int], printSearchInfo: Boolean, buildSearchTree: Boolean,
                           informationCapsule: InformationCapsule, timeCapsule: TimeCapsule):
  (SearchNode, Semaphore, ResultFunction[Plan], AbortFunction) = {
    import de.uniulm.ki.panda3.configuration.Timings._
    import de.uniulm.ki.panda3.configuration.Information._

    val semaphore: Semaphore = new Semaphore(0)
    val node: SearchNode = new SearchNode(initialPlan, null, -1)

    // variables for the search
    val initTime: Long = System.currentTimeMillis()
    var nodes: Int = 0 // count the nodes
    var d: Int = 0 // the depth
    var crap: Int = 0 // and how many dead ends we have encountered

    var abort = false
    informationCapsule increment NUMBER_OF_NODES

    def search(domain: Domain, node: SearchNode): Option[Plan] = {
      timeCapsule start SEARCH_FLAW_COMPUTATION
      val flaws = node.plan.flaws
      timeCapsule stop SEARCH_FLAW_COMPUTATION

      if (flaws.isEmpty) Some(node.plan)
      else if ((nodeLimit.isDefined && nodes > nodeLimit.get) || abort) None
      else {
        informationCapsule increment NUMBER_OF_EXPANDED_NODES
        nodes = nodes + 1
        d = d + 1
        if (printSearchInfo && nodes % 10 == 0) println(nodes * 1000.0 / (System.currentTimeMillis() - initTime) + " " + d + s" $crap/$nodes")

        if (releaseEvery.isDefined && nodes % releaseEvery.get == 0) semaphore.release()

        timeCapsule start SEARCH_FLAW_RESOLVER
        node setModifications (flaws map { _.resolvents(domain) })
        timeCapsule stop SEARCH_FLAW_RESOLVER

        // check whether we are at a dead end in the search space
        if (node.modifications exists { _.isEmpty }) {d = d - 1; crap = crap + 1; node.dirty = false; None }
        else {
          timeCapsule start SEARCH_FLAW_SELECTOR
          val selectedResolvers: Seq[Modification] = (node.modifications sortBy { _.size }).head
          // set the selected flaw
          node setSelectedFlaw (node.modifications indexOf selectedResolvers)
          timeCapsule stop SEARCH_FLAW_SELECTOR

          // create all children
          timeCapsule start SEARCH_GENERATE_SUCCESSORS
          node setChildren (selectedResolvers.zipWithIndex map { case (m, i) => (new SearchNode(node.plan.modify(m), node, -1), i) } filterNot { _._1.plan.isSolvable contains false })
          informationCapsule.add(NUMBER_OF_NODES, node.children.size)
          node.dirty = false
          timeCapsule stop SEARCH_GENERATE_SUCCESSORS

          // perform the search
          val ret = (node.children map { _._1 }).foldLeft[Option[Plan]](None)({
                                                                                case (Some(p), _) => Some(p)
                                                                                case (None, res)  => search(domain, res)
                                                                              })
          d = d - 1

          ret
        }
      }
    }

    val resultSemaphore = new Semaphore(0)
    var result: Option[Plan] = None

    new Thread(new Runnable {
      override def run(): Unit = {
        timeCapsule start SEARCH
        result = search(domain, node)
        timeCapsule stop SEARCH
        // notify waiting threads
        resultSemaphore.release()
        semaphore.release()
      }
    }).start()
    (node, semaphore, ResultFunction({ _ => resultSemaphore.acquire(); result }), AbortFunction({ _ => abort = true }))
  }
}
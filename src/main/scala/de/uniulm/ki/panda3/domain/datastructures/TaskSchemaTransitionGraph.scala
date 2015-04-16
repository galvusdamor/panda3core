package de.uniulm.ki.panda3.domain.datastructures

import de.uniulm.ki.panda3.domain.{DecompositionMethod, Domain, Task}
import de.uniulm.ki.util.Graph

import scala.collection.mutable

/**
 * This is a simplified version of the TDG (task decomposition graph) which does not take variable constraints into account.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class TaskSchemaTransitionGraph(domain: Domain) extends Graph[Task] {

  lazy val canBeDecomposedInto: Map[Task, Seq[Task]] = {
    val decompositionMap: mutable.Map[Task, Seq[Task]] = mutable.HashMap()

    def dfs(scc: Seq[Task]): Unit = {
      // if any node of the scc is already in the map, simply ignore it
      if (!decompositionMap.contains(scc.head)) {
        condensation.edges(scc) foreach {dfs(_)}

        val allPredicates = scc ++ (condensation.edges(scc) flatMap { neighbour => decompositionMap(neighbour.head) })

        scc foreach {decompositionMap(_) = allPredicates}
      }
    }
    // run the dfs on all source SCCs of the condensation
    condensation.sources foreach {dfs(_)}

    decompositionMap.toMap
  }


  lazy val canBeDecomposedIntoVia: Map[Task, Seq[(DecompositionMethod, Task)]] = (canBeDirectlyDecomposedIntoVia map { case (task, directDecomps) => (task, directDecomps
    .toSeq flatMap { case (method, subtask) => canBeDecomposedInto(subtask) map {(method, _)} })
  }).toMap


  /** a list of all node of the graph */
  override val nodes: Seq[Task] = domain.tasks


  /** describes which tasks can be obtained from a given task by applying a given decomposition method */
  val canBeDirectlyDecomposedIntoVia: Map[Task, Set[(DecompositionMethod, Task)]] = (domain.tasks map { case task => (task, (domain.decompositionMethods flatMap { case method => method
    .subPlan.planStepWithoutInitGoal.map { case ps => (method, ps.schema) }
  }).toSet)
  }).toMap[Task, Set[(DecompositionMethod, Task)]]


  val canBeDirectlyDecomposedInto: Map[Task, Seq[Task]] = canBeDirectlyDecomposedIntoVia map { case (t, tasks) => (t, tasks.toSeq map {_._2}) }

  /** adjacency list of the graph */
  override def edges: Map[Task, Seq[Task]] = canBeDirectlyDecomposedInto
}
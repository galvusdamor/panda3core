package de.uniulm.ki.panda3.efficient.domain.datastructures

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.util.DirectedGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTaskSchemaTransitionGraph(domain: EfficientDomain) extends DirectedGraph[Int] {

  /** all tasks */
  override def vertices: Seq[Int] = {
    domain.tasks.indices
  }

  /** adjacency list of the graph */
  override def edges: Map[Int, Seq[Int]] = (vertices map { task => val decomposableInto = domain.taskToPossibleMethods(task) flatMap {
    case (method, _) =>
      val methodTasks = method.subPlan.planStepTasks
      methodTasks.slice(2, methodTasks.length)
  }
    (task, decomposableInto.toSeq.distinct)
  }).toMap


  lazy val taskCanSupportByDecomposition: Map[Int, Array[(Int, Boolean)]] = (vertices map { task =>
      val produciblePredicates = reachable(task) filterNot { _ == task } flatMap { subTask => domain.tasks(subTask).effect map { eff => (eff.predicate, eff.isPositive) } }
      (task, produciblePredicates.toArray)
    }).toMap
}
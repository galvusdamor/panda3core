package de.uniulm.ki.panda3.efficient.domain.datastructures

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.util.{DirectedGraphWithAlgorithms, DirectedGraph}

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTaskSchemaTransitionGraph(domain: EfficientDomain) extends DirectedGraphWithAlgorithms[Int] {

  /** all tasks */
  override val vertices: Seq[Int] = domain.tasks.indices

  /** adjacency list of the graph */
  override val edges: Map[Int, Seq[Int]] = (vertices map { task => val decomposableInto = domain.taskToPossibleMethods(task) flatMap {
    case (method, _) =>
      val methodTasks = method.subPlan.planStepTasks
      methodTasks.slice(2, methodTasks.length)
  }
    (task, decomposableInto.toSeq.distinct)
  }).toMap


  lazy val taskCanSupportByDecomposition: Map[Int, Array[(Int, Boolean)]] = {
    val startT = System.currentTimeMillis()
    (vertices map { task =>
      val buffer = new ArrayBuffer[(Int, Boolean)]()
      val reach = reachable(task) filterNot { _ == task }

      reach foreach { subTask => domain.tasks(subTask).effect foreach { eff => buffer append ((eff.predicate, eff.isPositive)) } }

      val arr = buffer.toArray
      //println("EFF " + arr.length)
      (task, arr)
    }).toMap
  }
}
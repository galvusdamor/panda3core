package de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.GroundedPlanningGraph
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientGroundedPlanningGraphTrait {

  def actionLayer: Array[Array[(Int, Array[Int])]]

  def stateLayer: Array[Array[(Int, Array[Int])]]
}


case class EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraph: GroundedPlanningGraph, wrapper: Wrapping) extends EfficientGroundedPlanningGraphTrait {

  override val actionLayer: Array[Array[(Int, Array[Int])]] = symbolicPlanningGraph.layer map { case (applicableTasks, _) =>
    applicableTasks map { case GroundTask(task, arguments) => (wrapper.unwrap(task), arguments map wrapper.unwrap toArray) } toArray
  } toArray


  override val stateLayer: Array[Array[(Int, Array[Int])]] = symbolicPlanningGraph.layer map { case (_, statepredicates) =>
    statepredicates map { case GroundLiteral(predicate, isPositive, arguments) =>
      assert(isPositive)
      (wrapper.unwrap(predicate), arguments map wrapper.unwrap toArray) } toArray
  } toArray
}
package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate, GroundLiteral}
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait ReachabilityAnalysis {
  val reachableLiftedLiterals: Seq[(Predicate, Boolean)]

  val reachableLiftedActions: Seq[Task]
}

trait LiftedReachabilityAnalysis extends ReachabilityAnalysis {
  override lazy val reachableLiftedLiterals: Seq[(Predicate, Boolean)] = layer.last._2.toSeq

  override lazy val reachableLiftedActions: Seq[Task] = layer.last._1.toSeq

  protected val layer: Seq[(Set[Task], Set[(Predicate, Boolean)])]
}

trait GroundedReachabilityAnalysis extends ReachabilityAnalysis {
  lazy val reachableGroundLiterals: Seq[GroundLiteral] = layer.last._2.toSeq

  lazy val reachableGroundActions: Seq[GroundTask] = layer.last._1.toSeq

  override lazy val reachableLiftedLiterals: Seq[(Predicate, Boolean)] = (layer.last._2 map { case GroundLiteral(pred, isPositive, _) => (pred, isPositive) }).toSeq

  override lazy val reachableLiftedActions: Seq[Task] = (layer.last._1 map { _.task }).toSeq

  protected val layer: Seq[(Set[GroundTask], Set[GroundLiteral])]
}
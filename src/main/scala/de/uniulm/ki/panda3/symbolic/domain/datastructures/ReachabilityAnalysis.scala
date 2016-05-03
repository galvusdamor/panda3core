package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic.domain.{GroundedDecompositionMethod, DecompositionMethod, Task}
import de.uniulm.ki.panda3.symbolic.logic.{GroundLiteral, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait PrimitiveReachabilityAnalysis {
  val reachableLiftedLiterals        : Seq[(Predicate, Boolean)]
  val reachableLiftedPrimitiveActions: Seq[Task]
}

trait GroundedPrimitiveReachabilityAnalysis extends PrimitiveReachabilityAnalysis {
  val reachableGroundLiterals        : Seq[GroundLiteral]
  val reachableGroundPrimitiveActions: Seq[GroundTask]

  val reachableLiftedLiterals        : Seq[(Predicate, Boolean)] = reachableGroundLiterals map { case GroundLiteral(pred, isPositive, _) => (pred, isPositive) } distinct
  val reachableLiftedPrimitiveActions: Seq[Task]                 = reachableGroundPrimitiveActions map { _.task } distinct
}

trait ReachabilityAnalysis extends {
  val reachableLiftedAbstractActions: Seq[Task]
  val reachableLiftedMethods        : Seq[DecompositionMethod]
}

trait GroundedReachabilityAnalysis extends ReachabilityAnalysis with GroundedPrimitiveReachabilityAnalysis {
  val reachableGroundedTasks: Seq[GroundTask]
  val reachableGroundMethods: Seq[GroundedDecompositionMethod]

  val reachableGroundAbstractActions : Seq[GroundTask] = reachableGroundedTasks filter { _.task.isAbstract }
  val reachableGroundPrimitiveActions: Seq[GroundTask] = reachableGroundedTasks filter { _.task.isPrimitive }

  val reachableLiftedAbstractActions: Seq[Task]                = reachableGroundAbstractActions map { _.task } distinct
  val reachableLiftedMethods        : Seq[DecompositionMethod] = reachableGroundMethods map { _.decompositionMethod } distinct
}


trait LayeredLiftedPrimitiveReachabilityAnalysis extends PrimitiveReachabilityAnalysis {
  override lazy val reachableLiftedLiterals: Seq[(Predicate, Boolean)] = layer.last._2.toSeq

  override lazy val reachableLiftedPrimitiveActions: Seq[Task] = layer.last._1.toSeq

  protected val layer: Seq[(Set[Task], Set[(Predicate, Boolean)])]
}

trait LayeredGroundedPrimitiveReachabilityAnalysis extends GroundedPrimitiveReachabilityAnalysis {
  override lazy val reachableGroundLiterals: Seq[GroundLiteral] = layer.last._2.toSeq

  override lazy val reachableGroundPrimitiveActions: Seq[GroundTask] = layer.last._1.toSeq

  protected val layer: Seq[(Set[GroundTask], Set[GroundLiteral])]
}
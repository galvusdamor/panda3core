package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic.domain.{GroundedDecompositionMethod, DecompositionMethod, Task}
import de.uniulm.ki.panda3.symbolic.logic.{GroundLiteral, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.element.{PlanStep, GroundTask}

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

  lazy val reachableLiftedLiterals        : Seq[(Predicate, Boolean)] = reachableGroundLiterals map { case GroundLiteral(pred, isPositive, _) => (pred, isPositive) } distinct
  lazy val reachableLiftedPrimitiveActions: Seq[Task]                 = reachableGroundPrimitiveActions map { _.task } distinct
}

trait ReachabilityAnalysis extends {
  val reachableLiftedAbstractActions: Seq[Task]
  val reachableLiftedMethods        : Seq[DecompositionMethod]
}

trait GroundedReachabilityAnalysis extends ReachabilityAnalysis with GroundedPrimitiveReachabilityAnalysis {
  val reachableGroundedTasks: Seq[GroundTask]
  val reachableGroundMethods: Seq[GroundedDecompositionMethod]

  lazy val reachableGroundMethodInitAndGoalActions: Seq[GroundTask] = reachableGroundMethods flatMap { case GroundedDecompositionMethod(liftedMethod, binding) =>
    liftedMethod.subPlan.initAndGoal map { case PlanStep(_, schema, args) => GroundTask(schema, args map binding) }
  }

  lazy val reachableGroundAbstractActions : Seq[GroundTask] = reachableGroundedTasks filter { _.task.isAbstract }
  lazy val reachableGroundPrimitiveActions: Seq[GroundTask] = reachableGroundedTasks filter { _.task.isPrimitive }

  lazy val reachableLiftedAbstractActions: Seq[Task]                = reachableGroundAbstractActions map { _.task } distinct
  lazy val reachableLiftedMethods        : Seq[DecompositionMethod] = reachableGroundMethods map { _.decompositionMethod } distinct

  val additionalTaskNeededToGround   : Seq[GroundTask]
  val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod]
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
package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class KautzSelman(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int) extends LinearPrimitivePlanEncoding with EncodingWithLinearPlan {
  override lazy val offsetToK = 0

  override lazy val overrideK = None

  override lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  override val numberOfChildrenClauses = 0 // none

  override val expansionPossible = Math.pow(2, domain.predicates.length) > taskSequenceLength

  override val decompositionFormula = Nil

  override val givenActionsFormula = Nil

  override val noAbstractsFormula = Nil

  override lazy val stateTransitionFormula: Seq[Clause] = stateTransitionFormulaOfLength(taskSequenceLength) ++
    Range(0, taskSequenceLength).flatMap(position => atMostOneOf(domain.primitiveTasks map { action(K - 1, position, _) }))

  override lazy val goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  println("Kautz-Selman, plan length: " + taskSequenceLength)

  override def linearPlan: scala.Seq[Map[Task, String]] = Range(0, taskSequenceLength) map { case i => domain.primitiveTasks map { t => t -> { action(K - 1, i, t) } } toMap }


  override def linearStateFeatures: scala.Seq[Map[Predicate, String]] =
    Range(0, taskSequenceLength + 1) map { case i => domain.predicates map { p => p -> { statePredicate(K - 1, i, p) } } toMap }

}

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class KautzSelman(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int) extends LinearPrimitivePlanEncoding {
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
}

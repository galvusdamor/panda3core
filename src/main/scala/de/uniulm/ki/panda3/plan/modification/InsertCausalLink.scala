package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Equal, VariableConstraint}
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}

/**
 * Inserts a causal link
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class InsertCausalLink(plan: Plan, causalLink: CausalLink, equalityConstraints: Seq[Equal]) extends Modification {
  override def addedCausalLinks: Seq[CausalLink] = causalLink :: Nil

  override def addedVariableConstraints: Seq[VariableConstraint] = equalityConstraints map { case c: VariableConstraint => c}
}

object InsertCausalLink {

  def apply(plan: Plan, consumer: PlanStep, precondition: Literal): Seq[InsertCausalLink] =
    plan.planSteps flatMap { p => apply(plan, p, consumer, precondition)}

  /** creates causal links for the preconditions of the given consumer, supported by an effect of the producer */
  def apply(plan : Plan, producer: PlanStep, consumer: PlanStep, precondition: Literal): Seq[InsertCausalLink] = {
    val link = CausalLink(producer, consumer, precondition)
    producer.substitutedEffects map { l => (l #?# precondition)(plan.variableConstraints)} collect { case Some(mgu) => InsertCausalLink(plan, link, mgu)}
  }

  /** creates at most one causal links between effect of producer and preconditions of consumer */
  def apply(plan : Plan, producer: PlanStep, effect : Literal, consumer: PlanStep, precondition: Literal): Seq[InsertCausalLink] = {
    (effect #?# precondition)(plan.variableConstraints) match {
      case None => Nil
      case Some(mgu) => InsertCausalLink(plan, CausalLink(producer, consumer, precondition), mgu) :: Nil
    }
  }
}
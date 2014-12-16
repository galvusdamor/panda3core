package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Equal, Variable, VariableConstraint}
import de.uniulm.ki.panda3.domain.Task
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class InsertPlanStepWithLink(planStep: PlanStep, causalLink: CausalLink, equalityConstraints: Seq[Equal], plan: Plan) extends Modification {
  override def addedPlanSteps: Seq[PlanStep] = planStep :: Nil

  override def addedCausalLinks: Seq[CausalLink] = causalLink :: Nil

  // automatically infer which variables to add
  override def addedVariables: Seq[Variable] = planStep.arguments

  override def addedVariableConstraints: Seq[VariableConstraint] = equalityConstraints map { case c: VariableConstraint => c}
}

object InsertPlanStepWithLink {
  def apply(plan: Plan, schema: Task, consumer: PlanStep, precondition: Literal): Seq[InsertPlanStepWithLink] = {
    val parameter = for (schemaParameter <- schema.parameters) yield Variable.newVariable(schemaParameter.sort)
    val producer = PlanStep(plan.getNewId(), schema, parameter)
    val link = CausalLink(producer, consumer, precondition)

    val extendedCSP = plan.variableConstraints.addVariables(parameter)

    producer.substitutedEffects map { l => (l #?# precondition)(extendedCSP)} collect { case Some(mgu) => InsertPlanStepWithLink(producer, link, mgu, plan)}
  }

  def apply(plan: Plan, consumer: PlanStep, precondition: Literal): Seq[InsertPlanStepWithLink] = plan.domain.tasks flatMap { schema => apply(plan, schema, consumer, precondition)}
}
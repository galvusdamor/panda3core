package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Substitution, VariableConstraint}
import de.uniulm.ki.panda3.domain.{Domain, Task}
import de.uniulm.ki.panda3.logic.{Literal, Variable}
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class InsertPlanStepWithLink(planStep: PlanStep, causalLink: CausalLink, constraints: Seq[VariableConstraint], plan: Plan) extends Modification {
  override def addedPlanSteps: Seq[PlanStep] = planStep :: Nil

  override def addedCausalLinks: Seq[CausalLink] = causalLink :: Nil

  // automatically infer which variables to add
  override def addedVariables: Seq[Variable] = planStep.arguments

  override def addedVariableConstraints: Seq[VariableConstraint] = constraints
}

object InsertPlanStepWithLink {
  def apply(plan: Plan, schema: Task, consumer: PlanStep, precondition: Literal): Seq[InsertPlanStepWithLink] = {
    // generate new variables
    val firstFreeVariableID = plan.getFirstFreeVariableID
    val parameter = for (newVar <- schema.parameters zip (firstFreeVariableID until firstFreeVariableID + schema.parameters.size)) yield Variable(newVar._2, newVar._1.name, newVar._1.sort)
    val sub = Substitution(schema.parameters, parameter)
    val newConstraints = schema.parameterConstraints map {c => c.substitute(sub)}

    // new plan step
    val producer = PlanStep(plan.getFirstFreePlanStepID, schema, parameter)
    val link = CausalLink(producer, consumer, precondition)

    // new csp, for tight checking of possible causal links
    val extendedCSP = plan.variableConstraints.addVariables(parameter).addConstraints(newConstraints)

    producer.substitutedEffects map {l => (l #?# precondition)(extendedCSP)} collect {case Some(mgu) => InsertPlanStepWithLink(producer, link, newConstraints ++ mgu, plan)}
  }

  def apply(plan: Plan, consumer: PlanStep, precondition: Literal, domain: Domain): Seq[InsertPlanStepWithLink] = domain.tasks flatMap {schema => apply(plan, schema, consumer, precondition)}
}
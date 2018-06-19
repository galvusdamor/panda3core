// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.{PartialSubstitution, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Variable}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, PlanStep}

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
    val sub = PartialSubstitution(schema.parameters, parameter)
    val newConstraints = schema.parameterConstraints map { c => c.substitute(sub) }

    // new plan step
    val producer = PlanStep(plan.getFirstFreePlanStepID, schema, parameter)
    val link = CausalLink(producer, consumer, precondition)

    // new csp, for tight checking of possible causal links
    val extendedCSP = plan.variableConstraints.addVariables(parameter).addConstraints(newConstraints)

    producer.substitutedEffects map { l => (l #?# precondition) (extendedCSP) } collect { case Some(mgu) => InsertPlanStepWithLink(producer, link, newConstraints ++ mgu, plan) }
  }

  def apply(plan: Plan, consumer: PlanStep, precondition: Literal, domain: Domain): Seq[InsertPlanStepWithLink] =
    domain.producersOf(precondition.predicate) flatMap { schema => apply(plan, schema, consumer, precondition) }
}

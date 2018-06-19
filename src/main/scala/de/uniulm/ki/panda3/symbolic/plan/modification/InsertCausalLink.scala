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

import de.uniulm.ki.panda3.symbolic.csp.{Equal, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, PlanStep}

/**
  * Inserts a causal link
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class InsertCausalLink(plan: Plan, causalLink: CausalLink, equalityConstraints: Seq[VariableConstraint]) extends Modification {
  override def addedCausalLinks: Seq[CausalLink] = causalLink :: Nil

  override def addedVariableConstraints: Seq[VariableConstraint] = equalityConstraints map { case c: VariableConstraint => c }
}

object InsertCausalLink {

  def apply(plan: Plan, consumer: PlanStep, precondition: Literal): Seq[InsertCausalLink] =
    plan.planSteps flatMap { p => if (plan.orderingConstraints.gt(p, consumer)) Nil
    else {
      if (p != consumer || p.schema.isAbstract) apply(plan, p, consumer, precondition) else Nil
    }
    }

  /** creates causal links for the preconditions of the given consumer, supported by an effect of the producer */
  def apply(plan: Plan, producer: PlanStep, consumer: PlanStep, precondition: Literal): Seq[InsertCausalLink] = {
    val link = CausalLink(producer, consumer, precondition)
    producer.substitutedEffects map { l => (l #?# precondition) (plan.variableConstraints) } collect { case Some(mgu) => InsertCausalLink(plan, link, mgu) }
  }

  /** creates at most one causal links between effect of producer and preconditions of consumer */
  def apply(plan: Plan, producer: PlanStep, effect: Literal, consumer: PlanStep, precondition: Literal): Seq[InsertCausalLink] = {
    (effect #?# precondition) (plan.variableConstraints) match {
      case None      => Nil
      case Some(mgu) => InsertCausalLink(plan, CausalLink(producer, consumer, precondition), mgu) :: Nil
    }
  }
}

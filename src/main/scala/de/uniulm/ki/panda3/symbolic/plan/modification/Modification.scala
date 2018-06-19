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

import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint
import de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod
import de.uniulm.ki.panda3.symbolic.logic.Variable
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 * Trait describing an abstract modification. Every specific modification should extend this trait.
 *
 * The actually induced ordering constraints are inferred from the explicitly induced ones and those implicitly given as causal links
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Modification {

  val plan: Plan

  def addedVariables: Seq[Variable] = Nil

  def addedVariableConstraints: Seq[VariableConstraint] = Nil

  /** all added orderings constraints. contains the explicitly ones and the implicit ones, which are based on causal links */
  final def addedOrderingConstraints: Seq[OrderingConstraint] = (nonInducedAddedOrderingConstraints ++
    (addedCausalLinks collect { case CausalLink(producer, consumer, _) if producer.schema.isPrimitive && consumer.schema.isPrimitive => OrderingConstraint(producer, consumer)})
    ++ (addedPlanSteps flatMap { ps => OrderingConstraint(plan.init, ps) :: OrderingConstraint(ps, plan.goal) :: Nil})).distinct

  /* Adding modifications*/
  def addedPlanSteps: Seq[PlanStep] = Nil

  def addedCausalLinks: Seq[CausalLink] = Nil

  def setParentOfPlanSteps : Seq[(PlanStep,(PlanStep,PlanStep))] = Nil

  def setPlanStepDecomposedByMethod : Seq[(PlanStep,DecompositionMethod)]  = Nil

  def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = Nil


  /* removing modifications */
  def removedPlanSteps: Seq[PlanStep] = Nil

  def removedVariables: Seq[Variable] = Nil

  def removedCausalLinks: Seq[CausalLink] = Nil

  def removedOrderingConstraints: Seq[OrderingConstraint] = Nil

  def removedVariableConstraints: Seq[VariableConstraint] = Nil
}

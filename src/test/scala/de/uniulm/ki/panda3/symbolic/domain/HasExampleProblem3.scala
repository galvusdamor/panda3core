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

package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
// scalastyle:off magic.number
trait HasExampleProblem3 extends HasExampleDomain3 {
  val psInit1 = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val psGoal1 = PlanStep(1, goal1, instance_variableSort1(1) :: Nil)


  val psAbstract1    = PlanStep(2, abstractTask1, instance_variableSort1(1) :: Nil)
  val planPlanSteps1 = psInit1 :: psGoal1 :: psAbstract1 :: Nil

  val causalLinkInitAbstract = CausalLink(psInit1, psAbstract1, psInit1.substitutedEffects.head)
  val causalLinkAbstractGoal = CausalLink(psAbstract1, psGoal1, psGoal1.substitutedPreconditions.head)

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan1WithoutCausalLinks = Plan(planPlanSteps1, Nil,
                                     TaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                     CSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1, AllModifications, AllFlaws,Map(),Map())

  // create a plan  init| -p1> a1 -> |goal (with causal links)
  val plan1WithOneCausalLinks = Plan(planPlanSteps1, causalLinkInitAbstract :: Nil,
                                     TaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                     CSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1, AllModifications, AllFlaws,Map(),Map())


  // create a plan  init| -p1> a1 -p1> |goal (with causal links)
  val plan1WithBothCausalLinks = Plan(planPlanSteps1, causalLinkInitAbstract :: causalLinkAbstractGoal :: Nil,
                                      TaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                      CSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1, AllModifications, AllFlaws,Map(),Map())

}

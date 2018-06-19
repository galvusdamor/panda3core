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
trait HasExampleProblem4 extends HasExampleDomain4 {

  val psInit2 = PlanStep(0, init4, instance_variableSort1(1) :: Nil)
  val psGoal2 = PlanStep(1, goal4, Nil)


  val psAbstract2    = PlanStep(2, abstractTask2, instance_variableSort1(1) :: Nil)
  val planPlanSteps2 = psInit2 :: psGoal2 :: psAbstract2 :: Nil

  val causalLinkInit2Abstract2P1 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects.head)
  val causalLinkInit2Abstract2P2 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects(1))

  // create a plan  init| -> a1 -> |goal (with one causal link)
  val plan2WithoutLink = Plan(planPlanSteps2, Nil, TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2),
                              CSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2, AllModifications, AllFlaws, Map(), Map())

  // create a plan  init| -> a1 -> |goal (with one causal link)
  val plan2WithOneLink = Plan(planPlanSteps2, causalLinkInit2Abstract2P1 :: Nil, TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2),
                              CSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2, AllModifications, AllFlaws, Map(), Map())

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan2WithTwoLinks = Plan(planPlanSteps2, causalLinkInit2Abstract2P1 :: causalLinkInit2Abstract2P2 :: Nil,
                               TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2), CSP(Set(instance_variableSort1(1)), Nil),
                               psInit2, psGoal2, AllModifications, AllFlaws, Map(), Map())

  val psAbstract3                    = PlanStep(2, abstractTask3, Nil)
  // create a plan  init| -> a3 -> |goal (with one causal link)
  val plan2WithoutLinkAndEpsilonTask = Plan(psInit2 :: psGoal2 :: psAbstract3 :: Nil, Nil,
                                            TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract3), psInit2 :: psGoal2 :: psAbstract3 :: Nil),
                                            CSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2, AllModifications, AllFlaws, Map(), Map())


}

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
import de.uniulm.ki.panda3.symbolic.logic.{And, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasExampleDomain4 extends HasExampleDomain2 {

  val task3: ReducedTask = ReducedTask("task3", isPrimitive = true, variableSort1(2) :: Nil, Nil, Nil,
                                       precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(2) :: Nil) :: Nil), effect = And[Literal](Nil))
  val task4: ReducedTask = ReducedTask("task4", isPrimitive = false, variableSort1(3) :: Nil, Nil, Nil,
                                       precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(3) :: Nil) :: Nil), effect = And[Literal](Nil))

  val task5: ReducedTask = ReducedTask("task3", isPrimitive = true, variableSort1(4) :: Nil, Nil, Nil,
                                       precondition = And[Literal](Literal(predicate2, isPositive = true, variableSort1(4) :: Nil) :: Nil), effect = And[Literal](Nil))
  val task6: ReducedTask = ReducedTask("task4", isPrimitive = false, variableSort1(5) :: Nil, Nil, Nil,
                                       precondition = And[Literal](Literal(predicate2, isPositive = true, variableSort1(5) :: Nil) :: Nil), effect = And[Literal](Nil))

  val abstractTask2: ReducedTask = ReducedTask("abstractTask_2", isPrimitive = false, variableSort1(7) :: Nil, Nil, Nil,
                                               precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(7) :: Nil) ::
                                                                             Literal(predicate2, isPositive = true, variableSort1(7) :: Nil) :: Nil), effect = And[Literal](Nil))
  val abstractTask3: ReducedTask = ReducedTask("abstractTask_3", isPrimitive = false, Nil, Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](Nil))


  // decomposition method 3, with two tasks
  val initTaskOfPlanOfDecompositionMethod3: ReducedTask = ReducedTask("initM3", isPrimitive = true, variableSort1(8) :: Nil, Nil, Nil, precondition = And[Literal](Nil), effect =
    And[Literal](Literal(predicate1, isPositive = true, variableSort1(8) :: Nil) :: Literal(predicate2, isPositive = true, variableSort1(8) :: Nil) :: Nil))
  val goalTaskOfPlanOfDecompositionMethod3: ReducedTask = ReducedTask("goalM3", isPrimitive = true, Nil, Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](Nil))

  val initOfPlanOfDecompositionMethod3           : PlanStep     = PlanStep(0, initTaskOfPlanOfDecompositionMethod3, variableSort1(7) :: Nil)
  val goalOfPlanOfDecompositionMethod3           : PlanStep     = PlanStep(5, goalTaskOfPlanOfDecompositionMethod3, Nil)
  val actualPlanStep1OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(1, task3, variableSort1(8) :: Nil)
  val actualPlanStep2OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(2, task4, variableSort1(9) :: Nil)
  val actualPlanStep3OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(3, task5, variableSort1(10) :: Nil)
  val actualPlanStep4OfPlanOfDecompositionMethod3: PlanStep     = PlanStep(4, task6, variableSort1(11) :: Nil)
  val planStepsOfPlanOfDecompositionMethod3                     = initOfPlanOfDecompositionMethod3 :: goalOfPlanOfDecompositionMethod3 :: actualPlanStep1OfPlanOfDecompositionMethod3 ::
    actualPlanStep2OfPlanOfDecompositionMethod3 :: actualPlanStep3OfPlanOfDecompositionMethod3 :: actualPlanStep4OfPlanOfDecompositionMethod3 :: Nil
  val taskOrderingOfPlanOfDecompositionMethod3   : TaskOrdering = TaskOrdering(OrderingConstraint.allBetween(initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3,
                                                                                                             actualPlanStep1OfPlanOfDecompositionMethod3,
                                                                                                             actualPlanStep2OfPlanOfDecompositionMethod3,
                                                                                                             actualPlanStep3OfPlanOfDecompositionMethod3,
                                                                                                             actualPlanStep4OfPlanOfDecompositionMethod3),
                                                                               planStepsOfPlanOfDecompositionMethod3)
  val cspOfPlanOfDecompositionMethod3            : CSP          = CSP(Set(variableSort1(7), variableSort1(8), variableSort1(9), variableSort1(10), variableSort1(11)), Nil)

  val planOfDecompositionMethod3: Plan                      = Plan(planStepsOfPlanOfDecompositionMethod3, Nil, taskOrderingOfPlanOfDecompositionMethod3,
                                                                   cspOfPlanOfDecompositionMethod3, initOfPlanOfDecompositionMethod3, goalOfPlanOfDecompositionMethod3,
                                                                   AllModifications, AllFlaws, Map(), Map())
  /** a decomposition method without causal links */
  val decompositionMethod3      : SimpleDecompositionMethod = SimpleDecompositionMethod(abstractTask2, planOfDecompositionMethod3, "some method")

  /** an empty decomposition method */
  val noop                      : ReducedTask               = ReducedTask("__noop", isPrimitive = true, Nil,Nil, Nil, And[Literal](Nil), And[Literal](Nil))
  val epsilonInit                                           = PlanStep(0, noop, Nil)
  val epsilonGoal                                           = PlanStep(1, noop, Nil)
  val decompositionMethodEpsilon: SimpleDecompositionMethod =
    SimpleDecompositionMethod(abstractTask3,
                              Plan(epsilonInit :: epsilonGoal :: Nil, Nil, TaskOrdering(OrderingConstraint.allBetween(epsilonInit, epsilonGoal), epsilonInit :: epsilonGoal :: Nil),
                                   CSP(Set(), Nil), epsilonInit, epsilonGoal, AllModifications, AllFlaws, Map(), Map()), "some method")

  val init4: ReducedTask = ReducedTask("init", isPrimitive = true, variableSort1(3) :: Nil, Nil, Nil, precondition = And[Literal](Nil), effect =
    And[Literal](Literal(predicate1, isPositive = true, variableSort1(3) :: Nil) :: Literal(predicate2, isPositive = true, variableSort1(3) :: Nil) :: Nil))
  val goal4: ReducedTask = ReducedTask("goal", isPrimitive = true, Nil, Nil, Nil, precondition = And[Literal](Nil), effect = And[Literal](Nil))


  val domain4 = Domain(sort1 :: Nil, predicate1 :: predicate2 :: Nil, task1 :: task2 :: task3 :: task4 :: task5 :: task6 :: abstractTask2 :: abstractTask3 :: Nil, decompositionMethod3 ::
    decompositionMethodEpsilon :: Nil, Nil)
}

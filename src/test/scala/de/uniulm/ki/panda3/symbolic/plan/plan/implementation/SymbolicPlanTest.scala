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

package de.uniulm.ki.panda3.symbolic.plan.plan.implementation

import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, CausalLink, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class SymbolicPlanTest extends FlatSpec {

  val sort1     : Sort      = Sort("sort1", Vector() :+ Constant("a") :+ Constant("b"), Nil)
  val predicate1: Predicate = Predicate("predicate1", sort1 :: sort1 :: Nil)
  val psInit                = PlanStep(0, ReducedTask("init", true, Nil, Nil, Nil, And(Nil), And(Nil)), Nil)
  val psGoal                = PlanStep(1, ReducedTask("goal", true, Nil, Nil, Nil, And(Nil), And(Nil)), Nil)


  val d_v1 = Variable(1, "d_v1", sort1)
  val d_v2 = Variable(2, "d_v2", sort1)
  //  val d_v3 = Variable("d_v3", sort2)
  //  val d_v4 = Variable("d_v4", sort2)
  //  val d_v5 = Variable("d_v5", sort2)
  //  val d_v6 = Variable("d_v6", sort2)
  //  val d_v7 = Variable("d_v7", sort2)
  val p_v1 = Variable(3, "p_v1", sort1)
  val p_v2 = Variable(4, "p_v2", sort1)
  val p_v3 = Variable(5, "p_v2", sort1)
  val p_v4 = Variable(6, "p_v2", sort1)


  val schemaProd : ReducedTask = ReducedTask("task_prod", isPrimitive = true, d_v1 :: d_v2 :: Nil, Nil, Nil, And[Literal](Nil), And[Literal](Literal(predicate1, isPositive = true, d_v1 :: d_v2 ::
    Nil) :: Nil))
  val schemaCons : ReducedTask = ReducedTask("task_cons", isPrimitive = true, d_v1 :: d_v2 :: Nil, Nil, Nil, And[Literal](Literal(predicate1, isPositive = true, d_v1 :: d_v2 :: Nil) :: Nil),
                                             And[Literal](Nil))
  val schemaDestr: ReducedTask = ReducedTask("task_destr", isPrimitive = true, d_v1 :: d_v2 :: Nil, Nil, Nil, And[Literal](Nil),
                                             And[Literal](Literal(predicate1, isPositive = false, d_v1 :: d_v2 :: Nil) ::
                                                            Nil))

  "Computing open preconditions" must "be possible" in {
    val plan1PlanSteps = psInit :: psGoal :: PlanStep(2, schemaCons, p_v1 :: p_v2 :: Nil) :: Nil
    val plan1: Plan = Plan(plan1PlanSteps, Nil, TaskOrdering(Nil, plan1PlanSteps).addOrderings(OrderingConstraint.allBetween(psInit, psGoal, plan1PlanSteps(2))),
                           CSP(Set(p_v1, p_v2), Nil), psInit, psGoal, AllModifications, AllFlaws,Map(),Map())

    assert(plan1.allPreconditions.size == 1)
    assert(plan1.openPreconditions.size == 1)
  }

  "Computing open preconditions" must "not contain protected preconditions" in {
    val plan1PlanSteps = psInit :: psGoal :: PlanStep(2, schemaCons, p_v1 :: p_v2 :: Nil) :: PlanStep(3, schemaCons, p_v2 :: p_v1 :: Nil) :: Nil
    val plan1: Plan = Plan(plan1PlanSteps, CausalLink(plan1PlanSteps(3), plan1PlanSteps(2), Literal(predicate1, isPositive = true, p_v1
      :: p_v2 :: Nil)) :: Nil, TaskOrdering(Nil, plan1PlanSteps).addOrderings(OrderingConstraint.allBetween(psInit, psGoal, plan1PlanSteps(2), plan1PlanSteps(3))),
                           CSP(Set(p_v1, p_v2), Nil), psInit, psGoal, AllModifications, AllFlaws,Map(),Map())

    assert(plan1.allPreconditions.size == 2)
    assert(plan1.openPreconditions.size == 1)
    assert(plan1.openPreconditions.forall(_.planStep.id == 3))
  }


  "Computing causal threats" must "be possible" in {
    val ps0 = PlanStep(2, schemaProd, p_v1 :: p_v2 :: Nil)
    val ps1 = PlanStep(3, schemaCons, p_v1 :: p_v2 :: Nil)
    val ps2 = PlanStep(4, schemaDestr, p_v3 :: p_v4 :: Nil)
    val plan1PlanSteps = psInit :: psGoal :: ps0 :: ps1 :: ps2 :: Nil
    val plan1: Plan = Plan(plan1PlanSteps, CausalLink(ps0, ps1, Literal(predicate1, isPositive = true, p_v1 :: p_v2 :: Nil)) :: Nil,
                           TaskOrdering(Nil, plan1PlanSteps).addOrderings(OrderingConstraint.allBetween(psInit, psGoal, ps0, ps1, ps2)),
                           CSP(Set(p_v1, p_v2, p_v3, p_v4), Nil), psInit, psGoal, AllModifications, AllFlaws,Map(),Map())
    assert(plan1.causalThreats.size == 1)
  }

}

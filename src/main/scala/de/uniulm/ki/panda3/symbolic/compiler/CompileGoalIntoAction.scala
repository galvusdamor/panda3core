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

package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, SimpleDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic.{And, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object CompileGoalIntoAction extends DomainTransformer[Unit] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val initAndGoalNOOP = ReducedTask("__noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))


    ///////////// DECOMP METHOD for initial plan
    // TODO not yet correct for hybrid planning problems
    val noopInit = PlanStep(plan.init.id, initAndGoalNOOP, Nil)
    val noopGoal = PlanStep(plan.goal.id, initAndGoalNOOP, Nil)
    val initialPlanWithout = plan.replaceInitAndGoal(noopInit, noopGoal, plan.init.arguments ++ plan.goal.arguments)
    val planTask = ReducedTask("__orig_plan", isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil))
    val planMethod = SimpleDecompositionMethod(planTask, initialPlanWithout, "__plan")


    ///// top method
    val psInit = plan.init.copy(id = 0)
    val psGoal = noopGoal.copy(id = 1)
    val psPlanTask = PlanStep(2, planTask, Nil)
    val planSteps = psInit :: psPlanTask :: plan.goal :: psGoal :: Nil
    val ordering = TaskOrdering.totalOrdering(planSteps)
    val csp = CSP(psInit.argumentSet, Nil)
    val initialPlan = Plan(planSteps, Nil, ordering, csp, psInit, psGoal,plan.isModificationAllowed,plan.isFlawAllowed, Map(), Map(),
                           plan.dontExpandVariableConstraints,plan.ltlConstraint)


    val newDomain = domain.copy(tasks = (domain.tasks :+ plan.goal.schema :+ planTask).distinct,
                                decompositionMethods = domain.decompositionMethods :+ planMethod,
                                sasPlusRepresentation = None)

    (newDomain, initialPlan)
  }
}

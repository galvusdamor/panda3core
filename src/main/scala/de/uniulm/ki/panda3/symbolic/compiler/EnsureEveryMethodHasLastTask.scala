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

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object EnsureEveryMethodHasLastTask extends DecompositionMethodTransformer[Unit] {

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Unit, originalDomain: Domain): (Seq[DecompositionMethod], Seq[Task]) = {
    val noopTask = ReducedTask("noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0))

    val transformedMethods = methods :+ topMethod map {
      case m if m.subPlan.orderingConstraints.graph.sinks.size == 1 => m
      case m: SimpleDecompositionMethod                             =>
        val noopPS = PlanStep(m.subPlan.planStepsAndRemovedPlanSteps.map(_.id).max + 1, noopTask, Nil)
        val newOrdering = m.subPlan.orderingConstraints.addPlanStep(noopPS).addOrderings(m.subPlan.orderingConstraints.graph.sinks map { l => OrderingConstraint(l, noopPS) }).
          addOrdering(noopPS, m.subPlan.goal).addOrdering(m.subPlan.init,noopPS)
        val newSubPlan = m.subPlan.copy(planStepsAndRemovedPlanSteps = m.subPlan.planStepsAndRemovedPlanSteps :+ noopPS, orderingConstraints = newOrdering)

        SimpleDecompositionMethod(m.abstractTask, newSubPlan, m.name)
      case _                                                        => noSupport(NONSIMPLEMETHOD)
    }


    (transformedMethods, noopTask :: Nil)
  }

  override protected val transformationName = "last_action_in_method"
}

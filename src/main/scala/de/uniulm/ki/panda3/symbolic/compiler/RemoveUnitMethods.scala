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

import de.uniulm.ki.panda3.symbolic.domain.updates.ExchangePlanSteps
import de.uniulm.ki.util._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * Removes all unit methods from the domain by compilation. Only accepts grounded domains
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object RemoveUnitMethods extends DecompositionMethodTransformer[Unit] {

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, unit : Unit, originalDomain : Domain):
  (Seq[DecompositionMethod],Seq[Task]) = {

    val (unitMethods, nonUnitMethods) = methods partition { _.subPlan.planStepsWithoutInitGoal.size == 1 }
    val oneStepReplacementRules: Map[Task, Seq[Task]] = unitMethods map { case SimpleDecompositionMethod(abstractTask, subPlan, _) =>
      (abstractTask, subPlan.planStepsWithoutInitGoal.head.schema)
    } groupBy { _._1 } map { case (a, b) => a -> (b map { _._2 }) } toMap

    def expand(current: Map[Task, Seq[Task]]): Map[Task, Seq[Task]] = {
      val expansion = current map { case (from, toList) =>
        val nextStep = toList filter current.contains flatMap current
        (from, (toList ++ nextStep) distinct)
      }
      if (expansion == current) current else expand(expansion)
    }

    val replacementRules: Map[Task, Seq[Task]] = expand(oneStepReplacementRules)

    ((nonUnitMethods :+ topMethod) flatMap { case SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
      val replaceablePlanSteps = subPlan.planStepsWithoutInitGoal filter { replacementRules contains _.schema }
      val replacementPossibilities: Seq[Seq[PlanStep]] = allSubsets(replaceablePlanSteps)

      val allPlanStepSubstitutions = replacementPossibilities flatMap { planStepsToReplace =>
        planStepsToReplace.foldLeft[Seq[Seq[(PlanStep, PlanStep)]]](Nil :: Nil)(
          { case (mappings, planStep) => replacementRules(planStep.schema) flatMap { newTask => mappings map { mapping => (planStep, PlanStep(planStep.id, newTask, Nil)) +: mapping } } })
      }

      allPlanStepSubstitutions map { substitution =>
        SimpleDecompositionMethod(abstractTask, subPlan update ExchangePlanSteps(substitution.toMap), methodName)
      }
    }, Nil)
  }

  override protected val transformationName: String = "unitMethod"

  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    assert(domain.predicates forall { _.argumentSorts.isEmpty })
    super.transform(domain, plan, info)
  }
}

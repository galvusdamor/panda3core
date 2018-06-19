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

package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, SimpleDecompositionMethod, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.modification.{DecomposePlanStep, InsertCausalLink, InsertPlanStepWithLink, Modification}


import de.uniulm.ki.panda3.symbolic._

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class OpenPrecondition(plan: Plan, planStep: PlanStep, precondition: Literal) extends Flaw {
  override def computeAllResolvents(domain: Domain): Seq[Modification] = InsertPlanStepWithLink(plan, planStep, precondition, domain) ++ InsertCausalLink(plan, planStep, precondition) ++
    resolverByDecompose(domain)


  private def resolverByDecompose(domain: Domain): Seq[Modification] = {

    val possibleDecompositions: Seq[(PlanStep, SimpleDecompositionMethod)] =
      plan.planStepsWithoutInitGoal filterNot { producer => plan.orderingConstraints.gt(producer, planStep) } flatMap { ps =>
        domain.taskSchemaTransitionGraph.canBeDirectlyDecomposedIntoForPredicate((ps.schema, precondition.predicate, precondition.isPositive)) map {
          case method: SimpleDecompositionMethod => (ps, method.asInstanceOf[SimpleDecompositionMethod])
          case _                                 => noSupport(NONSIMPLEMETHOD)
        }
      }
    /*
      (plan.planStepWithoutInitGoal filter { _ != planStep }) flatMap { ps => domain.taskSchemaTransitionGraph.canBeDirectlyDecomposedIntoVia(ps.schema) map {
        case (method, reduced: ReducedTask) => (method, reduced)
        case _                              => noSupport(FORUMLASNOTSUPPORTED)
      } collect {
        case (method: SimpleDecompositionMethod, task) if task.effect.conjuncts exists {
          case Literal(predicate, isPositive, _) => precondition.predicate == predicate && precondition.isPositive == isPositive
        }                                                                   => (ps, method.asInstanceOf[SimpleDecompositionMethod])
        case (method, _) if !method.isInstanceOf[SimpleDecompositionMethod] => noSupport(NONSIMPLEMETHOD)
      }
      }
    */
    possibleDecompositions flatMap { case (ps, method) => DecomposePlanStep(plan, ps, method) }
  }

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "OpenPrecondition: " + precondition.shortInfo + " of " + planStep.shortInfo
}

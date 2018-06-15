// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2017 the original author or authors.
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

import de.uniulm.ki.panda3.symbolic.compiler.pruning.PruneUselessAbstractTasks
import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, Task, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.DecomposePlanStep

/**
  * This expands choiceless abstract tasks as one step, but does not remove newly useless ATs (this would require full propagation).
  * To use the transformator properly, we have to repeat it until nowthing changes
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ExpandChoicelessAbstractTasks extends DecompositionMethodTransformer[Unit] {

  override protected val transformationName: String = "choicelessAT"

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, unit: Unit, originalDomain: Domain):
  (Seq[DecompositionMethod], Seq[Task]) = {

    // we don't want lifted domains nor hybrid ones
    assert(originalDomain.isGround)
    assert(!originalDomain.isHybrid)
    // find choiceless ATs
    val choicelessAbstractTasksWithMethod: Map[Task, DecompositionMethod] = originalDomain.choicelessAbstractTasks map { at => at -> originalDomain.methodsForAbstractTasks(at).head } toMap
    val uselessMethods = choicelessAbstractTasksWithMethod.values.toSet

    val remainingMethods = methods filterNot uselessMethods.contains

    (uselessMethods.toSeq ++ ((remainingMethods :+ topMethod) map { case sm@SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
      val planStepsToExpand = subPlan.planStepsWithoutInitGoal filter { ps => originalDomain.choicelessAbstractTasks contains ps.schema }

      val newPlan = planStepsToExpand.foldLeft(subPlan)(
        {
          case (plan,planStepToReplace) =>
            val possibleDecompositions = DecomposePlanStep(plan, planStepToReplace, originalDomain)
            assert(possibleDecompositions.length == 1)
            plan.modify(possibleDecompositions.head).normalise
        })

      SimpleDecompositionMethod(abstractTask, newPlan, methodName)
    }), Nil)

  }
}


object RemoveChoicelessAbstractTasks extends DomainTransformer[Unit] {
  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = if (domain.choicelessAbstractTasks.isEmpty) (domain,plan) else {
    // try to propagate
    val propagated = ExpandChoicelessAbstractTasks.transform(domain, plan, ())
    val removed = PruneUselessAbstractTasks.transform(propagated, ())

    transform(removed, ())
  }
}

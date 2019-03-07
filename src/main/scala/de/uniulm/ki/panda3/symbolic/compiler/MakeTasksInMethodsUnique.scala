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
import de.uniulm.ki.panda3.symbolic.domain.updates.ExchangePlanSteps
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object MakeTasksInMethodsUnique extends DecompositionMethodTransformer[Unit] {

  override protected val allowToRemoveTopMethod: Boolean = false

  val emptySchema = ReducedTask("empty", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0))

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Unit, originalDomain: Domain): (Seq[DecompositionMethod], Seq[Task]) = {
    var newAbstractTaskID = 0

    val compilationResult : Seq[(Seq[DecompositionMethod],Seq[Task])] = (methods :+ topMethod) map { case method =>
      if (method.subPlan.planStepsWithoutInitGoal.length == method.subPlan.planStepsWithoutInitGoal.map(_.schema).distinct.size) {
        (method :: Nil, Nil)
      }else {
        // there are duplicats
        val duplicatedPlanSteps: Map[Task, Seq[PlanStep]] = method.subPlan.planStepsWithoutInitGoal.groupBy(_.schema) filter { _._2.size > 1 }

        val result: Seq[(Task, DecompositionMethod, (PlanStep, PlanStep))] = duplicatedPlanSteps flatMap { case (_, pss) =>
          pss map { case ps =>
            // create new abstract task
            val newAbstract = ReducedTask(ps.schema.name + "_replacement_" + newAbstractTaskID, isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0))
            val newPlan = Plan(PlanStep(2, ps.schema, Nil) :: Nil, emptySchema, emptySchema, Map[PlanStep, DecompositionMethod](), Map[PlanStep, (PlanStep, PlanStep)]())
            val newMethod = SimpleDecompositionMethod(newAbstract, newPlan, newAbstract.name)
            newAbstractTaskID += 1

            (newAbstract, newMethod, (ps, ps.copy(schema = newAbstract)))
          }
        } toSeq


        val newTasks = result.map(_._1)
        val newMethods = result.map(_._2)
        val replacement = method update ExchangePlanSteps(result.map(_._3).toMap)

        (newMethods :+ replacement, newTasks)
      }
    }


    (compilationResult.flatMap(_._1),compilationResult.flatMap(_._2))
  }

  override protected val transformationName: String = "MakeMethodSubTasksUnique"
}

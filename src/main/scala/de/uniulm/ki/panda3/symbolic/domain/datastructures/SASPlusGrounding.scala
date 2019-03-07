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

package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem
import de.uniulm.ki.panda3.symbolic.csp.SymbolicUnionFind
import de.uniulm.ki.panda3.symbolic.domain.{ConstantActionCost, Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SASPlusGrounding(domain: Domain, problem: Plan, sasPlusProblem: SasPlusProblem) extends GroundedPrimitiveReachabilityAnalysis {


  override lazy val reachableGroundLiterals: Seq[GroundLiteral] =
    ((reachableGroundPrimitiveActions flatMap { t => t.substitutedPreconditions ++ t.substitutedEffects }) ++ problem.groundedInitialState) distinct

  override lazy val reachableGroundPrimitiveActions: Seq[GroundTask] = tempReachableGroundPrimitiveActions

  lazy val sasPlusPredicates: Array[Predicate] = sasPlusProblem.factStrs map { v => Predicate(/*"var" + varNum + "=" +*/ v, Nil) }

  val tasksWithMultipleGroundings: Map[GroundTask, Int] = tempTasksWithMultipleGroundings

  lazy val (groundedTasksToNewGroundTasksMapping, tempReachableGroundPrimitiveActions, sasPlusTaskIndexToNewGroundTask, tempTasksWithMultipleGroundings) = {
    val generalActions: Seq[((GroundTask, Task), (Int, Task))] = sasPlusProblem.getGroundedOperatorSignatures.zipWithIndex map { case (op, i) =>
      val splitted = op split " "
      val operatorName = splitted.head
      val parameter = splitted.tail

      import de.uniulm.ki.panda3.symbolic.writer._
      val taskOption: Option[Task] = domain.primitiveTasks find { t => toPDDLIdentifier(t.name).toLowerCase == operatorName }

      (taskOption, parameter, i)
    } collect { case (Some(t), parameterStrings, taskIndex) =>
      val taskUF = SymbolicUnionFind.constructVariableUnionFind(t)

      // generate the actual parameter list (may contain constants that where omitted when writing the PDDL format)
      val (params, remainingParameter) = t.parameters.foldLeft[(Seq[Constant], Array[String])]((Nil, parameterStrings))(
        {
          case ((paramsSoFar, nextParams), parameter) =>
            taskUF.getRepresentative(parameter) match {
              case v: Variable =>
                import de.uniulm.ki.panda3.symbolic.writer._

                //if (domain.constants.find(p => toPDDLIdentifier(p.name).toLowerCase == nextParams.head).isEmpty)
                //  println("OCH NEE")

                (paramsSoFar.:+(domain.constants.find(p => toPDDLIdentifier(p.name).toLowerCase == nextParams.head).get), nextParams.tail)
              case c: Constant => (paramsSoFar.:+(c), nextParams)
            }
        })
      assert(remainingParameter.length == 0)
      val groundTask = GroundTask(t, params)

      // build new task
      val sasGroundTask = {
        val precondition = sasPlusProblem.precLists(taskIndex) map { prec => Literal(sasPlusPredicates(prec), isPositive = true, Nil) }
        val addEffects = sasPlusProblem.addLists(taskIndex) map { eff => Literal(sasPlusPredicates(eff), isPositive = true, Nil) }
        //val delEffects = sasPlusProblem.delLists(taskIndex) map { eff => Literal(sasPlusPredicates(eff), isPositive = false, Nil) }
        val delEffects = sasPlusProblem.expandedDelLists(taskIndex) map { eff => Literal(sasPlusPredicates(eff), isPositive = false, Nil) }

        // XXX grounding!!!
        ReducedTask(t.name + params.map { _.name }.mkString("[", ",", "]"), isPrimitive = true, Nil, Nil, Nil, And(precondition), And(addEffects ++ delEffects),
                    t.cost.evaluateOnGrounding(groundTask.parameterSubstitution, domain.costValues))
      }

      (groundTask -> sasGroundTask, taskIndex -> sasGroundTask)
    }

    val newInitTask = {
      val effects = sasPlusProblem.s0List map { eff => Literal(sasPlusPredicates(eff), isPositive = true, Nil) }
      ReducedTask("init", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(effects), ConstantActionCost(0))
    }

    val newGoalTask = {
      val preconditions = sasPlusProblem.gList map { prec => Literal(sasPlusPredicates(prec), isPositive = true, Nil) }
      ReducedTask("goal", isPrimitive = true, Nil, Nil, Nil, And(preconditions), And(Nil), ConstantActionCost(0))
    }

    //val groundedToNewGroundMap: Map[GroundTask, Task] = (generalActions map { _._1 })
    // toMap

    // check for duplicates in general actions map
    val sasActionsGroupedByGroundAction: Map[GroundTask, Seq[((GroundTask, Task), (Int, Task))]] = generalActions.groupBy(_._1._1)
    val groundTasksWithMultipleGroundings: Map[GroundTask, Int] = sasActionsGroupedByGroundAction filter { _._2.size != 1 } map { case (gt, l) => gt -> l.size }

    val sasPlusIndexMap: Map[Int, Task] = sasActionsGroupedByGroundAction flatMap {
      case (_, indexList) if indexList.size == 1 =>
        (indexList.head._2._1 -> indexList.head._2._2) :: Nil
      case (_, indexList)                        =>
        indexList.zipWithIndex map { case ((_, (sasIndex, task)), copyIndex) =>
          sasIndex -> task.asInstanceOf[ReducedTask].copy(name = task.name + "#" + copyIndex)
        }
    }


    val simplifiedGroundedToNewGroundMap: Map[(String, Seq[Constant]), Task] = (sasActionsGroupedByGroundAction flatMap {
      case (gtInModel, instance) if instance.size == 1 =>
        ((gtInModel.task.name, gtInModel.arguments) -> instance.head._1._2) :: Nil

      case (gtInModel, possibleInstances) =>
        possibleInstances.zipWithIndex map { case (((_, groundedTask), _), index) =>
          (gtInModel.task.name + "#" + index, gtInModel.arguments) -> groundedTask.asInstanceOf[ReducedTask].copy(name = groundedTask.name + "#" + index)
        }
    }) ++ (((problem.groundedInitialTask.task.name, problem.groundedInitialTask.arguments), newInitTask) ::
      ((problem.groundedGoalTask.task.name, problem.groundedGoalTask.arguments), newGoalTask) :: Nil)

    (simplifiedGroundedToNewGroundMap, generalActions.map(_._1._1).distinct :+ problem.groundedInitialTask :+ problem.groundedGoalTask,
      sasPlusIndexMap, groundTasksWithMultipleGroundings)
  }
}

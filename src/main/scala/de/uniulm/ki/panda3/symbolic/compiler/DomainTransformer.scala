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
import de.uniulm.ki.panda3.symbolic.domain.updates.RemoveVariables
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * represents any possible domain Transformation
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DomainTransformer[Information] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domainAndPlan: (Domain, Plan), info: Information): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2, info)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domain: Domain, plan: Plan, info: Information): (Domain, Plan) = transform(domain, plan, info)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domainAndPlan: (Domain, Plan), info: Information): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2, info)
}

trait DomainTransformerWithOutInformation extends DomainTransformer[Unit] {
  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan): (Domain, Plan) = transform(domain, plan, ())

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domain: Domain, plan: Plan): (Domain, Plan) = transform(domain, plan)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2)
}

trait DecompositionMethodTransformer[Information] extends DomainTransformer[Information] {

  protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Information, originalDomain: Domain): (Seq[DecompositionMethod], Seq[Task])

  protected val transformationName: String

  protected val allowToRemoveTopMethod = true


  override def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan) = {
    // create an artificial method
    // TODO not yet correct for hybrid planning problems

    val (topTask, topMethod, addedTop) = if (!allowToRemoveTopMethod &&
      plan.planStepsWithoutInitGoal.size == 1 && domain.methodsForAbstractTasks(plan.planStepsWithoutInitGoal.head.schema).size == 1 && domain.isGround) {
      val _topTask = plan.planStepsWithoutInitGoal.head.schema
      val _topMethod = domain.methodsForAbstractTasks(_topTask).head

      //println("\n\nTaking existing top task " + _topTask.name)
      (_topTask, _topMethod, false)
    } else {
      val initAndGoalNOOP = ReducedTask("__noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0))
      val topInit = PlanStep(plan.init.id, initAndGoalNOOP, Nil)
      val topGoal = PlanStep(plan.goal.id, initAndGoalNOOP, Nil)
      val initialPlanWithout = plan.replaceInitAndGoal(topInit, topGoal, plan.init.arguments ++ plan.goal.arguments)
      val _topTask = ReducedTask("__" + transformationName + "Compilation__top_" + DecompositionMethodTransformer.instanceCounter,
                                 isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0))
      val _topMethod = SimpleDecompositionMethod(_topTask, initialPlanWithout, "__top_" + DecompositionMethodTransformer.instanceCounter)
      DecompositionMethodTransformer.instanceCounter += 1

      (_topTask, _topMethod, true)
    }


    val (extendedMethods, newTasksProposed) = transformMethods(domain.decompositionMethods filterNot { _ == topMethod }, topMethod, info, domain)

    val newTasks = newTasksProposed filterNot domain.taskSet.contains

    val addedPrimitiveTasks = newTasks filter { _.isPrimitive }
    assert(addedPrimitiveTasks forall { _.precondition.isEmpty })
    assert(addedPrimitiveTasks forall { _.effect.isEmpty })

    val newSasPlus = domain.sasPlusRepresentation match {
      case None                                                               => None
      case Some(SASPlusRepresentation(problem, indexToTask, predicateToTask)) =>

        val newMapEntries = addedPrimitiveTasks map { case prim =>
          problem.numOfOperators += 1
          problem.opNames = problem.opNames :+ prim.name
          problem.precLists = problem.precLists :+ new Array[Int](0)
          problem.addLists = problem.addLists :+ new Array[Int](0)
          problem.delLists = problem.delLists :+ new Array[Int](0)
          problem.expandedDelLists = problem.expandedDelLists :+ new Array[Int](0)
          problem.numPrecs = problem.numPrecs :+ 0
          problem.costs = problem.costs :+ 0


          (problem.numOfOperators - 1, prim)
        }

        Some(SASPlusRepresentation(problem, indexToTask ++ newMapEntries, predicateToTask))

    }

    val numberOfTopMethods = extendedMethods count { _.abstractTask == topTask }
    if (numberOfTopMethods == 0) {
      // if the compiler does not want to add a top method, it's ok
      (domain.copy(decompositionMethods = extendedMethods :+ topMethod, tasks = domain.tasks ++ newTasks), plan)
    } else if (!addedTop) {
      (domain.copy(decompositionMethods = extendedMethods, tasks = domain.tasks ++ newTasks, sasPlusRepresentation = newSasPlus), plan)
    } else if (numberOfTopMethods == 1 && allowToRemoveTopMethod) {
      // regenerate the initial plan, as it may have changed
      val remainingTopMethod = (extendedMethods find { _.abstractTask == topTask }).get.subPlan
      val newPlan = remainingTopMethod.replaceInitAndGoal(plan.init, plan.goal, Nil)
      (domain.copy(decompositionMethods = extendedMethods filterNot { _.abstractTask == topTask }, tasks = domain.tasks ++ newTasks, sasPlusRepresentation = newSasPlus), newPlan)
    } else {
      // generate a new
      val topPS = PlanStep(2, topTask, Nil)
      val planSteps: Seq[PlanStep] = plan.init :: plan.goal :: topPS :: Nil
      val ordering = TaskOrdering(OrderingConstraint.allBetween(plan.init, plan.goal, topPS), planSteps)
      val unnecessaryVariables = plan.variableConstraints.variables filterNot { v => planSteps exists { _.arguments.contains(v) } } toSeq
      val initialPlan = Plan(planSteps, Nil, ordering, plan.variableConstraints update RemoveVariables(unnecessaryVariables), plan.init, plan.goal,
                             plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map(), plan.dontExpandVariableConstraints, plan.ltlConstraint)

      (domain.copy(decompositionMethods = extendedMethods, tasks = domain.tasks ++ newTasks :+ topTask, sasPlusRepresentation = newSasPlus), initialPlan)
    }
  }
}

object DecompositionMethodTransformer {
  private var instanceCounter = 0
}

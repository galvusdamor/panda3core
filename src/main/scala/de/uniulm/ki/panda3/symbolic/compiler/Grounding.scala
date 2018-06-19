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

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Equal, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep, GroundTask}
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Grounding extends DomainTransformer[(GroundedReachabilityAnalysis, Map[GroundTask, Int])] {


  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, _temp : (GroundedReachabilityAnalysis, Map[GroundTask, Int])): (Domain, Plan) = {
    val reachabilityAnalysis: GroundedReachabilityAnalysis = _temp._1
    val tasksWithMultipleGroundings : Map[GroundTask,Int] = _temp._2


    // ----- Predicates
    val groundedPredicates: Map[Predicate, Map[Seq[Constant], Predicate]] =
      ((reachabilityAnalysis.reachableGroundLiterals ++ reachabilityAnalysis.additionalLiteralsNeededToGround ++ plan.groundedInitialState) map {
        case GroundLiteral(predicate, _, parameter) =>
          GroundLiteral(predicate, isPositive = true, parameter) // make all literals positive, we just want to ground the predicates
      }).distinct groupBy { _.predicate } map {
        case (predicate, litList) =>
          val argumentMapping = litList groupBy { _.parameter } map { case (args, lits) =>
            assert(lits.length == 1)
            val onlyLit = lits.head
            val newPredicateName = onlyLit.predicate.name + ((onlyLit.parameter map { _.name }) mkString("[", ",", "]"))
            (args, Predicate(newPredicateName, Nil))
          }
          (predicate, argumentMapping)
      }
    val allGroundedPredicates = groundedPredicates flatMap { _._2.values }

    // ----- Tasks
    def groundTaskToGroundedTask(groundTask: GroundTask): Task = groundTask match {
      case g@GroundTask(ReducedTask(name, isPrimitive, _, _, _, _, _), constants) =>
        val newTaskName = name + ((constants map { _.name }) mkString("[", ",", "]"))
        // ground precondition and effect
        val preconditionLiterals = g.substitutedPreconditions map {
          case GroundLiteral(predicate, isPositive, parameter) =>
            Literal(groundedPredicates(predicate)(parameter), isPositive, Nil)
        } distinct

        val effectLiteralsUnfiltered: Set[Literal] = g.substitutedEffects map {
          case GroundLiteral(predicate, isPositive, parameter) => Literal(groundedPredicates(predicate)(parameter), isPositive, Nil)
        } toSet

        // remove effects that occur also in the preconditions
        val effectLiterals = effectLiteralsUnfiltered filterNot { l => l.isNegative && (effectLiteralsUnfiltered contains l.negate) }

        ReducedTask(newTaskName, isPrimitive, Nil, Nil, Nil, And(preconditionLiterals), And(effectLiterals.toSeq))
      case _                                                                      => noSupport(FORUMLASNOTSUPPORTED)
    }

    val alreadyGroundedVariableMapping = plan.variableConstraints.variables map { vari => (vari, plan.variableConstraints.getRepresentative(vari)) } collect {
      case (v, c: Constant) => (v, c)
    } toMap

    val allTasksAnalysis = reachabilityAnalysis.reachableGroundedTasks ++ reachabilityAnalysis.additionalTaskNeededToGround ++ reachabilityAnalysis.reachableGroundMethodInitAndGoalActions
    val initAndGoalInitialTask = GroundTask(plan.init.schema, plan.init.arguments map alreadyGroundedVariableMapping) ::
      GroundTask(plan.goal.schema, plan.goal.arguments map alreadyGroundedVariableMapping) :: Nil
    val groundedTasks: Map[Task, Map[Seq[Constant], Seq[(Task, GroundTask)]]] = ((allTasksAnalysis ++ initAndGoalInitialTask).distinct groupBy { _.task } map { case (t, groundActs) =>
      val taskMap: Map[Seq[Constant], Seq[(Task, GroundTask)]] = groundActs groupBy { _.arguments } map { case (args, taskList) =>
        assert(taskList.length == 1)
        // check whether we actually have to create multiple groundings (because of SAS+ which generates the same instantiation of an action with difference preconditions / effects)
        // this usually happens where the propositional model contained negative preconditions (like (not (at loc-2 truck-2)))
        val preliminaryGroundTask : GroundTask = taskList.head

        val actualGroundingsList = if (tasksWithMultipleGroundings.getOrElse(preliminaryGroundTask,1) == 1)
          (groundTaskToGroundedTask(preliminaryGroundTask), preliminaryGroundTask) :: Nil
        else {
          val preliminaryGroundedTask = groundTaskToGroundedTask(preliminaryGroundTask)

          Range(0, tasksWithMultipleGroundings(preliminaryGroundTask)) map { copyIndex =>
            val newGroundTask = preliminaryGroundTask.copy(task = preliminaryGroundTask.task.asInstanceOf[ReducedTask].copy(name = preliminaryGroundTask.task.name + "#" + copyIndex))
            val newGroundedTask = preliminaryGroundedTask.asInstanceOf[ReducedTask].copy(name = preliminaryGroundedTask.name + "#" + copyIndex)
            (newGroundedTask, newGroundTask)
          }
        }

        (args, actualGroundingsList)
      }
      (t, taskMap)
    }) ++ tasksWithMultipleGroundings.groupBy(_._1.task).map({ case (task,gts) =>
      // insert additional abstract tasks if needed for grounding, i.e, in the case where an abstract task has multiple groundings with the same parameters
      val newAbstractTask: Task = task.asInstanceOf[ReducedTask].copy(isPrimitive = false, name = task.name + "#abstract", precondition = And(Nil), effect = And(Nil))

      val instances : Map[Seq[Constant], Seq[(Task,GroundTask)]] = gts map { case (gt, _) =>
        val groundAbstractTask: GroundTask = gt.copy(task = newAbstractTask)
        val groundedAbstractTask: Task = groundTaskToGroundedTask(groundAbstractTask)
        gt.arguments -> Seq((groundedAbstractTask, groundAbstractTask))
      }

      (newAbstractTask,instances)
                                                                                })

    val additionalHiddenTasks = reachabilityAnalysis.additionalMethodsNeededToGround flatMap { _.decompositionMethod.subPlan.initAndGoal } map { _.schema } distinct
    val allGroundedTasks: Iterable[Task] = groundedTasks flatMap { _._2.values } flatMap { x => x } collect {
      case (task, groundTask) if !((domain.hiddenTasks ++ additionalHiddenTasks) contains groundTask.task) && !(initAndGoalInitialTask contains groundTask) => task
    }
    val groundingTaskBackMapping: Map[Task, GroundTask] = groundedTasks flatMap { _._2.values } flatMap { x => x } toMap


    // helper methods
    def groundPS(oldPS: PlanStep, mapVariable: Variable => Constant): PlanStep = groundedTasks(oldPS.schema)(oldPS.arguments map mapVariable) match {
      case singleGrounding if singleGrounding.size == 1 => PlanStep(oldPS.id, singleGrounding.head._1, Nil)
      case multipleGroundings     =>
        // construct equivalent abstract task for grounding and access it.
        val abstractReplacement = oldPS.schema.asInstanceOf[ReducedTask].copy(isPrimitive = false, name = oldPS.schema.name + "#abstract", precondition = And(Nil), effect = And(Nil))
        val groundedAbstract = groundedTasks(abstractReplacement)(oldPS.arguments map mapVariable).head._1
        PlanStep(oldPS.id, groundedAbstract, Nil)
    }


    def groundPlan(plan: Plan, mapVariable: Variable => Constant): Plan = {
      // check whether it is possible to ground the method
      plan.planSteps foreach { ps => assert(groundedTasks(ps.schema) contains (ps.arguments map mapVariable)) }

      // create the inner plan
      val groundedPlanStepMapping = (plan.planSteps map { ps => (ps, groundPS(ps, mapVariable)) }).toMap
      val actualGroundedPlansteps = groundedPlanStepMapping.values.toSeq

      val orderingConstraints = plan.orderingConstraints.originalOrderingConstraints map {
        case OrderingConstraint(before, after) => OrderingConstraint(groundedPlanStepMapping(before), groundedPlanStepMapping(after))
      }

      val causalLinks = plan.causalLinks map { case CausalLink(producer, consumer, condition) =>
        // get the correct ground Literal
        val groundCondition = condition match {
          case Literal(predicate, isPositive, parameterVariables) =>
            val parameterConstants = parameterVariables map mapVariable
            Literal(groundedPredicates(predicate)(parameterConstants), isPositive, Nil)
        }
        CausalLink(groundedPlanStepMapping(producer), groundedPlanStepMapping(consumer), groundCondition)
      }

      Plan(actualGroundedPlansteps, causalLinks, TaskOrdering(orderingConstraints, actualGroundedPlansteps), CSP(Set(), Nil),
           groundedPlanStepMapping(plan.init), groundedPlanStepMapping(plan.goal), plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())
    }


    // ----- Decomposition methods
    val groundedDecompositionMethods: Seq[DecompositionMethod] = (reachabilityAnalysis.reachableGroundMethods ++ reachabilityAnalysis.additionalMethodsNeededToGround map {
      case GroundedDecompositionMethod(liftedMethod, variableBinding) =>
        // ground the abstract actions
        val groundedAbstractTask = groundTaskToGroundedTask(GroundTask(liftedMethod.abstractTask, liftedMethod.abstractTask.parameters map variableBinding))
        SimpleDecompositionMethod(groundedAbstractTask, groundPlan(liftedMethod.subPlan, variableBinding), liftedMethod.name + (
          variableBinding map { case (v, c) => v.name + "=" + c.name }
          ).mkString("[", ",", "]"))
    }) ++ tasksWithMultipleGroundings.flatMap({ case (gt, numberOfCopies) =>
      val abstractReplacement = gt.task.asInstanceOf[ReducedTask].copy(isPrimitive = false, name = gt.task.name + "#abstract", precondition = And(Nil), effect = And(Nil))
      val abstractGround = groundedTasks(abstractReplacement)(gt.arguments).head._1

      // if argument is not contained in grounding done by TDG, this grounding is actually superflous
      val primitiveInstantiations = groundedTasks(gt.task).getOrElse(gt.arguments,Nil).map(_._1).sortBy(_.name.split("#").last.toInt) // sorts by index of instance

      Range(0, numberOfCopies).zip(primitiveInstantiations) map { case (copyIndex, primitiveInstanceTask) =>
        SimpleDecompositionMethod(abstractGround, Plan.sequentialPlan(primitiveInstanceTask :: Nil), abstractGround.name + "_multiple_instantiations#" + copyIndex)
      }
                                                                                    })

    // check whether we have to insert a new abstract task, as the initial plan might not be completely grounded


    val initialPlan = if (reachabilityAnalysis.additionalMethodsNeededToGround.isEmpty) {
      groundPlan(plan, alreadyGroundedVariableMapping)
    } else {
      // ground the plan containing the initial abstract task
      val topTask = reachabilityAnalysis.additionalMethodsNeededToGround.head.groundAbstractTask.task
      val topPS = PlanStep(2, topTask, topTask.parameters)
      val planSteps: Seq[PlanStep] = plan.init :: plan.goal :: topPS :: Nil
      val ordering = TaskOrdering(OrderingConstraint.allBetween(plan.init, plan.goal, topPS), planSteps)
      val topPlan = Plan(planSteps, Nil, ordering, plan.variableConstraints, plan.init, plan.goal, plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())

      groundPlan(topPlan, alreadyGroundedVariableMapping)
    }


    // TODO handle decomposition axioms ?!?
    assert(domain.decompositionAxioms.isEmpty)
    (Domain(Nil, allGroundedPredicates.toSeq, allGroundedTasks.toSeq, groundedDecompositionMethods, Nil, Some(GroundedDomainToDomainMapping(groundingTaskBackMapping)), None), initialPlan)
  }

}

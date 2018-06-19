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

package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.modification.InsertPlanStepWithLink
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.util._

import scala.annotation.elidable
import scala.annotation.elidable._


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait TaskDecompositionGraph extends GroundedReachabilityAnalysis with WithTopMethod with DotPrintable[DirectedGraphDotOptions] {

  def domain: Domain

  def initialPlan: Plan

  def groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis

  def prunePrimitive: Boolean

  def abstractTaskGroundings: Map[Task, Set[GroundTask]]

  def groundedDecompositionMethods: Map[GroundTask, Set[GroundedDecompositionMethod]]

  val isInitialPlanGround = initialPlan.variableConstraints.variables forall { v => initialPlan.variableConstraints.getRepresentative(v).isInstanceOf[Constant] }

  def messageFunction: String => Unit

  lazy val taskDecompositionGraph: (AndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod], Seq[GroundTask], Seq[GroundedDecompositionMethod]) =
    if (!(abstractTaskGroundings contains topTask)) {
      messageFunction("Initial Plan cannot be decomposed into a primitive plan ... generating trivially unsolvable problem")
      val emptyInit = GroundTask(initAndGoalNOOP, Nil)
      val abstractWithOutDecomposition = ReducedTask("__fail_abstract", isPrimitive = false, Nil, Nil, Nil, And[Literal](Nil), And[Literal](Nil))
      val abstractGT = GroundTask(abstractWithOutDecomposition, Nil)
      val abstractPlanTemp = Plan(PlanStep(3, abstractWithOutDecomposition, Nil) :: Nil, initAndGoalNOOP, initAndGoalNOOP,
                                  Map[PlanStep, DecompositionMethod](), Map[PlanStep, (PlanStep, PlanStep)]())
      val abstractPlan = abstractPlanTemp.copy(parameterVariableConstraints = abstractPlanTemp.variableConstraints.addVariables(topTask.parameters))
      val topToAbstract = SimpleDecompositionMethod(topTask, abstractPlan, "useless method")
      val topGroundMethod = GroundedDecompositionMethod(topToAbstract, groundedTopTask.task.parameters.zip(groundedTopTask.arguments).toMap)


      val emptyTDG = SimpleAndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod](Set(abstractGT), Set(),
                                                                                       Map((abstractGT, Set())),
                                                                                       Map())


      (emptyTDG, abstractGT :: emptyInit :: groundedTopTask :: Nil, topGroundMethod :: Nil)
    } else {
      assert(abstractTaskGroundings(topTask).size == 1)
      val topGrounded = abstractTaskGroundings(topTask).head

      /**
        * @return abstract tasks and methods that remain in the pruning
        */
      def pruneMethodsAndTasksIfPossible(remainingGroundTasks: Set[GroundTask], remainingGroundMethods: Set[GroundedDecompositionMethod], firstRound: Boolean = false):
      (Set[GroundTask], Set[GroundedDecompositionMethod]) = {
        // test all decomposition methods
        val stillSupportedMethods: Set[GroundedDecompositionMethod] = remainingGroundMethods filter { _.subPlanGroundedTasksWithoutInitAndGoal forall { remainingGroundTasks.contains } }

        // in the first round we might also have to prune abstract tasks without methods being unsupported
        if (!firstRound && stillSupportedMethods.size == remainingGroundMethods.size) {
          // nothing to prune
          (remainingGroundTasks, remainingGroundMethods)
        } else {
          // find all supported abstract tasks
          val stillSupportedAbstractGroundTasks: Set[GroundTask] = stillSupportedMethods map { _.groundAbstractTask }
          val stillSupportedPrimitiveGroundTasks: Set[GroundTask] =
            if (prunePrimitive) stillSupportedMethods flatMap { _.subPlanGroundedTasksWithoutInitAndGoal filter { _.task.isPrimitive } } else remainingGroundTasks filter { _.task
              .isPrimitive
            }

          val stillSupportedTasks = stillSupportedAbstractGroundTasks ++ stillSupportedPrimitiveGroundTasks

          if (stillSupportedTasks.size == remainingGroundTasks.size)
            (remainingGroundTasks, stillSupportedMethods) // no tasks have been pruned so stop
          else pruneMethodsAndTasksIfPossible(stillSupportedTasks, stillSupportedMethods)
        }
      }

      val allGroundedActions: Set[GroundTask] = (abstractTaskGroundings.values.flatten ++ groundedReachabilityAnalysis.reachableGroundPrimitiveActions).toSet
      val (remainingGroundTasks, remainingGroundMethods) = pruneMethodsAndTasksIfPossible(allGroundedActions, groundedDecompositionMethods.values.flatten.toSet, firstRound = true)

      val alwaysNecessaryPrimitiveTasks =
        if (initialPlan.isModificationAllowed(InsertPlanStepWithLink(null, null, null, null))) groundedReachabilityAnalysis.reachableGroundPrimitiveActions else Nil

      //println((remainingGroundTasks groupBy { _.task } map { case (t, gts) => t.name + ": " + gts.size }).toSeq.sorted mkString "\n")

      val prunedTaskToMethodEdgesMaybeIncomplete = groundedDecompositionMethods collect { case (a, b) if remainingGroundTasks contains a => (a, b.toSet intersect remainingGroundMethods) }
      val notMappedTasks = (remainingGroundTasks ++ alwaysNecessaryPrimitiveTasks) diff prunedTaskToMethodEdgesMaybeIncomplete.keySet
      val prunedTaskToMethodEdges = (prunedTaskToMethodEdgesMaybeIncomplete) ++ (notMappedTasks map { _ -> Set[GroundedDecompositionMethod]() })
      val prunedMethodToTaskEdges = remainingGroundMethods map { case m => (m, m.subPlanGroundedTasksWithoutInitAndGoal.toSet) }
      val firstAndOrGraph = SimpleAndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod](remainingGroundTasks ++ alwaysNecessaryPrimitiveTasks, remainingGroundMethods,
                                                                                              prunedTaskToMethodEdges, prunedMethodToTaskEdges.toMap)
      // reachability analysis
      //System.in.read()
      val nonEmptyTDG = firstAndOrGraph.andVertices contains topGrounded
      val allReachable = if (nonEmptyTDG) firstAndOrGraph.reachableFrom(topGrounded) ++ alwaysNecessaryPrimitiveTasks else Set[AnyRef](topGrounded)

      val reachableWithoutTop = allReachable partition {
        case GroundedDecompositionMethod(m, _) => m.abstractTask == topTask
        case GroundTask(task, _)               => task == topTask
      }

      val topMethods = reachableWithoutTop._1 collect { case x: GroundedDecompositionMethod => x }

      val prunedTDG = firstAndOrGraph pruneToEntities reachableWithoutTop._2

      //Dot2PdfCompiler.writeDotToFile(prunedTDG,"tdg.pdf")

      if (!nonEmptyTDG) messageFunction("TDG contains no tasks after pruning ... problem is trivially unsolvable")

      (prunedTDG, if (isInitialPlanGround && nonEmptyTDG) Nil else topGrounded :: GroundTask(initAndGoalNOOP, Nil) :: Nil, if (isInitialPlanGround || !nonEmptyTDG) Nil else topMethods.toSeq)
    }

  override lazy val reachableGroundedTasks         : Seq[GroundTask]                  = taskDecompositionGraph._1.andVertices.toSeq
  override lazy val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = taskDecompositionGraph._1.orVertices.toSeq
  override lazy val reachableGroundLiterals        : Seq[GroundLiteral]               = groundedReachabilityAnalysis.reachableGroundLiterals
  override      val additionalTaskNeededToGround   : Seq[GroundTask]                  = taskDecompositionGraph._2 :+ initialPlan.groundedGoalTask
  override      val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = taskDecompositionGraph._3

  @elidable(ASSERTION)
  val assertion = {
    reachableGroundPrimitiveActions foreach { gt =>
      gt.substitutedEffects foreach { e => assert(reachableGroundLiterals contains e, "action " + gt.longInfo + " has the non reachable effect " + e.longInfo) }
      gt.substitutedPreconditions foreach { e => assert(reachableGroundLiterals contains e, "action " + gt.longInfo + " has the non reachable precondition " + e.longInfo) }
    }
    reachableGroundAbstractActions foreach { gt =>
      gt.substitutedEffects foreach { e => assert(reachableGroundLiterals contains e, "action " + gt.longInfo + " has the non reachable effect " + e.longInfo) }
      gt.substitutedPreconditions foreach { e => assert(reachableGroundLiterals contains e, "action " + gt.longInfo + " has the non reachable precondition " + e.longInfo) }
    }
  }

  override lazy val dotString: String = dotString(DirectedGraphDotOptions())

  /** The DOT representation of the object with options */
  override def dotString(options: DirectedGraphDotOptions): String = taskDecompositionGraph._1.dotString(options)
}

trait WithTopMethod {

  def domain: Domain

  def initialPlan: Plan

  lazy val (topTask,topMethod,initAndGoalNOOP,groundedTopTask) = {
    val initialPlanAlreadyGroundedVariableMapping = initialPlan.variableConstraints.variables map { vari => (vari, initialPlan.variableConstraints.getRepresentative(vari)) } collect {
      case (v, c: Constant) => (v, c)
    } toMap

    // just to be safe, we create a new initial abstract task, and ensure that it is fully grounded
    // create a new virtual abstract task
    assert(initialPlan.init.schema.isInstanceOf[ReducedTask])
    assert(initialPlan.goal.schema.isInstanceOf[ReducedTask])

    // TODO we cant handle this case (yet)
    assert(!(initialPlan.causalLinks exists { _.containsOne(initialPlan.initAndGoal: _*) }))

    val noop = ReducedTask("__noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))
    val topInit = PlanStep(initialPlan.init.id, noop, Nil)
    val topGoal = PlanStep(initialPlan.goal.id, noop, Nil)

    val topPlanTasks = initialPlan.planStepsAndRemovedPlanStepsWithoutInitGoal :+ topInit :+ topGoal
    val initialPlanInternalOrderings = initialPlan.orderingConstraints.originalOrderingConstraints filterNot { _.containsAny(initialPlan.initAndGoal: _*) }
    val topOrdering = TaskOrdering(initialPlanInternalOrderings ++ OrderingConstraint.allBetween(topInit, topGoal, initialPlan.planStepsAndRemovedPlanStepsWithoutInitGoal: _*), topPlanTasks)
    val initialPlanWithout = Plan(topPlanTasks, initialPlan.causalLinksAndRemovedCausalLinks, topOrdering, initialPlan.variableConstraints, topInit, topGoal,
                                  initialPlan.isModificationAllowed,
                                  initialPlan.isFlawAllowed, initialPlan.planStepDecomposedByMethod, initialPlan.planStepParentInDecompositionTree)

    // create an artificial method
    val createdTopTask = ReducedTask("__grounding__top", isPrimitive = false, initialPlanAlreadyGroundedVariableMapping.keys.toSeq, Nil, Nil, And(Nil), And(Nil))
    val createdTopMethod = SimpleDecompositionMethod(createdTopTask, initialPlanWithout, "__top")
    val groundedTop = GroundTask(createdTopTask, createdTopTask.parameters map initialPlanAlreadyGroundedVariableMapping)

    (createdTopTask, createdTopMethod, noop, groundedTop)
  }
}

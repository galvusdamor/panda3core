package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{ReachabilityAnalysis, GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic.{GroundLiteral, Constant, Sort, Variable}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.util.{AndOrGraph, SimpleAndOrGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class NaiveGroundedTaskDecompositionGraph(domain: Domain, initialPlan: Plan, groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis, prunePrimitive: Boolean) extends
  GroundedReachabilityAnalysis {

  lazy val taskDecompositionGraph: (AndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod], Seq[GroundTask], Seq[GroundedDecompositionMethod]) = {
    val isInitialPlanGround = initialPlan.variableConstraints.variables forall { v => initialPlan.variableConstraints.getRepresentative(v).isInstanceOf[Constant] }
    val alreadyGroundedVariableMapping = initialPlan.variableConstraints.variables map { vari => (vari, initialPlan.variableConstraints.getRepresentative(vari)) } collect {
      case (v, c: Constant) => (v, c)
    } toMap

    // just to be safe, we create a new initial abstract task, and ensure that it is fully grounded
    // create a new virtual abstract task
    assert(initialPlan.init.schema.isInstanceOf[ReducedTask])
    assert(initialPlan.goal.schema.isInstanceOf[ReducedTask])
    val initSchema = initialPlan.init.schema.asInstanceOf[ReducedTask]
    val goalSchema = initialPlan.goal.schema.asInstanceOf[ReducedTask]

    // create an artificial method
    val topTask = ReducedTask("__grounding__top", isPrimitive = false, alreadyGroundedVariableMapping.keys.toSeq, Nil, Nil, initSchema.effect, goalSchema.precondition)
    val topMethod = SimpleDecompositionMethod(topTask, initialPlan, "__top")

    // compute groundings of abstract tasks naively
    val abstractTaskGroundings: Map[Task, Set[GroundTask]] = (domain.abstractTasks map { abstractTask =>
      val groundedTasks = (Sort.allPossibleInstantiations(abstractTask.parameters map { _.sort }) filter abstractTask.areParametersAllowed map { GroundTask(abstractTask, _) }).toSet
      (abstractTask, groundedTasks)
    }).toMap + (topTask -> Set(GroundTask(topTask, topTask.parameters map alreadyGroundedVariableMapping)))

    assert(abstractTaskGroundings(topTask).size == 1)
    val topGrounded = abstractTaskGroundings(topTask).head

    // ground all methods naively
    val groundedDecompositionMethods: Map[GroundTask, Seq[GroundedDecompositionMethod]] = domain.decompositionMethods :+ topMethod flatMap {
      case method@SimpleDecompositionMethod(abstractTask, subPlan,_) =>
        abstractTaskGroundings(abstractTask) map { x => (x, method.groundWithAbstractTaskGrounding(x)) }
      case _                                                       => noSupport(NONSIMPLEMETHOD)
    } groupBy { _._1 } map { case (gt, s) => (gt, s flatMap { _._2 }) }

    /**
      * @return abstract tasks and methods that remain in the pruning
      */
    def pruneMethodsAndTasksIfPossible(remainingGroundTasks: Set[GroundTask], remainingGroundMethods: Set[GroundedDecompositionMethod]):
    (Set[GroundTask], Set[GroundedDecompositionMethod]) = {
      // test all decomposition methods
      val stillSupportedMethods: Set[GroundedDecompositionMethod] = remainingGroundMethods filter { _.subPlanGroundedTasksWithoutInitAndGoal forall { remainingGroundTasks.contains } }


      if (stillSupportedMethods.size == remainingGroundMethods.size) {
        // nothing to prune
        (remainingGroundTasks, remainingGroundMethods)
      } else {
        // find all supported abstract tasks
        val stillSupportedAbstractGroundTasks: Set[GroundTask] = stillSupportedMethods map { _.groundAbstractTask }
        val stillSupportedPrimitiveGroundTasks: Set[GroundTask] =
          if (prunePrimitive) stillSupportedMethods flatMap { _.subPlanGroundedTasksWithoutInitAndGoal filter { _.task.isPrimitive } } else remainingGroundTasks filter { _.task.isPrimitive }

        if (stillSupportedAbstractGroundTasks.size + stillSupportedPrimitiveGroundTasks.size == remainingGroundTasks.size)
          (remainingGroundTasks, stillSupportedMethods) // no tasks have been pruned so stop
        else pruneMethodsAndTasksIfPossible(stillSupportedAbstractGroundTasks ++ stillSupportedPrimitiveGroundTasks, stillSupportedMethods)
      }
    }

    val allGroundedActions: Set[GroundTask] = (abstractTaskGroundings.values.flatten ++ groundedReachabilityAnalysis.reachableGroundPrimitiveActions).toSet
    val (remainingGroundTasks, remainingGroundMethods) = pruneMethodsAndTasksIfPossible(allGroundedActions, groundedDecompositionMethods.values.flatten.toSet)

    val prunedTaskToMethodEdgesMaybeIncomplete = groundedDecompositionMethods collect { case (a, b) if remainingGroundTasks contains a => (a, b.toSet intersect remainingGroundMethods) }
    val notMappedTasks = remainingGroundTasks diff prunedTaskToMethodEdgesMaybeIncomplete.keySet
    val prunedTaskToMethodEdges = prunedTaskToMethodEdgesMaybeIncomplete ++ (notMappedTasks map { _ -> Set[GroundedDecompositionMethod]() })
    val prunedMethodToTaskEdges = remainingGroundMethods map { case m => (m, m.subPlanGroundedTasksWithoutInitAndGoal.toSet) }
    val firstAndOrGraph = SimpleAndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod](remainingGroundTasks, remainingGroundMethods, prunedTaskToMethodEdges,
                                                                                            prunedMethodToTaskEdges.toMap)
    // reachability analysis
    //System.in.read()
    val allReachable = firstAndOrGraph.reachable(topGrounded)
    val rechableWithoutTop = allReachable partition {
      case GroundedDecompositionMethod(m, _) => m.abstractTask == topTask
      case GroundTask(task, _)               => task == topTask
    }

    val topMethods = rechableWithoutTop._1 collect { case x: GroundedDecompositionMethod => x }

    (firstAndOrGraph pruneToEntities rechableWithoutTop._2, if (isInitialPlanGround) Nil else (topGrounded :: Nil), if (isInitialPlanGround) Nil else topMethods.toSeq)
  }

  override lazy val reachableGroundedTasks         : Seq[GroundTask]                  = taskDecompositionGraph._1.andVertices.toSeq
  override lazy val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = taskDecompositionGraph._1.orVertices.toSeq
  override lazy val reachableGroundLiterals        : Seq[GroundLiteral]               = groundedReachabilityAnalysis.reachableGroundLiterals
  override      val additionalTaskNeededToGround   : Seq[GroundTask]                  = taskDecompositionGraph._2 :+ initialPlan.groundedGoalTask
  override      val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = taskDecompositionGraph._3
}
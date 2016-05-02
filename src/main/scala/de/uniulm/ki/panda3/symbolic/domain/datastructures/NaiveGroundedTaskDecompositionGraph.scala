package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Variable, Sort, Constant}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{PlanStep, GroundTask}
import de.uniulm.ki.util.{SimpleAndOrGraph, AndOrGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class NaiveGroundedTaskDecompositionGraph(domain: Domain, initialPlan: Plan, groundedReachabilityAnalysis: GroundedReachabilityAnalysis, prunePrimitive: Boolean) {

  lazy val taskDecompositionGraph: AndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod] = {
    // compute groundings of abstract tasks naively
    val abstractTaskGroundings: Map[Task, Set[GroundTask]] = (domain.abstractTasks map { abstractTask =>
      val groundedTasks = (Sort.allPossibleInstantiations(abstractTask.parameters map { _.sort }) filter abstractTask.areParametersAllowed map { GroundTask(abstractTask, _) }).toSet
      (abstractTask, groundedTasks)
    }).toMap

    // ground all methods naively
    val groundedDecompositionMethods: Map[GroundTask, Seq[GroundedDecompositionMethod]] = domain.decompositionMethods flatMap {
      case method@SimpleDecompositionMethod(abstractTask, subPlan) =>
        abstractTaskGroundings(abstractTask) map {
          case groundTask =>
            val bindArguments = groundTask.task.parameters zip groundTask.arguments map { case (v, c) => Equal(v, c) }
            val boundCSP = subPlan.variableConstraints.addConstraints(bindArguments)
            val unboundVariables = boundCSP.variables filter { v => boundCSP.getRepresentative(v) match {
              case c: Constant    => false
              case repV: Variable => repV == v
            }
            }
            // try to bind all variables to their
            val unboundVariablesWithRemainingValues: Seq[(Variable, Seq[Constant])] = (unboundVariables map { v => (v, boundCSP.reducedDomainOf(v)) }).toSeq
            val allInstantiations = Sort allPossibleInstantiationsWithVariables unboundVariablesWithRemainingValues

            val methodInstantiations: Seq[Map[Variable, Constant]] = allInstantiations map { instantiation =>
              val additionalConstraints = instantiation map { case (v, c) => Equal(v, c) }
              val innerCSP = boundCSP addConstraints additionalConstraints
              if (innerCSP.isSolvable contains false) None
              else Some((innerCSP.variables map { v => v -> innerCSP.getRepresentative(v).asInstanceOf[Constant] }).toMap)
            } filter { _.isDefined } map { _.get }

            (groundTask, methodInstantiations map { args => GroundedDecompositionMethod(method, args) })
        }
      case _                                                       => noSupport(NONSIMPLEMETHOD)
    } groupBy { _._1 } map { case (gt, s) => (gt, s flatMap { _._2 }) }



    /**
      * @return abstract tasks and methods that remain in the pruning
      */
    def pruneMethodsAndTasksIfPossible(remainingGroundTasks: Set[GroundTask], remainingGroundMethods: Set[GroundedDecompositionMethod]):
    (Set[GroundTask], Set[GroundedDecompositionMethod]) = {
      // test all decomposition methods
      val stillSupportedMethods: Set[GroundedDecompositionMethod] = remainingGroundMethods filter { _.subPlanGroundedTasksWithoutInitAndGoal forall { remainingGroundTasks.contains } }


      if (stillSupportedMethods.size == remainingGroundMethods.size) // nothing to prune
        (remainingGroundTasks, remainingGroundMethods)
      else {
        // find all supported abstract tasks
        val stillSupportedAbstractGroundTasks: Set[GroundTask] = stillSupportedMethods map { _.groundAbstractTask }
        val stillSupportedPrimitiveGroundTasks: Set[GroundTask] =
          if (prunePrimitive) stillSupportedMethods flatMap { _.subPlanGroundedTasksWithoutInitAndGoal filter { _.task.isPrimitive } } else remainingGroundTasks filter { _.task.isPrimitive }

        if (stillSupportedAbstractGroundTasks.size + stillSupportedPrimitiveGroundTasks.size == remainingGroundTasks.size)
          (remainingGroundTasks, stillSupportedMethods) // no tasks have been pruned so stop
        else pruneMethodsAndTasksIfPossible(stillSupportedAbstractGroundTasks ++ stillSupportedPrimitiveGroundTasks, stillSupportedMethods)
      }
    }

    val allGroundedActions: Set[GroundTask] = (abstractTaskGroundings.values.flatten ++ groundedReachabilityAnalysis.reachableGroundActions).toSet
    val (remainingGroundTasks, remainingGroundMethods) = pruneMethodsAndTasksIfPossible(allGroundedActions, groundedDecompositionMethods.values.flatten.toSet)

    val prunedTaskToMethodEdges = groundedDecompositionMethods collect { case (a, b) if remainingGroundTasks contains a => (a, b.toSet intersect remainingGroundMethods) }
    val prunedMethodToTaskEdges = remainingGroundMethods map { case m => (m, m.subPlanGroundedTasksWithoutInitAndGoal.toSet) }
    val firstAndOrGraph = SimpleAndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod](remainingGroundTasks, remainingGroundMethods, prunedTaskToMethodEdges,
                                                                                            prunedMethodToTaskEdges.toMap)

    // rechability analysis
    // TODO handle the case where the initial plan contains variable, e.g. by introducing a new method
    initialPlan.variableConstraints.variables foreach { v => assert(initialPlan.variableConstraints.getRepresentative(v).isInstanceOf[Constant]) }
    val initialPlanGrounding = initialPlan.planStepsWithoutInitGoal map { case PlanStep(_, schema, arguments) =>
      val argumentConstants = arguments map { initialPlan.variableConstraints.getRepresentative(_).asInstanceOf[Constant] }
      GroundTask(schema, argumentConstants)
    }
    val reach = firstAndOrGraph.reachable
    val reachableEntities = initialPlanGrounding flatMap { groundT => firstAndOrGraph.reachable(groundT) }
    firstAndOrGraph pruneToEntities reachableEntities
  }

  lazy val reachableGroundedMethods       : Seq[GroundedDecompositionMethod] = taskDecompositionGraph.orVertices.toSeq
  lazy val reachableGroundedTasks         : Seq[GroundTask]                  = taskDecompositionGraph.andVertices.toSeq
  lazy val reachableGroundedAbstractTasks : Seq[GroundTask]                  = reachableGroundedTasks filter { _.task.isAbstract }
  lazy val reachableGroundedPrimitiveTasks: Seq[GroundTask]                  = reachableGroundedTasks filter { _.task.isPrimitive }
}
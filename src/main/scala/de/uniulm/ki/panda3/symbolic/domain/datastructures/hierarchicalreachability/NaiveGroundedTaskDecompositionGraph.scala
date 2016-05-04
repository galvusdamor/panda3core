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
    val topTask = ReducedTask("__grounding__top", isPrimitive = false, alreadyGroundedVariableMapping.keys.toSeq, Nil, initSchema.effect, goalSchema.precondition)
    val topMethod = SimpleDecompositionMethod(topTask, initialPlan)

    // compute groundings of abstract tasks naively
    val abstractTaskGroundings: Map[Task, Set[GroundTask]] = (domain.abstractTasks map { abstractTask =>
      val groundedTasks = (Sort.allPossibleInstantiations(abstractTask.parameters map { _.sort }) filter abstractTask.areParametersAllowed map { GroundTask(abstractTask, _) }).toSet
      (abstractTask, groundedTasks)
    }).toMap + (topTask -> Set(GroundTask(topTask, topTask.parameters map alreadyGroundedVariableMapping)))

    println(abstractTaskGroundings(topTask).size)
    assert(abstractTaskGroundings(topTask).size == 1)
    val topGrounded = abstractTaskGroundings(topTask).head

    // ground all methods naively
    val groundedDecompositionMethods: Map[GroundTask, Seq[GroundedDecompositionMethod]] = domain.decompositionMethods :+ topMethod flatMap {
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

    val allGroundedActions: Set[GroundTask] = (abstractTaskGroundings.values.flatten ++ groundedReachabilityAnalysis.reachableGroundPrimitiveActions).toSet
    val (remainingGroundTasks, remainingGroundMethods) = pruneMethodsAndTasksIfPossible(allGroundedActions, groundedDecompositionMethods.values.flatten.toSet)

    val prunedTaskToMethodEdges = groundedDecompositionMethods collect { case (a, b) if remainingGroundTasks contains a => (a, b.toSet intersect remainingGroundMethods) }
    val prunedMethodToTaskEdges = remainingGroundMethods map { case m => (m, m.subPlanGroundedTasksWithoutInitAndGoal.toSet) }
    val firstAndOrGraph = SimpleAndOrGraph[AnyRef, GroundTask, GroundedDecompositionMethod](remainingGroundTasks, remainingGroundMethods, prunedTaskToMethodEdges,
                                                                                            prunedMethodToTaskEdges.toMap)
    // reachability analysis
    val allReachable = firstAndOrGraph.reachable(topGrounded)
    val rechableWithoutTop = allReachable partition {
      case GroundedDecompositionMethod(m, _) => m.abstractTask == topTask
      case GroundTask(task, _)               => task == topTask
    }

    val topMethods = rechableWithoutTop._1 collect { case x: GroundedDecompositionMethod => x }

    (firstAndOrGraph pruneToEntities rechableWithoutTop._2, if (isInitialPlanGround) Nil else (topGrounded :: Nil), if (isInitialPlanGround) Nil else topMethods)
  }

  override lazy val reachableGroundedTasks         : Seq[GroundTask]                  = taskDecompositionGraph._1.andVertices.toSeq
  override lazy val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = taskDecompositionGraph._1.orVertices.toSeq
  override lazy val reachableGroundLiterals        : Seq[GroundLiteral]               = groundedReachabilityAnalysis.reachableGroundLiterals
  override      val additionalTaskNeededToGround   : Seq[GroundTask]                  = taskDecompositionGraph._2
  override      val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = taskDecompositionGraph._3
}
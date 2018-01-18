package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{ReachabilityAnalysis, GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, GroundTask, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.util.{AndOrGraph, SimpleAndOrGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class NaiveGroundedTaskDecompositionGraph(domain: Domain, initialPlan: Plan, groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis, prunePrimitive: Boolean,
                                               messageFunction : String => Unit) extends  TaskDecompositionGraph {

  // compute groundings of abstract tasks naively
  lazy val abstractTaskGroundings: Map[Task, Set[GroundTask]] = {
    val primitiveReachabilityAnalysisReachableLiterals = groundedReachabilityAnalysis.reachableGroundLiterals.toSet
    (domain.abstractTasks map { abstractTask =>
      val groundedTasks = (Sort.allPossibleInstantiations(abstractTask.parameters map { _.sort }) filter abstractTask.areParametersAllowed map { GroundTask(abstractTask, _) }).toSet
      val reachableGroundedTasks = groundedTasks filter { gt => (gt.substitutedPreconditionsSet ++ gt.substitutedEffects) subsetOf primitiveReachabilityAnalysisReachableLiterals }
      (abstractTask, reachableGroundedTasks)
    }).toMap + (topTask -> Set(groundedTopTask))
  }


  // ground all methods naively
  lazy val groundedDecompositionMethods: Map[GroundTask, Set[GroundedDecompositionMethod]] = domain.decompositionMethods :+ topMethod flatMap {
    case method@SimpleDecompositionMethod(abstractTask, subPlan, _) =>
      abstractTaskGroundings(abstractTask) map { x => (x, method.groundWithAbstractTaskGrounding(x)) }
    case _                                                          => noSupport(NONSIMPLEMETHOD)
  } groupBy { _._1 } map { case (gt, s) => (gt, s flatMap { _._2 } toSet) }
}
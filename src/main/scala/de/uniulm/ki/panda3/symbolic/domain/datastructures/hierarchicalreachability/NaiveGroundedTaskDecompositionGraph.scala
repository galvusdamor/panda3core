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

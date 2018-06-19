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
import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, GroundedDecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedReachabilityAnalysis, GroundedPrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic.{Constant, GroundLiteral}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EverythingIsHiearchicallyReachable(domain: Domain, initialPlan: Plan) extends GroundedReachabilityAnalysis {
  // TODO only provide applicable tasks
  private val allgroundings: Seq[GroundTask] = domain.allGroundedPrimitiveTasks ++ domain.allGroundedAbstractTasks

  override val reachableGroundLiterals     : Seq[GroundLiteral] = (allgroundings flatMap { _.substitutedEffects.toSet }) ++ initialPlan.groundedInitialState
  private  val reachableGroundLiteralsAsSet: Set[GroundLiteral] = reachableGroundLiterals.toSet
  override val reachableGroundedTasks      : Seq[GroundTask]    = allgroundings filter { _.substitutedPreconditionsSet subsetOf reachableGroundLiteralsAsSet }

  override val additionalTaskNeededToGround   : Seq[GroundTask]                  = Nil
  override val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = domain.decompositionMethods flatMap {
    case method@SimpleDecompositionMethod(abstractTask, _, _) => reachableGroundedTasks filter { _.task == abstractTask } flatMap method.groundWithAbstractTaskGrounding filter {
      _.subPlanGroundedTasksWithoutInitAndGoal forall reachableGroundedTasks.contains
    }
    case _                                                    => noSupport(NONSIMPLEMETHOD)

  }
  override val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = Nil
}


case class EverythingIsHiearchicallyReachableBasedOnPrimitiveReachability(domain: Domain, initialPlan: Plan, primitiveReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis)
  extends GroundedReachabilityAnalysis {
  override val reachableGroundedTasks         : Seq[GroundTask]                  = primitiveReachabilityAnalysis.reachableGroundPrimitiveActions ++ domain.allGroundedAbstractTasks
  override val additionalTaskNeededToGround   : Seq[GroundTask]                  = initialPlan.groundedGoalTask :: Nil
  // TODO this is not correct!!
  override val reachableGroundMethods         : Seq[GroundedDecompositionMethod] = domain.decompositionMethods flatMap {
    case method@SimpleDecompositionMethod(abstractTask, _, _) => reachableGroundedTasks filter { _.task == abstractTask } flatMap method.groundWithAbstractTaskGrounding filter {
      _.subPlanGroundedTasksWithoutInitAndGoal forall reachableGroundedTasks.contains
    }
    case _                                                    => noSupport(NONSIMPLEMETHOD)

  }
  override val additionalMethodsNeededToGround: Seq[GroundedDecompositionMethod] = Nil
  override val reachableGroundLiterals        : Seq[GroundLiteral]               =
    ((reachableGroundedTasks flatMap { _.substitutedEffects.toSet }) ++ initialPlan.groundedInitialState) distinct
}

package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.LayeredGroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EverythingIsReachable(domain: Domain, initialState: Set[GroundLiteral]) extends
  LayeredGroundedPrimitiveReachabilityAnalysis {

  override protected val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {
    val allGroundTasks = domain.allGroundedPrimitiveTasks.toSet
    val reachableLiterals = (allGroundTasks flatMap { t => t.substitutedPreconditionsSet ++ t.substitutedEffects }) ++ initialState
    (allGroundTasks, reachableLiterals) :: Nil
  }
}
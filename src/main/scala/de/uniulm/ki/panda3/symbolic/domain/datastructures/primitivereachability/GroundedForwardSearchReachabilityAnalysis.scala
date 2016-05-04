package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{LayeredGroundedPrimitiveReachabilityAnalysis, GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * A very simple, mutex relaxed reachability analysis
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GroundedForwardSearchReachabilityAnalysis(domain: Domain, initialState: Set[GroundLiteral])(allowedGroundings: Seq[GroundTask] = domain.allGroundedPrimitiveTasks) extends
  LayeredGroundedPrimitiveReachabilityAnalysis {

  protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {
    // function to build a single layer
    def buildLayer(state: Set[GroundLiteral]): (Set[GroundTask], Set[GroundLiteral]) = {
      val applicableGroundActions = allowedGroundings filter { _.substitutedPreconditionsSet subsetOf state }
      val resultingState = state ++ (applicableGroundActions flatMap { _.substitutedEffects })
      (applicableGroundActions.toSet, resultingState)
    }

    // function to compute all layers
    def iterateLayer(state: Set[GroundLiteral]): Seq[(Set[GroundTask], Set[GroundLiteral])] = {
      // build the next layer
      val nextLayer = buildLayer(state)
      if (nextLayer._2.size == state.size) {
        nextLayer :: Nil
      } else {
        val nextLayers = iterateLayer(nextLayer._2)
        nextLayer +: nextLayers
      }
    }
    iterateLayer(initialState)
  }
}
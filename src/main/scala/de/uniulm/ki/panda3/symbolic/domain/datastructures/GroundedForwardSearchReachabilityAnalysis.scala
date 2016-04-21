package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * A very simple, delete relaxed reachability analysis
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GroundedForwardSearchReachabilityAnalysis(domain: Domain, initialState: Set[GroundLiteral]) extends GroundedReachabilityAnalysis {

  protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {
    // function to build a single layer
    def buildLayer(state: Set[GroundLiteral]): (Set[GroundTask], Set[GroundLiteral]) = {
      val applicableGroundActions = domain.allGroundedPrimitiveTasks filter { _.substitutedPreconditions.toSet subsetOf state }
      val resultingState = state ++ (applicableGroundActions flatMap { _.substitutedEffects })
      (applicableGroundActions.toSet, resultingState)
    }

    // function to compute all layers
    def iterateLayer(initialState: Set[GroundLiteral]): Seq[(Set[GroundTask], Set[GroundLiteral])] = {
      // build the next layer
      val nextLayer = buildLayer(initialState)
      if (nextLayer._2.size == initialState.size) Nil
      else {
        val nextLayers = iterateLayer(nextLayer._2)
        nextLayer +: nextLayers
      }
    }
    iterateLayer(initialState)
  }
}
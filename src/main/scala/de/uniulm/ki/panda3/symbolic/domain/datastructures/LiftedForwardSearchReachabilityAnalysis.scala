package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LiftedForwardSearchReachabilityAnalysis(domain: Domain, initialState: Set[(Predicate, Boolean)]) extends LiftedReachabilityAnalysis {

  override protected val layer: Seq[(Set[Task], Set[(Predicate, Boolean)])] = {
    // function to build a single layer
    def buildLayer(state: Set[(Predicate, Boolean)]): (Set[Task], Set[(Predicate, Boolean)]) = {
      val applicableGroundActions = domain.primitiveTasks filter { _.preconditionsAsPredicateBool.toSet subsetOf state }
      val resultingState = state ++ (applicableGroundActions flatMap { _.effectsAsPredicateBool })
      (applicableGroundActions.toSet, resultingState)
    }

    // function to compute all layers
    def iterateLayer(state: Set[(Predicate, Boolean)]): Seq[(Set[Task], Set[(Predicate, Boolean)])] = {
      // build the next layer
      val nextLayer = buildLayer(state)
      if (nextLayer._2.size == state.size) {
        if (state == initialState) nextLayer :: Nil else Nil
      }
      else {
        val nextLayers = iterateLayer(nextLayer._2)
        nextLayer +: nextLayers
      }
    }
    iterateLayer(initialState)
  }
}

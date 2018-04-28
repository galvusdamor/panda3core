// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2017 the original author or authors.
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

package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.datastructures.{LayeredLiftedPrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LiftedForwardSearchReachabilityAnalysis(domain: Domain, initialState: Set[(Predicate, Boolean)]) extends LayeredLiftedPrimitiveReachabilityAnalysis {

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
        nextLayer :: Nil
      }
      else {
        val nextLayers = iterateLayer(nextLayer._2)
        nextLayer +: nextLayers
      }
    }
    iterateLayer(initialState)
  }
}

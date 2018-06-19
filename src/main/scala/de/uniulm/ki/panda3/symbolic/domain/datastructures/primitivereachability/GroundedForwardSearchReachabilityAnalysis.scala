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

package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis, LayeredGroundedPrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * A very simple, mutex relaxed reachability analysis
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GroundedForwardSearchReachabilityAnalysis(domain: Domain, initialState: Set[GroundLiteral])(allowedGroundings: Seq[GroundTask] = domain.allGroundedPrimitiveTasks) extends
  LayeredGroundedPrimitiveReachabilityAnalysis {

  lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {
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

object GroundedForwardSearchReachabilityAnalysis {
  def apply(d : Domain , p : Plan ) : GroundedForwardSearchReachabilityAnalysis =
    GroundedForwardSearchReachabilityAnalysis(d,p.groundedInitialState.toSet)()
}

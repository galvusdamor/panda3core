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

package de.uniulm.ki.panda3.efficient.search

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.search.SearchState

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class EfficientSearchNode[Payload](nodeID: Int, nodePlan: EfficientPlan, nodeParent: EfficientSearchNode[Payload], nodeHeuristic: Array[Double], nodeDistance: Double,
                                   nodeModHist: String = "")
  extends Ordered[EfficientSearchNode[Payload]] {
  /** the plan contained in this search node, if it has no flaws this is a solution */
  val plan         : EfficientPlan                = nodePlan
  /** the nodes parent */
  val parent       : EfficientSearchNode[Payload] = nodeParent
  /** the computed heuristic of this node. This might be -1 if the search procedure does not use a heuristic */
  val heuristic    : Array[Double]                = nodeHeuristic
  /** the distance part of the nodes heuristic */
  val distanceValue: Double                       = nodeDistance
  /** a unique ID for this search node */
  val id           : Int                          = nodeID

  val modHist: String = nodeModHist: String

  /** if this flag is true only the current plan, the heuristic and its parent are valid! Do not read any other information */
  def dirty: Boolean = innerDirty

  private var innerDirty           : Boolean              = true
  private var callBackIfSetNotDirty: Option[Unit => Unit] = None

  def setNotDirty(): Unit = {
    innerDirty = false
    if (callBackIfSetNotDirty.isDefined) (callBackIfSetNotDirty.get) ()
  }

  def setNotDirtyCallBack(callback: Unit => Unit): Unit = callBackIfSetNotDirty = Some(callback)


  /** returns the current state of this search node */
  def searchState: SearchState = if (plan.flaws.isEmpty) SearchState.SOLUTION
  else if (dirty) SearchState.INSEARCH
  else if (modifications exists { _.isEmpty }) SearchState.DEADEND_UNRESOLVABLEFLAW
  else SearchState.EXPLORED

  /** the flaw selected for refinement */
  var selectedFlaw : Int                                        = -1
  /** the possible modifications for all flaws. The i-th list of modifications will be the list of possible resolvantes for the i-the flaw in the plan's flaw list. If one of the lists is
    * empty this is a dead-end node in the search space. */
  var modifications: Array[Array[EfficientModification]]        = Array()
  /** the successors based on the list of modifications. The pair (sn,i) indicates that the child sn was generated based on the modification modifications(selectedFlaw)(i) */
  var children     : Array[(EfficientSearchNode[Payload], Int)] = Array()
  /** any possible further payload */
  var payload      : Array[Payload]                             = _

  override def compare(that: EfficientSearchNode[Payload]): Int = {
    var heuristicCompare = 0
    var hPos = 0
    while (heuristicCompare == 0 && hPos < heuristic.length) {
      heuristicCompare = Math.signum(that.heuristic(hPos) - this.heuristic(hPos)).toInt
      hPos += 1
    }
    // tiebreaker of last resort: FIFO
    if (heuristicCompare != 0) heuristicCompare else Math.signum(that.id - this.id).toInt
  }
}

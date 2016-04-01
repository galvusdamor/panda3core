package de.uniulm.ki.panda3.efficient.search

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.search.SearchState

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class EfficientSearchNode(nodePlan: EfficientPlan, nodeParent: EfficientSearchNode, nodeHeuristic: Double) {
  /** the plan contained in this search node, if it has no flaws this is a solution */
  val plan     : EfficientPlan       = nodePlan
  /** the nodes parent */
  val parent   : EfficientSearchNode = nodeParent
  /** the computed heuristic of this node. This might be -1 if the search procedure does not use a heuristic */
  val heuristic: Double              = nodeHeuristic

  /** if this flag is true only the current plan, the heuristic and its parent are valid! Do not read any other information */
  var dirty: Boolean = true


  /** returns the current state of this search node */
  def searchState: SearchState = if (plan.flaws.isEmpty) SearchState.SOLUTION
  else if (dirty) SearchState.INSEARCH
  else if (modifications exists { _.isEmpty }) SearchState.DEADEND_UNRESOLVABLEFLAW
  else SearchState.EXPLORED

  /** the flaw selected for refinement */
  var selectedFlaw : Int                                 = -1
  /** the possible modifications for all flaws. The i-th list of modifications will be the list of possible resolvantes for the i-the flaw in the plan's flaw list. If one of the lists is
    * empty this is a dead-end node in the search space. */
  var modifications: Array[Array[EfficientModification]] = Array()
  /** the successors based on the list of modifications. The pair (sn,i) indicates that the child sn was generated based on the modification modifications(selectedFlaw)(i) */
  var children     : Array[(EfficientSearchNode, Int)]   = Array()
  /** any possible further payload */
  var payload      : Any                                 = null
}

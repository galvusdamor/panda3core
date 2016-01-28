package de.uniulm.ki.panda3.symbolic.search

import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

/**
  * Represents a search state in a search space. This structure will (usually, i.e., we will probably never have loops do the systematicity) be a tree.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class SearchNode(nodePlan: Plan, nodeParent: SearchNode, nodeHeuristic: Double) {
  /** the plan contained in this search node, if it has no flaws this is a solution */
  val plan     : Plan       = nodePlan
  /** the nodes parent */
  val parent   : SearchNode = nodeParent
  /** the computed heuristic of this node. This might be -1 if the search procedure does not use a heuristic */
  val heuristic: Double     = nodeHeuristic

  /** if this flag is true only the current plan, the heuristic and its parent are valid! Do not read any other information */
  var dirty: Boolean = true


  /** the flaw selected for refinement */
  var selectedFlaw : Int                    = -1
  /** the possible modifications for all flaws. The i-th list of modifications will be the list of possible resolvantes for the i-the flaw in the plan's flaw list. If one of the lists is
    * empty this is a dead-end node in the search space. */
  var modifications: Seq[Seq[Modification]] = Nil
  /** the successors based on the list of modifications. The pair (sn,i) indicates that the child sn was generated based on the modification modifications(selectedFlaw)(i) */
  var children     : Seq[(SearchNode,Int)]        = Nil
  /** any possible further payload */
  var payload      : Any                    = null
}
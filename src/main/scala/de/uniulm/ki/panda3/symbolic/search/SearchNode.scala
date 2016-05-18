package de.uniulm.ki.panda3.symbolic.search

import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

import scala.concurrent.Promise
import scala.util.Success

/**
  * Represents a search state in a search space. This structure will (usually, i.e., we will probably never have loops do the systematicity) be a tree.
  *
  * @param plan      the plan contained in this search node, if it has no flaws this is a solution
  * @param parent    the nodes parent node
  * @param heuristic the computed heuristic of this node. This might be -1 if the search procedure does not use a heuristic
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SearchNode(id : Int, plan: Plan, parent: SearchNode, heuristic: Double) {

  /** if this flag is true only the current plan, the heuristic and its parent are valid! Do not read any other information */
  var dirty: Boolean = true

  /** returns the current state of this search node */
  def searchState: SearchState = if (plan.flaws.isEmpty || (!dirty && children.isEmpty)) SearchState.SOLUTION
  else if (dirty) SearchState.INSEARCH
  else if (modifications exists { _.isEmpty }) SearchState.DEADEND_UNRESOLVABLEFLAW
  else SearchState.EXPLORED


  def setSelectedFlaw(flaw: Int): Unit = { memorySelectedFlaw = Some(flaw) }

  def setSelectedFlaw(flaw: () => Int): Unit = { promiseSelectedFlaw = Some(flaw) }

  def setModifications(mods: Seq[Seq[Modification]]): Unit = { memoryModifications = Some(mods) }

  def setModifications(mods: () => Seq[Seq[Modification]]): Unit = { promiseModifications = Some(mods) }

  def setChildren(children: Seq[(SearchNode, Int)]): Unit = { memoryChildren = Some(children) }

  def setChildren(children: () => Seq[(SearchNode, Int)]): Unit = { promiseChildren = Some(children) }

  def setPayload(payload: Any): Unit = { memoryPayload = Some(payload) }

  def setPayload(payload: () => Any): Unit = { promisePayload = Some(payload) }


  private var promiseSelectedFlaw : Option[() => Int]                    = None
  private var promiseModifications: Option[() => Seq[Seq[Modification]]] = None
  private var promiseChildren     : Option[() => Seq[(SearchNode, Int)]] = None
  private var promisePayload      : Option[() => Any]                    = None

  private var memorySelectedFlaw : Option[Int]                    = None
  private var memoryModifications: Option[Seq[Seq[Modification]]] = None
  private var memoryChildren     : Option[Seq[(SearchNode, Int)]] = None
  private var memoryPayload      : Option[Any]                    = None


  /** the flaw selected for refinement */
  def selectedFlaw: Int = {
    assert(!dirty)
    if (memorySelectedFlaw.isEmpty) {
      memorySelectedFlaw = promiseSelectedFlaw match {
        case Some(x) => assert(!dirty); Some(x())
        case _       => None
      }
    }
    memorySelectedFlaw getOrElse -1
  }

  /** the possible modifications for all flaws. The i-th list of modifications will be the list of possible resolvantes for the i-the flaw in the plan's flaw list. If one of the lists is
    * empty this is a dead-end node in the search space. */
  def modifications: Seq[Seq[Modification]] = {
    if (memoryModifications.isEmpty) {
      memoryModifications = promiseModifications match {
        case Some(x) => assert(!dirty); Some(x())
        case _       => None
      }
    }
    memoryModifications getOrElse Nil
  }

  /** the successors based on the list of modifications. The pair (sn,i) indicates that the child sn was generated based on the modification modifications(selectedFlaw)(i) */
  def children: Seq[(SearchNode, Int)] = {
    if (memoryChildren.isEmpty) {
      memoryChildren = promiseChildren match {
        case Some(x) => assert(!dirty); Some(x())
        case _       => None
      }
    }
    memoryChildren getOrElse Nil
  }

  /** any possible further payload */
  def payload: Any = {
    if (memoryPayload.isEmpty) {
      memoryPayload = promisePayload match {
        case Some(x) => assert(!dirty); Some(x())
        case _       => None
      }
    }
    memoryPayload.orNull
  }
}
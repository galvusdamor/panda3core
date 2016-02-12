package de.uniulm.ki.panda3.efficient.plan.ordering

import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering._

/**
 * The assumption is, that there are exactly sz(orderingConstraints) many tasks, which are numbered 0..sz(orderingConstraints)-1
 *
 * The matrix contained in this object describes the relation between two tasks using the constants of the companion object [[de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering]]
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class EfficientOrdering(val orderingConstraints: Array[Array[Byte]] = Array(), var isConsistent: Boolean = true) extends PartialOrdering[Int] {

  /**
   * Propagate newly inserted ordering constraints using Bellman-Ford
   */
  private def propagate(edgesFrom: Array[Int], edgesTo: Array[Int]): Unit = {
    assert(edgesFrom.length == edgesTo.length)
    var edge = 0
    while (edge < edgesFrom.length && isConsistent) {
      var from = 0
      val ordEdgeFromEdgeTo = orderingConstraints(edgesFrom(edge))(edgesTo(edge))
      while (from < orderingConstraints.length && isConsistent) {
        val ordFromEdgeFrom = orderingConstraints(from)(edgesFrom(edge))
        if (ordFromEdgeFrom != DONTKNOW) {
          var to = 0
          while (to < orderingConstraints.length && isConsistent) {
            // check whether from -> edgesFrom[i] -> edgesTo[i] -> to entails something
            val ordEdgeToTo = orderingConstraints(edgesTo(edge))(to)
            if (ordEdgeToTo != DONTKNOW) {
              // check whether "before" is implied
              if (ordFromEdgeFrom <= SAME && ordEdgeFromEdgeTo <= SAME && ordEdgeToTo <= SAME) {
                val inferredOrdering = math.min(ordFromEdgeFrom, math.min(ordEdgeFromEdgeTo, ordEdgeToTo))
                if (orderingConstraints(from)(to) == DONTKNOW || orderingConstraints(from)(to) == inferredOrdering) orderingConstraints(from)(to) = inferredOrdering.toByte
                else isConsistent = false
              }
              // check whether "after" is implied
              if (ordFromEdgeFrom >= SAME && ordEdgeFromEdgeTo >= SAME && ordEdgeToTo >= SAME) {
                val inferredOrdering = math.max(ordFromEdgeFrom, math.max(ordEdgeFromEdgeTo, ordEdgeToTo))
                if (orderingConstraints(from)(to) == DONTKNOW || orderingConstraints(from)(to) == inferredOrdering) orderingConstraints(from)(to) = inferredOrdering.toByte
                else isConsistent = false
              }
            }
            to += 1
          }
        }
        from += 1
      }
      edge += 1
    }
  }

  // Functions needed to be a partial ordering
  override def tryCompare(x: Int, y: Int): Option[Int] =
    if (x >= orderingConstraints.length || y >= orderingConstraints.length) None
    else if (orderingConstraints(x)(y) == DONTKNOW) None else Some(orderingConstraints(x)(y))

  override def lteq(x: Int, y: Int): Boolean = if (x >= orderingConstraints.length || y >= orderingConstraints.length) false else orderingConstraints(x)(y) <= SAME


  /**
   * deep-clone this CSP
   */
  def copy(): EfficientOrdering = addPlanSteps(0)

  /**
   * add the given amount of plan steps without any connection to the rest of the plan steps
   *
   * the new plan steps will be numbered sz(orderingConstraints) .. sz(orderingConstraints) + newPlanSteps - 1
   */
  def addPlanSteps(newPlanSteps: Int): EfficientOrdering = addPlanStepsWithMaybeFromBase(fromBase = false, -1, None, -1, newPlanSteps)

  /**
   * adds the ordering constraint before < after to the ordering. This will automatically re-compute the transitive hull of the ordering relation
   */
  def addOrderingConstraint(before: Int, after: Int): Unit =
    if (orderingConstraints(before)(after) == SAME || orderingConstraints(before)(after) == AFTER) isConsistent = false
    else {
      orderingConstraints(before)(after) = BEFORE
      orderingConstraints(after)(before) = AFTER
      propagate(Array(before, after), Array(after, before))
    }

  /**
   * removes a plan step entirely from the ordering, while preserving all ordering constraints that went through it.
   * All plan steps with an index greater than ps will be renumbered s.t. the numbers are again contiguous, i.e. 1 will be substracted from them
   *
   * E.g. if 1 is removed from {0<1<2,3}, the result will be {0<1,2}, not {0,1,2}
   */
  def removePlanStep(ps: Int): EfficientOrdering = {
    val newOrdering = new Array[Array[Byte]](orderingConstraints.length - 1)
    var i = 0
    while (i < newOrdering.length) {
      newOrdering(i) = new Array[Byte](newOrdering.length)
      var j = 0
      while (j < newOrdering.length) {
        newOrdering(i)(j) = orderingConstraints(i + (if (i >= ps) 1 else 0))(j + (if (j >= ps) 1 else 0))
        j += 1
      }
      i += 1
    }
    new EfficientOrdering(newOrdering, isConsistent)
  }


  /**
   * indexBaseInNew will assume the index of oldPlanStep, while the newly added plan steps will receive the next sz(internalOrdering)-1 numbers
   */
  def replacePlanStep(oldPlanStep: Int, ordering: EfficientOrdering, indexBaseInNew: Int): EfficientOrdering =
    addPlanStepsWithMaybeFromBase(fromBase = true, oldPlanStep, Some(ordering.orderingConstraints), indexBaseInNew, -1)


  /**
   * internal function that actually performs the copying-around needed to copy, add, delete and replace plan steps.
   */
  private def addPlanStepsWithMaybeFromBase(fromBase: Boolean, base: Int, internalOrdering: Option[Array[Array[Byte]]], indexBaseInNew: Int, newPlanSteps: Int): EfficientOrdering = {
    val originalSize = orderingConstraints.length
    val newOrdering = new Array[Array[Byte]](originalSize + (if (fromBase) internalOrdering.get.length - 1 else newPlanSteps))
    var i = 0
    while (i < originalSize) {
      newOrdering(i) = new Array[Byte](newOrdering.length)
      var j = 0
      while (j < originalSize) {
        // copy the original matrix
        newOrdering(i)(j) = orderingConstraints(i)(j)
        j += 1
      }
      while (j < newOrdering.length) {
        // copy the original matrix
        if (fromBase) {
          if (i != base) newOrdering(i)(j) = orderingConstraints(i)(base)
          else {
            var indexOnNewOrdering = j - originalSize
            if (indexOnNewOrdering >= indexBaseInNew) indexOnNewOrdering = indexOnNewOrdering + 1
            newOrdering(i)(j) = internalOrdering.get(indexBaseInNew)(indexOnNewOrdering)
          }
        } else newOrdering(i)(j) = DONTKNOW
        j += 1
      }
      i += 1
    }
    while (i < newOrdering.length) {
      newOrdering(i) = new Array[Byte](newOrdering.length)
      var j = 0
      while (j < newOrdering.length) {
        if (i == j) newOrdering(i)(j) = SAME
        else if (!fromBase) newOrdering(i)(j) = DONTKNOW
        else if (j < originalSize) {
          if (j != base) newOrdering(i)(j) = orderingConstraints(base)(j)
          else {
            var indexOnNewOrdering = i - originalSize
            if (indexOnNewOrdering >= indexBaseInNew) indexOnNewOrdering = indexOnNewOrdering + 1
            newOrdering(i)(j) = internalOrdering.get(indexOnNewOrdering)(indexBaseInNew)
          }
        } else {
          var iIndexOnNewOrdering = i - originalSize
          if (iIndexOnNewOrdering >= indexBaseInNew) iIndexOnNewOrdering = iIndexOnNewOrdering + 1
          var jIndexOnNewOrdering = j - originalSize
          if (jIndexOnNewOrdering >= indexBaseInNew) jIndexOnNewOrdering = jIndexOnNewOrdering + 1
          // apply
          newOrdering(i)(j) = internalOrdering.get(iIndexOnNewOrdering)(jIndexOnNewOrdering)
        }
        j += 1
      }
      i += 1
    }
    new EfficientOrdering(newOrdering, isConsistent)
  }
}
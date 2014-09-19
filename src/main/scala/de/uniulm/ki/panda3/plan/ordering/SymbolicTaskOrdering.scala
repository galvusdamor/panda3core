package de.uniulm.ki.panda3.plan.ordering

import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicTaskOrdering(val originalOrderingConstraints: IndexedSeq[OrderingConstraint]) extends TaskOrdering {

  private var isTransitiveHullComputed: Boolean = false
  private val arrangement: Array[Array[Byte]] = Array.fill(originalOrderingConstraints.length, originalOrderingConstraints.length)(0)


  def initialiseExplicitly(lastKOrderingsAreNew: Int = originalOrderingConstraints.length, prevArrangement: Array[Array[Byte]] = Array.ofDim(0, 0)): Unit = {
    // init the current arrangement with the old one
    List.range(0, prevArrangement.length - 1).foreach(i => prevArrangement(i).copyToArray(arrangement(i)))
    // update the arrangement
    List.range(originalOrderingConstraints.length - lastKOrderingsAreNew, originalOrderingConstraints.length - 1).foreach(i => {
      arrangement(originalOrderingConstraints(i).before.id)(originalOrderingConstraints(i).after.id) = SymbolicTaskOrdering.BEFORE
      arrangement(originalOrderingConstraints(i).after.id)(originalOrderingConstraints(i).before.id) = SymbolicTaskOrdering.AFTER
    })

    // run floyd-warshall
    {
        for {
        newEdge <- originalOrderingConstraints.length - lastKOrderingsAreNew to arrangement.length - 1
        from <- 0 to arrangement.length - 1
        to <- 0 to arrangement.length - 1
        }{
            
        }
    }

    isTransitiveHullComputed = true
  }

  override def tryCompare(x: PlanStep, y: PlanStep): Option[Int] = {
    if (!isTransitiveHullComputed)
      initialiseExplicitly()

    arrangement(x.id)(y.id) match {
      case 0 => None
      case i => Some(i)
    }
  }

  override def lteq(x: PlanStep, y: PlanStep): Boolean = {
    tryCompare(x, y) match {
      case None => false
      case Some(SymbolicTaskOrdering.BEFORE) => true
      case _ => false
    }
  }
}


object SymbolicTaskOrdering {
  private val AFTER: Byte = 1
  private val BEFORE: Byte = -1
}
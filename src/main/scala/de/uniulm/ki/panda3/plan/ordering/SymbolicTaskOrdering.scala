package de.uniulm.ki.panda3.plan.ordering

import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicTaskOrdering(originalOrderingConstraints: Seq[OrderingConstraint], numberOfTasks: Int) extends TaskOrdering {

  import de.uniulm.ki.panda3.plan.ordering.TaskOrdering._

  private val innerArrangement: Array[Array[Byte]] = Array.fill(numberOfTasks, numberOfTasks)(DONTKNOW)
  private var isTransitiveHullComputed: Boolean = false
  private var computedInconsistent = false

  override def isConsistent: Boolean = {
    if (!isTransitiveHullComputed)
      initialiseExplicitly()

    !computedInconsistent
  }

  override def tryCompare(x: PlanStep, y: PlanStep): Option[Int] = {
    arrangement()(x.id)(y.id) match {
      case DONTKNOW => None
      case i => Some(i)
    }
  }

  override def arrangement(): Array[Array[Byte]] = {
    if (!isTransitiveHullComputed)
      initialiseExplicitly()

    innerArrangement
  }

  override def addOrdering(before: PlanStep, after: PlanStep): SymbolicTaskOrdering = {
    val newNumberOfVariables: Int = math.max(numberOfTasks, math.max(before.id + 1, after.id + 1))
    val newOrdering: SymbolicTaskOrdering = new SymbolicTaskOrdering(originalOrderingConstraints :+ OrderingConstraint(before, after), newNumberOfVariables)

    // if this ordering was already initialised let the new one know what we did so far
    if (isTransitiveHullComputed)
      newOrdering.initialiseExplicitly(1, newNumberOfVariables - numberOfTasks, innerArrangement)

    newOrdering
  }

  def initialiseExplicitly(lastKOrderingsAreNew: Int = originalOrderingConstraints.length, lastKTasksAreNew: Int = numberOfTasks,
                           prevArrangement: Array[Array[Byte]] = Array.ofDim(0, 0)): Unit = {
    // init the current arrangement with the old one
    Range(0, prevArrangement.length).foreach(i => prevArrangement(i).copyToArray(innerArrangement(i)))
    // update the arrangement
    Range(originalOrderingConstraints.length - lastKOrderingsAreNew, originalOrderingConstraints.length).foreach(i => {
      if (innerArrangement(originalOrderingConstraints(i).before.id)(originalOrderingConstraints(i).after.id) == AFTER) computedInconsistent = true
      if (innerArrangement(originalOrderingConstraints(i).before.id)(originalOrderingConstraints(i).after.id) == SAME) computedInconsistent = true
      innerArrangement(originalOrderingConstraints(i).before.id)(originalOrderingConstraints(i).after.id) = BEFORE
      innerArrangement(originalOrderingConstraints(i).after.id)(originalOrderingConstraints(i).before.id) = AFTER
    })
    if (!computedInconsistent) {

      // for new tasks fill the diagonale
      Range(numberOfTasks - lastKTasksAreNew, numberOfTasks).foreach(i => innerArrangement(i)(i) = SAME)

      // run floyd-warshall for new edges
      for (newEdge <- originalOrderingConstraints.length - lastKOrderingsAreNew until originalOrderingConstraints.length;
           from <- 0 until innerArrangement.length) {
        val edgeTypeFromNew = innerArrangement(from)(originalOrderingConstraints(newEdge).before.id)
        if (edgeTypeFromNew != DONTKNOW)
          for (to <- 0 until innerArrangement.length) {
            val edgeTypeNewTo = innerArrangement(originalOrderingConstraints(newEdge).after.id)(to)
            (edgeTypeFromNew, edgeTypeNewTo) match {
              case (BEFORE, BEFORE) | (SAME, BEFORE) | (BEFORE, SAME) =>
                innerArrangement(from)(to) = BEFORE
                innerArrangement(to)(from) = AFTER
              case (_, _) => ()
            }
          }
      }

      // run floyd-warshall for new tasks
      for (middle <- numberOfTasks - lastKTasksAreNew until numberOfTasks; from <- 0 until innerArrangement.length)
        if (innerArrangement(from)(middle) != DONTKNOW) for (to <- 0 until innerArrangement.length)
          (innerArrangement(from)(middle), innerArrangement(middle)(to)) match {
            case (AFTER, AFTER) | (SAME, AFTER) | (AFTER, SAME) => innerArrangement(from)(to) = AFTER
            case (BEFORE, BEFORE) | (SAME, BEFORE) | (BEFORE, SAME) => innerArrangement(from)(to) = BEFORE
            case (_, _) => ()
          }

      // check for inconsistency
      for (i <- 0 to numberOfTasks - 1) computedInconsistent = computedInconsistent | innerArrangement(i)(i) != SAME

      isTransitiveHullComputed = true
    }
  }

  override def addPlanStep(ps: PlanStep): SymbolicTaskOrdering = {
    val newNumberOfVariables: Int = math.max(numberOfTasks, ps.id + 1)
    val newOrdering: SymbolicTaskOrdering = new SymbolicTaskOrdering(originalOrderingConstraints, newNumberOfVariables)

    // if this ordering was already initialised let the new one know what we did so far
    if (isTransitiveHullComputed)
      newOrdering.initialiseExplicitly(0, newNumberOfVariables - numberOfTasks, innerArrangement)

    newOrdering
  }

}
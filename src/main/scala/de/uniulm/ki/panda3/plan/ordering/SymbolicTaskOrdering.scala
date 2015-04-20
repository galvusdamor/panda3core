package de.uniulm.ki.panda3.plan.ordering

import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicTaskOrdering(originalOrderingConstraints: Seq[OrderingConstraint], tasks: Seq[PlanStep]) extends TaskOrdering {

  import de.uniulm.ki.panda3.plan.ordering.TaskOrdering._


  private lazy val arrangemetnIndexToPlanStep: Map[Int, PlanStep] = tasks.zipWithIndex.map(_.swap).toMap
  private      val numberOfTasks                                  = tasks.length
  private      val innerArrangement          : Array[Array[Byte]] = Array.fill(numberOfTasks, numberOfTasks)(DONTKNOW)
  private      val planStepToArrangemetnIndex: Map[PlanStep, Int] = tasks.zipWithIndex.toMap
  private var isTransitiveHullComputed: Boolean = false
  private var computedInconsistent = false

  override def isConsistent: Boolean = {
    ensureTransitiveHull()

    !computedInconsistent
  }

  override def tryCompare(x: PlanStep, y: PlanStep): Option[Int] = {
    arrangement()(planStepToArrangemetnIndex(x))(planStepToArrangemetnIndex(y)) match {
      case DONTKNOW => None
      case i => Some(i)
    }
  }

  override def arrangement(): Array[Array[Byte]] = {
    ensureTransitiveHull()

    innerArrangement
  }

  private def ensureTransitiveHull() = if (!isTransitiveHullComputed) initialiseExplicitly()

  def removePlanStep(ps: PlanStep): TaskOrdering =
    if (!(tasks contains ps)) this
    else {
      val newOrdering: SymbolicTaskOrdering = new SymbolicTaskOrdering(originalOrderingConstraints, tasks filterNot {_ == ps})

      // don't initialise
      newOrdering
    }

  def replacePlanStep(psOld: PlanStep, psNew: PlanStep): TaskOrdering =
    if (!(tasks contains psOld)) this
    else {
      val newOrdering: SymbolicTaskOrdering = new SymbolicTaskOrdering(originalOrderingConstraints, tasks map {
        case ps => if (ps == psOld) psNew
                   else ps
      })

      // if this ordering was already initialised let the new one know what we did so far
      if (isTransitiveHullComputed)
        newOrdering.initialiseExplicitly(0, 0, innerArrangement)

      newOrdering
    }

  override def addOrdering(before: PlanStep, after: PlanStep): SymbolicTaskOrdering = {
    // if necessary add the plan steps to the ordering
    val orderingWithPlanSteps = addPlanStep(before).addPlanStep(after)
    // generate the new order
    val newOrdering: SymbolicTaskOrdering = new SymbolicTaskOrdering(originalOrderingConstraints :+ OrderingConstraint(before, after), orderingWithPlanSteps.tasks)

    // if this ordering was already initialised, the orderingWithPlanSteps is too
    // and thus we can initialise the newOrdering
    if (orderingWithPlanSteps.isTransitiveHullComputed)
      newOrdering.initialiseExplicitly(1, 0, orderingWithPlanSteps.innerArrangement)

    newOrdering
  }

  override def addPlanStep(ps: PlanStep): SymbolicTaskOrdering =
    if (tasks contains ps) this
    else {
      val newOrdering: SymbolicTaskOrdering = new SymbolicTaskOrdering(originalOrderingConstraints, tasks :+ ps)

      // if this ordering was already initialised let the new one know what we did so far
      if (isTransitiveHullComputed)
        newOrdering.initialiseExplicitly(0, 1, innerArrangement)

      newOrdering
    }

  /**
   * Initialises the task ordering with a given, previously computed arrangement.
   * Use this function only, if you know exactly what you are doing
   */
  def initialiseExplicitly(lastKOrderingsAreNew: Int = originalOrderingConstraints.length, lastKTasksAreNew: Int = numberOfTasks,
                           prevArrangement: Array[Array[Byte]] = Array.ofDim(0, 0)): Unit = {
    // init the current arrangement with the old one
    Range(0, prevArrangement.length).foreach(i => prevArrangement(i).copyToArray(innerArrangement(i)))
    // update the arrangement
    Range(originalOrderingConstraints.length - lastKOrderingsAreNew, originalOrderingConstraints.length).foreach(i => {

      val beforeID = planStepToArrangemetnIndex(originalOrderingConstraints(i).before)
      val afterID = planStepToArrangemetnIndex(originalOrderingConstraints(i).after)

      if (innerArrangement(beforeID)(afterID) == AFTER) computedInconsistent = true
      if (innerArrangement(beforeID)(afterID) == SAME) computedInconsistent = true
      innerArrangement(beforeID)(afterID) = BEFORE
      innerArrangement(afterID)(beforeID) = AFTER
    })
    if (!computedInconsistent) {

      // for new tasks fill the diagonal
      Range(numberOfTasks - lastKTasksAreNew, numberOfTasks).foreach(i => innerArrangement(i)(i) = SAME)

      // run bellman-ford for new edges
      for (newEdge <- originalOrderingConstraints.length - lastKOrderingsAreNew until originalOrderingConstraints.length; from <- 0 until innerArrangement.length) {
        val beforeID = planStepToArrangemetnIndex(originalOrderingConstraints(newEdge).before)
        val afterID = planStepToArrangemetnIndex(originalOrderingConstraints(newEdge).after)

        val edgeTypeFromNew = innerArrangement(from)(beforeID)
        if (edgeTypeFromNew != DONTKNOW)
          for (to <- 0 until innerArrangement.length) {
            val edgeTypeNewTo = innerArrangement(afterID)(to)
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

  override def minimalOrderingConstraints(): Seq[OrderingConstraint] = {
    ensureTransitiveHull()
    if (!isConsistent) Nil
    else {
      val ord = innerArrangement.map(_.clone())

      // run floyd-warshall backwards
      for (from <- 0 until ord.length; middle <- 0 until ord.length)
        if (ord(from)(middle) != DONTKNOW)
          for (to <- 0 until ord.length)
            if (from != middle && to != middle && from != to)
              (ord(from)(middle), ord(middle)(to)) match {
                case (AFTER, AFTER) | (SAME, AFTER) | (AFTER, SAME)     => ord(from)(to) = DONTKNOW
                case (BEFORE, BEFORE) | (SAME, BEFORE) | (BEFORE, SAME) => ord(from)(to) = DONTKNOW
                case (_, _)                                             => ()
              }

      val allPairs = for (from <- 0 until ord.length; to <- from + 1 until ord.length) yield (from, to)


      allPairs collect { case (x, y) if ord(x)(y) == AFTER => OrderingConstraint(arrangemetnIndexToPlanStep(y), arrangemetnIndexToPlanStep(x))
      case (x, y) if ord(x)(y) == BEFORE => OrderingConstraint(arrangemetnIndexToPlanStep(x), arrangemetnIndexToPlanStep(y))
      }
    }
  }

  private def readOrderingConstraintsFromArrangement(arrangement: Array[Array[Byte]]): Seq[OrderingConstraint] =
    (0 until arrangement.length flatMap {case x => (x + 1) until arrangement.length map ((x, _))}) collect { case (x, y) if arrangement(x)(y) == BEFORE => OrderingConstraint(
      arrangemetnIndexToPlanStep(x), arrangemetnIndexToPlanStep(y))
    }
}
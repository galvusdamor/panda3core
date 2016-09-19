package de.uniulm.ki.panda3.symbolic.plan.ordering

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangePlanSteps, DomainUpdate}
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.util.{DirectedGraphWithInternalMapping, SimpleDirectedGraph, DirectedGraph, HashMemo}

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TaskOrdering(originalOrderingConstraints: Seq[OrderingConstraint], tasks: Seq[PlanStep]) extends PartialOrdering[PlanStep]
                                                                                                            with DomainUpdatable with PrettyPrintable with HashMemo {

  import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering._


  private lazy val arrangemetnIndexToPlanStep: Map[Int, PlanStep] = tasks.zipWithIndex.map(_.swap).toMap
  private      val numberOfTasks                                  = tasks.length
  private      val innerArrangement          : Array[Array[Byte]] = Array.fill(numberOfTasks, numberOfTasks)(DONTKNOW)
  private      val planStepToArrangementIndex: Map[PlanStep, Int] = tasks.zipWithIndex.toMap
  private      var isTransitiveHullComputed  : Boolean            = false
  private      var computedInconsistent                           = false

  def isConsistent: Boolean = {
    ensureTransitiveHull()

    !computedInconsistent
  }

  def tryCompare(x: PlanStep, y: PlanStep): Option[Int] = {
    arrangement()(planStepToArrangementIndex(x))(planStepToArrangementIndex(y)) match {
      case DONTKNOW => None
      case i        => Some(i)
    }
  }

  def arrangement(): Array[Array[Byte]] = {
    ensureTransitiveHull()

    innerArrangement
  }

  private def ensureTransitiveHull() = if (!isTransitiveHullComputed) initialiseExplicitly()


  /** removes several plan steps */
  def removePlanSteps(pss: Seq[PlanStep]): TaskOrdering = (pss foldLeft this) { case (ordering, ps) => ordering.removePlanStep(ps) }

  /** remove a plan step from a task ordering --> this may infer new ordering constraints as it will keep the transitive closure identical */
  def removePlanStep(ps: PlanStep): TaskOrdering =
    if (!(tasks contains ps)) this
    else {
      val keptOrderingConstraints = originalOrderingConstraints filterNot { _ contains ps }
      val newConstraintsForTransitivity =
        for (before <- originalOrderingConstraints collect { case OrderingConstraint(b, `ps`) => b }; after <- originalOrderingConstraints collect { case OrderingConstraint(`ps`, a) => a
        }) yield OrderingConstraint(before, after)

      val newOrdering = TaskOrdering(keptOrderingConstraints ++ newConstraintsForTransitivity, tasks filterNot { _ == ps })


      if (isTransitiveHullComputed) {
        // remove the plan step from the arrangement
        val newArrangement = innerArrangement.zipWithIndex filterNot { _._2 == planStepToArrangementIndex(ps) } map { _._1 } map { a => a.zipWithIndex filterNot {
          _._2 == planStepToArrangementIndex(ps)
        } map { _._1 }
        }
        // run the init, it won't do anything
        newOrdering.initialiseExplicitly(0, 0, newArrangement)
      }


      newOrdering
    }

  /** replace an old plan step with a new one, all orderings will be inherited */
  def replacePlanStep(psOld: PlanStep, psNew: PlanStep): TaskOrdering =
    if (!(tasks contains psOld)) this
    else {
      val newOrdering = TaskOrdering(originalOrderingConstraints map { _.update(ExchangePlanSteps(psOld, psNew)) }, tasks map { case ps => if (ps == psOld) psNew else ps })

      // if this ordering was already initialised let the new one know what we did so far
      if (isTransitiveHullComputed)
        newOrdering.initialiseExplicitly(0, 0, innerArrangement)

      newOrdering
    }

  def lteq(x: PlanStep, y: PlanStep): Boolean = {
    tryCompare(x, y) match {
      case None                      => false
      case Some(TaskOrdering.BEFORE) => true
      case Some(TaskOrdering.SAME)   => true
      case _                         => false
    }
  }

  def addOrderings(orderings: Seq[OrderingConstraint]): TaskOrdering = (orderings foldLeft this) { case (ordering, constraint) => ordering.addOrdering(constraint) }

  def addOrdering(ordering: OrderingConstraint): TaskOrdering = addOrdering(ordering.before, ordering.after)

  def addOrdering(before: PlanStep, after: PlanStep): TaskOrdering = {
    // if necessary add the plan steps to the ordering
    val orderingWithPlanSteps = addPlanStep(before).addPlanStep(after)
    // generate the new order
    val newOrdering = TaskOrdering(originalOrderingConstraints :+ OrderingConstraint(before, after), orderingWithPlanSteps.tasks)

    // if this ordering was already initialised, the orderingWithPlanSteps is too
    // and thus we can initialise the newOrdering
    if (orderingWithPlanSteps.isTransitiveHullComputed)
      newOrdering.initialiseExplicitly(1, 0, orderingWithPlanSteps.innerArrangement)

    newOrdering
  }

  /** adds a sequence of plan steps */
  def addPlanSteps(pss: Seq[PlanStep]): TaskOrdering = (pss foldLeft this) { case (ordering, ps) => ordering.addPlanStep(ps) }

  /** registers a new plan step at this ordering */
  def addPlanStep(ps: PlanStep): TaskOrdering =
    if (tasks contains ps) this
    else {
      val newOrdering = TaskOrdering(originalOrderingConstraints, tasks :+ ps)

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
    var i = 0
    while (i < prevArrangement.length) {
      prevArrangement(i).copyToArray(innerArrangement(i))
      i += 1
    }

    // update the arrangement
    i = originalOrderingConstraints.length - lastKOrderingsAreNew
    while (i < originalOrderingConstraints.length) {
      val beforeID = planStepToArrangementIndex(originalOrderingConstraints(i).before)
      val afterID = planStepToArrangementIndex(originalOrderingConstraints(i).after)

      if (innerArrangement(beforeID)(afterID) == AFTER) computedInconsistent = true
      if (innerArrangement(beforeID)(afterID) == SAME) computedInconsistent = true
      innerArrangement(beforeID)(afterID) = BEFORE
      innerArrangement(afterID)(beforeID) = AFTER

      i += 1
    }
    if (!computedInconsistent) {

      // for new tasks fill the diagonal
      var i = numberOfTasks - lastKTasksAreNew
      while (i < numberOfTasks) {
        innerArrangement(i)(i) = SAME
        i += 1
      }

      // run bellman-ford for new edges
      var newEdge = originalOrderingConstraints.length - lastKOrderingsAreNew
      while (newEdge < originalOrderingConstraints.length) {
        var from = 0
        while (from < innerArrangement.length) {
          val beforeID = planStepToArrangementIndex(originalOrderingConstraints(newEdge).before)
          val afterID = planStepToArrangementIndex(originalOrderingConstraints(newEdge).after)

          val edgeTypeFromNew = innerArrangement(from)(beforeID)
          if (edgeTypeFromNew != DONTKNOW) {
            var to = 0
            while (to < innerArrangement.length) {
              val edgeTypeNewTo = innerArrangement(afterID)(to)
              (edgeTypeFromNew, edgeTypeNewTo) match {
                case (BEFORE, BEFORE) | (SAME, BEFORE) | (BEFORE, SAME) =>
                  innerArrangement(from)(to) = BEFORE
                  innerArrangement(to)(from) = AFTER
                case (_, _)                                             => ()
              }
              to += 1
            }
          }
          from += 1
        }
        newEdge += 1
      }

      // run floyd-warshall for new tasks
      var middle = numberOfTasks - lastKTasksAreNew
      while (middle < numberOfTasks) {
        var from = 0
        while (from < innerArrangement.length) {
          if (innerArrangement(from)(middle) != DONTKNOW) {
            var to = 0
            while (to < innerArrangement.length) {
              (innerArrangement(from)(middle), innerArrangement(middle)(to)) match {
                case (AFTER, AFTER) | (SAME, AFTER) | (AFTER, SAME)     => innerArrangement(from)(to) = AFTER
                case (BEFORE, BEFORE) | (SAME, BEFORE) | (BEFORE, SAME) => innerArrangement(from)(to) = BEFORE
                case (_, _)                                             => ()
              }
              to += 1
            }
          }
          from += 1
        }

        middle += 1
      }

      // check for inconsistency
      i = 0
      while (i < numberOfTasks) {
        computedInconsistent = computedInconsistent | innerArrangement(i)(i) != SAME
        i += 1
      }

      isTransitiveHullComputed = true
    }
  }

  /** computes a minimal set of ordering constraints, s.t. their transitive hull is this task ordering */
  def minimalOrderingConstraints(): Seq[OrderingConstraint] = {
    ensureTransitiveHull()
    if (!isConsistent) Nil
    else {
      val ord = innerArrangement.map(_.clone())

      // run floyd-warshall backwards
      for (from <- ord.indices; middle <- ord.indices)
        if (ord(from)(middle) != DONTKNOW)
          for (to <- ord.indices)
            if (from != middle && to != middle && from != to)
              (ord(from)(middle), ord(middle)(to)) match {
                case (AFTER, AFTER) | (SAME, AFTER) | (AFTER, SAME)     => ord(from)(to) = DONTKNOW
                case (BEFORE, BEFORE) | (SAME, BEFORE) | (BEFORE, SAME) => ord(from)(to) = DONTKNOW
                case (_, _)                                             => ()
              }

      getOrderingConstraintsOfArrangement(ord)
    }
  }

  def allOrderingConstraints() : Seq[OrderingConstraint] = {
    ensureTransitiveHull()
    if (!isConsistent) Nil
    else getOrderingConstraintsOfArrangement(innerArrangement)
  }

  private def getOrderingConstraintsOfArrangement(arrangement : Array[Array[Byte]]) : Seq[OrderingConstraint] = {
    val allPairs = for (from <- arrangement.indices; to <- from + 1 until arrangement.length) yield (from, to)


    allPairs collect { case (x, y) if arrangement(x)(y) == AFTER => OrderingConstraint(arrangemetnIndexToPlanStep(y), arrangemetnIndexToPlanStep(x))
    case (x, y) if arrangement(x)(y) == BEFORE                   => OrderingConstraint(arrangemetnIndexToPlanStep(x), arrangemetnIndexToPlanStep(y))
    }
  }

  private def readOrderingConstraintsFromArrangement(arrangement: Array[Array[Byte]]): Seq[OrderingConstraint] =
    (arrangement.indices flatMap { case x => (x + 1) until arrangement.length map ((x, _)) }) collect {
      case (x, y) if arrangement(x)(y) == BEFORE => OrderingConstraint(arrangemetnIndexToPlanStep(x), arrangemetnIndexToPlanStep(y))
    }

  override def update(domainUpdate: DomainUpdate): TaskOrdering = domainUpdate match {
    case ExchangePlanSteps(exchangeMap) => exchangeMap.foldLeft(this) { case (ord, (oldPS, newPS)) => ord.replacePlanStep(oldPS, newPS) }
    case _                              => TaskOrdering(originalOrderingConstraints map { _.update(domainUpdate) }, tasks map { _.update(domainUpdate) })
  }

  /** checks whether this order is total */
  def isTotalOrder(): Boolean = {
    def find(remaining: Seq[PlanStep]): Boolean = if (remaining.size == 1) true
    else {
      // try all
      remaining exists { t =>
        // before all
        (remaining forall { ot => ot == t || lt(t, ot) }) && find(remaining.filterNot({ _ == t }))

      }
    }
    find(tasks)
    /*val mini = minimalOrderingConstraints()
    if (mini.size != tasks.size - 1) false
    else if (tasks.size == 1) true
    else tasks forall { t1 => tasks forall { t2 => t1 == t2 || (tryCompare(t1, t2) != None) } }*/
  }

  /** returns a short information about the object */
  override def shortInfo: String = "OrderingConstraints:\n" + (minimalOrderingConstraints() map { oc => "\t" + oc.before.shortInfo + " -> " + oc.after.shortInfo }).mkString("\n")

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = "OrderingConstraints:\n" + (
    (for (t1 <- tasks; t2 <- tasks) yield (t1, t2))
      collect { case (t1, t2) if lt(t1, t2) => "\t" + t1.shortInfo + " -> " + t2.shortInfo }).mkString("\n")

  lazy val graph: DirectedGraph[PlanStep] = {
    val tasksWithoutInitAndGoal = tasks filterNot { t => tasks forall { ot => lteq(t, ot) } } filterNot { t => tasks forall { ot => gteq(t, ot) } }
    val edges = minimalOrderingConstraints() map { case OrderingConstraint(before, after) => (before, after) } filter {
      case (a, b) => (tasksWithoutInitAndGoal contains a) && (tasksWithoutInitAndGoal contains b)
    }

    DirectedGraphWithInternalMapping(tasksWithoutInitAndGoal, edges)
  }
}

object TaskOrdering {
  val AFTER   : Byte = 1
  val BEFORE  : Byte = -1
  val SAME    : Byte = 0
  val DONTKNOW: Byte = 2
}
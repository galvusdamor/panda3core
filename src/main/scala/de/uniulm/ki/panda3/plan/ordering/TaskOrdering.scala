package de.uniulm.ki.panda3.plan.ordering
import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait TaskOrdering extends PartialOrdering[PlanStep]{

  def originalOrderingConstraints: Seq[OrderingConstraint]

  def numberOfTasks: Int

  def isConsistent: Boolean

  def arrangement(): Array[Array[Byte]]

  def tryCompare(x: PlanStep, y: PlanStep): Option[Int]

  def lteq(x: PlanStep, y: PlanStep): Boolean = {
    tryCompare(x, y) match {
      case None => false
      case Some(TaskOrdering.BEFORE) => true
      case _ => false
    }
  }

  def addOrderings(orderings: Seq[OrderingConstraint]) : TaskOrdering = (orderings foldLeft this){ case (ordering , constraint) => ordering.addOrdering(constraint)}

  def addOrdering(ordering: OrderingConstraint): TaskOrdering  = addOrdering(ordering.before, ordering.after)

  def addOrdering(before: PlanStep, after: PlanStep): TaskOrdering
}

object TaskOrdering {
  val AFTER: Byte = 1
  val BEFORE: Byte = -1
  val SAME: Byte = 0
  val DONTKNOW: Byte = 2
}

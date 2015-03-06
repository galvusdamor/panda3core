package de.uniulm.ki.panda3.plan.ordering

import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait TaskOrdering extends PartialOrdering[PlanStep] {

  def originalOrderingConstraints: Seq[OrderingConstraint]

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

  def addOrderings(orderings: Seq[OrderingConstraint]): TaskOrdering = (orderings foldLeft this) {case (ordering, constraint) => ordering.addOrdering(constraint)}

  def addOrdering(ordering: OrderingConstraint): TaskOrdering = addOrdering(ordering.before, ordering.after)

  def addOrdering(before: PlanStep, after: PlanStep): TaskOrdering


  /** registers a new plan step at this ordering */
  def addPlanStep(ps: PlanStep): TaskOrdering

  /** removed a plan step from a task ordering, all connections to it will be lost, including those inferred transitively */
  def removePlanStep(ps: PlanStep): TaskOrdering

  /** replace an old plan step with a new one, all orderings will be inherited */
  def replacePlanStep(psOld: PlanStep, psNew: PlanStep): TaskOrdering

  /** adds a sequence of plan steps */
  def addPlanSteps(pss: Seq[PlanStep]) = (pss foldLeft this) {case (ordering, ps) => ordering.addPlanStep(ps)}

  /** computes a minimal set of ordering constraints, s.t. their transitive hull is this task ordering */
  def minimalOrderingConstraints(): Seq[OrderingConstraint]

}

object TaskOrdering {
  val AFTER : Byte = 1
  val BEFORE: Byte = -1
  val SAME  : Byte = 0
  val DONTKNOW: Byte = 2
}

package de.uniulm.ki.panda3.symbolic.plan.ordering

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait TaskOrdering extends PartialOrdering[PlanStep] with DomainUpdatable with PrettyPrintable {

  def tasks: Seq[PlanStep]

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

  def addOrderings(orderings: Seq[OrderingConstraint]): TaskOrdering = (orderings foldLeft this) { case (ordering, constraint) => ordering.addOrdering(constraint) }

  def addOrdering(ordering: OrderingConstraint): TaskOrdering = addOrdering(ordering.before, ordering.after)

  def addOrdering(before: PlanStep, after: PlanStep): TaskOrdering


  /** registers a new plan step at this ordering */
  def addPlanStep(ps: PlanStep): TaskOrdering

  /** adds a sequence of plan steps */
  def addPlanSteps(pss: Seq[PlanStep]) = (pss foldLeft this) { case (ordering, ps) => ordering.addPlanStep(ps) }

  /** removed a plan step from a task ordering --> this may infer new ordering constraints as it will keep the transitive closure identical */
  def removePlanStep(ps: PlanStep): TaskOrdering

  /** removes several plan steps */
  def removePlanSteps(pss: Seq[PlanStep]) = (pss foldLeft this) { case (ordering, ps) => ordering.removePlanStep(ps) }


  /** replace an old plan step with a new one, all orderings will be inherited */
  def replacePlanStep(psOld: PlanStep, psNew: PlanStep): TaskOrdering

  /** computes a minimal set of ordering constraints, s.t. their transitive hull is this task ordering */
  def minimalOrderingConstraints(): Seq[OrderingConstraint]

  override def update(domainUpdate: DomainUpdate): TaskOrdering

  /** returns a short information about the object */
  override def shortInfo: String = "OrderingConstraints:\n" + (minimalOrderingConstraints() map { oc => "\t" + oc.before.shortInfo + " -> " + oc.after.shortInfo }).mkString("\n")

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = "OrderingConstraints:\n" + ((tasks zip tasks) collect { case (t1, t2) if lt(t1, t2) => "\t" + t1.shortInfo + " -> " + t2.shortInfo })
}

object TaskOrdering {
  val AFTER : Byte = 1
  val BEFORE: Byte = -1
  val SAME  : Byte = 0
  val DONTKNOW: Byte = 2
}

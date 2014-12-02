package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AddOrdering(ordering: OrderingConstraint) extends Modification {
  override def addedOrderingConstraints: Seq[OrderingConstraint] = ordering :: Nil
}

object AddOrdering {
  def apply(before: PlanStep, after: PlanStep): Seq[AddOrdering] = AddOrdering(OrderingConstraint(before, after)) :: Nil

  def apply(plan: Plan, ps: PlanStep, cl: CausalLink): Seq[AddOrdering] =
    (ps, cl.producer) ::(cl.consumer, ps) :: Nil collect { case (before, after) if (plan.orderingConstraints.gteq(before, after)) => AddOrdering(OrderingConstraint(before, after))}
}
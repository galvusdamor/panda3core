package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 * A modification that add a single ordering constraint to a plan
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AddOrdering(plan: Plan, ordering: OrderingConstraint) extends Modification {
  override def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = ordering :: Nil
}

object AddOrdering {
  /** modify by inserting the specified constraint */
  def apply(plan: Plan, before: PlanStep, after: PlanStep): Seq[AddOrdering] = AddOrdering(plan, OrderingConstraint(before, after)) :: Nil

  /** modify to solve a causal thread, this is either promotion or demotion */
  def apply(plan: Plan, ps: PlanStep, cl: CausalLink): Seq[AddOrdering] =
    (ps, cl.producer) ::(cl.consumer, ps) :: Nil collect { case (before, after) if !plan.orderingConstraints.gteq(before, after) => AddOrdering(plan, OrderingConstraint(before, after)) }
}
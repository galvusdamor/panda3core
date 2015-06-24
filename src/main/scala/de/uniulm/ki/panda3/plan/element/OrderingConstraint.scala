package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OrderingConstraint(before: PlanStep, after: PlanStep) extends DomainUpdatable {
  def contains(ps: PlanStep) = before == ps || after == ps

  override def update(domainUpdate: DomainUpdate): OrderingConstraint = OrderingConstraint(before.update(domainUpdate), after.update(domainUpdate))
}

object OrderingConstraint {

  def allBetween(first: PlanStep, last: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] =
    (steps flatMap { ps => OrderingConstraint(first, ps) :: OrderingConstraint(ps, last) :: Nil }) :+ OrderingConstraint(first, last)
}

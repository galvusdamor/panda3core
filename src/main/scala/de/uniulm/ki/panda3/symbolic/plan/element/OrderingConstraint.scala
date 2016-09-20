package de.uniulm.ki.panda3.symbolic.plan.element

import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class OrderingConstraint(before: PlanStep, after: PlanStep) extends DomainUpdatable {
  def contains(ps: PlanStep): Boolean = before == ps || after == ps

  def containsAny(ps: PlanStep*): Boolean = (ps map contains).fold(false)({ case (a, b) => a || b })

  override def update(domainUpdate: DomainUpdate): OrderingConstraint = OrderingConstraint(before.update(domainUpdate), after.update(domainUpdate))
}

object OrderingConstraint {

  def allBetween(first: PlanStep, last: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] =
    (steps flatMap { ps => OrderingConstraint(first, ps) :: OrderingConstraint(ps, last) :: Nil }) :+ OrderingConstraint(first, last)

  def allAfter(first: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] = steps map { OrderingConstraint(first, _) }

  def allBefore(first: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] = steps map { OrderingConstraint(_, first) }
}
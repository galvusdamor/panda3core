package de.uniulm.ki.panda3.plan.element

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OrderingConstraint(before: PlanStep, after: PlanStep) {
  def contains(ps: PlanStep) = before == ps || after == ps
}

object OrderingConstraint {

  def allBetween(first: PlanStep, last: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] = steps flatMap { ps => OrderingConstraint(first, ps) :: OrderingConstraint(ps, last) :: Nil }
}

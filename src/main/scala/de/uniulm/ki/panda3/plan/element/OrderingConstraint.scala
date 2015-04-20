package de.uniulm.ki.panda3.plan.element

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OrderingConstraint(before: PlanStep, after: PlanStep) {
}

object OrderingConstraint {

  def allBetween(first: PlanStep, last: PlanStep, steps: PlanStep*) = steps flatMap { ps => OrderingConstraint(first, ps) :: OrderingConstraint(ps, last) :: Nil }
}

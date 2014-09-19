package de.uniulm.ki.panda3.plan.element

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OrderingConstraint(val before: PlanStep, val after: PlanStep) {
}

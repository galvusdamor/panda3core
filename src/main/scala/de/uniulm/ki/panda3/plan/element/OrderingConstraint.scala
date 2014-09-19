package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait OrderingConstraint {
  val plan: Plan

  val before: PlanStep
  val after: PlanStep
}

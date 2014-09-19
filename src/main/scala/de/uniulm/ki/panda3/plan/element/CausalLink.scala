package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait CausalLink {
  val plan: Plan
  val producer: PlanStep
  val consumer: PlanStep

  val condition: Literal
}

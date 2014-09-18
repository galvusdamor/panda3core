package de.uniulm.ki.panda3.plan.elements

import de.uniulm.ki.panda3.plan.logic.Literal

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait CausalLink {
  val source: PlanStep
  val destination: PlanStep
  val literal : Literal
}

package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.logic.Literal

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class CausalLink(producer: PlanStep, consumer: PlanStep, condition: Literal) {
}
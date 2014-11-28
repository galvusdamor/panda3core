package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.PlanStep

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OpenPrecondition(planStep: PlanStep, precondition: Literal) extends Flaw {

}

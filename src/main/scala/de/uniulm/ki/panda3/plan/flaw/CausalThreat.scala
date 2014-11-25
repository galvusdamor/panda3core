package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}

/**
 * This trait represents a causal thread flaw
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class CausalThreat(link : CausalLink, planStep : PlanStep, effect : Literal) extends Flaw {

}

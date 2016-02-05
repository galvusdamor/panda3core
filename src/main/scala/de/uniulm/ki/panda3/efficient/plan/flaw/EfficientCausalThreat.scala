package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientCausalThreat(causalLink : EfficientCausalLink, threatingPlanStep : Int, indexOfThreatingEffect : Int, mgu : Array[EfficientVariableConstraint]) {

}

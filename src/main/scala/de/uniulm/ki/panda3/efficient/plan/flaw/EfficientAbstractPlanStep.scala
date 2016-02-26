package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientDecomposePlanStep, EfficientModification}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientAbstractPlanStep(plan: EfficientPlan, planStep: Int) extends EfficientFlaw {
  override lazy val resolver: Array[EfficientModification] = EfficientDecomposePlanStep(plan, this, planStep)
}
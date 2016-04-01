package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientDecomposePlanStep, EfficientModification}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientAbstractPlanStep(plan: EfficientPlan, planStep: Int) extends EfficientFlaw {
  override lazy val resolver: Array[EfficientModification] = EfficientDecomposePlanStep(plan, this, planStep)

  def severLinkToPlan: EfficientAbstractPlanStep = EfficientAbstractPlanStep(null, planStep)

  def equalToSeveredFlaw(flaw: EfficientFlaw): Boolean = if (flaw.isInstanceOf[EfficientAbstractPlanStep]) {
    val eaps = flaw.asInstanceOf[EfficientAbstractPlanStep]
    eaps.planStep == planStep
  } else false

  override lazy val estimatedNumberOfResolvers: Int = EfficientDecomposePlanStep.estimate(plan, this, planStep)
}
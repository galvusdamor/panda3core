package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientBindVariable, EfficientModification}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientUnboundVariable(plan: EfficientPlan, variable: Int) extends EfficientFlaw {
  override lazy val resolver: Array[EfficientModification] = EfficientBindVariable(plan, this, variable)

  def severLinkToPlan: EfficientUnboundVariable = EfficientUnboundVariable(null, variable)

  def equalToSeveredFlaw(flaw: EfficientFlaw) : Boolean = if (flaw.isInstanceOf[EfficientUnboundVariable]) {
    val euv = flaw.asInstanceOf[EfficientUnboundVariable]
    euv.variable == variable
  } else false

  override lazy val estimatedNumberOfResolvers: Int = EfficientBindVariable.estimate(plan,this,variable)
}
package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientBindVariable, EfficientModification}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientUnboundVariable(plan: EfficientPlan, variable: Int) extends EfficientFlaw {
  override lazy val resolver: Array[EfficientModification] = EfficientBindVariable(plan, variable)
}

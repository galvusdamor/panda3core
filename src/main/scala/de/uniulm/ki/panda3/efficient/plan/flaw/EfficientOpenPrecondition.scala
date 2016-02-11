package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientInsertCausalLink, EfficientModification}

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientOpenPrecondition(plan: EfficientPlan, planStep: Int, preconditionIndex: Int) extends EfficientFlaw {

  override lazy val resolver: Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    buffer appendAll EfficientInsertCausalLink(plan, planStep, preconditionIndex)
    buffer appendAll EfficientInsertPlanStepWithLink(plan, planStep, preconditionIndex)
    buffer.toArray
  }
}

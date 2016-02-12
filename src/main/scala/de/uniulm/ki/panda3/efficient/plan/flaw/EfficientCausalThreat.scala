package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.modification._

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientCausalThreat(plan: EfficientPlan, causalLink: EfficientCausalLink, threatingPlanStep: Int, indexOfThreatingEffect: Int, mgu: Array[EfficientVariableConstraint])
  extends EfficientFlaw {

  override lazy val resolver: Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    buffer appendAll EfficientMakeLiteralsUnUnifiable(plan, this, plan.argumentsOfPlanStepsEffect(threatingPlanStep, indexOfThreatingEffect),
                                                      plan.argumentsOfPlanStepsEffect(causalLink.producer, causalLink.conditionIndexOfProducer))
    buffer appendAll EfficientAddOrdering(plan, this, causalLink, threatingPlanStep)
    buffer.toArray
  }
}

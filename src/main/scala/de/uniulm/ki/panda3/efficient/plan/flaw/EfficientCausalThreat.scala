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
    // TODO ... maybe only add ordering if the threater is primitive ?!
    if (!plan.domain.tasks(plan.planStepTasks(threatingPlanStep)).isPrimitive) buffer appendAll EfficientDecomposePlanStep(plan, this, threatingPlanStep)
    buffer.toArray
  }

  def severLinkToPlan: EfficientCausalThreat = EfficientCausalThreat(null, causalLink, threatingPlanStep, indexOfThreatingEffect, mgu)

  def equalToSeveredFlaw(flaw: EfficientFlaw): Boolean = if (flaw.isInstanceOf[EfficientCausalThreat]) {
    val ect = flaw.asInstanceOf[EfficientCausalThreat]
    ect.causalLink == causalLink && ect.threatingPlanStep == threatingPlanStep && ect.indexOfThreatingEffect == indexOfThreatingEffect && ect.mgu.sameElements(mgu)
  } else false

  override lazy val estimatedNumberOfResolvers: Int = {
    val makeUnUnifiable = EfficientMakeLiteralsUnUnifiable.estimate(plan, this, plan.argumentsOfPlanStepsEffect(threatingPlanStep, indexOfThreatingEffect),
                                                                    plan.argumentsOfPlanStepsEffect(causalLink.producer, causalLink.conditionIndexOfProducer))
    val addOrdering = EfficientAddOrdering.estimate(plan, this, causalLink, threatingPlanStep)

    val decompose = if (!plan.domain.tasks(plan.planStepTasks(threatingPlanStep)).isPrimitive) EfficientDecomposePlanStep.estimate(plan, this, threatingPlanStep) else 0

    makeUnUnifiable + addOrdering + decompose
  }
}
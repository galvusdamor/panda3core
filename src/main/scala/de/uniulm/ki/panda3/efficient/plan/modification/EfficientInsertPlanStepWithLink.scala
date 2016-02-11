package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientInsertPlanStepWithLink(plan: EfficientPlan, newPlanStep: (Int, Array[Int], Int, Int), parameterVariableSorts: Array[Int], causalLink: EfficientCausalLink,
                                           necessaryVariableConstraints: Array[EfficientVariableConstraint]) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = necessaryVariableConstraints
  override val addedCausalLinks        : Array[EfficientCausalLink]         = Array(causalLink)
  override val addedPlanSteps          : Array[(Int, Array[Int], Int, Int)] = Array(newPlanStep)
  override val addedVariableSorts      : Array[Int]                         = parameterVariableSorts
}


object EfficientInsertPlanStepWithLink {

  def apply(plan: EfficientPlan, consumer: Int, consumerIndex: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()

    val consumerTask = plan.domain.tasks(plan.planStepTasks(consumer))
    val consumerLiteral = consumerTask.precondition(consumerIndex)
    val consumerParameters = consumerTask.getArgumentsOfLiteral(plan.planStepParameters(consumer), consumerLiteral)


    var possibleProducer: Array[(Int, Int)] = Array()
    if (consumerLiteral.isPositive) possibleProducer = plan.domain.possibleProducerTasksOf(consumerLiteral.predicate)._1
    else possibleProducer = plan.domain.possibleProducerTasksOf(consumerLiteral.predicate)._2


    var i = 0
    while (i < possibleProducer.length){
      val producerTask = plan.domain.tasks(possibleProducer(i)._1)
      val producerLiteral = producerTask.effect(possibleProducer(i)._2)
      val newVariableSorts = producerTask.parameterSorts
      val planStepParameterVariables = Array.range(plan.firstFreeVariableID, plan.firstFreeVariableID + newVariableSorts.length)
      val constraintsBuffer = new ArrayBuffer[EfficientVariableConstraint]()
      // add the constraints inherent to the task itself
      var j = 0
      while (j < producerTask.constraints.length){
        constraintsBuffer append producerTask.applyArgumentsToConstraint(planStepParameterVariables,producerTask.constraints(j))
        j+=1
      }

      // add the constraints we need to set the causal link
      // TODO: this is really naive .. maybe we add a CSP here
      val producerParameters = producerTask.getArgumentsOfLiteral(planStepParameterVariables,producerLiteral)
      j = 0
      while (j < producerParameters.length){
        constraintsBuffer append new EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE,producerParameters(j),consumerParameters(i))
        j += 1
      }

      val planStep = (plan.firstFreePlanStepID,planStepParameterVariables,-1,-1)
      val causalLink = EfficientCausalLink(plan.firstFreePlanStepID,consumer,possibleProducer(i)._2,consumerIndex)
      buffer append EfficientInsertPlanStepWithLink(plan,planStep,newVariableSorts,causalLink,constraintsBuffer.toArray)
      i+=1
    }

    buffer.toArray
  }
}
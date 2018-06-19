// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientInsertPlanStepWithLink(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, newPlanStep: (Int, Array[Int], Int, Int, Int), parameterVariableSorts: Array[Int],
                                           causalLink: EfficientCausalLink, necessaryVariableConstraints: Array[EfficientVariableConstraint]) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint]      = necessaryVariableConstraints
  override val addedCausalLinks        : Array[EfficientCausalLink]              = Array(causalLink)
  override val addedPlanSteps          : Array[(Int, Array[Int], Int, Int, Int)] = Array(newPlanStep)
  override val addedVariableSorts      : Array[Int]                              = parameterVariableSorts

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification = EfficientInsertPlanStepWithLink(null, severedFlaw, newPlanStep, parameterVariableSorts, causalLink,
                                                                                                           necessaryVariableConstraints)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "InsertPS " + newPlanStep + " and CL " + causalLink
}


object EfficientInsertPlanStepWithLink {

  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int): Array[EfficientModification] = if (!plan.problemConfiguration.taskInsertionAllowed)
    Array()
  else {
    val buffer = new ArrayBuffer[EfficientModification]()

    val consumerTask = plan.domain.tasks(plan.planStepTasks(consumer))
    val consumerLiteral = consumerTask.precondition(consumerIndex)
    val consumerParameters = consumerTask.getArgumentsOfLiteral(plan.planStepParameters(consumer), consumerLiteral)


    var possibleProducer: Array[(Int, Int)] = Array()
    if (consumerLiteral.isPositive) possibleProducer = plan.domain.possibleProducerTasksOf(consumerLiteral.predicate)._1
    else possibleProducer = plan.domain.possibleProducerTasksOf(consumerLiteral.predicate)._2


    var i = 0
    while (i < possibleProducer.length) {
      val producerTask = plan.domain.tasks(possibleProducer(i)._1)
      if (producerTask.allowedToInsert) {
        val producerLiteral = producerTask.effect(possibleProducer(i)._2)
        val newVariableSorts = producerTask.parameterSorts
        val planStepParameterVariables = Array.range(plan.firstFreeVariableID, plan.firstFreeVariableID + newVariableSorts.length)
        val constraintsBuffer = new ArrayBuffer[EfficientVariableConstraint]()
        // add the constraints inherent to the task itself
        var j = 0
        while (j < producerTask.constraints.length) {
          constraintsBuffer append producerTask.applyArgumentsToConstraint(planStepParameterVariables, producerTask.constraints(j))
          j += 1
        }

        // add the constraints we need to set the causal link
        // TODO: this is really naive .. maybe we add a CSP here (on the other hand CSPs are really slow if created that often)
        val producerParameters = producerTask.getArgumentsOfLiteral(planStepParameterVariables, producerLiteral)
        j = 0
        while (j < producerParameters.length) {
          constraintsBuffer append new EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, producerParameters(j), consumerParameters(j))
          j += 1
        }

        val planStep = (possibleProducer(i)._1, planStepParameterVariables, -1, -1, -1)
        val causalLink = EfficientCausalLink(plan.firstFreePlanStepID, consumer, possibleProducer(i)._2, consumerIndex)
        buffer append EfficientInsertPlanStepWithLink(plan, resolvedFlaw, planStep, newVariableSorts, causalLink, constraintsBuffer.toArray)
      }
      i += 1
    }

    buffer.toArray
  }

  def estimate(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int): Int = if (!plan.problemConfiguration.taskInsertionAllowed) 0
  else {
    val consumerTask = plan.domain.tasks(plan.planStepTasks(consumer))
    val consumerLiteral = consumerTask.precondition(consumerIndex)

    var possibleProducer: Array[(Int, Int)] = Array()
    if (consumerLiteral.isPositive) possibleProducer = plan.domain.possibleProducerTasksOf(consumerLiteral.predicate)._1
    else possibleProducer = plan.domain.possibleProducerTasksOf(consumerLiteral.predicate)._2

    possibleProducer.length
  }
}

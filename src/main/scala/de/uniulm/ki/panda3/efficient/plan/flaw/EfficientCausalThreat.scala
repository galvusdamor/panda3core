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

    // TODO is there any target predicate ????
    val decompose = if (!plan.domain.tasks(plan.planStepTasks(threatingPlanStep)).isPrimitive) EfficientDecomposePlanStep.estimate(plan, this, threatingPlanStep,-1,false) else 0

    makeUnUnifiable + addOrdering + decompose
  }

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Causal Threat: CL " + causalLink + " on PS: " + threatingPlanStep + " with effect " + indexOfThreatingEffect

}

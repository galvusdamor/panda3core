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

package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.EfficientGroundTask
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.InformationCapsule

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientHeuristic[Payload] {

  def computeHeuristic(plan: EfficientPlan, payload: Payload, appliedModification: Option[EfficientModification], depth: Int, oldHeuristic: Double,
                       informationCapsule: InformationCapsule): (Double, Payload)

  final def computeHeuristic(plan: EfficientPlan, payload: Payload, appliedModification: EfficientModification, depth: Int, oldHeuristic: Double,
                       informationCapsule: InformationCapsule): (Double, Payload) = computeHeuristic(plan, payload, Some(appliedModification), depth, oldHeuristic, informationCapsule)

  def computeInitialPayLoad(plan: EfficientPlan) : Payload
}


object AlwaysZeroHeuristic extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double,
                                informationCapsule: InformationCapsule): (Double, Unit) = (0, ())

  def computeInitialPayLoad(plan: EfficientPlan) : Unit = ()
}

trait MinimisationOverGroundingsBasedHeuristic[Payload] extends EfficientHeuristic[Payload] {

  def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double

  def computeHeuristicByGrounding(planStep: Int, plan: EfficientPlan): Double =
    computeHeuristicByGrounding(planStep, new Array[Int](plan.planStepParameters(planStep).length), 0, plan)

  private def computeHeuristicByGrounding(planStep: Int, parameter: Array[Int], numberOfChosenParameters: Int, plan: EfficientPlan): Double =
    if (numberOfChosenParameters == parameter.length) {
      // query the actual heuristic
      //val groundTask = EfficientGroundTask(plan.planStepTasks(planStep), parameter)
      groundingEstimator(plan, planStep, parameter)
    } else {
      val psParams = plan.planStepParameters(planStep)
      val variableInQuestion = psParams(numberOfChosenParameters)
      if (!plan.variableConstraints.isRepresentativeAVariable(variableInQuestion)) {
        // if a parameter is already bound to a constant
        parameter(numberOfChosenParameters) = plan.variableConstraints.getRepresentativeConstant(variableInQuestion)
        computeHeuristicByGrounding(planStep, parameter, numberOfChosenParameters + 1, plan)
      } else {
        // check whether this variable is identical to another we have already bound
        var otherParameter = 0
        var identicalTo = -1
        while (otherParameter < numberOfChosenParameters) {
          val otherParameterVariable = psParams(otherParameter)
          if (plan.variableConstraints.isRepresentativeAVariable(otherParameterVariable) &&
            plan.variableConstraints.getRepresentativeVariable(otherParameterVariable) == plan.variableConstraints.getRepresentativeVariable(variableInQuestion))
            identicalTo = otherParameter
          otherParameter += 1
        }

        if (identicalTo != -1) {
          parameter(numberOfChosenParameters) = parameter(identicalTo)
          computeHeuristicByGrounding(planStep, parameter, numberOfChosenParameters + 1, plan)
        } else {
          // ok now we actually have to ground
          val remainingValues = plan.variableConstraints.getRemainingDomain(variableInQuestion).toArray
          var heuristicMin = Double.MaxValue
          var constant = 0
          while (constant < remainingValues.length) {
            parameter(numberOfChosenParameters) = remainingValues(constant)
            heuristicMin = Math.min(heuristicMin, computeHeuristicByGrounding(planStep, parameter, numberOfChosenParameters + 1, plan))
            constant += 1
          }
          heuristicMin
        }
      }
    }
}

package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.EfficientGroundTask
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientHeuristic[Payload] {

  def computeHeuristic(plan: EfficientPlan, payload: Payload, appliedModification: EfficientModification, depth : Int): (Double, Payload)
}


object AlwaysZeroHeuristic extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification,depth : Int): (Double, Unit) = (0, ())
}

trait MinimisationOverGroundingsBasedHeuristic[Payload] extends EfficientHeuristic[Payload] {

  protected def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double

  protected def computeHeuristicByGrounding(planStep: Int, plan: EfficientPlan): Double =
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
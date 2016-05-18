package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.EfficientGroundTask
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait TDGHeuristics extends EfficientHeuristic {
  def taskDecompositionTree: Any
}


case class MinimumModificationEffortHeuristic(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph) extends TDGHeuristics {

  private def computeHeuristic(planStep: Int, parameter: Array[Int], numberOfChoosenParameters: Int, plan: EfficientPlan): Double =
    if (numberOfChoosenParameters == parameter.length) {
      // query the TDG
      val groundTask = EfficientGroundTask(plan.planStepTasks(planStep), parameter)
      if (!taskDecompositionTree.graph.andVertices.contains(groundTask)) Double.MaxValue
      else
        taskDecompositionTree.graph.minSumTraversal(groundTask, { task => plan.domain.tasks(task.taskID).precondition.length })
    } else {
      val psParams = plan.planStepParameters(planStep)
      val variableInQuestion = psParams(numberOfChoosenParameters)
      if (!plan.variableConstraints.isRepresentativeAVariable(variableInQuestion)) {
        // if a parameter is already bound to a constant
        parameter(numberOfChoosenParameters) = plan.variableConstraints.getRepresentativeVariable(variableInQuestion)
        computeHeuristic(planStep, parameter, numberOfChoosenParameters + 1, plan)
      } else {
        // check whether this variable is identical to another we have already bound
        var otherParameter = 0
        var identicalTo = -1
        while (otherParameter < numberOfChoosenParameters) {
          val otherParameterVariable = psParams(otherParameter)
          if (plan.variableConstraints.isRepresentativeAVariable(otherParameterVariable) &&
            plan.variableConstraints.getRepresentativeVariable(otherParameterVariable) == plan.variableConstraints.getRepresentativeVariable(variableInQuestion))
            identicalTo = otherParameter
          otherParameter += 1
        }

        if (identicalTo != -1) {
          parameter(numberOfChoosenParameters) = parameter(identicalTo)
          computeHeuristic(planStep, parameter, numberOfChoosenParameters + 1, plan)
        } else {
          // ok now we actually have to ground
          val remainingValues = plan.variableConstraints.getRemainingDomain(variableInQuestion).toArray
          var heuristicMin = Double.MaxValue
          var constant = 0
          while (constant < remainingValues.length) {
            parameter(numberOfChoosenParameters) = remainingValues(constant)
            heuristicMin = Math.min(heuristicMin, computeHeuristic(planStep, parameter, numberOfChoosenParameters + 1, plan))
            constant += 1
          }
          heuristicMin
        }
      }
    }

  override def computeHeuristic(plan: EfficientPlan): Double = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = plan.openPreconditions.length // every flaw must be addressed

    // while looking at the TDG we will count the already closed preconditions, which are currently supported by causal links pointing to abstract tasks, again
    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer)) heuristicValue -= 1
      cl += 1
    }


    var i = 1 // init can't have a flaw
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i) && plan.domain.tasks(plan.planStepTasks(i)).isAbstract) {
        // we have to ground here
        heuristicValue += computeHeuristic(i, new Array[Int](plan.planStepParameters(i).length), 0, plan)
      }

      i += 1
    }
    heuristicValue
  }
}
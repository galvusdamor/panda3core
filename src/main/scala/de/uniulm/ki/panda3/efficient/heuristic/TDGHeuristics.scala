package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientGroundTask}
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.DotPrintable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */

trait TDGHeuristics extends MinimisationOverGroundingsBasedHeuristic[Unit] with DotPrintable[Unit] {

  val taskDecompositionTree: EfficientGroundedTaskDecompositionGraph
  val domain               : EfficientDomain

  // memoise the heuristic values for task groundings
  def modificationEfforts: Map[EfficientGroundTask, Double]

  override lazy val dotString: String = dotString(())

  /** The DOT representation of the object with options */
  override def dotString(options: Unit): String = {
    val dotStringBuilder = new StringBuilder()

    dotStringBuilder append "digraph somePlan{\n"
    dotStringBuilder append "\trankdir=\"LR\";"

    val groundTaskToIndex = modificationEfforts.keys.zipWithIndex.toMap
    modificationEfforts foreach { case (gt, h) => dotStringBuilder append ("GT" + groundTaskToIndex(gt) + "[label=\"" + gt.taskID + "|" + h + "\"];") }

    groundTaskToIndex.keys foreach { case from =>
      taskDecompositionTree.graph.andEdges(from) flatMap taskDecompositionTree.graph.orEdges foreach { case to =>
        dotStringBuilder append ("GT" + groundTaskToIndex(from) + " -> GT" + groundTaskToIndex(to) + ";")
      }
    }

    dotStringBuilder append "}"
    dotStringBuilder.toString
  }
}

case class MinimumModificationEffortHeuristic(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, domain: EfficientDomain) extends TDGHeuristics {

  val modificationEfforts: Map[EfficientGroundTask, Double] = taskDecompositionTree.graph.minSumTraversalMap({ groundTask =>
    val task = domain.tasks(groundTask.taskID)
    if (task.initOrGoalTask) 0 else task.precondition.length
                                                                                                             }, sumInitialValue = 1)

  protected def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double = {
    val groundTask = EfficientGroundTask(plan.planStepTasks(planStep), arguments)
    if (!modificationEfforts.contains(groundTask)) Double.MaxValue else modificationEfforts(groundTask)
  }

  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification): (Double, Unit) = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = plan.openPreconditions.length // every flaw must be addressed

    // TODO use this
    //  val supportedByCausalLink = plan.planStepSupportedPreconditions(planStep) contains precondition

    // while looking at the TDG we will count the already closed preconditions, which are currently supported by causal links pointing to abstract tasks, again
    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer) && domain.tasks(plan.planStepTasks(link.consumer)).isAbstract)
        heuristicValue -= 1
      cl += 1
    }

    var i = 2 // init can't have a flaw
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i) && domain.tasks(plan.planStepTasks(i)).isAbstract) {
        // we have to ground here
        heuristicValue += computeHeuristicByGrounding(i, plan)
      }

      i += 1
    }
    (heuristicValue, ())
  }


}

trait TDGPrimitiveActionValueHeuristic extends TDGHeuristics {

  protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double

  protected def deductionForSupportedPrecondition(predicate: Int, arguments: Array[Int]): Double

  // memoize the heuristic values for task groundings
  val modificationEfforts: Map[EfficientGroundTask, Double] =
    taskDecompositionTree.graph.minSumTraversalMap(
      { groundTask =>
        val task = domain.tasks(groundTask.taskID)
        if (task.initOrGoalTask) 0 else computeHeuristicForGroundPrimitive(groundTask.taskID, groundTask.arguments)
      }, sumInitialValue = 0)


  protected def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double = {
    val groundTask = EfficientGroundTask(plan.planStepTasks(planStep), arguments)
    if (!modificationEfforts.contains(groundTask)) Double.MaxValue
    else {
      var heuristicEstimate = modificationEfforts(groundTask)
      val planStepTask = domain.tasks(plan.planStepTasks(planStep))
      val planStepPreconditions = planStepTask.precondition

      var precondition = 0
      while (precondition < planStepPreconditions.length) {
        // look whether this precondition is protected by a causal link
        val supportedByCausalLink = plan.planStepSupportedPreconditions(planStep) contains precondition

        if (supportedByCausalLink) {
          val literalArguments = planStepTask.getArgumentsOfLiteral(arguments, planStepPreconditions(precondition))
          val h = deductionForSupportedPrecondition(planStepPreconditions(precondition).predicate, literalArguments)
          heuristicEstimate -= h
        }
        precondition += 1
      }

      heuristicEstimate
    }
  }

  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification): (Double, Unit) = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = -initialDeductionFromHeuristicValue(plan)

    var i = 2 // init can't have a precondition
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i)) {
        // we have to ground here
        heuristicValue += computeHeuristicByGrounding(i, plan)
      }
      i += 1
    }
    (heuristicValue, ())
  }
}


case class MinimumADDHeuristic(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, addHeuristic: AddHeuristic, domain: EfficientDomain) extends TDGPrimitiveActionValueHeuristic {

  protected def deductionForSupportedPrecondition(predicate: Int, arguments: Array[Int]): Double = addHeuristic.efficientAccessMaps(predicate)(arguments)

  protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double = {
    var heuristicEstimate = 0.0
    val planStepTask = domain.tasks(taskID)
    val planStepPreconditions = planStepTask.precondition

    var precondition = 0
    while (precondition < planStepPreconditions.length) {
      // look whether this precondition is protected by a causal link
      val literalArguments = planStepTask.getArgumentsOfLiteral(arguments, planStepPreconditions(precondition))
      val h = addHeuristic.efficientAccessMaps(planStepPreconditions(precondition).predicate)(literalArguments)
      heuristicEstimate += h
      precondition += 1
    }

    heuristicEstimate
  }

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = 0.0
}

case class MinimumActionCount(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, domain: EfficientDomain) extends TDGPrimitiveActionValueHeuristic {
  override protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double = 1.0

  override protected def deductionForSupportedPrecondition(predicate: Int, arguments: Array[Int]): Double = 0.0

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = plan.numberOfPlanSteps - 2
}
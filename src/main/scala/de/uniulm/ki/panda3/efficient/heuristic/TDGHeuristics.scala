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

  val taskDecompositionTree         : EfficientGroundedTaskDecompositionGraph
  val domain                        : EfficientDomain
  val primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]]

  protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double

  protected def computeHeuristicForGroundMethod(methodID: Int, arguments: Array[Int]): Double

  // memoize the heuristic values for task groundings
  val modificationEfforts: Map[EfficientGroundTask, Double] =
    taskDecompositionTree.graph.minSumTraversalMap(
      { groundTask =>
        val task = domain.tasks(groundTask.taskID)
        if (task.initOrGoalTask) 0 else computeHeuristicForGroundPrimitive(groundTask.taskID, groundTask.arguments)
      }, {
        groundMethod => computeHeuristicForGroundMethod(groundMethod.methodIndex, groundMethod.methodArguments)
      })

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

trait ModificationTDGHeuristic extends TDGHeuristics {

  def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double =
    if (plan.taskOfPlanStep(planStep).isPrimitive && primitiveActionInPlanHeuristic.isDefined) {
      primitiveActionInPlanHeuristic.get.groundingEstimator(plan, planStep, arguments)
    } else {
      val groundTask = EfficientGroundTask(plan.planStepTasks(planStep), arguments)
      if (!modificationEfforts.contains(groundTask)) Double.MaxValue else modificationEfforts(groundTask)
    }

  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = domain.tasks(plan.planStepTasks(1)).precondition.length // every flaw must be addressed

    //println("\nGROUND\nINI " + heuristicValue)
    //println(plan.openPreconditions map {_.planStep} mkString " ")
    // TODO use this
    //  val supportedByCausalLink = plan.planStepSupportedPreconditions(planStep) contains precondition

    // while looking at the TDG we will count the already closed preconditions, which are currently supported by causal links pointing to abstract tasks, again
    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      //println("LINK " + link.producer + " " + link.consumer)
      if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer))
        heuristicValue -= 1
      cl += 1
    }

    //println("INI " + heuristicValue)

    var i = 2 // init can't have a flaw
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i)) {
        //&& domain.tasks(plan.planStepTasks(i)).isAbstract) {
        // we have to ground here
        heuristicValue += computeHeuristicByGrounding(i, plan)
        //println("PS " + i + ": " + computeHeuristicByGrounding(i, plan))
      }

      /*if (plan.isPlanStepPresentInPlan(i) && domain.tasks(plan.planStepTasks(i)).isPrimitive)
        println("PS " + i + ": prim ")*/

      i += 1
    }
    (heuristicValue, ())
  }
}

trait TDGPrimitiveActionValueHeuristic extends TDGHeuristics {

  protected def computeHeuristicForGroundMethod(methodID: Int, arguments: Array[Int]): Double = 0

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double

  protected def deductionForSupportedPrecondition(predicate: Int, arguments: Array[Int]): Double

  def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double = {
    if (plan.taskOfPlanStep(planStep).isPrimitive && primitiveActionInPlanHeuristic.isDefined) {
      primitiveActionInPlanHeuristic.get.groundingEstimator(plan, planStep, arguments)
    } else {
      val groundTask = EfficientGroundTask(plan.planStepTasks(planStep), arguments)
      if (!modificationEfforts.contains(groundTask) && planStep != 1) Double.MaxValue
      else {
        var heuristicEstimate = if (planStep != 1) modificationEfforts(groundTask) else computeHeuristicForGroundPrimitive(groundTask.taskID, groundTask.arguments)
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
  }

  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = -initialDeductionFromHeuristicValue(plan)

    var i = 1 // init can't have a precondition
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


case class MinimumModificationEffortHeuristicWithCycleDetection(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, domain: EfficientDomain,
                                                                primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None)
  extends ModificationTDGHeuristic {
  protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double = domain.tasks(taskID).precondition.length

  protected def computeHeuristicForGroundMethod(methodID: Int, arguments: Array[Int]): Double = 1
}

case class PreconditionRelaxationTDGHeuristic(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, domain: EfficientDomain,
                                              primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends ModificationTDGHeuristic {
  protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double = domain.tasks(taskID).precondition.length

  protected def computeHeuristicForGroundMethod(methodID: Int, arguments: Array[Int]): Double = 1 - domain.decompositionMethods(methodID).subPlan.causalLinks.length
}


case class MinimumADDHeuristic(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, addHeuristic: AddHeuristic, domain: EfficientDomain,
                               primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends TDGPrimitiveActionValueHeuristic {
  protected def deductionForSupportedPrecondition(predicate: Int, arguments: Array[Int]): Double = 1 + addHeuristic.efficientAccessMaps(predicate)(arguments)

  protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double = {
    var heuristicEstimate = 0.0
    val planStepTask = domain.tasks(taskID)
    val planStepPreconditions = planStepTask.precondition

    var precondition = 0
    while (precondition < planStepPreconditions.length) {
      // look whether this precondition is protected by a causal link
      val literalArguments = planStepTask.getArgumentsOfLiteral(arguments, planStepPreconditions(precondition))
      val h = 1 + addHeuristic.efficientAccessMaps(planStepPreconditions(precondition).predicate)(literalArguments)
      heuristicEstimate += h
      precondition += 1
    }

    heuristicEstimate
  }

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = 0
}

case class MinimumActionCount(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, domain: EfficientDomain,
                              primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends TDGPrimitiveActionValueHeuristic {
  override protected def computeHeuristicForGroundPrimitive(taskID: Int, arguments: Array[Int]): Double = 1.0

  override protected def deductionForSupportedPrecondition(predicate: Int, arguments: Array[Int]): Double = 0.0

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = plan.numberOfPrimitivePlanSteps - 1
}
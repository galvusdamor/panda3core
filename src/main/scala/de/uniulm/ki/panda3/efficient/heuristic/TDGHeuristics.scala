package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientGroundTask}
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.util.DotPrintable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait TDGHeuristics extends EfficientHeuristic {
  def taskDecompositionTree: Any
}


case class MinimumModificationEffortHeuristic(taskDecompositionTree: EfficientGroundedTaskDecompositionGraph, domain: EfficientDomain)
  extends MinimisationOverGroundingsBasedHeuristic with DotPrintable[Unit] {

  // memoise the heuristic values for task groundings
  val modificationEfforts: Map[EfficientGroundTask, Double] = taskDecompositionTree.graph.andVertices map { groundTask =>
    groundTask -> taskDecompositionTree.graph.minSumTraversal(groundTask, { groundTask =>
      val task = domain.tasks(groundTask.taskID)
      if (task.initOrGoalTask) 0 else task.precondition.length
    })
  } toMap


  protected def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double = {
    val groundTask = EfficientGroundTask(plan.planStepTasks(planStep),arguments)
    if (!modificationEfforts.contains(groundTask)) Double.MaxValue else modificationEfforts(groundTask)
  }

  override def computeHeuristic(plan: EfficientPlan): Double = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = plan.openPreconditions.length // every flaw must be addressed

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
        heuristicValue += computeHeuristicByGrounding(i, new Array[Int](plan.planStepParameters(i).length), 0, plan)
      }

      i += 1
    }
    heuristicValue
  }

  override val dotString: String = dotString(())

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
package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.efficient.domain.{EfficientDecompositionMethod, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.{Dot2PdfCompiler, SimpleAndOrGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait TSTGHeuristic extends EfficientHeuristic[Unit] {

  def domain: EfficientDomain

  lazy val argumentRelaxedTDG = {
    // methods will be represented by negative numbers to avoid clashes
    val methodsToSubTasks = domain.decompositionMethods.zipWithIndex map { case (m, i) => (-i - 1, m.subPlan.planStepTasks.drop(2) toSet) }
    val tasksToMethods = domain.tasks.zipWithIndex map { case (t, i) => i -> (domain.taskToPossibleMethods(i) map { -_._2 - 1 }).toSet }

    SimpleAndOrGraph[Any, Int, Int](domain.tasks.indices.toSet, domain.decompositionMethods.indices map { case m => -m - 1 } toSet,
                                    tasksToMethods.toMap.withDefaultValue(Set()),
                                    methodsToSubTasks.toMap.withDefaultValue(Set()))
  }

  protected def computeHeuristicForPrimitive(taskID: Int): Double

  protected def computeHeuristicForMethod(methodID: Int): Double

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double

  val taskValues: Map[Int, Int] = argumentRelaxedTDG.minSumTraversalMap(computeHeuristicForPrimitive, computeHeuristicForMethod) map { case (a, b) => a -> b.toInt }

  override def computeHeuristic(plan: EfficientPlan, payload: Unit, appliedModification: EfficientModification): (Double, Unit) = {
    var h = -initialDeductionFromHeuristicValue(plan)
    //println("INI " + h)

    var ps = 2
    while (ps < plan.planStepTasks.length) {
      if (plan.isPlanStepPresentInPlan(ps)) {
        h += taskValues(plan.planStepTasks(ps))
        //println("PS " + ps + ": " + taskValues(plan.planStepTasks(ps)))
      }
      ps += 1
    }

    (h, ())
  }
}


trait DeduceCausalLinksTSTGHeuristic extends TSTGHeuristic {
  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = {
    var deduction = -domain.tasks(plan.planStepTasks(1)).precondition.length
    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer)) {
        deduction += 1
      }
      cl += 1
    }

    deduction
  }
}


case class LiftedMinimumModificationEffortHeuristicWithCycleDetection(domain: EfficientDomain) extends DeduceCausalLinksTSTGHeuristic {
  protected def computeHeuristicForPrimitive(taskID: Int): Double = domain.tasks(taskID).precondition.length

  protected def computeHeuristicForMethod(methodID: Int): Double = 1
}

case class LiftedPreconditionRelaxationTDGHeuristic(domain: EfficientDomain) extends DeduceCausalLinksTSTGHeuristic {
  protected def computeHeuristicForPrimitive(taskID: Int): Double = domain.tasks(taskID).precondition.length

  protected def computeHeuristicForMethod(methodID: Int): Double = 1 - domain.decompositionMethods(-methodID - 1).subPlan.causalLinks.length
}

case class LiftedMinimumActionCount(domain: EfficientDomain) extends TSTGHeuristic {
  override protected def computeHeuristicForPrimitive(taskID: Int): Double = 1

  override protected def computeHeuristicForMethod(methodID: Int): Double = 0

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = plan.numberOfPrimitivePlanSteps - 2
}
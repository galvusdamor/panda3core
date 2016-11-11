package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.SimpleAndOrGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait TSTGHeuristic extends EfficientHeuristic[Unit] {

  def domain: EfficientDomain

  val primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]]

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

  protected def taskValue(plan: EfficientPlan): Int => Int

  override def computeHeuristic(plan: EfficientPlan, payload: Unit, appliedModification: EfficientModification, depth: Int): (Double, Unit) = {
    var h = -initialDeductionFromHeuristicValue(plan)

    val valuation = taskValue(plan)

    var ps = 1
    while (ps < plan.planStepTasks.length) {
      if (plan.isPlanStepPresentInPlan(ps)) {
        if (plan.taskOfPlanStep(ps).isPrimitive && primitiveActionInPlanHeuristic.isDefined)
          h += primitiveActionInPlanHeuristic.get.computeHeuristicByGrounding(ps, plan)
        else
          h += valuation(ps)
      }
      ps += 1
    }

    assert(h >= 0, "TSTG heuristics can never be negative! h = " + h + " " + (-initialDeductionFromHeuristicValue(plan)))

    (h, ())
  }
}

trait PreComputationTSTGHeuristic extends TSTGHeuristic {

  val taskValues: Map[Int, Int] = argumentRelaxedTDG.minSumTraversalMap(computeHeuristicForPrimitive, computeHeuristicForMethod) map { case (a, b) => a -> b.toInt }

  protected def taskValue(plan: EfficientPlan) = { case planStep: Int => taskValues(plan.planStepTasks(planStep)) }
}

trait ReachabilityRecomputingTSTGHeuristic extends TSTGHeuristic {
  protected def taskValue(plan: EfficientPlan) = {
    val taskValues: Map[Int, Int] = argumentRelaxedTDG.minSumTraversalMap(
      {
        task => if ((plan taskAllowed task) || (task == plan.planStepTasks(1))) computeHeuristicForPrimitive(task) else Int.MaxValue
      },
      computeHeuristicForMethod) map { case (a, b) => a -> b.toInt }

    {case planStep: Int => taskValues(plan.planStepTasks(planStep))}
  }
}

trait CausalLinkRecomputingTSTGHeuristic extends TSTGHeuristic {

  assert(domain.noNegativePreconditions)

  protected def taskValue(plan: EfficientPlan) = {
    case planStep =>
      // find all causal links which span over this planstep
      val spanningLinks = plan.causalLinks filter { case EfficientCausalLink(prod, cons, _, _) =>
        plan.isPlanStepPresentInPlan(prod) && plan.isPlanStepPresentInPlan(cons) && plan.ordering.lt(prod, planStep) && plan.ordering.gt(cons, planStep)
      } map { case EfficientCausalLink(prod, _, prodIndex, _) => plan.taskOfPlanStep(prod).effect(prodIndex).predicate } toSet

      val taskValues: Map[Int, Int] = argumentRelaxedTDG.minSumTraversalMap(
        {
          task =>
            if (((plan taskAllowed task) || (task == plan.planStepTasks(1))) && !(domain.tasks(task).negativeEffectPredicates exists spanningLinks)) {
              computeHeuristicForPrimitive(task)
            } else Int.MaxValue
        }, computeHeuristicForMethod) map { case (a, b) => a -> b.toInt }


      taskValues.getOrElse(plan.planStepTasks(planStep), Int.MaxValue)
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


trait LiftedMinimumModificationEffortHeuristicWithCycleDetection extends DeduceCausalLinksTSTGHeuristic {
  protected def computeHeuristicForPrimitive(taskID: Int): Double = {
    assert(domain.tasks(taskID).isPrimitive)
    domain.tasks(taskID).precondition.length
  }

  protected def computeHeuristicForMethod(methodID: Int): Double = 1
}

trait LiftedPreconditionRelaxationTDGHeuristic extends DeduceCausalLinksTSTGHeuristic {
  protected def computeHeuristicForPrimitive(taskID: Int): Double = {
    assert(domain.tasks(taskID).isPrimitive)
    domain.tasks(taskID).precondition.length
  }

  protected def computeHeuristicForMethod(methodID: Int): Double = 1 - domain.decompositionMethods(-methodID - 1).subPlan.causalLinks.length
}

trait LiftedMinimumActionCount extends TSTGHeuristic {
  override protected def computeHeuristicForPrimitive(taskID: Int): Double = 1

  override protected def computeHeuristicForMethod(methodID: Int): Double = 0

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = plan.numberOfPrimitivePlanSteps - 1
}

trait LiftedMinimumADD extends TSTGHeuristic {

  def addHeuristic: AddHeuristic

  lazy val addValuesPerPredicate: Array[Double] =
    domain.predicates.indices map { pred => (addHeuristic.heuristicMap collect { case (grounding, v) if pred == grounding.predicate => v }).toSeq.:+(Integer.MAX_VALUE.toDouble) min } toArray

  override protected def computeHeuristicForPrimitive(taskID: Int): Double = {
    val task = domain.tasks(taskID)
    val precs = task.precondition

    var h = 0.0
    var i = 0
    while (i < precs.length) {
      h += 1 + addValuesPerPredicate(precs(i).predicate)
      i += 1
    }

    h
  }

  override protected def computeHeuristicForMethod(methodID: Int): Double = 0

  protected def initialDeductionFromHeuristicValue(plan: EfficientPlan): Double = {
    // deduct add value for every link literal
    var deduction = 0.0
    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      if (plan.isPlanStepPresentInPlan(link.consumer) && plan.isPlanStepPresentInPlan(link.producer)) {
        deduction += 1 + addValuesPerPredicate(plan.taskOfPlanStep(link.consumer).precondition(link.conditionIndexOfConsumer).predicate)
      }
      cl += 1
    }
    deduction
  }
}

case class PreComputingLiftedMinimumModificationEffortHeuristicWithCycleDetection(domain: EfficientDomain,
                                                                                  primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  PreComputationTSTGHeuristic with LiftedMinimumModificationEffortHeuristicWithCycleDetection

case class PreComputingLiftedPreconditionRelaxationTDGHeuristic(domain: EfficientDomain,
                                                                primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  PreComputationTSTGHeuristic with LiftedPreconditionRelaxationTDGHeuristic

case class PreComputingLiftedMinimumActionCount(domain: EfficientDomain,
                                                primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  PreComputationTSTGHeuristic with LiftedMinimumActionCount

case class PreComputingLiftedMinimumADD(domain: EfficientDomain, addHeuristic: AddHeuristic,
                                        primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  PreComputationTSTGHeuristic with LiftedMinimumADD

case class ReachabilityRecomputingLiftedMinimumModificationEffortHeuristicWithCycleDetection(domain: EfficientDomain,
                                                                                             primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None)
  extends
    ReachabilityRecomputingTSTGHeuristic with LiftedMinimumModificationEffortHeuristicWithCycleDetection

case class ReachabilityRecomputingLiftedPreconditionRelaxationTDGHeuristic(domain: EfficientDomain,
                                                                           primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  ReachabilityRecomputingTSTGHeuristic with LiftedPreconditionRelaxationTDGHeuristic

case class ReachabilityRecomputingLiftedMinimumActionCount(domain: EfficientDomain,
                                                           primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  ReachabilityRecomputingTSTGHeuristic with LiftedMinimumActionCount

case class ReachabilityRecomputingLiftedMinimumADD(domain: EfficientDomain, addHeuristic: AddHeuristic,
                                                   primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  ReachabilityRecomputingTSTGHeuristic with LiftedMinimumADD


case class CausalLinkRecomputingLiftedMinimumModificationEffortHeuristicWithCycleDetection(domain: EfficientDomain,
                                                                                           primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None)
  extends
    CausalLinkRecomputingTSTGHeuristic with LiftedMinimumModificationEffortHeuristicWithCycleDetection

case class CausalLinkRecomputingLiftedPreconditionRelaxationTDGHeuristic(domain: EfficientDomain,
                                                                         primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  CausalLinkRecomputingTSTGHeuristic with LiftedPreconditionRelaxationTDGHeuristic

case class CausalLinkRecomputingLiftedMinimumActionCount(domain: EfficientDomain,
                                                         primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  CausalLinkRecomputingTSTGHeuristic with LiftedMinimumActionCount

case class CausalLinkRecomputingLiftedMinimumADD(domain: EfficientDomain, addHeuristic: AddHeuristic,
                                                 primitiveActionInPlanHeuristic: Option[MinimisationOverGroundingsBasedHeuristic[Unit]] = None) extends
  CausalLinkRecomputingTSTGHeuristic with LiftedMinimumADD

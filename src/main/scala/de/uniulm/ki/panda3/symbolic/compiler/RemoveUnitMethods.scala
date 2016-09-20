package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.updates.ExchangePlanSteps
import de.uniulm.ki.util._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * Removes all unit methods from the domain by compilation. Only accepts grounded domains
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object RemoveUnitMethods extends DecompositionMethodTransformer[Unit] {

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, unit : Unit): Seq[DecompositionMethod] = {

    val (unitMethods, nonUnitMethods) = methods partition { _.subPlan.planStepsWithoutInitGoal.size == 1 }
    val oneStepReplacementRules: Map[Task, Seq[Task]] = unitMethods map { case SimpleDecompositionMethod(abstractTask, subPlan, _) =>
      (abstractTask, subPlan.planStepsWithoutInitGoal.head.schema)
    } groupBy { _._1 } map { case (a, b) => a -> (b map { _._2 }) } toMap

    def expand(current: Map[Task, Seq[Task]]): Map[Task, Seq[Task]] = {
      val expansion = current map { case (from, toList) =>
        val nextStep = toList filter current.contains flatMap current
        (from, (toList ++ nextStep) distinct)
      }
      if (expansion == current) current else expand(expansion)
    }

    val replacementRules: Map[Task, Seq[Task]] = expand(oneStepReplacementRules)

    (nonUnitMethods :+ topMethod) flatMap { case SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
      val replaceablePlanSteps = subPlan.planStepsWithoutInitGoal filter { replacementRules contains _.schema }
      val replacementPossibilities: Seq[Seq[PlanStep]] = allSubsets(replaceablePlanSteps)

      val allPlanStepSubstitutions = replacementPossibilities flatMap { planStepsToReplace =>
        planStepsToReplace.foldLeft[Seq[Seq[(PlanStep, PlanStep)]]](Nil :: Nil)(
          { case (mappings, planStep) => replacementRules(planStep.schema) flatMap { newTask => mappings map { mapping => (planStep, PlanStep(planStep.id, newTask, Nil)) +: mapping } } })
      }

      allPlanStepSubstitutions map { substitution =>
        SimpleDecompositionMethod(abstractTask, subPlan update ExchangePlanSteps(substitution.toMap), methodName)
      }
    }
  }

  override protected val transformationName: String = "unitMethod"

  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    assert(domain.predicates forall { _.argumentSorts.isEmpty })
    super.transform(domain, plan, info)
  }
}
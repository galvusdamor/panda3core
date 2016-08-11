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
object RemoveUnitMethods extends DomainTransformer[Unit] {

  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    assert(domain.predicates forall { _.argumentSorts.isEmpty })

    val (unitMethods, nonUnitMethods) = domain.decompositionMethods partition { _.subPlan.planStepsWithoutInitGoal.size == 1 }
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


    // create an artificial method
    val initAndGoalNOOP = ReducedTask("__noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))
    val topInit = PlanStep(plan.init.id, initAndGoalNOOP, Nil)
    val topGoal = PlanStep(plan.goal.id, initAndGoalNOOP, Nil)

    val topPlanTasks = plan.planStepsAndRemovedPlanStepsWithoutInitGoal :+ topInit :+ topGoal
    val initialPlanInternalOrderings = plan.orderingConstraints.originalOrderingConstraints filterNot { _.containsAny(plan.initAndGoal: _*) }
    val topOrdering = TaskOrdering(initialPlanInternalOrderings ++ OrderingConstraint.allBetween(topInit, topGoal, plan.planStepsAndRemovedPlanStepsWithoutInitGoal: _*), topPlanTasks)
    val initialPlanWithout = Plan(topPlanTasks, plan.causalLinksAndRemovedCausalLinks, topOrdering, plan.variableConstraints, topInit, topGoal,
                                  plan.isModificationAllowed,
                                  plan.isFlawAllowed, plan.planStepDecomposedByMethod, plan.planStepParentInDecompositionTree)

    val topTask = ReducedTask("__unitCompilation__top", isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil))
    val topMethod = SimpleDecompositionMethod(topTask, initialPlanWithout, "__top")

    val extendedMethods: Seq[DecompositionMethod] = (nonUnitMethods :+ topMethod) flatMap { case SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
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

    if ((extendedMethods count { _.abstractTask == topTask }) == 1) {
      // we don't need to alter the plan
      (domain.copy(decompositionMethods = extendedMethods filterNot { _.abstractTask == topTask }), plan)
    } else {
      // generate a new
      val topPS = PlanStep(2, topTask, Nil)
      val planSteps: Seq[PlanStep] = plan.init :: plan.goal :: topPS :: Nil
      val ordering = TaskOrdering(OrderingConstraint.allBetween(plan.init, plan.goal, topPS), planSteps)
      val initialPlan = Plan(planSteps, Nil, ordering, plan.variableConstraints, plan.init, plan.goal, plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())

      (domain.copy(decompositionMethods = extendedMethods, tasks = domain.tasks :+ topTask), initialPlan)
    }
  }
}
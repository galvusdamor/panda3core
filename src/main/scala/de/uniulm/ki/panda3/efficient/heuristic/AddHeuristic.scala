package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientGroundTask}
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraph
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.logic.EfficientGroundLiteral

import scala.collection.mutable.ArrayBuffer

/**
  * standard ADD-heuristic according to Young & Simons
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class AddHeuristic(planningGraph: EfficientGroundedPlanningGraph, domain: EfficientDomain, initialState: Array[(Int, Array[Int])]) extends MinimisationOverGroundingsBasedHeuristic {

  private val heuristicMapping: Map[EfficientGroundLiteral, Double] =
    (planningGraph.actionLayer zip planningGraph.stateLayer).foldLeft(initialState map { case (predicate, args) => EfficientGroundLiteral(predicate, isPositive = true, args) -> 0.0 } toMap)(
      {
        case (initiallyComputedValues, (actions, stateFeatures)) =>
          // apply all actions
          actions.foldLeft(initiallyComputedValues)({ case (computedValues, (task, arguments)) =>
            val groundTask = EfficientGroundTask(task, arguments)

            // determine the total cost for this action
            var actionCost = 1.0

            var precondition = 0
            while (precondition < domain.tasks(task).precondition.length) {
              actionCost += computedValues(groundTask.substitutedPrecondition(precondition, domain))
              precondition += 1
            }


            val newPredicateCosts: ArrayBuffer[(EfficientGroundLiteral, Double)] = new ArrayBuffer[(EfficientGroundLiteral, Double)]()

            var effect = 0
            while (effect < domain.tasks(task).effect.length) {
              val effectLiteral = groundTask.substitutedEffect(effect, domain)
              if (effectLiteral.isPositive && (!computedValues.contains(effectLiteral) || computedValues(effectLiteral) > actionCost))
                newPredicateCosts append ((effectLiteral, actionCost))
              effect += 1
            }

            computedValues ++ newPredicateCosts.toArray
                                                    })
      })

  /*heuristicMapping foreach {case (lit,v) =>
    println(lit.predicate + (lit.arguments.mkString("(",",",")")) + " -> " + v)
  }*/

  protected def groundingEstimator(groundTask: EfficientGroundTask, plan: EfficientPlan, planStep: Int): Double = {
    var heuristicEstimate = 0.0
    val planStepPreconditions = domain.tasks(plan.planStepTasks(planStep)).precondition

    var precondition = 0
    while (precondition < planStepPreconditions.length) {
      // look whether this precondition is protected by a causal link
      // TODO we should to this more efficiently by memoizing it in the plan itself
      var supportedByCausalLink = false
      var cl = 0
      while (cl < plan.causalLinks.length) {
        val link = plan.causalLinks(cl)
        if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer))
          if (link.consumer == planStep && link.conditionIndexOfConsumer == precondition)
            supportedByCausalLink = true
        cl += 1
      }

      if (!supportedByCausalLink) heuristicEstimate += heuristicMapping(groundTask.substitutedPrecondition(precondition, domain))

      precondition += 1
    }
    heuristicEstimate
  }

  override def computeHeuristic(plan: EfficientPlan): Double = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = plan.openPreconditions.length // every flaw must be addressed

    var i = 2 // init doesn't have effects
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i)) {
        // we have to ground here
        heuristicValue += computeHeuristicByGrounding(i, new Array[Int](plan.planStepParameters(i).length), 0, plan)
      }

      i += 1
    }
    heuristicValue
  }
}
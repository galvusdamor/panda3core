package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientGroundTask}
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraph
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.logic.EfficientGroundLiteral
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.BucketAccessMap

import scala.collection.mutable.ArrayBuffer

/**
  * standard ADD-heuristic according to Young & Simons
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class AddHeuristic(planningGraph: EfficientGroundedPlanningGraph, domain: EfficientDomain, initialState: Array[(Int, Array[Int])],
                        resuingAsVHPOP: Boolean) extends MinimisationOverGroundingsBasedHeuristic[Unit] {

  private val heuristicMap: Map[EfficientGroundLiteral, Double] =
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
      }) withDefault { _ => Double.MaxValue }

  /*heuristicMapping foreach {case (lit,v) =>
    println(lit.predicate + (lit.arguments.mkString("(",",",")")) + " -> " + v)
  }*/

  // TODO used to be pricate
  val efficientAccessMaps = domain.predicates.indices map { case p =>
    val literals = heuristicMap filter { _._1.predicate == p }

    BucketAccessMap(literals map { case (k, v) => (k.arguments, v) })
  }

  var foo = 0

  protected def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double = {
    var heuristicEstimate = 0.0
    foo += 1
    val planStepTask = domain.tasks(plan.planStepTasks(planStep))
    val planStepPreconditions = planStepTask.precondition

    var precondition = 0
    //println("CL " + plan.causalLinks.length * planStepPreconditions.length)
    while (precondition < planStepPreconditions.length) {
      // look whether this precondition is protected by a causal link
      val supportedByCausalLink = plan.planStepSupportedPreconditions(planStep) contains precondition

      if (!supportedByCausalLink) {
        // if resuing include only if we can't support it
        // TODO this is only correct if we are grounded
        val possibleSupporter = plan.potentialSupportersOfPlanStepPreconditions(planStep)(precondition).iterator
        var potentialSupporterFound = false
        while (possibleSupporter.hasNext) {
          val supporter = possibleSupporter.next()
          if (!plan.ordering.gt(supporter, planStep))
            potentialSupporterFound = true
        }
        if (!resuingAsVHPOP || !potentialSupporterFound) {
          val literalArguments = planStepTask.getArgumentsOfLiteral(arguments, planStepPreconditions(precondition))
          val h = efficientAccessMaps(planStepPreconditions(precondition).predicate)(literalArguments)
          heuristicEstimate += h
        }
      }

      precondition += 1
    }
    heuristicEstimate
  }

  override def computeHeuristic(plan: EfficientPlan, unit : Unit, mod : EfficientModification): (Double,Unit) = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = plan.openPreconditions.length // every flaw must be addressed
    foo = 0
    //println("ACTIONS " + plan.numberOfPlanSteps)
    //println("CLs " + plan.causalLinks.length)
    val startTime = System.currentTimeMillis()
    var i = 2 // init doesn't have effects
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i)) {
        // we have to ground here
        heuristicValue += computeHeuristicByGrounding(i, new Array[Int](plan.planStepParameters(i).length), 0, plan)
      }

      i += 1
    }
    //println("HEURISTIC " + heuristicValue + " took " + (System.currentTimeMillis() - startTime) + " groundings "  + foo)
    (heuristicValue,())
  }
}
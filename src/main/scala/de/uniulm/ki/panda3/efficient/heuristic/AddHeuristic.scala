// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientGroundTask}
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraph
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.logic.EfficientGroundLiteral
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util._

import scala.collection.mutable.ArrayBuffer

/**
  * standard ADD-heuristic according to Young & Simons
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class AddHeuristic(planningGraph: EfficientGroundedPlanningGraph, domain: EfficientDomain, initialState: Array[(Int, Array[Int])],
                        resuingAsVHPOP: Boolean) extends MinimisationOverGroundingsBasedHeuristic[Unit] {

  val heuristicMap: Map[EfficientGroundLiteral, Double] =
    (planningGraph.actionLayer zip planningGraph.stateLayer)
      .foldLeft(initialState map { case (predicate, args) => EfficientGroundLiteral(predicate, isPositive = true, args) -> 0.0 } toMap)(
        {
          case (initiallyComputedValues, (actions, _)) =>
            // apply all actions
            actions.distinct.foldLeft(initiallyComputedValues)({ case (computedValues, (task, arguments)) =>
              val groundTask = EfficientGroundTask(task, arguments)

              // determine the total cost for this action
              var actionCost = 1.0

              var precondition = 0
              while (precondition < domain.tasks(task).precondition.length) {
                actionCost += computedValues(groundTask.substitutedPrecondition(precondition, domain))
                precondition += 1
              }


              val newPredicateCosts: ArrayBuffer[(EfficientGroundLiteral, Double)] = new ArrayBuffer[(EfficientGroundLiteral, Double)]()
              val removePredicateCosts: ArrayBuffer[EfficientGroundLiteral] = new ArrayBuffer[EfficientGroundLiteral]()

              var effect = 0
              while (effect < domain.tasks(task).effect.length) {
                val effectLiteral = groundTask.substitutedEffect(effect, domain)
                if (effectLiteral.isPositive && !computedValues.contains(effectLiteral)) {
                  newPredicateCosts append ((effectLiteral, actionCost))
                }

                if (effectLiteral.isPositive && computedValues.contains(effectLiteral) && computedValues(effectLiteral) > actionCost) {
                  removePredicateCosts append effectLiteral
                  newPredicateCosts append ((effectLiteral, actionCost))
                }

                effect += 1
              }

              (computedValues -- removePredicateCosts) ++ newPredicateCosts.toArray
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

  def groundingEstimator(plan: EfficientPlan, planStep: Int, arguments: Array[Int]): Double = {
    var heuristicEstimate = 0.0
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

  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = {
    // accumulate for all actions in the plan
    var heuristicValue: Double = 0 // plan.openPreconditions.length // every flaw must be addressed

    var i = 1 // init doesn't have effects
    while (i < plan.numberOfAllPlanSteps) {
      if (plan.isPlanStepPresentInPlan(i)) {
        // we have to ground here
        heuristicValue += computeHeuristicByGrounding(i, plan)
      }

      i += 1
    }
    (heuristicValue, ())
  }

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

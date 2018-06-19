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

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraph
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.InformationCapsule

import scala.collection.{mutable, BitSet}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class RelaxHeuristic(planningGraph: EfficientGroundedPlanningGraph, domain: EfficientDomain, initialState: Array[(Int, Array[Int])])
  extends EfficientHeuristic[Unit] {

  assert(domain.isGround)

  val initialStateBitSet = BitSet(initialState map { _._1 }: _*)

  val predicatesToLayerAndAchievingActions: Map[Int, (Int, BitSet)] = domain.predicates.indices map { predicate =>
    val firstIndex = planningGraph.stateLayer indexWhere { _ exists { _._1 == predicate } }
    if (firstIndex != -1) {
      val achievingActions = planningGraph.actionLayer(firstIndex) filter { case (t, _) => domain.tasks(t).effect exists { case EfficientLiteral(p, pos, _) => pos && p == predicate } }
      if (!(initialState exists { _._1 == predicate }))
        assert(achievingActions.nonEmpty)

      predicate ->(firstIndex, BitSet(achievingActions map { _._1 }: _*))
    } else predicate ->(-1, BitSet())
  } toMap


  override def computeHeuristic(plan: EfficientPlan, payload: Unit, appliedModification: Option[EfficientModification], depth: Int, oldHeuristic: Double,
                                informationCapsule: InformationCapsule): (Double, Unit) = {
    // gather all open preconditions
    val conditions = mutable.BitSet()
    val planActions = mutable.BitSet()
    var planStep = 1 // init doesn't have effects
    while (planStep < plan.numberOfAllPlanSteps) {
      planActions add plan.planStepTasks(planStep)

      if (plan.isPlanStepPresentInPlan(planStep)) {
        val planStepPreconditions = domain.tasks(plan.planStepTasks(planStep)).precondition
        var precondition = 0
        while (precondition < planStepPreconditions.length) {
          // look whether this precondition is protected by a causal link
          val supportedByCausalLink = plan.planStepSupportedPreconditions(planStep) contains precondition
          if (!supportedByCausalLink) conditions add planStepPreconditions(precondition).predicate
          precondition += 1
        }
      }
      planStep += 1
    }

    val queue = mutable.PriorityQueue[(Int, Int)]()
    val conditionIterator = conditions.iterator
    while (conditionIterator.hasNext) {
      val predicate = conditionIterator.next()
      queue += ((predicatesToLayerAndAchievingActions(predicate)._1, predicate))
    }

    var h = 0.0
    while (queue.nonEmpty) {
      val (_, predicate) = queue.dequeue()
      if ((conditions contains predicate) && !(initialStateBitSet contains predicate)) {
        val (_, actions) = predicatesToLayerAndAchievingActions(predicate)

        val potentialAction = actions find planActions
        val selectedAction = potentialAction.getOrElse(actions.head)
        if (potentialAction.isEmpty) h += 1

        val task = domain.tasks(selectedAction)

        // remove all effects
        var effect = 0
        while (effect < task.effect.length) {
          val predicate = task.effect(effect).predicate
          if (conditions contains predicate) conditions remove predicate
          effect += 1
        }

        // add all preconditions
        var precondition = 0
        while (precondition < task.precondition.length) {
          val predicate = task.precondition(precondition).predicate
          if (!(conditions contains predicate)) {
            conditions add predicate
            queue += ((predicatesToLayerAndAchievingActions(predicate)._1, predicate))
          }
          precondition += 1
        }
      }
    }

    (h, ())
  }

  def computeInitialPayLoad(plan: EfficientPlan) : Unit = ()
}

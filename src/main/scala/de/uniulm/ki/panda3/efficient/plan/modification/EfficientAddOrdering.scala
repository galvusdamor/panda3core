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

package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientAddOrdering(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, before: Int, after: Int) extends EfficientModification {
  assert(plan == null || plan.isPlanStepPresentInPlan(before))
  assert(plan == null || plan.isPlanStepPresentInPlan(after))
  override val nonInducedAddedOrderings: Array[(Int, Int)] = Array((before, after))

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification = EfficientAddOrdering(null, severedFlaw, before, after)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Add Ordering: " + before + " < " + after
}

object EfficientAddOrdering {
  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, causalLink: EfficientCausalLink, threater: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    if (!plan.ordering.lt(threater, causalLink.consumer)) buffer append EfficientAddOrdering(plan, resolvedFlaw, causalLink.consumer, threater)
    if (!plan.ordering.lt(causalLink.producer, threater)) buffer append EfficientAddOrdering(plan, resolvedFlaw, threater, causalLink.producer)
    buffer.toArray
  }

  def estimate(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, causalLink: EfficientCausalLink, threater: Int): Int = {
    var numberOfModifications = 0
    if (!plan.ordering.lt(threater, causalLink.consumer)) numberOfModifications += 1
    if (!plan.ordering.lt(causalLink.producer, threater)) numberOfModifications += 1
    numberOfModifications
  }
}

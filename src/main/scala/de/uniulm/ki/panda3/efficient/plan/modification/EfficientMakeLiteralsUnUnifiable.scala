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

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientMakeLiteralsUnUnifiable(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, variable1: Int, variable2: Int) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = Array(EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, variable1, variable2))

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification = EfficientMakeLiteralsUnUnifiable(null, severedFlaw, variable1, variable2)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Make variables non-unifyable " + variable1 + " != " + variable2
}

object EfficientMakeLiteralsUnUnifiable {
  def apply(plan: EfficientPlan, flaw: EfficientFlaw, literal1Parameters: Array[Int], literal2Parameters: Array[Int]): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    var i = 0
    while (i < literal1Parameters.length) {
      if (!plan.variableConstraints.areEqual(literal1Parameters(i), literal2Parameters(i)))
        buffer append EfficientMakeLiteralsUnUnifiable(plan, flaw, literal1Parameters(i), literal2Parameters(i))
      i += 1
    }
    buffer.toArray
  }

  def estimate(plan: EfficientPlan, flaw: EfficientFlaw, literal1Parameters: Array[Int], literal2Parameters: Array[Int]): Int = {
    var numberOfModifications = 0
    var i = 0
    while (i < literal1Parameters.length) {
      if (!plan.variableConstraints.areEqual(literal1Parameters(i), literal2Parameters(i))) numberOfModifications += 1
      i += 1
    }
    numberOfModifications
  }
}

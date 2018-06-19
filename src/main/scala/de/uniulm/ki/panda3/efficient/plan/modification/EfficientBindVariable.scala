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
case class EfficientBindVariable(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, variable: Int, constant: Int) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALCONSTANT, variable, constant))

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification = EfficientBindVariable(null, severedFlaw, variable, constant)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Bind variable to value: " + variable + " = " + constant
}

object EfficientBindVariable {
  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, variable: Int): Array[EfficientModification] = {
    val modificationBuffer = new ArrayBuffer[EfficientModification]()
    val it = plan.variableConstraints.getRemainingDomain(variable).iterator
    while (it.hasNext) {
      val constant = it.next()
      modificationBuffer append EfficientBindVariable(plan, resolvedFlaw, variable, constant)
    }
    modificationBuffer.toArray
  }

  def estimate(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, variable: Int): Int = plan.variableConstraints.getRemainingDomain(variable).size
}

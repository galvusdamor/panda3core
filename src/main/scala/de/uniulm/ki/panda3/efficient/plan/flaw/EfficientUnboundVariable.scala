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

package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientBindVariable, EfficientModification}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientUnboundVariable(plan: EfficientPlan, variable: Int) extends EfficientFlaw {
  override lazy val resolver: Array[EfficientModification] = EfficientBindVariable(plan, this, variable)

  def severLinkToPlan: EfficientUnboundVariable = EfficientUnboundVariable(null, variable)

  def equalToSeveredFlaw(flaw: EfficientFlaw) : Boolean = if (flaw.isInstanceOf[EfficientUnboundVariable]) {
    val euv = flaw.asInstanceOf[EfficientUnboundVariable]
    euv.variable == variable
  } else false

  override lazy val estimatedNumberOfResolvers: Int = EfficientBindVariable.estimate(plan,this,variable)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Unbound Variable " + variable
}

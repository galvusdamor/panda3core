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

package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.{NotEqual, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class MakeLiteralsUnUnifiable(plan: Plan, constraint: NotEqual) extends Modification {
  override def addedVariableConstraints: Seq[VariableConstraint] = constraint :: Nil
}

object MakeLiteralsUnUnifiable {
  def apply(plan: Plan, a: Literal, b: Literal): Seq[MakeLiteralsUnUnifiable] = (a !?! b)(plan.variableConstraints) map { c => MakeLiteralsUnUnifiable(plan, c)}
}

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

package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.modification.{DecomposePlanStep, Modification}

/**
 * Represents the flaw of a plan containing a still abstract task
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AbstractPlanStep(plan: Plan, ps: PlanStep) extends Flaw {

  override def computeAllResolvents(domain: Domain): Seq[Modification] = DecomposePlanStep(plan, ps, domain)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "AbstractPlanStep: " + ps.shortInfo

}

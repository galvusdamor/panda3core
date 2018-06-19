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

package de.uniulm.ki.panda3.symbolic.plan.element

import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class OrderingConstraint(before: PlanStep, after: PlanStep) extends DomainUpdatable {
  def contains(ps: PlanStep): Boolean = before == ps || after == ps

  def containsAny(ps: PlanStep*): Boolean = (ps map contains).fold(false)({ case (a, b) => a || b })

  override def update(domainUpdate: DomainUpdate): OrderingConstraint = OrderingConstraint(before.update(domainUpdate), after.update(domainUpdate))
}

object OrderingConstraint {

  def allBetween(first: PlanStep, last: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] =
    (steps flatMap { ps => OrderingConstraint(first, ps) :: OrderingConstraint(ps, last) :: Nil }) :+ OrderingConstraint(first, last)

  def allAfter(first: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] = steps map { OrderingConstraint(first, _) }

  def allBefore(first: PlanStep, steps: PlanStep*): Seq[OrderingConstraint] = steps map { OrderingConstraint(_, first) }
}

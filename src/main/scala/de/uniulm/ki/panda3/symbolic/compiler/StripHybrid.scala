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

package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{Task, ReducedTask, Domain}
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeTask, DeleteCausalLinks}
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object StripHybrid extends DomainTransformer[Unit] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = if (domain.isHybrid) {
    // remove all causal links
    val noLinksDomain = domain update DeleteCausalLinks
    val noLinksProblem = plan update DeleteCausalLinks

    // remove all preconditions and effects of abstract tasks
    val replacementMap: Map[Task, Task] =
      domain.abstractTasks map { t => t -> ReducedTask(t.name, isPrimitive = false, t.parameters, t.artificialParametersRepresentingConstants, t.parameterConstraints, And(Nil), And(Nil),
                                                       t.cost)
      } toMap

    (noLinksDomain update ExchangeTask(replacementMap), noLinksProblem update ExchangeTask(replacementMap))
  } else (domain,plan)

}

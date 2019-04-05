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

package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemoveEffects, RemovePredicate}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PrunePredicates extends DomainTransformer[Set[String]] {

  override def transform(domain: Domain, plan: Plan, predicatesToKeep: Set[String]): (Domain, Plan) = {
    val unnecessaryPredicates = domain.predicates filter { p =>
      // it might be true and cannot be made false
      if ((plan.groundInitialStateOnlyPositivesSetOnlyPredicates contains p) && domain.producersOfPosNeg(p)._2.isEmpty)
        true
      else if (!(plan.groundInitialStateOnlyPositivesSetOnlyPredicates contains p) && domain.consumersOf(p).isEmpty &&
        !plan.goal.schema.posPreconditionAsPredicateSet.contains(p) && domain.producersOfPosNeg(p)._1.isEmpty)
        true
      else
        false
    } filterNot { p => predicatesToKeep contains p.name.split("\\[").head }

    if (unnecessaryPredicates.isEmpty) (domain, plan)
    else {
      (domain update RemovePredicate(unnecessaryPredicates.toSet), plan update RemovePredicate(unnecessaryPredicates.toSet))
    }
  }
}

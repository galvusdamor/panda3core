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
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemovePredicate, RemoveEffects}
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.{Predicate, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneEffects extends DomainTransformer[Set[Task]] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, reachableTasks: Set[Task]): (Domain, Plan) = {
    // determine which predicates (with negation) are actually necessary
    val necessaryPredicates: Set[(Predicate, Boolean)] = (reachableTasks + plan.goal.schema) flatMap {
      case task: ReducedTask => task.precondition.conjuncts flatMap { case Literal(predicate, isPositive, _) => (predicate, true) ::(predicate, false) :: Nil }
      case _                 => noSupport(FORUMLASNOTSUPPORTED)
    }

    val unnecessaryPredicatesWithSign = (domain.predicates flatMap { p => (p, true) ::(p, false) :: Nil } filterNot necessaryPredicates.contains).toSet
    val unnecessaryPredicates = domain.predicates filter { p => (p, true) ::(p, false) :: Nil forall unnecessaryPredicatesWithSign.contains } toSet
    val effectUpdate = RemoveEffects(unnecessaryPredicatesWithSign, invertedTreatment = false)
    val predicateUpdate = RemovePredicate(unnecessaryPredicates)

    val newDom = domain update effectUpdate update predicateUpdate
    val newPlan = plan update effectUpdate update predicateUpdate

     (newDom,  newPlan)
  }
}

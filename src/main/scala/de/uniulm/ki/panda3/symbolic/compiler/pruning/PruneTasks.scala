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
import de.uniulm.ki.panda3.symbolic.domain.{SASPlusRepresentation, Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * Removes a given set of tasks from the domain, entailed changes (like removing decomposition methods) are _not_ performed.s
  *
  * Tasks will _not_ be removed from the initial plan
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneTasks extends DomainTransformer[Set[Task]] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, removedTasks: Set[Task]): (Domain, Plan) = {
    val reducedDomain = Domain(domain.sorts, domain.predicates, domain.tasks filterNot removedTasks.contains,
                               domain.decompositionMethods filterNot { _.containsAnyFrom(removedTasks) }, domain.decompositionAxioms,
                               domain.costValues,
                               domain.mappingToOriginalGrounding,
                               domain.sasPlusRepresentation map { case SASPlusRepresentation(p, map1, map2) =>
                                 SASPlusRepresentation(p, map1 filterNot { case (i, t) => removedTasks contains t }, map2)
                               })

    (reducedDomain, plan)
  }
}

object PruneUselessAbstractTasks extends DomainTransformer[Unit] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, unit: Unit): (Domain, Plan) = {
    val uselessAbstractTasksNoDecomposition = (domain.abstractTasks filterNot { at => domain.methodsForAbstractTasks contains at }).toSet
    val tasksInMethods: Set[Task] = ((domain.decompositionMethods map { _.subPlan }) :+ plan) flatMap { _.planStepsAndRemovedPlanSteps map { _.schema } } toSet
    val uselessAbstractTasksNeverOccurring = (domain.abstractTasks filterNot tasksInMethods.contains).toSet

    val uselessAbstractTasks = (uselessAbstractTasksNoDecomposition ++ uselessAbstractTasksNeverOccurring) filterNot plan.planStepSchemaArray.contains

    val reducedDomain = Domain(domain.sorts, domain.predicates, domain.tasks filterNot uselessAbstractTasks.contains,
                               domain.decompositionMethods filter { m =>
                                 !uselessAbstractTasks.contains(m.abstractTask) &&
                                   m.subPlan.planStepTasksSet.forall { rt => !uselessAbstractTasks.contains(rt) }
                               },
                               domain.decompositionAxioms,
                               domain.costValues,
                               domain.mappingToOriginalGrounding, domain.sasPlusRepresentation)
    (reducedDomain, plan)
  }
}

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

package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.util.{DirectedGraphWithAlgorithms, DirectedGraph}

/**
  * This is a simplified version of the TDG (task decomposition graph) which does not take variable constraints into account.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TaskSchemaTransitionGraph(domain: Domain) extends DirectedGraphWithAlgorithms[Task] {

  /** a list of all node of the graph */
  override val vertices: Seq[Task] = domain.tasks

  /** describes which tasks can be obtained from a given task by applying a given decomposition method */
  val canBeDirectlyDecomposedIntoVia: Map[Task, Set[(DecompositionMethod, Task)]] = (domain.tasks map { case task => (task,
    (domain.methodsForAbstractTasks.getOrElse(task, Nil) flatMap { case method => method.subPlan.planStepsWithoutInitGoal.map { case ps => (method, ps.schema) } }).toSet)
  }).toMap
  // assertion for the decomposition via
  canBeDirectlyDecomposedIntoVia foreach { case (task, setofDecomps) => setofDecomps foreach { case (method, _) => assert(method.abstractTask == task) } }

  val canBeDirectlyDecomposedInto: Map[Task, Seq[Task]] = canBeDirectlyDecomposedIntoVia map { case (t, tasks) => (t, tasks.toSeq map { _._2 }) }

  /** adjacency list of the graph */
  override val edges: Map[Task, Seq[Task]] = canBeDirectlyDecomposedInto


  lazy val canBeDecomposedIntoVia: Map[Task, Seq[(DecompositionMethod, Task)]] = canBeDirectlyDecomposedIntoVia map { case (task, directDecomps) => (task, directDecomps
    .toSeq flatMap { case (method, subtask) => (reachable(subtask) + subtask) map { (method, _) } })
  }

  /** the boolean states whether the preciate can be produced negated or positive */
  lazy val canBeDirectlyDecomposedIntoForPredicate: Map[(Task, Predicate, Boolean), Seq[DecompositionMethod]] = canBeDecomposedIntoVia flatMap { case (task, set) =>
    (for (pred <- domain.predicates; positive <- true :: false :: Nil) yield (pred, positive)) map { case (pred, positive) =>
      val targetDecompositions = set filter {
        case (method, reducedTask: ReducedTask) => reducedTask.effect.conjuncts exists { eff => eff.predicate == pred && eff.isPositive == positive }
        case _                                  => noSupport(FORUMLASNOTSUPPORTED)
      } map { _._1 }
      (task, pred, positive) -> targetDecompositions.distinct
    }
  }
}

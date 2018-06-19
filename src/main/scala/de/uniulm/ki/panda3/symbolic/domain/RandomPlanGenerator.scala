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

package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.Plan

import scala.util.Random

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class RandomPlanGenerator(domain: Domain, plan: Plan) {
  // this currently only works for grounded domains
  assert(domain.predicates forall { _.argumentSorts.isEmpty })

  def randomExecutablePlan(length: Int, seed: Int): Seq[Task] = {
    val r = new Random(seed)

    def randomWalk(state: Set[Predicate], length: Int): Option[Seq[Task]] = if (length == 0) Some(Nil)
    else {
      // all applicable tasks
      val applicableTasks = domain.primitiveTasks filter {
        case rt: ReducedTask => rt.precondition.conjuncts forall { case Literal(predicate, isPositive, _) => (state contains predicate) == isPositive }
        case _               => false
      } map { t => (t, r.nextInt()) } sortBy { _._2 } map { _._1 }

      if (applicableTasks.isEmpty) None
      else applicableTasks.foldLeft[Option[Seq[Task]]](None)(
        {
          case (Some(x), _) => Some(x)
          case (None, task) =>
            val delEffects = task.asInstanceOf[ReducedTask].effect.conjuncts collect { case x if !x.isPositive => x.predicate } toSet

            val addEffects = task.asInstanceOf[ReducedTask].effect.conjuncts collect { case x if x.isPositive => x.predicate } toSet

            val resultState = (state diff delEffects) ++ addEffects

            randomWalk(resultState, length - 1) match {
              case Some(seq) => Some(task +: seq)
              case None      => None
            }
        })
    }

    randomWalk(plan.init.substitutedEffects collect { case l if l.isPositive => l.predicate } toSet, length).get
  }
}

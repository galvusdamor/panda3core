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

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class KautzSelman(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, intProblem: IntProblem,
                       taskSequenceLengthQQ: Int) extends LinearPrimitivePlanEncoding with EncodingWithLinearPlan {
  override lazy val offsetToK = 0

  override lazy val overrideK = Some(0)

  override lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  override val numberOfChildrenClauses = 0 // none

  override val expansionPossible = Math.pow(2, domain.predicates.length) > taskSequenceLength

  override val decompositionFormula = Nil

  override val givenActionsFormula = Nil

  override val noAbstractsFormula = Nil

  override lazy val stateTransitionFormula: Seq[Clause] = stateTransitionFormulaOfLength(taskSequenceLength) ++
    Range(0, taskSequenceLength).flatMap(position => atMostOneOf(domain.primitiveTasks map { action(K - 1, position, _) }))

  override lazy val numberOfPrimitiveTransitionSystemClauses = stateTransitionFormula.length

    override lazy val goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  println("Kautz-Selman, plan length: " + taskSequenceLength)
}

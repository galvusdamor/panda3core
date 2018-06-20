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

package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan, LinearPrimitivePlanEncoding}

/**
  * forces the minimum common subplan of the solution and the reference plan to be at least of length K
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LongestCommonSubplan(referencePlan: Seq[Task], minimumLength: Int, ignoreOrder: Boolean) extends MatchingBasedConstraints {


  def matchPathAndReference(pathPosition: Int, referencePosition: Int): String = "match_" + pathPosition + "_" + referencePosition

  def matchingSizeAt(pathPosition: Int, size: Int): String = "matchingSize_" + pathPosition + "_" + size

  override def apply(linearEncoding: LinearPrimitivePlanEncoding): Seq[Clause] = {

    // generate clauses representing the matching
    val matchingClauses = generateMatchingClauses(linearEncoding, matchPathAndReference)

    // compute matching size
    val automataTransition = linearEncoding.linearPlan.indices flatMap { pathPosition => linearEncoding.linearPlan.indices flatMap { subPlanLength =>
      val oldState = matchingSizeAt(pathPosition - 1, subPlanLength)
      val newStateSame = matchingSizeAt(pathPosition, subPlanLength)
      val newStateIncrease = matchingSizeAt(pathPosition, subPlanLength + 1)

      val matchings = referencePlan.indices map { referencePosition => matchPathAndReference(pathPosition, referencePosition) }

      // if it increases then one has to be matched
      val ifIncreasing = linearEncoding.impliesRightOr(oldState :: newStateIncrease :: Nil, matchings)
      // if it stays the same all must be false
      val ifSame = linearEncoding.impliesAllNot(oldState :: newStateSame :: Nil, matchings)
      // if the predecessor is true, at least one transition has to happen
      val hasToBeTransition = linearEncoding.impliesRightOr(oldState :: Nil, newStateIncrease :: newStateSame :: Nil)

      ifSame :+ ifIncreasing :+ hasToBeTransition
    }
    }

    val onlyOneState = Range(-1, linearEncoding.linearPlan.length) flatMap { pathPosition =>
      val states = linearEncoding.linearPlan.indices map { subPlanLength => matchingSizeAt(pathPosition, subPlanLength) }
      linearEncoding.atMostOneOf(states)
    }

    val startClause = Clause(matchingSizeAt(-1, 0))

    // go get the minimum, at least one of them must be true
    val endClause = linearEncoding.atLeastOneOf(Range(minimumLength, linearEncoding.linearPlan.length) map { matchingSizeAt(linearEncoding.linearPlan.length - 1, _) })

    matchingClauses ++ automataTransition ++ onlyOneState :+ startClause :+ endClause
  }
}

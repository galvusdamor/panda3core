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
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait MatchingBasedConstraints extends AdditionalSATConstraint {

  def referencePlan: Seq[Task]

  def ignoreOrder: Boolean

  protected def generateMatchingClauses(linearEncoding: EncodingWithLinearPlan, matchAtom: (Int, Int) => String) : Seq[Clause] = {
    // every position and path can be matched only once
    val onlyOneMatchingPerPath = linearEncoding.linearPlan.indices flatMap { pathPosition =>
      val matchings = referencePlan.indices map { referencePosition => matchAtom(pathPosition, referencePosition) }
      linearEncoding.atMostOneOf(matchings)
    }

    val onlyOneMatchingPerReference = referencePlan.indices flatMap { referencePosition =>
      val matchings = linearEncoding.linearPlan.indices map { pathPosition => matchAtom(pathPosition, referencePosition) }
      linearEncoding.atMostOneOf(matchings)
    }

    // if matched, the task must be in the path
    val ifMatchedTaskMustBeThere: Seq[Clause] = linearEncoding.linearPlan.zipWithIndex flatMap { case (possibleTasks, pathPosition) =>
      referencePlan.zipWithIndex map { case (referenceTask, referencePosition) =>
        if (possibleTasks contains referenceTask) {
          linearEncoding.impliesSingle(matchAtom(pathPosition, referencePosition), possibleTasks(referenceTask))
        } else {
          // if the path cannot contain the position, then forbid this matching
          Clause((matchAtom(pathPosition, referencePosition), false))
        }
      }
    }

    // forbid cross matchings
    val crossMatching = if (ignoreOrder) Nil
    else for (path1 <- linearEncoding.linearPlan.indices; path2 <- linearEncoding.linearPlan.indices
              if path1 > path2;
              reference1 <- referencePlan.indices; reference2 <- referencePlan.indices
              if reference1 < reference2) yield
      Clause((matchAtom(path1, reference1), false) ::(matchAtom(path2, reference2), false) :: Nil)


    onlyOneMatchingPerPath ++ onlyOneMatchingPerReference ++ ifMatchedTaskMustBeThere ++ crossMatching
  }
}

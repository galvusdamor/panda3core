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

import de.uniulm.ki.panda3.configuration.{SATReductionMethod, Solvertype}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGPOCLDeleteEncoding(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, intProblem : IntProblem,
                                 taskSequenceLengthQQ: Int, reductionMethod: SATReductionMethod, offsetToK: Int, overrideK: Option[Int] = None,
                                 restrictionMethod: RestrictionMethod, usePDTMutexes: Boolean, useEncoder: Solvertype) extends SOGPOCLEncoding {
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  protected def deletes(path: Seq[Int], precondition: Predicate): String = "del^" + path.mkString(";") + "_" + precondition.name

  def causalThreatsFormula(supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)]): Seq[Clause] = {

    val willDelete: Seq[Clause] = extendedSOG.vertices flatMap { case (path, tasks) =>
      tasks flatMap { t => t.effectsAsPredicateBool filterNot { _._2 } map { case (p, _) => impliesSingle(pathAction(path.length, path, t), deletes(path, p)) } }
    }
    println("Paths may delete: " + willDelete.length + " clauses")

    val supporterPairs = supporterLiterals map { case (a, b, _) => (a, b) } distinct
    val nonOrderedMap: Map[(Seq[Int], Seq[Int]), Seq[(Seq[Int], Set[Task])]] = supporterPairs map { case (p1, p2) =>
      val nonord = extendedSOG.vertices filterNot { case (p, _) => p == p1 || p == p2 || onlyPathSOG.reachable(p2).contains(p) || onlyPathSOG.reachable(p).contains(p1) }
      (p1, p2) -> nonord
    } toMap

    val startTime = System.currentTimeMillis()
    // no causal threats
    val potentialThreader: Seq[(Seq[Int], Seq[Int], Seq[Int], Predicate, String)] = supporterLiterals flatMap { case (p1, p2, prec) =>
      val nonOrdered = nonOrderedMap((p1, p2))
      val supporterLiteral = supporter(p1, p2, prec)

      nonOrdered collect { case (pt, ts) if ts exists { t => t.effectsAsPredicateBool exists { case (p, s) => !s && p == prec } } =>
        (p1, p2, pt, prec, supporterLiteral)
      }
    }
    val middleTime = System.currentTimeMillis

    val noCausalThreat = potentialThreader map { case (p1, p2, pt, prec, supporterLiteral) =>
      impliesRightOr(supporterLiteral :: deletes(pt, prec) :: Nil, before(pt, p1) :: before(p2, pt) :: Nil)
    }
    val endTime = System.currentTimeMillis()
    println("No causal threats: " + noCausalThreat.length + " clauses, time needed " + (middleTime - startTime).toDouble./(1000) + " and " + (endTime - middleTime).toDouble./(1000))

    willDelete ++ noCausalThreat
  }

}

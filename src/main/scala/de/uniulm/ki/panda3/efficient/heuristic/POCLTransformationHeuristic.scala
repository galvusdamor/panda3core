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

package de.uniulm.ki.panda3.efficient.heuristic

import java.util

import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientTask}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.progression.heuristics.sasp.ExplorationQueueBasedHeuristics.{hAddhFFEq, hFilter, hLmCutEq, hMaxEq}
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic.SasHeuristics
import de.uniulm.ki.panda3.progression.heuristics.sasp.{SasHeuristic, hCausalGraph}
import de.uniulm.ki.util.InformationCapsule

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class POCLTransformationHeuristic(classicalHeuristic: SasHeuristics, domain: EfficientDomain, initTask: EfficientTask) extends EfficientHeuristic[Unit] {

  val sasHeuristic = {
    if (classicalHeuristic eq SasHeuristic.SasHeuristics.hAdd) new hAddhFFEq(domain.sasPlusProblem, SasHeuristic.SasHeuristics.hAdd)
    else if (classicalHeuristic eq SasHeuristic.SasHeuristics.hMax) new hMaxEq(domain.sasPlusProblem)
    else if (classicalHeuristic eq SasHeuristic.SasHeuristics.hFF) new hAddhFFEq(domain.sasPlusProblem, SasHeuristic.SasHeuristics.hFF)
    else if (classicalHeuristic eq SasHeuristic.SasHeuristics.hLmCut) new hLmCutEq(domain.sasPlusProblem, false)
    else ???
  }

  val s0: util.BitSet = {
    val s = new util.BitSet()

    initTask.effect filter { _.isPositive } map { _.predicate } map domain.predicateIndexToSASPlus foreach { p =>
      s.set(p)
    }

    s
  }

  override def computeHeuristic(plan: EfficientPlan, payload: Unit,
                                appliedModification: Option[EfficientModification], depth: Int, oldHeuristic: Double,
                                informationCapsule: InformationCapsule): (Double, Unit) = {

    val g = new util.BitSet()
    // get all unsupported preconditions
    var op = 0
    while (op < plan.openPreconditions.length) {
      g.set(domain.predicateIndexToSASPlus(plan.openPreconditions(op).openPredicate))

      op += 1
    }

    val h: Double = sasHeuristic.calcHeu(s0, g)

    (h, ())
  }

  override def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

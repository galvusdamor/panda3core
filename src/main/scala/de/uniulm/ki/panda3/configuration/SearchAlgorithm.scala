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

package de.uniulm.ki.panda3.configuration

import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.search.EfficientSearchNode
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.SearchNode
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait SearchAlgorithm[DomainType, PlanType, SearchNodeType] {

  def startSearch(domain: DomainType, initialPlan: PlanType,
                  nodeLimit: Option[Int] = None, timeLimit: Option[Int] = None, releaseEvery: Option[Int] = None, printSearchInfo: Boolean = false, buildSearchTree: Boolean,
                  informationCapsule: InformationCapsule, timeCapsule: TimeCapsule = new TimeCapsule()):
  (SearchNodeType, Semaphore, ResultFunction[PlanType], AbortFunction)

}

trait SymbolicSearchAlgorithm extends SearchAlgorithm[Domain, Plan, SearchNode]

trait EfficientSearchAlgorithm[Payload] extends SearchAlgorithm[EfficientDomain, EfficientPlan, EfficientSearchNode[Payload]]


case class AbortFunction(abortFunction: Unit => Unit) extends (Unit => Unit) {
  override def apply(v1: Unit): Unit = abortFunction.apply(v1)
}

case class ResultFunction[ResultType](resultFunction: Unit => Seq[ResultType]) extends (Unit => Seq[ResultType]) {
  override def apply(v1: Unit): Seq[ResultType] = resultFunction.apply(v1)
}

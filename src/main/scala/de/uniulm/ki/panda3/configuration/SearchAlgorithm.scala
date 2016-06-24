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

trait EfficientSearchAlgorithm extends SearchAlgorithm[EfficientDomain, EfficientPlan, EfficientSearchNode]


case class AbortFunction(abortFunction: Unit => Unit) extends (Unit => Unit) {
  override def apply(v1: Unit): Unit = abortFunction.apply(v1)
}

case class ResultFunction[ResultType](resultFunction: Unit => Option[ResultType]) extends (Unit => Option[ResultType]) {
  override def apply(v1: Unit): Option[ResultType] = resultFunction.apply(v1)
}
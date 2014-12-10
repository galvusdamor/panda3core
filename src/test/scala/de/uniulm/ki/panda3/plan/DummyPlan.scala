package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThreat, OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.plan.modification.Modification
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * A Dummy plan, which can't do anything
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class DummyPlan extends Plan {
  override val openPreconditions: Seq[OpenPrecondition] = ???
  override val causalThreads: Seq[CausalThreat] = ???
  override val unboundVariables: Seq[UnboundVariable] = ???

  override def domain: Domain = ???

  override def variableConstraints: CSP = ???

  override def modify(modification: Modification): Plan = ???

  override def causalLinks: Seq[CausalLink] = ???

  override def orderingConstraints: TaskOrdering = ???


  override def init: PlanStep = ???

  override def goal: PlanStep = ???

  override def getNewId(): Int = ???

  /** returns (if possible), whether this plan can be refined into a solution or not */
  override def isSolvable: Option[Boolean] = ???

  override def planSteps: Seq[PlanStep] = ???

}

object DummyPlan extends DummyPlan {

}
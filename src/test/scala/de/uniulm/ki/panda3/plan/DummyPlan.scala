package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * A Dummy plan, which can't do anything
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class DummyPlan extends Plan {
  override def planSteps: IndexedSeq[PlanStep] = ???

  override def variableConstraints: CSP = ???

  override def openPreconditions: IndexedSeq[OpenPrecondition] = ???

  override def causalLinks: IndexedSeq[CausalLink] = ???

  override def causalThreads: IndexedSeq[CausalThread] = ???

  override def orderingConstraints: TaskOrdering = ???

}

object DummyPlan extends DummyPlan {

}

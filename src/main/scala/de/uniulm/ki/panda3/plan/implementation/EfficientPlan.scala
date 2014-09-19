package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class EfficientPlan extends Plan {

  override def planSteps(): IndexedSeq[PlanStep] = ???

  override def causalLinks(): IndexedSeq[CausalLink] = ???

  override def orderingConstraints(): TaskOrdering = ???

  override def variableConstraints(): CSP = ???

  override def causalThreads: IndexedSeq[CausalThread] = ???

  override def openPreconditions: IndexedSeq[OpenPrecondition] = ???
}

package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * Simple implementation of a plan, based on symbols
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */

class SymbolicPlan extends Plan {

  override val planSteps: IndexedSeq[PlanStep] = ???
  override val causalLinks: IndexedSeq[CausalLink] = ???
  override val orderingConstraints: TaskOrdering = ???
  override val variableConstraints: CSP = ???


  override def causalThreads(): IndexedSeq[CausalThread] = ???

  override def openPreconditions(): IndexedSeq[OpenPrecondition] = ???
}
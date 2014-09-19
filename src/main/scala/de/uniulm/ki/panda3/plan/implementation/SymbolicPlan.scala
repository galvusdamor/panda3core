package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.csp.SymbolicCSP
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering

/**
 * Simple implementation of a plan, based on symbols
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */

class SymbolicPlan(override val planSteps: IndexedSeq[PlanStep],
                   override val causalLinks: IndexedSeq[CausalLink],
                   override val orderingConstraints: SymbolicTaskOrdering,
                   override val variableConstraints: SymbolicCSP) extends Plan {

  override def causalThreads(): IndexedSeq[CausalThread] = ???

  override def openPreconditions(): IndexedSeq[OpenPrecondition] = ???
}
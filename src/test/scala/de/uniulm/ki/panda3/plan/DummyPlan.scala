package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThreat, OpenPrecondition}
import de.uniulm.ki.panda3.plan.modification.Modification
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * A Dummy plan, which can't do anything
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class DummyPlan extends Plan {
  override def planSteps : IndexedSeq[PlanStep] = ???

  override def variableConstraints : CSP = ???

  override def openPreconditions : IndexedSeq[OpenPrecondition] = ???

  override def causalLinks : IndexedSeq[CausalLink] = ???

  override def causalThreads : IndexedSeq[CausalThreat] = ???

  override def orderingConstraints : TaskOrdering = ???

  /** returns (if possible), whether this plan can be refined into a solution or not */
  override def isSolvable : Option[Boolean] = ???

  override def apply(modification : Modification) = ???
}

object DummyPlan extends DummyPlan {

}

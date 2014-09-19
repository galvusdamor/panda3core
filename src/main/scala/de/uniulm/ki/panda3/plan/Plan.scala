package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, Flaw, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Plan {

  def planSteps: IndexedSeq[PlanStep]

  def causalLinks: IndexedSeq[CausalLink]

  def orderingConstraints: TaskOrdering

  def variableConstraints: CSP


  def causalThreads: IndexedSeq[CausalThread]

  def openPreconditions: IndexedSeq[OpenPrecondition]


  def flaws: IndexedSeq[Flaw] = causalThreads ++ openPreconditions
}

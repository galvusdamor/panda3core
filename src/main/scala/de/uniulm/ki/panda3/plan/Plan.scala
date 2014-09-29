package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, Flaw, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Plan {

  def planSteps : Seq[PlanStep]

  def causalLinks : Seq[CausalLink]

  def orderingConstraints: TaskOrdering

  def variableConstraints: CSP


  /** list of all causal threads in this plan */
  def causalThreads : Seq[CausalThread]

  /** list fo all open preconditions in this plan */
  def openPreconditions : Seq[OpenPrecondition]

  /** list of all flaws in this plan */
  def flaws : Seq[Flaw] = causalThreads ++ openPreconditions

  /** returns (if possible), whether this plan can be refined into a solution or not */
  def isSolvable : Option[Boolean]
}

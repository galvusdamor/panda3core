package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThreat, Flaw, OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.plan.modification.Modification
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Plan {

  lazy val flaws: Seq[Flaw] = {
    val hardFlaws = causalThreads ++ openPreconditions
    if (hardFlaws.size == 0)
      unboundVariables
    else hardFlaws
  }
  val causalThreads: Seq[CausalThreat]
  val openPreconditions: Seq[OpenPrecondition]
  val unboundVariables: Seq[UnboundVariable]

  def domain: Domain

  def planSteps: Seq[PlanStep]

  def causalLinks: Seq[CausalLink]

  def orderingConstraints: TaskOrdering

  def variableConstraints: CSP

  def init: PlanStep

  def goal: PlanStep

  /** returns (if possible), whether this plan can be refined into a solution or not */
  def isSolvable: Option[Boolean]

  def modify(modification: Modification): Plan

  def getNewId() : Int
}

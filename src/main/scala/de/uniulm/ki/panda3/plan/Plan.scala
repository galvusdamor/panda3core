package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThreat, OpenPrecondition}
import de.uniulm.ki.panda3.plan.modification.Modification
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Plan {

  val causalThreads: Seq[CausalThreat]
  val openPreconditions: Seq[OpenPrecondition]
  val allPreconditions: Seq[(PlanStep, Literal)]

  def domain: Domain

  def planSteps: Seq[PlanStep]

  def causalLinks: Seq[CausalLink]

  def orderingConstraints: TaskOrdering

  def variableConstraints: CSP

  /** returns (if possible), whether this plan can be refined into a solution or not */
  def isSolvable: Option[Boolean]

  def modify(modification: Modification): Plan

  def getNewId() : Int
}

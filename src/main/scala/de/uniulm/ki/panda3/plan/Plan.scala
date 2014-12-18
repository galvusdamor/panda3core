package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.{CSP, Variable}
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
  val getFirstFreePlanStepID: Int = 1 + (planSteps foldLeft 0) { case (m, ps: PlanStep) => math.max(m, ps.id)}
  val getFirstFreeVariableID: Int = 1 + (variableConstraints.variables foldLeft 0) { case (m, v: Variable) => math.max(m, v.id)}

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

  /** returns a completely new instantiated version of the current plan. This can e.g. be used to clone subplans of [[de.uniulm.ki.panda3.domain.DecompositionMethod]]s. */
  def newInstance(): Plan
}
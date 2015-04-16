package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.{CSP, Substitution, Variable}
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw._
import de.uniulm.ki.panda3.plan.modification.Modification
import de.uniulm.ki.panda3.plan.ordering.TaskOrdering

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Plan {

  lazy val flaws: Seq[Flaw] = {
    val hardFlaws = causalThreats ++ openPreconditions ++ abstractPlanSteps
    if (hardFlaws.size == 0)
      unboundVariables
    else hardFlaws
  }
  lazy val planStepWithoutInitGoal: Seq[PlanStep] = planSteps filter {ps => ps != init && ps != goal}

  /** all abstract plan steps */
  val abstractPlanSteps: Seq[AbstractPlanStep]

  /** all causal threads in this plan */
  val causalThreats: Seq[CausalThreat]

  /** all open preconditions in this plan */
  val openPreconditions: Seq[OpenPrecondition]

  /** all variables which are not bound to a constant, yet */
  val unboundVariables: Seq[UnboundVariable]
  val getFirstFreePlanStepID: Int = 1 + (planSteps foldLeft 0) { case (m, ps: PlanStep) => math.max(m, ps.id)}
  val getFirstFreeVariableID: Int = 1 + (variableConstraints.variables foldLeft 0) { case (m, v: Variable) => math.max(m, v.id)}

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
  def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: Substitution = Substitution(Nil, Nil)): (Plan, Substitution)

}
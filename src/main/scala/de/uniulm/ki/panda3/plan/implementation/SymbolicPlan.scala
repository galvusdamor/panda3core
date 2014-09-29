package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.csp.SymbolicCSP
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThread, OpenPrecondition}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering

/**
 * Simple implementation of a plan, based on symbols
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */

case class SymbolicPlan(planSteps : Seq[PlanStep],
                        causalLinks : Seq[CausalLink],
                        orderingConstraints : SymbolicTaskOrdering,
                        variableConstraints : SymbolicCSP) extends Plan {

  /** list of all causal threads in this plan */
  override def causalThreads : Seq[CausalThread] = Nil

  /** list fo all open preconditions in this plan */
  override def openPreconditions : Seq[OpenPrecondition] = allPreconditions() filterNot {
    case (ps, literal) => causalLinks exists { case CausalLink(_, consumer, condition) => (consumer =?= ps)(variableConstraints) && (condition =?= literal)(variableConstraints)}
  } map { case (ps, literal) => OpenPrecondition(ps, literal)}

  /** returns (if possible), whether this plan can be refined into a solution or not */
  override def isSolvable : Option[Boolean] = if (!orderingConstraints.isConsistent || variableConstraints.isSolvable == Some(false)) Some(false) else None


  // =================== Local Helper ==================== //

  /** list containing all preconditions in this plan */
  def allPreconditions() : Seq[(PlanStep, Literal)] = (planSteps map { ps => ps.substitutedPreconditions map { prec => (ps, prec)}}).flatten

}
package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.SymbolicCSP
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThreat, OpenPrecondition}
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
  lazy val causalThreads : Seq[CausalThreat] =
    for (causalLink@CausalLink(producer, consumer, literal) <- causalLinks; planStep <- planSteps
         if (planStep != producer && planStep != consumer && !orderingConstraints.lt(planStep, producer) && !orderingConstraints.gt(planStep, consumer));
         effect <- planStep.substitutedEffects
         if ((effect #?# literal.negate)(variableConstraints) != None)
    ) yield
      CausalThreat(causalLink, planStep, effect)


  /** list fo all open preconditions in this plan */
  override lazy val openPreconditions : Seq[OpenPrecondition] = allPreconditions filterNot {
    case (ps, literal) => causalLinks exists { case CausalLink(_, consumer, condition) => (consumer =?= ps)(variableConstraints) && (condition =?= literal)(variableConstraints)}
  } map { case (ps, literal) => OpenPrecondition(ps, literal)}

  /** returns (if possible), whether this plan can be refined into a solution or not */
  override def isSolvable : Option[Boolean] = if (!orderingConstraints.isConsistent || variableConstraints.isSolvable == Some(false)) Some(false) else None


  // =================== Local Helper ==================== //

  /** list containing all preconditions in this plan */
  lazy val allPreconditions : Seq[(PlanStep, Literal)] = planSteps flatMap { ps => ps.substitutedPreconditions map { prec => (ps, prec)}}
}
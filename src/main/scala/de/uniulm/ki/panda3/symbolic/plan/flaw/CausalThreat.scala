package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.modification.{AddOrdering, DecomposePlanStep, MakeLiteralsUnUnifiable, Modification}

/**
 * This trait represents a causal thread flaw
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class CausalThreat(plan: Plan, link: CausalLink, threater: PlanStep, effectOfThreater: Literal) extends Flaw {
  override def computeAllResolvents(domain: Domain): Seq[Modification] = AddOrdering(plan, threater, link) ++ MakeLiteralsUnUnifiable(plan, link.condition, effectOfThreater.negate) ++
    DecomposePlanStep(plan, threater, domain)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "CausalThreat: on " + link.shortInfo + " by " + threater.shortInfo + " with " + effectOfThreater.shortInfo

}
package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.modification.{AddOrdering, MakeLiteralsUnUnifiable, Modification}

/**
 * This trait represents a causal thread flaw
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class CausalThreat(plan: Plan, link: CausalLink, planStep: PlanStep, effectOfThreater: Literal) extends Flaw {
  override def resolvants(domain: Domain): Seq[Modification] = AddOrdering(plan, planStep, link) ++ MakeLiteralsUnUnifiable(plan, link.condition, effectOfThreater.negate)
}
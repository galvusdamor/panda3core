package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.PlanStep
import de.uniulm.ki.panda3.plan.modification.{DecomposePlanStep, Modification}

/**
 * Represents the flaw of a plan containing a still abstract task
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AbstractPlanStep(plan: Plan, ps: PlanStep) extends Flaw {

  override def resolvents(domain: Domain): Seq[Modification] = DecomposePlanStep(plan, ps, domain)
}

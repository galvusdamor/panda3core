package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.modification.{DecomposePlanStep, Modification}

/**
 * Represents the flaw of a plan containing a still abstract task
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AbstractPlanStep(plan: Plan, ps: PlanStep) extends Flaw {

  override def resolvents(domain: Domain): Seq[Modification] = DecomposePlanStep(plan, ps, domain)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "AbstractPlanStep: " + ps.shortInfo

}
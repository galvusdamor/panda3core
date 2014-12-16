package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.PlanStep
import de.uniulm.ki.panda3.plan.modification.Modification

/**
 * Represents the flaw of a plan containing a still abstract task
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AbstractPlanStep(plan: Plan, ps: PlanStep) extends Flaw {

  override def resolvants: Seq[Modification] = ???
}

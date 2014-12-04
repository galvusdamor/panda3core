package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.PlanStep
import de.uniulm.ki.panda3.plan.modification.{InsertPlanStepWithLink, Modification}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OpenPrecondition(plan: Plan, planStep: PlanStep, precondition: Literal) extends Flaw {
  override def resolvants: Seq[Modification] = InsertPlanStepWithLink(plan, planStep, precondition)
}

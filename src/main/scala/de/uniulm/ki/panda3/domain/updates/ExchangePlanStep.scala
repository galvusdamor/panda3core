package de.uniulm.ki.panda3.domain.updates

import de.uniulm.ki.panda3.plan.element.PlanStep

/**
 * replaces one [[de.uniulm.ki.panda3.plan.element.PlanStep]] with another
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class ExchangePlanStep(oldPlanStep: PlanStep, newPlanStep: PlanStep) extends DomainUpdate {

}

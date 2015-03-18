package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.logic.Literal

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class CausalLink(producer: PlanStep, consumer: PlanStep, condition: Literal) {
  def containsOne(pss: PlanStep*): Boolean = (pss foldLeft false) {case (b, ps) => b || contains(ps)}

  def contains(ps: PlanStep): Boolean = ps == producer || ps == consumer
}
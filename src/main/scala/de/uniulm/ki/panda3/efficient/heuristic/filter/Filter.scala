package de.uniulm.ki.panda3.efficient.heuristic.filter

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait Filter {
  def isPossiblySolvable(plan: EfficientPlan): Boolean
}


case class PlanLengthLimit(limit : Int) extends Filter {
  override def isPossiblySolvable(plan: EfficientPlan): Boolean = (plan.numberOfPlanSteps - 2) <= limit
}
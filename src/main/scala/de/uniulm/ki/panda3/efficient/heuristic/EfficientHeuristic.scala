package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientHeuristic {

  def computeHeuristic(plan: EfficientPlan): Double
}


object AlwaysZeroHeuristic extends EfficientHeuristic{
  override def computeHeuristic(plan: EfficientPlan): Double = 0
}
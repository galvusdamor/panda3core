package de.uniulm.ki.panda3.efficient.heuristic.filter

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object RecomputeHTN extends Filter {
  override def isPossiblySolvable(plan: EfficientPlan): Boolean = plan.goalPotentiallyReachable && plan.allLandmarksApplicable && plan.allContainedApplicable && plan.allAbstractTasksAllowed
}

package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

/**
  * This class contains all "trivial" heuristics
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */

object EfficientNumberOfFlaws extends EfficientHeuristic {
  override def computeHeuristic(plan: EfficientPlan): Double = plan.flaws.length
}

case class EfficientWeightedFlaws(openPreconditionWeight: Double, causalThreadWeight: Double, abstractTaskWeight: Double) extends EfficientHeuristic {
  override def computeHeuristic(plan: EfficientPlan): Double = plan.openPreconditions.length * openPreconditionWeight + plan.causalThreats.length * causalThreadWeight +
    plan.abstractPlanSteps.length * abstractTaskWeight
}

object EfficientNumberOfPlanSteps extends EfficientHeuristic {
  override def computeHeuristic(plan: EfficientPlan): Double = plan.numberOfPlanSteps
}

package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification

/**
  * This class contains all "trivial" heuristics
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */

object EfficientNumberOfFlaws extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = (plan.flaws.length, ())
}

object EfficientNumberOfOpenPreconditions extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = (plan.openPreconditions.length, ())
}

case class EfficientWeightedFlaws(openPreconditionWeight: Double, causalThreadWeight: Double, abstractTaskWeight: Double) extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) =
    (plan.openPreconditions.length * openPreconditionWeight + plan.causalThreats.length * causalThreadWeight + plan.abstractPlanSteps.length * abstractTaskWeight, ())
}

object EfficientNumberOfPlanSteps extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = (plan.numberOfPlanSteps, ())
}

object EfficientNumberOfAbstractPlanSteps extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = (plan.abstractPlanSteps.length, ())
}

object EfficientUMCPHeuristic extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: EfficientModification, depth: Int): (Double, Unit) = {
    val abstractPS = plan.abstractPlanSteps.length
    (if (abstractPS == 0) -depth else abstractPS, ())
  }
}

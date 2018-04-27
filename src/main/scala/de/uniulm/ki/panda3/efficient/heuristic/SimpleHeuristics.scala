package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.InformationCapsule

import scala.util.Random

/**
  * This class contains all "trivial" heuristics
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */

object EfficientNumberOfFlaws extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = (plan.flaws.length, ())

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

object EfficientNumberOfOpenPreconditions extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = (plan.openPreconditions.length, ())

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

case class EfficientWeightedFlaws(openPreconditionWeight: Double, causalThreadWeight: Double, abstractTaskWeight: Double) extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) =
    (plan.openPreconditions.length * openPreconditionWeight + plan.causalThreats.length * causalThreadWeight + plan.abstractPlanSteps.length * abstractTaskWeight, ())

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

object EfficientNumberOfPlanSteps extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = (plan.numberOfPlanSteps, ())

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

object EfficientNumberOfAbstractPlanSteps extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = (plan.abstractPlanSteps.length, ())

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

object EfficientUMCPHeuristic extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = {
    val abstractPS = plan.abstractPlanSteps.length
    (if (abstractPS == 0) -depth else abstractPS, ())
  }

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}

object EfficientUMCPBFHeuristic extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = {
    val abstractPS = plan.abstractPlanSteps.length
    (if (abstractPS == 0) -depth else depth, ())
  }

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}


case class EfficientRandomHeuristic(random: Random) extends EfficientHeuristic[Unit] {
  override def computeHeuristic(plan: EfficientPlan, unit: Unit, mod: Option[EfficientModification], depth: Int, oldHeuristic: Double, informationCapsule: InformationCapsule):
  (Double, Unit) = (random.nextInt(Integer.MAX_VALUE / 2), ())

  def computeInitialPayLoad(plan: EfficientPlan): Unit = ()
}
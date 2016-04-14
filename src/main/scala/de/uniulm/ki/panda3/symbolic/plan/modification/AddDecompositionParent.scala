package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{PlanStep, CausalLink, OrderingConstraint}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class AddDecompositionParent(plan : Plan) extends Modification {

  override def addedPlanSteps: Seq[PlanStep] = super.addedPlanSteps

  override def addedCausalLinks: Seq[CausalLink] = super.addedCausalLinks

  override def removedCausalLinks: Seq[CausalLink] = super.removedCausalLinks

  override def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = super.nonInducedAddedOrderingConstraints
}

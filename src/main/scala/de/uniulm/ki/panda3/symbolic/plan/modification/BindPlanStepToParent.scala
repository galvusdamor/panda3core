package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, CausalLink, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BindPlanStepToParent(plan: Plan, child: PlanStep, parent: PlanStep, asPlanStepInSubPlan: PlanStep) extends Modification {
  override def setParentOfPlanSteps: Seq[(PlanStep, (PlanStep, PlanStep))] = (child, (parent, asPlanStepInSubPlan)) :: Nil
}

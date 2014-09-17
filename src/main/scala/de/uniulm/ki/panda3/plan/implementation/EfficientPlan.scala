package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.elements.{CausalLink, OrderingConstraint, PlanStep, VariableConstraint}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class EfficientPlan extends Plan {


  override def planSteps(): Array[PlanStep] = {
    val x: Array[PlanStep] = Array(new PlanStep(55))
    x
  }

  override def causalLinks(): Array[CausalLink] = {
    var x: Array[CausalLink] = Array()
    x
  }

  override def orderingConstraints(): Array[OrderingConstraint] = ???

  override def variableConstraints(): Array[VariableConstraint] = ???
}

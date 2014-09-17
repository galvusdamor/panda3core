package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.elements.{CausalLink, OrderingConstraint, PlanStep, VariableConstraint}

/**
 * Created by gregor on 17.09.14.
 */

class SymbolicPlan extends Plan {

  override val planSteps: Array[PlanStep] = ???

  override val causalLinks: Array[CausalLink] = ???

  override val orderingConstraints: Array[OrderingConstraint] = ???

  override val variableConstraints: Array[VariableConstraint] = ???
}
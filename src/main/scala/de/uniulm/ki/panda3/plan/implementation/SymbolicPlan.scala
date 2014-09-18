package de.uniulm.ki.panda3.plan.implementation

import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.elements.{CausalLink, OrderingConstraint, PlanStep, VariableConstraint}

/**
 * Simple implementation of a plan, based on symbols
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */

class SymbolicPlan extends Plan {

  override val planSteps: Array[PlanStep] = Array()
  override val causalLinks: Array[CausalLink] = Array()
  override val orderingConstraints: Array[OrderingConstraint] = Array()
  override val variableConstraints: Array[VariableConstraint] = Array()

}

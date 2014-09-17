package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.plan.elements.{CausalLink, OrderingConstraint, PlanStep, VariableConstraint}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Plan {

  def planSteps: Array[PlanStep]

  def causalLinks: Array[CausalLink]

  def orderingConstraints: Array[OrderingConstraint]

  def variableConstraints: Array[VariableConstraint]
}

package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{VariableConstraint, Variable}
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 * Trait describint an abstract modification. Every specific modification should extend this trait
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Modification {

  /* Adding modifications*/
  def addedPlanSteps : Seq[PlanStep]

  def addedVariables : Seq[Variable]

  def addedCausalLinks : Seq[CausalLink]

  def addedOrderingConstraints : Seq[OrderingConstraint]

  def addedVariableConstraints : Seq[VariableConstraint]


  /* removing modifications */

  def removedPlanSteps : Seq[PlanStep]

  def removedVariables : Seq[Variable]

  def removedCausalLinks : Seq[CausalLink]

  def removedOrderingConstraints : Seq[OrderingConstraint]

  def removedVariableConstraints : Seq[VariableConstraint]


}
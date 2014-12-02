package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Variable, VariableConstraint}
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 * Trait describint an abstract modification. Every specific modification should extend this trait
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Modification {

  /* Adding modifications*/

  def addedPlanSteps: Seq[PlanStep] = Nil

  def addedVariables: Seq[Variable] = Nil

  def addedCausalLinks: Seq[CausalLink] = Nil

  def addedOrderingConstraints: Seq[OrderingConstraint] = Nil

  def addedVariableConstraints: Seq[VariableConstraint] = Nil


  /* removing modifications */

  def removedPlanSteps: Seq[PlanStep] = Nil

  def removedVariables: Seq[Variable] = Nil

  def removedCausalLinks: Seq[CausalLink] = Nil

  def removedOrderingConstraints: Seq[OrderingConstraint] = Nil

  def removedVariableConstraints: Seq[VariableConstraint] = Nil
}
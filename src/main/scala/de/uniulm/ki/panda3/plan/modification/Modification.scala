package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Variable, VariableConstraint}
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 * Trait describing an abstract modification. Every specific modification should extend this trait.
 *
 * The actually induced ordering constraints are inferred from the explicitly induced ones and those implicitly given as causal links
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Modification {

  /* Adding modifications*/

  def addedPlanSteps: Seq[PlanStep] = Nil

  def addedVariables: Seq[Variable] = Nil

  def addedVariableConstraints: Seq[VariableConstraint] = Nil

  /** all added orderings constraints. contains the explicitly ones and the implicit ones, which are based on causal links */
  final def addedOrderingConstraints: Seq[OrderingConstraint] = nonInducedAddedOrderingConstraints ++
    (addedCausalLinks collect { case CausalLink(producer, consumer, _) if producer.schema.isPrimitive && consumer.schema.isPrimitive => OrderingConstraint(producer, consumer)})

  def addedCausalLinks: Seq[CausalLink] = Nil

  def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = Nil


  /* removing modifications */

  def removedPlanSteps: Seq[PlanStep] = Nil

  def removedVariables: Seq[Variable] = Nil

  def removedCausalLinks: Seq[CausalLink] = Nil

  def removedOrderingConstraints: Seq[OrderingConstraint] = Nil

  def removedVariableConstraints: Seq[VariableConstraint] = Nil
}
package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Variable}

/**
 * Tasks are blue-prints for actions, actually contained in plans, i.e. they describe which variables a [[de.uniulm.ki.panda3.plan.element.PlanStep]] of their type must have and which
 * preconditions and effects it has.
 *
 * Additionally Tasks can either be primitive or abstract. The first kind can be executed directly, while the latter must be decomposed further during the planning process.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
// TODO: check, whether the parameter constraints of a task schema are always observed correctly
case class Task(name: String, isPrimitive: Boolean, parameters: Seq[Variable], parameterConstraints: Seq[VariableConstraint], preconditions: Seq[Literal], effects: Seq[Literal]) extends
DomainUpdatable with PrettyPrintable {

  def substitute(literal: Literal, newParameter: Seq[Variable]): Literal = {
    val sub = Substitution(parameters, newParameter)
    Literal(literal.predicate, literal.isPositive, literal.parameterVariables map sub)
  }

  override def update(domainUpdate: DomainUpdate): Task = Task(name, isPrimitive, parameters map {_.update(domainUpdate)}, parameterConstraints map {_.update(domainUpdate)}, preconditions
    map {_.update(domainUpdate)}, effects map {_.update(domainUpdate)})

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + (parameters map {_.shortInfo}).mkString("(", ", ", ")")

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo + "\npreconditions:\n" + (preconditions map {"\t" + _.shortInfo}).mkString("\n") +
    "\neffects:\n" + (effects map {"\t" + _.shortInfo}).mkString("\n")

}
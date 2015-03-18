package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.csp._
import de.uniulm.ki.panda3.logic.Literal

/**
 * Tasks are blue-prints for actions, actually contained in plans, i.e. they describe which variables a [[de.uniulm.ki.panda3.plan.element.PlanStep]] of their type must have and which
 * preconditions and effects it has.
 *
 * Additionally Tasks can either be primitive or abstract. The first kind can be executed directly, while the latter must be decomposed further during the planning process.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
// TODO: check, whether the parameter constraints of a task schema are always observed correctly
case class Task(name: String, isPrimitive: Boolean, parameters: Seq[Variable], parameterConstraints: Seq[VariableConstraint], preconditions: Seq[Literal], effects: Seq[Literal]) {

  def substitute(literal: Literal, newParameter: Seq[Variable]): Literal = {
    val sub = Substitution(parameters, newParameter)
    Literal(literal.predicate, literal.isPositive, literal.parameterVariables map sub)
  }
}
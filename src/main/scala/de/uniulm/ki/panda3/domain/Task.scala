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
case class Task(name: String, isPrimitive: Boolean, parameters: Seq[Variable], parameterConstraints: Seq[VariableConstraint], preconditions: Seq[Literal], effects: Seq[Literal]) {

  def substitute(literal: Literal, newParameter: Seq[Variable]): Literal = Literal(literal.predicate, literal.isPositive, literal.parameterVariables map {substitute(_, newParameter)})

  private def substitute(v: Variable, newParameter: Seq[Variable]): Variable = newParameter(parameters.indexOf(v))

  def substitute(constraint: VariableConstraint, newParameter: Seq[Variable]): VariableConstraint = {
    def sub(v: Variable): Variable = substitute(v, newParameter)

    constraint match {
      case Equal(v1, Left(v2))    => Equal(sub(v1), sub(v2))
      case Equal(v1, Right(c))    => Equal(sub(v1), c)
      case NotEqual(v1, Left(v2)) => NotEqual(sub(v1), sub(v2))
      case NotEqual(v1, Left(c))  => NotEqual(sub(v1), c)
      case OfSort(v, s)           => OfSort(sub(v), s)
      case NotOfSort(v, s)        => NotOfSort(sub(v), s)
    }
  }
}
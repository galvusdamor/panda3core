package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait VariableConstraint {

  def compileNotOfSort: Set[VariableConstraint] = {
    this match {
      case Equals(_, _) | NotEquals(_, _) | OfSort(_, _) => Set(this)
      case NotOfSort(v, s) => s.elements.map(element => NotEquals(v, Right(element))).toSet[VariableConstraint]
    }
  }
}

// the 4 kinds of constraints the CSPs currently support

case class Equals(left: Variable, right: Either[Variable, Constant]) extends VariableConstraint {}

object Equals {
  def apply(left: Variable, right: Variable): Equals = Equals(left, Left(right))

  def apply(left: Variable, right: Constant): Equals = Equals(left, Right(right))
}

case class NotEquals(left: Variable, right: Either[Variable, Constant]) extends VariableConstraint {}

object NotEquals {
  def apply(left: Variable, right: Variable): NotEquals = NotEquals(left, Left(right))

  def apply(left: Variable, right: Constant): NotEquals = NotEquals(left, Right(right))
}

case class OfSort(left: Variable, right: Sort) extends VariableConstraint {}

case class NotOfSort(left: Variable, right: Sort) extends VariableConstraint {}


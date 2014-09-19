package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait VariableConstraint {

  def compileNotOfSort : Set[VariableConstraint] = {
    this match {
      case Equals(_, _) | NotEquals(_, _) | OfSort(_, _) => Set(this)
      case NotOfSort(v, s) => s.elements.map(element => NotEquals(v, Right(element))).toSet[VariableConstraint]
    }
  }
}


case class Equals(left : Variable, right : Either[Variable, Constant]) extends VariableConstraint {}

case class NotEquals(left : Variable, right : Either[Variable, Constant]) extends VariableConstraint {}

case class OfSort(left : Variable, right : Sort) extends VariableConstraint {}

case class NotOfSort(left : Variable, right : Sort) extends VariableConstraint {}


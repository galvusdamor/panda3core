package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait VariableConstraint {

  def compileNotOfSort: Set[VariableConstraint] = {
    this match {
      case Equal(_, _) | NotEqual(_, _) | OfSort(_, _) => Set(this)
      case NotOfSort(v, s) => s.elements.map(element => NotEqual(v, Right(element))).toSet[VariableConstraint]
    }
  }
}

// the 4 kinds of constraints the CSPs currently support

case class Equal(left: Variable, right: Either[Variable, Constant]) extends VariableConstraint {

  override def equals(that: Any) =
    that match {
      case Equal(thatLeft, thatRight) =>
        val thatRightContent: Any = thatRight match {case Left(v) => v; case Right(c) => c}
        val thisRightContent: Any = this.right match {case Left(v) => v; case Right(c) => c}

        (thatLeft == this.left && thatRight == this.right) || (this.left == thatRightContent && thisRightContent == thatLeft)
      case _ => false
    }

}

object Equal {
  def apply(left: Variable, right: Variable): Equal = Equal(left, Left(right))

  def apply(left: Variable, right: Constant): Equal = Equal(left, Right(right))
}

case class NotEqual(left: Variable, right: Either[Variable, Constant]) extends VariableConstraint {
  override def equals(that: Any) =
    that match {
      case NotEqual(thatLeft, thatRight) =>
        val thatRightContent: Any = thatRight match {case Left(v) => v; case Right(c) => c}
        val thisRightContent: Any = this.right match {case Left(v) => v; case Right(c) => c}

        (thatLeft == this.left && thatRight == this.right) || (this.left == thatRightContent && thisRightContent == thatLeft)
      case _ => false
    }
}

object NotEqual {
  def apply(left: Variable, right: Variable): NotEqual = NotEqual(left, Left(right))

  def apply(left: Variable, right: Constant): NotEqual = NotEqual(left, Right(right))
}

case class OfSort(left: Variable, right: Sort) extends VariableConstraint {}

case class NotOfSort(left: Variable, right: Sort) extends VariableConstraint {}


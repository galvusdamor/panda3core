package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}

/**
 * Variable Constraints are symbolic representations of relations between variables.
 * A [[CSP]] can handle constraint networks expressed with relations between variables expressed by VariableConstraints.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
sealed trait VariableConstraint {

  /** Returns an equivalent set of constraints, which does not contains [[NotOfSort]] constraints. These will be compiled into [[NotEqual]] constraints. */
  def compileNotOfSort: Set[VariableConstraint] = {
    this match {
      case Equal(_, _) | NotEqual(_, _) | OfSort(_, _) => Set(this)
      case NotOfSort(v, s) => s.elements.map(element => NotEqual(v, Right(element))).toSet[VariableConstraint]
    }
  }
}

// the 4 kinds of constraints the CSPs currently support

/**
 * Represents the constraint v_1 = v_2 or v = c, i.e. either forced equality between two variables or a variable and a constant.
 */
case class Equal(left: Variable, right: Either[Variable, Constant]) extends VariableConstraint {

  /** equals respects the equivalence of v_1 = v_2 and v_2 = v_1 */
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
  //convenience methods for creating equality constraints
  def apply(left: Variable, right: Variable): Equal = Equal(left, Left(right))

  def apply(left: Variable, right: Constant): Equal = Equal(left, Right(right))
}

/**
 * Represents the constraint v_1 != v_2 or v != c, i.e. either forced un-equality between two variables or a variable and a constant.
 */
case class NotEqual(left: Variable, right: Either[Variable, Constant]) extends VariableConstraint {

  /** equals respects the equivalence of v_1 = v_2 and v_2 = v_1 */
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
  //convenience methods for creating equality constraints
  def apply(left: Variable, right: Variable): NotEqual = NotEqual(left, Left(right))

  def apply(left: Variable, right: Constant): NotEqual = NotEqual(left, Right(right))
}

/**
 * Represents the constraint v_1 element-of S, for some sort S
 */
case class OfSort(left: Variable, right: Sort) extends VariableConstraint {}

/**
 * Represents the constraint v_1 not-element-of S, for some sort S
 */
case class NotOfSort(left: Variable, right: Sort) extends VariableConstraint {}
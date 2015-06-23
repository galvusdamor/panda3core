package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort, Value, Variable}

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
      case NotOfSort(v, s) => s.elements.map(element => NotEqual(v, element)).toSet[VariableConstraint]
    }
  }

  val getVariables: Seq[Variable]

  def substitute(sub: Substitution): VariableConstraint
}


// the 4 kinds of constraints the CSPs currently support
/**
 * Represents the constraint v_1 = v_2 or v = c, i.e. either forced equality between two variables or a variable and a constant.
 */
case class Equal(left: Variable, right: Value) extends VariableConstraint {

  /** equals respects the equivalence of v_1 = v_2 and v_2 = v_1 */
  override def equals(that: Any) =
    that match {
      case Equal(thatLeft, thatRight) => (thatLeft == this.left && thatRight == this.right) || (this.left == thatRight && this.right == thatLeft)
      case _ => false
    }

  override val getVariables = if (right.isInstanceOf[Variable]) right.asInstanceOf[Variable] :: left :: Nil else left :: Nil

  override def substitute(sub: Substitution): VariableConstraint = {
    val newLeft = sub(left)
    right match {
      case v: Variable => Equal(newLeft, sub(v))
      case c: Constant => Equal(newLeft, c)
    }
  }
}


/**
 * Represents the constraint v_1 != v_2 or v != c, i.e. either forced un-equality between two variables or a variable and a constant.
 */
case class NotEqual(left: Variable, right: Value) extends VariableConstraint {

  /** equals respects the equivalence of v_1 = v_2 and v_2 = v_1 */
  override def equals(that: Any) =
    that match {
      case NotEqual(thatLeft, thatRight) => (thatLeft == this.left && thatRight == this.right) || (this.left == thatRight && this.right == thatLeft)
      case _ => false
    }

  override val getVariables = if (right.isInstanceOf[Variable]) right.asInstanceOf[Variable] :: left :: Nil else left :: Nil

  override def substitute(sub: Substitution): VariableConstraint = {
    val newLeft = sub(left)
    right match {
      case v: Variable => NotEqual(newLeft, sub(v))
      case c: Constant => NotEqual(newLeft, c)
    }
  }
}


/**
 * Represents the constraint v_1 element-of S, for some sort S
 */
case class OfSort(left: Variable, right: Sort) extends VariableConstraint {
  override val getVariables = left :: Nil

  override def substitute(sub: Substitution): VariableConstraint = OfSort(sub(left), right)
}

/**
 * Represents the constraint v_1 not-element-of S, for some sort S
 */
case class NotOfSort(left: Variable, right: Sort) extends VariableConstraint {
  override val getVariables = left :: Nil

  override def substitute(sub: Substitution): VariableConstraint = NotOfSort(sub(left), right)

}
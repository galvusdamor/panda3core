package de.uniulm.ki.panda3.efficient.csp

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class VariableConstraint(constraintType : Int, variable : Int, other : Int) {

}

object VariableConstraint{
  val EQUALVARIABLE = 0
  val UNEQUALVARIABLE = 1
  val EQUALCONSTANT = 2
  val UNEQUALCONSTANT = 3
  val OFSORT = 4
  val NOTOFSORT = 5
}

package de.uniulm.ki.panda3.efficient.csp

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientVariableConstraint(constraintType: Int, variable: Int, other: Int) {
  assert(constraintType >= 0 && constraintType <= 5)
  assert(variable >= 0)
  assert(other >= 0)

  def addToVariableIndexIfGreaterEqualThen(offset: Int, ifGEQThen: Int): EfficientVariableConstraint = {
    var newVariable = variable
    if (newVariable >= ifGEQThen) newVariable += offset
    var newOther = other
    if (newOther >= ifGEQThen) newOther += offset

    if (constraintType == EfficientVariableConstraint.EQUALVARIABLE || constraintType == EfficientVariableConstraint.UNEQUALVARIABLE)
      EfficientVariableConstraint(constraintType, newVariable, newOther)
    else
      EfficientVariableConstraint(constraintType, newVariable, other) // don't update if it is a sort of a constant
  }
}

object EfficientVariableConstraint {
  val EQUALVARIABLE   = 0
  val UNEQUALVARIABLE = 1
  val EQUALCONSTANT   = 2
  val UNEQUALCONSTANT = 3
  val OFSORT          = 4
  val NOTOFSORT       = 5
}
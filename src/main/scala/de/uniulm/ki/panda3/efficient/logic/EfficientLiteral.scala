package de.uniulm.ki.panda3.efficient.logic

/**
  * Represents an efficient literal
  *
  * If the value of a variable is negative it is in fact a constant. See [[de.uniulm.ki.panda3.efficient.switchConstant]]
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientLiteral(predicate: Int, isPositive: Boolean, parameterVariables: Array[Int]) {

  def checkPredicateAndSign(other: EfficientLiteral): Boolean = predicate == other.predicate && isPositive == other.isPositive
}
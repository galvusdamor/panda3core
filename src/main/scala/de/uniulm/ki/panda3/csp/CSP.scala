package de.uniulm.ki.panda3.csp

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait CSP {

  val variables: Array[Variable]
  val constraints: Array[VariableConstraint]

}
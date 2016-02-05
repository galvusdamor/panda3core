package de.uniulm.ki.panda3

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object symbolic {
  val FORUMLASNOTSUPPORTED: String = "arbitrary formulas (in preconditions and effects)."
  val NONSIMPLEMETHOD: String  = "non-simple decomposition methods"
  val REINSTANTIATINGPLANSINOUTSIDEMETHODS = "re-instantiating plan that are not part of methods"

  def noSupport(message: String): Nothing = throw new UnsupportedOperationException("The current version of PANDA3 does not support " + message)
}

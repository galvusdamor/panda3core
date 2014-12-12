package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Sort

/**
 * Represents variables of a [[CSP]].
 * Each variable has a name and it belongs to some [[Sort]].
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Variable(name: String, sort: Sort) {

}

object Variable {
  private var globalVariableCounter = 42
  private val generatedVariablePrefix = "generated_variable_#"

  /**
   * Returns a completely new variable of the given sort.
   * This function assumes, that there will never be a variable with the name prefix [[generatedVariablePrefix]], except those returned by this function
   *
   * This function has side-effects
   */
  def newVariable(sort: Sort): Variable = {
    val v = Variable(generatedVariablePrefix + globalVariableCounter, sort)
    globalVariableCounter = globalVariableCounter + 1

    v
  }
}
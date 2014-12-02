package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Sort

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Variable(name: String, sort: Sort) {

}

object Variable {
  private var globalVariableCounter = 42
  private val generatedVariablePrefix = "generated_variable_#"

  def newVariable(sort : Sort) : Variable = {
    val v = Variable(generatedVariablePrefix + globalVariableCounter,sort)
    globalVariableCounter = globalVariableCounter + 1

    v
  }
}
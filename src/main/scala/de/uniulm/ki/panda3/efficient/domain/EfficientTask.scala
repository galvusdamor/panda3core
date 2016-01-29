package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.VariableConstraint
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral

import scala.collection.mutable.ArrayBuffer

/**
  * The representation of a task in the efficient domain.
  *
  * The parameters of this task are numbered 0..sz(parameterSorts) and the ith sort is the type of the variable i.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTask(isPrimitive: Boolean, parameterSorts: Array[Int], constraints: Array[VariableConstraint], precondition: Array[EfficientLiteral],
                         effect: Array[EfficientLiteral]) {

  /** given a literal of this task and the actual parameters of this task, it returns the actual argumtens of the literal*/
  def getArgumentsOfLiteral(taskArguments : Array[Int], literal : EfficientLiteral) :Array[Int] = {
    val arguments = new Array[Int](literal.parameterVariables.length)
    var i = 0
    while (i < literal.parameterVariables.length){
      arguments(i) = taskArguments(literal.parameterVariables(i))
      i += 1
    }
    arguments
  }

}
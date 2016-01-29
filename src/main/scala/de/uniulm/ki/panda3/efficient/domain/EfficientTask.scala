package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.VariableConstraint
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral

/**
  * The representation of a task in the efficient domain.
  *
  * The parameters of this task are numbered 0..sz(parameterSorts) and the ith sort is the type of the variable i.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTask(isPrimitive: Boolean, parameterSorts: Array[Int], constraints: Array[VariableConstraint], precondition: Array[EfficientLiteral],
                         effect: Array[EfficientLiteral]) {

}
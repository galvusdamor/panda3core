package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientMakeLiteralsUnUnifiable(plan: EfficientPlan, variable1: Int, variable2: Int) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = Array(EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, variable1, variable2))
}

object EfficientMakeLiteralsUnUnifiable {
  def apply(plan: EfficientPlan, literal1Parameters: Array[Int], literal2Parameters: Array[Int]): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    var i = 0
    while (i < literal1Parameters.length) {
      if (!plan.variableConstraints.areEqual(literal1Parameters(i), literal2Parameters(i)))
        buffer append EfficientMakeLiteralsUnUnifiable(plan,literal1Parameters(i), literal2Parameters(i))
      i += 1
    }
    buffer.toArray
  }
}

package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientMakeLiteralsUnUnifiable(plan: EfficientPlan, resolvedFlaw : EfficientFlaw , variable1: Int, variable2: Int) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = Array(EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, variable1, variable2))
}

object EfficientMakeLiteralsUnUnifiable {
  def apply(plan: EfficientPlan, flaw : EfficientFlaw, literal1Parameters: Array[Int], literal2Parameters: Array[Int]): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    var i = 0
    while (i < literal1Parameters.length) {
      if (!plan.variableConstraints.areEqual(literal1Parameters(i), literal2Parameters(i)))
        buffer append EfficientMakeLiteralsUnUnifiable(plan,flaw, literal1Parameters(i), literal2Parameters(i))
      i += 1
    }
    buffer.toArray
  }
}

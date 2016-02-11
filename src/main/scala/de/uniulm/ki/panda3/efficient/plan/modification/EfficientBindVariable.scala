package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientBindVariable(plan: EfficientPlan, variable: Int, constant: Int) extends EfficientModification {
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALCONSTANT, variable, constant))
}

object EfficientBindVariable {
  def apply(plan: EfficientPlan, variable: Int): Array[EfficientModification] = {
    val modificationBuffer = new ArrayBuffer[EfficientModification]()
    val it = plan.variableConstraints.getRemainingDomain(variable).iterator
    while (it.hasNext) {
      val constant = it.next()
      modificationBuffer append EfficientBindVariable(plan, variable, constant)
    }
    modificationBuffer.toArray
  }
}
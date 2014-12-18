package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Equal, Variable, VariableConstraint}
import de.uniulm.ki.panda3.logic.Constant
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class BindVariableToValue(plan: Plan, variable: Variable, value: Constant) extends Modification {
  override def addedVariableConstraints: Seq[VariableConstraint] = Equal(variable, value) :: Nil
}

object BindVariableToValue {
  def apply(plan: Plan, variable: Variable): Seq[BindVariableToValue] = plan.variableConstraints.reducedDomainOf(variable).toSeq map { c => BindVariableToValue(plan, variable, c)}
}

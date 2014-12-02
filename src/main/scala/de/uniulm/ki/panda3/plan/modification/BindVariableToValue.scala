package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Equals, Variable, VariableConstraint}
import de.uniulm.ki.panda3.logic.Constant
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class BindVariableToValue(variable: Variable, value: Constant) extends Modification {
  override def addedVariableConstraints: Seq[VariableConstraint] = Equals(variable, value) :: Nil
}

object BindVariableToValue {
  def apply(plan: Plan, variable: Variable): Seq[BindVariableToValue] = plan.variableConstraints.reducedDomainOf(variable).toSeq map { c => BindVariableToValue(variable, c)}
}

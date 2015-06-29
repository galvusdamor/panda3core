package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.{NotEqual, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class MakeLiteralsUnUnifiable(plan: Plan, constraint: NotEqual) extends Modification {
  override def addedVariableConstraints: Seq[VariableConstraint] = constraint :: Nil
}

object MakeLiteralsUnUnifiable {
  def apply(plan: Plan, a: Literal, b: Literal): Seq[MakeLiteralsUnUnifiable] = (a !?! b)(plan.variableConstraints) map { c => MakeLiteralsUnUnifiable(plan, c)}
}

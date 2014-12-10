package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{NotEqual, VariableConstraint}
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class MakeLiteralsUnUnifiable(constraint: NotEqual) extends Modification {
  override def addedVariableConstraints: Seq[VariableConstraint] = constraint :: Nil
}

object MakeLiteralsUnUnifiable {
  def apply(plan: Plan, a: Literal, b: Literal): Seq[MakeLiteralsUnUnifiable] = (a !?! b)(plan.variableConstraints) map { c => MakeLiteralsUnUnifiable(c)}
}

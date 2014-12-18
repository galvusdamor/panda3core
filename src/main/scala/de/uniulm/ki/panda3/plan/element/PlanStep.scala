package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.csp.{CSP, Variable}
import de.uniulm.ki.panda3.domain.Task
import de.uniulm.ki.panda3.logic.Literal

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class PlanStep(id: Int, schema: Task, arguments: Seq[Variable]) {

  /** returns a version of the preconditions */
  lazy val substitutedPreconditions: Seq[Literal] = schema.preconditions map substitute
  /** returns a version of the effects */
  lazy val substitutedEffects: Seq[Literal] = schema.effects map substitute

  /** check whether two literals are identical given a CSP */
  def =?=(that: PlanStep)(csp: CSP) = this.schema == that.schema &&
    ((this.arguments zip that.arguments) forall { case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2)})

  private def substitute(literal: Literal): Literal = schema.substitute(literal, arguments)
}
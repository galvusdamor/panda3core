package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.domain.{DomainUpdatable, Task}
import de.uniulm.ki.panda3.logic.{Literal, Variable}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class PlanStep(id: Int, schema: Task, arguments: Seq[Variable]) extends DomainUpdatable {

  /** returns a version of the preconditions */
  lazy val substitutedPreconditions: Seq[Literal] = schema.preconditions map substitute
  /** returns a version of the effects */
  lazy val substitutedEffects: Seq[Literal] = schema.effects map substitute

  /** check whether two literals are identical given a CSP */
  def =?=(that: PlanStep)(csp: CSP) = this.schema == that.schema &&
    ((this.arguments zip that.arguments) forall {case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2)})

  def indexOfPrecondition(l: Literal, csp: CSP): Int = indexOf(l, substitutedPreconditions, csp)

  private def indexOf(l: Literal, ls: Seq[Literal], csp: CSP): Int = (ls.zipWithIndex foldLeft -1) {case (i, (nl, j)) => if ((l =?= nl)(csp)) j else i}

  def indexOfEffect(l: Literal, csp: CSP): Int = indexOf(l, substitutedEffects, csp)

  private def substitute(literal: Literal): Literal = schema.substitute(literal, arguments)

  override def update(domainUpdate: DomainUpdate): PlanStep = PlanStep(id, schema.update(domainUpdate), arguments map {_.update(domainUpdate)})
}
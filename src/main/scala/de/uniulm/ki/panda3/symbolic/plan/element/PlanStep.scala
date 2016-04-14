package de.uniulm.ki.panda3.symbolic.plan.element

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp.{CSP, Substitution}
import de.uniulm.ki.panda3.symbolic.domain.updates.{DomainUpdate, ExchangePlanStep}
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, ReducedTask, DomainUpdatable, Task}
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Variable}
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.util.HashMemo

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PlanStep(id: Int, schema: Task, arguments: Seq[Variable])
  extends DomainUpdatable with PrettyPrintable {


  // TODO: this might cause problems in the wrapper (two decompositon methods might be judges as equal if they really are not), but is necessary to achieve at least a decent performance
  // for the symbolic planner
  override def equals(o: Any): Boolean = o match {
    case step: PlanStep => id == step.id && schema.name == step.schema.name
    case _              => false
  }

  override val hashCode: Int = id

  if (arguments.size != schema.parameters.size) {
    System.out.println("The number of parameters given in a plan step definition does not match the number that was given in the definition of the task schema.")
  }
  assert(arguments.size == schema.parameters.size)
  // TODO: test whether it is a subsort relation instead
  //assert((arguments zip schema.parameters) forall {case (a,b) => a.sort == b.sort})

  /** returns a version of the preconditions */
  lazy val substitutedPreconditions: Seq[Literal] = schema match {
    case reduced: ReducedTask => reduced.precondition.conjuncts map substitute
    case _                    => noSupport(FORUMLASNOTSUPPORTED)
  }
  /** returns a version of the effects */
  lazy val substitutedEffects      : Seq[Literal] = schema match {
    case reduced: ReducedTask => reduced.effect.conjuncts map substitute
    case _                    => noSupport(FORUMLASNOTSUPPORTED)
  }

  lazy val schemaParameterSubstitution = Substitution(schema.parameters, arguments)

  ///** check whether two literals are identical given a CSP */
  //def =?=(that: PlanStep)(csp: CSP): Boolean = this.schema == that.schema &&
  //  ((this.arguments zip that.arguments) forall { case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2) })

  def indexOfPrecondition(l: Literal, csp: CSP): Int = indexOf(l, substitutedPreconditions, csp)

  private def indexOf(l: Literal, ls: Seq[Literal], csp: CSP): Int = (ls.zipWithIndex foldLeft -1) { case (i, (nl, j)) => if ((l =?= nl) (csp)) j else i }

  def indexOfEffect(l: Literal, csp: CSP): Int = indexOf(l, substitutedEffects, csp)

  private def substitute(literal: Literal): Literal = schema.substitute(literal, arguments)

  override def update(domainUpdate: DomainUpdate): PlanStep = domainUpdate match {
    case ExchangePlanStep(oldps, newps) => if (oldps == this) newps else this
    case _                              => PlanStep(id, schema.update(domainUpdate), arguments map { _.update(domainUpdate) })
  }

  /** returns a short information about the object */
  override def shortInfo: String = id + ":" + schema.shortInfo

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + (arguments map {
    _.shortInfo
  }).mkString("(", ", ", ")")

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo + "\npreconditions:\n" +
    (substitutedPreconditions map {
      "\t" + _.shortInfo
    }).mkString("\n") + "\neffects:\n" + (substitutedEffects map {
    "\t" + _.shortInfo
  }).mkString("\n")
}
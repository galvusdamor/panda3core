package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.csp._

/**
 * A simple literal in First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Literal(predicate: Predicate, isInverted: Boolean, parameterVariables: Seq[Variable]) extends Formula {

  /** negated version of the literal */
  lazy val negate: Literal = copy(isInverted = !isInverted)

  /** check whether two literals are identical given a CSP */
  def =?=(that: Literal)(csp: CSP): Boolean = this.predicate == that.predicate && this.isInverted == that.isInverted &&
    ((this.parameterVariables zip that.parameterVariables) forall { case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2)})


  /**
   * check whether two literals can be unified given a CSP
   * @return an option to the mgu, if None, there is no such unifier
   */
  def #?#(that: Literal)(csp: CSP): Option[Seq[VariableConstraint]] = if (this.predicate != that.predicate || this.isInverted != that.isInverted)
    None
  else {
    // try building a unification and test it
    val result: (CSP, IndexedSeq[VariableConstraint]) =
      (this.parameterVariables zip that.parameterVariables).foldLeft((csp, Vector[VariableConstraint]()))(
      {
        case ((currentCSP, unification), (v1, v2)) =>
          if (currentCSP.isSolvable == Some(false) || csp.getRepresentative(v1) == csp.getRepresentative(v2)) (currentCSP, unification)
          else if (currentCSP.areCompatible(v1, v2) == Option(false)) (UnsolvableCSP, Vector())
          else (currentCSP.addConstraint(Equals(v1, v2)), unification :+ Equals(v1, v2))
      })
    // if the resulting CSP is not solvable any more, the two literals aren't unifiable
    if (result._1.isSolvable != Some(false))
      Some(result._2)
    else None
  }
}
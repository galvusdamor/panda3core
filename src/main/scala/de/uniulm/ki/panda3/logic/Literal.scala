package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.csp.{CSP, Variable}

/**
 * A simple literal in First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Literal(predicate : Predicate, isInverted : Boolean, parameterVariables : Seq[Variable]) {

  /** check whether two literals are identical given a CSP */
  def =?=(that : Literal)(csp : CSP) = this.predicate == that.predicate && this.isInverted == that.isInverted &&
    ((this.parameterVariables zip that.parameterVariables) forall { case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2)})

  //  /** check whether two literals are identical given a CSP */
  //  def <?>(that : Literal)(csp : CSP) = this.predicate == that.predicate &&
  //    ((this.parameterVariables zip that.parameterVariables) forall { case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2)})

}

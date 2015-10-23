package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.util.HashMemo

/**
 * A simple literal in First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Literal(predicate: Predicate, isPositive: Boolean, parameterVariables: Seq[Variable]) extends Formula with PrettyPrintable with HashMemo {

  /** negated version of the literal */
  lazy val negate: Literal = copy(isPositive = !isPositive)

  val isNegative: Boolean = !isPositive

  /** check whether two literals are identical given a CSP */
  def =?=(that: Literal)(csp: CSP): Boolean = this.predicate == that.predicate && this.isPositive == that.isPositive &&
    ((this.parameterVariables zip that.parameterVariables) forall { case (v1, v2) => csp.getRepresentative(v1) == csp.getRepresentative(v2)})


  /**
   * check whether two literals can be unified given a CSP
   * @return an option to the mgu, if None, there is no such unifier
   */
  def #?#(that: Literal)(csp: CSP): Option[Seq[Equal]] = if (this.predicate != that.predicate || this.isPositive != that.isPositive)
    None
  else {
    val parameterPairs = this.parameterVariables zip that.parameterVariables

    // try building a unification and test it
    val result: (CSP, IndexedSeq[Equal]) = parameterPairs.foldLeft((csp, Vector[Equal]()))(
    {
      case ((currentCSP, unification), (v1, v2)) =>
        if (currentCSP.isSolvable == Some(false) || csp.getRepresentative(v1) == csp.getRepresentative(v2)) (currentCSP, unification)
        else if (currentCSP.areCompatible(v1, v2) == Option(false)) (UnsolvableCSP, Vector())
        else (currentCSP.addConstraint(Equal(v1, v2)), unification :+ Equal(v1, v2))
    })
    // if the resulting CSP is not solvable any more, the two literals aren't unifiable
    if (result._1.isSolvable != Some(false))
      Some(result._2)
    else None
  }

  /**
   * Returns a list of all differentiater, i.e. a all possible constraints that can make the two literals unequal
   */
  def !?!(that: Literal)(csp: CSP): Seq[NotEqual] = if (this.predicate != that.predicate || this.isPositive != that.isPositive) Nil
  else (this.parameterVariables zip that.parameterVariables) collect { case (v1, v2) if csp.getRepresentative(v1) != csp.getRepresentative(v2) => NotEqual(v1, v2)}

  override def update(domainUpdate: DomainUpdate): Literal = Literal(predicate.update(domainUpdate), isPositive, parameterVariables map {_.update(domainUpdate)})

  /** returns a short information about the object */
  override def shortInfo: String = (if (!isPositive) "!" else "") + predicate.shortInfo + (parameterVariables map {_.shortInfo}).mkString("(", ", ", ")")

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = (if (!isPositive) "!" else "") + predicate.shortInfo + (parameterVariables map {_.longInfo}).mkString("(", ", ", ")")
}
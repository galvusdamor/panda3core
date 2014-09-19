package de.uniulm.ki.panda3.csp

/**
 * Contains a CSP containing equals, unequals, of-sort and not-of-sort constraints
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait CSP {

  def variables : Set[Variable]

  def constraints : Set[VariableConstraint]

  def reducedDomainOf(v : Variable) : Iterable[Object]

  /** If true is returned it is guaranteed, that a solution exists is v1 and v2 are set equal. Likewise, if false is returned such a CSP is unsolvable. */
  def areCompatible(v1 : Variable, v2 : Variable) : Option[Boolean]

  def isSolvable : Option[Boolean]

  /** returns best known unique representative for a given variable */
  def getRepresentative(v : Variable) : Either[Variable, Object]

  /** computes the solution of this CSP, might be computationally expensive */
  def solution : Map[Variable, Object]

  def addConstraint(constraint : VariableConstraint) : CSP
}
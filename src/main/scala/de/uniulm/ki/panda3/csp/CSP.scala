package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Constant

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait CSP {

  def variables: Set[Variable]

  def constraints: List[VariableConstraint]

  def reducedDomainOf(v: Variable): Iterable[Constant]

  def areCompatible(v1: Variable, v2: Variable): Option[Boolean]

  def areCompatible(vOrC1: Either[Variable, Constant], vOrC2: Either[Variable, Constant]): Option[Boolean] = (vOrC1, vOrC2) match {
    case (Left(v1), Left(v2)) => areCompatible(v1, v2)
    case (Right(c), Left(v)) => if (reducedDomainOf(v) exists {
      _ == c
    }) None
    else Some(false)
    case (Left(v), Right(c)) => if (reducedDomainOf(v) exists {
      _ == c
    }) None
    else Some(false)
    case (Right(c1), Right(c2)) => Some(c1 == c2)
  }

  def addConstraint(constraint: VariableConstraint): CSP

  def addConstraints(constraints: Seq[VariableConstraint]): CSP = (constraints foldLeft this)({ case (c, vc) => c.addConstraint(vc)})

  def addVariable(variable: Variable): CSP

  def addVariables(variables: Seq[Variable]): CSP = (variables foldLeft this)({ case (c, v) => c.addVariable(v)})

  def isSolvable: Option[Boolean]

  /** computes the solution of this CSP, might be computationally expensive */
  def solution: Option[Map[Variable, Constant]]

  /** returns best known unique representative for a given variable */
  def getRepresentative(v: Variable): Either[Variable, Constant]

  /** returns best known unique representative for a given variable */
  def getRepresentative(vOrC: Either[Variable, Constant]): Either[Variable, Constant] = vOrC match {
    case Left(v) => getRepresentative(v)
    case _ => vOrC // constant
  }

  // boxing
  def equal(v1: Variable, v2: Variable): Boolean = equal(Left(v1), Left(v2))

  def equal(v1: Variable, c2: Constant): Boolean = equal(Left(v1), Right(c2))

  def equal(c1: Constant, v2: Variable): Boolean = equal(Right(c1), Left(v2))

  def equal(c1: Constant, c2: Constant): Boolean = equal(Right(c1), Right(c2))

  def equal(vOrC1: Either[Variable, Constant], vOrC2: Either[Variable, Constant]): Boolean = getRepresentative(vOrC1) == getRepresentative(vOrC2)
}

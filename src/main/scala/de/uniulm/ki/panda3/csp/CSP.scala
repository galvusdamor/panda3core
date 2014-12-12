package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Constant

/**
 * Handels Constraint-Satisfaction-Problems. The implementation decides which types of constraints can be handled.
 *
 * A CSP support several operations, most importantly it can be solved, i.e. an assignment of all variables to constants can be calculated.
 * Without solving the CSP, one can determine for each variable (and constant) a canonical representative, i.e. another variable or constant.
 * If this representative is equal for two variables, they must be equal.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait CSP {

  /** returns a list of all variables of the CSP */
  def variables: Set[Variable]

  /** returns a list of all constraints from which this CSP was build. This list does not contain inferred constraints */
  def constraints: List[VariableConstraint]

  /** returns all (potentially) possible values of v in this CSP, this does not imply that for every such constant c, there is a solution in which v = c */
  def reducedDomainOf(v: Variable): Iterable[Constant]

  /**
   * checks whether it is possible to unify two variables.
   * If Some(false) is returned this is not possible, i.e. the CSP implies v1 != v2.
   * If Some(true) is returned it is guaranteed, that if this CSP is solvable so is it if v1=v2 is added as a constraint.
   * In any other case None is returned.
   */
  def areCompatible(v1: Variable, v2: Variable): Option[Boolean]

  /**
   * checks whether it is possible to unify two variables.
   * If Some(false) is returned this is not possible, i.e. the CSP implies v1 != v2.
   * If Some(true) is returned it is guaranteed, that if this CSP is solvable so is it if v1=v2 is added as a constraint.
   * In any other case None is returned.
   */
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

  /** returns a new CSP containing all current constraints and the constraint passed as an argument */
  def addConstraint(constraint: VariableConstraint): CSP

  /** returns a new CSP containing all current constraints and the constraints passed as arguments */
  def addConstraints(constraints: Seq[VariableConstraint]): CSP = (constraints foldLeft this)({ case (c, vc) => c.addConstraint(vc)})

  /** returns a new CSP containing all current variables and the variable passed as an argument */
  def addVariable(variable: Variable): CSP

  /** returns a new CSP containing all current variables and the variables passed as arguments */
  def addVariables(variables: Seq[Variable]): CSP = (variables foldLeft this)({ case (c, v) => c.addVariable(v)})

  /** May return information on whether this CSP has a solution or not. If None is returned to information can be provided */
  def isSolvable: Option[Boolean]

  /** Returns a solution of this CSP, might be computationally expensive */
  def solution: Option[Map[Variable, Constant]]

  /** returns best known unique representative for a given variable */
  def getRepresentative(v: Variable): Either[Variable, Constant]

  /** returns best known unique representative for a given variable or constant */
  def getRepresentative(vOrC: Either[Variable, Constant]): Either[Variable, Constant] = vOrC match {
    case Left(v) => getRepresentative(v)
    case _ => vOrC // constant
  }

  // boxing
  /** determines whether two variables must be equal in this CSP */
  def equal(v1: Variable, v2: Variable): Boolean = equal(Left(v1), Left(v2))

  /** determines whether a variable and a constant must be equal in this CSP */
  def equal(v1: Variable, c2: Constant): Boolean = equal(Left(v1), Right(c2))

  /** determines whether a variable and a constant must be equal in this CSP */
  def equal(c1: Constant, v2: Variable): Boolean = equal(Right(c1), Left(v2))

  /** determines whether two constants must be equal in this CSP */
  def equal(c1: Constant, c2: Constant): Boolean = equal(Right(c1), Right(c2))

  /** determines whether two variables or constants must be equal in this CSP */
  def equal(vOrC1: Either[Variable, Constant], vOrC2: Either[Variable, Constant]): Boolean = getRepresentative(vOrC1) == getRepresentative(vOrC2)
}
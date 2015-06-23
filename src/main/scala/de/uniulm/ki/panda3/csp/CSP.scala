package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Value, Variable}

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
  def constraints: Seq[VariableConstraint]

  /** returns all (potentially) possible values of v in this CSP, this does not imply that for every such constant c, there is a solution in which v = c */
  def reducedDomainOf(v: Variable): Seq[Constant]

  /**
   * checks whether it is possible to unify two variables.
   * If Some(false) is returned this is not possible, i.e. the CSP implies v1 != v2.
   * If Some(true) is returned it is guaranteed, that if this CSP is solvable so is it if v1=v2 is added as a constraint.
   * In any other case None is returned.
   */
  def areCompatible(v1: Variable, v2: Variable): Option[Boolean]

  /** returns a new CSP containing all current constraints and the constraint passed as an argument */
  def addConstraint(constraint: VariableConstraint): CSP

  /** returns a new CSP containing all current variables and the variable passed as an argument */
  def addVariable(variable: Variable): CSP

  /** May return information on whether this CSP has a solution or not. If None is returned to information can be provided */
  def isSolvable: Option[Boolean]

  /** Returns a solution of this CSP, might be computationally expensive */
  def solution: Option[Map[Variable, Constant]]

  /** returns best known unique representative for a given variable */
  protected def getRepresentative(v: Variable): Value


  /**
   * checks whether it is possible to unify two variables.
   * If Some(false) is returned this is not possible, i.e. the CSP implies v1 != v2.
   * If Some(true) is returned it is guaranteed, that if this CSP is solvable so is it if v1=v2 is added as a constraint.
   * In any other case None is returned.
   */
  def areCompatible(v1: Value, v2: Value): Option[Boolean] = (v1, v2) match {
    case (var1: Variable, var2: Variable) => areCompatible(var1, var2)
    case (c: Constant, v: Variable) => if (getRepresentative(v) == c) Some(true) else if (reducedDomainOf(v) contains c) None else Some(false)
    case (v: Variable, c: Constant) => if (getRepresentative(v) == c) Some(true) else if (reducedDomainOf(v) contains c) None else Some(false)
    case (c1: Constant, c2: Constant) => Some(c1 == c2)
  }


  /** returns a new CSP containing all current constraints and the constraints passed as arguments */
  def addConstraints(constraints: Seq[VariableConstraint]): CSP = (constraints foldLeft this)({ case (c, vc) => c.addConstraint(vc) })

  /** returns a new CSP containing all current variables and the variables passed as arguments */
  def addVariables(variables: Seq[Variable]): CSP = (variables foldLeft this)({ case (c, v) => c.addVariable(v) })

  /** returns best known unique representative for a given variable or constant */
  def getRepresentative(value: Value): Value = value match {
    case v: Variable => getRepresentative(v)
    case _ => value // constant
  }

  /** determines whether two variables or constants must be equal in this CSP */
  def equal(v1: Value, v2: Value): Boolean = getRepresentative(v1) == getRepresentative(v2)
}
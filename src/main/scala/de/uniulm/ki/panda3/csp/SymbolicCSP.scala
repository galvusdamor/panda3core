package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Constant

import scala.collection.mutable


/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicCSP(variables : Set[Variable],
                       constraints : Set[VariableConstraint]) extends CSP {

  private var unequal : mutable.Set[(Variable, Variable)] = new mutable.HashSet[(Variable, Variable)]()

  private var remainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]]()

  // contains the union-find for all variables
  // entry may also point to Constant if it known that they can only have this value
  private var unionfind : mutable.Map[Variable, Either[Variable, Constant]] = new mutable.HashMap[Variable, Either[Variable, Constant]]()


  /**
   * Process all unprocessed constrains of the CSP and reduce maximally
   */
  def initialiseExplicitly(lastKConstraintsAreNew : Int = constraints.size,
                           previousUnequal : mutable.Set[(Variable, Variable)] = new mutable.HashSet[(Variable, Variable)](),
                           previousRemainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]](),
                           previousUnionfind : mutable.Map[Variable, Either[Variable, Constant]] = new mutable.HashMap[Variable, Either[Variable, Constant]]()) : Unit = {
    // get really new copies of the previous data structures
    unequal = previousUnequal.clone()
    remainingDomain = previousRemainingDomain.clone()
    unionfind = previousUnionfind.clone()

    // first part : transform all constraints into unequal or equal constraints
  }


  override def reducedDomainOf(v : Variable) : Iterable[Constant] = ???

  override def areCompatible(v1 : Variable, v2 : Variable) : Option[Boolean] = ???

  override def addConstraint(constraint : VariableConstraint) : CSP = ???

  override def isSolvable : Option[Boolean] = ???

  /** computes the solution of this CSP, might be computationally expensive */
  override def solution : Predef.Map[Variable, Constant] = ???

  /** returns best known unique representative for a given variable */
  override def getRepresentative(v : Variable) : Either[Variable, Constant] = ???
}
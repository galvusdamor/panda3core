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

  // this is only kept for the top elements of the union-find
  private var remainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]]()

  // contains the union-find for all variables
  // entry may also point to Constant if it known that they can only have this value
  private var unionFind : mutable.Map[Variable, Either[Variable, Constant]] = new mutable.HashMap[Variable, Either[Variable, Constant]]()

  private var isPotentiallySolvable = true

  /**
   * Process all unprocessed constrains of the CSP and reduce maximally
   */
  def initialiseExplicitly(lastKConstraintsAreNew : Int = constraints.size,
                           previousUnequal : mutable.Set[(Variable, Variable)] = new mutable.HashSet[(Variable, Variable)](),
                           previousRemainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]](),
                           previousUnionFind : mutable.Map[Variable, Either[Variable, Constant]] = new mutable.HashMap[Variable, Either[Variable, Constant]]()) : Unit = {
    // get really new copies of the previous data structures
    unequal = previousUnequal.clone()
    remainingDomain = previousRemainingDomain.clone()
    unionFind = previousUnionFind.clone()

    // add all new constraints
    for (originalConstraint <- constraints.drop(constraints.size - lastKConstraintsAreNew);
         constraint <- originalConstraint.compileNotOfSort)
    // treat each single constraint
      constraint match {
        case NotEquals(v1, Left(v2)) =>
          (getRepresentative(v1), getRepresentative(v2)) match {
            case (Left(rv1), Left(rv2)) => unequal.add((rv1, rv2))
            case (Left(rv), Right(const)) => remainingDomain(rv).remove(const)
            case (Right(const), Left(rv)) => remainingDomain(rv).remove(const)
            case (Right(const1), Right(const2)) => if (const1 == const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
          }
        case Equals(v1, Left(v2)) =>
          (getRepresentative(v1), getRepresentative(v2)) match {

            case (Left(rv1), Left(rv2)) => // set equal in unionfind
            case (Left(rv), Right(const)) => if (!remainingDomain(rv).contains(const)) isPotentiallySolvable = false
            else {
              // remove from variables and set equal with constant
            }
            case (Right(const), Left(rv)) => ??? // analogue

            case (Right(const1), Right(const2)) => if (const1 != const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
          }
      }


    override def reducedDomainOf(v : Variable) : Iterable[Constant] = ???

    override def areCompatible(v1 : Variable, v2 : Variable) : Option[Boolean] = ???

    override def addConstraint(constraint : VariableConstraint) : CSP = ???

    override def isSolvable : Option[Boolean] =
    {
      if (isPotentiallySolvable)
        None
      else Some(false)
    }

    /** computes the solution of this CSP, might be computationally expensive */
    override def solution : Predef.Map[Variable, Constant] = ???

    /** returns best known unique representative for a given variable */
    override def getRepresentative(v : Variable) : Either[Variable, Constant] =
    {
      unionFind(v) match {
        case Right(c) => Right(c)
        case Left(parent) =>
          if (parent == v) Left(v)
          else {
            val representative = getRepresentative(parent)
            unionFind(v) = representative
            representative
          }
      }
    }
  }
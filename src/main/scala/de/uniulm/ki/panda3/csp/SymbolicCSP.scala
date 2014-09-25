package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Sort}

import scala.collection.mutable
import scala.util.{Left, Right}

/**
 * Implementation of a symbolic CSP used as a part of a plan
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicCSP(variables : Set[Variable],
                       constraints : Set[VariableConstraint]) extends CSP {

  private var unequal : mutable.Set[(Variable, Variable)] = new mutable.HashSet[(Variable, Variable)]()

  // this is only kept for the top elements of the union-find
  private var remainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]]()

  private val unionFind : SymbolicUnionFind = new SymbolicUnionFind

  // if this flag is false, i.e. we know this CSP is unsolvable, the internal data might become inconsistent ... it is simply not necessary to have it still intact
  private var isPotentiallySolvable = true


  private def unitPropagation() : Unit = {

  }

  /** adds a single constraint to the CSP, but does not perform unit propagation */
  private def addSingleConstraint(constraint : VariableConstraint) : Unit = {
    // move a potential constant on the left side of equals to the right
    val equalsConstRight = constraint match {
      case Equals(v1, v2) => (getRepresentative(v1), getRepresentative(v2)) match {
        case (Right(const), Left(rv)) => Equals(rv, Right(const))
        case _ => Equals(v1, v2)
      }
      case x => x
    }


    val equalsConstEliminated = equalsConstRight match {
      case Equals(v1, v2) => (getRepresentative(v1), getRepresentative(v2)) match {
        case (Left(rv), Right(const)) => OfSort(rv, Sort("temp", Vector() :+ const))
        case _ => Equals(v1, v2)
      }
      case x => x
    }

    // all actually different cases
    equalsConstEliminated match {
      case NotEquals(v1, v2) =>
        (getRepresentative(v1), getRepresentative(v2)) match {
          case (Left(rv1), Left(rv2)) => unequal.add((rv1, rv2))
          case (Left(rv), Right(const)) => remainingDomain(rv).remove(const)
          case (Right(const), Left(rv)) => remainingDomain(rv).remove(const)
          case (Right(const1), Right(const2)) => if (const1 == const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
        }
      case Equals(v1, v2) => (getRepresentative(v1), getRepresentative(v2)) match {
        case (Left(rv1), Left(rv2)) => {
          // intersect domains
          val intersectionDomain = remainingDomain(rv1) intersect remainingDomain(rv2)

          if (intersectionDomain.size == 0)
            isPotentiallySolvable = false
          else {
            unionFind.assertEqual(rv1, Left(rv2))
            // find out which representative the union-find uses and remove the other from the real CSP
            if (unionFind.getRepresentative(rv1) == Left(rv1)) (rv2, rv1)
            else (rv1, rv2) match {
              case (remove, rep) =>
                remainingDomain.remove(remove)
                remainingDomain(rep) = intersectionDomain
            }
          }
        }
        case (Right(const1), Right(const2)) => if (const1 != const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
      }
      // sort of and var = const constraints
      case OfSort(v, sort) => getRepresentative(v) match {
        case Right(constant) => if (!sort.elements.contains(constraints)) isPotentiallySolvable = false
        case Left(rv) => {
          val intersectionDomain = remainingDomain(rv) filter { x => sort.elements.contains(x)}

          if (intersectionDomain.size == 0)
            isPotentiallySolvable = false
          else
            remainingDomain(rv) = intersectionDomain
        }
      }
    }
  }

  /**
   * Process all unprocessed constrains of the CSP and reduce maximally
   */
  def initialiseExplicitly(lastKConstraintsAreNew : Int = constraints.size,
                           previousUnequal : mutable.Set[(Variable, Variable)] = new mutable.HashSet[(Variable, Variable)](),
                           previousRemainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]](),
                           previousUnionFind : SymbolicUnionFind = new SymbolicUnionFind) : Unit = {
    // get really new copies of the previous data structures
    unequal = previousUnequal.clone()
    remainingDomain = previousRemainingDomain.clone()
    unionFind cloneFrom previousUnionFind

    // add all new constraints
    for (originalConstraint <- constraints.drop(constraints.size - lastKConstraintsAreNew);
         constraint <- originalConstraint.compileNotOfSort)
    // treat each single constraint
      addSingleConstraint(constraint)

    // unit propagation
    if (isPotentiallySolvable)
      unitPropagation()
  }


  override def reducedDomainOf(v : Variable) : Iterable[Constant] = getRepresentative(v) match {
    case Left(variable) => remainingDomain(variable)
    case Right(constant) => Vector() :+ constant
  }

  override def areCompatible(v1 : Variable, v2 : Variable) : Option[Boolean] = ???

  override def addConstraint(constraint : VariableConstraint) : CSP = ???

  override def isSolvable : Option[Boolean] = {
    if (isPotentiallySolvable)
      None
    else Some(false)
  }

  /** computes the solution of this CSP, might be computationally expensive */
  override def solution : Predef.Map[Variable, Constant] = ???

  /** returns best known unique representative for a given variable */
  override def getRepresentative(v : Variable) : Either[Variable, Constant] = ???

  /** returns best known unique representative for a given variable */
  override def getRepresentative(vOrC : Either[Variable, Constant]) : Either[Variable, Constant] = vOrC match {
    case Left(v) => getRepresentative(v)
    case _ => vOrC // constant
  }
}
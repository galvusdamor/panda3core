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
                       constraints : List[VariableConstraint]) extends CSP {

  private var unequal : mutable.Map[Variable, mutable.Set[Variable]] = new mutable.HashMap[Variable, mutable.Set[Variable]]()
  // this is only kept for the top elements of the union-find
  private var remainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]]()
  private val unionFind : SymbolicUnionFind = new SymbolicUnionFind
  private var isReductionComputed = false

  // if this flag is false, i.e. we know this CSP is unsolvable, the internal data might become inconsistent ... it is simply not necessary to have it still intact
  private var isPotentiallySolvable = true


  /** returns best known unique representative for a given variable */
  private def getRepresentativeUnsafe(v : Variable) : Either[Variable, Constant] = unionFind.getRepresentative(v)

  /** returns best known unique representative for a given variable */
  private def getRepresentativeUnsafe(vOrC : Either[Variable, Constant]) : Either[Variable, Constant] = vOrC match {
    case Left(v) => getRepresentativeUnsafe(v)
    case _ => vOrC // constant
  }


  /** function to detect all unit propagations */
  private def detectUnitPropagation() : Iterable[Variable] = remainingDomain filter { case (variable, values) => values.size == 1} map { case (variable, values) => variable}

  private def detectUnsolvability() = remainingDomain exists { case (variable, values) => values.size == 0}

  private def unitPropagation(toPropagate : Iterable[Variable] = detectUnitPropagation()) : Unit = if (toPropagate.size == 0) ()
  else {
    val x : Iterable[Variable] = (toPropagate map { variable =>
      if (!isPotentiallySolvable)
        Vector()
      else {
        val constant = remainingDomain(variable).last // this one exists and is the only element

        // remove this variable add assert
        unionFind.assertEqual(variable, Right(constant))
        remainingDomain.remove(variable)

        // propagate
        unequal(variable) foreach { other =>
          remainingDomain(other).remove(constant)
          if (remainingDomain(other).size == 0)
            isPotentiallySolvable = false
          unequal(other) -= variable
        }

        // find new propagations
        val newPropagation = unequal(variable) collect { case other if (remainingDomain(other).size == 1) => other}

        // removed from the datastructure
        unequal -= variable

        newPropagation
      }
    }).flatten.toSet // eliminate duplicates

    if (isPotentiallySolvable)
      unitPropagation(x)
  }

  /** adds a single constraint to the CSP, but does not perform unit propagation */
  private def addSingleConstraint(constraint : VariableConstraint) : Unit = {
    val equalsConstEliminated = constraint match {
      case equalConstr@Equals(v1, v2) => (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
        case (Left(rv), Right(const)) => OfSort(rv, Sort("temp", Vector() :+ const))
        case (Right(const), Left(rv)) => OfSort(rv, Sort("temp", Vector() :+ const))
        case _ => equalConstr
      }
      case x => x
    }

    // all actually different cases
    equalsConstEliminated match {
      case NotEquals(v1, v2) =>
        (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
          case (Left(rv1), Left(rv2)) => for (p <- (rv1, rv2) ::(rv2, rv1) :: Nil) p match {
            case (x, y) => unequal(x) += y
          }
          case (Left(rv), Right(const)) => remainingDomain(rv).remove(const)
          case (Right(const), Left(rv)) => remainingDomain(rv).remove(const)
          case (Right(const1), Right(const2)) => if (const1 == const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
        }
      case Equals(v1, v2) => (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
        case (Left(rv1), Left(rv2)) => {
          // intersect domains
          val intersectionDomain = remainingDomain(rv1) intersect remainingDomain(rv2)

          unionFind.assertEqual(rv1, Left(rv2))
          // find out which representative the union-find uses and remove the other from the real CSP
          if (getRepresentativeUnsafe(rv1) == Left(rv1)) (rv2, rv1)
          else (rv1, rv2) match {
            case (remove, rep) =>
              remainingDomain.remove(remove)
              remainingDomain(rep) = intersectionDomain
          }
        }
        case (Right(const1), Right(const2)) => if (const1 != const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
      }
      // sort of and var = const constraints
      case OfSort(v, sort) => getRepresentativeUnsafe(v) match {
        case Right(constant) => if (!sort.elements.contains(constraints)) isPotentiallySolvable = false
        case Left(rv) =>
          remainingDomain(rv) = remainingDomain(rv) filter { x => sort.elements.contains(x)}
      }
    }
  }

  /**
   * Process all unprocessed constrains of the CSP and reduce maximally
   */
  def initialiseExplicitly(previousUnequal : mutable.Map[Variable, mutable.Set[Variable]] = new mutable.HashMap[Variable, mutable.Set[Variable]](),
                           previousRemainingDomain : mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]](),
                           previousUnionFind : SymbolicUnionFind = new SymbolicUnionFind,
                           lastKConstraintsAreNew : Int = constraints.size,
                           newVariables : Set[Variable] = variables) : Unit = {
    // get really new copies of the previous data structures
    unequal = previousUnequal.clone()
    remainingDomain = previousRemainingDomain.clone()
    unionFind cloneFrom previousUnionFind

    // add completely new variables
    for (variable <- newVariables) {
      unionFind.addVariable(variable)
      remainingDomain(variable) = new mutable.HashSet[Constant]() ++ variable.sort.elements
      unequal(variable) = new mutable.HashSet[Variable]();
    }

    // add all new constraints
    for (originalConstraint <- constraints.drop(constraints.size - lastKConstraintsAreNew);
         constraint <- originalConstraint.compileNotOfSort)
    // treat each single constraint
      addSingleConstraint(constraint)

    if (detectUnsolvability())
      isPotentiallySolvable = false
    // unit propagation
    if (isPotentiallySolvable)
      unitPropagation()

    isReductionComputed = true
  }


  override def reducedDomainOf(v : Variable) : Iterable[Constant] = {
    if (!isReductionComputed) initialiseExplicitly()
    if (isPotentiallySolvable)
      getRepresentative(v) match {
        case Left(variable) => remainingDomain(variable)
        case Right(constant) => Vector() :+ constant
      }
    else
      Vector()
  }

  override def areCompatible(v1 : Variable, v2 : Variable) : Option[Boolean] = {
    if (!isReductionComputed) initialiseExplicitly()
    if (isPotentiallySolvable)
      (getRepresentative(v1), getRepresentative(v2)) match {
        case (Right(c1), Right(c2)) => if (c1 == c2) Some(true) else Some(false)
        case (Left(v1), Left(v2)) => if (v1 == v2) Some(true) else None
        case _ => None // possibly, but we are not sure
      }
    else
      Option(false)
  }

  override def addConstraint(constraint : VariableConstraint) : CSP = {
    val newVariables = (constraint match {
      case Equals(v1, Left(v2)) => Set(v1, v2)
      case Equals(v, Right(_)) => Set(v)
      case NotEquals(v1, Left(v2)) => Set(v1, v2)
      case NotEquals(v, Right(_)) => Set(v)
      case OfSort(v, _) => Set(v)
      case NotOfSort(v, _) => Set(v)
    }) -- variables

    val newCSP = SymbolicCSP(variables ++ newVariables, constraints :+ constraint)

    if (isReductionComputed)
      newCSP.initialiseExplicitly(unequal, remainingDomain, unionFind, 1, newVariables)

    newCSP
  }

  override def isSolvable : Option[Boolean] = {
    if (!isReductionComputed) initialiseExplicitly()

    if (isPotentiallySolvable)
      None
    else Some(false)
  }

  /** computes the solution of this CSP, might be computationally expensive */
  override def solution : Option[Map[Variable, Constant]] = {
    // returns partial solution
    def searchSolution(remainingDomain : Map[Variable, Set[Constant]]) : Option[Map[Variable, Constant]] = {
      if (remainingDomain.isEmpty) Some(Map())
      else {
        val resolutionVariable : (Variable, Set[Constant]) = remainingDomain.last // this will not fail, since the map is not empty
        val domainsWithout_1 : Map[Variable, Set[Constant]] = remainingDomain - resolutionVariable._1 // remove variable
        if (resolutionVariable._2.isEmpty) None
        else {
          resolutionVariable._2.foldLeft[Option[Map[Variable, Constant]]](None)({ case (ret@Some(_), _) => ret;
          case (None, const) => // try assigning _1 == const
            searchSolution(domainsWithout_1.map({ case (other, values) => if (unequal(resolutionVariable._1).contains(other)) other -> (values - const) else other -> values})) match {
              case Some(partialSolution) => Some(partialSolution + (resolutionVariable._1 -> const))
              case _ => None
            }
          })
        }
      }
    }

    if (!isReductionComputed) initialiseExplicitly()

    if (!isPotentiallySolvable)
      None
    else
      searchSolution((remainingDomain map { case (variable, viableConstants) => variable -> viableConstants.toSet}).toMap)
  }

  /** returns best known unique representative for a given variable */
  override def getRepresentative(v : Variable) : Either[Variable, Constant] = {
    if (!isReductionComputed) if (!isReductionComputed) initialiseExplicitly()
    unionFind.getRepresentative(v)
  }

  /** returns best known unique representative for a given variable */
  override def getRepresentative(vOrC : Either[Variable, Constant]) : Either[Variable, Constant] = vOrC match {
    case Left(v) => getRepresentative(v)
    case _ => vOrC // constant
  }
}
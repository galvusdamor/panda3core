package de.uniulm.ki.panda3.symbolic.csp

import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic.{Constant, Sort, Value, Variable}

import scala.collection.immutable.HashSet
import scala.collection.mutable

/**
 * CSP implemented using an object oriented structure.
 *
 * This CSP can handle four kinds of variable constraints: [[Equal]], [[NotEqual]], [[OfSort]], [[NotOfSort]].
 * It uses the AC3 algorithm to reduce the possible domains for every variable and maintains equivalence classes of equal variables.
 *
 * A solution can be generated via backtracking.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicCSP(variables: Set[Variable], constraints: Seq[VariableConstraint]) extends CSP {

  // holds equivalent variables
  private val unionFind: SymbolicUnionFind                            = new SymbolicUnionFind
  // contains information about unqeual variables
  private var unequal  : mutable.Map[Variable, mutable.Set[Variable]] = new mutable.HashMap[Variable, mutable.Set[Variable]]()
  // this is only kept for the top elements of the union-find
  private var remainingDomain: mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]]()
  // marker for the compuatation of the reduction
  private var isReductionComputed = false

  // if this flag is false, i.e. we know this CSP is unsolvable, the internal data might become inconsistent ... it is simply not necessary to have it still intact
  private var isPotentiallySolvable = true

  override def reducedDomainOf(v: Variable): Seq[Constant] = {
    if (!isReductionComputed) initialiseExplicitly()
    if (isPotentiallySolvable)
      getRepresentative(v) match {
        case variable: Variable => remainingDomain(variable).toSeq
        case constant: Constant => Vector() :+ constant
      } else
      Vector()
  }

  override def areCompatible(v1: Variable, v2: Variable): Option[Boolean] = {
    if (!isReductionComputed) initialiseExplicitly()
    if (isPotentiallySolvable)
      (getRepresentative(v1), getRepresentative(v2)) match {
        case (rc1: Constant, rc2: Constant) => if (rc1 == rc2) Some(true) else Some(false)
        case (rv1: Variable, rv2: Variable) => if (rv1 == rv2) Some(true) else if (unequal(rv1).contains(rv2)) Some(false) else None
        case _ => None // possibly, but we are not sure
      } else
      Option(false)
  }

  override def addConstraint(constraint: VariableConstraint): SymbolicCSP = {
    val newVariables = (constraint match {
      case Equal(v1, v2: Variable) => Set(v1, v2)
      case Equal(v, _: Constant) => Set(v)
      case NotEqual(v1, v2: Variable) => Set(v1, v2)
      case NotEqual(v, _: Constant) => Set(v)
      case OfSort(v, _) => Set(v)
      case NotOfSort(v, _) => Set(v)
    }) -- variables

    val newCSP = SymbolicCSP(variables ++ newVariables, constraints :+ constraint)

    if (isReductionComputed)
      newCSP.initialiseExplicitly(unequal, remainingDomain, unionFind, 1, newVariables)

    newCSP
  }

  override def addVariable(variable: Variable): SymbolicCSP = {
    val newCSP = SymbolicCSP(variables + variable, constraints)

    if (isReductionComputed)
      newCSP.initialiseExplicitly(unequal, remainingDomain, unionFind, 1, HashSet() + variable)

    newCSP
  }

  override def isSolvable: Option[Boolean] = {
    if (!isReductionComputed) initialiseExplicitly()

    if (isPotentiallySolvable)
      None
    else Some(false)
  }

  override def solution: Option[Map[Variable, Constant]] = {
    // returns partial solution
    def searchSolution(remainingDomain: Map[Variable, Set[Constant]]): Option[Map[Variable, Constant]] = {
      if (remainingDomain.isEmpty) Some(Map())
      else {
        val resolutionVariable: (Variable, Set[Constant]) = remainingDomain.last // this will not fail, since the map is not empty
        val domainsWithout_1: Map[Variable, Set[Constant]] = remainingDomain - resolutionVariable._1 // remove variable
        if (resolutionVariable._2.isEmpty) None
        else {
          resolutionVariable._2.foldLeft[Option[Map[Variable, Constant]]](None)({ case (ret@Some(_), _) => ret;
          case (None, const) => // try assigning _1 == const
            searchSolution(domainsWithout_1.map({ case (other, values) => if (unequal(resolutionVariable._1).contains(other)) other -> (values - const)
            else other -> values
                                                })) match {
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
      searchSolution((remainingDomain map { case (variable, viableConstants) => variable -> viableConstants.toSet }).toMap)
  }

  /** returns best known unique representative for a given variable */
  override def getRepresentative(v: Variable): Value = {
    if (!isReductionComputed) if (!isReductionComputed) initialiseExplicitly()
    unionFind.getRepresentative(v)
  }

  /**
   * Runs AC3.
   * Process all unprocessed constrains of the CSP and reduce maximally.
   * This function must not be called from an other class.
   */
  private def initialiseExplicitly(previousUnequal: mutable.Map[Variable, mutable.Set[Variable]] = new mutable.HashMap[Variable, mutable.Set[Variable]](),
                                   previousRemainingDomain: mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]](),
                                   previousUnionFind: SymbolicUnionFind = new SymbolicUnionFind, lastKConstraintsAreNew: Int = constraints.size,
                                   newVariables: Set[Variable] = variables): Unit = {
    // get really new copies of the previous data structures
    unequal = previousUnequal.clone()
    remainingDomain = previousRemainingDomain.clone()
    unionFind cloneFrom previousUnionFind

    // add completely new variables
    for (variable <- newVariables) {
      unionFind.addVariable(variable)
      remainingDomain(variable) = new mutable.HashSet[Constant]() ++ variable.sort.elements
      unequal(variable) = new mutable.HashSet[Variable]()
    }

    // add all new constraints
    for (originalConstraint <- constraints.drop(constraints.size - lastKConstraintsAreNew); constraint <- originalConstraint.compileNotOfSort)
    // treat each single constraint
      addSingleConstraint(constraint)

    if (detectUnsolvability())
      isPotentiallySolvable = false
    // unit propagation
    if (isPotentiallySolvable)
      unitPropagation()

    isReductionComputed = true
  }

  /** returns best known unique representative for a given variable */
  private def getRepresentativeUnsafe(value: Value): Value = value match {
    case v: Variable => unionFind.getRepresentative(v)
    case _: Constant => value // constant
  }

  /** function to detect all unit propagations */
  private def detectUnitPropagation(): Iterable[Variable] = remainingDomain filter { case (variable, values) => values.size == 1 } map { case (variable, values) => variable }

  /** simple test for unsolvability: a CSP is unsolvable if a variable exsists whose domain is empty */
  private def detectUnsolvability() = remainingDomain exists { case (variable, values) => values.size == 0 }

  /** executes uni propagation on a set of given variables */
  private def unitPropagation(toPropagate: Iterable[Variable] = detectUnitPropagation()): Unit = if (toPropagate.size == 0) ()
  else {
    val x: Iterable[Variable] = (toPropagate map { variable =>
      if (!isPotentiallySolvable)
        Vector()
      else {
        val constant = remainingDomain(variable).last // this one exists and is the only element
        // remove this variable add assert
        unionFind.assertEqual(variable, constant)
        remainingDomain.remove(variable)

        // propagate
        unequal(variable) foreach { other =>
          remainingDomain(other).remove(constant)
          if (remainingDomain(other).size == 0)
            isPotentiallySolvable = false
          unequal(other) -= variable
        }

        // find new propagations
        val newPropagation = unequal(variable) collect { case other if remainingDomain(other).size == 1 => other }

        // removed from the datastructure
        unequal -= variable

        newPropagation
      }
    }).flatten.toSet // eliminate duplicates

    if (isPotentiallySolvable)
      unitPropagation(x)
  }

  /** adds a single constraint to the CSP, but does not perform unit propagation */
  private def addSingleConstraint(constraint: VariableConstraint): Unit = {
    val equalsConstEliminated = constraint match {
      case equalConstr@Equal(v1, v2) => (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
        case (rv: Variable, const: Constant) => OfSort(rv, Sort("temp", Vector() :+ const, Nil))
        case (const: Constant, rv: Variable) => OfSort(rv, Sort("temp", Vector() :+ const, Nil))
        case _ => equalConstr
      }
      case x => x
    }

    // all actually different cases
    equalsConstEliminated match {
      case NotEqual(v1, v2) =>
        (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
          case (rv1: Variable, rv2: Variable) => for (p <- (rv1, rv2) ::(rv2, rv1) :: Nil) p match {
            case (x, y) => unequal(x) += y
          }
          case (rv: Variable, const: Constant) => remainingDomain(rv).remove(const)
          case (const: Constant, rv: Variable) => remainingDomain(rv).remove(const)
          case (const1: Constant, const2: Constant) => if (const1 == const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
        }
      case Equal(v1, v2) => (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
        case (rv1: Variable, rv2: Variable) => if (rv1 != rv2) {
          // intersect domains
          val intersectionDomain = remainingDomain(rv1) intersect remainingDomain(rv2)
          unionFind.assertEqual(rv1, rv2)


          // find out which representative the union-find uses and remove the other from the real CSP
          (if (getRepresentativeUnsafe(rv1) == rv1) (rv2, rv1) else (rv1, rv2)) match {
            case (remove, rep) =>
              remainingDomain.remove(remove)
              remainingDomain(rep) = intersectionDomain
          }
        }
        case (const1: Constant, const2: Constant) => if (const1 != const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
      }
      // sort of and var = const constraints
      case OfSort(v, sort) => getRepresentativeUnsafe(v) match {
        case constant: Constant => if (!sort.elements.contains(constant)) isPotentiallySolvable = false
        case rv: Variable =>
          remainingDomain(rv) = remainingDomain(rv) filter { x => sort.elements.contains(x) }
      }
    }
  }

  override def update(domainUpdate: DomainUpdate): SymbolicCSP = SymbolicCSP(variables map {_.update(domainUpdate)}, constraints map {_.update(domainUpdate)})
}


object UnsolvableCSP extends SymbolicCSP(Set(), Nil) {

  override def reducedDomainOf(v: Variable): Seq[Constant] = Nil

  /** If true is returned it is guaranteed, that a solution exists if v1 and v2 are set equal. Likewise, if false is returned such a CSP is unsolvable. */
  override def areCompatible(v1: Variable, v2: Variable): Option[Boolean] = Some(false)

  /** computes the solution of this CSP, might be computationally expensive */
  override def solution: Option[Map[Variable, Constant]] = None

  override def addConstraint(constraint: VariableConstraint): SymbolicCSP = this

  override def addVariable(variable: Variable): SymbolicCSP = this

  override def isSolvable: Option[Boolean] = Some(false)

  /** returns best known unique representative for a given variable */
  override def getRepresentative(v: Variable): Value = v
}
package de.uniulm.ki.panda3.symbolic.csp

import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic.{Constant, Sort, Value, Variable}
import de.uniulm.ki.util.HashMemo

import scala.collection.immutable.{HashMap, HashSet}
import scala.collection.mutable


/**
  * Handles Constraint-Satisfaction-Problems. The implementation decides which types of constraints can be handled.
  *
  * This CSP can handle four kinds of variable constraints: [[Equal]], [[NotEqual]], [[OfSort]], [[NotOfSort]].
  * It uses the AC3 algorithm to reduce the possible domains for every variable and maintains equivalence classes of equal variables.
  *
  * A solution can be generated via backtracking.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class CSP(variables: Set[Variable], constraints: Seq[VariableConstraint]) extends DomainUpdatable with HashMemo {

  // holds equivalent variables
  private val unionFind      : SymbolicUnionFind                            = new SymbolicUnionFind
  // contains information about unequal variables
  private val unequal        : mutable.Map[Variable, mutable.Set[Variable]] = new mutable.HashMap[Variable, mutable.Set[Variable]]()
  // this is only kept for the top elements of the union-find
  private val remainingDomain: mutable.Map[Variable, mutable.Set[Constant]] = new mutable.HashMap[Variable, mutable.Set[Constant]]()
  // marker for the computation of the reduction
  private var isReductionComputed                                           = false

  // if this flag is false, i.e. we know this CSP is unsolvable, the internal data might become inconsistent ... it is simply not necessary to have it still intact
  private var isPotentiallySolvable = true


  private def checkIntegrity() = if (!CSP.CHECKCSPINTEGRITY) true else {
    assert(unequal forall { _._2 forall { remainingDomain.contains } })
    assert(unequal forall { _._2 forall { variables.contains } })
    assert(unequal forall { case (v1, vals) => vals forall { case v2 => unequal(v2).contains(v1) } })
  }


  /** returns all (potentially) possible values of v in this CSP, this does not imply that for every such constant c, there is a solution in which v = c */
  def reducedDomainOf(v: Variable): Seq[Constant] = {
    if (!isReductionComputed) initialiseExplicitly()
    if (isPotentiallySolvable)
      getVariableRepresentative(v) match {
        case variable: Variable => remainingDomain(variable).toSeq
        case constant: Constant => Vector() :+ constant
      } else
      Vector()
  }

  /**
    * checks whether it is possible to unify two variables.
    * If Some(false) is returned this is not possible, i.e. the CSP implies v1 != v2.
    * If Some(true) is returned it is guaranteed, that if this CSP is solvable so is it if v1=v2 is added as a constraint.
    * In any other case None is returned.
    */
  def areCompatible(v1: Value, v2: Value): Option[Boolean] = (v1, v2) match {
    case (var1: Variable, var2: Variable) => areCompatible(var1, var2)
    case (c: Constant, v: Variable)       => if (getVariableRepresentative(v) == c) Some(true) else if (reducedDomainOf(v) contains c) None else Some(false)
    case (v: Variable, c: Constant)       => if (getVariableRepresentative(v) == c) Some(true) else if (reducedDomainOf(v) contains c) None else Some(false)
    case (c1: Constant, c2: Constant)     => Some(c1 == c2)
  }

  /**
    * checks whether it is possible to unify two variables.
    * If Some(false) is returned this is not possible, i.e. the CSP implies v1 != v2.
    * If Some(true) is returned it is guaranteed, that if this CSP is solvable so is it if v1=v2 is added as a constraint.
    * In any other case None is returned.
    */
  def areCompatible(v1: Variable, v2: Variable): Option[Boolean] = {
    if (!isReductionComputed) initialiseExplicitly()
    if (isPotentiallySolvable)
      (getVariableRepresentative(v1), getVariableRepresentative(v2)) match {
        case (rc1: Constant, rc2: Constant) => if (rc1 == rc2) Some(true) else Some(false)
        case (rv1: Variable, rv2: Variable) => if (rv1 == rv2) Some(true) else if (unequal(rv1).contains(rv2)) Some(false) else None
        case _                              => None // possibly, but we are not sure
      } else
      Option(false)
  }

  /** returns a new CSP containing all current constraints and the constraints passed as arguments */
  def addConstraints(constraints: Seq[VariableConstraint]): CSP = (constraints foldLeft this)({ case (c, vc) => c.addConstraint(vc) })

  /** returns a new CSP containing all current constraints and the constraint passed as an argument */
  def addConstraint(constraint: VariableConstraint): CSP = {
    val newVariables = (constraint match {
      case Equal(v1, v2: Variable)    => Set(v1, v2)
      case Equal(v, _: Constant)      => Set(v)
      case NotEqual(v1, v2: Variable) => Set(v1, v2)
      case NotEqual(v, _: Constant)   => Set(v)
      case OfSort(v, _)               => Set(v)
      case NotOfSort(v, _)            => Set(v)
    }) -- variables

    val newCSP = CSP(variables ++ newVariables, constraints :+ constraint)

    if (isReductionComputed)
      newCSP.initialiseExplicitly(unequal, remainingDomain, unionFind, 1, newVariables)

    newCSP
  }

  /** returns a new CSP containing all current variables and the variables passed as arguments */
  def addVariables(variables: Seq[Variable]): CSP = (variables foldLeft this)({ case (c, v) => c.addVariable(v) })

  /** returns a new CSP containing all current variables and the variable passed as an argument */
  def addVariable(variable: Variable): CSP = {
    assert(!variables.contains(variable))

    val newCSP = CSP(variables + variable, constraints)

    if (isReductionComputed)
      newCSP.initialiseExplicitly(unequal, remainingDomain, unionFind, 1, HashSet() + variable)

    newCSP
  }

  /** May return information on whether this CSP has a solution or not. If None is returned to information can be provided */
  def isSolvable: Option[Boolean] = {
    if (!isReductionComputed) initialiseExplicitly()

    if (isPotentiallySolvable)
      None
    else Some(false)
  }

  /** Returns a solution of this CSP, might be computationally expensive */
  def solution: Option[Map[Variable, Constant]] = {
    // returns partial solution
    def searchSolution(remainingDomain: Map[Variable, Set[Constant]]): Option[Map[Variable, Constant]] = {
      if (remainingDomain.isEmpty) Some(Map())
      else {
        val resolutionVariable: (Variable, Set[Constant]) = remainingDomain.last // this will not fail, since the map is not empty
        val domainsWithout_1: Map[Variable, Set[Constant]] = remainingDomain - resolutionVariable._1 // remove variable
        if (resolutionVariable._2.isEmpty) None
        else {
          resolutionVariable._2.foldLeft[Option[Map[Variable, Constant]]](None)({ case (ret@Some(_), _) => ret;
          case (None, const)                                                                            => // try assigning _1 == const
            searchSolution(domainsWithout_1.map({ case (other, values) => if (unequal(resolutionVariable._1).contains(other)) other -> (values - const)
            else other -> values
                                                })) match {
              case Some(partialSolution) => Some(partialSolution + (resolutionVariable._1 -> const))
              case _                     => None
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

  /** returns best known unique representative for a given variable or constant */
  def getRepresentative(value: Value): Value = value match {
    case v: Variable => getVariableRepresentative(v)
    case _ => value // constant
  }

  /** returns best known unique representative for a given variable */
  protected def getVariableRepresentative(v: Variable): Value = {
    if (!isReductionComputed) initialiseExplicitly()
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
    unequal.clear()
    previousUnequal foreach { case (key, value) =>
      val newSet = new mutable.HashSet[Variable]()
      value foreach newSet.add
      unequal.put(key, newSet)
    }

    remainingDomain.clear()
    previousRemainingDomain foreach { case (key, value) =>
      val newSet = new mutable.HashSet[Constant]()
      value foreach newSet.add
      remainingDomain.put(key, newSet)
    }

    unionFind cloneFrom previousUnionFind

    // check integrity
    assert(newVariables forall { case v => !unequal.contains(v) })

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

    checkIntegrity()
    isReductionComputed = true
  }

  /** returns best known unique representative for a given variable */
  private def getRepresentativeUnsafe(value: Value): Value = value match {
    case v: Variable => unionFind.getRepresentative(v)
    case _: Constant => value // constant
  }

  /** function to detect all unit propagations */
  private def detectUnitPropagation(): Iterable[Variable] = remainingDomain filter { case (variable, values) => values.size == 1 } map { case (variable, values) => variable }

  /** simple test for unsolvability: a CSP is unsolvable if a variable exists whose domain is empty */
  private def detectUnsolvability() = remainingDomain exists { case (variable, values) => values.isEmpty }

  /** executes uni propagation on a set of given variables */
  private def unitPropagation(toPropagate: Iterable[Variable] = detectUnitPropagation()): Unit = if (toPropagate.isEmpty) {() }
  else {
    val x: Set[Variable] = (toPropagate flatMap { variable =>
      if (!isPotentiallySolvable) {Vector() }
      else {
        val constant = remainingDomain(variable).last // this one exists and is the only element
        // remove this variable add assert
        unionFind.assertEqual(variable, constant)
        remainingDomain.remove(variable)

        // propagate
        val propagatedUnequalTo = unequal(variable)
        propagatedUnequalTo foreach { other =>
          remainingDomain(other).remove(constant)
          if (remainingDomain(other).isEmpty) isPotentiallySolvable = false
          unequal(other).remove(variable)
        }

        // find new propagations
        val newPropagation = propagatedUnequalTo collect { case other if remainingDomain(other).size == 1 => other }

        // removed from the datastructure
        unequal.remove(variable)
        propagatedUnequalTo foreach { unequal(_) remove variable }

        checkIntegrity()

        newPropagation
      }
    }).toSet // eliminate duplicates

    // subtraction seems to be necessary as another variable can issue a propagare of a not yet propagated variable
    if (isPotentiallySolvable) unitPropagation(x -- toPropagate)
    checkIntegrity()
  }

  /** adds a single constraint to the CSP, but does not perform unit propagation */
  private def addSingleConstraint(constraint: VariableConstraint): Unit = {
    checkIntegrity()
    val equalsConstEliminated = constraint match {
      case equalConstr@Equal(v1, v2) => (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
        case (rv: Variable, const: Constant) => OfSort(rv, Sort("temp", Vector() :+ const, Nil))
        case (const: Constant, rv: Variable) => OfSort(rv, Sort("temp", Vector() :+ const, Nil))
        case _                               => equalConstr
      }
      case x                         => x
    }

    // all actually different cases
    equalsConstEliminated match {
      case NotEqual(v1, v2) =>
        (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
          case (rv1: Variable, rv2: Variable)       => for (p <- (rv1, rv2) ::(rv2, rv1) :: Nil) p match {case (x, y) => unequal(x) += y;}
          case (rv: Variable, const: Constant)      => remainingDomain(rv).remove(const)
          case (const: Constant, rv: Variable)      => remainingDomain(rv).remove(const)
          case (const1: Constant, const2: Constant) => if (const1 == const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
        }
      case Equal(v1, v2)    => (getRepresentativeUnsafe(v1), getRepresentativeUnsafe(v2)) match {
        case (rv1: Variable, rv2: Variable)       => if (rv1 != rv2) {
          // intersect domains
          val intersectionDomain = remainingDomain(rv1) intersect remainingDomain(rv2)
          unionFind.assertEqual(rv1, rv2)


          // find out which representative the union-find uses and remove the other from the real CSP
          (if (getRepresentativeUnsafe(rv1) == rv1) (rv2, rv1) else (rv1, rv2)) match {
            case (remove, rep) =>
              // update remaining domains
              remainingDomain.remove(remove)
              remainingDomain(rep) = intersectionDomain
              // update the unequal map
              val removeTo = unequal(remove)
              if (removeTo contains rep) {isPotentiallySolvable = false; unequal(rep).remove(remove) } // force equality on two variables that also have an unequals constraint
              unequal.remove(remove)
              (removeTo - rep) foreach { to =>
                unequal(rep).add(to)
                unequal(to).remove(remove)
                unequal(to).add(rep)
              }
          }
        }
        case (const1: Constant, const2: Constant) => if (const1 != const2) isPotentiallySolvable = false // we found a definite flaw, that can't be resolved any more
      }
      // sort of and var = const constraints
      case OfSort(v, sort) =>
        getRepresentativeUnsafe(v) match {
          case constant: Constant => if (!sort.elements.contains(constant)) isPotentiallySolvable = false
          case rv: Variable       =>
            remainingDomain(rv) = remainingDomain(rv) filter { x => sort.elements.contains(x) }
        }
    }
  }

  override def update(domainUpdate: DomainUpdate): CSP = CSP(variables map { _.update(domainUpdate) }, constraints map { _.update(domainUpdate) })

  /** determines whether two variables or constants must be equal in this CSP */
  def equal(v1: Value, v2: Value): Boolean = getRepresentative(v1) == getRepresentative(v2)

  /** returns a list of all variables that are set to be unequal to this one */
  def getUnequalVariables(variable: Variable): Seq[Variable] = getRepresentative(variable) match {
    case Constant(_)         => Nil
    case v@Variable(_, _, _) => unequal(v).toSeq
  }
}

object CSP {
  private val CHECKCSPINTEGRITY : Boolean = false
}


object UnsolvableCSP extends CSP(Set(), Nil) {

  override def reducedDomainOf(v: Variable): Seq[Constant] = Nil

  /** If true is returned it is guaranteed, that a solution exists if v1 and v2 are set equal. Likewise, if false is returned such a CSP is unsolvable. */
  override def areCompatible(v1: Variable, v2: Variable): Option[Boolean] = Some(false)

  /** computes the solution of this CSP, might be computationally expensive */
  override def solution: Option[Map[Variable, Constant]] = None

  override def addConstraint(constraint: VariableConstraint): CSP = this

  override def addVariable(variable: Variable): CSP = this

  override def isSolvable: Option[Boolean] = Some(false)

  /** returns best known unique representative for a given variable */
  override protected def getVariableRepresentative(v: Variable): Value = v
}

object NoConstraintsCSP extends CSP(Set(),Nil) {

  override def reducedDomainOf(v: Variable): Seq[Constant] = v.sort.elements

  override def areCompatible(v1: Variable, v2: Variable): Option[Boolean] = None

  override def solution: Option[Map[Variable, Constant]] = Some(new HashMap[Variable, Constant]() {override def default(v: Variable) = v.sort.elements.head})

  override def addConstraint(constraint: VariableConstraint): CSP = throw new UnsupportedOperationException()

  override def addVariable(variable: Variable): CSP = throw new UnsupportedOperationException()

  override def isSolvable: Option[Boolean] = Some(true)

  override protected def getVariableRepresentative(v: Variable): Value = v

  override def update(domainUpdate: DomainUpdate): CSP = throw new UnsupportedOperationException()

  override def getUnequalVariables(variable: Variable): Seq[Variable] = Nil
}
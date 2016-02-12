package de.uniulm.ki.panda3.efficient.csp

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain

import scala.collection.mutable
import de.uniulm.ki.panda3.efficient._

import scala.collection.mutable.ArrayBuffer

/**
  *
  * Assumptions
  *
  * - all variables are numbered from 0..sz(remainingDomains)
  * - the union find will contain negative numbers for constants and will add 1 to every constant it encounters.
  * Normally the constants are numbered 0..k-1, here -1..-k
  * - this also holds (for simplicity) for the remaining domains values
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class EfficientCSP(domain: EfficientDomain, remainingDomains: Array[mutable.BitSet] = Array(), unequal: Array[mutable.BitSet] = Array(),
                   protected val unionFind: EfficientUnionFind = new EfficientUnionFind(Array()), val variableSorts: Array[Int] = Array(), var potentiallyConsistent: Boolean = true)
                  (lastKVariablesAreNew: Int = variableSorts.length) {
  // first propagate then check for consistency
  propagateNewVariablesIfSingleton(lastKVariablesAreNew)
  assert(isCSPInternallyConsistent())

  val numberOfVariables = remainingDomains.length

  /**
    * Determines whether the datastructures of this csp are consistent. There should be no need to call this function outside of tests
    */
  def isCSPInternallyConsistent(): Boolean = {
    var consistent = true
    consistent = consistent && (remainingDomains.length == unequal.length)
    consistent = consistent && (unequal.length == unionFind.parent.length)

    var i = 0
    while (i < unionFind.parent.length) {
      if (unionFind.parent(i) == i) {
        consistent = consistent && remainingDomains(i).size > 1
        val j = unequal(i).iterator
        while (j.hasNext) {
          val unequalTo = j.next()
          if (unequalTo < 0)
            consistent = false
          else consistent = consistent && (unionFind.parent(unequalTo) == unequalTo)
        }
      }
      i += 1
    }
    consistent || !potentiallyConsistent
  }

  def isRepresentativeAVariable(variable: Int): Boolean = unionFind.getRepresentative(variable) >= 0

  def getRepresentativeVariable(variable: Int): Int = if (unionFind.getRepresentative(variable) >= 0) unionFind.getRepresentative(variable)
  else throw new IllegalArgumentException("The variable " + variable + " is not bound to a variable")

  def getRepresentativeConstant(variable: Int): Int = if (unionFind.getRepresentative(variable) < 0) switchConstant(unionFind.getRepresentative(variable))
  else throw new IllegalArgumentException("The variable " + variable + " is not bound to a constant")

  def getRemainingDomain(variable: Int): mutable.Set[Int] = if (unionFind.getRepresentative(variable) < 0) {
    val returnSet = new mutable.HashSet[Int]()
    returnSet.add(switchConstant(unionFind.getRepresentative(variable)))
    returnSet
  } else {
    remainingDomains(unionFind.getRepresentative(variable))
  }

  def getVariableUnequalTo(variable: Int): mutable.Set[Int] = if (unionFind.getRepresentative(variable) < 0) new mutable.HashSet[Int]()
  else unequal(unionFind.getRepresentative(variable)).clone()


  /**
    * Deep clone this CSP.
    */
  def copy(): EfficientCSP = addVariables(Array())

  /**
    * Deep clone this CSP. The given arguments will be translated into new Variables of the sorts given as parameters
    */
  def addVariables(sortsOfNewVariables: Array[Int]): EfficientCSP = {
    val copies = copyAndAddNewVariables(sortsOfNewVariables)
    val sortsOfVariables: Array[Int] = new Array(numberOfVariables + sortsOfNewVariables.length)
    var i = 0
    while (i < sortsOfNewVariables.length) {
      if (i < numberOfVariables) sortsOfVariables(i) = variableSorts(i)
      else sortsOfVariables(i) = sortsOfNewVariables(i - numberOfVariables)
      i += 1
    }

    // the new variables will be propagated in the constructor
    new EfficientCSP(domain, copies._1, copies._2, copies._3, sortsOfVariables, potentiallyConsistent)(sortsOfNewVariables.length)
  }

  private def propagateNewVariablesIfSingleton(lastKVariablesAreNew: Int): Unit = {
    var i = remainingDomains.length - lastKVariablesAreNew
    while (i < remainingDomains.length && potentiallyConsistent) {
      if (remainingDomains(i).isEmpty) potentiallyConsistent = false
      if (remainingDomains(i).size == 1) propagate(i)
      i += 1
    }
  }

  private def copyAndAddNewVariables(sorts: Array[Int]): (Array[mutable.BitSet], Array[mutable.BitSet], EfficientUnionFind) = {
    val clonedDomains = new Array[mutable.BitSet](remainingDomains.length + sorts.length)
    val clonedUnequal = new Array[mutable.BitSet](remainingDomains.length + sorts.length)
    var i = 0
    while (i < clonedDomains.length) {
      if (i < remainingDomains.length) {
        clonedDomains(i) = remainingDomains(i).clone()
        clonedUnequal(i) = unequal(i).clone()
      } else {
        clonedDomains(i) = mutable.BitSet()
        val constants: Array[Int] = domain.constantsOfSort(sorts(i - remainingDomains.length))
        var j = 0
        while (j < constants.length) {
          clonedDomains(i).add(constants(j))
          j += 1
        }
        clonedUnequal(i) = mutable.BitSet()
      }
      i += 1
    }
    (clonedDomains, clonedUnequal, unionFind.addVariables(sorts.length))
  }


  private def switchSetOfConstants(constants: mutable.Set[Int]): mutable.Set[Int] = {
    val externalSet: mutable.Set[Int] = new mutable.HashSet[Int]()
    val i = constants.iterator
    while (i.hasNext) {
      externalSet.add(switchConstant(i.next()))
    }
    externalSet
  }

  private def assertEqual(v1: Int, v2: Int): Boolean = {
    val r1 = unionFind.getRepresentative(v1)
    val r2 = unionFind.getRepresentative(v2)

    if (r1 == r2) true // already equal
    else if (r1 >= 0 || r2 >= 0) {
      // union is possible
      unionFind.assertEqual(r1, r2)

      val newRepresentative: Int = unionFind.getRepresentative(r1)
      val newRemoved: Int = if (r1 == newRepresentative) r2 else r1

      // if both were variables, we also have to update the unequals table
      if (newRepresentative >= 0 && newRemoved >= 0) {
        if (unequal(newRepresentative).contains(newRemoved)) false
        else {
          val removedVariableWasUnequalTo = unequal(newRemoved).toArray
          unequal(newRepresentative) ++= removedVariableWasUnequalTo
          var i = 0
          while (i < removedVariableWasUnequalTo.length) {
            unequal(removedVariableWasUnequalTo(i)).remove(newRemoved)
            unequal(removedVariableWasUnequalTo(i)).add(newRepresentative)
            i = i + 1
          }
          // changed the remaining domains in the way it should result in them setting them equal
          remainingDomains(newRepresentative) = remainingDomains(newRepresentative) & remainingDomains(newRemoved)
          if (remainingDomains(newRepresentative).nonEmpty) true else false
        }
      } else {
        // newRemoved was the variable and newRepresentative a constant
        if (remainingDomains(newRemoved).contains(switchConstant(newRepresentative))) {
          remainingDomains(newRemoved).clear()
          remainingDomains(newRemoved).add(switchConstant(newRepresentative))
          // remove newRemoved from all unequal constraints
          val i = unequal(newRemoved).iterator
          while (i.hasNext) {
            val removeFrom = i.next()
            unequal(removeFrom).remove(newRemoved)
          }
          true
        } else false
      }
    } else false // different constants cannot be set equal
  }

  private def propagate(toPropagate: Int): Unit = {
    val array = Array(toPropagate)
    propagate(array)
  }

  private def propagate(toPropagate: Array[Int]): Unit = {
    val newPropagations: mutable.Set[Int] = new mutable.HashSet[Int]()

    var i = 0
    while (i < toPropagate.length && potentiallyConsistent) {
      // maybe this one is already bad
      assert(remainingDomains(toPropagate(i)).size <= 1)
      if (remainingDomains(toPropagate(i)).isEmpty) potentiallyConsistent = false
      else {

        // the value to which the variable has been set
        val unitValue = switchConstant(remainingDomains(toPropagate(i)).head)

        // set the variable to the constant (just to be sure)
        val equalSuccessful = assertEqual(toPropagate(i), unitValue)
        assert(equalSuccessful)

        // go through all variables this one has to be unequal to
        val unequalToPropagate = unequal(toPropagate(i)).iterator
        while (unequalToPropagate.hasNext) {
          val propagateTo = unequalToPropagate.next()
          // remove the unit from the domain
          remainingDomains(propagateTo).remove(switchConstant(unitValue))
          if (remainingDomains(propagateTo).size == 1) newPropagations.add(propagateTo)
          if (remainingDomains(propagateTo).isEmpty) potentiallyConsistent = false
        }
        i += 1
      }
    }
    if (potentiallyConsistent && newPropagations.nonEmpty) propagate(newPropagations.toArray)
  }

  def addConstraint(constraint: EfficientVariableConstraint): Unit =
    if (constraint.constraintType == EfficientVariableConstraint.EQUALVARIABLE) {
      // variable  =  variable
      val variableRepresentative = unionFind.getRepresentative(constraint.variable)
      val otherRepresentative = unionFind.getRepresentative(constraint.other)
      if (!assertEqual(variableRepresentative, otherRepresentative)) potentiallyConsistent = false
      else {
        if (variableRepresentative > 0 || otherRepresentative > 0) {
          // check whether this assertion could have lead to a variable with only a unit domain
          var representativeToCheck = variableRepresentative
          if (otherRepresentative > 0) representativeToCheck = otherRepresentative
          val chosenRepresentative = unionFind.getRepresentative(representativeToCheck)

          if (chosenRepresentative >= 0) {
            if (remainingDomains(chosenRepresentative).size == 1)
              propagate(chosenRepresentative)
          } else {
            val nonChosen = if (variableRepresentative >= 0) variableRepresentative else otherRepresentative
            propagate(nonChosen)
          }
        }
      }
    } else if (constraint.constraintType == EfficientVariableConstraint.EQUALCONSTANT) {
      // variable  = constant
      val variableRepresentative = unionFind.getRepresentative(constraint.variable)
      val internalConstant = switchConstant(constraint.other)
      // if equal, we have already set this constraint
      if (variableRepresentative != internalConstant)
        if (variableRepresentative < 0) potentiallyConsistent = false
        else {
          remainingDomains(variableRepresentative).clear()
          remainingDomains(variableRepresentative).add(switchConstant(internalConstant))
          // we just set it, so propagate
          propagate(variableRepresentative)
        }
    } else if (constraint.constraintType == EfficientVariableConstraint.UNEQUALVARIABLE || constraint.constraintType == EfficientVariableConstraint.UNEQUALCONSTANT) {
      val variableRepresentative = unionFind.getRepresentative(constraint.variable)
      val otherRepresentative = if (constraint.constraintType == EfficientVariableConstraint.UNEQUALCONSTANT) switchConstant(constraint.other)
      else unionFind.getRepresentative(constraint.other)

      if (variableRepresentative == otherRepresentative) potentiallyConsistent = false
      else if (variableRepresentative >= 0 && otherRepresentative >= 0) {
        // both are variables
        unequal(variableRepresentative).add(otherRepresentative)
        unequal(otherRepresentative).add(variableRepresentative)
      } else if (variableRepresentative < 0 && otherRepresentative < 0) {
        // we tried to set the same constant unequal
        if (variableRepresentative == otherRepresentative) potentiallyConsistent = false
      } else {
        val variable = if (variableRepresentative >= 0) variableRepresentative else otherRepresentative
        val constant = if (variableRepresentative >= 0) otherRepresentative else variableRepresentative

        remainingDomains(variable).remove(switchConstant(constant))
        if (remainingDomains(variable).size == 1) propagate(variable)
      }
    } else if (constraint.constraintType == EfficientVariableConstraint.OFSORT) {
      val variableRepresentative = unionFind.getRepresentative(constraint.variable)

      if (variableRepresentative < 0) {
        if (!domain.constantsOfSort(constraint.other).contains(switchConstant(variableRepresentative))) potentiallyConsistent = false
      } else {
        // TODO make this more efficient by using a loop
        remainingDomains(variableRepresentative) = remainingDomains(variableRepresentative) & mutable.BitSet(domain.constantsOfSort(constraint.other): _*)
        if (remainingDomains(variableRepresentative).isEmpty) potentiallyConsistent = false
        if (remainingDomains(variableRepresentative).size == 1) propagate(variableRepresentative)
      }
    } else if (constraint.constraintType == EfficientVariableConstraint.NOTOFSORT) {
      val variableRepresentative = unionFind.getRepresentative(constraint.variable)

      if (variableRepresentative < 0) {
        if (domain.constantsOfSort(constraint.other).contains(switchConstant(variableRepresentative))) potentiallyConsistent = false
      } else {
        var i = 0
        val constantsInSort = domain.constantsOfSort(constraint.other)
        while (i < constantsInSort.length) {
          remainingDomains(variableRepresentative).remove(constantsInSort(i))
          i += 1
        }
        if (remainingDomains(variableRepresentative).isEmpty) potentiallyConsistent = false
        if (remainingDomains(variableRepresentative).size == 1) propagate(variableRepresentative)
      }
    }

  /** determines whether two values (variables or constants) could be set equal */
  def areCompatible(val1: Int, val2: Int): Int = {
    val x = unionFind.getRepresentative(val1)
    val y = unionFind.getRepresentative(val2)

    if (x != y) {
      if (x < 0 || y < 0) {
        // at least one constant
        if (x < 0 && y < 0) EfficientCSP.INCOMPATIBLE // both are _different_ constants
        else {
          val constant = if (x < 0) x else y
          val variable = if (x < 0) y else x
          if (!remainingDomains(variable).contains(switchConstant(constant))) EfficientCSP.INCOMPATIBLE else EfficientCSP.COMPATIBLE
        }
      } else {
        // both are variables
        if (unequal(x).contains(y)) EfficientCSP.INCOMPATIBLE else EfficientCSP.COMPATIBLE
      }
    } else EfficientCSP.EQUAL
  }

  /** determines whether two variables are set equal */
  def areEqual(var1: Int, var2: Int): Boolean = unionFind.getRepresentative(var1) == unionFind.getRepresentative(var2)


  def computeMGU(someVariables: Array[Int], otherVariables: Array[Int]): Option[Array[EfficientVariableConstraint]] = {
    assert(someVariables.length == otherVariables.length)
    var possible = true
    val mgu = new ArrayBuffer[EfficientVariableConstraint]()
    val csp = copy() // this takes memory, but else it would be a real pain

    var i = 0
    while (i < someVariables.length && possible) {
      val x = csp.unionFind.getRepresentative(otherVariables(i))
      val y = csp.unionFind.getRepresentative(someVariables(i))
      val compatibility = csp.areCompatible(x, y)

      if (compatibility == EfficientCSP.INCOMPATIBLE) possible = false
      else if (compatibility == EfficientCSP.COMPATIBLE) {
        val constraint = EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, x, y)
        csp.addConstraint(constraint)
        mgu append constraint
      }
      i += 1
    }

    if (!possible) None else Some(mgu.toArray)
  }

  /** a faster routine to compute the mgu, it might return one even if it is illegal */
  def fastMGU(someVariables: Array[Int], otherVariables: Array[Int]): Option[Array[EfficientVariableConstraint]] = {
    assert(someVariables.length == otherVariables.length)
    var possible = true
    val mgu = new ArrayBuffer[EfficientVariableConstraint]()

    var i = 0
    while (i < someVariables.length && possible) {
      val x = unionFind.getRepresentative(otherVariables(i))
      val y = unionFind.getRepresentative(someVariables(i))
      val compatibility = areCompatible(x, y)

      if (compatibility == EfficientCSP.INCOMPATIBLE) possible = false
      else if (compatibility == EfficientCSP.COMPATIBLE) {
        mgu append EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, x, y)
      }
      i += 1
    }

    if (!possible) None else Some(mgu.toArray)
  }
}

object EfficientCSP {
  val COMPATIBLE   = 0
  val EQUAL        = 1
  val INCOMPATIBLE = -1
}
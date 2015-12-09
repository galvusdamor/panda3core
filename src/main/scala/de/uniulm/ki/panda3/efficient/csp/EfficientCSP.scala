package de.uniulm.ki.panda3.efficient.csp

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain

import scala.collection.mutable

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
class EfficientCSP(domain: EfficientDomain, remainingDomains: Array[mutable.Set[Int]] = Array(), unequal: Array[mutable.Set[Int]] = Array(), unionFind: Array[Int] = Array(), var
potentiallyConsistent: Boolean) {

  assert(remainingDomains.length == unequal.length)
  assert(unequal.length == unionFind.length)

  def isRepresentativeAVariable(variable: Int): Boolean = getUnionFindRepresentative(variable) >= 0

  def getRepresentativeVariable(variable: Int): Int = if (getUnionFindRepresentative(variable) >= 0) getUnionFindRepresentative(variable)
  else throw new IllegalArgumentException("The variable " + variable + " is not bound to a variable")

  def getRepresentativeConstant(variable: Int): Int = if (unionFind(variable) < 0) switchConstant(getUnionFindRepresentative(variable))
  else throw new IllegalArgumentException("The variable " + variable + " is not bound to a constant")

  def getRemainingDomain(variable: Int): mutable.Set[Int] = remainingDomains(variable)

  /**
   * Deep clone this CSP. The given arguments will be translated into new Variables of the sorts given as parameters
   */
  def copy(sortsOfNewVariables: Array[Int] = Array()): EfficientCSP = {
    val clonedDomains = new Array[mutable.Set[Int]](remainingDomains.length + sortsOfNewVariables.length)
    val clonedUnequal = new Array[mutable.Set[Int]](remainingDomains.length + sortsOfNewVariables.length)
    val clonedUnionFind = new Array[Int](unionFind.length + sortsOfNewVariables.length)
    var i = 0
    while (i < clonedDomains.length) {
      if (i < remainingDomains.length) {
        clonedDomains(i) = remainingDomains(i).clone()
        clonedUnequal(i) = unequal(i).clone()
        clonedUnionFind(i) = unionFind(i)
      } else {
        clonedDomains(i) = mutable.HashSet[Int]()
        val constants: Array[Int] = domain.constantsOfSort(sortsOfNewVariables(i - remainingDomains.length))
        var j  = 0
        while (j < constants.length){
          clonedDomains(i).add(switchConstant(constants(j)))
          j = j+1
        }
        clonedUnequal(i) = mutable.HashSet[Int]()
        clonedUnionFind(i) = i // init with itself
      }
      i = i + 1
    }
    val csp = new EfficientCSP(domain, clonedDomains, clonedUnequal, clonedUnionFind, potentiallyConsistent)

    i = remainingDomains.length
    while (i < clonedDomains.length) {
      if (clonedDomains(i).size == 0) csp.potentiallyConsistent = false
      if (clonedDomains(i).size == 1) csp.propagate(i)
      i = i + 1
    }

    csp
  }

  private def switchConstant(c: Int): Int = (-c) - 1

  /**
   * Returns the canonical representative inside the union find.
   * The result will be negative if this is a constant
   */
  private def getUnionFindRepresentative(v: Int): Int = if (v < 0) v
  else {
    if (unionFind(v) == v) v
    else {
      val representative = getUnionFindRepresentative(unionFind(v))
      unionFind(v) = representative
      representative
    }
  }

  private def assertEqual(v1: Int, v2: Int): Boolean = {
    val r1 = getUnionFindRepresentative(v1)
    val r2 = getUnionFindRepresentative(v2)

    if (r1 == r2) true // already equal
    else if (r1 >= 0 || r2 >= 0) {
      // union is possible
      var newRepresentative: Int = -1
      var newRemoved: Int = -1
      if (r1 >= 0) {
        unionFind(r1) = r2
        newRepresentative = r2
        newRemoved = r1
      } else {
        unionFind(r2) = r1
        newRepresentative = r1
        newRemoved = r2
      }

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
          if (remainingDomains(newRepresentative).size >= 1) true else false
        }
      } else if (remainingDomains(newRemoved).contains(newRepresentative)) true else false
    } else false // different constants cannot be set equal
  }

  private def propagate(toPropagate: Int): Unit = {
    val array = Array(toPropagate)
    propagate(array)
  }

  private def propagate(toPropagate: Array[Int]): Unit = {
    val newPropagations: mutable.Set[Int] = new mutable.HashSet[Int]()

    var i = 0
    while (i < toPropagate.length) {
      // maybe this one is already bad
      assert(remainingDomains(toPropagate(i)).size <= 1)
      if (remainingDomains(toPropagate(i)).size == 0) potentiallyConsistent = false

      // the value to which the variable has been set
      val unitValue = remainingDomains(toPropagate(i)).head

      // set the variable to the constant (just to be sure)
      val equalSuccessful = assertEqual(toPropagate(i), unitValue)
      assert(equalSuccessful)

      // go through all variables this one has to be unequal to
      var j = 0
      val unequalToPropagate: Array[Int] = unequal(toPropagate(i)).toArray
      while (j < unequalToPropagate.length) {
        val propagateTo = unequalToPropagate(j)
        // remove the unit from the domain
        remainingDomains(propagateTo).remove(unitValue)
        if (remainingDomains(propagateTo).size == 1) newPropagations.add(propagateTo)
        if (remainingDomains(propagateTo).size == 0) potentiallyConsistent = false
        j = j + 1
      }
      i = i + 1
    }
    if (potentiallyConsistent && newPropagations.size != 0) propagate(newPropagations.toArray)
  }

  def addConstraint(constraint: VariableConstraint): Unit =
    if (constraint.constraintType == VariableConstraint.EQUALVARIABLE) {
      val variableRepresentative = getUnionFindRepresentative(constraint.variable)
      val otherRepresentative = getUnionFindRepresentative(constraint.other)
      // variable  =  variable
      assertEqual(variableRepresentative, otherRepresentative)
      val chosenRepresentative = getUnionFindRepresentative(variableRepresentative)
      if (remainingDomains(chosenRepresentative).size == 1)
        propagate(chosenRepresentative)
    } else if (constraint.constraintType == VariableConstraint.EQUALCONSTANT) {
      // variable  = constant
      val variableRepresentative = getUnionFindRepresentative(constraint.variable)
      val internalConstant = switchConstant(constraint.other)
      // if equal, we have already set this constraint
      if (variableRepresentative != internalConstant)
        if (variableRepresentative < 0) potentiallyConsistent = false
        else {
          remainingDomains(variableRepresentative).clear()
          remainingDomains(variableRepresentative).add(internalConstant)
          // we just set it, so propagate
          propagate(variableRepresentative)
        }
    } else if (constraint.constraintType == VariableConstraint.UNEQUALVARIABLE || constraint.constraintType == VariableConstraint.UNEQUALCONSTANT) {
      val variableRepresentative = getUnionFindRepresentative(constraint.variable)
      val otherRepresentative = if (constraint.constraintType == VariableConstraint.UNEQUALCONSTANT) switchConstant(constraint.other) else getUnionFindRepresentative(constraint.other)

      if (variableRepresentative >= 0 && otherRepresentative >= 0) {
        // both are variables
        unequal(variableRepresentative).add(otherRepresentative)
        unequal(otherRepresentative).add(variableRepresentative)
      } else if (variableRepresentative < 0 && otherRepresentative < 0) {
        // we tried to set the same constant unequal
        if (variableRepresentative == otherRepresentative) potentiallyConsistent = false
      } else {
        val variable = if (variableRepresentative >= 0) variableRepresentative else otherRepresentative
        val constant = if (variableRepresentative >= 0) otherRepresentative else variableRepresentative

        remainingDomains(variable).remove(constant)
        if (remainingDomains(variable).size == 1) propagate(variable)
      }
    } else if (constraint.constraintType == VariableConstraint.OFSORT) {
      val variableRepresentative = getUnionFindRepresentative(constraint.variable)

      if (variableRepresentative < 0) {
        if (!domain.constantsOfSort(constraint.other).contains(switchConstant(variableRepresentative))) potentiallyConsistent = false
      } else {
        remainingDomains(variableRepresentative) = remainingDomains(variableRepresentative) & domain.constantsOfSort(constraint.other).toSet
        if (remainingDomains(variableRepresentative).size == 0) potentiallyConsistent = false
        if (remainingDomains(variableRepresentative).size == 1) propagate(variableRepresentative)
      }
    } else if (constraint.constraintType == VariableConstraint.NOTOFSORT) {
      val variableRepresentative = getUnionFindRepresentative(constraint.variable)

      if (variableRepresentative < 0) {
        if (domain.constantsOfSort(constraint.other).contains(switchConstant(variableRepresentative))) potentiallyConsistent = false
      } else {
        var i = 0
        val constantsInSort = domain.constantsOfSort(constraint.other)
        while (i < constantsInSort.length) {
          remainingDomains(variableRepresentative).remove(constantsInSort(i))
          i = i + 1
        }
        if (remainingDomains(variableRepresentative).size == 0) potentiallyConsistent = false
        if (remainingDomains(variableRepresentative).size == 1) propagate(variableRepresentative)
      }
    }
}
package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.{Constant, Value, Variable}

import scala.collection.mutable

/**
 * Contains a mutable union-find, containing variables and constants
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
// TODO: Maybe add the sizes to "increase" efficiency
class SymbolicUnionFind {

  // contains the union-find for all variables
  // entry may also point to Constant if it known that they can only have this value
  // if the entry points to itself, then this is a top element
  private var unionFind: mutable.Map[Variable, Value] = new mutable.HashMap[Variable, Value]()

  /** returns best known unique representative for a given variable */
  def getRepresentative(v: Variable): Value = {
    unionFind(v) match {
      case c: Constant => c
      case parent: Variable =>
        if (parent == v) v
        else {
          val representative = getRepresentative(parent)
          unionFind(v) = representative
          representative
        }
    }
  }

  /** Make the two arguments equal in the union-find */
  def assertEqual(v1: Variable, v2: Value): Boolean = {
    // obtains representatives
    val v1_representative: Value = getRepresentative(v1)
    val v2_representative: Value = v2 match {
      case variable: Variable => getRepresentative(variable)
      case constant: Constant => constant
    }

    if (v1_representative == v2_representative) // if they are equal we don't have anything to do
      true
    else {
      (v1_representative, v2_representative) match {
        case (const1: Constant, const2: Constant) => false // two unequal constants can't be made equal
        case (variable1: Variable, const2: Constant) => unionFind(variable1) = const2; true
        case (const1: Constant, variable2: Variable) => unionFind(variable2) = const1; true
        case (variable1: Variable, variable2: Variable) => unionFind(variable1) = variable2; true
      }
    }
  }

  /** add a new variable to the union find */
  def addVariable(v: Variable): Unit = {
    unionFind(v) = v
  }

  def cloneFrom(from: SymbolicUnionFind) = {
    unionFind = from.unionFind.clone()
  }
}
package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Constant

import scala.collection.mutable
import scala.util.Right

/**
 * Contains a mutable union-find, containing variables and constants
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicUnionFind {

  // contains the union-find for all variables
  // entry may also point to Constant if it known that they can only have this value
  // if the entry points to itself, then this is a top element
  private var unionFind: mutable.Map[Variable, Either[Variable, Constant]] = new mutable.HashMap[Variable, Either[Variable, Constant]]()

  /** returns best known unique representative for a given variable */
  def getRepresentative(v: Variable): Either[Variable, Constant] = {
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

  /** Make the two arguments equal in the union-find */
  def assertEqual(v1: Variable, v2: Either[Variable, Constant]): Boolean = {
    // obtains representatives
    val v1_representative: Either[Variable, Constant] = getRepresentative(v1)
    val v2_representative: Either[Variable, Constant] = v2 match {
      case Left(variable) => getRepresentative(variable)
      case Right(constant) => Right(constant)
    }

    if (v1_representative == v2_representative) // if they are equal we don't have anything to do
      true
    else {
      (v1_representative, v2_representative) match {
        case (Right(const1), Right(const2)) => false // two unequal constants can't be made equal
        case (Left(variable1), Right(const2)) => unionFind(variable1) = Right(const2); true
        case (Right(const1), Left(variable2)) => unionFind(variable2) = Right(const1); true
        case (Left(variable1), Left(variable2)) => unionFind(variable1) = Left(variable2); true
      }
    }
  }

  /** add a new variable to the union find */
  def addVariable(v: Variable): Unit = {
    unionFind(v) = Left(v)
  }

  def cloneFrom(from: SymbolicUnionFind) = {
    unionFind = from.unionFind.clone()
  }
}
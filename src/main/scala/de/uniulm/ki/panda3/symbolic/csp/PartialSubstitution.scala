package de.uniulm.ki.panda3.symbolic.csp

import scala.reflect.ClassTag

/**
 * Represents a simple substitution, given by two lists of
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class PartialSubstitution[T:ClassTag](oldVariables: Seq[T], newVariables: Seq[T]) extends (T => T) {

  private lazy val indexAccessMap: Map[T, Int] = oldVariables.zipWithIndex.toMap
  private lazy val newVarsArray : Array[T] = newVariables.toArray[T]

  def apply(v: T): T = {
    val index = indexAccessMap.getOrElse(v, -1)

    if (index == -1) v else newVarsArray(index)
  }
}


case class TotalSubstitution[S,T](oldVariables: Seq[S], newVariables: Seq[T]) extends (S => T) {
  assert(oldVariables.length == newVariables.length)

  private lazy val indexAccessMap: Map[S, Int] = oldVariables.zipWithIndex.toMap

  def apply(v: S): T = {
    val index = indexAccessMap.getOrElse(v, -1)
    assert(index != -1)
    newVariables(index)
  }
}
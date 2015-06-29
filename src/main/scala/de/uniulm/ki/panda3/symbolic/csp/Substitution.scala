package de.uniulm.ki.panda3.symbolic.csp

/**
 * Represents a simple substitution, given by two lists of
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Substitution[T](oldVariables: Seq[T], newVariables: Seq[T]) extends (T => T) {

  private lazy val indexAccessMap: Map[T, Int] = oldVariables.zipWithIndex.toMap

  def apply(v: T): T = {
    val index = indexAccessMap.getOrElse(v, -1)

    if (index == -1) v else newVariables(index)
  }
}
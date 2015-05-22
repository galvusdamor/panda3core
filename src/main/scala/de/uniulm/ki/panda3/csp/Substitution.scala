package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Variable

/**
 * Represents a simple substitution, given by two lists of
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Substitution(oldVariables: Seq[Variable], newVariables: Seq[Variable]) extends (Variable => Variable) {

  private lazy val indexAccessMap: Map[Variable, Int] = oldVariables.zipWithIndex.toMap

  def apply(v: Variable): Variable = {
    val index = indexAccessMap.getOrElse(v, -1)

    if (index == -1) v else newVariables(index)
  }
}

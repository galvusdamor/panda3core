package de.uniulm.ki.panda3.csp

import de.uniulm.ki.panda3.logic.Sort

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait VariableConstraint {

  object ConstraintType extends Enumeration {
    type ConstraintType = Value
    val equal, unequal, ofSort, notOfSort = Value
  }

  import ConstraintType._

  val firstVariable: Variable
  val constraintType: ConstraintType
  val secondVariable: Option[Variable]
  val sort: Option[Sort]

}
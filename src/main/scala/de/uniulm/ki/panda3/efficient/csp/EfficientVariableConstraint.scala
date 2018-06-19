// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.csp

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientVariableConstraint(constraintType: Int, variable: Int, other: Int) {
  assert(constraintType >= 0 && constraintType <= 5)
  assert(variable >= 0)
  assert(other >= 0)

  def addToVariableIndexIfGreaterEqualThen(offset: Int, ifGEQThen: Int): EfficientVariableConstraint = {
    var newVariable = variable
    if (newVariable >= ifGEQThen) newVariable += offset
    var newOther = other
    if (newOther >= ifGEQThen) newOther += offset

    if (constraintType == EfficientVariableConstraint.EQUALVARIABLE || constraintType == EfficientVariableConstraint.UNEQUALVARIABLE)
      EfficientVariableConstraint(constraintType, newVariable, newOther)
    else
      EfficientVariableConstraint(constraintType, newVariable, other) // don't update if it is a sort of a constant
  }
}

object EfficientVariableConstraint {
  val EQUALVARIABLE   = 0
  val UNEQUALVARIABLE = 1
  val EQUALCONSTANT   = 2
  val UNEQUALCONSTANT = 3
  val OFSORT          = 4
  val NOTOFSORT       = 5
}

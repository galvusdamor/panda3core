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

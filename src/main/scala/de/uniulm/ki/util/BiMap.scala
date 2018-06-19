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

package de.uniulm.ki.util

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BiMap[A, B](toMap: Map[A, B], fromMap: Map[B, A]) {
  // consistency
  fromMap foreach { case (a, b) => assert(toMap(b) == a) }
  toMap foreach { case (a, b) => assert(fromMap(b) == a) }


  def apply(a: A): B = toMap(a)

  def back(b: B): A = fromMap(b)
}

object BiMap {
  def apply[A, B](pairs: Seq[(A, B)]): BiMap[A, B] = {
    val toMap = pairs.toMap
    val fromMap = (pairs map { _.swap }).toMap

    BiMap(toMap, fromMap)
  }
}

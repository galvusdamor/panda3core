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
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientUnionFind(parent: Array[Int] = Array()) {

  /**
    * Returns the canonical representative inside the union find.
    * The result will be negative if this is a constant
    */
  def getRepresentative(v: Int): Int = if (v < 0) v
  else {
    if (parent(v) == v) v
    else {
      val representative = getRepresentative(parent(v))
      parent(v) = representative
      representative
    }
  }

  def assertEqual(v1: Int, v2: Int): Boolean = {
    val r1 = getRepresentative(v1)
    val r2 = getRepresentative(v2)

    if (r1 == r2) true // already equal
    else if (r1 >= 0 || r2 >= 0) {
      // union is possible
      if (r1 >= 0) parent(r1) = r2 else parent(r2) = r1
      false
    } else false // different constants cannot be seq equal
  }

  def addVariables(newVariables: Int): EfficientUnionFind = {
    val clonedParent = new Array[Int](parent.length + newVariables)

    var i = 0
    while (i < clonedParent.length) {
      if (i < parent.length) clonedParent(i) = parent(i) else clonedParent(i) = i
      i += 1
    }

    new EfficientUnionFind(clonedParent)
  }

  /**
    * Returns the number of sets in this union find
    */
  def numberOfUniqueElements() : Int = {
    var uniq = 0
    var i = 0
    while (i < parent.length){
      if (parent(i) == i) uniq += 1
      i +=1
    }

    uniq
  }

}

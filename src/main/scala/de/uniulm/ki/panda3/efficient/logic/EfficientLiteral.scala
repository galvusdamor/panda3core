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

package de.uniulm.ki.panda3.efficient.logic

/**
  * Represents an efficient literal
  *
  * If the value of a variable is negative it is in fact a constant. See [[de.uniulm.ki.panda3.efficient.switchConstant]]
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientLiteral(predicate: Int, isPositive: Boolean, parameterVariables: Array[Int]) {

  val isNegative : Boolean = !isPositive

  def checkPredicateAndSign(other: EfficientLiteral): Boolean = predicate == other.predicate && isPositive == other.isPositive
}

//scalastyle:off covariant.equals
case class EfficientGroundLiteral(predicate: Int, isPositive : Boolean, arguments: Array[Int]) {
  // we need a special equals as we use arrays
  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[EfficientGroundLiteral]) {
    val that = o.asInstanceOf[EfficientGroundLiteral]
    if (this.predicate != that.predicate) false else
      this.isPositive == that.isPositive && (this.arguments sameElements that.arguments)
  } else false

  override def hashCode(): Int = predicate
}

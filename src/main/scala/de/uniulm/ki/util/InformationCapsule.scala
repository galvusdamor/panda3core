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

import scala.collection.mutable


class Distribution() {
  private val innerDistribution: mutable.Map[Double, Int] = new mutable.HashMap[Double, Int]().withDefaultValue(0)

  def add(value: Double): Unit = innerDistribution.put(value, innerDistribution(value) + 1)

  def numberOfSamples(): Int = innerDistribution.values sum

  def mean(): Double = (innerDistribution map { case (a, b) => a * b } sum) / numberOfSamples()
}


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class InformationCapsule extends DataCapsule {
  private val internalInformationInt         : mutable.Map[String, Int]          = new mutable.HashMap[String, Int]().withDefaultValue(0)
  private val internalInformationString      : mutable.Map[String, String]       = new mutable.HashMap[String, String]().withDefaultValue("")
  private val internalInformationDistribution: mutable.Map[String, Distribution] = new mutable.HashMap[String, Distribution]().withDefaultValue(new Distribution())

  // set
  def set(key: String, value: Int): Unit = internalInformationInt.put(key, value)

  def set(key: String, value: String): Unit = internalInformationString.put(key, value)

  // basic arithmetics
  def add(key: String, value: Int): Unit = internalInformationInt.put(key, value + internalInformationInt(key))

  def subtract(key: String, value: Int): Unit = add(key, -value)

  def increment(key: String): Unit = add(key, 1)

  def decrement(key: String): Unit = add(key, -1)


  // min, max
  def min(key: String, value: Int): Unit = internalInformationInt.put(key, math.min(value, internalInformationInt(key)))

  def max(key: String, value: Int): Unit = internalInformationInt.put(key, math.max(value, internalInformationInt(key)))

  def apply (key : String) :Int = apply(key, classOf[Int])

  def apply[T](key: String, returnType : Class[T]): T = returnType match {
    case t if t == classOf[Int] => internalInformationInt(key).asInstanceOf[T]
    case t if t == classOf[String] => internalInformationString(key).asInstanceOf[T]
  }


  // add item to distribution
  def addToDistribution(key: String, value: Double): Unit = {
    val distribution = internalInformationDistribution(key)
    distribution add value
    internalInformationDistribution.put(key, distribution)
  }

  override def dataMap(): Map[String, String] = {
    internalInformationString.toMap ++ (
      internalInformationInt.toMap map { case (a, b) => (a, b.toString) }) ++
      (internalInformationDistribution map { case (a, b) => (a + "-mean", b.mean().toString) })
  }
}

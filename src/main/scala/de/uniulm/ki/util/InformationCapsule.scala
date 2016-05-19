package de.uniulm.ki.util

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class InformationCapsule {
  private val internalInformation: mutable.Map[String, Int] = new mutable.HashMap[String, Int]().withDefaultValue(0)

  // set
  def set(key: String, value: Int): Unit = internalInformation.put(key, value)

  // basic arithmetics
  def add(key: String, value: Int): Unit = internalInformation.put(key, value + internalInformation(key))

  def subtract(key: String, value: Int): Unit = add(key, -value)

  def increment(key: String): Unit = add(key, 1)

  def decrement(key: String): Unit = add(key, 1)


  // min, max
  def min(key: String, value: Int): Unit = internalInformation.put(key, math.min(value, internalInformation(key)))

  def max(key: String, value: Int): Unit = internalInformation.put(key, math.max(value, internalInformation(key)))

  def apply(key: String): Int = internalInformation(key)

  def informationMap: Map[String, Int] = internalInformation.toMap
}

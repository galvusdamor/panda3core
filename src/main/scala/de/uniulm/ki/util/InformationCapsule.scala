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
class InformationCapsule {
  private val internalInformation            : mutable.Map[String, Int]          = new mutable.HashMap[String, Int]().withDefaultValue(0)
  private val internalInformationDistribution: mutable.Map[String, Distribution] = new mutable.HashMap[String, Distribution]().withDefaultValue(new Distribution())

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


  // add item to distribution
  def addToDistribution(key: String, value: Double): Unit = {
    val distribution = internalInformationDistribution(key)
    distribution add value
    internalInformationDistribution.put(key, distribution)
  }

  // access through immutable datastructures
  def informationMap: Map[String, Int] = internalInformation.toMap ++ (internalInformationDistribution map { case (a, b) => (a, b.mean().round.toInt) } toMap)
}
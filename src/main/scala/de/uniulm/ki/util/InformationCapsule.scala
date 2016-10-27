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
  private val internalInformation            : mutable.Map[String, Int]          = new mutable.HashMap[String, Int]().withDefaultValue(0)
  private val internalStringInformation      : mutable.Map[String, String]       = new mutable.HashMap[String, String]().withDefaultValue("")
  private val internalInformationDistribution: mutable.Map[String, Distribution] = new mutable.HashMap[String, Distribution]().withDefaultValue(new Distribution())

  // set
  def set(key: String, value: Int): Unit = internalInformation.put(key, value)

  // basic arithmetics
  def add(key: String, value: Int): Unit = internalInformation.put(key, value + internalInformation(key))

  def add(key: String, value: String): Unit = internalStringInformation.put(key, value + internalStringInformation(key))

  def subtract(key: String, value: Int): Unit = add(key, -value)

  def increment(key: String): Unit = add(key, 1)

  def decrement(key: String): Unit = add(key, -1)


  // min, max
  def min(key: String, value: Int): Unit = internalInformation.put(key, math.min(value, internalInformation(key)))

  def max(key: String, value: Int): Unit = internalInformation.put(key, math.max(value, internalInformation(key)))

  def apply (key : String) :Int = apply(key, classOf[Int])

  def apply[T](key: String, returnType : Class[T]): T = returnType match {
    case t if t == classOf[Int] => internalInformation(key).asInstanceOf[T]
    case t if t == classOf[String] => internalStringInformation(key).asInstanceOf[T]
  }


  // add item to distribution
  def addToDistribution(key: String, value: Double): Unit = {
    val distribution = internalInformationDistribution(key)
    distribution add value
    internalInformationDistribution.put(key, distribution)
  }

  // access through immutable datastructures
  override def floatingDataMap(): Map[String, Double] =
    (internalInformation.toMap map { case (a, b) => (a, b.toDouble) }) ++ (internalInformationDistribution map { case (a, b) => (a, b.mean()) } toMap)

  override def integralDataMap(): Map[String, Long] = floatingDataMap map { case (a, b) => (a, b.round) }

  def stringDataMap(): Map[String, String] = internalStringInformation.toMap
}
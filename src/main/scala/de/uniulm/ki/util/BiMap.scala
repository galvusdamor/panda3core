package de.uniulm.ki.util

import scala.collection.immutable.HashMap

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BiMap[A, B](toMap: Map[A, B], fromMap: Map[B, A]) {
  // consistency
  fromMap foreach { case (a, b) => assert(toMap(b) == a) }
  toMap foreach { case (a, b) => assert(fromMap(b) == a) }


  def apply(a: A): B = toMap(a)

  def apply(b: B): A = fromMap(b)
}

object BiMap {
  def apply[A, B](pairs: Seq[(A, B)]): BiMap[A, B] = {
    val toMap = pairs.toMap
    val fromMap = (pairs map { _.swap }).toMap

    BiMap(toMap, fromMap)
  }
}
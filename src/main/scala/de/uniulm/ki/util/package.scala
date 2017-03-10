package de.uniulm.ki

import java.io.{PrintWriter, File}

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object util {
  def writeStringToFile(s: String, file: String): Unit = writeStringToFile(s, new File(file))

  def writeStringToFile(s: String, file: File): Unit = {
    Some(new PrintWriter(file)).foreach { p => p.write(s); p.close() }
  }


  def allMappings[A, B](listA: Seq[A], listB: Seq[B]): Seq[Seq[(A, B)]] = if (listA.isEmpty || listB.isEmpty) Nil :: Nil
  else {
    val aElem = listA.head
    val remListA = listA.tail
    listB flatMap { bElem =>
      val remListB = listB filter { _ != bElem }
      allMappings(remListA, remListB) map { case l => l :+(aElem, bElem) }
    }
  }

  def crossProduct[A](list: Seq[Seq[A]]): Seq[Seq[A]] = if (list.isEmpty) Nil :: Nil
  else {
    val subList = crossProduct(list.tail)
    list.head flatMap { e => subList map {l => l :+ e} }
  }

  def arrayContains[A](array: Array[A], element: A): Boolean = {
    var i = 0
    var found = false
    while (i < array.length && !found) {
      if (array(i) == element) found = true
      i += 1
    }
    found
  }

  def allSubsets[A](seq: Seq[A]): Seq[Seq[A]] = seq.toSet.subsets() map { _.toSeq } toSeq

  def memoise[Input, Output](function: Input => Output): Input => Output = {

    val memoisationMap = new mutable.HashMap[Input, Output]()

    def apply(input: Input): Output = {
      if (memoisationMap contains input){ memoisationMap(input)}
      else {
        val newValue = function(input)
        memoisationMap(input) = newValue
        newValue
      }
    }

    apply
  }
}
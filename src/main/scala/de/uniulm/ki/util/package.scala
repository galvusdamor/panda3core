package de.uniulm.ki

import java.io.{PrintWriter, File}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object util {
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


}

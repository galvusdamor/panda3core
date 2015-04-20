package de.uniulm.ki.panda3

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object Main {

  val a = 32

  private val f = 0


  def quick(l: Seq[Int]): Seq[Int] = if (l.size == 0 || l.size == 1) l
                                     else {
    val pivot: Int = l.head
                                       val rest: Seq[Int] = l.tail

    val smaller = rest filter {_ < pivot}
    val greater = rest filter {_ > pivot}

                                       (quick(smaller) :+ pivot) ++ quick(greater)
                                     }


  def main(args: Array[String]) {
    var x = 5
    val z = Math.sqrt(x)
    println(z)


    val list = 4 :: 1 :: 78 :: 2 :: 25 :: 0 :: Nil

    println(quick(list))

  }

}

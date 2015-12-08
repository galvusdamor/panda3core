package de.uniulm.ki.panda3.efficient.domain

/**
 *
 * Assumptions:
 *
 *  - Sorts are numbered 0..sz(subSortsForSort)
 *  - Constants are numbered
 *  - Predicates are numbered 0..sz(predicates) and the contents of that array are the predicates parameters
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class EfficientDomain(subSortsForSort: Array[Array[Int]], sortsOfConstant : Array[Array[Int]], predicates : Array[Array[Int]] ) {

  val constantsOfSort : Array[Array[Int]] = {
    val ret : Array[Array[Int]] = new Array[Array[Int]](subSortsForSort.length)

    val sortsOfConstantZipped = sortsOfConstant.zipWithIndex

    var i = 0
    while (i < ret.length){
      ret(i) = (sortsOfConstantZipped filter {_._1.contains(i)}).map({_._2}).array
      i = i+1
    }
    ret
  }
}

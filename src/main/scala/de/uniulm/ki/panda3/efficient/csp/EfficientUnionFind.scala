package de.uniulm.ki.panda3.efficient.csp

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientUnionFind(parent: Array[Int] = Array()) {

  /**
    * Returns the canonical representative inside the union find.
    * The result will be negative if this is a constant
    */
  def getRepresentative(v: Int): Int = if (v < 0) v
  else {
    if (parent(v) == v) v
    else {
      val representative = getRepresentative(parent(v))
      parent(v) = representative
      representative
    }
  }

  def assertEqual(v1: Int, v2: Int): Boolean = {
    val r1 = getRepresentative(v1)
    val r2 = getRepresentative(v2)

    if (r1 == r2) true // already equal
    else if (r1 >= 0 || r2 >= 0) {
      // union is possible
      if (r1 >= 0) parent(r1) = r2 else parent(r2) = r1
      false
    } else false // different constants cannot be seq equal
  }

  def addVariables(newVariables: Int): EfficientUnionFind = {
    val clonedParent = new Array[Int](parent.length + newVariables)

    var i = 0
    while (i < clonedParent.length) {
      if (i < parent.length) clonedParent(i) = parent(i) else clonedParent(i) = i
      i += 1
    }

    new EfficientUnionFind(clonedParent)
  }

}
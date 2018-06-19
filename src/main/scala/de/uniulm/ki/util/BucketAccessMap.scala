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

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BucketAccessMap(originalMap: Map[Array[Int], Double]) {
  assert((originalMap.keys map { _.length } toSet).size <= 1)

  // build the bucket structure
  // contains the base index for the next entry, or -1 if this is the final bucket
  private val (bucketArray, lengthArray): (Array[Int], Array[Int]) = {

    def buildIndexArrayFrom(indices: Seq[Array[Int]]): (Array[Int], Array[Int]) = if (indices.isEmpty) (Array(), Array())
    else if (indices.length == 1 && indices.head.length == 0) (Array(-1), Array(1))
    else {
      // final all staring indices
      val startingIndices = indices map { _.head }
      val highestIndex = startingIndices.max + 1

      val mainPointerArray = (Array.fill[Int](highestIndex)(-2), Array.fill[Int](highestIndex)(highestIndex))
      //mainPointerArray(0) = highestIndex // insert the length pointer

      if (indices.head.length == 1) {
        mainPointerArray
      } else {
        // call subarrays
        val subArrays = (indices groupBy { _.head } map { case (first, rest) => (first, buildIndexArrayFrom(rest map { _.tail })) }).toSeq.sortBy({ _._1 })

        subArrays.foldLeft(mainPointerArray)({ case ((pointerArray, sizeArray), (key, (subArray, subSizeArray))) =>
          val increasedSubArray = subArray map { case -1 => -1; case -2 => -2; case x => x + pointerArray.length }
          val newPointerArray = pointerArray ++ increasedSubArray
          val newSizeArray = sizeArray ++ subSizeArray
          newPointerArray(key) = pointerArray.length

          (newPointerArray, newSizeArray)
                                             })
      }
    }

    buildIndexArrayFrom(originalMap.keys.toSeq)
  }


  //println()
  //println()

  override def toString: String = "Bucket Array SZ " + bucketArray.length + " => " + bucketArray.mkString("\n") + "\n" +
    (originalMap map { case (k, v) => k.mkString("[", ",", "]") + " -> " + v }) + "\n" +
    "Value Array SZ " + finalArray.length + " => " + finalArray.mkString("\n")

  private val finalArray: Array[Double] = originalMap.foldLeft(Array.fill[Double](bucketArray.length)(Double.MaxValue))(
    { case (array, (key, value)) => array(getIndex(key)) = value; array })

  def getIndex(key: Array[Int]): Int = {
    def getIndex(basePosition: Int, position: Int): Int = if (lengthArray(basePosition) <= key(position)) {
      //println("Length violation " + lengthArray(basePosition) + " < " + position)
      -1
    }
    else {
      //println(key.length + " " + position + " ! " + (basePosition + key(position)))
      val content = bucketArray(basePosition + key(position))

      if (content < 0) basePosition + key(position) else getIndex(content, position + 1)
    }

    //println("Bucket Array SZ " + bucketArray.length + " => " + bucketArray.mkString(","))
    //println("Length Array SZ " + lengthArray.length + " => " + lengthArray.mkString(","))
    //println(key.mkString("(",",",")"))

    if (key.length == 0) 0 else getIndex(0, 0)
  }

  def apply(key: Array[Int]): Double = {
    val index = getIndex(key)
    if (index >= 0 && finalArray.size > 0) finalArray(index) else Double.MaxValue
  }


  originalMap foreach { case (k, v) => assert(apply(k) == v) }
}

object BucketAccessMap {
  private val FINALVALUE   = -1
  private val NOTCONTAINED = -2
}

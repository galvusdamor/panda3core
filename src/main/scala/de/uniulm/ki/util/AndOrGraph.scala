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

import scala.collection.mutable

/**
  * And Vertices are those that occur in and rules, i.e., all their successors are or
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait AndOrGraph[T, A <: T, O <: T] extends DirectedGraphWithAlgorithms[T] {
  val andVertices: Set[A]
  val orVertices : Set[O]

  val andEdges: Map[A, Set[O]]
  val orEdges : Map[O, Set[A]]

  /** a list of all node of the graph */
  lazy val vertices: Seq[T] = (andVertices ++ orVertices).toSeq

  /** adjacency list of the graph */
  lazy val edges: Map[T, Seq[T]] = {
    val edg = (andEdges ++ orEdges) map { case (a, b) => (a, b.toSeq) }
    //edg.keys foreach {x => assert(vertices contains x)}
    //edg.values.flatten foreach { x => assert(vertices contains x) }
    edg
  }

  /** does not remove vertices */
  def pruneToEntities(restrictToEntities: Set[T]): AndOrGraph[T, A, O] = {
    val prunedAndVertices = andVertices filter { restrictToEntities.contains }
    val prunedOrVertices = orVertices filter { restrictToEntities.contains }
    val prunedAndEdges = andEdges filter { case (a, _) => restrictToEntities contains a }
    val prunedOrEdges = orEdges filter { case (o, _) => restrictToEntities contains o }

    SimpleAndOrGraph(prunedAndVertices, prunedOrVertices, prunedAndEdges, prunedOrEdges)
  }

  def minSumTraversal(root: A, evaluate: (A => Double), sumInitialValue: (O => Double)): Double = minSumTraversalMap(evaluate, sumInitialValue)(root)

  def minSumTraversalMap(evaluate: (A => Double), sumInitialValue: (O => Double)): Map[A, Double] = {
    val seen: scala.collection.mutable.Map[T, Double] = mutable.HashMap[T, Double]()

    def mini(root: A): Boolean =
      if (!andEdges.contains(root) || andEdges(root).isEmpty) {
        val v = evaluate(root)
        seen(root) = v
        false
      } else {
        //seen.put(root, Integer.MAX_VALUE) // side effect
        val it = andEdges(root).iterator
        var value: Double = Integer.MAX_VALUE
        while (it.hasNext) value = Math.min(value, seen(it.next()))

        if (seen(root) != value) {
          seen.put(root, value) // side effect
          true
        } else false
      }

    def sum(root: O): Boolean = {
      val it = orEdges(root).iterator
      var value = sumInitialValue(root)
      while (it.hasNext) value += seen(it.next())
      if (seen(root) != value) {
        seen(root) = value
        true
      } else false
    }


    val topOrd = condensation.topologicalOrdering.get.reverse

    topOrd foreach { scc =>
      scc foreach { x => seen(x) = Integer.MAX_VALUE }
      scc foreach {
        case a: A if andEdges.contains(a) => andEdges(a) foreach { x => assert(seen contains x, "EDGE " + a + "->" + x) }
        case o: O if orEdges.contains(o)  => orEdges(o) foreach { x => assert(seen contains x, "EDGE " + o + "->" + x) }
      }

      var changed = true
      while (changed) {
        changed = scc map {
          case a: A if andEdges.contains(a) => mini(a)
          case o: O if orEdges.contains(o)  => sum(o)
        } exists { i => i }
      }
    }

    seen collect { case (a: A, v) if andVertices contains a => a -> v } toMap
  }

  override protected def dotVertexStyleRenderer(v: T): String = v match {
    case a: A if andEdges.contains(a) => ",shape = box, style = filled, fillcolor = red"
    case o: O if orEdges.contains(o)  => ""
  }

  override def reachableFrom(node: T): Set[T] = super.reachableFrom(node)
}


case class SimpleAndOrGraph[T, A <: T, O <: T](andVertices: Set[A], orVertices: Set[O], andEdges: Map[A, Set[O]], orEdges: Map[O, Set[A]]) extends AndOrGraph[T, A, O] {
  andEdges foreach { a => assert(andVertices contains a._1) }
  orEdges foreach { o => assert(orVertices contains o._1) }
  andEdges foreach { _._2 foreach { o => assert(orVertices contains o) } }
  orEdges foreach { _._2 foreach { a => assert(andVertices contains a) } }
  assert(andVertices.size == andEdges.size, andVertices.size + "!=" + andEdges.size)
  assert(orVertices.size == orEdges.size, orVertices.size + "!=" + orEdges.size)
}

case class IntegerAntOrGraph(andVertices: Set[Int], orVertices: Set[Int], andEdges: Map[Int, Set[Int]], orEdges: Map[Int, Set[Int]]) extends AndOrGraph[Int, Int, Int] {
  andEdges foreach { a => assert(andVertices contains a._1) }
  orEdges foreach { o => assert(orVertices contains o._1) }
  andEdges foreach { _._2 foreach { o => assert(orVertices contains o) } }
  orEdges foreach { _._2 foreach { a => assert(andVertices contains a) } }
  assert(andVertices.size == andEdges.size, andVertices.size + "!=" + andEdges.size)
  assert(orVertices.size == orEdges.size, orVertices.size + "!=" + orEdges.size)

  assert(andVertices forall { _ >= 0 })

  private lazy val maximumValue   : Int = (andVertices ++ orVertices).max
  private lazy val minimumValue   : Int = (andVertices ++ orVertices).min
  private lazy val maximumAndValue: Int = andVertices.max

  private lazy val offset           = -1 * minimumValue
  private lazy val indexArrayLength = maximumValue + offset + 1

  private lazy val reversedCondensedTopologicalOrderingArray: Array[Array[Int]] = condensation.topologicalOrdering.get.reverse map { _.toArray } toArray

  private lazy val isAndVertex: Array[Boolean] = Range(0, indexArrayLength) map { i => andVertices.contains(i - offset) } toArray
  private lazy val isOrVertex : Array[Boolean] = Range(0, indexArrayLength) map { i => orVertices.contains(i - offset) } toArray

  private lazy val adjacencyList: Array[Array[Int]] = Range(0, indexArrayLength) map { i =>
    val actualIndex = i - offset
    edges(actualIndex).toArray
  } toArray

  private lazy val reachabilityBitSet: Array[mutable.BitSet] = {
    val reachabilities: Array[mutable.BitSet] = new Array[mutable.BitSet](indexArrayLength)

    def dfs(scc: Set[Int]): Unit = {
      // if any node of the scc is already in the map, simply ignore it
      if (reachabilities(scc.head + offset) == null) {
        condensation.edges(scc) foreach dfs

        val allReachable = new mutable.BitSet()
        scc foreach { i => allReachable add (i + offset) }

        condensation.edges(scc) foreach { neighbour =>
          allReachable ++= reachabilities(neighbour.head + offset)
          if (neighbour.size == 1) allReachable add (neighbour.head + offset)
        }

        scc foreach { i => reachabilities(i + offset) = allReachable }
      }
    }
    // run the dfs on all source SCCs of the condensation
    condensation.sources foreach dfs

    reachabilities
  }

  def minSumTraversalArray(rootNodes: Array[Int], evaluate: (Int => Double), sumInitialValue: (Int => Double)): Array[Double] = {
    val seen: Array[Double] = new Array[Double](indexArrayLength)
    val computeValueFor: Array[Boolean] = new Array[Boolean](indexArrayLength)
    var i = 0
    while (i < indexArrayLength) {
      seen(i) = -1
      computeValueFor(i) = false
      var j = 0
      while (j < rootNodes.length && !computeValueFor(i)) {
        computeValueFor(i) |= reachabilityBitSet(rootNodes(j) + offset)(i)
        j += 1
      }
      i += 1
    }

    def mini(root: Int): Boolean =
      if (adjacencyList(root + offset).length == 0) {
        val v = evaluate(root)
        seen(root + offset) = v
        false
      } else {
        var value: Double = Integer.MAX_VALUE
        var pos = 0
        while (pos < adjacencyList(root + offset).length) {
          value = Math.min(value, seen(adjacencyList(root + offset)(pos) + offset))
          pos += 1
        }

        if (seen(root + offset) != value) {
          seen(root + offset) = value // side effect
          true
        } else false
      }

    def sum(root: Int): Boolean = {
      var value = sumInitialValue(root)
      var pos = 0
      while (pos < adjacencyList(root + offset).length) {
        value += seen(adjacencyList(root + offset)(pos) + offset)
        pos += 1
      }
      if (value > Integer.MAX_VALUE) value = Integer.MAX_VALUE

      if (seen(root + offset) != value) {
        seen(root + offset) = value
        true
      } else false
    }

    var sccIndex = 0
    while (sccIndex < reversedCondensedTopologicalOrderingArray.length) {
      val scc = reversedCondensedTopologicalOrderingArray(sccIndex)

      var sccElement = 0
      while (sccElement < scc.length) {
        seen(scc(sccElement) + offset) = Integer.MAX_VALUE
        sccElement += 1
      }

      var changed = true
      while (changed) {
        // reset
        changed = false

        sccElement = 0
        while (sccElement < scc.length) {
          val task = scc(sccElement)
          // ignore element if not reachable from root
          if (computeValueFor(task + offset)) {
            if (isAndVertex(task + offset)) changed |= mini(task)
            else if (isOrVertex(task + offset)) changed |= sum(task)
          }
          sccElement += 1
        }
      }
      sccIndex += 1
    }

    // copy seen
    val result = new Array[Double](maximumAndValue + 1)
    i = 0
    while (i <= maximumAndValue) {
      result(i) = seen(i + offset)
      i += 1
    }

    result
  }
}

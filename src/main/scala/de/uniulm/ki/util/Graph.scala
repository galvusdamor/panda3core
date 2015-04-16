package de.uniulm.ki.util

import scala.collection.mutable

/**
 * Represented general (directed) Graphs with noes of type T.
 *
 * The edges of the graph are stored in an adjacency map.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Graph[T] {

  /** a list of all node of the graph */
  def nodes: Seq[T]

  /** adjacency list of the graph */
  def edges: Map[T, Seq[T]]


  lazy val stronglyConnectedComponents: Seq[Seq[T]] = {
    // use Tarjan's algorithm to find the SCCs
    val lowLink: mutable.Map[T, Int] = mutable.HashMap()
    val dfsNumber: mutable.Map[T, Int] = mutable.HashMap()
    val stack: mutable.Stack[T] = mutable.Stack()
    val onStack: mutable.Set[T] = mutable.HashSet()

    var dfs = 0

    def tarjan(node: T): Seq[Seq[T]] = {
      dfsNumber(node) = dfs
      lowLink(node) = dfs
      dfs = dfs + 1
      stack.push(node)
      onStack.add(node)

      val recursionResult: Seq[Seq[T]] = edges(node) flatMap { neighbour =>
        if (dfsNumber.contains(neighbour)) {
          // search on stack, if found adjust lowlink
          if (onStack(neighbour))
            lowLink(node) = math.min(lowLink(node), dfsNumber(neighbour))
          Nil
        } else {
          val components = tarjan(neighbour)
          // adjust lowlink
          lowLink(node) = math.min(lowLink(node), lowLink(neighbour))
          components
        }
      }

      if (dfsNumber(node) == lowLink(node)) {
        val sccNodes: mutable.ListBuffer[T] = mutable.ListBuffer()
        var stop = false
        while (!stop) {
          val v = stack.pop()
          onStack.remove(v)
          sccNodes += v

          if (v == node)
            stop = true
        }
        recursionResult :+ sccNodes.toSeq
      } else recursionResult
    }


    nodes flatMap { node => if (!dfsNumber.contains(node)) tarjan(node) else Nil }

  }


}


case class SimpleGraph[T](nodes: Seq[T], edges: Map[T, Seq[T]]) extends Graph[T] {}

object SimpleGraph {
  def apply[T](nodes: Seq[T], edges: Seq[(T, T)]): SimpleGraph[T] = SimpleGraph(nodes, (nodes zip (nodes map { n => edges.filter({_._1 == n}).map({_._2}) })).toMap)
}
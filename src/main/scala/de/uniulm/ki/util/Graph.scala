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

  require(edges.size == nodes.size)

  /** list of all edges as a list of pairs */
  final lazy val edgeList: Seq[(T, T)] = edges.toSeq flatMap { case (node1, neighbours) => neighbours map {(node1, _)} }

  /** in- and out- degrees of all nodes */
  lazy val degrees: Map[T, (Int, Int)] = {
    val degCount: mutable.Map[T, (Int, Int)] = mutable.Map().withDefaultValue((0, 0))

    edgeList foreach { case (from, to) =>
      val degFrom = degCount(from)
      degCount(from) = (degFrom._1, degFrom._2 + 1)
      val degTo = degCount(to)
      degCount(to) = (degTo._1 + 1, degTo._2)
    }
    // convert to immutable map
    degCount.toMap
  }


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

  def getComponentOf(node: T): Option[Seq[T]] = stronglyConnectedComponents find {_.contains(node)}


  lazy val condensation: Graph[Seq[T]] =
    SimpleGraph(stronglyConnectedComponents, (edgeList map { case (from, to) => (getComponentOf(from).get, getComponentOf(to).get) }) collect { case e@(from, to) if from != to => e })

  lazy val sources: Seq[T] = (degrees collect { case (node, (in, _)) if in == 0 => node }).toSeq
}


case class SimpleGraph[T](nodes: Seq[T], edges: Map[T, Seq[T]]) extends Graph[T] {}

object SimpleGraph {
  def apply[T](nodes: Seq[T], edges: Seq[(T, T)]): SimpleGraph[T] = SimpleGraph(nodes, (nodes zip (nodes map { n => edges.filter({_._1 == n}).map({_._2}) })).toMap)
}
package de.uniulm.ki.util

import de.uniulm.ki.panda3.symbolic.PrettyPrintable

import scala.collection.mutable

/**
  * Represented general (directed) Graphs with noes of type T.
  *
  * The edges of the graph are stored in an adjacency map.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DirectedGraph[T] extends DotPrintable[Unit] {

  /** a list of all node of the graph */
  def vertices: Seq[T]

  /** adjacency list of the graph */
  def edges: Map[T, Seq[T]]

  /** adjacency list of the graph */
  private lazy val edgesSet: Map[T, Set[T]] = edges map { case (a, b) => (a, b.toSet) }

  // TODO: add this as a delayed intializer
  //require(edges.size == vertices.size)

  /** list of all edges as a list of pairs */
  final lazy val edgeList: Seq[(T, T)] = edges.toSeq flatMap { case (node1, neighbours) => neighbours map { (node1, _) } }

  /** in- and out- degrees of all nodes */
  lazy val degrees: Map[T, (Int, Int)] = {
    val degCount: mutable.Map[T, (Int, Int)] = mutable.Map()
    vertices foreach { v => degCount(v) = (0, 0) }

    edgeList foreach { case (from, to) =>
      val degFrom = degCount(from)
      degCount(from) = (degFrom._1, degFrom._2 + 1)
      val degTo = degCount(to)
      degCount(to) = (degTo._1 + 1, degTo._2)
    }
    // convert to immutable map
    degCount.toMap
  }


  lazy val stronglyConnectedComponents: Seq[Set[T]] = {
    // use Tarjan's algorithm to find the SCCs
    val lowLink: mutable.Map[T, Int] = mutable.HashMap()
    val dfsNumber: mutable.Map[T, Int] = mutable.HashMap()
    val stack: mutable.Stack[T] = mutable.Stack()
    val onStack: mutable.Set[T] = mutable.HashSet()

    var dfs = 0

    def tarjan(node: T): Seq[Set[T]] = {
      dfsNumber(node) = dfs
      lowLink(node) = dfs
      dfs = dfs + 1
      stack.push(node)
      onStack.add(node)

      val recursionResult: Seq[Set[T]] = if (!edges.contains(node)) Nil
      else edges(node) flatMap { neighbour =>
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
        val sccNodes: mutable.HashSet[T] = mutable.HashSet()
        var stop = false
        while (!stop) {
          val v = stack.pop()
          onStack.remove(v)
          sccNodes += v

          if (v == node)
            stop = true
        }
        recursionResult :+ sccNodes.toSet
      } else recursionResult
    }
    vertices flatMap { node => if (!dfsNumber.contains(node)) tarjan(node) else Nil }
  }

  lazy val getComponentOf: Map[T, Set[T]] = {
    val x = (stronglyConnectedComponents flatMap { scc => scc map { elem => (elem, scc) } }).toMap
      assert(x.size == vertices.length)
    x
  }


  lazy val condensation: DirectedGraph[Set[T]] = {
    val edgeMap: Map[Set[T], Seq[Set[T]]] = (stronglyConnectedComponents map { comp =>
      val edgeto = comp flatMap { elem => edges(elem) map getComponentOf filter { _ ne comp } }
      (comp, edgeto.toSeq)
    }).toMap
    SimpleDirectedGraph(stronglyConnectedComponents, edgeMap)
  }

  lazy val sources: Seq[T] = (degrees collect { case (node, (in, _)) if in == 0 => node }).toSeq


  /** computes for each node, which other nodes can be reached from it using the edges of the graph */
  // TODO: this computation might be inefficient
  lazy val reachable: Map[T, Set[T]] = {
    val reachabilityMap: mutable.Map[T, Set[T]] = mutable.HashMap()

    def dfs(scc: Set[T]): Unit = {
      // if any node of the scc is already in the map, simply ignore it
      if (!reachabilityMap.contains(scc.head)) {
        condensation.edges(scc) foreach dfs

        val allReachable: Set[T] =
          (if (scc.size > 1) scc else Set()) ++ (condensation.edges(scc) flatMap { neighbour => reachabilityMap(neighbour.head) ++ (if (neighbour.size == 1) neighbour else Nil) })

        scc foreach { reachabilityMap(_) = allReachable }
      }
    }
    // run the dfs on all source SCCs of the condensation
    condensation.sources foreach dfs
    assert(reachabilityMap.size == vertices.size)
    reachabilityMap.toMap
  }


  lazy val transitiveClosure = SimpleDirectedGraph(vertices, reachable map { case (a, b) => (a, b.toSeq) })


  /**
    * Compute a topological ordering of the graph.
    * If the graph contains a cycle this function returns None.
    */
  lazy val topologicalOrdering: Option[Seq[T]] = {
    val color: mutable.Map[T, Int] = mutable.Map(vertices map { (_, 0) }: _*)

    // dfs
    def dfs(v: T): Option[Seq[T]] = if (color(v) == 1) None
    else if (color(v) == 2) Some(Nil)
    else {
      // set own color to grey
      color.put(v, 1)
      val order: Option[Seq[T]] = edges(v).foldLeft[Option[Seq[T]]](Some(Nil))({
                                                                                 case (None, _)           => None
                                                                                 case (Some(topOrder), n) => val nTopOrder = dfs(n)
                                                                                   nTopOrder match {
                                                                                     case None         => None
                                                                                     case Some(nOrder) =>
                                                                                       Some(nOrder ++ topOrder)
                                                                                   }
                                                                               })

      // set own color to black
      color.put(v, 2)

      order match {
        case None    => None
        case Some(o) => Some(Seq(v) ++ o)
      }
    }


    // run dfs on every vertex
    vertices.foldLeft[Option[Seq[T]]](Some(Nil))({
                                                   case (None, _)        => None
                                                   case (Some(order), v) => dfs(v) match {
                                                     case None            => None
                                                     case Some(nextOrder) => Some(nextOrder ++ order)
                                                   }
                                                 })
  }

  override lazy val dotString: String = {
    val dotStringBuilder = new StringBuilder()

    dotStringBuilder append "digraph someDirectedGraph{\n"
    edgeList foreach { case (a, b) => dotStringBuilder append "\ta" + vertices.indexOf(a) + " -> a" + vertices.indexOf(b) + ";\n" }
    dotStringBuilder append "\n"
    vertices.zipWithIndex foreach { case (obj, index) =>
      val string = (obj match {case pp: PrettyPrintable => pp.shortInfo; case x => x.toString}).replace('\"', '\'')
      dotStringBuilder append ("\ta" + index + "[label=\"" + string + "\"];\n")
    }
    dotStringBuilder append "}"

    dotStringBuilder.toString
  }

  /** The DOT representation of the object with options */
  override def dotString(options: Unit): String = dotString
}


case class SimpleDirectedGraph[T](vertices: Seq[T], edges: Map[T, Seq[T]]) extends DirectedGraph[T] {}

object SimpleDirectedGraph {
  def apply[T](nodes: Seq[T], edges: Seq[(T, T)]): SimpleDirectedGraph[T] = SimpleDirectedGraph(nodes, (nodes zip (nodes map { n => edges.filter({ _._1 == n }).map({ _._2 }) })).toMap)
}
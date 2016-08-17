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
  private lazy val edgesSet        : Map[T, Set[T]] = edges map { case (a, b) => (a, b.toSet) }
  private lazy val reversedEdgesSet: Map[T, Set[T]] = vertices map { v => v -> (vertices filter { v2 => edgesSet(v2) contains v } toSet) } toMap

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


  def getVerticesInDistance(v: T, distance: Int): Seq[T] = if (distance == 0) v :: Nil else edges(v) flatMap { getVerticesInDistance(_, distance - 1) }

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

  def reachableFrom(root: T): Set[T] = {
    val visited = mutable.Set[T]()

    def dfs(node: T): Unit = if (!(visited contains node)) {
      visited add node
      edges(node) foreach dfs
    }

    dfs(root)

    visited.toSet[T]
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

  // Only implemented for acyclic graphs. Therefore Option[Int] as return type
  // For cyclic graphs the problem becomes NP-comlete instead of P for acyclic graphs
  lazy val longestPathLength: Option[Int] = {
    // check if graph is acyclic
    topologicalOrdering match {
      case (None)       => None
      case Some(topOrd) => {
        var nodeLongestPathMap = Map(topOrd.head -> 0)
        for (i <- topOrd.indices) {
          if (nodeLongestPathMap.get(topOrd(i)).isEmpty)
            nodeLongestPathMap += topOrd(i) -> 0
          edges(topOrd(i)) foreach (destination =>
            if (nodeLongestPathMap.get(destination).isEmpty || nodeLongestPathMap(topOrd(i)) >= nodeLongestPathMap(destination))
              nodeLongestPathMap += destination -> (nodeLongestPathMap(topOrd(i)) + 1))
        }

        Some(nodeLongestPathMap.valuesIterator.max)
      }
    }
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

  /**
    * This is not a fast implementation^^
    */
  lazy val allTotalOrderings: Option[Seq[Seq[T]]] = {

    def dfs(processedNodes: Set[T]): Option[Seq[Seq[T]]] = if (processedNodes.size == vertices.size) Some(Nil :: Nil)
    else {
      val potentiallyFirstNodes = vertices filterNot processedNodes.contains filter { v => reversedEdgesSet(v) forall processedNodes.contains }

      val possibleOrderings = potentiallyFirstNodes map { first => dfs(processedNodes + first) map { _ map { s => first +: s } } } collect { case Some(x) => x } flatten

      if (possibleOrderings.isEmpty) None else Some(possibleOrderings)
    }

    dfs(Set())
  }

  lazy val isAcyclic: Boolean = condensation.vertices.length == vertices.length

  /** The DOT representation of the object with options */
  override def dotString(options: Unit): String = dotString
}


case class SimpleDirectedGraph[T](vertices: Seq[T], edges: Map[T, Seq[T]]) extends DirectedGraph[T] {}

object SimpleDirectedGraph {
  def apply[T](nodes: Seq[T], edges: Seq[(T, T)]): SimpleDirectedGraph[T] = {
    edges flatMap { case (a, b) => a :: b :: Nil } foreach { n => assert(nodes contains n) }
    SimpleDirectedGraph(nodes, (nodes zip (nodes map { n => edges.filter({ _._1 == n }).map({ _._2 }) })).toMap)
  }
}

case class SimpleGraphNode(id: String, name: String) extends PrettyPrintable {
  override def shortInfo: String = name

  override def mediumInfo: String = name

  override def longInfo: String = name
}
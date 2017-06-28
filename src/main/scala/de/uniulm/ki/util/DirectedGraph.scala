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
trait DirectedGraph[T] extends DotPrintable[DirectedGraphDotOptions] {

  /** a list of all node of the graph */
  def vertices: Seq[T]

  /** adjacency list of the graph */
  def edges: Map[T, Seq[T]]

  /** adjacency list of the graph */
  lazy val edgesSet: Map[T, Set[T]] = edges map { case (a, b) => (a, b.toSet) }

  lazy val reversedEdgesSet: Map[T, Set[T]] = vertices map { v => v -> (vertices filter { v2 => edgesSet(v2) contains v } toSet) } toMap

  /** Replace all edges by non-edges and vice versa */
  def complementGraph: DirectedGraph[T]

  /** list of all edges as a list of pairs */
  def edgeList: Seq[(T, T)]

  /** in- and out- degrees of all nodes */
  def degrees: Map[T, (Int, Int)]

  def stronglyConnectedComponents: Seq[Set[T]]

  def getComponentOf: Map[T, Set[T]]

  lazy val condensation: DirectedGraph[Set[T]] = {
    val edgeMap: Map[Set[T], Seq[Set[T]]] = (stronglyConnectedComponents map { comp =>
      val edgeto = comp flatMap { elem => edges(elem) map getComponentOf filter { _ ne comp } }
      (comp, edgeto.toSeq)
    }).toMap
    SimpleDirectedGraph(stronglyConnectedComponents, edgeMap)
  }

  def sources: Seq[T]

  def sinks: Seq[T]

  def getVerticesInDistance(v: T, distance: Int): Set[T]

  /** computes for each node, which other nodes can be reached from it using the edges of the graph */
  def reachable: Map[T, Set[T]]

  def reachableFrom(root: T): Set[T]

  lazy val transitiveClosure = SimpleDirectedGraph(vertices, reachable map { case (a, b) => (a, b.toSeq) })

  /**
    * Compute a topological ordering of the graph.
    * If the graph contains a cycle this function returns None.
    */
  def topologicalOrdering: Option[Seq[T]]

  /** Only implemented for acyclic graphs. Therefore Option[Int] as return type
    * For cyclic graphs the problem becomes NP-complete instead of P for acyclic graphs
    */
  def longestPathLength: Option[Int]

  /**
    * This is not a fast implementation^^
    */
  def allTotalOrderings: Option[Seq[Seq[T]]]

  lazy val isAcyclic: Boolean = condensation.vertices.length == vertices.length

  /** Remove as many edges as possible as long as the transitive hull stays the same */
  lazy val transitiveReduction: DirectedGraph[T] = {
    val changingEdgeMap = edges map { case (k, v) => val h = new mutable.HashSet[T](); h ++= v; (k, h) }

    //

    for ((i, j) <- edgeList
         if changingEdgeMap(i) exists { k => reachable(k) contains j }
    ) {
      changingEdgeMap(i).remove(j)
    }


    SimpleDirectedGraph(vertices, changingEdgeMap map { case (k, v) => (k, v.toSeq) })
  }


  override lazy val dotString: String = dotString(DirectedGraphDotOptions())

  /** The DOT representation of the object with options */
  override def dotString(options: DirectedGraphDotOptions): String = dotString(options, { case x => x.toString })

  def dotString(options: DirectedGraphDotOptions, nodeRenderer: T => String): String = {
    val dotStringBuilder = new StringBuilder()

    dotStringBuilder append "digraph someDirectedGraph{\n"
    edgeList foreach { case (a, b) => dotStringBuilder append "\ta" + vertices.indexOf(a) + " -> a" + vertices.indexOf(b) + " [label=\"" + dotEdgeStyleRenderer(a, b) + "\"];\n" }
    dotStringBuilder append "\n"
    vertices.zipWithIndex foreach { case (obj, index) =>
      val string = (if (options.labelNodesWithNumbers) index.toString else obj match {case pp: PrettyPrintable => pp.shortInfo; case x => nodeRenderer(x)}).replace('\"', '\'')
      dotStringBuilder append ("\ta" + index + "[label=\"" + string + "\"" + dotVertexStyleRenderer(obj) + "];\n")
    }
    dotStringBuilder append "}"

    dotStringBuilder.toString
  }

  protected def dotVertexStyleRenderer(v: T): String = ""

  protected def dotEdgeStyleRenderer(from: T, to: T): String = ""


  def map[U](f: T => U): DirectedGraph[U] = SimpleDirectedGraph(vertices map f, edgeList map { case (a, b) => (f(a), f(b)) })
}

case class DirectedGraphDotOptions(labelNodesWithNumbers: Boolean = false)

trait DirectedGraphWithAlgorithms[T] extends DirectedGraph[T] {

  /** a list of all node of the graph */
  def vertices: Seq[T]

  /** adjacency list of the graph */
  def edges: Map[T, Seq[T]]


  // TODO: add this as a delayed intializer
  //require(edges.size == vertices.size)

  /** list of all edges as a list of pairs */
  final lazy val edgeList: Seq[(T, T)] = edges.toSeq flatMap { case (node1, neighbours) => neighbours map { (node1, _) } }

  final lazy val complementGraph: DirectedGraph[T] = SimpleDirectedGraph(vertices, edges map { case (v, ns) => v -> (vertices filterNot ns.contains) })

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

  lazy val sources: Seq[T] = (degrees collect { case (node, (in, _)) if in == 0 => node }).toSeq
  lazy val sinks  : Seq[T] = (degrees collect { case (node, (_, out)) if out == 0 => node }).toSeq


  private val memoisedVerticesInDistance = new mutable.HashMap[(T, Int), Set[T]]()

  def getVerticesInDistance(v: T, distance: Int): Set[T] = if (distance == 0) Set(v)
  else {
    if (memoisedVerticesInDistance contains(v, distance)) memoisedVerticesInDistance((v, distance))
    else {
      val inDistance = edges(v) flatMap { getVerticesInDistance(_, distance - 1) } toSet

      memoisedVerticesInDistance((v, distance)) = inDistance
      inDistance
    }
  }

  /** computes for each node, which other nodes can be reached from it using the edges of the graph */
  // TODO: this computation might be inefficient
  lazy val reachable: Map[T, Set[T]] = {
    val reachabilityMap: mutable.Map[T, Set[T]] = mutable.HashMap()

    def dfs(scc: Set[T]): Unit = {
      // if any node of the scc is already in the map, simply ignore it
      if (!reachabilityMap.contains(scc.head)) {
        condensation.edges(scc) foreach dfs

        val allReachable = new mutable.HashSet[T]()
        if (scc.size > 1) allReachable ++= scc

        condensation.edges(scc) foreach { neighbour =>
          allReachable ++= reachabilityMap(neighbour.head)
          if (neighbour.size == 1) allReachable add neighbour.head
        }

        val reachableSet = allReachable.toSet
        scc foreach { reachabilityMap(_) = reachableSet }
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


  /**
    * Compute a topological ordering of the graph.
    * If the graph contains a cycle this function returns None.
    */
  lazy val topologicalOrdering: Option[Seq[T]] = {
    val color: mutable.Map[T, Int] = mutable.Map(vertices map { (_, 0) }: _*)

    var ordering: Seq[T] = Nil
    var failure = false

    // dfs
    def dfs(v: T): Unit =
      if (failure || color(v) == 1) failure = true
      else if (color(v) != 2) {
        // set own color to grey
        color.put(v, 1)
        edges(v) foreach dfs
        // set own color to black
        color.put(v, 2)
        ordering = ordering.+:(v)
      }
    // run dfs on every vertex
    vertices foreach dfs

    if (failure) None else Some(ordering)
  }

  // Only implemented for acyclic graphs. Therefore Option[Int] as return type
  // For cyclic graphs the problem becomes NP-comlete instead of P for acyclic graphs
  lazy val longestPathLength: Option[Int] = {
    // check if graph is acyclic
    topologicalOrdering match {
      case (None)                    => None
      case Some(topologicalOrdering) => {
        var nodeLongestPathMap = Map(topologicalOrdering.head -> 0)
        for (i <- 0 until topologicalOrdering.size) {
          if (nodeLongestPathMap.get(topologicalOrdering(i)).isEmpty)
            nodeLongestPathMap += topologicalOrdering(i) -> 0
          edges(topologicalOrdering(i)) foreach (destination =>
            if (nodeLongestPathMap.get(destination).isEmpty || nodeLongestPathMap(topologicalOrdering(i)) >= nodeLongestPathMap(destination))
              nodeLongestPathMap += destination -> (nodeLongestPathMap(topologicalOrdering(i)) + 1))
        }
        Some(nodeLongestPathMap.valuesIterator.max)
      }
    }
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

}

object DirectedGraph {

  private final def defaultMappingPreference[T](v: T, s: Seq[(Int, Seq[T])]): Seq[Int] = s map { _._1 }

  final def defaultMetric[T](g: DirectedGraph[Int], m: Seq[Map[T, Int]]): Int = g.vertices.length

  /**
    * given a finite family of graphs G_i, this function returns the smallest graph G, s.t. all G_i are induced subgraphs of G.
    *
    * @param graphs            a finite family of graphs
    * @param mappingPreference (optionally) provide a preference, which nodes should be mapped to which other
    * @return The vertices of G are integral numbers, newly created and starting from zero
    */
  def minimalInducedSuperGraph[T](graphs: Seq[DirectedGraph[T]],
                                  metric: ((DirectedGraph[Int], Seq[Map[T, Int]]) => Int) = defaultMetric[T] _,
                                  lowerBoundOnMetric: Int = 0,
                                  mappingPreference: ((T, Seq[(Int, Seq[T])]) => Seq[Int]) = defaultMappingPreference[T] _):
  (DirectedGraph[Int], Seq[Map[T, Int]]) = if (graphs.isEmpty) (SimpleDirectedGraph(Nil, Nil), Nil)
  else {

    //println("GRAPH OPT " + (graphs map { _.vertices.length }).mkString(" "))

    var ncall = 0
    var smallestFound = Int.MaxValue

    def mappingDFS(currentGraph: DirectedGraph[Int], currentAntiGraph: DirectedGraph[Int], currentMapping: Seq[Map[T, Int]]): (Option[DirectedGraph[Int]], Seq[Map[T, Int]]) =
      if (!currentGraph.isAcyclic) {
        //println("CYC")
        (None, currentMapping)
      } else {
        ncall += 1
        //println(currentMapping map { _.size })
        //println("M " + metric(currentGraph, currentMapping))
        //println(graphs map { _.vertices.size })
        val nextGraphToMap = currentMapping.zipWithIndex zip graphs find { case ((m, _), g) => m.size != g.vertices.size }
        if (nextGraphToMap.isEmpty) {
          //if (smallestFound > currentGraph.vertices.length)
          smallestFound = metric(currentGraph, currentMapping)
          //smallestFound = 0
          //println("F " + currentGraph.vertices.length + " " + smallestFound)
          (Some(currentGraph), currentMapping)
        }
        else {
          val nextNodeToMap = (nextGraphToMap map { case ((m, _), g) => g.vertices filterNot m.contains }).get.head
          val ((thisMapping, mappingIndex), thisGraph) = nextGraphToMap.get

          val (conntectedNodes, unconntectedNodes) = thisMapping.keys.toSeq partition { ov => thisGraph.edgesSet(ov).contains(nextNodeToMap) || thisGraph.edgesSet(nextNodeToMap)
            .contains(ov)
          }

          // find all already existing nodes we can map this one possibly to
          val mappabileExistingNodes = currentGraph.vertices filter { v =>
            val dontMapTwoNodesToTheSameNode = !(thisMapping.values.toSet contains v)
            val allUnconnectedMappedNodesAreUnconnected = unconntectedNodes forall { uv =>
              val mappedUV = thisMapping(uv)
              !(currentGraph.edgesSet(mappedUV).contains(v) || currentGraph.edgesSet(v).contains(mappedUV))
            }

            val allConnectedMappedNodesAreNotUnconnected = conntectedNodes forall { uv =>
              val mappedUV = thisMapping(uv)

              val caseOutGoing = if (thisGraph.edgesSet(nextNodeToMap).contains(uv)) currentAntiGraph.edgesSet(v).contains(mappedUV) else currentGraph.edgesSet(v).contains(mappedUV)
              val caseInGoing = if (thisGraph.edgesSet(uv).contains(nextNodeToMap)) currentAntiGraph.edgesSet(mappedUV).contains(v) else currentGraph.edgesSet(mappedUV).contains(v)

              !caseInGoing && !caseOutGoing
            }

            dontMapTwoNodesToTheSameNode && allUnconnectedMappedNodesAreUnconnected && allConnectedMappedNodesAreNotUnconnected
          }

          // we can always add a new node
          val newNode = currentGraph.vertices.length

          // get selection preference
          val preferenceOrdering = mappingPreference(nextNodeToMap,
                                                     (mappabileExistingNodes map { v => (v, currentMapping flatMap { m => m collect { case (a, b) if b == v => a } }) }) :+(newNode, Nil))

          val (minGraph, minMapping) = preferenceOrdering.foldLeft[(Option[DirectedGraph[Int]], Seq[Map[T, Int]])]((None, currentMapping))(
            {
              case ((optionMinimalGraph, minimalMapping), node) =>
                val newVertices = currentGraph.vertices :+ node distinct
                val newEdges = currentGraph.edgeList ++ (conntectedNodes flatMap { cn =>
                  val before = if (thisGraph.edgesSet(cn).contains(nextNodeToMap)) (thisMapping(cn), node) :: Nil else Nil
                  val after = if (thisGraph.edgesSet(nextNodeToMap).contains(cn)) (node, thisMapping(cn)) :: Nil else Nil

                  before ++ after
                }) distinct
                val newAntiEdges = currentAntiGraph.edgeList ++ (
                  unconntectedNodes flatMap { cn =>
                    val before = if (!thisGraph.edgesSet(cn).contains(nextNodeToMap)) (thisMapping(cn), node) :: Nil else Nil
                    val after = if (!thisGraph.edgesSet(nextNodeToMap).contains(cn)) (node, thisMapping(cn)) :: Nil else Nil

                    before ++ after
                  }) distinct

                val newGraph = SimpleDirectedGraph(newVertices, newEdges)
                val newAntiGraph = SimpleDirectedGraph(newVertices, newAntiEdges)
                val newMapping = currentMapping.zipWithIndex map { case (m, i) => if (i != mappingIndex) m else thisMapping.+((nextNodeToMap, node)) }

                if (lowerBoundOnMetric == smallestFound || metric(newGraph, newMapping) >= smallestFound) (optionMinimalGraph, minimalMapping)
                else {
                  val (recursiveGraphOption, recursiveMapping) = mappingDFS(newGraph, newAntiGraph, newMapping)
                  if (recursiveGraphOption.isDefined && (
                    optionMinimalGraph.isEmpty || metric(recursiveGraphOption.get, recursiveMapping) < metric(optionMinimalGraph.get, minimalMapping)))
                    (recursiveGraphOption, recursiveMapping)
                  else (optionMinimalGraph, minimalMapping)
                }
            })
          (minGraph, minMapping)
        }

      }

    val (g, m) = mappingDFS(SimpleDirectedGraph[Int](Nil, Nil), SimpleDirectedGraph[Int](Nil, Nil), graphs map { _ => Map[T, Int]() })
    assert(g.isDefined)
    //println(ncall)
    (g.get, m)
  }
}

case class DirectedGraphWithInternalMapping[T](vertices: Seq[T], edges: Map[T, Seq[T]]) extends DirectedGraph[T] {

  // we don't want duplicats
  assert(vertices.length == vertices.distinct.length)

  private val verticesToInt: BiMap[T, Int] = BiMap(vertices.zipWithIndex)
  private val internalGraph                = SimpleDirectedGraph[Int](vertices map verticesToInt.apply, edges map { case (from, to) =>
    verticesToInt(from) -> to.map(verticesToInt.apply)
  })

  /** Only implemented for acyclic graphs. Therefore Option[Int] as return type
    * For cyclic graphs the problem becomes NP-complete instead of P for acyclic graphs
    */
  override lazy val longestPathLength: Option[Int] = internalGraph.longestPathLength

  override lazy val complementGraph: DirectedGraphWithInternalMapping[T] = DirectedGraphWithInternalMapping(vertices, edges map { case (v, ns) => v -> (vertices filterNot ns.contains) })

  /**
    * This is not a fast implementation^^
    */
  override lazy val allTotalOrderings: Option[Seq[Seq[T]]] = internalGraph.allTotalOrderings map { _ map { _ map verticesToInt.back } }

  override lazy val sources: Seq[T] = internalGraph.sources map verticesToInt.back
  override lazy val sinks  : Seq[T] = internalGraph.sinks map verticesToInt.back

  /**
    * Compute a topological ordering of the graph.
    * If the graph contains a cycle this function returns None.
    */
  override lazy val topologicalOrdering: Option[Seq[T]] = internalGraph.topologicalOrdering map { _ map verticesToInt.back }

  /** in- and out- degrees of all nodes */
  override lazy val degrees: Map[T, (Int, Int)] = internalGraph.degrees map { case (n, deg) => verticesToInt.back(n) -> deg }

  override lazy val getComponentOf: Map[T, Set[T]] = internalGraph.getComponentOf map { case (n, comp) => verticesToInt.back(n) -> comp.map(verticesToInt.back) }

  override def getVerticesInDistance(v: T, distance: Int): Set[T] = internalGraph.getVerticesInDistance(verticesToInt(v), distance) map verticesToInt.back

  /** list of all edges as a list of pairs */
  override lazy val edgeList: Seq[(T, T)] = internalGraph.edgeList map { case (a, b) => (verticesToInt.back(a), verticesToInt.back(b)) }

  override def reachableFrom(root: T): Set[T] = internalGraph.reachableFrom(verticesToInt(root)) map verticesToInt.back

  override lazy val stronglyConnectedComponents: Seq[Set[T]] = internalGraph.stronglyConnectedComponents map { _ map verticesToInt.back }

  /** computes for each node, which other nodes can be reached from it using the edges of the graph */
  override lazy val reachable: Map[T, Set[T]] = internalGraph.reachable map { case (n, reach) => verticesToInt.back(n) -> reach.map(verticesToInt.back) }
}

object DirectedGraphWithInternalMapping {
  def apply[T](nodes: Seq[T], edges: Seq[(T, T)]): DirectedGraphWithInternalMapping[T] = {
    edges flatMap { case (a, b) => a :: b :: Nil } foreach { n => assert(nodes contains n) }
    DirectedGraphWithInternalMapping(nodes, (nodes zip (nodes map { n => edges.filter({ _._1 == n }).map({ _._2 }) })).toMap)
  }
}

case class SimpleDirectedGraph[T](vertices: Seq[T], edges: Map[T, Seq[T]]) extends DirectedGraphWithAlgorithms[T] {
  override def equals(o: scala.Any): Boolean = o match {
    case g: SimpleDirectedGraph[T] => vertices.toSet == g.vertices.toSet && edgeList.toSet == g.edgeList.toSet
    case _                         => false
  }
}

object SimpleDirectedGraph {
  def apply[T](nodes: Seq[T], edges: Seq[(T, T)]): SimpleDirectedGraph[T] = {
    //edges flatMap { case (a, b) => a :: b :: Nil } foreach { n => assert(nodes contains n) }
    //println("SDG " + nodes.length + " " + edges.length)
    val baseEdgeMap: Map[T, Seq[T]] = edges.groupBy(_._1) map { case (a, b) => a -> (b map { _._2 }) }
    val edgeMap: Map[T, Seq[T]] = nodes map { n => n -> baseEdgeMap.getOrElse(n, Nil) } toMap

    //SimpleDirectedGraph(nodes, (nodes zip (nodes map { n => edges.filter({ _._1 == n }).map({ _._2 }) })).toMap)
    SimpleDirectedGraph(nodes, edgeMap)
  }
}

case class SimpleGraphNode(id: String, name: String) extends PrettyPrintable {
  override def shortInfo: String = name

  override def mediumInfo: String = name

  override def longInfo: String = name
}

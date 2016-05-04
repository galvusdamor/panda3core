package de.uniulm.ki.util

/**
  * And Vertices are those that occur in and rules, i.e., all their successors are or
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait AndOrGraph[T, A <: T, O <: T] extends DirectedGraph[T] {
  val andVertices: Set[A]
  val orVertices : Set[O]

  val andEdges: Map[A, Set[O]]
  val orEdges : Map[O, Set[A]]

  /** a list of all node of the graph */
  lazy val vertices: Seq[T] = (andVertices ++ orVertices).toSeq

  /** adjacency list of the graph */
  lazy val edges: Map[T, Seq[T]] = {
    val edg = (andEdges ++ orEdges) map { case (a, b) => (a, b.toSeq) }

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
}


case class SimpleAndOrGraph[T, A <: T, O <: T](andVertices: Set[A], orVertices: Set[O], andEdges: Map[A, Set[O]], orEdges: Map[O, Set[A]]) extends AndOrGraph[T, A, O] {
  andEdges foreach { a => assert(andVertices contains a._1) }
  orEdges foreach { o => assert(orVertices contains o._1) }
  andEdges foreach { _._2 foreach { o => assert(orVertices contains o) } }
  orEdges foreach { _._2 foreach { a => assert(andVertices contains a) } }
  assert(andVertices.size == andEdges.size)
  assert(orVertices.size == orEdges.size)
}
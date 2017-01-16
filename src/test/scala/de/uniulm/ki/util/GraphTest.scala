package de.uniulm.ki.util

import org.scalatest.FlatSpec

/**
  * teststhe Graph implementation
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class GraphTest extends FlatSpec {

  val edges = (0, 1) ::(1, 2) ::(2, 0) ::(2, 3) ::(3, 4) ::(4, 5) ::(5, 3) :: Nil
  val g     = SimpleDirectedGraph(0 until 6, edges)
  val g2    = SimpleDirectedGraph(0 until 9, (6, 0) ::(3, 7) ::(7, 8) :: edges)
  val g3    = SimpleDirectedGraph(0 until 9, (6, 0) ::(3, 7) ::(7, 8) ::(3, 7) :: edges)
  val g4    = SimpleDirectedGraph(0 until 8, (0, 1) ::(1, 2) ::(2, 4) ::(4, 5) ::(5, 6) ::(5, 7) ::(1, 3) ::(3, 4) :: Nil)

  "Graphs" must "have the correct SCCs" in {
    val sccs = g.stronglyConnectedComponents

    assert(sccs.size == 2)
    assert(sccs forall { _.size == 3 })
    assert(sccs exists { _ forall { n => n == 0 || n == 1 || n == 2 } })
    assert(sccs exists { _ forall { n => n == 3 || n == 4 || n == 5 } })

    assert(g.getComponentOf(0) == Set(0, 1, 2))
    assert(g.getComponentOf(1) == Set(0, 1, 2))
    assert(g.getComponentOf(2) == Set(0, 1, 2))
    assert(g.getComponentOf(3) == Set(3, 4, 5))
    assert(g.getComponentOf(4) == Set(3, 4, 5))
    assert(g.getComponentOf(5) == Set(3, 4, 5))
  }

  it must "have the correct list of edges" in {
    assert(g.edgeList.size == edges.size)
    g.edgeList foreach { e => assert(edges contains e) }
  }

  "Degrees" must "be computed correctly" in {
    assert(g.degrees(0) ==(1, 1))
    assert(g.degrees(1) ==(1, 1))
    assert(g.degrees(2) ==(1, 2))
    assert(g.degrees(3) ==(2, 1))
    assert(g.degrees(4) ==(1, 1))
    assert(g.degrees(5) ==(1, 1))
  }

  val condens = g.condensation

  "Condensation" must "be correct" in {

    assert(condens.vertices.size == 2)
    assert(condens.vertices exists { _.toSet == Set(0, 1, 2) })
    assert(condens.vertices exists { _.toSet == Set(3, 4, 5) })
    assert(condens.edgeList.size == 1)
    assert(condens.edgeList exists { case (from, to) => from.toSet == Set(0, 1, 2) && to.toSet == Set(3, 4, 5) })
  }

  "Sources" must "be computed correctly" in {
    assert(g.sources.isEmpty)

    assert(condens.sources.size == 1)
    assert(condens.sources exists { _.toSet == Set(0, 1, 2) })
  }

  "Reachability" must "be computed correctly" in {
    val reachabilityMap = g2.reachable
    assert(reachabilityMap.size == 9)

    for (v <- (0 until 3) :+ 6) {
      assert(reachabilityMap(v).size == 8)
      assert((0 until 6) :+ 7 :+ 8 forall { reachabilityMap(v).contains(_) })
    }

    for (v <- 3 until 6) {
      assert(reachabilityMap(v).size == 5)
      assert((3 until 6) :+ 7 :+ 8 forall { reachabilityMap(v).contains(_) })
    }

    // the outside nodes
    assert(reachabilityMap(7).size == 1)
    assert(reachabilityMap(7) contains 8)
    assert(reachabilityMap(8).isEmpty)
  }

  it must "not contain duplicats" in {
    val reachabilityMap = g3.reachable

    assert(reachabilityMap.size == 9)

    assert(reachabilityMap(6).size == 8)
    assert((0 until 6) :+ 7 :+ 8 forall reachabilityMap(6).contains)

    for (v <- 0 until 9) assert(reachabilityMap(v).size == reachabilityMap(v).toSet.size)
  }

  "topological orderings" must "not be computable for graphs with circles" in {
    val gOrdering = g.topologicalOrdering
    val g2Ordering = g2.topologicalOrdering
    val g3Ordering = g3.topologicalOrdering

    assert(gOrdering.isEmpty)
    assert(g2Ordering.isEmpty)
    assert(g3Ordering.isEmpty)
  }

  it must "be correct if one exists" in {
    val g4OrderingOpt = g4.topologicalOrdering
    assert(g4OrderingOpt.isDefined)
    val g4Ordering = g4OrderingOpt.get

    for ((v1, v2) <- g4.edgeList) assert(g4Ordering.indexOf(v1) < g4Ordering.indexOf(v2))
  }

  val g5 = SimpleDirectedGraph(0 until 4, (0,1) :: (1,2) :: (1,3) :: (0,2) :: (0,3):: Nil)
  val g6 = SimpleDirectedGraph(0 until 4, (0,1) :: (0,2) :: (0,3) :: (1,2) :: (1,3) :: (2,3) :: Nil)
  val g7 = SimpleDirectedGraph(0 until 4, (0,1) :: (0,2) :: (0,3) :: Nil)
  val g8 = SimpleDirectedGraph(0 until 5, Nil)
  val g9 = SimpleDirectedGraph(0 until 5, Nil)


  val gp1 = SimpleDirectedGraph(0 until 4, (0,1) :: (0,2) :: (0,3) :: (1,2) :: (1,3) :: (2,3) :: Nil)
  val gp2 = SimpleDirectedGraph(0 until 4, (0,1) :: (0,2) :: (0,3) :: (1,2) :: (1,3) :: (2,3) :: Nil)
  val gp3 = SimpleDirectedGraph(0 until 5, (0,1) :: (0,2) :: (0,3) :: (0,4) :: (1,2) :: (1,3) :: (1,4) :: (2,3) :: (2,4) :: (3,4) :: Nil)
  val gp4 = SimpleDirectedGraph(0 until 5, (0,1) :: (0,2) :: (0,3) :: (0,4) :: (1,2) :: (1,3) :: (1,4) :: (2,3) :: (2,4) :: (3,4) :: Nil)
  val gp5 = SimpleDirectedGraph(0 until 5, (0,1) :: (0,2) :: (0,3) :: (0,4) :: (1,2) :: (1,3) :: (1,4) :: (2,3) :: (2,4) :: (3,4) :: Nil)
  val gp6 = SimpleDirectedGraph(0 until 5, (0,1) :: (0,2) :: (0,3) :: (0,4) :: (1,2) :: (1,3) :: (1,4) :: (2,3) :: (2,4) :: (3,4) :: Nil)

  "Minimal Supergraph" must "be computed correctly" in {
    //val (miniSuper, miniMap) = DirectedGraph.minimalInducedSuperGraph(g5 :: g6 :: g7 :: g8 /* :: g9 */:: Nil)
    val (miniSuper, miniMap) = DirectedGraph.minimalInducedSuperGraph(gp1 :: gp2 :: gp3 :: gp4 :: gp5 :: gp6 :: Nil)
    println("Run Dot2PDF")
    Dot2PdfCompiler.writeDotToFile(miniSuper, "foo.pdf")
    //println(miniSuper)
    println(miniMap)
  }
}
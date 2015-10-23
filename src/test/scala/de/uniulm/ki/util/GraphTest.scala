package de.uniulm.ki.util

import org.scalatest.FlatSpec

/**
 * teststhe Graph implementation
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class GraphTest extends FlatSpec {

  val edges = (0, 1) ::(1, 2) ::(2, 0) ::(2, 3) ::(3, 4) ::(4, 5) ::(5, 3) :: Nil
  val g = SimpleDirectedGraph(0 until 6, edges)
  val g2 = SimpleDirectedGraph(0 until 9, (6, 0) ::(3, 7) ::(7, 8) :: edges)
  val g3 = SimpleDirectedGraph(0 until 9, (6, 0) ::(3, 7) ::(7, 8) ::(3, 7) :: edges)
  val g4 = SimpleDirectedGraph(0 until 8, (0, 1) ::(1, 2) ::(2, 4) ::(4, 5) ::(5, 6) ::(5, 7) ::(1, 3) ::(3, 4) :: Nil)

  "Graphs" must "have the correct SCCs" in {
    val sccs = g.stronglyConnectedComponents

    assert(sccs.size == 2)
    assert(sccs forall {_.size == 3})
    assert(sccs exists {_ forall { n => n == 0 || n == 1 || n == 2 }})
    assert(sccs exists {_ forall { n => n == 3 || n == 4 || n == 5 }})

    assert(g.getComponentOf(0) match { case Some(l) => l.toSet == Set(0, 1, 2); case None => false })
    assert(g.getComponentOf(1) match { case Some(l) => l.toSet == Set(0, 1, 2); case None => false })
    assert(g.getComponentOf(2) match { case Some(l) => l.toSet == Set(0, 1, 2); case None => false })
    assert(g.getComponentOf(3) match { case Some(l) => l.toSet == Set(3, 4, 5); case None => false })
    assert(g.getComponentOf(4) match { case Some(l) => l.toSet == Set(3, 4, 5); case None => false })
    assert(g.getComponentOf(5) match { case Some(l) => l.toSet == Set(3, 4, 5); case None => false })
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
    assert(condens.vertices exists {_.toSet == Set(0, 1, 2)})
    assert(condens.vertices exists {_.toSet == Set(3, 4, 5)})
    assert(condens.edgeList.size == 1)
    assert(condens.edgeList exists { case (from, to) => from.toSet == Set(0, 1, 2) && to.toSet == Set(3, 4, 5) })
  }

  "Sources" must "be computed correctly" in {
    assert(g.sources.size == 0)

    assert(condens.sources.size == 1)
    assert(condens.sources exists {_.toSet == Set(0, 1, 2)})
  }

  "Reachability" must "be computed correctly" in {
    val reachabilityMap = g2.reachable
    assert(reachabilityMap.size == 9)

    for (v <- (0 until 3) :+ 6) {
      assert(reachabilityMap(v).size == 8)
      assert((0 until 6) :+ 7 :+ 8 forall {reachabilityMap(v).contains(_)})
    }

    for (v <- 3 until 6) {
      assert(reachabilityMap(v).size == 5)
      assert((3 until 6) :+ 7 :+ 8 forall {reachabilityMap(v).contains(_)})
    }

    // the outside nodes
    assert(reachabilityMap(7).size == 1)
    assert(reachabilityMap(7) contains 8)
    assert(reachabilityMap(8).size == 0)
  }

  it must "not contain duplicats" in {
    val reachabilityMap = g3.reachable

    assert(reachabilityMap.size == 9)

    assert(reachabilityMap(6).size == 8)
    assert((0 until 6) :+ 7 :+ 8 forall {reachabilityMap(6).contains(_)})

    for (v <- 0 until 9) assert(reachabilityMap(v).size == reachabilityMap(v).toSet.size)
  }

  "topological orderings" must "not be computable for graphs with circles" in {
    val gOrdering = g.topologicalOrdering
    val g2Ordering = g2.topologicalOrdering
    val g3Ordering = g3.topologicalOrdering

    assert(gOrdering == None)
    assert(g2Ordering == None)
    assert(g3Ordering == None)
  }

  it must "be correct if one exists" in {
    val g4OrderingOpt = g4.topologicalOrdering
    assert(g4OrderingOpt.isDefined)
    val g4Ordering = g4OrderingOpt.get

    for ((v1, v2) <- g4.edgeList) assert(g4Ordering.indexOf(v1) < g4Ordering.indexOf(v2))
  }
}
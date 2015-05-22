package de.uniulm.ki.util

import org.scalatest.FlatSpec

/**
 * teststhe Graph implementation
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class GraphTest extends FlatSpec {

  val edges = (0, 1) ::(1, 2) ::(2, 0) ::(2, 3) ::(3, 4) ::(4, 5) ::(5, 3) :: Nil
  val g     = SimpleGraph(0 until 6, edges)

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

  "Condensation" must "be correct" in {
    val condens = g.condensation

    assert(condens.nodes.size == 2)
    assert(condens.nodes exists {_.toSet == Set(0, 1, 2)})
    assert(condens.nodes exists {_.toSet == Set(3, 4, 5)})
    assert(condens.edgeList.size == 1)
    assert(condens.edgeList exists { case (from, to) => from.toSet == Set(0, 1, 2) && to.toSet == Set(3, 4, 5) })
  }
}
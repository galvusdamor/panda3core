package de.uniulm.ki.util

import org.scalatest.FlatSpec

/**
 * teststhe Graph implementation
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class GraphTest extends FlatSpec {

  "Graphs" must "have the correct SCCs" in {
    val g = SimpleGraph(0 until 6, (0, 1) ::(1, 2) ::(2, 0) ::(2, 3) ::(3, 4) ::(4, 5) ::(5, 3) :: Nil)

    val sccs = g.stronglyConnectedComponents

    assert(sccs.size == 2)
    assert(sccs forall {_.size == 3})
    assert(sccs exists {_ forall { n => n == 0 || n == 1 || n == 2 }})
    assert(sccs exists {_ forall { n => n == 3 || n == 4 || n == 5 }})
  }
}
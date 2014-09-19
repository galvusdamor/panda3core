package de.uniulm.ki.panda3.plan.ordering

import de.uniulm.ki.panda3.csp.Variable
import de.uniulm.ki.panda3.domain.Task
import de.uniulm.ki.panda3.logic.{Literal, Sort}
import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}
import org.scalatest.FlatSpec


/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicOrderingTest extends FlatSpec {


  def getPlanStep(i: Int): PlanStep = new PlanStep {
    override val arguments: IndexedSeq[Variable] = Vector()
    override val schema: Task = new Task {
      override val parameterTypes: IndexedSeq[Sort] = Vector()
      override val isPrimitive: Boolean = false
      override val preconditions: IndexedSeq[Literal] = Vector()
      override val name: String = ""
      override val effects: IndexedSeq[Literal] = Vector()
    }
    override val id: Int = i
  }

  def getOrdering(i: Int, j: Int): OrderingConstraint = OrderingConstraint(getPlanStep(i), getPlanStep(j))

  "Orderings inference" must "allow simple inference" in {
    // a dummy plan
    val order = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2), 3)

    assert(order.isConsistent)
    assert(order.lteq(getPlanStep(0), getPlanStep(2)))
  }



  it must "allow almost simple inference" in {
    // a dummy plan
    val order = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2) :+ getOrdering(2, 3) :+ getOrdering(3, 4) :+ getOrdering(3, 5) :+ getOrdering(6, 2) :+ getOrdering(2, 7) :+ getOrdering(7, 3),
      8)

    assert(order.isConsistent)
    assert(order.lteq(getPlanStep(0), getPlanStep(2)))
    assert(order.lteq(getPlanStep(0), getPlanStep(3)))
    assert(order.lteq(getPlanStep(0), getPlanStep(4)))
    assert(order.lteq(getPlanStep(0), getPlanStep(5)))
    assert(order.lteq(getPlanStep(6), getPlanStep(4)))
    assert(order.lteq(getPlanStep(6), getPlanStep(7)))
    assert(order.tryCompare(getPlanStep(6), getPlanStep(1)) === None)
    assert(order.tryCompare(getPlanStep(5), getPlanStep(7)) === Some(1))
    assert(order.tryCompare(getPlanStep(6), getPlanStep(4)) === Some(-1))
  }



  "Orderings update" must "allow incremntal calculations" in {
    // a dummy plan
    val order1 = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2), 3)

    assert(order1.isConsistent)
    assert(order1.lteq(getPlanStep(0), getPlanStep(2)))

    val order2 = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2) :+ getOrdering(2, 3) :+ getOrdering(3, 4) :+ getOrdering(3, 5) :+ getOrdering(6, 2) :+ getOrdering(2, 7), 8)
    order2.initialiseExplicitly(5, 5, order1.arrangement())

    assert(order2.isConsistent)
    assert(order2.lteq(getPlanStep(0), getPlanStep(2)))
    assert(order2.lteq(getPlanStep(0), getPlanStep(3)))
    assert(order2.lteq(getPlanStep(0), getPlanStep(4)))
    assert(order2.lteq(getPlanStep(0), getPlanStep(5)))
    assert(order2.lteq(getPlanStep(6), getPlanStep(4)))
    assert(order2.lteq(getPlanStep(6), getPlanStep(7)))
    assert(order2.tryCompare(getPlanStep(6), getPlanStep(1)) === None)
    assert(order2.tryCompare(getPlanStep(5), getPlanStep(7)) === None)
    assert(order2.tryCompare(getPlanStep(6), getPlanStep(4)) === Some(-1))

    val order3 = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2) :+ getOrdering(2, 3) :+ getOrdering(3, 4) :+ getOrdering(3, 5) :+ getOrdering(6, 2) :+ getOrdering(2, 7) :+ getOrdering(7, 3),
      8)
    order3.initialiseExplicitly(1, 0, order2.arrangement())

    assert(order3.isConsistent)
    assert(order3.lteq(getPlanStep(0), getPlanStep(2)))
    assert(order3.lteq(getPlanStep(0), getPlanStep(3)))
    assert(order3.lteq(getPlanStep(0), getPlanStep(4)))
    assert(order3.lteq(getPlanStep(0), getPlanStep(5)))
    assert(order3.lteq(getPlanStep(6), getPlanStep(4)))
    assert(order3.lteq(getPlanStep(6), getPlanStep(7)))
    assert(order3.tryCompare(getPlanStep(6), getPlanStep(1)) === None)
    assert(order3.tryCompare(getPlanStep(5), getPlanStep(7)) === Some(1))
    assert(order3.tryCompare(getPlanStep(6), getPlanStep(4)) === Some(-1))

  }


  it must "allow add Ordering" in {
    // a dummy plan
    val order1 = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2), 3)

    assert(order1.isConsistent)
    assert(order1.lteq(getPlanStep(0), getPlanStep(2)))

    val order2 = order1.addOrdering(getPlanStep(0), getPlanStep(1)).addOrdering(getPlanStep(1), getPlanStep(2)).
      addOrdering(getPlanStep(2), getPlanStep(3)).addOrdering(getPlanStep(3), getPlanStep(4)).
      addOrdering(getPlanStep(3), getPlanStep(5)).addOrdering(getPlanStep(6), getPlanStep(2)).addOrdering(getPlanStep(2), getPlanStep(7))

    order2.initialiseExplicitly(5, 5, order1.arrangement())

    assert(order2.isConsistent)
    assert(order2.lteq(getPlanStep(0), getPlanStep(2)))
    assert(order2.lteq(getPlanStep(0), getPlanStep(3)))
    assert(order2.lteq(getPlanStep(0), getPlanStep(4)))
    assert(order2.lteq(getPlanStep(0), getPlanStep(5)))
    assert(order2.lteq(getPlanStep(6), getPlanStep(4)))
    assert(order2.lteq(getPlanStep(6), getPlanStep(7)))
    assert(order2.tryCompare(getPlanStep(6), getPlanStep(1)) === None)
    assert(order2.tryCompare(getPlanStep(5), getPlanStep(7)) === None)
    assert(order2.tryCompare(getPlanStep(6), getPlanStep(4)) === Some(-1))

    val order3 = order2.addOrdering(getPlanStep(7), getPlanStep(3))

    order3.initialiseExplicitly(1, 0, order2.arrangement())

    assert(order3.isConsistent)
    assert(order3.lteq(getPlanStep(0), getPlanStep(2)))
    assert(order3.lteq(getPlanStep(0), getPlanStep(3)))
    assert(order3.lteq(getPlanStep(0), getPlanStep(4)))
    assert(order3.lteq(getPlanStep(0), getPlanStep(5)))
    assert(order3.lteq(getPlanStep(6), getPlanStep(4)))
    assert(order3.lteq(getPlanStep(6), getPlanStep(7)))
    assert(order3.tryCompare(getPlanStep(6), getPlanStep(1)) === None)
    assert(order3.tryCompare(getPlanStep(5), getPlanStep(7)) === Some(1))
    assert(order3.tryCompare(getPlanStep(6), getPlanStep(4)) === Some(-1))

  }


  "Orderings inconsistencies" must "find simple inconsistencies" in {
    // a dummy plan
    val order = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2) :+ getOrdering(2, 0), 3)

    assert(!order.isConsistent)
  }

  it must "find tricky inconsistencies" in {
    // a dummy plan
    val order = new SymbolicTaskOrdering(Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 0), 2)

    assert(!order.isConsistent)
  }

  it must "find complex inconsistencies" in {
    // a dummy plan
    val order = new SymbolicTaskOrdering(
      Vector() :+ getOrdering(0, 1) :+ getOrdering(1, 2) :+ getOrdering(2, 3) :+ getOrdering(3, 4) :+ getOrdering(3, 5) :+ getOrdering(6, 2) :+ getOrdering(2, 7) :+ getOrdering(7, 3) :+ getOrdering(5, 1), 8)

    assert(!order.isConsistent)
  }


}

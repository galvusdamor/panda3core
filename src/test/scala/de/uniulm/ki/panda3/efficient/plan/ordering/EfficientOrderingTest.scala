package de.uniulm.ki.panda3.efficient.plan.ordering

import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class EfficientOrderingTest extends FlatSpec {

  "Ordering inference" must "allow trivial inference" in {
    val ordering = new EfficientOrdering().addPlanSteps(3)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(0, 1)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(1, 2)
    assert(ordering.isConsistent)

    for (i <- Range(0, 3)) assert(ordering.equiv(i, i))
    assert(ordering.lt(0, 2))
    assert(ordering.lt(0, 1))
    assert(ordering.lt(1, 2))
  }

  it must "allow almost trivial inference" in {
    val ordering = new EfficientOrdering().addPlanSteps(9)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(0, 1)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(1, 2)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(2, 3)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(3, 4)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(3, 5)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(6, 2)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(2, 7)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(7, 3)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(2, 8)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(8, 3)
    assert(ordering.isConsistent)


    assert(ordering.lteq(0, 2))
    assert(ordering.lteq(0, 3))
    assert(ordering.lteq(0, 4))
    assert(ordering.lteq(0, 5))
    assert(ordering.lteq(6, 4))
    assert(ordering.lteq(6, 7))
    assert(!ordering.lteq(4, 5))
    assert(!ordering.lteq(5, 1))
    assert(ordering.tryCompare(6, 1) === None)
    assert(ordering.tryCompare(5, 7) === Some(1))
    assert(ordering.tryCompare(6, 4) === Some(-1))
    assert(ordering.tryCompare(7, 8) === None)
  }

  "Orderings inconsistencies" must "find simple inconsistencies" in {
    // a dummy plan
    val order = new EfficientOrdering().addPlanSteps(3)
    order.addOrderingConstraint(0, 1)
    order.addOrderingConstraint(1, 2)
    order.addOrderingConstraint(2, 0)

    assert(!order.isConsistent)
  }

  it must "find tricky inconsistencies" in {
    // a dummy plan
    val order = new EfficientOrdering().addPlanSteps(2)
    order.addOrderingConstraint(0, 1)
    order.addOrderingConstraint(1, 0)

    assert(!order.isConsistent)
  }

  it must "find complex inconsistencies" in {
    val ordering = new EfficientOrdering().addPlanSteps(8)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(0, 1)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(1, 2)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(2, 3)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(3, 4)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(3, 5)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(6, 2)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(2, 7)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(7, 3)
    assert(ordering.isConsistent)
    ordering.addOrderingConstraint(5, 1)

    assert(!ordering.isConsistent)
  }

  "Removing plan steps" must "lead to a correct reamining order" in {
    val ordering = new EfficientOrdering().addPlanSteps(5)
    ordering.addOrderingConstraint(0, 1)
    ordering.addOrderingConstraint(1, 2)
    ordering.addOrderingConstraint(2, 3)
    ordering.addOrderingConstraint(0, 4)
    ordering.addOrderingConstraint(4, 3)
    assert(ordering.isConsistent)

    // test whether the ordering looks like we expect it
    assert(ordering.lt(0, 1))
    assert(ordering.lt(0, 2))
    assert(ordering.lt(0, 3))
    assert(ordering.lt(1, 2))
    assert(ordering.lt(1, 3))
    assert(ordering.lt(2, 3))

    assert(ordering.lt(0, 4))
    assert(ordering.lt(4, 3))
    assert(ordering.tryCompare(1, 4) === None)
    assert(ordering.tryCompare(2, 4) === None)

    // remove 2
    val removedOrdering = ordering.removePlanStep(2)
    assert(removedOrdering.isConsistent)
    // test whether the ordering looks like we expect it
    assert(removedOrdering.lt(0, 1))
    assert(removedOrdering.lt(0, 2))
    assert(removedOrdering.lt(1, 2))

    assert(removedOrdering.lt(0, 3))
    assert(removedOrdering.lt(3, 2))
    assert(removedOrdering.tryCompare(1, 3) === None)
  }

  "Adding a subordering replacing a single task" must "be correct" in {
    val mainOrdering = new EfficientOrdering().addPlanSteps(5)
    mainOrdering.addOrderingConstraint(0, 1)
    mainOrdering.addOrderingConstraint(1, 2)
    mainOrdering.addOrderingConstraint(0, 3)
    mainOrdering.addOrderingConstraint(3, 2)
    mainOrdering.addOrderingConstraint(2, 4)
    assert(mainOrdering.isConsistent)
    assert(mainOrdering.lt(0, 2))
    assert(mainOrdering.lt(0, 4))
    assert(mainOrdering.lt(1, 4))
    assert(mainOrdering.lt(3, 4))

    val subOrdering = new EfficientOrdering().addPlanSteps(5)
    subOrdering.addOrderingConstraint(0, 2)
    subOrdering.addOrderingConstraint(1, 2)
    subOrdering.addOrderingConstraint(2, 3)

    // replace 1 in the main ordering with the subordering and let 2 be the representative
    val replaced = mainOrdering.addPlanStepsFromBase(1, 5, subOrdering.orderingConstraints)
    assert(replaced.isConsistent)
    assert(replaced.orderingConstraints.length == 10)

    // direct relations
    assert(replaced.lt(0, 5))
    assert(replaced.lt(0, 6))
    assert(replaced.lt(0, 9))
    assert(replaced.lt(5, 7))
    assert(replaced.lt(6, 7))
    assert(replaced.lt(7, 8))
    assert(replaced.lt(8, 2))
    assert(replaced.lt(9, 2))
    assert(replaced.lt(0, 3))
    assert(replaced.lt(0, 2))

    // dome implied ones
    assert(replaced.lt(5, 4))
    assert(replaced.lt(6, 4))
    assert(replaced.lt(9, 4))
    assert(replaced.lt(7, 4))
    assert(replaced.lt(8, 4))

    // uncomparable external
    assert(replaced.tryCompare(7, 3) === None)
    assert(replaced.tryCompare(5, 3) === None)
    assert(replaced.tryCompare(6, 3) === None)
    assert(replaced.tryCompare(9, 3) === None)
    assert(replaced.tryCompare(8, 3) === None)
    // uncomparable internal
    assert(replaced.tryCompare(9, 5) === None)
    assert(replaced.tryCompare(9, 6) === None)
    assert(replaced.tryCompare(9, 7) === None)
    assert(replaced.tryCompare(9, 8) === None)
  }

  "Derived test" must "be correct " in {
    val ordering = new EfficientOrdering().addPlanSteps(4)
    ordering.addOrderingConstraint(0, 2)
    ordering.addOrderingConstraint(0, 3)
    ordering.addOrderingConstraint(3, 1)
    ordering.addOrderingConstraint(2, 1)

    assert(!ordering.gt(2, 3))
    assert(!ordering.lt(2, 3))
    assert(!ordering.gt(3, 2))
    assert(!ordering.lt(3, 2))
  }
}





















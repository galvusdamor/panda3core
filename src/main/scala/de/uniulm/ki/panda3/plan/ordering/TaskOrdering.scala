package de.uniulm.ki.panda3.plan.ordering

import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}

/**
 * A Trait representing the ordering of a set of plan steps
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait TaskOrdering extends PartialOrdering[PlanStep] {

  def originalOrderingConstraints(): IndexedSeq[OrderingConstraint]
}

package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.csp.Variable
import de.uniulm.ki.panda3.domain.Task

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait PlanStep {
  // the unique id of this planstep inside a plan
  val id: Int
  val schema: Task
  val arguments: IndexedSeq[Variable]
}

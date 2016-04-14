package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class NotInsertedByDecomposition(plan: Plan, planStep: PlanStep) extends Flaw {

  override def computeAllResolvents(domain: Domain): Seq[Modification] = ???

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "NotInsertedByDecomposition: " + planStep.shortInfo
}

package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientOpenPrecondition, EfficientAbstractPlanStep}
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering

import scala.collection.mutable.ArrayBuffer

/**
  * This is the efficient representation of a plan. Its implementation uses the following assumptions:
  * - its plansteps are numbered 0..sz(planStepTasks)-1 and have the type denoted by the entry in that array
  * - the ith subarray of planStepParameters contains the parameters of the ith task
  * - similar to the CSP and to literals, constants are stored in their negative representation (see [[de.uniulm.ki.panda3.efficient.switchConstant]])
  * - init and goal are assumed to be the plan steps indexed 0 and 1 respectively
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class EfficientPlan(domain: EfficientDomain, planStepTasks: Array[Int], planStepParameters: Array[Array[Int]], variableConstraints: EfficientCSP, ordering: EfficientOrdering,
                    causalLinks: Array[EfficientCausalLink]) {


}
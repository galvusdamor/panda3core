package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class EfficientPlan(planSteps: Array[Int], planStepParameters: Array[Array[Int]], variableConstraints: EfficientCSP, ordering: EfficientOrdering) {

}

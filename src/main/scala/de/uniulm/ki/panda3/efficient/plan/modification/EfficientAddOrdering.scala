package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientAddOrdering(plan: EfficientPlan, before: Int, after: Int) extends EfficientModification {
  override lazy val nonInducedAddedOrderings: Array[(Int, Int)] = Array((before,after))
}

object EfficientAddOrdering {
  def apply(plan: EfficientPlan, causalLink: EfficientCausalLink, threater: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()
    if (!plan.ordering.lt(threater, causalLink.consumer)) buffer append EfficientAddOrdering(plan,causalLink.consumer, threater)
    if (!plan.ordering.lt(causalLink.producer, threater)) buffer append EfficientAddOrdering(plan,threater, causalLink.producer)
    buffer.toArray
  }
}
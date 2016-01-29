package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientFlaw {
  /**
    * The plan in which this flaw is valid
    */
  val plan : EfficientPlan
}

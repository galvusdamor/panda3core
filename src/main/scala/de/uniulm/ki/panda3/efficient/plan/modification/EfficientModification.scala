package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink

import scala.collection.mutable.ArrayBuffer


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientModification {
  val plan: EfficientPlan
  val addedVariableConstraints: Array[EfficientVariableConstraint] = Array()
  val addedCausalLinks        : Array[EfficientCausalLink]         = Array()
  val addedPlanSteps          : Array[(Int, Array[Int], Int, Int)] = Array()
  val addedVariableSorts      : Array[Int]                         = Array()
  final val addedOrderings: Array[(Int, Int)] = {
    val buffer = new ArrayBuffer[(Int, Int)]()
    buffer appendAll nonInducedAddedOrderings

    var i = 0
    while (i < addedCausalLinks.length) {
      if (plan.taskOfPlanStep(addedCausalLinks(i).producer).isPrimitive && plan.taskOfPlanStep(addedCausalLinks(i).consumer).isPrimitive)
        buffer.append((addedCausalLinks(i).producer, addedCausalLinks(i).consumer))
      i += 1
    }

    buffer.toArray
  }


  // here we compute all other necessary orderings, like the ones inherited from causal links
  // the orderings to init and goal will be added by the plan itself
  // this _MUST_ be lazy!!
  protected lazy val nonInducedAddedOrderings: Array[(Int, Int)] = Array()


  // TODO: handle inserting orderings from causal links
}
package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientInsertCausalLink(plan: EfficientPlan, causalLink: EfficientCausalLink, necessaryVariableConstraints: Array[EfficientVariableConstraint]) extends EfficientModification {

  override val addedCausalLinks        : Array[EfficientCausalLink]         = Array(causalLink)
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = necessaryVariableConstraints
}

object EfficientInsertCausalLink {
  def apply(plan: EfficientPlan, consumer: Int, consumerIndex: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()

    val consumerTask = plan.domain.tasks(plan.planStepTasks(consumer))
    val consumerLiteral = consumerTask.precondition(consumerIndex)
    val consumerParameters = consumerTask.getArgumentsOfLiteral(plan.planStepParameters(consumer),consumerLiteral)


    // iterate through all plansteps in the plan to find possible producers
    var producer = 0
    while (producer < plan.planStepTasks.length) {
      if (producer != consumer && plan.planStepDecomposedByMethod(producer) != -1 && !plan.ordering.gt(producer, consumer)) {
        val producerTask = plan.domain.tasks(plan.planStepTasks(producer))
        // and loop through all of their effects
        var producerIndex = 0
        while (producerIndex < producerTask.effect.length){
          val producerLiteral = producerTask.effect(producerIndex)
          // check whether it is the same predicate and has the same arity
          if (consumerLiteral.predicate == producerLiteral.predicate && consumerLiteral.isPositive == producerLiteral.isPositive){
            // check whether they can be unified
            val producerParameters = producerTask.getArgumentsOfLiteral(plan.planStepParameters(producer),producerLiteral)
            val mgu = plan.variableConstraints.computeMGU(consumerParameters,producerParameters)
            if (mgu.isDefined)
              buffer append EfficientInsertCausalLink(plan,EfficientCausalLink(producer,consumer,producerIndex,consumerIndex),mgu.get)
          }
          producerIndex += 1
        }
      }
      producer += 1
    }
    buffer.toArray
  }
}

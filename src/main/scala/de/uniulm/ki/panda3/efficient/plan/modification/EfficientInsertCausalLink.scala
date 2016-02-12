package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientInsertCausalLink(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, causalLink: EfficientCausalLink, necessaryVariableConstraints: Array[EfficientVariableConstraint])
  extends EfficientModification {

  override lazy val addedCausalLinks        : Array[EfficientCausalLink]         = Array(causalLink)
  override      val addedVariableConstraints: Array[EfficientVariableConstraint] = necessaryVariableConstraints
}

object EfficientInsertCausalLink {

  private def addModificationFromPlanStep(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int, producer: Int, consumerLiteral: EfficientLiteral,
                                          consumerParameters: Array[Int],
                                          buffer: ArrayBuffer[EfficientModification]): Unit = {
    if (producer != consumer && plan.planStepDecomposedByMethod(producer) == -1 && !plan.ordering.gt(producer, consumer)) {
      val producerTask = plan.domain.tasks(plan.planStepTasks(producer))
      // and loop through all of their effects
      var producerIndex = 0
      while (producerIndex < producerTask.effect.length) {
        val producerLiteral = producerTask.effect(producerIndex)
        // check whether it is the same predicate and has the same arity
        if (consumerLiteral.predicate == producerLiteral.predicate && consumerLiteral.isPositive == producerLiteral.isPositive) {
          // check whether they can be unified
          val producerParameters = producerTask.getArgumentsOfLiteral(plan.planStepParameters(producer), producerLiteral)
          val mgu = plan.variableConstraints.fastMGU(consumerParameters, producerParameters)
          if (mgu.isDefined)
            buffer append EfficientInsertCausalLink(plan, resolvedFlaw, EfficientCausalLink(producer, consumer, producerIndex, consumerIndex), mgu.get)
        }
        producerIndex += 1
      }
    }
  }

  /**
    * generates all modifications to add a causal link to the consumer's precondition consumerIndex where the producer will be a planstep with index between producerFrom (inlcusive)
    * and producersTo (exclusive)
    */
  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int, producerFrom: Int, producersTo: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()

    val consumerTask = plan.domain.tasks(plan.planStepTasks(consumer))
    val consumerLiteral = consumerTask.precondition(consumerIndex)
    val consumerParameters = consumerTask.getArgumentsOfLiteral(plan.planStepParameters(consumer), consumerLiteral)


    // iterate through all plansteps in the plan to find possible producers
    var producer = producerFrom
    while (producer < producersTo) {
      addModificationFromPlanStep(plan, resolvedFlaw, consumer, consumerIndex, producer, consumerLiteral, consumerParameters, buffer)
      producer += 1
    }
    buffer.toArray
  }

  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int): Array[EfficientModification] =
    apply(plan, resolvedFlaw, consumer, consumerIndex, 0, plan.planStepTasks.length)
}

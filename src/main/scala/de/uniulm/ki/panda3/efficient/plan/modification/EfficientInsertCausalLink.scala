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

  override val addedCausalLinks        : Array[EfficientCausalLink]         = Array(causalLink)
  override val addedVariableConstraints: Array[EfficientVariableConstraint] = necessaryVariableConstraints

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification = EfficientInsertCausalLink(null, severedFlaw, causalLink, necessaryVariableConstraints)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Insert CL " + causalLink
}

object EfficientInsertCausalLink {

  private def iterateThroughModificationsFromPlanStep(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int, producer: Int, consumerLiteral:
  EfficientLiteral, consumerParameters: Array[Int], produceMGU: Boolean, whatToDo: (Int, Array[EfficientVariableConstraint]) => Unit): Unit = {
    if (producer != consumer && plan.isPlanStepPresentInPlan(producer) && !plan.ordering.gt(producer, consumer)) {
      val producerTaskIndex = plan.planStepTasks(producer)
      val producerTask = plan.domain.tasks(producerTaskIndex)

      val potentiallyMatchingEffects =
        if (consumerLiteral.isPositive) plan.domain.taskAddEffectsPerPredicate(producerTaskIndex)(consumerLiteral.predicate)
        else plan.domain.taskDelEffectsPerPredicate(producerTaskIndex)(consumerLiteral.predicate)


      // and loop through all of their effects
      var effectIndex = 0
      //println("P " + producerTask.effect.length)
      while (effectIndex < potentiallyMatchingEffects.length) {
        val (producerLiteral, producerIndex) = potentiallyMatchingEffects(effectIndex)
        // check whether it is the same predicate and has the same arity
        assert(consumerLiteral.predicate == producerLiteral.predicate && consumerLiteral.isPositive == producerLiteral.isPositive)
        // check whether they can be unified
        val producerParameters = producerTask.getArgumentsOfLiteral(plan.planStepParameters(producer), producerLiteral)
        val mgu = plan.variableConstraints.fastMGU(consumerParameters, producerParameters, produceMGU = produceMGU)
        if (mgu.isDefined) {
          assert(plan.potentialSupportersOfPlanStepPreconditions(consumer)(consumerIndex) contains producer)
          whatToDo(producerIndex, mgu.get)
        }
        effectIndex += 1
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
    //var producer = producerFrom
    val potentialSupporterIterator = plan.potentialSupportersOfPlanStepPreconditions(consumer)(consumerIndex).iterator
    while (potentialSupporterIterator.hasNext) {
      val producer = potentialSupporterIterator.next()
      var foundSupport = false

      if (producer >= producerFrom && producer < producersTo) {
        iterateThroughModificationsFromPlanStep(plan, resolvedFlaw, consumer, consumerIndex, producer, consumerLiteral, consumerParameters, produceMGU = true, { case (producerIndex, mgu) =>
          buffer append EfficientInsertCausalLink(plan, resolvedFlaw, EfficientCausalLink(producer, consumer, producerIndex, consumerIndex), mgu)
          foundSupport = true
        })

        if (!foundSupport) {
          //println("No support")
          plan.potentialSupportersOfPlanStepPreconditions(consumer)(consumerIndex) remove producer
        }

      }
    }
    buffer.toArray
  }

  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int): Array[EfficientModification] =
    apply(plan, resolvedFlaw, consumer, consumerIndex, 0, plan.planStepTasks.length)

  def estimate(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, consumer: Int, consumerIndex: Int): Int = {
    val consumerTask = plan.domain.tasks(plan.planStepTasks(consumer))
    val consumerLiteral = consumerTask.precondition(consumerIndex)
    val consumerParameters = consumerTask.getArgumentsOfLiteral(plan.planStepParameters(consumer), consumerLiteral)

    var numberOfModifications = 0

    // iterate through all plansteps in the plan to find possible producers
    val potentialSupporterIterator = plan.potentialSupportersOfPlanStepPreconditions(consumer)(consumerIndex).iterator
    //println("LENGTH " + plan.potentialSupportersOfPlanStepPreconditions(consumer).size)

    //var producer = 0
    //while (producer < plan.numberOfAllPlanSteps) {
    while (potentialSupporterIterator.hasNext) {
      val producer = potentialSupporterIterator.next()
      var foundSupport = false

      //println("START")
      iterateThroughModificationsFromPlanStep(plan, resolvedFlaw, consumer, consumerIndex, producer, consumerLiteral, consumerParameters, produceMGU = false, { case (_, _) =>
        numberOfModifications += 1
        foundSupport = true
        //println("SUPPORT")
      })

      if (!foundSupport) {
        //println("No support ---")
        plan.potentialSupportersOfPlanStepPreconditions(consumer)(consumerIndex) remove producer
      }
      //producer += 1
    }
    numberOfModifications
  }
}
package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw._
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering
import de.uniulm.ki.panda3.symbolic.plan.flaw.CausalThreat

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

  planStepTasks.indices foreach { ps => assert(domain.tasks(planStepTasks(ps)).parameterSorts.length == planStepParameters(ps).length) }


  /**
    * all abstract tasks of this plan
    */
  val abstractPlanSteps: Array[EfficientAbstractPlanStep] = {
    var i = 2 // init and goal are never abstract
    val flawBuffer = new ArrayBuffer[EfficientAbstractPlanStep]()
    while (i < planStepTasks.length) {
      if (!domain.tasks(planStepTasks(i)).isPrimitive) {
        flawBuffer append new EfficientAbstractPlanStep(this, i)
      }
      i += 1
    }
    flawBuffer.toArray
  }

  /** all open preconditions in this plan */
  // TODO: this is absolutely inefficient (especially the iterating over causal links)
  val openPreconditions: Array[EfficientOpenPrecondition] = {
    val flawBuffer = new ArrayBuffer[EfficientOpenPrecondition]()
    var planStep = 1
    while (planStep < planStepTasks.length) {
      var precondition = 0
      while (precondition < domain.tasks(planStepTasks(planStep)).precondition.length) {
        // check for a causal link
        var causalLink = 0
        var foundSupporter = false
        while (causalLink < causalLinks.length) {
          // checking whether this is the correct causal-link
          if (causalLinks(causalLink).consumer == planStep && causalLinks(causalLink).conditionIndexOfConsuer == precondition) foundSupporter = true
          causalLink += 1
        }

        if (!foundSupporter) flawBuffer append new EfficientOpenPrecondition(planStep, precondition)

        precondition += 1
      }
      planStep += 1
    }
    flawBuffer.toArray
  }

  /** all causal threads in this plan */
  val causalThreats: Array[EfficientCausalThreat] = {
    val flawBuffer = new ArrayBuffer[EfficientCausalThreat]()
    var causalLinkNumber = 0
    while (causalLinkNumber < causalLinks.length) {
      // extract information from the link
      val causalLink = causalLinks(causalLinkNumber)
      val producer = domain.tasks(planStepTasks(causalLink.producer))
      val producerLiteral = producer.effect(causalLink.conditionIndexOfProducer)
      val linkpredicate = producerLiteral.predicate
      val linkType = producerLiteral.isPositive
      val linkArguments = producer.getArgumentsOfLiteral(planStepParameters(causalLink.producer), producerLiteral)


      var planStepNumber = 2 // init an goal can nether threat a link
      while (planStepNumber < planStepTasks.length) {
        var effectNumber = 0
        val planStep = domain.tasks(planStepTasks(planStepNumber))

        // check whether it can be ore
        if (!ordering.lt(planStepNumber, causalLink.producer) && !ordering.gt(causalLink.consumer, planStepNumber)) {

          while (effectNumber < planStep.effect.length) {
            val effect = planStep.effect(effectNumber)
            if (effect.predicate == linkpredicate && effect.isPositive != linkType) {
              // check whether unification is possible
              val mgu = variableConstraints.computeMGU(linkArguments, planStep.getArgumentsOfLiteral(planStepParameters(planStepNumber), effect))
              if (mgu.isDefined) flawBuffer append EfficientCausalThreat(causalLink, planStepNumber, effectNumber, mgu.get)
            }
            effectNumber += 1
          }
        }
        planStepNumber += 1
      }
      causalLinkNumber += 1
    }
    flawBuffer.toArray
  }


  val flaws: Array[EfficientFlaw] = {
    val flawBuffer = new ArrayBuffer[EfficientFlaw]()
    flawBuffer ++ causalThreats
    flawBuffer ++ openPreconditions
    flawBuffer ++ abstractPlanSteps

    if (flawBuffer.isEmpty) flawBuffer ++ unboundVariables
    flawBuffer.toArray
  }

  /** all variables which are not bound to a constant, yet */
  lazy val unboundVariables: Array[EfficientUnboundVariable] = {
    val flawBuffer = new ArrayBuffer[EfficientUnboundVariable]()

    var v = 0
    while (v < variableConstraints.numberOfVariables) {
      if (variableConstraints.isRepresentativeAVariable(v)) flawBuffer append EfficientUnboundVariable(v)
      v += 1
    }

    flawBuffer.toArray
  }
}
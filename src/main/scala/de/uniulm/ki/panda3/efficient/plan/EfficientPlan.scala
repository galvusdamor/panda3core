package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.domain.{EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw._
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering

import scala.collection.mutable.ArrayBuffer

/**
  * This is the efficient representation of a plan. Its implementation uses the following assumptions:
  * - its plansteps are numbered 0..sz(planStepTasks)-1 and have the type denoted by the entry in that array
  * - any planstep i for which the value planStepDecomposedByMethod(i) is not -1 is not part of the plan any more
  * - plansteps without a parent in the plan's decomposition tree (i.e. plansteps of the initial plan) have their parent set to -1
  * - the ith subarray of planStepParameters contains the parameters of the ith task
  * - similar to the CSP and to literals, constants are stored in their negative representation (see [[de.uniulm.ki.panda3.efficient.switchConstant]])
  * - init and goal are assumed to be the plan steps indexed 0 and 1 respectively
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientPlan(domain: EfficientDomain, planStepTasks: Array[Int], planStepParameters: Array[Array[Int]], planStepDecomposedByMethod: Array[Int],
                         planStepParentInDecompositonTree: Array[Int], variableConstraints: EfficientCSP, ordering: EfficientOrdering, causalLinks: Array[EfficientCausalLink]) {

  assert(planStepTasks.length == planStepParameters.length)
  assert(planStepTasks.length == planStepDecomposedByMethod.length)
  assert(planStepTasks.length == planStepParentInDecompositonTree.length)
  planStepTasks.indices foreach {
    ps =>
      /*println("PS " + ps)
      println(planStepTasks(ps))
      println(domain.tasks.size)
      println(domain.tasks(planStepTasks(ps)))
      println(planStepParameters(ps))*/
      assert(domain.tasks(planStepTasks(ps)).parameterSorts.length == planStepParameters(ps).length)
  }


  /**
    * all abstract tasks of this plan
    */
  val abstractPlanSteps: Array[EfficientAbstractPlanStep] = {
    var i = 2 // init and goal are never abstract
    val flawBuffer = new ArrayBuffer[EfficientAbstractPlanStep]()
    while (i < planStepTasks.length) {
      if (planStepDecomposedByMethod(i) == -1 && !domain.tasks(planStepTasks(i)).isPrimitive) {
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
      if (planStepDecomposedByMethod(planStep) == -1) {
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

          if (!foundSupporter) flawBuffer append new EfficientOpenPrecondition(this, planStep, precondition)

          precondition += 1
        }
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
        if (planStepDecomposedByMethod(planStepNumber) == -1) {
          var effectNumber = 0
          val planStep = domain.tasks(planStepTasks(planStepNumber))

          // check whether it can be ore
          if (!ordering.lt(planStepNumber, causalLink.producer) && !ordering.gt(causalLink.consumer, planStepNumber)) {

            while (effectNumber < planStep.effect.length) {
              val effect = planStep.effect(effectNumber)
              if (effect.predicate == linkpredicate && effect.isPositive != linkType) {
                // check whether unification is possible
                val mgu = variableConstraints.computeMGU(linkArguments, planStep.getArgumentsOfLiteral(planStepParameters(planStepNumber), effect))
                if (mgu.isDefined) flawBuffer append EfficientCausalThreat(this, causalLink, planStepNumber, effectNumber, mgu.get)
              }
              effectNumber += 1
            }
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
    flawBuffer appendAll causalThreats
    flawBuffer appendAll openPreconditions
    flawBuffer appendAll abstractPlanSteps

    if (flawBuffer.isEmpty) flawBuffer appendAll unboundVariables
    flawBuffer.toArray
  }

  /** all variables which are not bound to a constant, yet */
  lazy val unboundVariables: Array[EfficientUnboundVariable] = {
    val flawBuffer = new ArrayBuffer[EfficientUnboundVariable]()

    var v = 0
    while (v < variableConstraints.numberOfVariables) {
      if (variableConstraints.isRepresentativeAVariable(v)) flawBuffer append EfficientUnboundVariable(this, v)
      v += 1
    }

    flawBuffer.toArray
  }

  def taskOfPlanStep(ps : Int) : EfficientTask = domain.tasks(planStepTasks(ps))

  val firstFreeVariableID : Int = variableConstraints.numberOfVariables
  val firstFreePlanStepID : Int = planStepTasks.length
}
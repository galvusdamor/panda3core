package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.domain.{EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw._
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientInsertCausalLink, EfficientModification}
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering

import scala.collection.{mutable, BitSet}
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
  * - the supporter arrays first contain all supporters for positive and negative predicates in an alternating fashion. For Predicate i the positive supporters are stored in 2*i, the
  * negative ones in 2*i+1
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// TODO there is no place to save the mapping, which planstep one is in their HTN parent's applied method subplan
case class EfficientPlan(domain: EfficientDomain, planStepTasks: Array[Int], planStepParameters: Array[Array[Int]], planStepDecomposedByMethod: Array[Int],
                         planStepParentInDecompositionTree: Array[Int], planStepIsInstanceOfSubPlanPlanStep: Array[Int], variableConstraints: EfficientCSP, ordering: EfficientOrdering,
                         causalLinks: Array[EfficientCausalLink], problemConfiguration: ProblemConfiguration)() {

  assert(planStepTasks.length == planStepParameters.length)
  assert(planStepTasks.length == planStepDecomposedByMethod.length)
  assert(planStepTasks.length == planStepParentInDecompositionTree.length)
  assert(planStepTasks.length == planStepIsInstanceOfSubPlanPlanStep.length)
  planStepTasks.indices foreach {
    ps =>
      /*println("PS " + ps)
      println(planStepTasks(ps))
      println(domain.tasks.size)
      println(domain.tasks(planStepTasks(ps)))
      println(planStepParameters(ps))*/
      assert(domain.tasks(planStepTasks(ps)).parameterSorts.length == planStepParameters(ps).length)

      planStepParameters(ps).indices foreach { arg =>
        planStepParameters(ps)(arg) < firstFreeVariableID
      }
  }

  //assert(possibleSupportersByDecompositionPerLiteral.length == 2 * domain.predicates.length)

  def isPlanStepPresentInPlan(planStep: Int): Boolean = planStepDecomposedByMethod(planStep) == -1

  val numberOfAllPlanSteps: Int = planStepTasks.length
  val numberOfPlanSteps   : Int = {
    var number = 2
    var i = 2
    while (i < numberOfAllPlanSteps) {
      if (isPlanStepPresentInPlan(i)) number += 1
      i += 1
    }
    number
  }

  private var precomputedAbstractPlanStepFlaws: Option[Array[EfficientAbstractPlanStep]] = None
  private var precomputedOpenPreconditionFlaws: Option[Array[EfficientOpenPrecondition]] = None
  private var appliedModification             : Option[EfficientModification]            = None
  private var precomputedCausalThreatFlaws    : Option[Array[EfficientCausalThreat]]     = None

  /** the open preconditions flaws of the parent of this plan --- and the number of newly added tasks.
    * The assumption is that the tasks sz(planstep) - nonHandledTasks .. sz(planstep)-1 are new
    */
  private def setPrecomputedOpenPreconditions(oldOpenPrecondition: Array[EfficientOpenPrecondition], modification: EfficientModification): Unit = {
    val severedOpenPreconditions = new Array[EfficientOpenPrecondition](oldOpenPrecondition.length)
    var i = 0
    while (i < severedOpenPreconditions.length) {
      severedOpenPreconditions(i) = oldOpenPrecondition(i).severLinkToPlan(dismissDecompositionModifications = true)
      i += 1
    }

    precomputedOpenPreconditionFlaws = Some(severedOpenPreconditions)
    appliedModification = Some(modification.severLinkToPlan)
  }

  def severLinkToParentPlan(): Unit = {
    precomputedAbstractPlanStepFlaws = None
    precomputedOpenPreconditionFlaws = None
    appliedModification = None
    precomputedCausalThreatFlaws = None
  }


  /** all abstract tasks of this plan */
  lazy val abstractPlanSteps: Array[EfficientAbstractPlanStep] = {
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


  /** recomputes the open preconditions flaws */
  private def computeOpenPreconditions(planStep: Int, buffer: ArrayBuffer[EfficientOpenPrecondition]) = {
    if (planStepDecomposedByMethod(planStep) == -1) {
      var precondition = 0
      while (precondition < domain.tasks(planStepTasks(planStep)).precondition.length) {
        // check for a causal link
        var causalLink = 0
        var foundSupporter = false
        while (causalLink < causalLinks.length) {
          // checking whether this is the correct causal-link
          if (causalLinks(causalLink).consumer == planStep && causalLinks(causalLink).conditionIndexOfConsumer == precondition) foundSupporter = true
          causalLink += 1
        }

        if (!foundSupporter) buffer append new EfficientOpenPrecondition(this, planStep, precondition)

        precondition += 1
      }
    }
  }


  /** all open preconditions in this plan */
  // TODO: this is absolutely inefficient (especially the iterating over causal links)
  lazy val openPreconditions: Array[EfficientOpenPrecondition] = {
    val flawBuffer = new ArrayBuffer[EfficientOpenPrecondition]()
    // nothing is given so recompute all
    if (precomputedOpenPreconditionFlaws.isEmpty) {
      var planStep = 1
      while (planStep < planStepTasks.length) {
        computeOpenPreconditions(planStep, flawBuffer)
        planStep += 1
      }
      flawBuffer.toArray
    } else {
      val flawBuffer = new ArrayBuffer[EfficientOpenPrecondition]()
      // 1. take all flaws of my "parent" plan and update them according to the newly added tasks
      val precomputed = precomputedOpenPreconditionFlaws.get
      var i = 0
      while (i < precomputed.length) {
        // check whether this flaw has actually been resolved
        val flaw = precomputed(i)
        val flawResolved = flaw.equalToSeveredFlaw(appliedModification.get.resolvedFlaw) &&
          (appliedModification.get.isInstanceOf[EfficientInsertCausalLink] || appliedModification.get.isInstanceOf[EfficientInsertPlanStepWithLink])

        if (!flawResolved && planStepDecomposedByMethod(flaw.planStep) == -1)
          flawBuffer append flaw.updateToNewPlan(this, appliedModification.get.addedPlanSteps.length, appliedModification.get.decomposedPlanSteps)
        i += 1
      }

      // add open precondition flaws for all new tasks
      i = 0
      while (i < appliedModification.get.addedPlanSteps.length) {
        computeOpenPreconditions(planStepTasks.length - i - 1, flawBuffer)
        i += 1
      }

      flawBuffer.toArray
    }
  }

  /** all causal threads in this plan */
  lazy val causalThreats: Array[EfficientCausalThreat] = {
    val flawBuffer = new ArrayBuffer[EfficientCausalThreat]()
    var causalLinkNumber = 0
    while (causalLinkNumber < causalLinks.length) {
      // extract information from the link
      val causalLink = causalLinks(causalLinkNumber)

      // check whether the link is still present
      if (planStepDecomposedByMethod(causalLink.producer) == -1 && planStepDecomposedByMethod(causalLink.consumer) == -1) {

        val producer = domain.tasks(planStepTasks(causalLink.producer))
        val consumer = domain.tasks(planStepTasks(causalLink.consumer))
        val producerLiteral = producer.effect(causalLink.conditionIndexOfProducer)
        val linkpredicate = producerLiteral.predicate
        val linkType = producerLiteral.isPositive
        val linkArguments = producer.getArgumentsOfLiteral(planStepParameters(causalLink.producer), producerLiteral)


        var planStepNumber = 2 // init and goal can nether threat a link
        while (planStepNumber < planStepTasks.length) {
          if (planStepDecomposedByMethod(planStepNumber) == -1) {
            var effectNumber = 0
            val planStep = domain.tasks(planStepTasks(planStepNumber))

            // check whether it can before
            if (!ordering.lteq(planStepNumber, causalLink.producer) && !ordering.lteq(causalLink.consumer, planStepNumber)) {

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
      }
      causalLinkNumber += 1
    }
    flawBuffer.toArray
  }


  lazy val flaws: Array[EfficientFlaw] = {
    val flawBuffer = new ArrayBuffer[EfficientFlaw]()
    flawBuffer appendAll causalThreats
    flawBuffer appendAll openPreconditions
    if (problemConfiguration.decompositionAllowed) flawBuffer appendAll abstractPlanSteps

    if (flawBuffer.isEmpty) flawBuffer appendAll unboundVariables

    // sever the link as we don't need its information any more
    severLinkToParentPlan()

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

  def modify(modification: EfficientModification): EfficientPlan = {
    val newVariableConstraints: EfficientCSP = variableConstraints.addVariables(modification.addedVariableSorts)
    val newOrdering: EfficientOrdering = ordering.addPlanSteps(modification.addedPlanSteps.length)

    // apply the modification

    // 1. new plan steps and the init -> ps -> goal orderings
    val numberOfNewPlanSteps = firstFreePlanStepID + modification.addedPlanSteps.length
    val newPlanStepTasks = new Array[Int](numberOfNewPlanSteps)
    val newPlanStepParameters = new Array[Array[Int]](numberOfNewPlanSteps)
    val newPlanStepDecomposedByMethod = new Array[Int](numberOfNewPlanSteps)
    val newPlanStepParentInDecompositionTree = new Array[Int](numberOfNewPlanSteps)
    val newPlanStepIsInstanceOfSubPlanPlanStep = new Array[Int](numberOfNewPlanSteps)

    var oldPS = 0
    while (oldPS < firstFreePlanStepID) {
      newPlanStepTasks(oldPS) = planStepTasks(oldPS)
      newPlanStepParameters(oldPS) = planStepParameters(oldPS)
      newPlanStepDecomposedByMethod(oldPS) = planStepDecomposedByMethod(oldPS)
      newPlanStepParentInDecompositionTree(oldPS) = planStepParentInDecompositionTree(oldPS)
      newPlanStepIsInstanceOfSubPlanPlanStep(oldPS) = planStepIsInstanceOfSubPlanPlanStep(oldPS)
      oldPS += 1
    }

    var newPS = 0
    while (newPS < modification.addedPlanSteps.length) {
      val newPSIndex = firstFreePlanStepID + newPS
      newPlanStepTasks(newPSIndex) = modification.addedPlanSteps(newPS)._1
      newPlanStepParameters(newPSIndex) = modification.addedPlanSteps(newPS)._2
      newPlanStepDecomposedByMethod(newPSIndex) = modification.addedPlanSteps(newPS)._3
      newPlanStepParentInDecompositionTree(newPSIndex) = modification.addedPlanSteps(newPS)._4
      newPlanStepIsInstanceOfSubPlanPlanStep(newPSIndex) = modification.addedPlanSteps(newPS)._5
      // new plan steps are between init and goal
      newOrdering.addOrderingConstraint(0, firstFreePlanStepID + newPS) // init < ps
      newOrdering.addOrderingConstraint(firstFreePlanStepID + newPS, 1) // ps < goal
      newPS += 1
    }

    // 2. new causal links
    val newCausalLinks = new Array[EfficientCausalLink](causalLinks.length + modification.addedCausalLinks.length)
    var causalLinkIndex = 0
    while (causalLinkIndex < newCausalLinks.length) {
      if (causalLinkIndex < causalLinks.length)
        newCausalLinks(causalLinkIndex) = causalLinks(causalLinkIndex)
      else
        newCausalLinks(causalLinkIndex) = modification.addedCausalLinks(causalLinkIndex - causalLinks.length)

      causalLinkIndex += 1
    }

    // 3. variable constraints
    var constraint = 0
    while (constraint < modification.addedVariableConstraints.length) {
      newVariableConstraints.addConstraint(modification.addedVariableConstraints(constraint))
      constraint += 1
    }

    // 4. orderings
    var ord = 0
    while (ord < modification.addedOrderings.length) {
      newOrdering.addOrderingConstraint(modification.addedOrderings(ord)._1, modification.addedOrderings(ord)._2)
      ord += 1
    }


    // 5. mark all decomposed planteps as decomposed
    var decomposedPS = 0
    while (decomposedPS < modification.decomposedPlanStepsByMethod.length) {
      val decompositionInformation = modification.decomposedPlanStepsByMethod(decomposedPS)
      newPlanStepDecomposedByMethod(decompositionInformation._1) = decompositionInformation._2
      decomposedPS += 1
    }

    val newPlan = EfficientPlan(domain, newPlanStepTasks, newPlanStepParameters, newPlanStepDecomposedByMethod, newPlanStepParentInDecompositionTree,
                                newPlanStepIsInstanceOfSubPlanPlanStep, newVariableConstraints, newOrdering, newCausalLinks, problemConfiguration)()

    //newPlan.setPrecomputedOpenPreconditions(openPreconditions, modification)

    newPlan
  }

  def taskOfPlanStep(ps: Int): EfficientTask = domain.tasks(planStepTasks(ps))

  def argumentsOfPlanStepsEffect(ps: Int, effectIndex: Int): Array[Int] = {
    val task = taskOfPlanStep(ps)
    task.getArgumentsOfLiteral(planStepParameters(ps), task.effect(effectIndex))
  }

  val firstFreeVariableID: Int = variableConstraints.numberOfVariables
  val firstFreePlanStepID: Int = planStepTasks.length

  lazy val possibleSupportersByDecompositionPerLiteral: Array[Array[Int]] = EfficientPlan.computeDecompositionSupportersPerLiteral(domain, planStepTasks, planStepDecomposedByMethod)
}

object EfficientPlan {
  private def computeDecompositionSupportersPerLiteral(domain: EfficientDomain, planStepTasks: Array[Int], planStepDecomposedByMethod: Array[Int]): Array[Array[Int]] = {
    val supporters = new Array[mutable.BitSet](2 * domain.predicates.length)
    var i = 0
    while (i < supporters.length) {
      supporters(i) = mutable.BitSet()
      i += 1
    }

    // iterate over all tasks
    i = 2
    while (i < planStepTasks.length) {
      if (planStepDecomposedByMethod(i) == -1 && !domain.tasks(planStepTasks(i)).isPrimitive) {
        val supportedLiterals = domain.taskSchemaTransitionGraph.taskCanSupportByDecomposition(planStepTasks(i))
        var j = 0
        while (j < supportedLiterals.length) {
          val predicateIndex = 2 * supportedLiterals(j)._1 + (if (supportedLiterals(j)._2) 0 else 1)
          supporters(predicateIndex) add i
          j += 1
        }
      }
      i += 1
    }

    // store the result in a big array
    val result = new Array[Array[Int]](2 * domain.predicates.length)
    i = 0
    while (i < result.length) {
      result(i) = supporters(i).toArray
      i += 1
    }
    result
  }
}
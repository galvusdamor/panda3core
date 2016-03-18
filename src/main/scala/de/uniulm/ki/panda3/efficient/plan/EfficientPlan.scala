package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.domain.{EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw._
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientInsertCausalLink, EfficientModification}
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
                         planStepParentInDecompositionTree: Array[Int], variableConstraints: EfficientCSP, ordering: EfficientOrdering, causalLinks: Array[EfficientCausalLink]) {

  assert(planStepTasks.length == planStepParameters.length)
  assert(planStepTasks.length == planStepDecomposedByMethod.length)
  assert(planStepTasks.length == planStepParentInDecompositionTree.length)
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


  private var precomputedAbstractPlanStepFlaws: Option[Array[EfficientAbstractPlanStep]] = None
  private var precomputedOpenPreconditionFlaws: Option[Array[EfficientOpenPrecondition]] = None
  private var appliedModification             : Option[EfficientModification]            = None
  private var precomputedCausalThreatFlaws    : Option[Array[EfficientCausalThreat]]     = None


  /** the open preconditions flaws of the parent of this plan --- and the number of newly added tasks.
    * The assumption is that the tasks sz(planstep) - nonHandledTasks .. sz(planstep)-1 are new
    */
  private def setPrecomputedOpenPreconditions(oldOpenPrecondition: Array[EfficientOpenPrecondition], modification: EfficientModification): Unit = {
    precomputedOpenPreconditionFlaws = Some(oldOpenPrecondition)
    appliedModification = Some(modification)
  }


  /**
    * all abstract tasks of this plan
    */
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
        val flawResolved = flaw == appliedModification.get.resolvedFlaw &&
          (appliedModification.isInstanceOf[EfficientInsertCausalLink] || appliedModification.isInstanceOf[EfficientInsertPlanStepWithLink])

        if (!flawResolved && planStepDecomposedByMethod(flaw.planStep) == -1)
          flawBuffer append flaw.updateToNewPlan(this, appliedModification.get.addedPlanSteps.length, appliedModification.get.decomposedPlanSteps)
        i += 1
      }
      //println("Taken " + flawBuffer.length)

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
            if (!ordering.lteq(planStepNumber, causalLink.producer) && !ordering.gteq(causalLink.consumer, planStepNumber)) {

              while (effectNumber < planStep.effect.length) {
                val effect = planStep.effect(effectNumber)
                if (effect.predicate == linkpredicate && effect.isPositive != linkType) {
                  // check whether unification is possible
                  val mgu = variableConstraints.fastMGU(linkArguments, planStep.getArgumentsOfLiteral(planStepParameters(planStepNumber), effect))
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

  def modify(modification: EfficientModification): EfficientPlan = {
    val newPlanStepTasks = new ArrayBuffer[Int]()
    val newPlanStepParameters = new ArrayBuffer[Array[Int]]()
    val newPlanStepDecomposedByMethod = new ArrayBuffer[Int]()
    val newPlanStepParentInDecompositionTree = new ArrayBuffer[Int]()
    val newCausalLinks = new ArrayBuffer[EfficientCausalLink]()
    val newVariableConstraints: EfficientCSP = variableConstraints.addVariables(modification.addedVariableSorts)
    val newOrdering: EfficientOrdering = ordering.addPlanSteps(modification.addedPlanSteps.length)

    newPlanStepTasks appendAll planStepTasks
    newPlanStepParameters appendAll planStepParameters
    newPlanStepDecomposedByMethod appendAll planStepDecomposedByMethod
    newPlanStepParentInDecompositionTree appendAll planStepParentInDecompositionTree
    newCausalLinks appendAll causalLinks

    // apply the modification

    // 1. new plan steps and the init -> ps -> goal orderings
    var newPS = 0
    while (newPS < modification.addedPlanSteps.length) {
      newPlanStepTasks append modification.addedPlanSteps(newPS)._1
      newPlanStepParameters append modification.addedPlanSteps(newPS)._2
      newPlanStepDecomposedByMethod append modification.addedPlanSteps(newPS)._3
      newPlanStepParentInDecompositionTree append modification.addedPlanSteps(newPS)._4
      // new plan steps are between init and goal
      newOrdering.addOrderingConstraint(0, firstFreePlanStepID + newPS) // init < ps
      newOrdering.addOrderingConstraint(firstFreePlanStepID + newPS, 1) // ps < goal
      newPS += 1
    }

    // 2. new causal links
    newCausalLinks appendAll modification.addedCausalLinks

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


    val newPlanStepDecomposedByMethodArray = newPlanStepDecomposedByMethod.toArray

    // 5. mark all decomposed planteps as decomposed
    var decomposedPS = 0
    while (decomposedPS < modification.decomposedPlanStepsByMethod.length) {
      val decompositionInformation = modification.decomposedPlanStepsByMethod(decomposedPS)
      newPlanStepDecomposedByMethodArray(decompositionInformation._1) = decompositionInformation._2
      decomposedPS += 1
    }

    val newPlan = EfficientPlan(domain, newPlanStepTasks.toArray, newPlanStepParameters.toArray, newPlanStepDecomposedByMethodArray, newPlanStepParentInDecompositionTree.toArray,
                                newVariableConstraints, newOrdering, newCausalLinks.toArray)

    newPlan.setPrecomputedOpenPreconditions(openPreconditions, modification)

    newPlan
  }

  def taskOfPlanStep(ps: Int): EfficientTask = domain.tasks(planStepTasks(ps))

  def argumentsOfPlanStepsEffect(ps: Int, effectIndex: Int): Array[Int] = {
    val task = taskOfPlanStep(ps)
    task.getArgumentsOfLiteral(planStepParameters(ps), task.effect(effectIndex))
  }

  val firstFreeVariableID: Int = variableConstraints.numberOfVariables
  val firstFreePlanStepID: Int = planStepTasks.length
}
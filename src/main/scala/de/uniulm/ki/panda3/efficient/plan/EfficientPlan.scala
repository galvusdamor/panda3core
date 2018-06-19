// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.EfficientCSP
import de.uniulm.ki.panda3.efficient.domain.{EfficientDomain, EfficientGroundTask, EfficientTask}
import de.uniulm.ki.panda3.efficient.logic.{EfficientGroundLiteral, EfficientLiteral}
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw._
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertCausalLink, EfficientInsertPlanStepWithLink, EfficientModification}
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering

import scala.annotation.elidable
import scala.annotation.elidable.ASSERTION
import scala.collection.{BitSet, mutable}
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
// scalastyle:off null
case class EfficientPlan(domain: EfficientDomain, planStepTasks: Array[Int], planStepParameters: Array[Array[Int]], planStepDecomposedByMethod: Array[Int],
                         planStepParentInDecompositionTree: Array[Int], planStepIsInstanceOfSubPlanPlanStep: Array[Int], planStepSupportedPreconditions: Array[mutable.BitSet],
                         potentialSupportersOfPlanStepPreconditions: Array[Array[mutable.BitSet]], causalLinksPotentialThreater: Array[mutable.BitSet],
                         variableConstraints: EfficientCSP, ordering: EfficientOrdering, causalLinks: Array[EfficientCausalLink], problemConfiguration: ProblemConfiguration,
                         depth: Int = 0)(val depthPerPlanStep: Array[Int] = Array.fill(planStepTasks.length)(0), val depthPerCausalLink: Array[Int] = Array.fill(causalLinks.length)(0)) {

  // sanity checks
  @elidable(ASSERTION)
  def assertion() : Boolean = {
    assert(planStepTasks.length == planStepParameters.length)
    if (planStepDecomposedByMethod != null) {
      assert(planStepTasks.length == planStepDecomposedByMethod.length)
      assert(planStepTasks.length == planStepParentInDecompositionTree.length)
      assert(planStepTasks.length == planStepIsInstanceOfSubPlanPlanStep.length)
    }
    assert(planStepTasks.length == planStepSupportedPreconditions.length)
    assert(planStepTasks.length == potentialSupportersOfPlanStepPreconditions.length)
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

    assert(causalLinksPotentialThreater.length == causalLinks.length)
    assert(causalLinks forall { case EfficientCausalLink(prod, cons, pInd, cInd) => taskOfPlanStep(prod).effect(pInd).predicate == taskOfPlanStep(cons).precondition(cInd).predicate })
    true
  }

  assert(assertion())

  //assert(possibleSupportersByDecompositionPerLiteral.length == 2 * domain.predicates.length)

  lazy val groundInitialState: Array[(Int, Array[Int])] = {
    val initSchema = domain.tasks(planStepTasks(0))
    val groundedInit = EfficientGroundTask(planStepTasks(0), planStepParameters(0) map variableConstraints.getRepresentativeConstant)

    initSchema.precondition.indices map { i => groundedInit.substitutedPrecondition(i, domain) } collect { case EfficientGroundLiteral(pred, true, args) => (pred, args) } toArray
  }

  def isPlanStepPresentInPlan(planStep: Int): Boolean = planStepDecomposedByMethod == null || planStepDecomposedByMethod(planStep) == -1

  val numberOfAllPlanSteps: Int                                                  = planStepTasks.length
  val (numberOfPlanSteps, numberOfPrimitivePlanSteps, numberOfAbstractPlanSteps) = {
    var numberPS = 2
    var numberPrimitive = 2
    var i = 2
    while (i < numberOfAllPlanSteps) {
      if (isPlanStepPresentInPlan(i)) {
        numberPS += 1
        if (domain.tasks(planStepTasks(i)).isPrimitive) numberPrimitive += 1
      }
      i += 1
    }
    (numberPS, numberPrimitive, numberPS - numberPrimitive)
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
    if (isPlanStepPresentInPlan(planStep)) {
      var precondition = 0
      while (precondition < domain.tasks(planStepTasks(planStep)).precondition.length) {
        // check for a causal link
        val foundSupporter = planStepSupportedPreconditions(planStep) contains precondition
        /*var foundSupporter = false
        var causalLink = 0
        while (causalLink < causalLinks.length) {
          // checking whether this is the correct causal-link
          if (causalLinks(causalLink).consumer == planStep && causalLinks(causalLink).conditionIndexOfConsumer == precondition) foundSupporter = true
          causalLink += 1
        }*/

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

        if (!flawResolved && isPlanStepPresentInPlan(flaw.planStep))
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
      assert(causalLink != null)
      if (isPlanStepPresentInPlan(causalLink.producer) && isPlanStepPresentInPlan(causalLink.consumer)) {

        val producer = domain.tasks(planStepTasks(causalLink.producer))
        val consumer = domain.tasks(planStepTasks(causalLink.consumer))
        val producerLiteral = producer.effect(causalLink.conditionIndexOfProducer)
        val linkpredicate = producerLiteral.predicate
        val linkType = producerLiteral.isPositive
        val linkArguments = producer.getArgumentsOfLiteral(planStepParameters(causalLink.producer), producerLiteral)

        // only iterate over potential threater
        val potentialThreaterList = causalLinksPotentialThreater(causalLinkNumber)
        val threaterIterator = potentialThreaterList.iterator

        while (threaterIterator.hasNext) {
          val potentialThreater = threaterIterator.next()

          var isThreating = false
          if (isPlanStepPresentInPlan(potentialThreater)) {
            var effectNumber = 0
            val planStep = domain.tasks(planStepTasks(potentialThreater))

            // check whether it can before
            if (!ordering.lteq(potentialThreater, causalLink.producer) && !ordering.lteq(causalLink.consumer, potentialThreater)) {

              while (effectNumber < planStep.effect.length) {
                val effect = planStep.effect(effectNumber)
                if (effect.predicate == linkpredicate && effect.isPositive != linkType) {
                  // check whether unification is possible
                  val mgu = variableConstraints.computeMGU(linkArguments, planStep.getArgumentsOfLiteral(planStepParameters(potentialThreater), effect))
                  if (mgu.isDefined) {
                    flawBuffer append EfficientCausalThreat(this, causalLink, potentialThreater, effectNumber, mgu.get)
                    isThreating = true
                  }
                }
                effectNumber += 1
              }
            }
          }

          if (!isThreating)
            potentialThreaterList remove potentialThreater
        }


        /*var planStepNumber = 2 // init and goal can never threat a link
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
                  if (mgu.isDefined) {
                    assert(causalLinksPotentialThreater(causalLinkNumber) contains planStepNumber)
                    //println("THREAT")
                    flawBuffer append EfficientCausalThreat(this, causalLink, planStepNumber, effectNumber, mgu.get)
                  }
                }
                effectNumber += 1
              }
            }
          }
          planStepNumber += 1
        }*/
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
    val newOrdering: EfficientOrdering =
      if (modification.insertInOrderingRelativeToPlanStep == -1) ordering.addPlanSteps(modification.addedPlanSteps.length)
      else ordering.addPlanStepsFromBase(modification.insertInOrderingRelativeToPlanStep, modification.addedPlanSteps.length, modification.insertedPlanStepsOrderingMatrix.get)

    // apply the modification

    val numberOfPlanStepsInNewPlan = firstFreePlanStepID + modification.addedPlanSteps.length
    val newPlanStepSupportedPreconditions = new Array[mutable.BitSet](numberOfPlanStepsInNewPlan)
    val newPotentialSupportersOfPlanStepPreconditions = new Array[Array[mutable.BitSet]](numberOfPlanStepsInNewPlan)
    val newCausalLinksPotentialThreater = new Array[mutable.BitSet](causalLinks.length + modification.addedCausalLinks.length)
    // initialise threater table
    var causalLinkIndex = 0
    while (causalLinkIndex < newCausalLinksPotentialThreater.length) {
      if (causalLinkIndex < causalLinks.length)
        newCausalLinksPotentialThreater(causalLinkIndex) = causalLinksPotentialThreater(causalLinkIndex).clone()
      else
        newCausalLinksPotentialThreater(causalLinkIndex) = mutable.BitSet()

      causalLinkIndex += 1
    }

    var newPlanStepTasks = planStepTasks
    var newPSDepths = depthPerPlanStep
    var newPlanStepParameters = planStepParameters
    var newPlanStepParentInDecompositionTree = planStepParentInDecompositionTree
    var newPlanStepIsInstanceOfSubPlanPlanStep = planStepIsInstanceOfSubPlanPlanStep
    var newPlanStepDecomposedByMethod = planStepDecomposedByMethod


    var oldPS = 0
    while (oldPS < firstFreePlanStepID) {
      newPlanStepSupportedPreconditions(oldPS) = planStepSupportedPreconditions(oldPS).clone()
      var preconditionNumber = 0
      val numberOfPreconditions = potentialSupportersOfPlanStepPreconditions(oldPS).length
      newPotentialSupportersOfPlanStepPreconditions(oldPS) = new Array[mutable.BitSet](numberOfPreconditions)
      while (preconditionNumber < numberOfPreconditions) {
        if (!(newPlanStepSupportedPreconditions(oldPS) contains preconditionNumber))
          newPotentialSupportersOfPlanStepPreconditions(oldPS)(preconditionNumber) = potentialSupportersOfPlanStepPreconditions(oldPS)(preconditionNumber).clone()
        preconditionNumber += 1
      }
      oldPS += 1
    }


    // only create the new arrays if a task was actually added
    if (!EfficientPlan.useIncrementalConstruction || modification.addedPlanSteps.length != 0) {
      newPlanStepTasks = new Array[Int](numberOfPlanStepsInNewPlan)
      newPlanStepParameters = new Array[Array[Int]](numberOfPlanStepsInNewPlan)

      if (planStepDecomposedByMethod != null) {
        newPlanStepParentInDecompositionTree = new Array[Int](numberOfPlanStepsInNewPlan)
        newPlanStepIsInstanceOfSubPlanPlanStep = new Array[Int](numberOfPlanStepsInNewPlan)
        newPSDepths = new Array[Int](newPlanStepTasks.length)
      }

      // 1. new plan steps and the init -> ps -> goal orderings
      var oldPS = 0
      while (oldPS < firstFreePlanStepID) {
        newPlanStepTasks(oldPS) = planStepTasks(oldPS)
        newPlanStepParameters(oldPS) = planStepParameters(oldPS)
        if (newPlanStepDecomposedByMethod != null) {
          newPlanStepParentInDecompositionTree(oldPS) = planStepParentInDecompositionTree(oldPS)
          newPlanStepIsInstanceOfSubPlanPlanStep(oldPS) = planStepIsInstanceOfSubPlanPlanStep(oldPS)
        }
        oldPS += 1
      }

      var newPS = 0
      while (newPS < modification.addedPlanSteps.length) {
        val newPSIndex = firstFreePlanStepID + newPS
        newPlanStepTasks(newPSIndex) = modification.addedPlanSteps(newPS)._1
        newPlanStepParameters(newPSIndex) = modification.addedPlanSteps(newPS)._2
        newPlanStepSupportedPreconditions(newPSIndex) = mutable.BitSet()

        if (newPlanStepDecomposedByMethod != null) {
          newPlanStepParentInDecompositionTree(newPSIndex) = modification.addedPlanSteps(newPS)._4
          newPlanStepIsInstanceOfSubPlanPlanStep(newPSIndex) = modification.addedPlanSteps(newPS)._5
        }

        // get the task schema
        val numberOfPreconditions = domain.tasks(newPlanStepTasks(newPSIndex)).precondition.length
        newPotentialSupportersOfPlanStepPreconditions(newPSIndex) = new Array[mutable.BitSet](numberOfPreconditions)

        var precondition = 0
        while (precondition < numberOfPreconditions) {
          newPotentialSupportersOfPlanStepPreconditions(newPSIndex)(precondition) = mutable.BitSet()
          precondition += 1
        }

        // new plan steps are between init and goal
        newOrdering.addOrderingConstraint(0, newPSIndex) // init < ps
        newOrdering.addOrderingConstraint(newPSIndex, 1) // ps < goal

        // check whether they threat old causal links
        var oldCausalLinkIndex = 0
        while (oldCausalLinkIndex < causalLinks.length) {
          val producer = domain.tasks(newPlanStepTasks(causalLinks(oldCausalLinkIndex).producer))
          val producerLiteral = producer.effect(causalLinks(oldCausalLinkIndex).conditionIndexOfProducer)
          val linkpredicate = producerLiteral.predicate
          val linkIsPositive = producerLiteral.isPositive

          val possibleEffects = domain.taskToEffectPredicates(newPlanStepTasks(newPSIndex))
          // positive link is threated by negative effect
          val opposedEffect = if (linkIsPositive) possibleEffects._2 else possibleEffects._1
          if (opposedEffect contains linkpredicate) newCausalLinksPotentialThreater(oldCausalLinkIndex).add(newPSIndex)

          oldCausalLinkIndex += 1
        }
        newPS += 1
      }
      // update the potential supporter table
      newPS = 0
      while (newPS < modification.addedPlanSteps.length) {
        val newPSIndex = firstFreePlanStepID + newPS
        val thisPSTaskID = newPlanStepTasks(newPSIndex)
        val newPSSupporter = domain.tasksPreconditionCanBeSupportedBy(thisPSTaskID)

        var otherPlanStep = 0
        while (otherPlanStep < numberOfPlanStepsInNewPlan) {
          val otherPSTaskID = newPlanStepTasks(otherPlanStep)

          // newPS supporters
          var precondition = 0
          while (precondition < newPSSupporter.length) {
            if (!(newPlanStepSupportedPreconditions(newPSIndex) contains precondition) && (newPSSupporter(precondition) contains otherPSTaskID))
              newPotentialSupportersOfPlanStepPreconditions(newPSIndex)(precondition) add otherPlanStep
            precondition += 1
          }

          val otherPSSupporter = domain.tasksPreconditionCanBeSupportedBy(otherPSTaskID)
          precondition = 0
          while (precondition < otherPSSupporter.length) {
            if (!(newPlanStepSupportedPreconditions(otherPlanStep) contains precondition) && (otherPSSupporter(precondition) contains thisPSTaskID))
              newPotentialSupportersOfPlanStepPreconditions(otherPlanStep)(precondition) add newPSIndex
            precondition += 1
          }

          otherPlanStep += 1
        }
        newPS += 1
      }



      if (newPlanStepDecomposedByMethod != null) {
        // 6. depth information
        var i = 0
        while (i < newPlanStepTasks.length) {
          if (i < planStepTasks.length) newPSDepths(i) = depthPerPlanStep(i)
          else newPSDepths(i) = depth + 1
          i += 1
        }
      }
    }

    if ((!EfficientPlan.useIncrementalConstruction || modification.decomposedPlanStepsByMethod.length != 0 || modification.addedPlanSteps.length != 0)
      && newPlanStepDecomposedByMethod != null) {

      newPlanStepDecomposedByMethod = new Array[Int](numberOfPlanStepsInNewPlan)

      var oldPS = 0
      while (oldPS < firstFreePlanStepID) {
        newPlanStepDecomposedByMethod(oldPS) = planStepDecomposedByMethod(oldPS)
        oldPS += 1
      }

      var newPS = 0
      while (newPS < modification.addedPlanSteps.length) {
        val newPSIndex = firstFreePlanStepID + newPS
        newPlanStepDecomposedByMethod(newPSIndex) = modification.addedPlanSteps(newPS)._3
        newPS += 1
      }

      // 5. mark all decomposed plansteps as decomposed
      var decomposedPS = 0
      while (decomposedPS < modification.decomposedPlanStepsByMethod.length) {
        val decompositionInformation = modification.decomposedPlanStepsByMethod(decomposedPS)
        newPlanStepDecomposedByMethod(decompositionInformation._1) = decompositionInformation._2
        decomposedPS += 1
      }

    }

    var newCausalLinks = causalLinks

    if (modification.addedCausalLinks.nonEmpty) {
      // 2. new causal links
      newCausalLinks = new Array[EfficientCausalLink](causalLinks.length + modification.addedCausalLinks.length)
      causalLinkIndex = 0
      while (causalLinkIndex < newCausalLinks.length) {
        if (causalLinkIndex < causalLinks.length)
          newCausalLinks(causalLinkIndex) = causalLinks(causalLinkIndex)
        else {
          // this is a new causal link
          newCausalLinks(causalLinkIndex) = modification.addedCausalLinks(causalLinkIndex - causalLinks.length)
          // closes an open precondition
          newPlanStepSupportedPreconditions(newCausalLinks(causalLinkIndex).consumer) add newCausalLinks(causalLinkIndex).conditionIndexOfConsumer

          val producer = domain.tasks(newPlanStepTasks(newCausalLinks(causalLinkIndex).producer))
          val producerLiteral = producer.effect(newCausalLinks(causalLinkIndex).conditionIndexOfProducer)
          val linkpredicate = producerLiteral.predicate
          val linkIsPositive = producerLiteral.isPositive

          // iterate over all actions and check whether they might threat us
          var planStep = 2
          while (planStep < numberOfPlanStepsInNewPlan) {
            if (planStep >= numberOfAllPlanSteps || isPlanStepPresentInPlan(planStep)) {
              val possibleEffects = domain.taskToEffectPredicates(newPlanStepTasks(planStep))
              // positive link is threated by negative effect
              val opposedEffect = if (linkIsPositive) possibleEffects._2 else possibleEffects._1

              if (opposedEffect contains linkpredicate)
                newCausalLinksPotentialThreater(causalLinkIndex).add(planStep)
            }
            planStep += 1
          }
        }
        causalLinkIndex += 1
      }
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


    val newCLDepths = if (depthPerCausalLink == null) null else new Array[Int](newCausalLinks.length)
    if (depthPerCausalLink != null) {
      var i = 0
      while (i < newCausalLinks.length) {
        if (i < causalLinks.length) newCLDepths(i) = depthPerCausalLink(i)
        else newCLDepths(i) = depth + 1
        i += 1
      }
    }


    val newPlan = EfficientPlan(domain, newPlanStepTasks, newPlanStepParameters, newPlanStepDecomposedByMethod, newPlanStepParentInDecompositionTree,
                                newPlanStepIsInstanceOfSubPlanPlanStep, newPlanStepSupportedPreconditions, newPotentialSupportersOfPlanStepPreconditions, newCausalLinksPotentialThreater,
                                newVariableConstraints, newOrdering, newCausalLinks, problemConfiguration,
                                depth = depth + 1)(newPSDepths, newCLDepths)

    //println("NP " + newPlan.planStepTasks.length + " of " + newPlan.numberOfPlanSteps)
    //println("C " + modification.getClass + " " + modification.addedPlanSteps.length + " " + modification.addedOrderings.length)

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

  lazy val tasksOfPresentPlanSteps = (Range(2, numberOfAllPlanSteps) filter isPlanStepPresentInPlan map planStepTasks).distinct.toArray

  lazy val remainingAccessiblePrimitiveTasks: Array[Int] = {
    val reachable = new mutable.BitSet()
    val (primitives, abstracts) = tasksOfPresentPlanSteps partition { domain.tasks(_).isPrimitive }
    reachable ++= primitives

    reachable ++= (abstracts flatMap domain.taskSchemaTransitionGraph.reachable filter { domain.tasks(_).isPrimitive })

    reachable.toArray
  }

  lazy val (reachablePositivePredicatesBasesOnPrimitives, reachablePrimitives) = {
    val state = new mutable.BitSet()
    val applicable = new mutable.BitSet()
    val init = domain.tasks(planStepTasks(0))


    var i = 0
    while (i < init.effect.length) {
      if (init.effect(i).isPositive)
        state.add(init.effect(i).predicate)
      i += 1
    }

    var potentialActions: Array[Int] = remainingAccessiblePrimitiveTasks
    var numberOfPotentialActions = potentialActions.length

    var changed = true
    while (changed) {
      val newPotentialActions = new Array[Int](numberOfPotentialActions)
      var newNumberOfPotentialActions = 0

      val oldStateSize = state.size

      // iterate through all
      i = 0
      while (i < numberOfPotentialActions) {
        val taskIndex = potentialActions(i)
        val task = domain.tasks(taskIndex)

        // check for executability
        var allPreconditionsTrue = true
        var prec = 0
        while (prec < task.precondition.length && allPreconditionsTrue) {
          allPreconditionsTrue &= state.contains(task.precondition(prec).predicate)
          prec += 1
        }

        if (allPreconditionsTrue) {
          // apply the task
          applicable.add(taskIndex)
          // add effects to the state
          var eff = 0
          while (eff < task.effect.length) {
            if (task.effect(eff).isPositive) state.add(task.effect(eff).predicate)
            eff += 1
          }

        } else {
          // keep it for the next round
          newPotentialActions(newNumberOfPotentialActions) = taskIndex
          newNumberOfPotentialActions += 1
        }


        i += 1
      }

      potentialActions = newPotentialActions
      numberOfPotentialActions = newNumberOfPotentialActions
      changed = oldStateSize != state.size
    }

    (state.toArray, applicable.toArray)
  }

  lazy val goalPotentiallyReachable: Boolean = {
    val goalPredicates = domain.tasks(planStepTasks(1)).precondition filter { _.isPositive } map { _.predicate } toSet

    goalPredicates subsetOf reachablePositivePredicatesBasesOnPrimitives.toSet
  }

  lazy val allContainedApplicable: Boolean = {
    val primitives = Range(2, numberOfAllPlanSteps) filter isPlanStepPresentInPlan map planStepTasks filter { domain.tasks(_).isPrimitive }

    primitives forall { reachablePrimitives.contains }
  }


  lazy val taskAllowed = domain.taskSchemaTransitionGraph.allowedTasksFromPrimitives(BitSet(reachablePrimitives: _*))

  lazy val landmarkMap = domain.taskSchemaTransitionGraph.landMarkFromPrimitives(BitSet(reachablePrimitives: _*))(taskAllowed)

  lazy val allLandmarks: BitSet = {
    val abstracts = Range(2, numberOfAllPlanSteps) filter isPlanStepPresentInPlan map planStepTasks filter { domain.tasks(_).isAbstract }
    BitSet(abstracts flatMap landmarkMap: _*)
  }

  lazy val simpleLandMark: BitSet = {
    val lm = domain.taskSchemaTransitionGraph.landMarkFromPrimitives(BitSet(domain.tasks.indices: _*))()

    //println((lm zip landmarkMap zip taskAllowed) map {case ((a,b),all) => if (all) b.size - a.size else 0} sum)

    val abstracts = Range(2, numberOfAllPlanSteps) filter isPlanStepPresentInPlan map planStepTasks filter { domain.tasks(_).isAbstract }
    BitSet(abstracts flatMap lm: _*)
  }

  lazy val allAbstractTasksAllowed: Boolean = {
    val abstracts = Range(2, numberOfAllPlanSteps) filter isPlanStepPresentInPlan map planStepTasks filter { domain.tasks(_).isAbstract }
    abstracts forall taskAllowed
  }


  lazy val allLandmarksApplicable: Boolean = {
    allLandmarks filter { domain.tasks(_).isPrimitive } forall reachablePrimitives.contains
  }
}

object EfficientPlan {

  val useIncrementalConstruction = false

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

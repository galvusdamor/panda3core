package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.domain.EfficientExtractedMethodPlan
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientDecomposePlanStep(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, decomposePlanStep: Int,
                                      override val addedPlanSteps: Array[(Int, Array[Int], Int, Int, Int)],
                                      override val addedVariableSorts: Array[Int],
                                      override val addedVariableConstraints: Array[EfficientVariableConstraint],
                                      override val addedCausalLinks: Array[EfficientCausalLink],
                                      override val nonInducedAddedOrderings: Array[(Int, Int)],
                                      override val decomposedPlanStepsByMethod: Array[(Int, Int)]
                                     ) extends EfficientModification {
  assert(decomposedPlanStepsByMethod.length == 1)

  def severLinkToPlan(severedFlaw: EfficientFlaw): EfficientModification = EfficientDecomposePlanStep(null, severedFlaw, decomposePlanStep, addedPlanSteps, addedVariableSorts,
                                                                                                      addedVariableConstraints, addedCausalLinks, nonInducedAddedOrderings,
                                                                                                      decomposedPlanStepsByMethod)

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "Decompose PS " + decomposedPlanSteps + " with " + addedPlanSteps.mkString("(", ",", ")")
}

object EfficientDecomposePlanStep {

  private def getNewVariableID(plan: EfficientPlan, decomposedPS: Int, oldID: Int): Int =
    if (oldID < plan.planStepParameters(decomposedPS).length)
      plan.planStepParameters(decomposedPS)(oldID)
    else
      oldID + plan.firstFreeVariableID - plan.planStepParameters(decomposedPS).length


  private def applyMethodToPlanWithLinks(buffer: ArrayBuffer[EfficientModification], plan: EfficientPlan, resolvedFlaw: EfficientFlaw, decomposedPS: Int, method:
  EfficientExtractedMethodPlan, methodIndex: Int, inheritedCausalLinks: Array[EfficientCausalLink], variableConstraintsForInheritedLinks: Array[EfficientVariableConstraint]): Unit = {

    // create new instances of the plan steps
    val addedPlanSteps: Array[(Int, Array[Int], Int, Int, Int)] = new Array[(Int, Array[Int], Int, Int, Int)](method.addedPlanSteps.length)
    var ps = 0
    while (ps < method.addedPlanSteps.length) {
      val oldParameters = method.addedPlanSteps(ps)._2
      val parameters = new Array[Int](oldParameters.length)
      var parameter = 0
      while (parameter < parameters.length) {
        parameters(parameter) = getNewVariableID(plan, decomposedPS, oldParameters(parameter))
        parameter += 1
      }
      addedPlanSteps(ps) = (method.addedPlanSteps(ps)._1, parameters, -1, decomposedPS, ps + 2)
      ps += 1
    }

    // create new instances of the variable constraints
    val addedVariableSorts: Array[Int] = method.addedVariableSorts
    val addedVariableConstraints: Array[EfficientVariableConstraint] =
      new Array[EfficientVariableConstraint](method.addedVariableConstraints.length + variableConstraintsForInheritedLinks.length)
    var constraint = 0
    // those contained inside the method
    while (constraint < method.addedVariableConstraints.length) {
      val oldConstraint = method.addedVariableConstraints(constraint)
      val newVariableOfVariable = getNewVariableID(plan, decomposedPS, oldConstraint.variable)
      val newVariableOfOther = getNewVariableID(plan, decomposedPS, oldConstraint.other)

      if (oldConstraint.constraintType == EfficientVariableConstraint.EQUALVARIABLE || oldConstraint.constraintType == EfficientVariableConstraint.UNEQUALVARIABLE) {
        addedVariableConstraints(constraint) = EfficientVariableConstraint(oldConstraint.constraintType, newVariableOfVariable, newVariableOfOther)
      } else {
        // other is a constant or a sort so just keep it
        addedVariableConstraints(constraint) = EfficientVariableConstraint(oldConstraint.constraintType, newVariableOfVariable, oldConstraint.other)
      }
      constraint += 1
    }
    // those necessary to inherit the causal links
    while (constraint < addedVariableConstraints.length) {
      addedVariableConstraints(constraint) = variableConstraintsForInheritedLinks(constraint - method.addedVariableConstraints.length)
      constraint += 1
    }


    // causal links, either contained inside the method or added due to inheritances
    val addedCausalLinks: Array[EfficientCausalLink] = new Array[EfficientCausalLink](method.addedCausalLinks.length + inheritedCausalLinks.length)
    var causalLink = 0
    while (causalLink < method.addedCausalLinks.length) {
      val oldCausalLink = method.addedCausalLinks(causalLink)
      addedCausalLinks(causalLink) = EfficientCausalLink(oldCausalLink.producer + plan.firstFreePlanStepID, oldCausalLink.consumer + plan.firstFreePlanStepID, oldCausalLink
        .conditionIndexOfProducer, oldCausalLink.conditionIndexOfConsumer)
      causalLink += 1
    }
    // copy the inherited Links
    while (causalLink < addedCausalLinks.length) {
      addedCausalLinks(causalLink) = inheritedCausalLinks(causalLink - method.addedCausalLinks.length)
      causalLink += 1
    }


    // compute inherited ordering constraints
    val inheritedOrderingConstraintsBuffer = new ArrayBuffer[Int]()
    var planStep = 2
    while (planStep < plan.numberOfAllPlanSteps) {
      if (planStep != decomposedPS && plan.isPlanStepPresentInPlan(planStep)) {
        if (plan.ordering.lt(planStep, decomposedPS)) inheritedOrderingConstraintsBuffer append -planStep
        if (plan.ordering.lt(decomposedPS, planStep)) inheritedOrderingConstraintsBuffer append planStep
      }
      planStep += 1
    }
    val inheritedOrderingConstraints = inheritedOrderingConstraintsBuffer.toArray

    // TODO this adds transitively implied orderings as well as those implied by causal links. It might be faster to transfer the ordering matrix directly ...
    val nonInducedAddedOrderings: Array[(Int, Int)] = new Array[(Int, Int)](method.nonInducedAddedOrderings.length + method.addedPlanSteps.length * inheritedOrderingConstraints.length)
    var ordering = 0
    while (ordering < method.nonInducedAddedOrderings.length) {
      val oldOrdering = method.nonInducedAddedOrderings(ordering)
      nonInducedAddedOrderings(ordering) = (oldOrdering._1 + plan.firstFreePlanStepID, oldOrdering._2 + plan.firstFreePlanStepID)
      ordering += 1
    }


    ps = 0
    while (ps < method.addedPlanSteps.length) {
      var inheritedPS = 0
      while (inheritedPS < inheritedOrderingConstraints.length) {
        nonInducedAddedOrderings(method.nonInducedAddedOrderings.length + ps * inheritedOrderingConstraints.length + inheritedPS) =
          if (inheritedOrderingConstraints(inheritedPS) < 0) (-inheritedOrderingConstraints(inheritedPS), ps + plan.firstFreePlanStepID)
          else (ps + plan.firstFreePlanStepID, inheritedOrderingConstraints(inheritedPS))
        inheritedPS += 1
      }
      ps += 1
    }



    // add the constructed modification to the buffer
    buffer append
      EfficientDecomposePlanStep(plan, resolvedFlaw, decomposedPS, addedPlanSteps, addedVariableSorts, addedVariableConstraints, addedCausalLinks, nonInducedAddedOrderings,
                                 Array((decomposedPS, methodIndex)))
  }


  // iterate through all possibilities to inherit the causal links
  // scalastyle:off parameter.number
  private def applyMethodToPlan(buffer: ArrayBuffer[EfficientModification], plan: EfficientPlan, resolvedFlaw: EfficientFlaw, decomposedPS: Int, method:
  EfficientExtractedMethodPlan, methodIndex: Int, currentPrecondition: Int, currentEffect: Int, skipFirstNCausalLinks: Int, inheritedLinks: mutable.ArrayStack[EfficientCausalLink],
                                inheritedLinksVariableConstraints: mutable.ArrayStack[EfficientVariableConstraint]): Unit =
    if (currentPrecondition < plan.domain.tasks(plan.planStepTasks(decomposedPS)).precondition.length || currentEffect < plan.domain.tasks(plan.planStepTasks(decomposedPS)).effect.length) {
      // determine the mode
      val linkIngoing = currentPrecondition < plan.domain.tasks(plan.planStepTasks(decomposedPS)).precondition.length
      val linkOutgoing = !linkIngoing
      val preconditionOrEffectIndex = if (linkIngoing) currentPrecondition else currentEffect
      val nextPrecondition = if (linkIngoing) currentPrecondition + 1 else currentPrecondition
      val nextEffect = if (linkIngoing) currentEffect else currentEffect + 1

      // determine whether there actually is an ingoing link to this action
      var planLinksCounter = 0
      var planLinkIndex = -1
      var linksSkipped = 0
      while (planLinksCounter < plan.causalLinks.length && planLinkIndex == -1) {
        if (plan.causalLinks(planLinksCounter).consumerOrProducer(isProducer = linkOutgoing) == decomposedPS &&
          plan.planStepDecomposedByMethod(plan.causalLinks(planLinksCounter).consumerOrProducer(isProducer = !linkOutgoing)) == -1 &&
          plan.causalLinks(planLinksCounter).consumerOrProducerIndex(isProducer = linkOutgoing) == preconditionOrEffectIndex) {

          if (linksSkipped == skipFirstNCausalLinks)
            planLinkIndex = planLinksCounter
          else linksSkipped += 1
        }
        planLinksCounter += 1
      }

      // get matching internal supporters
      val internalSupporters = if (linkIngoing) method.ingoingSupporters(preconditionOrEffectIndex) else method.outgoingSupporters(preconditionOrEffectIndex)
      val internalSupportersNecessary =
        if (linkIngoing) method.ingoingSupportersContainNecessary(preconditionOrEffectIndex) else method.outgoingSupportersContainNecessary(preconditionOrEffectIndex)


      if (!(planLinkIndex == -1 && internalSupportersNecessary)) {
        if (planLinkIndex == -1)
          applyMethodToPlan(buffer, plan, resolvedFlaw, decomposedPS, method, methodIndex, nextPrecondition, nextEffect, 0, inheritedLinks, inheritedLinksVariableConstraints)
        else {
          val originalCausalLink = plan.causalLinks(planLinkIndex)
          val outsidePlanStep = if (linkIngoing) originalCausalLink.producer else originalCausalLink.consumer
          val outsideTask = plan.domain.tasks(plan.planStepTasks(outsidePlanStep))
          val linkLiteral = if (linkIngoing) outsideTask.effect(originalCausalLink.conditionIndexOfProducer) else outsideTask.precondition(originalCausalLink.conditionIndexOfConsumer)
          val outsideLiteralArguments = outsideTask.getArgumentsOfLiteral(plan.planStepParameters(outsidePlanStep), linkLiteral)

          // inherit the link any way possible
          var indexOfSupporter = 0
          while (indexOfSupporter < internalSupporters.length) {
            val newPlanStepSchema = plan.domain.tasks(method.addedPlanSteps(internalSupporters(indexOfSupporter).planStep)._1)
            val newPlanStepArguments = method.addedPlanSteps(internalSupporters(indexOfSupporter).planStep)._2
            val newPlanStep = internalSupporters(indexOfSupporter).planStep + plan.firstFreePlanStepID
            val newPlanStepPrecEff = internalSupporters(indexOfSupporter).conditionIndex
            val newPlanStepLiteral = if (linkIngoing) newPlanStepSchema.precondition(newPlanStepPrecEff) else newPlanStepSchema.effect(newPlanStepPrecEff)


            val inheritedCausalLink =
              if (linkIngoing) EfficientCausalLink(outsidePlanStep, newPlanStep, originalCausalLink.conditionIndexOfProducer, newPlanStepPrecEff)
              else EfficientCausalLink(newPlanStep, outsidePlanStep, newPlanStepPrecEff, originalCausalLink.conditionIndexOfConsumer)

            val consumerLiteralArguments = newPlanStepSchema.getArgumentsOfLiteral(newPlanStepArguments, newPlanStepLiteral)
            // generate the mgu
            var linkParameter = 0
            while (linkParameter < linkLiteral.parameterVariables.length) {
              inheritedLinksVariableConstraints push EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, outsideLiteralArguments(linkParameter),
                                                                                 getNewVariableID(plan, decomposedPS, consumerLiteralArguments(linkParameter)))
              linkParameter += 1
            }
            inheritedLinks.push(inheritedCausalLink)
            applyMethodToPlan(buffer, plan, resolvedFlaw, decomposedPS, method, methodIndex, currentPrecondition, currentEffect, skipFirstNCausalLinks + 1,
                              inheritedLinks, inheritedLinksVariableConstraints)
            // pop everything from the stack an continue
            inheritedLinks.pop()
            linkParameter = 0
            while (linkParameter < linkLiteral.parameterVariables.length) {
              inheritedLinksVariableConstraints.pop()
              linkParameter += 1
            }

            indexOfSupporter += 1
          }
        }
      } else {
        // TODO: what to do here? We have no ingoing link but a rule how to inherit it ?!
      }
    } else {
      // end of the recursive decent, actually trigger generating the modification
      applyMethodToPlanWithLinks(buffer, plan, resolvedFlaw, decomposedPS, method, methodIndex, inheritedLinks.toArray, inheritedLinksVariableConstraints.toArray)
    }


  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, planStep: Int): Array[EfficientModification] = apply(plan, resolvedFlaw, planStep, -1, false)

  /**
    * @param targetPredicate -1 signalises that there is no target predicate and all predicates should be examined
    */
  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, planStep: Int, targetPredicate: Int, predicatePositive: Boolean): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()

    val possibleMethods = plan.domain.taskToPossibleMethods(plan.planStepTasks(planStep))
    val literal = 2 * targetPredicate + (if (predicatePositive) 0 else 1)


    var possibleMethodIndex = 0
    while (possibleMethodIndex < possibleMethods.length) {
      if (targetPredicate == -1 || plan.domain.methodCanSupportLiteral(possibleMethods(possibleMethodIndex)._2)(literal)) {
        val method = possibleMethods(possibleMethodIndex)._1
        val methodExtract = method.extract
        val methodIndex = possibleMethods(possibleMethodIndex)._2

        // check whether parameter of abstract task are compatible
        var parameter = 0
        var parameterOK = method.subPlan.variableConstraints.potentiallyConsistent
        val abstractTaskParameter = plan.planStepParameters(planStep)
        while (parameter < abstractTaskParameter.length) {
          if (!plan.variableConstraints.isRepresentativeAVariable(abstractTaskParameter(parameter))) {
            val boundToConstant = plan.variableConstraints.getRepresentativeConstant(abstractTaskParameter(parameter))
            parameterOK &= method.subPlan.variableConstraints.getRemainingDomain(parameter) contains boundToConstant
          }
          parameter += 1
        }

        if (parameterOK)
        // generate all causal link inheritances
          applyMethodToPlan(buffer, plan, resolvedFlaw, planStep, methodExtract, methodIndex, 0, 0, 0,
                            new mutable.ArrayStack[EfficientCausalLink](), new mutable.ArrayStack[EfficientVariableConstraint]())
      }
      possibleMethodIndex += 1
    }
    buffer.toArray
  }

  def estimate(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, planStep: Int, targetPredicate: Int, predicatePositive: Boolean): Int = {
    val possibleMethods = plan.domain.taskToPossibleMethods(plan.planStepTasks(planStep))
    val literal = 2 * targetPredicate + (if (predicatePositive) 0 else 1)

    val hasLinkIngoing = Array.fill[Int](plan.domain.tasks(plan.planStepTasks(planStep)).precondition.length)(0)
    val hasLinkOutgoing = Array.fill[Int](plan.domain.tasks(plan.planStepTasks(planStep)).effect.length)(0)

    var causalLink = 0
    while (causalLink < plan.causalLinks.length) {
      val producer = plan.causalLinks(causalLink).producer
      val consumer = plan.causalLinks(causalLink).consumer
      if (plan.planStepDecomposedByMethod(producer) == -1 && plan.planStepDecomposedByMethod(consumer) == -1) {
        if (producer == planStep) hasLinkOutgoing(plan.causalLinks(causalLink).conditionIndexOfProducer) += 1
        else if (consumer == planStep) hasLinkIngoing(plan.causalLinks(causalLink).conditionIndexOfConsumer) += 1
      }
      causalLink += 1
    }

    var numberOfModifications = 0
    var possibleMethodIndex = 0
    while (possibleMethodIndex < possibleMethods.length) {
      if (targetPredicate == -1 || plan.domain.methodCanSupportLiteral(possibleMethods(possibleMethodIndex)._2)(literal)) {
        val method = possibleMethods(possibleMethodIndex)._1.extract

        var numberOfLinkInheritances = 1 // change by multiplication

        // preconditions
        var abstractPrecondition = 0
        while (abstractPrecondition < plan.taskOfPlanStep(planStep).precondition.length) {
          var linkConnected: Int = 0
          var causalLinkIndex = 0
          while (causalLinkIndex < plan.causalLinks.length) {
            val causalLink = plan.causalLinks(causalLinkIndex)
            if (causalLink.consumer == planStep && plan.isPlanStepPresentInPlan(causalLink.producer)) linkConnected += 1
            causalLinkIndex += 1
          }
          if (method.ingoingSupportersContainNecessary(abstractPrecondition) && linkConnected == 0) numberOfLinkInheritances = 0

          // TODO these should be pruned from the domain ... at some point in time
          //assert(method.ingoingSupporters(abstractPrecondition).length != 0)
          if (method.ingoingSupporters(abstractPrecondition).length == 0) {
            if (linkConnected != 0) numberOfLinkInheritances = 0
          } else
            numberOfLinkInheritances *= Math.pow(method.ingoingSupporters(abstractPrecondition).length, linkConnected).round.toInt

          abstractPrecondition += 1
        }

        // effects
        var abstractEffect = 0
        while (abstractEffect < plan.taskOfPlanStep(planStep).effect.length) {
          var linkConnected: Int = 0
          var causalLinkIndex = 0
          while (causalLinkIndex < plan.causalLinks.length) {
            val causalLink = plan.causalLinks(causalLinkIndex)
            if (causalLink.producer == planStep && plan.isPlanStepPresentInPlan(causalLink.consumer)) linkConnected += 1
            causalLinkIndex += 1
          }
          if (method.outgoingSupportersContainNecessary(abstractEffect) && linkConnected == 0) numberOfLinkInheritances = 0

          // TODO these should be pruned from the domain ... at some point in time
          //assert(method.outgoingSupporters(abstractEffect).length != 0)
          if (method.outgoingSupporters(abstractEffect).length == 0) {
            if (linkConnected != 0) numberOfLinkInheritances = 0
          } else
            numberOfLinkInheritances *= Math.pow(method.outgoingSupporters(abstractEffect).length, linkConnected).round.toInt
          abstractEffect += 1
        }

        numberOfModifications += numberOfLinkInheritances
      }
      possibleMethodIndex += 1
    }

    numberOfModifications
  }
}
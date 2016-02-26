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
                                      override val addedPlanSteps: Array[(Int, Array[Int], Int, Int)],
                                      override val addedVariableSorts: Array[Int],
                                      override val addedVariableConstraints: Array[EfficientVariableConstraint],
                                      override val addedCausalLinks: Array[EfficientCausalLink],
                                      override val nonInducedAddedOrderings: Array[(Int, Int)],
                                      override val decomposedPlanStepsByMethod: Array[(Int, Int)]
                                     ) extends EfficientModification {
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
    val addedPlanSteps: Array[(Int, Array[Int], Int, Int)] = new Array[(Int, Array[Int], Int, Int)](method.addedPlanSteps.length)
    var ps = 0
    while (ps < method.addedPlanSteps.length) {
      val oldParameters = method.addedPlanSteps(ps)._2
      val parameters = new Array[Int](oldParameters.length)
      var parameter = 0
      while (parameter < parameters.length) {
        parameters(parameter) = getNewVariableID(plan, decomposedPS, oldParameters(parameter))
        parameter += 1
      }
      addedPlanSteps(ps) = (method.addedPlanSteps(ps)._1, method.addedPlanSteps(ps)._2, -1, decomposedPS)
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
      }else {// other is a constant or a sort so just keep it
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

    // TODO this adds transitively implied orderings as well as those implied be causal links. It might be faster to transfer the ordering matrix directly ...
    val nonInducedAddedOrderings: Array[(Int, Int)] = new Array[(Int, Int)](method.nonInducedAddedOrderings.length)
    var ordering = 0
    while (ordering < nonInducedAddedOrderings.length) {
      val oldOrdering = method.nonInducedAddedOrderings(ordering)
      nonInducedAddedOrderings(ordering) = (oldOrdering._1 + plan.firstFreePlanStepID, oldOrdering._2 + plan.firstFreePlanStepID)
      ordering += 1
    }

    // add the constructed modification to the buffer
    buffer append
      EfficientDecomposePlanStep(plan, resolvedFlaw, decomposedPS, addedPlanSteps, addedVariableSorts, addedVariableConstraints, addedCausalLinks, nonInducedAddedOrderings,
                                 Array((decomposedPS, methodIndex)))
  }


  // iterate through all possibilities to inherit the causal links
  // scalastyle:off parameter.number
  private def applyMethodToPlan(buffer: ArrayBuffer[EfficientModification], plan: EfficientPlan, resolvedFlaw: EfficientFlaw, decomposedPS: Int, method:
  EfficientExtractedMethodPlan, methodIndex: Int, currentPrecondition: Int, currentEffect: Int, inheritedLinks: mutable.ArrayStack[EfficientCausalLink],
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
      while (planLinksCounter < plan.causalLinks.length && planLinkIndex == -1) {
        if (plan.causalLinks(planLinksCounter).consumerOrProducer(isProducer = linkOutgoing) == decomposedPS &&
          plan.causalLinks(planLinksCounter).consumerOrProducerIndex(isProducer = linkOutgoing) == preconditionOrEffectIndex)
          planLinkIndex = planLinksCounter
        planLinksCounter += 1
      }

      // determine whether there is a matching ingoing link
      var linkCounter = 0
      var isLink = false
      val relevantLinks = if (linkIngoing) method.ingoingLinks else method.outgoingLinks
      while (linkCounter < relevantLinks.length) {
        isLink |= relevantLinks(linkCounter).consumerOrProducerIndex(isProducer = linkIngoing) == preconditionOrEffectIndex
        linkCounter += 1
      }

      // case 1: there is an ingoing link using the precondition
      if (isLink || planLinkIndex == -1) {
        if ((isLink && planLinkIndex != -1) || planLinkIndex == -1)
          applyMethodToPlan(buffer, plan, resolvedFlaw, decomposedPS, method, methodIndex, nextPrecondition, nextEffect, inheritedLinks, inheritedLinksVariableConstraints)
        // if there is no ingoing link this decomposition is invalid
        // TODO: discuss whether we actually want to dismiss this case
      } else {
        val originalCausalLink = plan.causalLinks(planLinkIndex)
        val outsidePlanStep = if (linkIngoing) originalCausalLink.producer else originalCausalLink.consumer
        val outsideTask = plan.domain.tasks(plan.planStepTasks(outsidePlanStep))
        val linkLiteral = if (linkIngoing) outsideTask.effect(originalCausalLink.conditionIndexOfProducer) else outsideTask.precondition(originalCausalLink.conditionIndexOfConsumer)
        val outsideLiteralArguments = outsideTask.getArgumentsOfLiteral(plan.planStepParameters(outsidePlanStep), linkLiteral)


        // case 2: we need to inherit the link to every possible input
        var newPlanStep = 0
        while (newPlanStep < method.addedPlanSteps.length) {
          val newPlanStepSchema = plan.domain.tasks(method.addedPlanSteps(newPlanStep)._1)
          val newPlanStepArguments = method.addedPlanSteps(newPlanStep)._2

          var newPlanStepPrecEff = 0
          val schemaPrecEff = if (linkIngoing) newPlanStepSchema.precondition else newPlanStepSchema.effect
          while (newPlanStepPrecEff < schemaPrecEff.length) {
            val literal = schemaPrecEff(newPlanStepPrecEff)
            if (linkLiteral.checkPredicateAndSign(literal)) {
              // a potential supporter, generate the causal link and decent
              val inheritedCausalLink =
                if (linkIngoing) EfficientCausalLink(outsidePlanStep, newPlanStep + plan.firstFreePlanStepID, originalCausalLink.conditionIndexOfProducer, newPlanStepPrecEff)
                else EfficientCausalLink(newPlanStep + plan.firstFreePlanStepID, outsidePlanStep, newPlanStepPrecEff, originalCausalLink.conditionIndexOfConsumer)

              val consumerLiteralArguments = newPlanStepSchema.getArgumentsOfLiteral(newPlanStepArguments, literal)
              // generate the mgu
              var linkParameter = 0
              while (linkParameter < linkLiteral.parameterVariables.length) {
                inheritedLinksVariableConstraints push EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, outsideLiteralArguments(linkParameter),
                                                                                   getNewVariableID(plan, decomposedPS, consumerLiteralArguments(linkParameter)))
                linkParameter += 1
              }
              inheritedLinks.push(inheritedCausalLink)
              applyMethodToPlan(buffer, plan, resolvedFlaw, decomposedPS, method, methodIndex, nextPrecondition, nextEffect, inheritedLinks, inheritedLinksVariableConstraints)
              // pop everything from the stack an continue
              inheritedLinks.pop()
              linkParameter = 0
              while (linkParameter < linkLiteral.parameterVariables.length) {
                inheritedLinksVariableConstraints.pop()
                linkParameter += 1
              }
            }
            newPlanStepPrecEff += 1
          }
          newPlanStep += 1
        }
      }
    } else {
      // end of the recursive decent, actually trigger generating the modification
      applyMethodToPlanWithLinks(buffer, plan, resolvedFlaw, decomposedPS, method, methodIndex, inheritedLinks.toArray, inheritedLinksVariableConstraints.toArray)
    }


  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, planStep: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()

    val possibleMethods = plan.domain.taskToPossibleMethods(plan.planStepTasks(planStep))

    var possibleMethodIndex = 0
    while (possibleMethodIndex < possibleMethods.length) {
      val method = possibleMethods(possibleMethodIndex)._1.extract
      val methodIndex = possibleMethods(possibleMethodIndex)._2

      // generate all causal link inheritances
      applyMethodToPlan(buffer, plan, resolvedFlaw, planStep, method, methodIndex, 0, 0, new mutable.ArrayStack[EfficientCausalLink](), new mutable.ArrayStack[EfficientVariableConstraint]())

      possibleMethodIndex += 1
    }
    buffer.toArray
  }
}
package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.domain.EfficientExtractedMethodPlan
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientFlaw

import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientDecomposePlanStep(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, decomposePlanStep: Int,
                                      override val addedPlanSteps: Array[(Int, Array[Int], Int, Int)],
                                      override val addedVariableSorts: Array[Int],
                                      override val addedVariableConstraints: Array[EfficientVariableConstraint],
                                      override val addedCausalLinks: Array[EfficientCausalLink],
                                      override val nonInducedAddedOrderings: Array[(Int, Int)]
                                     ) extends EfficientModification {
}

object EfficientDecomposePlanStep {

  private def getNewVariableID(plan: EfficientPlan, decomposedPS: Int, oldID: Int): Int =
    if (oldID < plan.planStepParameters(decomposedPS).length)
      plan.planStepParameters(decomposedPS)(oldID)
    else
      oldID + plan.firstFreeVariableID - plan.planStepParameters(decomposedPS).length


  private def applyMethodToPlanWithLinks(buffer: ArrayBuffer[EfficientModification], plan: EfficientPlan, resolvedFlaw: EfficientFlaw, decomposedPS: Int, method:
  EfficientExtractedMethodPlan, methodIndex: Int): () = {

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
    val addedVariableConstraints: Array[EfficientVariableConstraint] = new Array[EfficientVariableConstraint](method.addedVariableConstraints.length)
    var constraint = 0
    while (constraint < addedVariableConstraints.length) {
      val oldConstraint = method.addedVariableConstraints(constraint)
      val newVariableOfVariable = getNewVariableID(plan, decomposedPS, oldConstraint.variable)
      val newVariableOfOther = getNewVariableID(plan, decomposedPS, oldConstraint.other)

      if (oldConstraint.constraintType == EfficientVariableConstraint.EQUALVARIABLE || oldConstraint.constraintType == EfficientVariableConstraint.UNEQUALVARIABLE)
        addedVariableConstraints(constraint) = EfficientVariableConstraint(oldConstraint.constraintType, newVariableOfVariable, newVariableOfOther)
      else // other is a constant or a sort so just keep it
        addedVariableConstraints(constraint) = EfficientVariableConstraint(oldConstraint.constraintType, newVariableOfVariable, oldConstraint.other)

      constraint += 1
    }

    //
    // TODO this copies only the links from _inside_ the method
    val addedCausalLinks: Array[EfficientCausalLink] = new Array[EfficientCausalLink](method.addedCausalLinks.length)
    var causalLink = 0
    while (causalLink < addedCausalLinks.length) {
      val oldCausalLink = method.addedCausalLinks(causalLink)
      addedCausalLinks(causalLink) = EfficientCausalLink(oldCausalLink.producer + plan.firstFreePlanStepID, oldCausalLink.consumer + plan.firstFreePlanStepID, oldCausalLink
        .conditionIndexOfProducer, oldCausalLink.conditionIndexOfConsuer)
      causalLink += 1
    }

    val nonInducedAddedOrderings: Array[(Int, Int)] = new Array[(Int, Int)](method.nonInducedAddedOrderings.length)
    var ordering = 0
    while (ordering < nonInducedAddedOrderings.length) {
      val oldOrdering = method.nonInducedAddedOrderings(ordering)
      nonInducedAddedOrderings(ordering) = (oldOrdering._1 + plan.firstFreePlanStepID, oldOrdering._2 + plan.firstFreePlanStepID)
      ordering += 1
    }

    buffer append EfficientDecomposePlanStep(plan, resolvedFlaw, decomposedPS, addedPlanSteps, addedVariableSorts, addedVariableConstraints, addedCausalLinks, nonInducedAddedOrderings)
  }


  def apply(plan: EfficientPlan, resolvedFlaw: EfficientFlaw, planStep: Int): Array[EfficientModification] = {
    val buffer = new ArrayBuffer[EfficientModification]()

    val possibleMethods = plan.domain.taskToPossibleMethods(plan.planStepTasks(planStep))

    var possibleMethodIndex = 0
    while (possibleMethodIndex < possibleMethods.length) {
      val method = possibleMethods(possibleMethodIndex)._1.extract
      val methodIndex = possibleMethods(possibleMethodIndex)._2


      // TODO generate all causal link inheritances
      applyMethodToPlanWithLinks(buffer, plan, resolvedFlaw, planStep, method, methodIndex)

      possibleMethodIndex += 1
    }
    buffer.toArray
  }
}
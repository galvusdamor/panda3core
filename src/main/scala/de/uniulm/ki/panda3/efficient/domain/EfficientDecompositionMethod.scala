package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientDecompositionMethod(abstractTask: Int, subPlan: EfficientPlan) {

  val extract: EfficientExtractedMethodPlan = {
    val planSteps: Array[(Int, Array[Int])] = (2 until subPlan.planStepTasks.length map { case ps => (subPlan.planStepTasks(ps), subPlan.planStepParameters(ps)) }).toArray
    val innerCausalLinks = subPlan.causalLinks filter { cl => cl.consumer != 1 && cl.producer != 0 }
    val ingoingLinks = subPlan.causalLinks filter { _.producer == 0 }
    val outgoingLinks = subPlan.causalLinks filter { _.consumer == 1 }
    // TODO: remove the ones induced by causal links
    val orderings = subPlan.ordering.minimalOrderingConstraintsWithoutInitAndGoal()

    // find best sort for variables
    val newVariableSorts = (2 until subPlan.firstFreeVariableID map { v =>
      val possibleValuesOfVariable = subPlan.variableConstraints.getRemainingDomain(v)
      // select the smallest (judged by the number of its constants) possible sort
      (subPlan.domain.constantsOfSort.zipWithIndex filter { case (constants, _) => possibleValuesOfVariable forall { constants contains _ } } sortBy { _._1.length }).head._2
    }).toArray

    val isEqualToConstant = for (i <- 0 until subPlan.firstFreeVariableID if !subPlan.variableConstraints.isRepresentativeAVariable(i))
      yield EfficientVariableConstraint(EfficientVariableConstraint.EQUALCONSTANT, i, subPlan.variableConstraints.getRepresentativeConstant(i))

    val necessaryEqualConstraints =
      for (i <- 0 until subPlan.firstFreeVariableID;
           j <- i until subPlan.firstFreeVariableID
           if subPlan.variableConstraints.isRepresentativeAVariable(i) && subPlan.variableConstraints.areEqual(i, j))
        yield EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, i, j)

    val restrictPossibleValues = for (i <- 2 until subPlan.firstFreeVariableID if subPlan.variableConstraints.isRepresentativeAVariable(i)
                                      if subPlan.variableConstraints.getRepresentativeVariable(i) == i)
      yield subPlan.domain.constantsOfSort(newVariableSorts(i - 2)) diff subPlan.variableConstraints.getRemainingDomain(i).toSeq map {
        EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALCONSTANT, i, _)
      }

    val unequalConstraints = for (i <- 0 until subPlan.firstFreeVariableID if subPlan.variableConstraints.getRepresentativeVariable(i) == i;
                                  j <- subPlan.variableConstraints.getVariableUnequalTo(i)) yield EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, i, j)


    val allVariableConstraints = isEqualToConstant ++ necessaryEqualConstraints ++ restrictPossibleValues.flatten ++ unequalConstraints
    EfficientExtractedMethodPlan(planSteps, newVariableSorts, allVariableConstraints.toArray, innerCausalLinks, orderings, ingoingLinks, outgoingLinks)
  }
}


case class EfficientExtractedMethodPlan(addedPlanSteps: Array[(Int, Array[Int])],
                                        addedVariableSorts: Array[Int],
                                        addedVariableConstraints: Array[EfficientVariableConstraint],
                                        addedCausalLinks: Array[EfficientCausalLink],
                                        nonInducedAddedOrderings: Array[(Int, Int)],
                                        ingoingLinks: Array[EfficientCausalLink],
                                        outgoingLinks: Array[EfficientCausalLink]) {
}
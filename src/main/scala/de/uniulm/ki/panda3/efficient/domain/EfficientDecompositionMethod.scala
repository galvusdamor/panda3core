package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.{EfficientCSP, EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off null
case class EfficientDecompositionMethod(abstractTask: Int, subPlan: EfficientPlan) {

  lazy val extract: EfficientExtractedMethodPlan = {
    val planSteps: Array[(Int, Array[Int])] = (2 until subPlan.planStepTasks.length map { case ps => (subPlan.planStepTasks(ps), subPlan.planStepParameters(ps)) }).toArray
    val innerCausalLinks = subPlan.causalLinks filter { cl => cl.consumer != 1 && cl.producer != 0 } map {
      case EfficientCausalLink(p, c, pi, ci) => EfficientCausalLink(p - 2, c - 2, pi, ci)
    }

    // compute necessary supporters
    val ingoingSupporters: Array[Array[PotentialLinkSupporter]] = new Array[Array[PotentialLinkSupporter]](subPlan.domain.tasks(abstractTask).precondition.length)
    val outgoingSupporters: Array[Array[PotentialLinkSupporter]] = new Array[Array[PotentialLinkSupporter]](subPlan.domain.tasks(abstractTask).effect.length)

    val ingoingLinks = subPlan.causalLinks filter { _.producer == 0 } map { case EfficientCausalLink(p, c, pi, ci) => EfficientCausalLink(p - 2, c - 2, pi, ci) }
    val outgoingLinks = subPlan.causalLinks filter { _.consumer == 1 } map { case EfficientCausalLink(p, c, pi, ci) => EfficientCausalLink(p - 2, c - 2, pi, ci) }

    ingoingLinks foreach { case EfficientCausalLink(_, c, pi, ci) =>
      ingoingSupporters(pi) = Array[PotentialLinkSupporter](PotentialLinkSupporter(c, ci, isNecessary = true))
    }

    val outgoingLinkSupporterMap: mutable.Map[Int, ArrayBuffer[PotentialLinkSupporter]] = mutable.HashMap().withDefaultValue(new ArrayBuffer[PotentialLinkSupporter]())
    outgoingLinks foreach { case EfficientCausalLink(p, _, pi, ci) =>
      outgoingLinkSupporterMap(ci) append PotentialLinkSupporter(p, pi, isNecessary = true)
    }
    outgoingLinkSupporterMap foreach { case (idx, buffer) => outgoingSupporters(idx) = buffer.toArray }


    // compute potential supporters
    ingoingSupporters.indices foreach { case preconditionIndex => if (ingoingSupporters(preconditionIndex) == null) {
      val abstractPrecondition = subPlan.domain.tasks(abstractTask).precondition(preconditionIndex)

      ingoingSupporters(preconditionIndex) = (Range(2, subPlan.numberOfAllPlanSteps) flatMap { case consumer =>
        val planStep = subPlan.taskOfPlanStep(consumer)
        planStep.precondition.zipWithIndex collect {
          case (literal, idx) if literal.checkPredicateAndSign(abstractPrecondition) &&
            ((planStep.getArgumentsOfLiteral(subPlan.planStepParameters(consumer), literal) zip abstractPrecondition.parameterVariables) forall { case (v1, v2) =>
              subPlan.variableConstraints.areCompatible(v1, v2) != EfficientCSP.INCOMPATIBLE
            }) =>
            PotentialLinkSupporter(consumer - 2, idx, isNecessary = false)
        }
      }).toArray
    }
    }

    outgoingSupporters.indices foreach { case effectIndex => if (outgoingSupporters(effectIndex) == null) {
      val abstractEffect = subPlan.domain.tasks(abstractTask).effect(effectIndex)
      outgoingSupporters(effectIndex) = (Range(2, subPlan.numberOfAllPlanSteps) flatMap { case producer =>
        val planStep = subPlan.taskOfPlanStep(producer)
        planStep.effect.zipWithIndex collect {
          case (literal, idx) if literal.checkPredicateAndSign(abstractEffect) &&
            ((planStep.getArgumentsOfLiteral(subPlan.planStepParameters(producer), literal) zip abstractEffect.parameterVariables) forall { case (v1, v2) =>
              subPlan.variableConstraints.areCompatible(v1, v2) != EfficientCSP.INCOMPATIBLE
            }) =>
            PotentialLinkSupporter(producer - 2, idx, isNecessary = false)
        }
      }).toArray
    }
    }


    // TODO: remove the ones induced by causal links
    val orderings = subPlan.ordering.orderingConstraints map { _ drop 2 } drop 2

    //val orderings = subPlan.ordering.minimalOrderingConstraintsWithoutInitAndGoal() map { case (a, b) => (a - 2, b - 2) }
    //println("DEC " + (subPlan.numberOfPlanSteps-2) + " " + orderings.length)


    val numberOfAbstractTaskParameters = subPlan.domain.tasks(abstractTask).parameterSorts.length
    // find best sort for variables
    val newVariableSorts = (numberOfAbstractTaskParameters until subPlan.firstFreeVariableID map { v =>
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

    val restrictPossibleValues = for (i <- numberOfAbstractTaskParameters until subPlan.firstFreeVariableID if subPlan.variableConstraints.isRepresentativeAVariable(i)
                                      if subPlan.variableConstraints.getRepresentativeVariable(i) == i)
      yield subPlan.domain.constantsOfSort(newVariableSorts(i - numberOfAbstractTaskParameters)) diff subPlan.variableConstraints.getRemainingDomain(i).toSeq map {
        EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALCONSTANT, i, _)
      }

    val unequalConstraints = for (i <- 0 until subPlan.firstFreeVariableID if subPlan.variableConstraints.isRepresentativeAVariable(i)
                                  if subPlan.variableConstraints.getRepresentativeVariable(i) == i;
                                  j <- subPlan.variableConstraints.getVariableUnequalTo(i)) yield EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, i, j)


    val allVariableConstraints = isEqualToConstant ++ necessaryEqualConstraints ++ restrictPossibleValues.flatten ++ unequalConstraints
    EfficientExtractedMethodPlan(planSteps, newVariableSorts, allVariableConstraints.toArray, innerCausalLinks, orderings, ingoingSupporters, outgoingSupporters)
  }

}

case class PotentialLinkSupporter(planStep: Int, conditionIndex: Int, isNecessary: Boolean) {
}

case class EfficientExtractedMethodPlan(addedPlanSteps: Array[(Int, Array[Int])],
                                        addedVariableSorts: Array[Int],
                                        addedVariableConstraints: Array[EfficientVariableConstraint],
                                        addedCausalLinks: Array[EfficientCausalLink],
                                        innerOrdering: Array[Array[Byte]],
                                        ingoingSupporters: Array[Array[PotentialLinkSupporter]],
                                        outgoingSupporters: Array[Array[PotentialLinkSupporter]]) {
  val ingoingSupportersContainNecessary : Array[Boolean] = ingoingSupporters map { arr => arr exists { _.isNecessary } }
  val outgoingSupportersContainNecessary: Array[Boolean] = outgoingSupporters map { arr => arr exists { _.isNecessary } }
}


case class EfficientGroundedDecompositionMethod(methodIndex: Int, methodArguments: Array[Int])
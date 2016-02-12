package de.uniulm.ki.panda3.efficient.plan.modification

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.domain.HasEfficientExampleDomain1
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
// scalastyle:off null
class EfficientInsertPlanStepTest extends FlatSpec with HasEfficientExampleDomain1 {

  "Computing insert plan step modifications" must "be correct if computed anew" in {
    val possibleNewPlanSteps = EfficientInsertPlanStepWithLink(simpleOpenPreconditionPlan, null, 2, 0)

    assert(possibleNewPlanSteps.length == 3)

    assert(possibleNewPlanSteps exists { case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
      val planStepOK = plan == simpleOpenPreconditionPlan && newPlanStep._1 == 2 && newPlanStep._2.length == 1 && newPlanStep._2(0) == 1 && newPlanStep._3 == -1 && newPlanStep._4 == -1
      val parameterOK = parameterVariableSorts sameElements Array(0)
      val causalLinkOK = causalLink == EfficientCausalLink(4, 2, 0, 0)
      val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 1, 0))) ||
        (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 1)))
      planStepOK && parameterOK && causalLinkOK && constraintOK
    })
    assert(possibleNewPlanSteps exists { case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
      val planStepOK = plan == simpleOpenPreconditionPlan && newPlanStep._1 == 5 && newPlanStep._2.length == 2 && newPlanStep._2(0) == 1 && newPlanStep._2(1) == 2 &&
        newPlanStep._3 == -1 && newPlanStep._4 == -1
      val parameterOK = parameterVariableSorts sameElements Array(0, 0)
      val causalLinkOK = causalLink == EfficientCausalLink(4, 2, 0, 0)
      val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 1, 0))) ||
        (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 1)))
      planStepOK && parameterOK && causalLinkOK && constraintOK
    })
    assert(possibleNewPlanSteps exists { case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
      val planStepOK = plan == simpleOpenPreconditionPlan && newPlanStep._1 == 5 && newPlanStep._2.length == 2 && newPlanStep._2(0) == 1 && newPlanStep._2(1) == 2 &&
        newPlanStep._3 == -1 && newPlanStep._4 == -1
      val parameterOK = parameterVariableSorts sameElements Array(0, 0)
      val causalLinkOK = causalLink == EfficientCausalLink(4, 2, 1, 0)
      val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 2, 0))) ||
        (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 2)))

      planStepOK && parameterOK && causalLinkOK && constraintOK
    })
  }
}
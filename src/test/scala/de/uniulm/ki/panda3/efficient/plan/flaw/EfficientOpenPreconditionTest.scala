package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.csp.EfficientVariableConstraint
import de.uniulm.ki.panda3.efficient.domain.HasEfficientExampleDomain1
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientInsertCausalLink}
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class EfficientOpenPreconditionTest extends FlatSpec with HasEfficientExampleDomain1 {

  "Modifications for Open Preconditions" must "be correct if computed anew" in {
    val flaw = (simpleOpenPreconditionPlan.openPreconditions find { _.planStep == 3 }).get
    val resolvers = flaw.resolver
    assert(resolvers.length == 4)

    assert(resolvers exists {
      case EfficientInsertCausalLink(_, _, link, conditions) =>
        link.producer == 2 && link.consumer == 3 && link.conditionIndexOfProducer == 0 && link.conditionIndexOfConsumer == 0 && conditions.length == 0
      case _                                                 => false
    })



    assert(resolvers exists {
      case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
        val planStepOK = plan == simpleOpenPreconditionPlan && newPlanStep._1 == 2 && newPlanStep._2.length == 1 && newPlanStep._2(0) == 1 && newPlanStep._3 == -1 && newPlanStep._4 == -1
        val parameterOK = parameterVariableSorts sameElements Array(0)
        val causalLinkOK = causalLink == EfficientCausalLink(4, 3, 0, 0)
        val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 1, 0))) ||
          (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 1)))
        planStepOK && parameterOK && causalLinkOK && constraintOK
      case _                                                                                                                       => false
    })
    assert(resolvers exists {
      case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
        val planStepOK = plan == simpleOpenPreconditionPlan && newPlanStep._1 == 5 && newPlanStep._2.length == 2 && newPlanStep._2(0) == 1 && newPlanStep._2(1) == 2 &&
          newPlanStep._3 == -1 && newPlanStep._4 == -1
        val parameterOK = parameterVariableSorts sameElements Array(0, 0)
        val causalLinkOK = causalLink == EfficientCausalLink(4, 3, 0, 0)
        val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 1, 0))) ||
          (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 1)))
        planStepOK && parameterOK && causalLinkOK && constraintOK
      case _                                                                                                                       => false
    })
    assert(resolvers exists {
      case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
        val planStepOK = plan == simpleOpenPreconditionPlan && newPlanStep._1 == 5 && newPlanStep._2.length == 2 && newPlanStep._2(0) == 1 && newPlanStep._2(1) == 2 &&
          newPlanStep._3 == -1 && newPlanStep._4 == -1
        val parameterOK = parameterVariableSorts sameElements Array(0, 0)
        val causalLinkOK = causalLink == EfficientCausalLink(4, 3, 1, 0)
        val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 2, 0))) ||
          (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 2)))

        planStepOK && parameterOK && causalLinkOK && constraintOK
      case _                                                                                                                       => false
    })

  }
}
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

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint, EfficientCSP}
import de.uniulm.ki.panda3.efficient.domain.{HasEfficientExampleDomain1, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientOpenPrecondition
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertCausalLink, EfficientInsertPlanStepWithLink, EfficientModification}
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
// scalastyle:off null
class EfficientPlanTest extends FlatSpec with HasEfficientExampleDomain1 {

  "Detecting Open Preconditions" must "yield all of them" in {
    val openPrecondition = efficientPlanTestPlan.openPreconditions

    assert(openPrecondition.length == 5)

    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 1 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 2 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 3 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 5 && prec == 0 })
    assert(openPrecondition exists { case EfficientOpenPrecondition(_, ps, prec) => ps == 6 && prec == 0 })
  }

  "Detecting Abstract Tasks" must "yield all of them" in {
    val abstractTasks = efficientPlanTestPlan.abstractPlanSteps

    assert(abstractTasks.length == 3)
    assert(abstractTasks exists { _.planStep == 2 })
    assert(abstractTasks exists { _.planStep == 5 })
    assert(abstractTasks exists { _.planStep == 6 })
  }

  "Detecting Causal Threats" must "yield all of them" in {
    val causalThreats = efficientPlanTestPlan.causalThreats

    assert(causalThreats.length == 1)
    assert(causalThreats exists { _.causalLink == efficientPlanTestPlan.causalLinks.head })
    assert(causalThreats exists { _.threatingPlanStep == 2 })
    assert(causalThreats exists { _.indexOfThreatingEffect == 0 })
    assert(causalThreats exists { _.mgu.length == 1 })
    assert(causalThreats exists { cl =>
      cl.mgu.head == EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, efficientPlanTestPlan.variableConstraints.getRepresentativeVariable(1)) ||
        cl.mgu.head == EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, efficientPlanTestPlan.variableConstraints.getRepresentativeVariable(1), 0)
    })
  }

  "Detecting Unbound Variables" must "yield all of them" in {
    val unboundVariables = efficientPlanTestPlan.unboundVariables

    assert(unboundVariables.length == 6)
    assert(unboundVariables.toSet.size == 6)
  }

  var modifiedPlan: EfficientPlan = null
  "Modifications" must "be applicable" in {
    val flaws = simpleOpenPreconditionPlan.openPreconditions
    assert(flaws.length == 3)
    assert(simpleOpenPreconditionPlan.openPreconditions exists { f => f.plan == simpleOpenPreconditionPlan && f.planStep == 1 && f.preconditionIndex == 0 })
    assert(simpleOpenPreconditionPlan.openPreconditions exists { f => f.plan == simpleOpenPreconditionPlan && f.planStep == 2 && f.preconditionIndex == 0 })
    assert(simpleOpenPreconditionPlan.openPreconditions exists { f => f.plan == simpleOpenPreconditionPlan && f.planStep == 3 && f.preconditionIndex == 0 })

    val flaw = (simpleOpenPreconditionPlan.openPreconditions find { _.planStep == 3 }).get
    assert(flaw.resolver.length == 4)

    val modification = (flaw.resolver find { case EfficientInsertPlanStepWithLink(_, _, (2, _, _, _,_), _, _, _) => true; case _ => false }).get

    modifiedPlan = simpleOpenPreconditionPlan modify modification
  }

  it must "lead to the expected plan" in {
    assert(modifiedPlan.planStepTasks.length == 5)
  }

  "Open Precondition Flaws" must "be correct if computed incrementally" in {
    val flaws: Array[EfficientOpenPrecondition] = modifiedPlan.openPreconditions
    assert(flaws.length == 3)
    assert(flaws exists { flaw => flaw.plan == modifiedPlan && flaw.planStep == 1 && flaw.preconditionIndex == 0 })
    assert(flaws exists { flaw => flaw.plan == modifiedPlan && flaw.planStep == 2 && flaw.preconditionIndex == 0 })
    assert(flaws exists { flaw => flaw.plan == modifiedPlan && flaw.planStep == 4 && flaw.preconditionIndex == 0 })


    val t3Flaw = (flaws find { case EfficientOpenPrecondition(_, 2, _) => true; case _ => false }).get
    val t3Modifications = t3Flaw.resolver

    assert(t3Modifications.length == 4)
    assert(t3Modifications exists {
      case EfficientInsertCausalLink(_, _, link, conditions) =>
        link.producer == 4 && link.consumer == 2 && link.conditionIndexOfProducer == 0 && link.conditionIndexOfConsumer == 0 && conditions.length == 0
      case _                                                 => false
    })
    assert(t3Modifications exists {
      case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
        val planStepOK = plan == modifiedPlan && newPlanStep._1 == 2 && newPlanStep._2.length == 1 && newPlanStep._2(0) == 2 && newPlanStep._3 == -1 && newPlanStep._4 == -1
        val parameterOK = parameterVariableSorts sameElements Array(0)
        val causalLinkOK = causalLink == EfficientCausalLink(5, 2, 0, 0)
        val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 2, 0))) ||
          (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 2)))
        planStepOK && parameterOK && causalLinkOK && constraintOK
      case _                                                                                                                       => false
    })
    assert(t3Modifications exists {
      case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
        val planStepOK = plan == modifiedPlan && newPlanStep._1 == 5 && newPlanStep._2.length == 2 && newPlanStep._2(0) == 2 && newPlanStep._2(1) == 3 &&
          newPlanStep._3 == -1 && newPlanStep._4 == -1
        val parameterOK = parameterVariableSorts sameElements Array(0, 0)
        val causalLinkOK = causalLink == EfficientCausalLink(5, 2, 0, 0)
        val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 2, 0))) ||
          (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 2)))
        planStepOK && parameterOK && causalLinkOK && constraintOK
      case _                                                                                                                       => false
    })
    assert(t3Modifications exists {
      case EfficientInsertPlanStepWithLink(plan, _, newPlanStep, parameterVariableSorts, causalLink, necessaryVariableConstraints) =>
        val planStepOK = plan == modifiedPlan && newPlanStep._1 == 5 && newPlanStep._2.length == 2 && newPlanStep._2(0) == 2 && newPlanStep._2(1) == 3 &&
          newPlanStep._3 == -1 && newPlanStep._4 == -1
        val parameterOK = parameterVariableSorts sameElements Array(0, 0)
        val causalLinkOK = causalLink == EfficientCausalLink(5, 2, 1, 0)
        val constraintOK = (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 3, 0))) ||
          (necessaryVariableConstraints sameElements Array(EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, 0, 3)))

        planStepOK && parameterOK && causalLinkOK && constraintOK
      case _                                                                                                                       => false
    })
  }

}

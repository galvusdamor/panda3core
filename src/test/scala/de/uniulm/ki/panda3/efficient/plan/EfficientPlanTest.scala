package de.uniulm.ki.panda3.efficient.plan

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint, EfficientCSP}
import de.uniulm.ki.panda3.efficient.domain.{HasEfficientExampleDomain1, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw.EfficientOpenPrecondition
import de.uniulm.ki.panda3.efficient.plan.modification.{EfficientInsertPlanStepWithLink, EfficientModification}
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
    assert(simpleOpenPreconditionPlan.openPreconditions exists {f => f.plan == simpleOpenPreconditionPlan && f.planStep == 1 && f.preconditionIndex == 0})
    assert(simpleOpenPreconditionPlan.openPreconditions exists {f => f.plan == simpleOpenPreconditionPlan && f.planStep == 2 && f.preconditionIndex == 0})
    assert(simpleOpenPreconditionPlan.openPreconditions exists {f => f.plan == simpleOpenPreconditionPlan && f.planStep == 3 && f.preconditionIndex == 0})

    val flaw = (simpleOpenPreconditionPlan.openPreconditions find { _.planStep == 3 }).get
    assert(flaw.resolver.length == 4)

    val modification = (flaw.resolver find { case EfficientInsertPlanStepWithLink(_, _, (2, _, _, _), _, _, _) => true; case _ => false }).get

    modifiedPlan = simpleOpenPreconditionPlan modify modification
  }

  it must "lead to the expected plan" in {
    assert(modifiedPlan.planStepTasks.length == 5)
  }

  "Open Precondition Flaws" must "be correct if computed incrementally" in {
    val flaws : Array[EfficientOpenPrecondition]= modifiedPlan.openPreconditions
    assert(flaws.length == 3)
    assert(flaws exists {flaw => flaw.plan == modifiedPlan && flaw.planStep == 1 && flaw.preconditionIndex == 0})
    assert(flaws exists {flaw => flaw.plan == modifiedPlan && flaw.planStep == 2 && flaw.preconditionIndex == 0})
    assert(flaws exists {flaw => flaw.plan == modifiedPlan && flaw.planStep == 4 && flaw.preconditionIndex == 0})
  }

}
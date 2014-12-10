package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.{Equal, SymbolicCSP}
import de.uniulm.ki.panda3.domain.HasExampleDomain1
import de.uniulm.ki.panda3.plan.element.CausalLink
import de.uniulm.ki.panda3.plan.flaw.{OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.plan.modification.{BindVariableToValue, InsertCausalLink, InsertPlanStepWithLink}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class ModificationTest1 extends FlatSpec with HasExampleDomain1 {

  "Modifications" must "be computed for Open Preconditions" in {
    val plan1: SymbolicPlan = SymbolicPlan(exampleDomain1, planstep0init :: planstep1goal :: Nil, Nil, SymbolicTaskOrdering(Nil, 2).addOrdering(planstep0init, planstep1goal),
                                           SymbolicCSP(Set(instance_variable1sort1, instance_variable1sort2), Nil), planstep0init, planstep1goal)


    val plan1flaws = plan1.flaws

    // there should be a single open precondition flaw
    assert(plan1flaws.size == 1)
    assert(plan1flaws exists { case OpenPrecondition(_, planStep, precondition) => planStep == planstep1goal && precondition == planstep1goal.substitutedPreconditions(0)})


    val plan1flaw = plan1flaws(0)
    val plan1flawModifications = plan1flaw.resolvants
    // there should be exactly one modification
    assert(plan1flawModifications.size == 1)
    // and it should a the needed plan step
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, cl, constr, _) =>
      val newCSP = plan1.variableConstraints.addVariables(ps.arguments).addConstraints(constr)
      ps.schema == task1 &&
        ps.id == 2 &&
        cl.producer == ps &&
        cl.consumer == planstep1goal &&
        (cl.condition =?= planstep1goal.substitutedPreconditions(0))(newCSP) &&
        (cl.condition =?= ps.substitutedPreconditions(0).negate)(newCSP) &&
        constr.size == 1 &&
        constr(0) == Equal(ps.arguments(0), instance_variable1sort2)
    })

    // now apply the modification
    val plan2 = plan1.modify(plan1flawModifications(0))
    assert(plan2.planSteps.size == 3)
    assert(plan2.causalLinks.size == 1)
    assert(
      plan2.causalLinks exists { case CausalLink(p, c, l) => p == plan2.planSteps(2) && c == planstep1goal && (l =?= planstep1goal.substitutedPreconditions(0))(plan2.variableConstraints)})
    assert(plan2.orderingConstraints.lteq(plan2.planSteps(2), planstep1goal))


    // calculate flaws of the new plan
    val plan2flaws = plan2.flaws

    assert(plan2flaws.size == 1)
    assert(plan2flaws exists { case OpenPrecondition(_, planStep, precondition) => planStep == plan2.planSteps(2) && precondition == plan2.planSteps(2).substitutedPreconditions(0)})

    // generate all modifications for the only flaw
    val plan2flawModifications = plan2flaws(0).resolvants

    assert(plan2flawModifications.size == 1)
    assert(plan2flawModifications exists { case InsertCausalLink(cl, constr) =>
      val csp = plan2.variableConstraints.addConstraints(constr)

      cl.producer == planstep0init &&
        cl.consumer == plan2.planSteps(2) &&
        (cl.condition =?= planstep0init.substitutedEffects(0))(csp) &&
        (cl.condition =?= plan2.planSteps(2).substitutedPreconditions(0))(csp) &&
        constr.size == 1 &&
        constr(0) == Equal(planstep0init.arguments(0), plan2.planSteps(2).arguments(0))
    })

    // generate thrid plan
    val plan3 = plan2.modify(plan2flawModifications(0))

    val plan3flaws = plan3.flaws
    assert(plan3flaws.size == 1)
    assert(plan3flaws exists { case UnboundVariable(_, v) => plan3.variableConstraints.equal(v, plan3.init.arguments(0))})

    val plan3flawModifications = plan3flaws(0).resolvants
    assert(plan3flawModifications.size == 4)
    assert(plan3flawModifications.toSet.size == 4)
    assert(plan3flawModifications forall { case BindVariableToValue(v, value) =>
      plan3.variableConstraints.equal(v, plan3.init.arguments(0)) && v.sort.elements.contains(value)
    })

    val plan4 = plan3.modify(plan3flawModifications(0))

    assert(plan4.flaws.size == 0)
  }
}
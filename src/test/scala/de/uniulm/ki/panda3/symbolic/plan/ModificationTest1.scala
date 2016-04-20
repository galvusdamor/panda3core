package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.csp.{Equal, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.domain.HasExampleDomain1
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw.{OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.symbolic.plan.modification.{BindVariableToValue, InsertCausalLink, InsertPlanStepWithLink}
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class ModificationTest1 extends FlatSpec with HasExampleDomain1 {

  val planstep0init = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val planstep1goal = PlanStep(1, goal1, instance_variableSort1(2) :: Nil)


  "Modifications" must "be computed for Open Preconditions" in {
    val plan1PlanSteps = planstep0init :: planstep1goal :: Nil
    val plan1: Plan = Plan(plan1PlanSteps, Nil, SymbolicTaskOrdering(Nil, plan1PlanSteps).addOrdering(planstep0init, planstep1goal),
                                           SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2)), Nil), planstep0init, planstep1goal, AllModifications, AllFlaws, Map(),
                                           Map())
    // it should be possible to solve the plan
    assert(plan1.isSolvable.isEmpty)


    val plan1flaws = plan1.flaws

    // there should be a single open precondition flaw
    assert(plan1flaws.size == 1)
    assert(plan1flaws exists { case OpenPrecondition(_, planStep, precondition) => planStep == planstep1goal && precondition == planstep1goal.substitutedPreconditions.head })


    val plan1flaw = plan1flaws.head
    val plan1flawModifications = plan1flaw.resolvents(exampleDomain1)
    // there should be exactly one modification
    assert(plan1flawModifications.size == 1)
    // and it should a the needed plan step
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, _, _, _) => ps.schema == task1 })
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, _, _, _) => ps.id == 2 })
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, cl, _, _) => cl.producer == ps })
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, cl, _, _) => cl.consumer == planstep1goal })
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(_, _, constr, _) => constr.size == 1 })
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, _, constr, _) => constr.head == Equal(ps.arguments.head, instance_variableSort1(2)) })
    assert(plan1flawModifications exists { case InsertPlanStepWithLink(ps, cl, constr, _) =>
      val newCSP = plan1.variableConstraints.addVariables(ps.arguments).addConstraints(constr)
      (cl.condition =?= planstep1goal.substitutedPreconditions.head) (newCSP) &&
        (cl.condition =?= ps.substitutedPreconditions.head.negate) (newCSP)
    })


    // now apply the modification
    val plan2 = plan1.modify(plan1flawModifications.head)
    // it should be possbile to solve the plan
    assert(plan2.isSolvable == None)
    // properties of this plan
    assert(plan2.planSteps.size == 3)
    assert(plan2.causalLinks.size == 1)
    assert(plan2.causalLinks exists { case CausalLink(p, c, l) => p == plan2.planSteps(2) && c == planstep1goal && (l =?= planstep1goal.substitutedPreconditions.head) (
                                                                                                                                                                         plan2
                                                                                                                                                                           .variableConstraints)
    })
    assert(plan2.orderingConstraints.lteq(plan2.planSteps(2), planstep1goal))


    // calculate flaws of the new plan
    val plan2flaws = plan2.flaws

    assert(plan2flaws.size == 1)
    assert(plan2flaws exists { case OpenPrecondition(_, planStep, precondition) => planStep == plan2.planSteps(2) && precondition == plan2.planSteps(2).substitutedPreconditions.head })

    // generate all modifications for the only flaw
    val plan2flawModifications = plan2flaws.head.resolvents(exampleDomain1)

    assert(plan2flawModifications.size == 1)
    assert(plan2flawModifications exists { case InsertCausalLink(_, cl, _) => cl.producer == planstep0init })
    assert(plan2flawModifications exists { case InsertCausalLink(_, cl, _) => cl.consumer == plan2.planSteps(2) })
    assert(plan2flawModifications exists { case InsertCausalLink(_, _, constr) => constr.size == 1 })
    assert(plan2flawModifications exists { case InsertCausalLink(_, _, constr) => constr.head == Equal(planstep0init.arguments.head, plan2.planSteps(2).arguments.head) })
    assert(plan2flawModifications exists { case InsertCausalLink(_, cl, constr) =>
      val csp = plan2.variableConstraints.addConstraints(constr)
      (cl.condition =?= planstep0init.substitutedEffects.head) (csp) && (cl.condition =?= plan2.planSteps(2).substitutedPreconditions.head) (csp)
    })


    // generate third plan
    val plan3 = plan2.modify(plan2flawModifications.head)
    // it should be possible to solve the plan
    assert(plan3.isSolvable == None)

    val plan3flaws = plan3.flaws
    assert(plan3flaws.size == 1)
    assert(plan3flaws exists { case UnboundVariable(_, v) => plan3.variableConstraints.equal(v, plan3.init.arguments.head) })

    val plan3flawModifications = plan3flaws.head.resolvents(exampleDomain1)
    assert(plan3flawModifications.size == 4)
    assert(plan3flawModifications.toSet.size == 4)
    assert(plan3flawModifications forall { case BindVariableToValue(_, v, value) => plan3.variableConstraints.equal(v, plan3.init.arguments.head) && v.sort.elements.contains(value) })

    val plan4 = plan3.modify(plan3flawModifications.head)
    // it should be possible to solve the plan
    assert(plan4.isSolvable == Some(true))

    assert(plan4.flaws.size == 0)
  }
}
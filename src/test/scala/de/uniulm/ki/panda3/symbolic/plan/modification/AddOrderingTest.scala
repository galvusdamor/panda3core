package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.SymbolicCSP
import de.uniulm.ki.panda3.symbolic.domain.HasExampleDomain2
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
// scalastyle:off magic.number
class AddOrderingTest extends FlatSpec with HasExampleDomain2 {

  val psinit = PlanStep(0, init, instance_variableSort1(1) :: Nil, None,None)
  val psgoal = PlanStep(1, goal1, instance_variableSort1(1) :: Nil, None,None)


  "Generating Ordering Modifications" must "be possible" in {
    val ps2 = PlanStep(2, task1, instance_variableSort1(3) :: Nil, None,None)
    val ps3 = PlanStep(3, task1, instance_variableSort1(4) :: Nil, None,None)

    val singleOrderingModification = AddOrdering(null, ps2, ps3)


    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == ps2 && after == ps3})
  }

  "Generating Ordering Modifications for causal threads" must "produce promotion and demotion" in {
    /*
      * This is the plan in question:
      *
      * init:-p(x)        -p(y):ps2:p(y)----------p(y):goal
      *
      *                   :ps3:-p(z),q(z)         q(y):goal
      */
    val ps2 = PlanStep(2, task1, instance_variableSort1(2) :: Nil, None,None)
    val ps3 = PlanStep(3, task2, instance_variableSort1(3) :: Nil, None,None)
    val cl = CausalLink(ps2, psgoal, psgoal.substitutedPreconditions.head)


    val planPlanSteps = psinit :: psgoal :: ps2 :: ps3 :: Nil
    val plan: SymbolicPlan = SymbolicPlan(planPlanSteps, cl :: Nil, SymbolicTaskOrdering(Nil, planPlanSteps).addOrdering(ps2, psgoal),
                                          SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psgoal)

    val singleOrderingModification = AddOrdering(plan, ps3, cl)

    assert(singleOrderingModification.size == 2)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == ps3 && after == ps2})
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == psgoal && after == ps3})
  }


  "Generating Ordering Modifications for causal threads" must "be correct, if demotion is not possible" in {
    /*
     * This is the plan in question:
     *
     * init:-p(x)        -p(y):ps2:p(y)----------p(y):goal
     *
     *                   :ps3:-p(z),q(z)         q(y):goal
     */
    val ps2 = PlanStep(2, task1, instance_variableSort1(2) :: Nil, None,None)
    val ps3 = PlanStep(3, task2, instance_variableSort1(3) :: Nil, None,None)
    val cl = CausalLink(ps2, psgoal, psgoal.substitutedPreconditions.head)


    val planPlanSteps = psinit :: psgoal :: ps2 :: ps3 :: Nil
    val plan: SymbolicPlan = SymbolicPlan(planPlanSteps, Nil,
                                          SymbolicTaskOrdering(Nil, planPlanSteps).addOrdering(psinit, psgoal).addOrdering(psinit, ps2).addOrdering(psinit, ps3).addOrdering(ps2, psgoal)
                                            .addOrdering(ps3, psgoal), SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psgoal)

    val singleOrderingModification = AddOrdering(plan, ps3, cl)

    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == ps3 && after == ps2})
  }
}

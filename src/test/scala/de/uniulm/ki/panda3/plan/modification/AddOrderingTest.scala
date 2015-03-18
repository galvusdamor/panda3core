package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.SymbolicCSP
import de.uniulm.ki.panda3.domain.HasExampleDomain2
import de.uniulm.ki.panda3.plan.SymbolicPlan
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class AddOrderingTest extends FlatSpec with HasExampleDomain2 {

  val psinit = PlanStep(0, init, instance_variable1sort1 :: Nil)
  val psgoal = PlanStep(1, goal1, instance_variable2sort1 :: Nil)


  "Generating Ordering Modifications" must "be possible" in {
    val ps2 = PlanStep(2, task1, instance_variable3sort1 :: Nil)
    val ps3 = PlanStep(3, task1, instance_variable4sort1 :: Nil)

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
    val ps2 = PlanStep(2, task1, instance_variable2sort1 :: Nil)
    val ps3 = PlanStep(3, task2, instance_variable3sort1 :: Nil)
    val cl = CausalLink(ps2, psgoal, psgoal.substitutedPreconditions(0))


    val planPlanSteps = psinit :: psgoal :: ps2 :: ps3 :: Nil
    val plan: SymbolicPlan = SymbolicPlan(planPlanSteps, cl :: Nil, SymbolicTaskOrdering(Nil, planPlanSteps).addOrdering(ps2, psgoal),
                                          SymbolicCSP(Set(instance_variable1sort1, instance_variable2sort1, instance_variable3sort1), Nil), psinit, psgoal)

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
    val ps2 = PlanStep(2, task1, instance_variable2sort1 :: Nil)
    val ps3 = PlanStep(3, task2, instance_variable3sort1 :: Nil)
    val cl = CausalLink(ps2, psgoal, psgoal.substitutedPreconditions(0))


    val planPlanSteps = psinit :: psgoal :: ps2 :: ps3 :: Nil
    val plan: SymbolicPlan = SymbolicPlan(planPlanSteps, Nil,
                                          SymbolicTaskOrdering(Nil, planPlanSteps).addOrdering(psinit, psgoal).addOrdering(psinit, ps2).addOrdering(psinit, ps3).addOrdering(ps2, psgoal)
                                            .addOrdering(ps3, psgoal), SymbolicCSP(Set(instance_variable1sort1, instance_variable2sort1, instance_variable3sort1), Nil), psinit, psgoal)

    val singleOrderingModification = AddOrdering(plan, ps3, cl)

    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == ps3 && after == ps2})
  }
}

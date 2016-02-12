package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.SymbolicCSP
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, HasExampleDomain2, Task}
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.{Plan, SymbolicPlan}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class AddOrderingTest extends FlatSpec with AddOrderingTestData {

  "Generating Ordering Modifications" must "be possible" in {
    val ps2 = PlanStep(2, task1, instance_variableSort1(3) :: Nil, None, None)
    val ps3 = PlanStep(3, task1, instance_variableSort1(4) :: Nil, None, None)

    val singleOrderingModification = AddOrdering(null, ps2, ps3)


    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == ps2 && after == ps3 })
  }

  "Generating Ordering Modifications for causal threads" must "produce promotion and demotion" in {
    val singleOrderingModification = AddOrdering(promotionDemotionPlan, promotionDemotionPlanPS3, promotionDemotionPlanCL)

    assert(singleOrderingModification.size == 2)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == promotionDemotionPlanPS3 && after == promotionDemotionPlanPS2 })
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == psgoal && after == promotionDemotionPlanPS3 })
  }

  "Generating Ordering Modifications for causal threads" must "be correct, if demotion is not possible" in {
    val singleOrderingModification = AddOrdering(demotionNotPossiblePlan, demotionNotPossiblePlanPS3, demotionNotPossiblePlanCL)
    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case AddOrdering(_, OrderingConstraint(before, after)) => before == demotionNotPossiblePlanPS3 && after == demotionNotPossiblePlanPS2 })
  }
}


trait AddOrderingTestData extends HasExampleDomain2 {

  val psinit = PlanStep(0, init, instance_variableSort1(1) :: Nil, None, None)
  val psgoal = PlanStep(1, goal1, instance_variableSort1(1) :: Nil, None, None)

  /*
   * This is the plan in question:
   *
   * init:-p(x)        -p(y):ps2:p(y)----------p(y):goal          :realgoal
   *
   *                   :ps3:-p(z),q(z)         q(y):goal
   */
  val promotionDemotionPlanPS2    = PlanStep(2, task1, instance_variableSort1(2) :: Nil, None, None)
  val promotionDemotionPlanPS3    = PlanStep(3, task2, instance_variableSort1(3) :: Nil, None, None)
  val promotionDemotionPlanCL     = CausalLink(promotionDemotionPlanPS2, psgoal, psgoal.substitutedPreconditions.head)
  val promotionDemotionPlan: Plan = {
    val psRealGoal = PlanStep(4, ReducedTask("realgoal", true, Nil, Nil, And(Nil), And(Nil)), Nil, None, None)
    val cl = CausalLink(promotionDemotionPlanPS2, psgoal, psgoal.substitutedPreconditions.head)

    // hacky as we use psgoal as a real action
    val planPlanSteps = psinit :: psRealGoal :: psgoal :: promotionDemotionPlanPS2 :: promotionDemotionPlanPS3 :: Nil
    val ordering = SymbolicTaskOrdering(Nil, planPlanSteps).addOrdering(promotionDemotionPlanPS2, psgoal)
      .addOrderings(OrderingConstraint.allBetween(psinit, psRealGoal, promotionDemotionPlanPS2, promotionDemotionPlanPS3, psgoal))
    SymbolicPlan(planPlanSteps, cl :: Nil, ordering, SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psRealGoal)
  }
  val promotionDemotionDomain  = Domain(sort1 :: Nil, predicate1 :: predicate2 :: Nil, task1 :: task2 :: goal1 :: Nil, Nil, Nil)

  /*
    * This is the plan in question:
    *
    * init:-p(x)        -p(y):ps2:p(y)----------p(y):goal
    *
    *                   :ps3:-p(z),q(z)         q(y):goal
    */
  val demotionNotPossiblePlanPS2 = PlanStep(2, task1, instance_variableSort1(2) :: Nil, None, None)
  val demotionNotPossiblePlanPS3 = PlanStep(3, task2, instance_variableSort1(3) :: Nil, None, None)
  val demotionNotPossiblePlanCL  = CausalLink(demotionNotPossiblePlanPS2, psgoal, psgoal.substitutedPreconditions.head)

  val demotionNotPossiblePlan: SymbolicPlan = {
    val planPlanSteps = psinit :: psgoal :: demotionNotPossiblePlanPS2 :: demotionNotPossiblePlanPS3 :: Nil
    SymbolicPlan(planPlanSteps, Nil,
                 SymbolicTaskOrdering(Nil, planPlanSteps).addOrderings(OrderingConstraint.allBetween(psinit, psgoal, demotionNotPossiblePlanPS2, demotionNotPossiblePlanPS3)),
                 SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psgoal)
  }
}
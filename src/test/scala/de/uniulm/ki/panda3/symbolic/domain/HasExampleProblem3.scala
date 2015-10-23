package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.SymbolicCSP
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait HasExampleProblem3 extends HasExampleDomain3 {
  val psInit1 = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val psGoal1 = PlanStep(1, goal1, instance_variableSort1(1) :: Nil)


  val psAbstract1    = PlanStep(2, abstractTask1, instance_variableSort1(1) :: Nil)
  val planPlanSteps1 = psInit1 :: psGoal1 :: psAbstract1 :: Nil

  val causalLinkInitAbstract = CausalLink(psInit1, psAbstract1, psInit1.substitutedEffects.head)
  val causalLinkAbstractGoal = CausalLink(psAbstract1, psGoal1, psGoal1.substitutedPreconditions.head)

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan1WithoutCausalLinks = SymbolicPlan(planPlanSteps1, Nil,
                                             SymbolicTaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                             SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1)

  // create a plan  init| -p1> a1 -> |goal (with causal links)
  val plan1WithOneCausalLinks = SymbolicPlan(planPlanSteps1, causalLinkInitAbstract :: Nil,
                                             SymbolicTaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                             SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1)


  // create a plan  init| -p1> a1 -p1> |goal (with causal links)
  val plan1WithBothCausalLinks = SymbolicPlan(planPlanSteps1, causalLinkInitAbstract :: causalLinkAbstractGoal :: Nil,
                                              SymbolicTaskOrdering(OrderingConstraint(psInit1, psAbstract1) :: OrderingConstraint(psAbstract1, psGoal1) :: Nil, planPlanSteps1),
                                              SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit1, psGoal1)

}
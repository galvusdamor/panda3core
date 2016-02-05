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
// scalastyle:off magic.number
trait HasExampleProblem4 extends HasExampleDomain4 {

  val psInit2 = PlanStep(0, init4, instance_variableSort1(1) :: Nil, None,None)
  val psGoal2 = PlanStep(1, goal4, Nil, None,None)


  val psAbstract2    = PlanStep(2, abstractTask2, instance_variableSort1(1) :: Nil, None,None)
  val planPlanSteps2 = psInit2 :: psGoal2 :: psAbstract2 :: Nil

  val causalLinkInit2Abstract2P1 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects.head)
  val causalLinkInit2Abstract2P2 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects(1))

  // create a plan  init| -> a1 -> |goal (with one causal link)
  val plan2WithoutLink = SymbolicPlan(planPlanSteps2, Nil, SymbolicTaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2),
                                      SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2)

  // create a plan  init| -> a1 -> |goal (with one causal link)
  val plan2WithOneLink = SymbolicPlan(planPlanSteps2, causalLinkInit2Abstract2P1 :: Nil, SymbolicTaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2),
                                      SymbolicCSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2)

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan2WithTwoLinks = SymbolicPlan(planPlanSteps2, causalLinkInit2Abstract2P1 :: causalLinkInit2Abstract2P2 :: Nil,
                                       SymbolicTaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2), SymbolicCSP(Set(instance_variableSort1(1)), Nil),
                                       psInit2, psGoal2)
}
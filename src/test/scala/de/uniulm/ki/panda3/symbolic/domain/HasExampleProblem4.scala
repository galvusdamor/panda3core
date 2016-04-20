package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
// scalastyle:off magic.number
trait HasExampleProblem4 extends HasExampleDomain4 {

  val psInit2 = PlanStep(0, init4, instance_variableSort1(1) :: Nil)
  val psGoal2 = PlanStep(1, goal4, Nil)


  val psAbstract2    = PlanStep(2, abstractTask2, instance_variableSort1(1) :: Nil)
  val planPlanSteps2 = psInit2 :: psGoal2 :: psAbstract2 :: Nil

  val causalLinkInit2Abstract2P1 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects.head)
  val causalLinkInit2Abstract2P2 = CausalLink(psInit2, psAbstract2, psInit2.substitutedEffects(1))

  // create a plan  init| -> a1 -> |goal (with one causal link)
  val plan2WithoutLink = Plan(planPlanSteps2, Nil, TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2),
                              CSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2, AllModifications, AllFlaws,Map(),Map())

  // create a plan  init| -> a1 -> |goal (with one causal link)
  val plan2WithOneLink = Plan(planPlanSteps2, causalLinkInit2Abstract2P1 :: Nil, TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2),
                              CSP(Set(instance_variableSort1(1)), Nil), psInit2, psGoal2, AllModifications, AllFlaws,Map(),Map())

  // create a plan  init| -> a1 -> |goal (without causal links)
  val plan2WithTwoLinks = Plan(planPlanSteps2, causalLinkInit2Abstract2P1 :: causalLinkInit2Abstract2P2 :: Nil,
                               TaskOrdering(OrderingConstraint.allBetween(psInit2, psGoal2, psAbstract2), planPlanSteps2), CSP(Set(instance_variableSort1(1)), Nil),
                                       psInit2, psGoal2, AllModifications, AllFlaws,Map(),Map())
}
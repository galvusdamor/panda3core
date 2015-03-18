package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.SymbolicCSP
import de.uniulm.ki.panda3.domain.HasExampleDomain3
import de.uniulm.ki.panda3.plan.SymbolicPlan
import de.uniulm.ki.panda3.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class DecomposePlanStepTest extends FlatSpec with HasExampleDomain3 {

  val psinit = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val psgoal = PlanStep(1, goal1, instance_variableSort1(1) :: Nil)


  "Order" must "be inherited" in {
    val psAbstract = PlanStep(2, abstractTask1, instance_variableSort1(1) :: Nil)

    val planPlanSteps = psinit :: psgoal :: psAbstract :: Nil

    val plan = SymbolicPlan(planPlanSteps, Nil, SymbolicTaskOrdering(OrderingConstraint(psinit, psAbstract) :: OrderingConstraint(psAbstract, psgoal) :: Nil, planPlanSteps),
                            SymbolicCSP(Set(instance_variableSort1(1)), Nil), psinit, psgoal)

    val possibleDecompositions: Seq[DecomposePlanStep] = DecomposePlanStep(plan, psAbstract, domain3)

    assert(possibleDecompositions.size == 1)
  }

}

package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.{NotEqual, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.domain.HasExampleDomain2
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class MakeLiteralsUnUnifiableTest extends FlatSpec with HasExampleDomain2 {
  val psinit = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val psgoal = PlanStep(1, goal1, instance_variableSort1(2) :: Nil)


  "UnUnifiying Literals in a plan" must "be possible" in {
    /*
      * This is the plan in question:
      *
      * -p(v2):ps2:p(v2)----------p(v2):goal
      *
      *           :ps3:-p(v3),q(v3)
      */
    val ps2 = PlanStep(2, task1, instance_variableSort1(2) :: Nil)
    val ps3 = PlanStep(3, task2, instance_variableSort1(3) :: Nil)


    val planPlanSteps = psinit :: psgoal :: ps2 :: ps3 :: Nil
    val plan: Plan = Plan(planPlanSteps, Nil, TaskOrdering(Nil, planPlanSteps).addOrderings(OrderingConstraint.allBetween(psinit, psgoal, ps2, ps3)),
                                          SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psgoal, AllModifications, AllFlaws,
                                          Map(), Map())

    val singleOrderingModification = MakeLiteralsUnUnifiable(plan, ps3.substitutedEffects.head.negate, ps2.substitutedEffects.head)

    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case MakeLiteralsUnUnifiable(_, ne) => ne == NotEqual(instance_variableSort1(2), instance_variableSort1(3)) })
  }
}

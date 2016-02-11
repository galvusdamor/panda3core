package de.uniulm.ki.panda3.symbolic.plan.modification

import de.uniulm.ki.panda3.symbolic.csp.{NotEqual, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.domain.HasExampleDomain2
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class MakeLiteralsUnUnifiableTest extends FlatSpec with HasExampleDomain2 {
  val psinit = PlanStep(0, init, instance_variableSort1(1) :: Nil, None, None)
  val psgoal = PlanStep(1, goal1, instance_variableSort1(2) :: Nil, None, None)


  "UnUnifiying Literals in a plan" must "be possible" in {
    /*
      * This is the plan in question:
      *
      * -p(v2):ps2:p(v2)----------p(v2):goal
      *
      *           :ps3:-p(v3),q(v3)
      */
    val ps2 = PlanStep(2, task1, instance_variableSort1(2) :: Nil, None, None)
    val ps3 = PlanStep(3, task2, instance_variableSort1(3) :: Nil, None, None)


    val planPlanSteps = psinit :: psgoal :: ps2 :: ps3 :: Nil
    val plan: SymbolicPlan = SymbolicPlan(planPlanSteps, Nil, SymbolicTaskOrdering(Nil, planPlanSteps).addOrderings(OrderingConstraint.allBetween(psinit, psgoal, ps2, ps3)),
                                          SymbolicCSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psgoal)

    val singleOrderingModification = MakeLiteralsUnUnifiable(plan, ps3.substitutedEffects.head.negate, ps2.substitutedEffects.head)

    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case MakeLiteralsUnUnifiable(_, ne) => ne == NotEqual(instance_variableSort1(2), instance_variableSort1(3)) })
  }
}

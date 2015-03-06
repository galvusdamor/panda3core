package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{NotEqual, SymbolicCSP}
import de.uniulm.ki.panda3.domain.HasExampleDomain2
import de.uniulm.ki.panda3.plan.SymbolicPlan
import de.uniulm.ki.panda3.plan.element.PlanStep
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class MakeLiteralsUnUnifiableTest extends FlatSpec with HasExampleDomain2 {
  val psgoal = PlanStep(1, goal1, instance_variable1sort2 :: Nil)


  "UnUnifiying Literals in a plan" must "be possible" in {
    /*
      * This is the plan in question:
      *
      * -p(y):ps2:p(y)----------p(y):goal
      *
      *           :ps3:-p(z),q(z)
      */
    val ps2 = PlanStep(2, task1, instance_variable1sort2 :: Nil)
    val ps3 = PlanStep(3, task2, instance_variable1sort3 :: Nil)


    val planPlanSteps = psgoal :: ps2 :: ps3 :: Nil
    val plan: SymbolicPlan = SymbolicPlan(exampleDomain2, psgoal :: ps2 :: ps3 :: Nil, Nil, SymbolicTaskOrdering(Nil, planPlanSteps),
                                          SymbolicCSP(Set(instance_variable1sort2, instance_variable1sort3), Nil), ps2, psgoal)

    val singleOrderingModification = MakeLiteralsUnUnifiable(plan, ps3.substitutedEffects(0).negate, ps2.substitutedEffects(0))

    assert(singleOrderingModification.size == 1)
    assert(singleOrderingModification exists { case MakeLiteralsUnUnifiable(_, ne) => ne == NotEqual(instance_variable1sort2, instance_variable1sort3)})
  }
}

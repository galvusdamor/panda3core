package de.uniulm.ki.panda3.plan.plan.implementation

import de.uniulm.ki.panda3.csp.{SymbolicCSP, Variable}
import de.uniulm.ki.panda3.domain.Task
import de.uniulm.ki.panda3.logic.{Constant, Literal, Predicate, Sort}
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.plan.implementation.SymbolicPlan
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class SymbolicPlanTest extends FlatSpec {

  val sort1 : Sort = Sort("sort1", Vector() :+ Constant("a") :+ Constant("b"))
  val predicate1 : Predicate = Predicate("predicate1", sort1 :: sort1 :: Nil)

  val d_v1 = Variable("d_v1", sort1)
  val d_v2 = Variable("d_v2", sort1)
  //  val d_v3 = Variable("d_v3", sort2)
  //  val d_v4 = Variable("d_v4", sort2)
  //  val d_v5 = Variable("d_v5", sort2)
  //  val d_v6 = Variable("d_v6", sort2)
  //  val d_v7 = Variable("d_v7", sort2)

  val p_v1 = Variable("p_v1", sort1)
  val p_v2 = Variable("p_v2", sort1)


  val schema1 : Task = Task("task1", true, d_v1 :: d_v2 :: Nil, Literal(predicate1, false, d_v1 :: d_v2 :: Nil) :: Nil, Nil)

  "Computing open preconditions" must "be possible" in {
    val plan1 : SymbolicPlan = SymbolicPlan(PlanStep(0, schema1, p_v1 :: p_v2 :: Nil) :: Nil, Nil, SymbolicTaskOrdering(Nil, 1), SymbolicCSP(Set(p_v1, p_v2), Nil))

    assert(plan1.allPreconditions.size == 1)
    assert(plan1.openPreconditions.size == 1)
  }


  "Computing open preconditions" must "not contain protected preconditions" in {
    val plan1 : SymbolicPlan = SymbolicPlan(PlanStep(0, schema1, p_v1 :: p_v2 :: Nil) :: PlanStep(1, schema1, p_v2 :: p_v1 :: Nil) :: Nil,
      CausalLink(PlanStep(1, schema1, p_v2 :: p_v1 :: Nil), PlanStep(0, schema1, p_v1 :: p_v2 :: Nil), Literal(predicate1, false, p_v1 :: p_v2 :: Nil)) :: Nil,
      SymbolicTaskOrdering(Nil, 2), SymbolicCSP(Set(p_v1, p_v2), Nil))

    assert(plan1.allPreconditions.size == 2)
    assert(plan1.openPreconditions.size == 1)
    assert(plan1.openPreconditions.forall(_.planStep.id == 1))
  }

}
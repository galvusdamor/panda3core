package de.uniulm.ki.panda3.symbolic.plan.plan.implementation

import de.uniulm.ki.panda3.symbolic.csp.SymbolicCSP
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.SymbolicPlan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class SymbolicPlanTest extends FlatSpec {

  val sort1     : Sort      = Sort("sort1", Vector() :+ Constant("a") :+ Constant("b"), Nil)
  val predicate1: Predicate = Predicate("predicate1", sort1 :: sort1 :: Nil)

  val d_v1 = Variable(1, "d_v1", sort1)
  val d_v2 = Variable(2, "d_v2", sort1)
  //  val d_v3 = Variable("d_v3", sort2)
  //  val d_v4 = Variable("d_v4", sort2)
  //  val d_v5 = Variable("d_v5", sort2)
  //  val d_v6 = Variable("d_v6", sort2)
  //  val d_v7 = Variable("d_v7", sort2)
  val p_v1 = Variable(3, "p_v1", sort1)
  val p_v2 = Variable(4, "p_v2", sort1)
  val p_v3 = Variable(5, "p_v2", sort1)
  val p_v4 = Variable(6, "p_v2", sort1)


  val schemaProd : ReducedTask = ReducedTask("task_prod", isPrimitive = true, d_v1 :: d_v2 :: Nil, Nil, And[Literal](Nil), And[Literal](Literal(predicate1, isPositive = true, d_v1 :: d_v2 ::
    Nil) :: Nil))
  val schemaCons : ReducedTask = ReducedTask("task_cons", isPrimitive = true, d_v1 :: d_v2 :: Nil, Nil, And[Literal](Literal(predicate1, isPositive = true, d_v1 :: d_v2 :: Nil) :: Nil),
                                             And[Literal](Nil))
  val schemaDestr: ReducedTask = ReducedTask("task_destr", isPrimitive = true, d_v1 :: d_v2 :: Nil, Nil, And[Literal](Nil),
                                             And[Literal](Literal(predicate1, isPositive = false, d_v1 :: d_v2 :: Nil) ::
                                                            Nil))

  "Computing open preconditions" must "be possible" in {
    val plan1PlanSteps = PlanStep(0, schemaCons, p_v1 :: p_v2 :: Nil) :: Nil
    val plan1: SymbolicPlan = SymbolicPlan(plan1PlanSteps, Nil, SymbolicTaskOrdering(Nil, plan1PlanSteps), SymbolicCSP(Set(p_v1, p_v2), Nil), null, null)

    assert(plan1.allPreconditions.size == 1)
    assert(plan1.openPreconditions.size == 1)
  }

  "Computing open preconditions" must "not contain protected preconditions" in {
    val plan1PlanSteps = PlanStep(0, schemaCons, p_v1 :: p_v2 :: Nil) :: PlanStep(1, schemaCons, p_v2 :: p_v1 :: Nil) :: Nil
    val plan1: SymbolicPlan = SymbolicPlan(plan1PlanSteps, CausalLink(plan1PlanSteps(1), plan1PlanSteps.head, Literal(predicate1, isPositive = true, p_v1
      :: p_v2 :: Nil)) :: Nil, SymbolicTaskOrdering(Nil, plan1PlanSteps), SymbolicCSP(Set(p_v1, p_v2), Nil), null, null)

    assert(plan1.allPreconditions.size == 2)
    assert(plan1.openPreconditions.size == 1)
    assert(plan1.openPreconditions.forall(_.planStep.id == 1))
  }


  "Computing causal threats" must "be possible" in {
    val ps0 = PlanStep(0, schemaProd, p_v1 :: p_v2 :: Nil)
    val ps1 = PlanStep(1, schemaCons, p_v1 :: p_v2 :: Nil)
    val ps2 = PlanStep(2, schemaDestr, p_v3 :: p_v4 :: Nil)
    val plan1PlanSteps = ps0 :: ps1 :: ps2 :: Nil
    val plan1: SymbolicPlan = SymbolicPlan(plan1PlanSteps, CausalLink(ps0, ps1, Literal(predicate1, isPositive = true, p_v1 :: p_v2 :: Nil)) :: Nil,
                                           SymbolicTaskOrdering(Nil, plan1PlanSteps), SymbolicCSP(Set(p_v1, p_v2, p_v3, p_v4), Nil), null, null)

    assert(plan1.causalThreats.size == 1)
  }

}

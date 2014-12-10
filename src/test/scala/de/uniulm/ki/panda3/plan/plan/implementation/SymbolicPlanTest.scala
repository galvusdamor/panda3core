package de.uniulm.ki.panda3.plan.plan.implementation

import de.uniulm.ki.panda3.csp.{SymbolicCSP, Variable}
import de.uniulm.ki.panda3.domain.{EmptyDomain, Task}
import de.uniulm.ki.panda3.logic.{Constant, Literal, Predicate, Sort}
import de.uniulm.ki.panda3.plan.SymbolicPlan
import de.uniulm.ki.panda3.plan.element.{CausalLink, PlanStep}
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
  val p_v3 = Variable("p_v2", sort1)
  val p_v4 = Variable("p_v2", sort1)


  val schemaProd: Task = Task("task_prod", true, d_v1 :: d_v2 :: Nil, Nil, Literal(predicate1, isPositive = true, d_v1 :: d_v2 :: Nil) :: Nil)
  val schemaCons: Task = Task("task_cons", true, d_v1 :: d_v2 :: Nil, Literal(predicate1, isPositive = true, d_v1 :: d_v2 :: Nil) :: Nil, Nil)
  val schemaDestr: Task = Task("task_destr", true, d_v1 :: d_v2 :: Nil, Nil, Literal(predicate1, isPositive = false, d_v1 :: d_v2 :: Nil) :: Nil)

  "Computing open preconditions" must "be possible" in {
    val plan1: SymbolicPlan = SymbolicPlan(EmptyDomain, PlanStep(0, schemaCons, p_v1 :: p_v2 :: Nil) :: Nil, Nil, SymbolicTaskOrdering(Nil, 1), SymbolicCSP(Set(p_v1, p_v2), Nil), null, null)

    assert(plan1.allPreconditions.size == 1)
    assert(plan1.openPreconditions.size == 1)
  }


  "Computing open preconditions" must "not contain protected preconditions" in {
    val plan1: SymbolicPlan = SymbolicPlan(EmptyDomain, PlanStep(0, schemaCons, p_v1 :: p_v2 :: Nil) :: PlanStep(1, schemaCons, p_v2 :: p_v1 :: Nil) :: Nil,
                                           CausalLink(PlanStep(1, schemaCons, p_v2 :: p_v1 :: Nil), PlanStep(0, schemaCons, p_v1 :: p_v2 :: Nil), Literal(predicate1, isPositive = true, p_v1
                                             :: p_v2 :: Nil)) :: Nil,
                                           SymbolicTaskOrdering(Nil, 2), SymbolicCSP(Set(p_v1, p_v2), Nil), null, null)

    assert(plan1.allPreconditions.size == 2)
    assert(plan1.openPreconditions.size == 1)
    assert(plan1.openPreconditions.forall(_.planStep.id == 1))
  }


  "Computing causal threats" must "be possible" in {
    val ps0 = PlanStep(0, schemaProd, p_v1 :: p_v2 :: Nil)
    val ps1 = PlanStep(1, schemaCons, p_v1 :: p_v2 :: Nil)
    val ps2 = PlanStep(2, schemaDestr, p_v3 :: p_v4 :: Nil)
    val plan1: SymbolicPlan = SymbolicPlan(EmptyDomain, ps0 :: ps1 :: ps2 :: Nil,
                                           CausalLink(ps0, ps1, Literal(predicate1, isPositive = true, p_v1 :: p_v2 :: Nil)) :: Nil,
                                           SymbolicTaskOrdering(Nil, 3), SymbolicCSP(Set(p_v1, p_v2, p_v3, p_v4), Nil), null, null)

    assert(plan1.causalThreads.size == 1)
  }

}

package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.csp.{NotEqual, SymbolicCSP}
import de.uniulm.ki.panda3.domain.HasExampleDomain2
import de.uniulm.ki.panda3.plan.SymbolicPlan
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.modification.{AddOrdering, MakeLiteralsUnUnifiable}
import de.uniulm.ki.panda3.plan.ordering.SymbolicTaskOrdering
import org.scalatest.FlatSpec

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class CausalThreatTest extends FlatSpec with HasExampleDomain2 {
  /*
    * This is the plan in question:
    *
    * init:-p(x)        -p(y):ps2:p(y)----------p(y):goal
    *
    *                   :ps3:-p(z),q(z)         q(y):goal
    */
  val psinit = PlanStep(0, init, instance_variable1sort1 :: Nil)
  val psgoal = PlanStep(1, goal2, instance_variable1sort2 :: Nil)
  val ps2 = PlanStep(2, task1, instance_variable1sort2 :: Nil)
  val ps3 = PlanStep(3, task2, instance_variable1sort3 :: Nil)
  val cl = CausalLink(ps2, psgoal, psgoal.substitutedPreconditions(0))


  val planPlanSteps      = psinit :: psgoal :: ps2 :: ps3 :: Nil
  val plan: SymbolicPlan = SymbolicPlan(exampleDomain2, planPlanSteps, cl :: Nil,
                                        SymbolicTaskOrdering(Nil, planPlanSteps).addOrdering(psinit, psgoal).addOrdering(psinit, ps2).addOrdering(psinit, ps3).addOrdering(ps2, psgoal)
                                          .addOrdering(ps3, psgoal), SymbolicCSP(Set(instance_variable1sort1, instance_variable1sort2, instance_variable1sort3), Nil), psinit, psgoal)


  "Detecting causal threads" must "be possible" in {
    val flaws = plan.flaws
    val threats: Seq[CausalThreat] = flaws collect { case cl: CausalThreat => cl}

    assert(threats.size == 1)
    assert(threats exists {_.planStep == ps3})
    assert(threats exists {_.link == cl})
    assert(threats exists {_.effectOfThreater == ps3.substitutedEffects(0)})
  }

  "Resolving causal threads" must "lead to promotion/demotion and unUnification" in {
    val flaws = plan.flaws
    val threats: Seq[CausalThreat] = flaws collect { case cl: CausalThreat => cl}
    assert(threats.size == 1)

    val resolvers = threats(0).resolvants

    assert(resolvers.size == 2)
    assert(resolvers exists { case MakeLiteralsUnUnifiable(_, ne) => ne == NotEqual(instance_variable1sort2, instance_variable1sort3); case _ => false})
    assert(resolvers exists { case AddOrdering(_, OrderingConstraint(ps3, ps2)) => true; case _ => false})

    val unUnify: MakeLiteralsUnUnifiable = (resolvers collect { case r: MakeLiteralsUnUnifiable => r}).head


    val planUnUnify = plan.modify(unUnify)
    assert(!(planUnUnify.flaws exists { case c: CausalThreat => true; case _ => false}))
    assert(planUnUnify.variableConstraints.areCompatible(instance_variable1sort2, instance_variable1sort3) == Some(false))



    val promote: AddOrdering = (resolvers collect { case r: AddOrdering => r}).head
    val planPromote = plan.modify(promote)
    assert(!(planPromote.flaws exists { case c: CausalThreat => true; case _ => false}))
    assert(planPromote.orderingConstraints.lt(ps3, ps2))
  }
}
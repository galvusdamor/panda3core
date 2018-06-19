// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.csp.{CSP, NotEqual}
import de.uniulm.ki.panda3.symbolic.domain.HasExampleDomain2
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.modification.{AddOrdering, MakeLiteralsUnUnifiable}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{AllFlaws, AllModifications}
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
class CausalThreatTest extends FlatSpec with HasExampleDomain2 {
  /*
    * This is the plan in question:
    *
    * init:-p(x)        -p(y):ps2:p(y)----------p(y):goal
    *
    *                   :ps3:-p(z),q(z)         q(y):goal
    */
  val psinit = PlanStep(0, init, instance_variableSort1(1) :: Nil)
  val psgoal = PlanStep(1, goal2, instance_variableSort1(2) :: Nil)
  val ps2    = PlanStep(2, task1, instance_variableSort1(2) :: Nil)
  val ps3    = PlanStep(3, task2, instance_variableSort1(3) :: Nil)
  val cl     = CausalLink(ps2, psgoal, psgoal.substitutedPreconditions.head)


  val planPlanSteps      = psinit :: psgoal :: ps2 :: ps3 :: Nil
  val plan: Plan = Plan(planPlanSteps, cl :: Nil,
                        TaskOrdering(Nil, planPlanSteps).addOrdering(psinit, psgoal).addOrdering(psinit, ps2).addOrdering(psinit, ps3).addOrdering(ps2, psgoal)
                                          .addOrdering(ps3, psgoal), CSP(Set(instance_variableSort1(1), instance_variableSort1(2), instance_variableSort1(3)), Nil), psinit, psgoal,
                                        AllModifications, AllFlaws, Map(), Map())


  "Detecting causal threads" must "be possible" in {
    val flaws = plan.flaws
    val threats: Seq[CausalThreat] = flaws collect { case cl: CausalThreat => cl }

    assert(threats.size == 1)
    assert(threats exists { _.threater == ps3 })
    assert(threats exists { _.link == cl })
    assert(threats exists { _.effectOfThreater == ps3.substitutedEffects.head })
  }

  "Resolving causal threads" must "lead to promotion/demotion and unUnification" in {
    val flaws = plan.flaws
    val threats: Seq[CausalThreat] = flaws collect { case cl: CausalThreat => cl }
    assert(threats.size == 1)

    val resolvers = threats.head.resolvents(exampleDomain2)

    assert(resolvers.size == 2)
    assert(resolvers exists { case MakeLiteralsUnUnifiable(_, ne) => ne == NotEqual(instance_variableSort1(2), instance_variableSort1(3)); case _ => false })
    assert(resolvers exists { case AddOrdering(_, OrderingConstraint(`ps3`, `ps2`)) => true; case _ => false })

    val unUnify: MakeLiteralsUnUnifiable = (resolvers collect { case r: MakeLiteralsUnUnifiable => r }).head


    val planUnUnify = plan.modify(unUnify)
    assert(!(planUnUnify.flaws exists { case c: CausalThreat => true; case _ => false }))
    assert(planUnUnify.variableConstraints.areCompatible(instance_variableSort1(2), instance_variableSort1(3)).contains(false))



    val promote: AddOrdering = (resolvers collect { case r: AddOrdering => r }).head
    val planPromote = plan.modify(promote)
    assert(!(planPromote.flaws exists { case c: CausalThreat => true; case _ => false }))
    assert(planPromote.orderingConstraints.lt(ps3, ps2))
  }
}

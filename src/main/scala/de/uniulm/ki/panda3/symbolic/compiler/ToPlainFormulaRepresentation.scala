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

package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{Task, ReducedTask, GeneralTask, Domain}
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeTask, ReduceTasks, ReduceFormula}
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * Replaces all occurring formulas with simple formulas if possible
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ToPlainFormulaRepresentation extends DomainTransformer[Unit] {

  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan, ())

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, ())

  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    // remove quantifiers
    val replaceTasks: Seq[(Task, Task)] = domain.tasks flatMap {
      case gt@GeneralTask(_, _, parameters, _, _, prec, eff,_) =>
        val (newPrec, precVars) = prec.compileQuantors()
        val (newEff, effVars) = eff.compileQuantors()
        val newTask = gt.copy(effect = newEff, precondition = newPrec, parameters = parameters ++ precVars ++ effVars)
        //if ((precVars ++ effVars).nonEmpty) (gt, newTask) :: Nil else Nil
        (gt, newTask) :: Nil
      case _ => Nil
    }

    val compiledDomain = domain update ExchangeTask(replaceTasks.toMap)
    val compiledPlan = plan update ExchangeTask(replaceTasks.toMap)

    // reduce all formulas
    val domReduced = compiledDomain update ReduceFormula()
    val planReduced = compiledPlan update ReduceFormula()

    (domReduced.update(ReduceTasks()), planReduced.update(ReduceTasks()))
  }
}

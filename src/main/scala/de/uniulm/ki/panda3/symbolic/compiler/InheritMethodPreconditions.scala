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

import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeTask, ExchangeVariables}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{And, Exists, Formula, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object InheritMethodPreconditions extends DomainTransformerWithOutInformation {


  private def extractStaticPrecondition(f: Formula, staticPredicates: Set[Predicate]): Formula = if (f.containsOnly(staticPredicates)) f else f match {
    case And(conjuncts) => And(conjuncts map { sf => extractStaticPrecondition(sf, staticPredicates) })
    case _              => And(Nil)
  }

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val staticPredicates = domain.predicates filterNot { p => domain.primitiveTasks.exists(_.effect.containedPredicatesWithSign.exists(_._1 == p)) } toSet

    //println(staticPredicates.map(_.name).mkString("\n"))

    val taskReplacements : Seq[(Task,Task)] = domain.decompositionMethods flatMap {
      case SHOPDecompositionMethod(_, plan, prec, _, name) =>
        val subtasksOnlyInThisMethod = plan.planStepsWithoutInitGoal filter { t => domain.decompositionMethods.map(_.subPlan.planStepsWithoutInitGoal.count(_.schema == t.schema)).sum == 1 }

        if (subtasksOnlyInThisMethod.nonEmpty) {
          //println("Method " + name + ": " + subtasksOnlyInThisMethod.map(_.schema.name).mkString(" "))
          val inheritableMethodPrecs = extractStaticPrecondition(prec, staticPredicates)
          val variablesInMethodPrec = inheritableMethodPrecs.containedVariables

          //println("INHERIT " + inheritableMethodPrecs)

          subtasksOnlyInThisMethod map { ps =>
            val nonContainedVariables = variablesInMethodPrec.filterNot(ps.arguments.contains)
            val updatedFormula = inheritableMethodPrecs.update(ExchangeVariables(ps.arguments zip ps.schema.parameters toMap))

            val formulaWithExists = Exists(nonContainedVariables.toSeq, updatedFormula)
            val newTask = ps.schema match {
              case gt: GeneralTask => gt.copy(precondition = And(gt.precondition :: formulaWithExists :: Nil))
              case rt: ReducedTask =>
                GeneralTask(rt.name, rt.isPrimitive, rt.parameters, rt.artificialParametersRepresentingConstants, rt.parameterConstraints,
                            And(rt.precondition :: formulaWithExists :: Nil), rt.effect, rt.cost)

            }
            (ps.schema, newTask)
          }
        } else Nil
      case _                                               => Nil // nothing can be done
    }

    val newDomain = domain.update(ExchangeTask(taskReplacements.toMap))
    val newPlan = plan.update(ExchangeTask(taskReplacements.toMap))

    //println(HDDLWriter("foo", "foo").writeDomain(newDomain))

    //System exit 0
    (newDomain, newPlan)
  }
}

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

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.updates.ExchangePlanSteps
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.util._

/**
  * Replaces all partially ordered methods by all their total orderings
  *
  * ATTENTION: this (possibly) removes solutions from the domain
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object TotallyOrderAllMethods extends DecompositionMethodTransformer[TotallyOrderingOption] {


  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, orderingOption: TotallyOrderingOption, originalDomain: Domain):
  (Seq[DecompositionMethod], Seq[Task]) =
    ((methods :+ topMethod) flatMap { m =>
      val abstractTask = m.abstractTask
      val subPlan = m.subPlan
      val orderings = orderingOption match {
        case AllOrderings                                       => subPlan.orderingConstraints.graph.allTotalOrderings.get
        case AtMostKOrderings(1)                                => subPlan.orderingConstraints.graph.topologicalOrdering.get :: Nil
        case AtMostKOrderings(k)                                => subPlan.orderingConstraints.graph.allTotalOrderings.get take k
        case AllNecessaryOrderings | OneOfTheNecessaryOrderings => // this gets complicated
          assert(!originalDomain.hasNegativePreconditions)
          // dependency graph
          val possiblePreconditionsAndEffects = subPlan.planStepsWithoutInitGoal map { ps =>
            val reachableTasks = originalDomain.taskSchemaTransitionGraph.reachableFrom(ps.schema)
            val possiblePreconditions = reachableTasks flatMap { t => t.preconditionsAsPredicateBool } map { _._1 } toSet
            val possibleEffects = reachableTasks flatMap { t => t.effectsAsPredicateBool } toSet

            ps ->(possiblePreconditions, possibleEffects)
          } toMap

          val edges = subPlan.planStepsWithoutInitGoal flatMap { ps =>
            val perEffect = possiblePreconditionsAndEffects(ps)._2 map {
              case (pred, addEffect) =>
                val after = if (addEffect) subPlan.planStepsWithoutInitGoal filter { ops => possiblePreconditionsAndEffects(ops)._1 contains pred } else
                  subPlan.planStepsWithoutInitGoal filter { ops => possiblePreconditionsAndEffects(ops)._2.contains((pred, true)) }
                val before = if (!addEffect) subPlan.planStepsWithoutInitGoal filter { ops => possiblePreconditionsAndEffects(ops)._1 contains pred } else
                  subPlan.planStepsWithoutInitGoal filter { ops => possiblePreconditionsAndEffects(ops)._2.contains((pred, false)) }

                (before, after)
            }

            val before = perEffect flatMap { _._1 }
            val after = perEffect flatMap { _._2 }

            (before map { b => (b, ps) }) ++ (after map { a => (ps, a) })
          } distinct

          // build Graph
          val g = SimpleDirectedGraph(subPlan.planSteps, edges ++ (
            subPlan.orderingConstraints.minimalOrderingConstraints() collect { case OrderingConstraint(a, b) => (a, b) }))

          //Dot2PdfCompiler.writeDotToFile(g, "foo.pdf")

          val condensation = g.condensation

          val possibleOrderingSequences: Seq[Seq[Seq[PlanStep]]] = condensation.topologicalOrdering.get map { planSteps =>
            val remainingOrderingConstraints =
              subPlan.orderingConstraints.minimalOrderingConstraints() collect { case OrderingConstraint(a, b) if planSteps.contains(a) && planSteps.contains(b) => (a, b) }

            val psList: Seq[PlanStep] = planSteps.toSeq

            orderingOption match {
              case AllNecessaryOrderings      => SimpleDirectedGraph(psList, remainingOrderingConstraints).allTotalOrderings.get
              case OneOfTheNecessaryOrderings => SimpleDirectedGraph(psList, remainingOrderingConstraints).topologicalOrdering.get :: Nil
            }
          }

          val expandesSequences = possibleOrderingSequences.foldLeft[Seq[Seq[PlanStep]]](Nil :: Nil)({ case (orderings, next) => orderings flatMap { o => next map { n => o ++ n } } })

          expandesSequences map { s => s }
      }

      val orderingConstraints = orderings map { case ordering =>
        if (ordering.isEmpty)
          OrderingConstraint(subPlan.init, subPlan.goal) :: Nil
        else
          (if (ordering.length > 1) ordering zip ordering.tail map { case (a, b) => OrderingConstraint(a, b) } else Nil) ++
            (OrderingConstraint(subPlan.init, ordering.head) :: OrderingConstraint(ordering.last, subPlan.goal) :: Nil)
      } map { constraints => TaskOrdering(constraints, subPlan.planSteps) }

      orderingConstraints.zipWithIndex map { case (ordering, i) =>
        m match {
          case SimpleDecompositionMethod(_, _, methodName)    => SimpleDecompositionMethod(abstractTask, subPlan.copy(orderingConstraints = ordering), methodName + "_ordering_" + i)
          case SHOPDecompositionMethod(_, _, prec, eff, name) =>
            SHOPDecompositionMethod(abstractTask, subPlan.copy(orderingConstraints = ordering), prec, eff, name + "_ordering_" + i)
        }
      }

    }, Nil)

  override protected val transformationName: String = "totalOrder"
}

sealed trait TotallyOrderingOption

object AllOrderings extends TotallyOrderingOption

case class AtMostKOrderings(k: Int) extends TotallyOrderingOption

object OneRandomOrdering {
  def apply(): AtMostKOrderings = AtMostKOrderings(k = 1)
}

object AllNecessaryOrderings extends TotallyOrderingOption

object OneOfTheNecessaryOrderings extends TotallyOrderingOption

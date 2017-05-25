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
    ((methods :+ topMethod) flatMap {
      case SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
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

              SimpleDirectedGraph(psList, remainingOrderingConstraints).allTotalOrderings.get
            }

            val expandesSequences = possibleOrderingSequences.foldLeft[Seq[Seq[PlanStep]]](Nil :: Nil)({ case (orderings, next) => orderings flatMap { o => next map { n => o ++ n } } })

            expandesSequences map { s => s }
        }

        val orderingConstraints = orderings map { case ordering =>
          (ordering zip ordering.tail map { case (a, b) => OrderingConstraint(a, b) }) ++ (OrderingConstraint(subPlan.init, ordering.head) :: OrderingConstraint(ordering.last,
                                                                                                                                                                 subPlan.goal) :: Nil)
        } map { constraints => TaskOrdering(constraints, subPlan.planSteps) }

        val methods = orderingConstraints.zipWithIndex map { case (ordering, i) => SimpleDecompositionMethod(abstractTask, subPlan.copy(orderingConstraints = ordering),
                                                                                                             methodName + "_ordering_" + i)

        }
        orderingOption match {
          case AllNecessaryOrderings                             => methods
          case OneOfTheNecessaryOrderings if methods.length < 50 => methods
          case OneOfTheNecessaryOrderings                        => methods.take(200)
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
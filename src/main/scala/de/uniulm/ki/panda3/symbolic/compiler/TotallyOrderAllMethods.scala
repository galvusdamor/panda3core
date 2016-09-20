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


  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, orderingOption: TotallyOrderingOption): Seq[DecompositionMethod] =
    (methods :+ topMethod) flatMap {
      case SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
        val orderings = orderingOption match {
          case AllOrderings() => subPlan .orderingConstraints.graph.allTotalOrderings.get
          case AtMostKOrderings(1) => subPlan.orderingConstraints.graph.topologicalOrdering.get :: Nil
          case AtMostKOrderings(k) => subPlan .orderingConstraints.graph.allTotalOrderings.get take k
        }

        val orderingConstraints = orderings map { case ordering =>
          (ordering zip ordering.tail map { case (a, b) => OrderingConstraint(a, b) }) ++ (OrderingConstraint(subPlan.init, ordering.head) :: OrderingConstraint(ordering.last,
                                                                                                                                                                 subPlan.goal) :: Nil)
        } map { constraints => TaskOrdering(constraints, subPlan.planSteps) }

        orderingConstraints.zipWithIndex map { case (ordering, i) => SimpleDecompositionMethod(abstractTask, subPlan.copy(orderingConstraints = ordering),
                                                                                             methodName + "_ordering_" + i)
        }
    }

  override protected val transformationName: String = "totalOrder"
}

sealed trait TotallyOrderingOption

case class AllOrderings() extends TotallyOrderingOption

case class AtMostKOrderings(k: Int) extends TotallyOrderingOption

object OneRandomOrdering {
  def apply(): AtMostKOrderings = AtMostKOrderings(k = 1)
}
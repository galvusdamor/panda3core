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
object TotallyOrderAllMethods extends DecompositionMethodTransformer[Unit] {


  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod): Seq[DecompositionMethod] = (methods :+ topMethod) flatMap {
    case SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
      val allTotalOrderings = subPlan.orderingConstraints.graph.allTotalOrderings.get map { ordering =>
        (ordering zip ordering.tail map { case (a, b) => OrderingConstraint(a, b) }) ++ (OrderingConstraint(subPlan.init, ordering.head) :: OrderingConstraint(ordering.last,
                                                                                                                                                               subPlan.goal) :: Nil)
      } map { constraints => TaskOrdering(constraints, subPlan.planSteps) }

      allTotalOrderings map { ordering => SimpleDecompositionMethod(abstractTask, subPlan.copy(orderingConstraints = ordering), methodName) }
  }

  override protected val transformationName: String = "totalOrder"
}
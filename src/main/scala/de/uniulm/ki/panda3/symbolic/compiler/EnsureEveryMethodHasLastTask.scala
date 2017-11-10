package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object EnsureEveryMethodHasLastTask extends DecompositionMethodTransformer[Unit] {

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Unit, originalDomain: Domain): (Seq[DecompositionMethod], Seq[Task]) = {
    val noopTask = ReducedTask("noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))

    val transformedMethods = methods :+ topMethod map {
      case m if m.subPlan.orderingConstraints.graph.sinks.size == 1 => m
      case m: SimpleDecompositionMethod                             =>
        val noopPS = PlanStep(m.subPlan.planStepsAndRemovedPlanSteps.map(_.id).max + 1, noopTask, Nil)
        val newOrdering = m.subPlan.orderingConstraints.addPlanStep(noopPS).addOrderings(m.subPlan.orderingConstraints.graph.sinks map { l => OrderingConstraint(l, noopPS) }).
          addOrdering(noopPS, m.subPlan.goal).addOrdering(m.subPlan.init,noopPS)
        val newSubPlan = m.subPlan.copy(planStepsAndRemovedPlanSteps = m.subPlan.planStepsAndRemovedPlanSteps :+ noopPS, orderingConstraints = newOrdering)

        SimpleDecompositionMethod(m.abstractTask, newSubPlan, m.name)
      case _                                                        => noSupport(NONSIMPLEMETHOD)
    }


    (transformedMethods, noopTask :: Nil)
  }

  override protected val transformationName = "last_action_in_method"
}

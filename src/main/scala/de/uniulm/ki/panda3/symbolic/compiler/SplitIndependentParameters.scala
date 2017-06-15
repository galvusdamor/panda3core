package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemoveVariables, ExchangePlanSteps}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.logic.{And, Variable}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object SplitIndependentParameters extends DecompositionMethodTransformer[Unit] {

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Unit, originalDomain: Domain): (Seq[DecompositionMethod], Seq[Task]) = {
    // TODO assert non-hybridity
    val splittedMethods = (methods :+ topMethod) map { case om@SimpleDecompositionMethod(abstractTask, subPlan, methodName) =>
      // determine splittable variables
      val subPlanVariables = subPlan.variableConstraints.variables filterNot abstractTask.parameters.contains
      val splittableA = subPlanVariables filter { v => subPlan.planSteps.count({ _.argumentSet.contains(v) }) == 1 } filter {
        v => subPlan.variableConstraints.reducedDomainOf(v).size != 1 && subPlan.variableConstraints.constraints.count(_.getVariables.contains(v)) == 0
      }

      val commonParameters = (subPlanVariables -- splittableA) filter { v => subPlan.variableConstraints.reducedDomainOf(v).size != 1 }
      val planStepsOfSplittable: Set[PlanStep] = splittableA map { v => subPlan.planSteps.find(_.arguments contains v).get }

      val splittable = if (planStepsOfSplittable.size == 1) splittableA filterNot {
        v =>
          val ps = subPlan.planSteps.find(_.arguments contains v).get
          commonParameters.isEmpty || (commonParameters forall { cp => ps.arguments.contains(cp) })
      } toSeq
      else splittableA.toSeq

      if (splittable.nonEmpty) {
        val groupedSplittable: Map[PlanStep, Seq[Variable]] = splittable groupBy { v => subPlan.planSteps.find(_.argumentSet contains v).get }
        // generate a new Task for every planStep
        val exchange: Map[PlanStep, (PlanStep, DecompositionMethod, Task)] = groupedSplittable map { case (op@PlanStep(id, subSchema, arguments), freeVariables) =>
          val realArguments = arguments filterNot freeVariables.contains
          val reducedSchema = ReducedTask(subSchema.name + "_" + methodName + "_" + id, isPrimitive = false, realArguments, Nil, Nil, And(Nil), And(Nil))
          val reducedPlanStep = PlanStep(id, reducedSchema, realArguments)

          // build the new decomposition method
          val extensionInit = PlanStep(0, ReducedTask("init", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil)), Nil)
          val extensionGoal = PlanStep(1, ReducedTask("goal", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil)), Nil)
          val extensionPlanStep = op
          val allExtensionPlanSteps = extensionInit :: extensionGoal :: extensionPlanStep :: Nil
          val extensionOrdering = TaskOrdering(OrderingConstraint.allBetween(extensionInit, extensionGoal, extensionPlanStep), allExtensionPlanSteps)
          val extensionCSP = CSP(arguments.toSet, Nil)
          val extensionPlan = Plan(allExtensionPlanSteps, Nil, extensionOrdering, extensionCSP, extensionInit, extensionGoal, subPlan.isModificationAllowed, subPlan.isFlawAllowed, Map(),
                                   Map())

          val extensionMethod = SimpleDecompositionMethod(reducedSchema, extensionPlan, methodName + "_" + methodName + "_" + id)


          op ->(reducedPlanStep, extensionMethod, reducedSchema)
        }
        val planStepExchange = exchange map { case (a, (b, _, _)) => a -> b }
        val newMethods = exchange map { _._2._2 } toSeq
        val newTasks = exchange map { _._2._3 } toSeq

        val methodWithNewPlanSteps = om update ExchangePlanSteps(planStepExchange)
        val newReducedMethod = methodWithNewPlanSteps update RemoveVariables(splittable)

        (newMethods :+ newReducedMethod, newTasks)
      } else {
        (om :: Nil, Nil)
      }
    }

    (splittedMethods flatMap { _._1 }, splittedMethods flatMap { _._2 })
  }

  override protected val transformationName: String = "Split Methods"
}
package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Formula, And}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object SHOPMethodCompiler extends DomainTransformerWithOutInformation {

  /** transforms all SHOP2 style methods into ordinary methods */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val compiledMethods: Seq[(SimpleDecompositionMethod, Seq[Task])] = domain.decompositionMethods.zipWithIndex map {
      case (sm@SimpleDecompositionMethod(_, _, _), _)                                        => (sm, Nil)
      case (SHOPDecompositionMethod(abstractTask, subPlan, precondition, effect, name), idx) =>
        if (precondition.isEmpty && effect.isEmpty) (SimpleDecompositionMethod(abstractTask, subPlan, name), Nil)
        else {
          // generate a new schema that represents the decomposition method
          val containedVariables = (precondition.containedVariables ++ effect.containedVariables).toSeq.distinct
          val preconditionTaskSchema = GeneralTask("SHOP_method" + name + "_precondition", isPrimitive = true, containedVariables,
                                                   Nil, subPlan.variableConstraints.constraints filter { _.getVariables.toSet.toSet.subsetOf(containedVariables.toSet) },
                                                   precondition, effect)
          // instantiate
          val preconditionPlanStep = new PlanStep(subPlan.getFirstFreePlanStepID, preconditionTaskSchema, preconditionTaskSchema.parameters)
          // make this plan step the first actual task in the method


          /* // generate a new schema that represents the decomposition method
           val effectTaskSchema = GeneralTask("SHOP_method" + name + "_effect", isPrimitive = true, effect.containedVariables.toSeq, Nil, Nil,
                                              new And[Formula](Nil), effect)
           // instantiate
           val effectPlanStep = new PlanStep(subPlan.getFirstFreePlanStepID + 1, effectTaskSchema, effectTaskSchema.parameters)
           // make this plan step the first actual task in the method

           val additionalPlanSteps: Seq[PlanStep] = (if (!precondition.isEmpty) preconditionPlanStep :: Nil else Nil) ++ (if (!effect.isEmpty) effectPlanStep :: Nil else Nil)

           val newOrderingWithPrec =
             if (!precondition.isEmpty) subPlan.orderingConstraints.addOrderings(OrderingConstraint.allAfter(preconditionPlanStep, subPlan.planStepsWithoutInitGoal ++
               (if (!effect.isEmpty) effectPlanStep :: subPlan.goal :: Nil else subPlan.goal :: Nil): _*)).addOrdering(subPlan.init, preconditionPlanStep)
             else subPlan.orderingConstraints

           val newOrderingWithEffect =
             if (!effect.isEmpty) newOrderingWithPrec.addOrderings(OrderingConstraint.allBefore(effectPlanStep, subPlan.planStepsWithoutInitGoal ++
               (if (!precondition.isEmpty) preconditionPlanStep :: subPlan.init :: Nil else subPlan.init :: Nil): _*)).addOrdering(effectPlanStep, subPlan.goal)
             else newOrderingWithPrec
 */

          val newOrderingWithPrec = if (!precondition.isEmpty)
            subPlan.orderingConstraints.addOrderings(OrderingConstraint.allAfter(preconditionPlanStep, subPlan.planStepsWithoutInitGoal :+ subPlan.goal: _*))
              .addOrdering(subPlan.init, preconditionPlanStep)
          else subPlan.orderingConstraints

          val additionalPlanSteps: Seq[PlanStep] = preconditionPlanStep :: Nil


          (SimpleDecompositionMethod(abstractTask, new Plan(subPlan.planSteps ++ additionalPlanSteps,
                                                            subPlan.causalLinks, newOrderingWithPrec, subPlan.variableConstraints, subPlan.init,
                                                            subPlan.goal, subPlan.isModificationAllowed, subPlan.isFlawAllowed, subPlan.planStepDecomposedByMethod,
                                                            subPlan.planStepParentInDecompositionTree), name), additionalPlanSteps map { _.schema })
        }
    }
    (Domain(domain.sorts, domain.predicates, domain.tasks ++ (compiledMethods flatMap { _._2 }), compiledMethods map { _._1 }, domain.decompositionAxioms,
            domain.mappingToOriginalGrounding, domain.sasPlusRepresentation), plan)
  }
}
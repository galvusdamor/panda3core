package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Formula, And}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object SHOPMethodCompiler extends DomainTransformer[Unit] {

  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan, ())

  /** transforms all SHOP2 style methods into ordinary methods */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val compiledMethods: Seq[(SimpleDecompositionMethod, Option[Task])] = domain.decompositionMethods.zipWithIndex map {
      case (sm@SimpleDecompositionMethod(_, _,_), _)                             => (sm, None)
      case (SHOPDecompositionMethod(abstractTask, subPlan, precondition,name), idx) => if (precondition.isEmpty) (SimpleDecompositionMethod(abstractTask, subPlan, name), None)
      else {
        // generate a new schema that represents the decomposition method
        val preconditionTaskSchema = GeneralTask("SHOP_method" + idx + "_precondition", isPrimitive = true, precondition.containedVariables.toSeq, Nil, precondition, new And[Formula](Nil))
        // instantiate
        val preconditionPlanStep = new PlanStep(subPlan.getFirstFreePlanStepID, preconditionTaskSchema, preconditionTaskSchema.parameters)
        // make this plan step the first actual task in the method
        val newOrdering = subPlan.orderingConstraints.addOrderings(OrderingConstraint.allAfter(preconditionPlanStep, subPlan.planStepsWithoutInitGoal :+ subPlan.goal: _*))
          .addOrdering(subPlan.init, preconditionPlanStep)


        (SimpleDecompositionMethod(abstractTask, new Plan(subPlan.planSteps :+ preconditionPlanStep, subPlan.causalLinks, newOrdering, subPlan.variableConstraints, subPlan.init,
                                                          subPlan.goal, subPlan.isModificationAllowed, subPlan.isFlawAllowed, subPlan.planStepDecomposedByMethod,
                                                          subPlan.planStepParentInDecompositionTree), name), Some(preconditionTaskSchema))
      }
    }
    (Domain(domain.sorts, domain.predicates, domain.tasks ++ (compiledMethods collect { case (_, Some(x)) => x }), compiledMethods map { _._1 }, domain.decompositionAxioms), plan)
  }
}
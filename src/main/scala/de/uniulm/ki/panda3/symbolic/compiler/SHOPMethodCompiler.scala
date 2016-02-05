package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Formula, And}
import de.uniulm.ki.panda3.symbolic.plan.{SymbolicPlan, Plan}
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object SHOPMethodCompiler extends DomainTransformer[Unit] {

  /** transforms all SHOP2 style methods into ordinary methods */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val compiledMethods: Seq[(SimpleDecompositionMethod, Option[Task])] = domain.decompositionMethods.zipWithIndex map {
      case (sm@SimpleDecompositionMethod(_, _), _)                             => (sm, None)
      case (SHOPDecompositionMethod(abstractTask, subPlan, precondition), idx) =>
        // generate a new schema that represents the decomposition method
        val preconditionTaskSchema = GeneralTask("SHOP_method" + idx + "_precondition", isPrimitive = true, precondition.containedVariables.toSeq, Nil, precondition, new And[Formula](Nil))
        // instantiate
        val preconditionPlanStep = new PlanStep(subPlan.getFirstFreePlanStepID, preconditionTaskSchema, preconditionTaskSchema.parameters,None,None)
        // make this plan step the first actual task in the method
        val newOrdering = subPlan.orderingConstraints.addOrderings(OrderingConstraint.allAfter(preconditionPlanStep, subPlan.planStepWithoutInitGoal :+ subPlan.goal: _*))
          .addOrdering(subPlan.init, preconditionPlanStep)


        (SimpleDecompositionMethod(abstractTask, new SymbolicPlan(subPlan.planSteps :+ preconditionPlanStep, subPlan.causalLinks, newOrdering, subPlan.variableConstraints, subPlan.init,
                                                                  subPlan.goal)), Some(preconditionTaskSchema))
    }
    (Domain(domain.sorts, domain.predicates, domain.tasks ++ (compiledMethods collect {case (_,Some(x)) => x}), compiledMethods map {_._1}, domain.decompositionAxioms), plan)
  }
}

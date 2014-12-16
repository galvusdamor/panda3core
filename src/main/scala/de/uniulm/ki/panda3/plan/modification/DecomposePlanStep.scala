package de.uniulm.ki.panda3.plan.modification

import de.uniulm.ki.panda3.csp.{Variable, VariableConstraint}
import de.uniulm.ki.panda3.domain.DecompositionMethod
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}

/**
 * A modification which decomposes a given plan step using a given method
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class DecomposePlanStep(ps: PlanStep, newSubPlan: Plan, inheritedCausalLinks: Seq[CausalLink], plan: Plan) extends Modification {

  // internal variables for convenience
  private val init = newSubPlan.init
  private val goal = newSubPlan.goal

  private val initCausalLinks = newSubPlan.causalLinks filter { case CausalLink(p, c, _) => p == init}
  private val goalCausalLinks = newSubPlan.causalLinks filter { case CausalLink(p, c, _) => c == goal}

  override def addedVariables: Seq[Variable] = newSubPlan.variableConstraints.variables.toSeq

  override def addedVariableConstraints: Seq[VariableConstraint] = newSubPlan.variableConstraints.constraints

  override def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = (newSubPlan.orderingConstraints.originalOrderingConstraints filter { case OrderingConstraint(b, a) =>
    b != init && b != goal && a != init && a != goal
  }) ++ (plan.orderingConstraints.originalOrderingConstraints flatMap { case OrderingConstraint(p, ps) => addedPlanSteps map {OrderingConstraint(p, _)}
  case OrderingConstraint(ps, p)                                                                       => addedPlanSteps map {OrderingConstraint(_, p)}
  case _                                                                                               => Nil
  })

  // remove init and goal task of the subplan
  override def addedPlanSteps: Seq[PlanStep] = (newSubPlan.planSteps) filter { p => p != init && p != goal}

  override def addedCausalLinks: Seq[CausalLink] = (newSubPlan.causalLinks filter { p => !initCausalLinks.contains(p) && !goalCausalLinks.contains(p)}) ++ inheritedCausalLinks

  override def removedPlanSteps: Seq[PlanStep] = ps :: Nil

  override def removedCausalLinks: Seq[CausalLink] = plan.causalLinks filter { case CausalLink(p, c, _) => p == ps || c == ps}

  override def removedOrderingConstraints: Seq[OrderingConstraint] = plan.orderingConstraints.originalOrderingConstraints filter { case OrderingConstraint(p, c) => p == ps || c == ps}
}

object DecomposePlanStep {


  def apply(plan: Plan, ps: PlanStep): Seq[DecomposePlanStep] = plan.domain.decompositionMethods flatMap {apply(plan, ps, _)}

  def apply(plan: Plan, ps: PlanStep, method: DecompositionMethod): Seq[DecomposePlanStep] = if (ps.schema != method.abstractTask) Nil
                                                                                             else {
                                                                                               Nil
                                                                                             }
}
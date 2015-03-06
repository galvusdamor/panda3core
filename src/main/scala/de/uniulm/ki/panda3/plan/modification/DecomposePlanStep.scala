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

  private val initCausalLinks = newSubPlan.causalLinks filter {case CausalLink(p, c, _) => p == init}
  private val goalCausalLinks = newSubPlan.causalLinks filter {case CausalLink(p, c, _) => c == goal}

  override def addedVariables: Seq[Variable] = newSubPlan.variableConstraints.variables.toSeq

  override def addedVariableConstraints: Seq[VariableConstraint] = newSubPlan.variableConstraints.constraints

  override def nonInducedAddedOrderingConstraints: Seq[OrderingConstraint] = (newSubPlan.orderingConstraints.originalOrderingConstraints filter {
    case OrderingConstraint(b, a) =>
      b != init && b != goal && a != init && a != goal
  }) ++ (plan.orderingConstraints.originalOrderingConstraints flatMap {
    case OrderingConstraint(p, ps) => addedPlanSteps map {OrderingConstraint(p, _)}
    case OrderingConstraint(ps, p) => addedPlanSteps map {OrderingConstraint(_, p)}
    case _                         => Nil
  })

  // remove init and goal task of the subplan
  override def addedPlanSteps: Seq[PlanStep] = (newSubPlan.planSteps) filter {p => p != init && p != goal}

  override def addedCausalLinks: Seq[CausalLink] = (newSubPlan.causalLinks filter {p => !initCausalLinks.contains(p) && !goalCausalLinks.contains(p)}) ++ inheritedCausalLinks

  override def removedPlanSteps: Seq[PlanStep] = ps :: Nil

  override def removedCausalLinks: Seq[CausalLink] = plan.causalLinks filter {case CausalLink(p, c, _) => p == ps || c == ps}

  override def removedOrderingConstraints: Seq[OrderingConstraint] = plan.orderingConstraints.originalOrderingConstraints filter {case OrderingConstraint(p, c) => p == ps || c == ps}
}

object DecomposePlanStep {

  def apply(plan: Plan, ps: PlanStep): Seq[DecomposePlanStep] = plan.domain.decompositionMethods flatMap {apply(plan, ps, _)}

  def apply(currentPlan: Plan, ps: PlanStep, method: DecompositionMethod): Seq[DecomposePlanStep] =
    if (ps.schema != method.abstractTask) Nil
    else {
      val firstFreePlanStepID = currentPlan.getFirstFreePlanStepID
      val firstFreeVariableID = currentPlan.getFirstFreeVariableID

      val copyResult = method.subPlan.newInstance(firstFreePlanStepID, firstFreeVariableID)
      val copiedPlan = copyResult._1
      val sub = copyResult._2

      val methodIngoingCausalLinks = copiedPlan.causalLinks filter {_.producer == copiedPlan.init}
      val methodOutgoingCausalLinks = copiedPlan.causalLinks filter {_.consumer == copiedPlan.goal}


      val abstractsIngoingCausalLinks = (currentPlan.causalLinks filter {_.consumer == ps}) partition {
        case CausalLink(_, _, l) =>
          val preconditionIndex = ps.indexOfPrecondition(l, currentPlan.variableConstraints)
          val newPlanLiteral = copiedPlan.init.substitutedEffects(preconditionIndex)

          methodIngoingCausalLinks exists {case CausalLink(_, _, l) => (newPlanLiteral =?= l)(copiedPlan.variableConstraints)}
      }
      val abstractsOutgoingCausalLinks = currentPlan.causalLinks filter {_.producer == ps} partition {
        case CausalLink(_, _, l) =>
          val effectIndex = ps.indexOfEffect(l, currentPlan.variableConstraints)
          val newPlanLiteral = copiedPlan.goal.substitutedPreconditions(effectIndex)

          methodOutgoingCausalLinks exists {case CausalLink(_, _, l) => (newPlanLiteral =?= l)(copiedPlan.variableConstraints)}
      }


      ???
      //DecomposePlanStep(ps,copiedPlan,abstractsIngoingCausalLinks._1 ++ abstract)
    }
}
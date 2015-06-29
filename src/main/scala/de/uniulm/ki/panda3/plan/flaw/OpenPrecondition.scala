package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.domain.{DecompositionMethod, Domain}
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.element.PlanStep
import de.uniulm.ki.panda3.plan.modification.{DecomposePlanStep, InsertCausalLink, InsertPlanStepWithLink, Modification}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class OpenPrecondition(plan: Plan, planStep: PlanStep, precondition: Literal) extends Flaw {
  override def resolvents(domain: Domain): Seq[Modification] = InsertPlanStepWithLink(plan, planStep, precondition, domain) ++ InsertCausalLink(plan, planStep, precondition) ++
    resolverByDecompose(domain)


  private def resolverByDecompose(domain: Domain): Seq[Modification] = {

    val possibleDecompositions: Seq[(PlanStep, DecompositionMethod)] =
      (plan.planStepWithoutInitGoal filter {_ != planStep}) flatMap { ps => domain.taskSchemaTransitionGraph.canBeDirectlyDecomposedIntoVia(ps.schema) collect { case (method, task)
        if task.effects exists { case Literal(predicate, isPositive, _) => precondition.predicate == predicate && precondition.isPositive == isPositive }
      => (ps, method)
      }
      }

    possibleDecompositions flatMap { case (ps, method) => DecomposePlanStep(plan, ps, method) }
  }

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "OpenPrecondition: " + precondition.shortInfo + " of " + planStep.shortInfo
}

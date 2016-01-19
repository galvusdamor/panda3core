package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, SimpleDecompositionMethod, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.modification.{DecomposePlanStep, InsertCausalLink, InsertPlanStepWithLink, Modification}


import de.uniulm.ki.panda3.symbolic._

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class OpenPrecondition(plan: Plan, planStep: PlanStep, precondition: Literal) extends Flaw {
  override def resolvents(domain: Domain): Seq[Modification] = InsertPlanStepWithLink(plan, planStep, precondition, domain) ++ InsertCausalLink(plan, planStep, precondition) ++
    resolverByDecompose(domain)


  private def resolverByDecompose(domain: Domain): Seq[Modification] = {

    val possibleDecompositions: Seq[(PlanStep, SimpleDecompositionMethod)] =
      (plan.planStepWithoutInitGoal filter { _ != planStep }) flatMap { ps => domain.taskSchemaTransitionGraph.canBeDirectlyDecomposedIntoVia(ps.schema) map {
        case (method, reduced: ReducedTask) => (method, reduced)
        case _                              => noSupport(FORUMLASNOTSUPPORTED)
      } collect {
        case (method: SimpleDecompositionMethod, task) if task.effect.conjuncts exists {
          case Literal(predicate, isPositive, _) => precondition.predicate == predicate && precondition.isPositive == isPositive
        }                                                                   => (ps, method.asInstanceOf[SimpleDecompositionMethod])
        case (method, _) if !method.isInstanceOf[SimpleDecompositionMethod] => noSupport(NONSIMPLEMETHOD)
      }
      }

    possibleDecompositions flatMap { case (ps, method) => DecomposePlanStep(plan, ps, method) }
  }

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "OpenPrecondition: " + precondition.shortInfo + " of " + planStep.shortInfo
}

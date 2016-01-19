package de.uniulm.ki.panda3.symbolic.plan.flaw

import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.logic.Literal
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.plan.modification.{DecomposePlanStep, InsertCausalLink, InsertPlanStepWithLink, Modification}

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
      (plan.planStepWithoutInitGoal filter {_ != planStep}) flatMap { ps => domain.taskSchemaTransitionGraph.canBeDirectlyDecomposedIntoVia(ps.schema) collect { case (method, task)
        if task.effects exists { case Literal(predicate, isPositive, _) => precondition.predicate == predicate && precondition.isPositive == isPositive }
      => assert(method.isInstanceOf[SimpleDecompositionMethod], "The planner cannot yet handle non-simple decomposition methods"); (ps, method.asInstanceOf[SimpleDecompositionMethod])
      }
      }

    possibleDecompositions flatMap { case (ps, method) => DecomposePlanStep(plan, ps, method) }
  }

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "OpenPrecondition: " + precondition.shortInfo + " of " + planStep.shortInfo
}

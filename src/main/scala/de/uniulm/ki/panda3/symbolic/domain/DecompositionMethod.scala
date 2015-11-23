package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class DecompositionMethod(abstractTask: Task, subPlan: Plan) extends DomainUpdatable {
  assert(abstractTask.preconditions.size == subPlan.init.substitutedEffects.size)
  assert(abstractTask.effects.size == subPlan.goal.substitutedPreconditions.size)
  assert((abstractTask.preconditions zip subPlan.init.substitutedEffects) forall { case (l1, l2) => l1.predicate == l2.predicate && l1.isNegative == l2.isNegative })
  assert((abstractTask.effects zip subPlan.init.substitutedPreconditions) forall { case (l1, l2) => l1.predicate == l2.predicate && l1.isNegative == l2.isNegative })
  assert(abstractTask.parameters forall subPlan.variableConstraints.variables.contains)

  lazy val canGenerate: Seq[Predicate] = subPlan.planStepWithoutInitGoal flatMap {_.schema.effects map {_.predicate}}

  override def update(domainUpdate: DomainUpdate): DecompositionMethod = DecompositionMethod(abstractTask.update(domainUpdate), subPlan.update(domainUpdate))
}
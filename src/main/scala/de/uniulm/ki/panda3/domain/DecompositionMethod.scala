package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.logic.Predicate
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class DecompositionMethod(abstractTask: Task, subPlan: Plan) extends DomainUpdatable {

  lazy val canGenerate: Seq[Predicate] = subPlan.planStepWithoutInitGoal flatMap {_.schema.effects map {_.predicate}}

  override def update(domainUpdate: DomainUpdate): DecompositionMethod = DecompositionMethod(abstractTask.update(domainUpdate), subPlan.update(domainUpdate))
}
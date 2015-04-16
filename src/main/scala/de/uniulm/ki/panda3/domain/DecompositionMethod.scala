package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.logic.Predicate
import de.uniulm.ki.panda3.plan.Plan

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class DecompositionMethod(abstractTask: Task, subPlan: Plan) {

  lazy val canGenerate: Seq[Predicate] = subPlan.planStepWithoutInitGoal flatMap {_.schema.effects map {_.predicate}}
}
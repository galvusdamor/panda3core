package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.updates.RemoveEffects
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.{Predicate, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneEffects extends DomainTransformer[Set[Task]] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, reachableTasks: Set[Task]): (Domain, Plan) = {
    // determine which predicates (with negation) are actually necessary
    val necessaryPredicates: Set[(Predicate, Boolean)] = (reachableTasks + plan.goal.schema) flatMap {
      case task: ReducedTask => task.precondition.conjuncts flatMap { case Literal(predicate, isPositive, _) => (predicate, true) ::(predicate, false) :: Nil }
      case _                 => noSupport(FORUMLASNOTSUPPORTED)
    }

    val unnecessaryPredicates = domain.predicates flatMap { p => (p, true) ::(p, false) :: Nil } filterNot necessaryPredicates.contains
    val update = RemoveEffects(unnecessaryPredicates, invertedTreatment = false)
    (domain update update, plan update update)
  }
}

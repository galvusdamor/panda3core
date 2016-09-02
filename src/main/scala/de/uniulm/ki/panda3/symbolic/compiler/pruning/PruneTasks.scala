package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * Removes a given set of tasks from the domain, entailed changes (like removing decomposition methods) are _not_ performed.s
  *
  * Tasks will _not_ be removed from the initial plan
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneTasks extends DomainTransformer[Set[Task]] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, removedTasks: Set[Task]): (Domain, Plan) = {
    val reducedDomain = Domain(domain.sorts, domain.predicates, domain.tasks filterNot removedTasks.contains,
                               domain.decompositionMethods filterNot { m => removedTasks exists { rt => m containsTask rt } }, domain.decompositionAxioms)

    (reducedDomain, plan)
  }
}

object PruneUselessAbstractTasks extends DomainTransformer[Unit] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, unit: Unit): (Domain, Plan) = {
    val uselessAbstractTasks = (domain.abstractTasks filterNot { at => domain.methodsForAbstractTasks contains at }).toSet
    val reducedDomain = Domain(domain.sorts, domain.predicates, domain.tasks filterNot uselessAbstractTasks.contains,
                               domain.decompositionMethods filterNot { m => uselessAbstractTasks exists { rt => m containsTask rt } }, domain.decompositionAxioms)
    (reducedDomain, plan)
  }
}

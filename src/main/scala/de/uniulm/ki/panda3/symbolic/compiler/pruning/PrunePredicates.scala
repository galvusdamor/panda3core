package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemoveEffects, RemovePredicate}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PrunePredicates extends DomainTransformer[Unit] {

  override def transform(domain: Domain, plan: Plan, info: Unit) : (Domain,Plan)= {
    val unnecessaryPredicates = domain.predicates filter { p =>
      // it might be true and cannot be made false
      if ((plan.groundInitialStateOnlyPositivesSetOnlyPredicates contains p) && !(domain.tasks exists { _.effectsAsPredicateBool.contains((p, false)) })) {
        true
      } else if (!(plan.groundInitialStateOnlyPositivesSetOnlyPredicates contains p) && !(domain.tasks exists { _.effectsAsPredicateBool.contains((p, true)) })) {
        true
      } else {
        false
      }
    }

    (domain update RemovePredicate(unnecessaryPredicates.toSet), plan update RemovePredicate(unnecessaryPredicates.toSet))
  }
}

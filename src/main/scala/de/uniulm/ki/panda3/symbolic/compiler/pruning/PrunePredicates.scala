package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemoveEffects, RemovePredicate}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PrunePredicates extends DomainTransformer[Set[String]] {

  override def transform(domain: Domain, plan: Plan, predicatesToKeep: Set[String]): (Domain, Plan) = {
    val unnecessaryPredicates = domain.predicates filter { p =>
      // it might be true and cannot be made false
      if ((plan.groundInitialStateOnlyPositivesSetOnlyPredicates contains p) && domain.producersOfPosNeg(p)._2.isEmpty)
        true
      else if (!(plan.groundInitialStateOnlyPositivesSetOnlyPredicates contains p) && domain.consumersOf(p).isEmpty && domain.producersOfPosNeg(p)._1.isEmpty)
        true
      else
        false
    } filterNot { p => predicatesToKeep contains p.name.split("\\[").head }

    (domain update RemovePredicate(unnecessaryPredicates.toSet), plan update RemovePredicate(unnecessaryPredicates.toSet))
  }
}

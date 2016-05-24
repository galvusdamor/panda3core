package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.updates.ExchangeLiteralsByPredicate
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object RemoveNegativePreconditions extends DomainTransformer[Unit] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val predicateTranslationMap = domain.predicates map { case op@Predicate(name, args) => op ->(Predicate("+" + name, args), Predicate("-" + name, args)) } toMap

    val update = ExchangeLiteralsByPredicate(predicateTranslationMap, invertedTreatment = false)

    (domain update update, plan update update)
  }
}
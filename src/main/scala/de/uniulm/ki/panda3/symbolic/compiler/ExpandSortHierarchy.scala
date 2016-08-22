package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ExpandSortHierarchy extends DomainTransformerWithOutInformation{

  /** expands the sort hierarchy */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    val update = domain.expandSortHierarchy()

    (domain update update, plan update update)
  }
}
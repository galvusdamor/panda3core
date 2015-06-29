package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 * represents any possible domain Transformation
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait DomainTransformer[Information] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan)
}

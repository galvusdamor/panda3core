package de.uniulm.ki.panda3.compiler

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.Plan

/**
 * represents any possible domain Transformation
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait DomainTransformer[Information] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan)
}

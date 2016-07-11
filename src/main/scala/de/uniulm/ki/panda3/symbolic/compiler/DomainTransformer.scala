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

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domainAndPlan: (Domain, Plan), info: Information): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2, info)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domain: Domain, plan: Plan, info: Information): (Domain, Plan) = transform(domain, plan, info)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domainAndPlan: (Domain, Plan), info: Information): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2, info)
}


trait DomainTransformerWithOutInformation extends DomainTransformer[Unit] {
  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan): (Domain, Plan) = transform(domain, plan, ())

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domain: Domain, plan: Plan): (Domain, Plan) = transform(domain, plan)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2)
}
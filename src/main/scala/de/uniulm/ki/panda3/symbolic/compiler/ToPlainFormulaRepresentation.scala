package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.updates.{ReduceTasks, ReduceFormula}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
 * Replaces all occurring formulas with simple formulas if possible
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object ToPlainFormulaRepresentation extends DomainTransformer[Unit] {

  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan, ())

  def transform(inDomain: Domain, inPlan: Plan): (Domain, Plan) = transform(inDomain, inPlan, ())

  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    // reduce all formulas
    val domReduced = domain.update(ReduceFormula())
    val planReduced = plan.update(ReduceFormula())

    (domReduced.update(ReduceTasks()), planReduced.update(ReduceTasks()))
  }
}
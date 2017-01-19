package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{Task, ReducedTask, Domain}
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeTask, DeleteCausalLinks}
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object StripHybrid extends DomainTransformer[Unit] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, info: Unit): (Domain, Plan) = {
    // remove all causal links
    val noLinksDomain = domain update DeleteCausalLinks
    val noLinksProblem = plan update DeleteCausalLinks

    // remove all preconditions and effects of abstract tasks
    val replacementMap: Map[Task, Task] =
      domain.abstractTasks map { t => t -> ReducedTask(t.name, isPrimitive = false, t.parameters, t.artificialParametersRepresentingConstants, t.parameterConstraints, And(Nil), And(Nil))
      } toMap

    (noLinksDomain update ExchangeTask(replacementMap), noLinksProblem update ExchangeTask(replacementMap))
  }

}

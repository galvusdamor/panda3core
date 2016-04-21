package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * Prunes decomposition methods if any of their tasks do not occur in the domain any more ...
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneDecompositionMethods extends DomainTransformer[Unit] {

  override def transform(domain: Domain, plan: Plan, unit: Unit): (Domain, Plan) = {
    val validDecompositionMethods = domain.decompositionMethods filter { _.subPlan.planStepsWithoutInitGoal map {_.schema} forall domain.tasks.contains }
    val reducedDomain = Domain(domain.sorts, domain.predicates, domain.tasks, validDecompositionMethods, domain.decompositionAxioms)
    (reducedDomain, plan)
  }
}

/**
  * Prunes the domain according to a set of disallowed primitive tasks
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneHierarchy extends DomainTransformer[Set[Task]] {

  /** takes a domain, an initial plan and a set of tasks that are disallowed */
  override def transform(domain: Domain, plan: Plan, removedTasks: Set[Task]): (Domain, Plan) = {
    // 1. remove the tasks
    val initialPruning = PruneTasks.transform(domain, plan, removedTasks)

    def propagateInHierarchy(curDomain: Domain): Domain = {
      val withoutMethods = PruneDecompositionMethods.transform(curDomain, plan, ())
      val withoutAbstractTasks = PruneUselessAbstractTasks.transform(withoutMethods, ())
      if (withoutAbstractTasks._1 == curDomain) curDomain else propagateInHierarchy(withoutAbstractTasks._1)
    }

    (propagateInHierarchy(initialPruning._1), plan)
  }
}
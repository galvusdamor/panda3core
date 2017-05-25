package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan

/**
  * Prunes decomposition methods if any of their tasks do not occur in the domain any more ...
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneInconsistentDecompositionMethods extends DomainTransformer[Unit] {

  override def transform(domain: Domain, plan: Plan, unit: Unit): (Domain, Plan) = {
    val validDecompositionMethods = domain.decompositionMethods filter { _.subPlan.planStepsWithoutInitGoal map { _.schema } forall domain.taskSet.contains }
    val reducedDomain = Domain(domain.sorts, domain.predicates, domain.tasks, validDecompositionMethods, domain.decompositionAxioms,
                               domain.mappingToOriginalGrounding, domain.sasPlusRepresentation)
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
  override def transform(domain: Domain, plan: Plan, tasksToBeRemoved: Set[Task]): (Domain, Plan) = {
    // do not remove actions in the initial plan
    val removedTasks = tasksToBeRemoved diff plan.planStepTasksSet
    // 1. remove the tasks
    val initialPruning = PruneTasks.transform(domain, plan, removedTasks)

    def propagateInHierarchy(curDomain: Domain): Domain = {
      val withoutMethods = PruneInconsistentDecompositionMethods.transform(curDomain, plan, ())
      val withoutAbstractTasks = PruneUselessAbstractTasks.transform(withoutMethods, ())
      if (withoutAbstractTasks._1 == curDomain) curDomain else propagateInHierarchy(withoutAbstractTasks._1)
    }

    val propagated = propagateInHierarchy(initialPruning._1)

    // all abstract tasks should have at least one method
    assert(propagated.abstractTasks forall { at => propagated.methodsForAbstractTasks(at).nonEmpty || plan.planStepTasksSet.contains(at)})

    (propagated, plan)
  }
}

object PruneDecompositionMethods extends DomainTransformer[Seq[DecompositionMethod]] {
  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, remainingMethods: Seq[DecompositionMethod]): (Domain, Plan) = {
    val domainWithOutMethods = Domain(domain.sorts, domain.predicates, domain.tasks, remainingMethods, domain.decompositionAxioms,
                                      domain.mappingToOriginalGrounding, domain.sasPlusRepresentation)
    // run recursive pruning
    PruneHierarchy.transform(domainWithOutMethods, plan, Set())
  }
}
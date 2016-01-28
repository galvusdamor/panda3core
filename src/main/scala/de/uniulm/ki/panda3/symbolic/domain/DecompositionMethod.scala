package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic.{Formula, Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.Plan

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.util.HashMemo

/**
  * The general view onto a decomposition method: it takes an abstract task and maps it to a plan, by which this task can be replaced
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DecompositionMethod extends DomainUpdatable{

  val abstractTask: Task
  val subPlan     : Plan

  assert(!abstractTask.isPrimitive)
  (abstractTask, subPlan.init.schema) match {
    case (reducedAbstractTask: ReducedTask, _: ReducedTask) =>
      assert(reducedAbstractTask.precondition.conjuncts.size == subPlan.init.substitutedEffects.size)
      assert(reducedAbstractTask.effect.conjuncts.size == subPlan.goal.substitutedPreconditions.size)
      assert((reducedAbstractTask.precondition.conjuncts zip subPlan.init.substitutedEffects) forall { case (l1, l2) => l1.predicate == l2.predicate && l1.isNegative == l2.isNegative })
      assert((reducedAbstractTask.effect.conjuncts zip subPlan.init.substitutedPreconditions) forall { case (l1, l2) => l1.predicate == l2.predicate && l1.isNegative == l2.isNegative })
    case _                                                  => () // I cannot check anything
  }
  assert(abstractTask.parameters forall subPlan.variableConstraints.variables.contains)

  lazy val canGenerate: Seq[Predicate] = subPlan.planStepWithoutInitGoal map { _.schema } map {
    case reduced: ReducedTask => reduced
    case _                    => noSupport(FORUMLASNOTSUPPORTED)
  } flatMap { _.effect.conjuncts map { _.predicate } }

  override def update(domainUpdate: DomainUpdate): DecompositionMethod
}


/**
  * The most simple implementation of a decomposition method
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SimpleDecompositionMethod(abstractTask: Task, subPlan: Plan) extends DecompositionMethod with HashMemo{
  override def update(domainUpdate: DomainUpdate): SimpleDecompositionMethod = SimpleDecompositionMethod(abstractTask.update(domainUpdate), subPlan.update(domainUpdate))
}

/**
  * In addition to a plan, SHOPs (and SHOP2s) decomposition methods also may have preconditions. For the semantics of these preconditions see the SHOP/SHOP2 papers
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SHOPDecompositionMethod(abstractTask: Task, subPlan: Plan, methodPrecondition: Formula) extends DecompositionMethod with HashMemo {
  override def update(domainUpdate: DomainUpdate): SHOPDecompositionMethod = SHOPDecompositionMethod(abstractTask.update(domainUpdate), subPlan.update(domainUpdate),
                                                                                                     methodPrecondition update domainUpdate)
}
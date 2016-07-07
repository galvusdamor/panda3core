package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.Equal
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeLiteralsByPredicate, DomainUpdate}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.element.{PlanStep, GroundTask}
import de.uniulm.ki.util.HashMemo

/**
  * The general view onto a decomposition method: it takes an abstract task and maps it to a plan, by which this task can be replaced
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DecompositionMethod extends DomainUpdatable {

  val abstractTask: Task
  val subPlan     : Plan

  assert(!abstractTask.isPrimitive)
  (abstractTask, subPlan.init.schema) match {
    case (reducedAbstractTask: ReducedTask, _: ReducedTask) =>
      assert(reducedAbstractTask.precondition.conjuncts.size == subPlan.init.substitutedEffects.size)
      if (!(reducedAbstractTask.effect.conjuncts.size == subPlan.goal.substitutedPreconditions.size)) {
        val a = reducedAbstractTask.effect.conjuncts
        val b = subPlan.goal.substitutedPreconditions
        println("SIZE " + a.size + "!=" + b.size)
      }
      assert(reducedAbstractTask.effect.conjuncts.size == subPlan.goal.substitutedPreconditions.size)
      assert((reducedAbstractTask.precondition.conjuncts zip subPlan.init.substitutedEffects) forall { case (l1, l2) => l1.predicate == l2.predicate && l1.isNegative == l2.isNegative })
      assert((reducedAbstractTask.effect.conjuncts zip subPlan.init.substitutedPreconditions) forall { case (l1, l2) => l1.predicate == l2.predicate && l1.isNegative == l2.isNegative })
    case _                                                  => () // I cannot check anything
  }
  assert(abstractTask.parameters forall subPlan.variableConstraints.variables.contains)

  lazy val canGenerate: Seq[Predicate] = subPlan.planStepsWithoutInitGoal map { _.schema } map {
    case reduced: ReducedTask => reduced
    case _                    => noSupport(FORUMLASNOTSUPPORTED)
  } flatMap { _.effect.conjuncts map { _.predicate } }

  override def update(domainUpdate: DomainUpdate): DecompositionMethod
}


/**
  * The most simple implementation of a decomposition method
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SimpleDecompositionMethod(abstractTask: Task, subPlan: Plan) extends DecompositionMethod with HashMemo {
  override def update(domainUpdate: DomainUpdate): SimpleDecompositionMethod = domainUpdate match {
    case ExchangeLiteralsByPredicate(map, false) => SimpleDecompositionMethod(abstractTask update domainUpdate, subPlan update ExchangeLiteralsByPredicate(map, invertedTreatment = true))
    case _                                       => SimpleDecompositionMethod(abstractTask.update(domainUpdate), subPlan.update(domainUpdate))
  }


  def groundWithAbstractTaskGrounding(groundedAbstractTask: GroundTask): Seq[GroundedDecompositionMethod] = {
    val bindArguments = groundedAbstractTask.task.parameters zip groundedAbstractTask.arguments map { case (v, c) => Equal(v, c) }
    val boundCSP = subPlan.variableConstraints.addConstraints(bindArguments)
    val unboundVariables = boundCSP.variables filter { v => boundCSP.getRepresentative(v) match {
      case c: Constant    => false
      case repV: Variable => repV == v
    }
    }
    // try to bind all variables to their
    val unboundVariablesWithRemainingValues: Seq[(Variable, Seq[Constant])] = (unboundVariables map { v => (v, boundCSP.reducedDomainOf(v)) }).toSeq
    val allInstantiations = Sort allPossibleInstantiationsWithVariables unboundVariablesWithRemainingValues

    val methodInstantiations: Seq[Map[Variable, Constant]] = allInstantiations map { instantiation =>
      val additionalConstraints = instantiation map { case (v, c) => Equal(v, c) }
      val innerCSP = boundCSP addConstraints additionalConstraints
      if (innerCSP.isSolvable contains false) None
      else Some((innerCSP.variables map { v => v -> innerCSP.getRepresentative(v).asInstanceOf[Constant] }).toMap)
    } filter { _.isDefined } map { _.get }

    methodInstantiations map { args => GroundedDecompositionMethod(this, args) }
  }


}

/**
  * In addition to a plan, SHOPs (and SHOP2s) decomposition methods also may have preconditions. For the semantics of these preconditions see the SHOP/SHOP2 papers
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SHOPDecompositionMethod(abstractTask: Task, subPlan: Plan, methodPrecondition: Formula) extends DecompositionMethod with HashMemo {
  override def update(domainUpdate: DomainUpdate): SHOPDecompositionMethod = domainUpdate match {
    case ExchangeLiteralsByPredicate(map, false) =>
      SHOPDecompositionMethod(abstractTask update domainUpdate, subPlan update ExchangeLiteralsByPredicate(map, invertedTreatment = true), methodPrecondition update domainUpdate)
    case _                                       => SHOPDecompositionMethod(abstractTask update domainUpdate, subPlan update domainUpdate, methodPrecondition update domainUpdate)
  }
}

case class GroundedDecompositionMethod(decompositionMethod: DecompositionMethod, variableBinding: Map[Variable, Constant]) extends HashMemo with PrettyPrintable {
  val groundAbstractTask                    : GroundTask      = GroundTask(decompositionMethod.abstractTask, decompositionMethod.abstractTask.parameters map variableBinding)
  val subPlanGroundedTasksWithoutInitAndGoal: Seq[GroundTask] = decompositionMethod.subPlan.planStepsWithoutInitGoal map { case PlanStep(_, schema, arguments) =>
    GroundTask(schema, arguments map variableBinding)
  }

  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "method-" + decompositionMethod.abstractTask.name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = ???

  /** returns a detailed information about the object */
  override def longInfo: String = ???
}
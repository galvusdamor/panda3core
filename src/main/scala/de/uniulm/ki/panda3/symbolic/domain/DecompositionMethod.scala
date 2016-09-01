package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.{NotOfSort, OfSort, NotEqual, Equal}
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemoveEffects, ExchangeLiteralsByPredicate, DomainUpdate}
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
  val name        : String

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

  def containsTask(task: Task): Boolean =
    task == abstractTask || (subPlan.planSteps exists { _.schema == task })
}


/**
  * The most simple implementation of a decomposition method
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off covariant.equals
case class SimpleDecompositionMethod(abstractTask: Task, subPlan: Plan, name: String) extends DecompositionMethod with HashMemo {

  abstractTask match {
    case ReducedTask(_, _, _, _, _, prec, eff) =>
      // check preconditions
      prec.conjuncts foreach { case Literal(pred, isPos, _) =>
        val canBeInherited = subPlan.planStepsWithoutInitGoal map { _.schema } exists {
          case ReducedTask(_, _, _, _, _, tPre, _) => tPre.conjuncts exists { l => l.predicate == pred && l.isPositive == isPos }
          case _                                   => false
        }
        assert(canBeInherited)
      }

      // check effects
      eff.conjuncts foreach { case Literal(pred, isPos, _) =>
        val canBeInherited = subPlan.planStepsWithoutInitGoal map { _.schema } exists {
          case ReducedTask(_, _, _, _, _, _, tEff) => tEff.conjuncts exists { l => l.predicate == pred && l.isPositive == isPos }
          case _                                   => false
        }
        assert(canBeInherited)
      }
    case _                                     =>
  }

  override def update(domainUpdate: DomainUpdate): SimpleDecompositionMethod = domainUpdate match {
    case ExchangeLiteralsByPredicate(map, false) => SimpleDecompositionMethod(abstractTask update domainUpdate, subPlan update ExchangeLiteralsByPredicate(map, invertedTreatment = true),
                                                                              name)
    case RemoveEffects(toRemove, false)          => SimpleDecompositionMethod(abstractTask update domainUpdate, subPlan update RemoveEffects(toRemove, invertedTreatment = true), name)
    case _                                       => SimpleDecompositionMethod(abstractTask.update(domainUpdate), subPlan.update(domainUpdate), name)
  }


  def groundWithAbstractTaskGrounding(groundedAbstractTask: GroundTask): Seq[GroundedDecompositionMethod] = {
    val initialMapping = groundedAbstractTask.task.parameters zip groundedAbstractTask.arguments toMap

    val nonMappedVariables = subPlan.variableConstraints.variables filterNot initialMapping.contains


    def expandGrounding(mapping: Map[Variable, Constant], remainingVars: Seq[Variable]): Option[(Map[Variable, Constant], Seq[Variable])] = {
      val newlyBoundVariables = subPlan.variableConstraints.constraints collect {
        case Equal(var1, var2: Variable) if mapping.contains(var1) && !mapping.contains(var2) => var2 -> mapping(var1)
        case Equal(var1, var2: Variable) if mapping.contains(var2) && !mapping.contains(var1) => var1 -> mapping(var2)
        case Equal(vari, const: Constant) if !mapping.contains(vari)                          => vari -> const
      } toMap

      if (newlyBoundVariables.isEmpty)
        Some(mapping, remainingVars)
      else {
        val stillUnbound = remainingVars filterNot newlyBoundVariables.contains

        if (newlyBoundVariables forall { case (v, c) => v.sort.elements contains c })
          expandGrounding(mapping ++ newlyBoundVariables, stillUnbound)
        else None
      }
    }

    expandGrounding(initialMapping, nonMappedVariables.toSeq) match {
      case None                                      => Nil
      case Some((expandedMapping, unboundVariables)) =>
        val allInstantiations = Sort allPossibleInstantiationsWithVariables (unboundVariables map { v => (v, v.sort.elements) })

        def areParametersAllowed(instantiation: Map[Variable, Constant]): Boolean = subPlan.variableConstraints.constraints forall {
          case Equal(var1, var2: Variable)     => instantiation(var1) == instantiation(var2)
          case Equal(vari, const: Constant)    => instantiation(vari) == const
          case NotEqual(var1, var2: Variable)  => instantiation(var1) != instantiation(var2)
          case NotEqual(vari, const: Constant) => instantiation(vari) != const
          case OfSort(vari, sort)              => sort.elements contains instantiation(vari)
          case NotOfSort(vari, sort)           => !(sort.elements contains instantiation(vari))
        }


        val methodInstantiations: Seq[Map[Variable, Constant]] = allInstantiations map { instantiation => expandedMapping ++ instantiation } filter areParametersAllowed

        // only take those methods that inherit correctly
        methodInstantiations map { args => GroundedDecompositionMethod(this, args) } filter { gm =>
          val groundedAbstractTask = gm.groundAbstractTask
          val groundedSubtasks = gm.subPlanGroundedTasksWithoutInitAndGoal

          val inheritPreconditions = groundedAbstractTask.substitutedPreconditions forall { prec => groundedSubtasks exists { sub => sub.substitutedPreconditionsSet contains prec } }
          val inheritEffects = groundedAbstractTask.substitutedEffects forall { eff => groundedSubtasks exists { sub => sub.substitutedEffectSet contains eff } }

          inheritPreconditions && inheritEffects
        }
    }
  }

  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[SimpleDecompositionMethod] && this.hashCode == o.hashCode()) super.equals(o) else false
}

// scalastyle:on

/**
  * In addition to a plan, SHOPs (and SHOP2s) decomposition methods also may have preconditions. For the semantics of these preconditions see the SHOP/SHOP2 papers
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SHOPDecompositionMethod(abstractTask: Task, subPlan: Plan, methodPrecondition: Formula, name: String) extends DecompositionMethod with HashMemo {
  override def update(domainUpdate: DomainUpdate): SHOPDecompositionMethod = domainUpdate match {
    case ExchangeLiteralsByPredicate(map, false) =>
      SHOPDecompositionMethod(abstractTask update domainUpdate, subPlan update ExchangeLiteralsByPredicate(map, invertedTreatment = true), methodPrecondition update domainUpdate, name)
    case _                                       => SHOPDecompositionMethod(abstractTask update domainUpdate, subPlan update domainUpdate, methodPrecondition update domainUpdate, name)
  }
}

case class GroundedDecompositionMethod(decompositionMethod: DecompositionMethod, variableBinding: Map[Variable, Constant]) extends HashMemo with PrettyPrintable {
  val groundAbstractTask: GroundTask = GroundTask(decompositionMethod.abstractTask, decompositionMethod.abstractTask.parameters map variableBinding)

  lazy val subPlanPlanStepsToGrounded: Map[PlanStep, GroundTask] = decompositionMethod.subPlan.planSteps map { case ps@PlanStep(_, schema, arguments) =>
    ps -> GroundTask(schema, arguments map variableBinding)
  } toMap

  lazy val subPlanGroundedTasksWithoutInitAndGoal: Seq[GroundTask] = decompositionMethod.subPlan.planStepsWithoutInitGoal map subPlanPlanStepsToGrounded


  /** returns a string by which this object may be referenced */
  override def shortInfo: String = "method-" + decompositionMethod.name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + " of " + decompositionMethod.abstractTask.name

  /** returns a detailed information about the object */
  override def longInfo: String = mediumInfo

  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[GroundedDecompositionMethod] && this.hashCode == o.hashCode()) super.equals(o) else false
}
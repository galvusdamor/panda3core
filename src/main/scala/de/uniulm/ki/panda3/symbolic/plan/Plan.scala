package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Substitution}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.logic.{Formula, And, Literal, Variable}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw._
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{IsModificationAllowed, IsFlawAllowed}
import de.uniulm.ki.util.HashMemo

/**
  * Simple implementation of a plan, based on symbols
  *
  * - planStepParentInDecompositionTree: first entry is the parent, the second respective plan step in the methods subplan
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class Plan(planStepsAndRemovedPlanSteps: Seq[PlanStep], causalLinksAndRemovedCausalLinks: Seq[CausalLink], orderingConstraints: TaskOrdering, parameterVariableConstraints: CSP,
                init: PlanStep, goal: PlanStep, isModificationAllowed: IsModificationAllowed, isFlawAllowed: IsFlawAllowed,
                planStepDecomposedByMethod: Map[PlanStep, DecompositionMethod], planStepParentInDecompositionTree: Map[PlanStep, (PlanStep, PlanStep)]) extends
  DomainUpdatable with PrettyPrintable with HashMemo {

  assert(planStepsAndRemovedPlanSteps forall {
    ps => ps.arguments.size == ps.schema.parameters.size
  })
  //assert(planSteps forall { ps => ps.arguments forall { v => parameterVariableConstraints.variables.contains(v) } })
  planStepsAndRemovedPlanSteps foreach {
    ps => ps.arguments foreach {
      v =>
        assert(parameterVariableConstraints.variables.contains(v), ps.id + " - " + ps.schema.name + ": var " + v)
    }
  }

  planStepsWithoutInitGoal foreach {
    ps =>
      assert(orderingConstraints.lt(init, ps))
      assert(orderingConstraints.lt(ps, goal))
  }

  lazy val planSteps  : Seq[PlanStep]   = planStepsAndRemovedPlanSteps filter isPresent
  lazy val causalLinks: Seq[CausalLink] = causalLinksAndRemovedCausalLinks filter {
    cl => isPresent(cl.producer) && isPresent(cl.consumer)
  }

  lazy val flaws: Seq[Flaw] = {
    val hardFlaws = causalThreats ++ openPreconditions ++ abstractPlanSteps // ++ notInsertedByDecomposition
    if (hardFlaws.isEmpty) unboundVariables else hardFlaws
  } filter isFlawAllowed

  lazy val planStepsWithoutInitGoal: Seq[PlanStep] = planSteps filter { ps => ps != init && ps != goal }

  lazy val planStepsAndRemovedPlanStepsWithoutInitGoal: Seq[PlanStep] = planStepsAndRemovedPlanSteps filter { ps => ps != init && ps != goal }

  lazy val initAndGoal = init :: goal :: Nil

  lazy val planStepsAndRemovedWithInitAndGoalFirst = (init :: goal :: Nil) ++ planStepsAndRemovedPlanStepsWithoutInitGoal

  // TODO: this is extremely inefficient
  // add all constraints inherited from tasks to the CSP
  val variableConstraints = planSteps.foldLeft(parameterVariableConstraints)(
    { case (csp, ps) => ps.schema.parameterConstraints.foldLeft(csp)({ case (csp2, c) => csp2.addConstraint(c.substitute(ps.schemaParameterSubstitution)) }) })

  /** all causal threads in this plan */
  lazy val causalThreats: Seq[CausalThreat] =
    for {
      causalLink@CausalLink(producer, consumer, literal) <- causalLinks
      potentialThreater <- planSteps
      if potentialThreater != producer && potentialThreater != consumer && !orderingConstraints.lt(potentialThreater, producer) && !orderingConstraints.gt(potentialThreater, consumer)
      effect <- potentialThreater.substitutedEffects
      if (effect #?# literal.negate) (variableConstraints).isDefined
    } yield
      CausalThreat(this, causalLink, potentialThreater, effect)


  /** all open preconditions in this plan */
  lazy val openPreconditions: Seq[OpenPrecondition] = allPreconditions filterNot {
    case (ps, literal) => causalLinks exists {
      case CausalLink(_, consumer, condition) =>
        consumer.id == ps.id && (condition =?= literal) (variableConstraints)
    }
  } map {
    case (ps, literal) => OpenPrecondition(this, ps, literal)
  }

  /** all abstract plan steps */
  lazy val abstractPlanSteps: Seq[AbstractPlanStep] = planSteps filter {
    !_.schema.isPrimitive
  } map {
    AbstractPlanStep(this, _)
  }

  /** all variables which are not bound to a constant, yet */
  lazy val unboundVariables: Seq[UnboundVariable] =
    ((variableConstraints.variables map variableConstraints.getRepresentative) collect {
      case v: Variable => UnboundVariable(this, v)
    }).toSeq

  /** all plansteps that have no parent in the decomposition tree */
  lazy val notInsertedByDecomposition: Seq[NotInsertedByDecomposition] =
    planStepsWithoutInitGoal filterNot {
      planStepParentInDecompositionTree.contains
    } map {
      NotInsertedByDecomposition(this, _)
    }

  /** returns (if possible), whether this plan can be refined into a solution or not */
  def isSolvable: Option[Boolean] = if (!orderingConstraints.isConsistent || variableConstraints.isSolvable.contains(false)) Some(false)
  else if (flaws.isEmpty) Some(true)
  else None

  // =================== Local Helper ==================== //
  /** list containing all preconditions in this plan */
  lazy val allPreconditions: Seq[(PlanStep, Literal)] = planSteps flatMap {
    ps => ps.substitutedPreconditions map {
      prec => (ps, prec)
    }
  }

  def modify(modification: Modification): Plan = {
    val newPlanStepsIncludingRemovedOnes: Seq[PlanStep] = (planStepsAndRemovedPlanSteps diff modification.removedPlanSteps) union modification.addedPlanSteps
    val newCausalLinks = (causalLinksAndRemovedCausalLinks diff modification.removedCausalLinks) union modification.addedCausalLinks

    // compute new ordering constraints ... either update the old ones incrementally, or, if some were removed, compute them anew
    val newOrderingConstraints = if (modification.removedOrderingConstraints.isEmpty)
      orderingConstraints.removePlanSteps(modification.removedPlanSteps).addPlanSteps(modification.addedPlanSteps).addOrderings(modification.addedOrderingConstraints)
    else {
      val newOrderingConstraints = (orderingConstraints.originalOrderingConstraints diff modification.removedOrderingConstraints) union modification.addedOrderingConstraints
      TaskOrdering(newOrderingConstraints, newPlanStepsIncludingRemovedOnes)
    }


    val newVariableConstraints = if (modification.removedVariableConstraints.isEmpty && modification.removedVariables.isEmpty)
      variableConstraints.addVariables(modification.addedVariables).addConstraints(modification.addedVariableConstraints)
    else {
      val newVariableSet = (variableConstraints.variables diff modification.removedVariables.toSet) union modification.addedVariables.toSet
      val newConstraintSet = (variableConstraints.constraints diff modification.removedVariableConstraints) union modification.addedVariableConstraints

      CSP(newVariableSet, newConstraintSet)
    }

    val newPlanStepDecomposedByMethod = planStepDecomposedByMethod ++ modification.setPlanStepDecomposedByMethod
    val newPlanStepParentInDecompositionTree = planStepParentInDecompositionTree ++ modification.setParentOfPlanSteps

    Plan(newPlanStepsIncludingRemovedOnes, newCausalLinks, newOrderingConstraints, newVariableConstraints, init, goal, isModificationAllowed, isFlawAllowed,
         newPlanStepDecomposedByMethod, newPlanStepParentInDecompositionTree)
  }

  /** returns a completely new instantiated version of the current plan. This can e.g. be used to clone subplans of [[de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod]]s. */
  def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: Substitution[Variable], parentPlanStep: PlanStep):
  (Plan, Substitution[Variable], Map[PlanStep, PlanStep]) = {
    assert(planSteps.size == planStepsAndRemovedPlanSteps.size)
    val oldPlanVariables = variableConstraints.variables.toSeq

    val newVariables = oldPlanVariables zip (firstFreeVariableID until firstFreeVariableID + oldPlanVariables.size) map {
      case (v, id) =>
        if (partialSubstitution(v) == v) Variable(id, v.name, v.sort) else partialSubstitution(v)
    }
    val sub = Substitution(oldPlanVariables, newVariables)

    val planStepMapping = planSteps zip (firstFreePlanStepID until firstFreePlanStepID + planSteps.size) map {
      case (ps, id) =>
        (PlanStep(id, ps.schema, ps.arguments map sub), ps)
    }

    val newPlanSteps = planStepMapping map {
      _._1
    }

    def substitutePlanStep(oldPS: PlanStep) = newPlanSteps(planSteps.indexOf(oldPS))

    val newInit = substitutePlanStep(init)
    val newGoal = substitutePlanStep(goal)

    val newOrderingConstraints = TaskOrdering(orderingConstraints.originalOrderingConstraints map {
      case OrderingConstraint(b, a) => OrderingConstraint(substitutePlanStep(b), substitutePlanStep(a))
    }, newPlanSteps)

    // transfer the computed arrangement, this has a side effect!!!!
    newOrderingConstraints.initialiseExplicitly(0, 0, orderingConstraints.arrangement())

    // includes only "internal causal links"
    val newCausalLinks = causalLinksAndRemovedCausalLinks map {
      case CausalLink(p, c, l) => CausalLink(substitutePlanStep(p), substitutePlanStep(c),
                                             Literal(l.predicate, l.isPositive, l.parameterVariables map sub))
    }

    val newVariableConstraints = CSP(newVariables.toSet, variableConstraints.constraints map {
      _.substitute(sub)
    })

    assert(planStepDecomposedByMethod.isEmpty, "Not yet implemented")
    assert(planStepParentInDecompositionTree.isEmpty, "Not yet implemented")

    (Plan(newPlanSteps, newCausalLinks, newOrderingConstraints, newVariableConstraints, newInit, newGoal, isModificationAllowed, isFlawAllowed, Map(), Map()), sub,
      planStepMapping.toMap)
  }

  def update(domainUpdate: DomainUpdate): Plan = domainUpdate match {
    case AddVariables(newVariables)                                        => Plan(planStepsAndRemovedPlanSteps, causalLinksAndRemovedCausalLinks, orderingConstraints,
                                                                                   variableConstraints.addVariables(newVariables), init, goal, isModificationAllowed, isFlawAllowed,
                                                                                   planStepDecomposedByMethod, planStepParentInDecompositionTree)
    case AddVariableConstraints(newVariableConstraints)                    => Plan(planStepsAndRemovedPlanSteps, causalLinksAndRemovedCausalLinks, orderingConstraints,
                                                                                   variableConstraints.addConstraints(newVariableConstraints), init, goal, isModificationAllowed,
                                                                                   isFlawAllowed,
                                                                                   planStepDecomposedByMethod, planStepParentInDecompositionTree)
    case AddLiteralsToInitAndGoal(literalsInit, literalsGoal, constraints) => {
      // TODO: currently we only support this operation if all literals are 0-ary
      assert((literalsInit ++ literalsGoal) forall {
        _.parameterVariables.isEmpty
      })
      // build a new schema for init
      val initTask = init.schema match {
        case reduced: ReducedTask => ReducedTask(reduced.name, isPrimitive = true, reduced.parameters, reduced.parameterConstraints ++ constraints, reduced.precondition,
                                                 And[Literal](reduced.effect.conjuncts ++ literalsInit))
        case general: GeneralTask =>
          GeneralTask(general.name, isPrimitive = true, general.parameters, general.parameterConstraints ++ constraints, general.precondition, And[Formula](literalsInit :+ general.effect))
      }
      val newInit = PlanStep(init.id, initTask, init.arguments)

      // build a new schema for goal
      val goalTask = goal.schema match {
        case reduced: ReducedTask =>
          ReducedTask(reduced.name, isPrimitive = true, reduced.parameters, reduced.parameterConstraints ++ constraints, And[Literal](reduced.precondition.conjuncts ++ literalsGoal),
                      reduced.effect)
        case general: GeneralTask =>
          GeneralTask(general.name, isPrimitive = true, general.parameters, general.parameterConstraints ++ constraints, And[Formula](literalsGoal :+ general.precondition), general.effect)
      }
      val newGoal = PlanStep(goal.id, goalTask, goal.arguments)

      val exchangeInit = ExchangePlanStep(init, newInit)
      val exchangeGoal = ExchangePlanStep(goal, newGoal)

      Plan(planStepsAndRemovedPlanSteps map {
        _ update exchangeInit update exchangeGoal
      }, causalLinksAndRemovedCausalLinks map {
        _ update exchangeInit update exchangeGoal
      },
           orderingConstraints update exchangeInit update exchangeGoal,
           variableConstraints, newInit, goal, isModificationAllowed, isFlawAllowed, planStepDecomposedByMethod, planStepParentInDecompositionTree)
    }
    case _                                                                 => Plan(planStepsAndRemovedPlanSteps map {
      _.update(domainUpdate)
    },
                                                                                   causalLinksAndRemovedCausalLinks map {
                                                                                     _.update(domainUpdate)
                                                                                   },
                                                                                   orderingConstraints.update(domainUpdate),
                                                                                   variableConstraints.update(domainUpdate), init.update(domainUpdate), goal.update(domainUpdate),
                                                                                   isModificationAllowed, isFlawAllowed,
                                                                                   planStepDecomposedByMethod map {
                                                                                     case (ps, method) => (ps update domainUpdate, method update
                                                                                       domainUpdate)
                                                                                   },
                                                                                   planStepParentInDecompositionTree map {
                                                                                     case (ps, (parent, inPlan)) =>
                                                                                       (ps update domainUpdate, (parent update domainUpdate, inPlan update domainUpdate))
                                                                                   }
                                                                                  )
  }

  def isPresent(planStep: PlanStep): Boolean = !planStepDecomposedByMethod.contains(planStep)

  /* convenience methods to determine usable IDs */
  lazy val getFirstFreePlanStepID: Int = 1 + (planSteps foldLeft 0) { case (m, ps: PlanStep) => math.max(m, ps.id) }
  lazy val getFirstFreeVariableID: Int = 1 + (variableConstraints.variables foldLeft 0) { case (m, v: Variable) => math.max(m, v.id) }

  /** returns a short information about the object */
  override def shortInfo: String = (planSteps map { "PS " + _.mediumInfo }).mkString("\n") + "\n" + orderingConstraints.shortInfo + "\n" + (causalLinks map { _.longInfo }).mkString("\n") +
    "\n" + variableConstraints.constraints.mkString("\n")

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = shortInfo
}
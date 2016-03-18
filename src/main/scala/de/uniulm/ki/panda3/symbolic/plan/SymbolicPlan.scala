package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Substitution, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.domain.{GeneralTask, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.logic.{Formula, And, Literal, Variable}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw.{AbstractPlanStep, CausalThreat, OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.panda3.symbolic.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}
import de.uniulm.ki.util.HashMemo

/**
  * Simple implementation of a plan, based on symbols
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SymbolicPlan(planStepsAndRemovedPlanSteps: Seq[PlanStep], causalLinksAndRemovedCausalLinks: Seq[CausalLink], orderingConstraints: TaskOrdering, parameterVariableConstraints: CSP,
                        init: PlanStep, goal: PlanStep) extends Plan with HashMemo {


  assert(planStepsAndRemovedPlanSteps forall { ps => ps.arguments.size == ps.schema.parameters.size })
  //assert(planSteps forall { ps => ps.arguments forall { v => parameterVariableConstraints.variables.contains(v) } })
  planStepsAndRemovedPlanSteps foreach { ps => ps.arguments foreach { v =>
    if (!parameterVariableConstraints.variables.contains(v))
      println("foo")
    assert(parameterVariableConstraints.variables.contains(v), ps.id + " - " + ps.schema.name + ": var " + v) } }

  planStepWithoutInitGoal foreach { ps =>
    assert(orderingConstraints.lt(init, ps))
    assert(orderingConstraints.lt(ps, goal))
  }


  lazy val planSteps  : Seq[PlanStep]   = planStepsAndRemovedPlanSteps filter { _.isPresent }
  lazy val causalLinks: Seq[CausalLink] = causalLinksAndRemovedCausalLinks filter { cl => cl.producer.isPresent && cl.consumer.isPresent }


  // TODO: this is extremely inefficient
  // add all constraints inherited from tasks to the CSP
  val variableConstraints = planSteps.foldLeft(parameterVariableConstraints)(
    { case (csp, ps) => ps.schema.parameterConstraints.foldLeft(csp)({ case (csp2, c) => csp2.addConstraint(c.substitute(ps.schemaParameterSubstitution))
                                                                     })
    })

  override lazy val causalThreats: Seq[CausalThreat] =
    for {causalLink@CausalLink(producer, consumer, literal) <- causalLinks
         potentialThreater <- planSteps
         if potentialThreater != producer && potentialThreater != consumer && !orderingConstraints.lt(potentialThreater, producer) && !orderingConstraints.gt(potentialThreater, consumer)
         effect <- potentialThreater.substitutedEffects
         if (effect #?# literal.negate) (variableConstraints).isDefined} yield
      CausalThreat(this, causalLink, potentialThreater, effect)


  override lazy val openPreconditions: Seq[OpenPrecondition] = allPreconditions filterNot { case (ps, literal) => causalLinks exists { case CausalLink(_, consumer, condition) =>
    (consumer =?= ps) (variableConstraints) && (condition =?= literal) (variableConstraints)
  }
  } map { case (ps, literal) => OpenPrecondition(this, ps, literal) }


  override lazy val abstractPlanSteps: Seq[AbstractPlanStep] = planSteps filter { !_.schema.isPrimitive } map { AbstractPlanStep(this, _) }


  override lazy val unboundVariables: Seq[UnboundVariable] =
    ((variableConstraints.variables map variableConstraints.getRepresentative) collect { case v: Variable => UnboundVariable(this, v) }).toSeq

  override def isSolvable: Option[Boolean] = if (!orderingConstraints.isConsistent || variableConstraints.isSolvable.contains(false)) Some(false) else if (flaws.isEmpty) Some(true) else None

  // =================== Local Helper ==================== //
  /** list containing all preconditions in this plan */
  lazy val allPreconditions: Seq[(PlanStep, Literal)] = planSteps flatMap { ps => ps.substitutedPreconditions map { prec => (ps, prec) } }

  override def modify(modification: Modification): SymbolicPlan = {
    val newPlanStepsIncludingRemovedOnes: Seq[PlanStep] = (planStepsAndRemovedPlanSteps diff modification.removedPlanSteps) union modification.addedPlanSteps
    val newCausalLinks = (causalLinksAndRemovedCausalLinks diff modification.removedCausalLinks) union modification.addedCausalLinks

    // compute new ordering constraints ... either update the old ones incrementally, or, if some were removed, compute them anew
    val newOrderingConstraints = if (modification.removedOrderingConstraints.isEmpty)
      orderingConstraints.removePlanSteps(modification.removedPlanSteps).addPlanSteps(modification.addedPlanSteps).addOrderings(modification.addedOrderingConstraints)
    else {
      val newOrderingConstraints = (orderingConstraints.originalOrderingConstraints diff modification.removedOrderingConstraints) union modification.addedOrderingConstraints
      SymbolicTaskOrdering(newOrderingConstraints, newPlanStepsIncludingRemovedOnes)
    }


    val newVariableConstraints = if (modification.removedVariableConstraints.isEmpty && modification.removedVariables.isEmpty)
      variableConstraints.addVariables(modification.addedVariables).addConstraints(modification.addedVariableConstraints)
    else {
      val newVariableSet = (variableConstraints.variables diff modification.removedVariables.toSet) union modification.addedVariables.toSet
      val newConstraintSet = (variableConstraints.constraints diff modification.removedVariableConstraints) union modification.addedVariableConstraints

      SymbolicCSP(newVariableSet, newConstraintSet)
    }

    SymbolicPlan(newPlanStepsIncludingRemovedOnes, newCausalLinks, newOrderingConstraints, newVariableConstraints, init, goal)
  }

  /** returns a completely new instantiated version of the current plan. This can e.g. be used to clone subplans of [[de.uniulm.ki.panda3.symbolic.domain.DecompositionMethod]]s. */
  override def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: Substitution[Variable], parentPlanStep: PlanStep): (Plan, Substitution[Variable]) = {
    assert(planSteps.size == planStepsAndRemovedPlanSteps.size)
    val oldPlanVariables = variableConstraints.variables.toSeq

    val newVariables = oldPlanVariables zip (firstFreeVariableID until firstFreeVariableID + oldPlanVariables.size) map { case (v, id) =>
      if (partialSubstitution(v) == v) Variable(id, v.name, v.sort) else partialSubstitution(v)
    }
    val sub = Substitution(oldPlanVariables, newVariables)

    val newPlanSteps = planSteps zip (firstFreePlanStepID until firstFreePlanStepID + planSteps.size) map { case (ps, id) =>
      // TODO: if necessary implement this. We probably need to do topsort to so the instanciation in the correct order
      if (ps.decomposedByMethod.isDefined) noSupport(REINSTANTIATINGPLANSINOUTSIDEMETHODS)
      PlanStep(id, ps.schema, ps.arguments map sub, None, Some(parentPlanStep))
    }

    def substitutePlanStep(oldPS: PlanStep) = newPlanSteps(planSteps.indexOf(oldPS))

    val newInit = substitutePlanStep(init)
    val newGoal = substitutePlanStep(goal)

    val newOrderingConstraints = SymbolicTaskOrdering(orderingConstraints.originalOrderingConstraints map {
      case OrderingConstraint(b, a) => OrderingConstraint(substitutePlanStep(b), substitutePlanStep(a))
    }, newPlanSteps)

    // transfer the computed arrangement, this has a side effect!!!!
    newOrderingConstraints.initialiseExplicitly(0, 0, orderingConstraints.arrangement())

    // includes only "internal causal links"
    val newCausalLinks = causalLinksAndRemovedCausalLinks map { case CausalLink(p, c, l) => CausalLink(substitutePlanStep(p), substitutePlanStep(c),
                                                                                                       Literal(l.predicate, l.isPositive, l.parameterVariables map sub))
    }

    val newVariableConstraints = SymbolicCSP(newVariables.toSet, variableConstraints.constraints map { _.substitute(sub) })

    (SymbolicPlan(newPlanSteps, newCausalLinks, newOrderingConstraints, newVariableConstraints, newInit, newGoal), sub)
  }

  override def update(domainUpdate: DomainUpdate): SymbolicPlan = domainUpdate match {
    case AddVariables(newVariables)                     => SymbolicPlan(planStepsAndRemovedPlanSteps, causalLinksAndRemovedCausalLinks, orderingConstraints,
                                                                        variableConstraints.addVariables(newVariables), init, goal)
    case AddVariableConstraints(newVariableConstraints) => SymbolicPlan(planStepsAndRemovedPlanSteps, causalLinksAndRemovedCausalLinks, orderingConstraints,
                                                                        variableConstraints.addConstraints(newVariableConstraints), init, goal)
    case AddLiteralsToInit(literals, constraints)       => {
      // TODO: currently we only support this operation if all literals are 0-ary
      assert(literals forall { _.parameterVariables.isEmpty })
      // build a new schema for init
      val initTask = init.schema match {
        case reduced: ReducedTask => ReducedTask(reduced.name, isPrimitive = true, reduced.parameters, reduced.parameterConstraints ++ constraints, reduced.precondition,
                                                 And[Literal](reduced.effect.conjuncts ++ literals))
        case general: GeneralTask =>
          GeneralTask(general.name, isPrimitive = true, general.parameters, general.parameterConstraints ++ constraints, general.precondition, And[Formula](literals :+ general.effect))
      }
      val newInit = PlanStep(init.id, initTask, init.arguments, init.decomposedByMethod, init.parentInDecompositionTree)
      val exchange = ExchangePlanStep(init, newInit)
      SymbolicPlan(planStepsAndRemovedPlanSteps map { _ update exchange }, causalLinksAndRemovedCausalLinks map { _ update exchange }, orderingConstraints update exchange,
                   variableConstraints, newInit, goal)
    }
    case _                                              => SymbolicPlan(planStepsAndRemovedPlanSteps map { _.update(domainUpdate) },
                                                                        causalLinksAndRemovedCausalLinks map { _.update(domainUpdate) }, orderingConstraints.update(domainUpdate),
                                                                        variableConstraints.update(domainUpdate), init.update(domainUpdate), goal.update(domainUpdate))
  }
}
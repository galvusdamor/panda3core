package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Substitution, SymbolicCSP}
import de.uniulm.ki.panda3.symbolic.domain.updates.{AddVariableConstraints, AddVariables, DomainUpdate, ExchangePlanStep}
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Variable}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw.{AbstractPlanStep, CausalThreat, OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.panda3.symbolic.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}

/**
 * Simple implementation of a plan, based on symbols
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicPlan(planSteps: Seq[PlanStep], causalLinks: Seq[CausalLink], orderingConstraints: TaskOrdering, variableConstraints: CSP, init: PlanStep, goal: PlanStep) extends Plan {

  override lazy val causalThreats: Seq[CausalThreat] =
    for {causalLink@CausalLink(producer, consumer, literal) <- causalLinks
         potentialThreater <- planSteps
         if potentialThreater != producer && potentialThreater != consumer && !orderingConstraints.lt(potentialThreater, producer) && !orderingConstraints.gt(potentialThreater, consumer)
         effect <- potentialThreater.substitutedEffects
         if (effect #?# literal.negate)(variableConstraints) != None} yield
    CausalThreat(this, causalLink, potentialThreater, effect)


  override lazy val openPreconditions: Seq[OpenPrecondition] = allPreconditions filterNot { case (ps, literal) => causalLinks exists { case CausalLink(_, consumer,
  condition) => (consumer =?= ps)(
    variableConstraints) && (condition =?= literal)(variableConstraints)
  }
  } map { case (ps, literal) => OpenPrecondition(this, ps, literal) }


  override lazy val abstractPlanSteps: Seq[AbstractPlanStep] = planSteps filter {!_.schema.isPrimitive} map {AbstractPlanStep(this, _)}


  override lazy val unboundVariables: Seq[UnboundVariable] =
    ((variableConstraints.variables map variableConstraints.getRepresentative) collect { case v: Variable => UnboundVariable(this, v) }).toSeq

  override def isSolvable: Option[Boolean] = if (!orderingConstraints.isConsistent || variableConstraints.isSolvable == Some(false)) Some(false)
  else if (flaws.size == 0) Some(true)
  else
    None

  // =================== Local Helper ==================== //
  /** list containing all preconditions in this plan */
  lazy val allPreconditions: Seq[(PlanStep, Literal)] = planSteps flatMap { ps => ps.substitutedPreconditions map { prec => (ps, prec) } }

  override def modify(modification: Modification): SymbolicPlan = {
    val newPlanSteps: Seq[PlanStep] = (planSteps diff modification.removedPlanSteps) union modification.addedPlanSteps
    val newCausalLinks = (causalLinks diff modification.removedCausalLinks) union modification.addedCausalLinks

    // compute new ordering constraints ... either update the old ones incrementally, or, if some were removed, compute them anew
    val newOrderingConstraints = if (modification.removedOrderingConstraints.size == 0)
      orderingConstraints.removePlanSteps(modification.removedPlanSteps).addPlanSteps(modification.addedPlanSteps).addOrderings(modification.addedOrderingConstraints)
    else {
      val newOrderingConstraints = (orderingConstraints.originalOrderingConstraints diff modification.removedOrderingConstraints) union modification.addedOrderingConstraints
      SymbolicTaskOrdering(newOrderingConstraints, newPlanSteps)
    }


    val newVariableConstraints = if (modification.removedVariableConstraints.size == 0 && modification.removedVariables.size == 0)
      variableConstraints.addVariables(modification.addedVariables).addConstraints(modification.addedVariableConstraints)
    else {
      val newVariableSet = (variableConstraints.variables diff modification.removedVariables.toSet) union modification.addedVariables.toSet
      val newConstraintSet = (variableConstraints.constraints diff modification.removedVariableConstraints) union modification.addedVariableConstraints

      SymbolicCSP(newVariableSet, newConstraintSet)
    }

    SymbolicPlan(newPlanSteps, newCausalLinks, newOrderingConstraints, newVariableConstraints, init, goal)
  }

  /** returns a completely new instantiated version of the current plan. This can e.g. be used to clone subplans of [[de.uniulm.ki.panda3.domain.DecompositionMethod]]s. */
  override def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: Substitution[Variable]): (Plan, Substitution[Variable]) = {
    val oldPlanVariables = variableConstraints.variables.toSeq

    val newVariables = oldPlanVariables zip (firstFreeVariableID until firstFreeVariableID + oldPlanVariables.size) map { case (v, id) =>
      if (partialSubstitution(v) == v) Variable(id, v.name, v.sort) else partialSubstitution(v)
    }
    val sub = Substitution(oldPlanVariables, newVariables)

    val newPlanSteps = planSteps zip (firstFreePlanStepID until firstFreePlanStepID + planSteps.size) map { case (ps, id) => PlanStep(id, ps.schema, ps.arguments map sub)
    }

    def substitutePlanStep(oldPS: PlanStep) = newPlanSteps(planSteps.indexOf(oldPS))

    val newInit = substitutePlanStep(init)
    val newGoal = substitutePlanStep(goal)

    val newOrderingConstraints = SymbolicTaskOrdering(orderingConstraints.originalOrderingConstraints map { case OrderingConstraint(b, a) => OrderingConstraint(substitutePlanStep(b),
      substitutePlanStep(a))
    }, newPlanSteps)
    // transfer the computed arrangement, this has a side effect!!!!
    newOrderingConstraints.initialiseExplicitly(0, 0, orderingConstraints.arrangement())

    // includes only "internal causal links"
    val newCausalLinks = causalLinks map { case CausalLink(p, c, l) => CausalLink(substitutePlanStep(p), substitutePlanStep(c),
      Literal(l.predicate, l.isPositive, l.parameterVariables map sub))
    }

    val newVariableConstraints = SymbolicCSP(newVariables.toSet, variableConstraints.constraints map {_.substitute(sub)})

    (SymbolicPlan(newPlanSteps, newCausalLinks, newOrderingConstraints, newVariableConstraints, newInit, newGoal), sub)
  }

  override def update(domainUpdate: DomainUpdate): SymbolicPlan = domainUpdate match {
    case AddVariables(newVariables)                     => SymbolicPlan(planSteps, causalLinks, orderingConstraints, variableConstraints.addVariables(newVariables), init, goal)
    case AddVariableConstraints(newVariableConstraints) => SymbolicPlan(planSteps, causalLinks, orderingConstraints, variableConstraints.addConstraints(newVariableConstraints), init, goal)
    case ExchangePlanStep(oldPS, newPS)                 => SymbolicPlan(planSteps map { ps => if (ps == oldPS) newPS else ps }, causalLinks map {_.update(domainUpdate)},
      orderingConstraints.update(domainUpdate), variableConstraints, if (oldPS == init) newPS else init, if (oldPS == goal) newPS else goal)

    case _ => SymbolicPlan(planSteps map {_.update(domainUpdate)}, causalLinks map {_.update(domainUpdate)}, orderingConstraints.update(domainUpdate), variableConstraints.update
      (domainUpdate), init.update(domainUpdate), goal.update(domainUpdate))
  }

}
package de.uniulm.ki.panda3.plan

import de.uniulm.ki.panda3.csp.{CSP, Substitution, SymbolicCSP, Variable}
import de.uniulm.ki.panda3.logic.Literal
import de.uniulm.ki.panda3.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.plan.flaw.{CausalThreat, OpenPrecondition, UnboundVariable}
import de.uniulm.ki.panda3.plan.modification.Modification
import de.uniulm.ki.panda3.plan.ordering.{SymbolicTaskOrdering, TaskOrdering}

/**
 * Simple implementation of a plan, based on symbols
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class SymbolicPlan(planSteps: Seq[PlanStep], causalLinks: Seq[CausalLink], orderingConstraints: TaskOrdering, variableConstraints: CSP, init: PlanStep, goal: PlanStep) extends Plan {

  /** list of all causal threads in this plan */
  override lazy val causalThreads: Seq[CausalThreat] =
    for {causalLink@CausalLink(producer, consumer, literal) <- causalLinks
         planStep <- planSteps
         if planStep != producer && planStep != consumer && !orderingConstraints.lt(planStep, producer) && !orderingConstraints.gt(planStep, consumer)
         effect <- planStep.substitutedEffects
         if (effect #?# literal.negate)(variableConstraints) != None} yield
    CausalThreat(this, causalLink, planStep, effect)


  /** list fo all open preconditions in this plan */
  override lazy val openPreconditions: Seq[OpenPrecondition]    = allPreconditions filterNot { case (ps, literal) => causalLinks exists { case CausalLink(_, consumer,
                                                                                                                                                          condition) => (consumer =?= ps)(
    variableConstraints) && (condition =?= literal)(variableConstraints)
  }
  } map { case (ps, literal) => OpenPrecondition(this, ps, literal) }
  /** list containing all preconditions in this plan */
  lazy          val allPreconditions : Seq[(PlanStep, Literal)] = planSteps flatMap { ps => ps.substitutedPreconditions map { prec => (ps, prec) } }


  override lazy val unboundVariables: Seq[UnboundVariable] = (variableConstraints.variables collect { case v if variableConstraints.getRepresentative(v).isLeft => variableConstraints
    .getRepresentative(v) match {
    case Left(v) => v
  }
  } map { case v => UnboundVariable(this, v)
  }).toSeq

  /** returns (if possible), whether this plan can be refined into a solution or not */
  override def isSolvable: Option[Boolean] = if (!orderingConstraints.isConsistent || variableConstraints.isSolvable == Some(false)) Some(false)
  else if (flaws.size == 0) Some(true)
  else
    None

  // =================== Local Helper ==================== //
  override def modify(modification: Modification): SymbolicPlan = {
    val newPlanSteps: Seq[PlanStep] = (planSteps diff modification.removedPlanSteps) union modification.addedPlanSteps
    val newCausalLinks = (causalLinks diff modification.removedCausalLinks) union modification.addedCausalLinks

    // compute new ordering constraints ... either update the old ones incrementally, or ,if some were removed, compute them anew
    val newOrderingConstraints = if (modification.removedOrderingConstraints.size == 0)
      orderingConstraints.addPlanSteps(modification.addedPlanSteps).addOrderings(modification.addedOrderingConstraints)
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
  override def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: Substitution): (Plan, Substitution) = {
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

    val newOrderingConstraints = SymbolicTaskOrdering(
      orderingConstraints.originalOrderingConstraints map { case OrderingConstraint(b, a) => OrderingConstraint(substitutePlanStep(b), substitutePlanStep(a))
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
}
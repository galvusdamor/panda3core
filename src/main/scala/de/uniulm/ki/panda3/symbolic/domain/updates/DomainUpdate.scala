package de.uniulm.ki.panda3.symbolic.domain.updates

import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Task}
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate, Sort, Variable}
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep

/**
 * represents a generic domain update.
 *
 * This is an empty marker trait
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait DomainUpdate{}

case class AddTask(newTasks: Seq[Task]) extends DomainUpdate {}

case class AddVariableConstraints(newConstraints: Seq[VariableConstraint]) extends DomainUpdate {}

case class AddMethod(newMethods: Seq[DecompositionMethod]) extends DomainUpdate {}

case class AddLiteralsToInit(literals: Seq[Literal], variableConstraints: Seq[VariableConstraint]) extends DomainUpdate {}

case class AddPredicate(newPredicates: Seq[Predicate]) extends DomainUpdate {}

case class AddVariables(newVariables: Seq[Variable]) extends DomainUpdate {}

case class ExchangePlanStep(oldPlanStep: PlanStep, newPlanStep: PlanStep) extends DomainUpdate {}

case class ExchangeSorts(exchangeMap: Map[Sort, Sort]) extends DomainUpdate {}

case class ExchangeTask(exchange: Map[Task, Task]) extends DomainUpdate {}

case class ExchangeTaskSchemaInMethods(exchange: Map[Task, Task]) extends DomainUpdate {}


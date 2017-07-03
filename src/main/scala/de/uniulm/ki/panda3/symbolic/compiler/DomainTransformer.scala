package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.updates.RemoveVariables
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * represents any possible domain Transformation
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait DomainTransformer[Information] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domainAndPlan: (Domain, Plan), info: Information): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2, info)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domain: Domain, plan: Plan, info: Information): (Domain, Plan) = transform(domain, plan, info)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domainAndPlan: (Domain, Plan), info: Information): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2, info)
}

trait DomainTransformerWithOutInformation extends DomainTransformer[Unit] {
  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domain: Domain, plan: Plan): (Domain, Plan) = transform(domain, plan, ())

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def transform(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domain: Domain, plan: Plan): (Domain, Plan) = transform(domain, plan)

  /** takes a domain, an initial plan and some additional Information and transforms them */
  def apply(domainAndPlan: (Domain, Plan)): (Domain, Plan) = transform(domainAndPlan._1, domainAndPlan._2)
}

trait DecompositionMethodTransformer[Information] extends DomainTransformer[Information] {

  protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Information, originalDomain: Domain): (Seq[DecompositionMethod], Seq[Task])

  protected val transformationName: String

  protected val allowToRemoveTopMethod = true


  override def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan) = {
    // create an artificial method
    // TODO not yet correct for hybrid planning problems
    val initAndGoalNOOP = ReducedTask("__noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))
    val topInit = PlanStep(plan.init.id, initAndGoalNOOP, Nil)
    val topGoal = PlanStep(plan.goal.id, initAndGoalNOOP, Nil)
    val initialPlanWithout = plan.replaceInitAndGoal(topInit, topGoal, plan.init.arguments ++ plan.goal.arguments)
    val topTask = ReducedTask("__" + transformationName + "Compilation__top", isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil))
    val topMethod = SimpleDecompositionMethod(topTask, initialPlanWithout, "__top")

    val (extendedMethods, newTasks) = transformMethods(domain.decompositionMethods, topMethod, info, domain)

    val numberOfTopMethods = extendedMethods count { _.abstractTask == topTask }
    if (numberOfTopMethods == 0) {
      // if the compiler does not want to add a top method, it's ok
      (domain.copy(decompositionMethods = extendedMethods, tasks = domain.tasks ++ newTasks), plan)
    } else if (numberOfTopMethods == 1 && allowToRemoveTopMethod) {
      // regenerate the initial plan, as it may have changed
      val remainingTopMethod = (extendedMethods find { _.abstractTask == topTask }).get.subPlan
      val newPlan = remainingTopMethod.replaceInitAndGoal(plan.init, plan.goal, Nil)
      (domain.copy(decompositionMethods = extendedMethods filterNot { _.abstractTask == topTask }, tasks = domain.tasks ++ newTasks), newPlan)
    } else {
      // generate a new
      val topPS = PlanStep(2, topTask, Nil)
      val planSteps: Seq[PlanStep] = plan.init :: plan.goal :: topPS :: Nil
      val ordering = TaskOrdering(OrderingConstraint.allBetween(plan.init, plan.goal, topPS), planSteps)
      val unnecessaryVariables = plan.variableConstraints.variables filterNot { v => planSteps exists { _.arguments.contains(v) } } toSeq
      val initialPlan = Plan(planSteps, Nil, ordering, plan.variableConstraints update RemoveVariables(unnecessaryVariables), plan.init, plan.goal,
                             plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())

      (domain.copy(decompositionMethods = extendedMethods, tasks = domain.tasks ++ newTasks :+ topTask), initialPlan)
    }
  }
}
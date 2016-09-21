package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, SimpleDecompositionMethod, ReducedTask, Domain}
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

  protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, info: Information): Seq[DecompositionMethod]

  protected val transformationName: String


  override def transform(domain: Domain, plan: Plan, info: Information): (Domain, Plan) = {
    // create an artificial method
    // TODO not yet correct for hybrid planning problems
    val initAndGoalNOOP = ReducedTask("__noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))
    val topInit = PlanStep(plan.init.id, initAndGoalNOOP, Nil)
    val topGoal = PlanStep(plan.goal.id, initAndGoalNOOP, Nil)
    val initialPlanWithout = plan.replaceInitAndGoal(topInit, topGoal)
    val topTask = ReducedTask("__" + transformationName + "Compilation__top", isPrimitive = false, Nil, Nil, Nil, And(Nil), And(Nil))
    val topMethod = SimpleDecompositionMethod(topTask, initialPlanWithout, "__top")

    val extendedMethods: Seq[DecompositionMethod] = transformMethods(domain.decompositionMethods, topMethod, info)

    if ((extendedMethods count { _.abstractTask == topTask }) == 1) {
      // regenerate the initial plan, as it may have changed
      val remainingTopMethod = (extendedMethods find { _.abstractTask == topTask }).get.subPlan
      val newPlan = remainingTopMethod.replaceInitAndGoal(plan.init,plan.goal)
      (domain.copy(decompositionMethods = extendedMethods filterNot { _.abstractTask == topTask }), newPlan)
    } else {
      // generate a new
      val topPS = PlanStep(2, topTask, Nil)
      val planSteps: Seq[PlanStep] = plan.init :: plan.goal :: topPS :: Nil
      val ordering = TaskOrdering(OrderingConstraint.allBetween(plan.init, plan.goal, topPS), planSteps)
      val initialPlan = Plan(planSteps, Nil, ordering, plan.variableConstraints, plan.init, plan.goal, plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())

      (domain.copy(decompositionMethods = extendedMethods, tasks = domain.tasks :+ topTask), initialPlan)
    }
  }
}
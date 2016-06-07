package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Equal, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep, GroundTask}
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Grounding extends DomainTransformer[GroundedReachabilityAnalysis] {


  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, reachabilityAnalysis: GroundedReachabilityAnalysis): (Domain, Plan) = {
    // ----- Predicates
    val groundedPredicates: Map[Predicate, Map[Seq[Constant], Predicate]] =
      (reachabilityAnalysis.reachableGroundLiterals map { case GroundLiteral(predicate, _, parameter) => GroundLiteral(predicate, true, parameter) }).distinct groupBy { _.predicate } map {
        case (predicate, litList) =>
          val argumentMapping = litList groupBy { _.parameter } map { case (args, lits) =>
            assert(lits.length == 1)
            val onlyLit = lits.head
            val newPredicateName = onlyLit.predicate.name + ((onlyLit.parameter map { _.name }) mkString("[", ",", "]"))
            (args, Predicate(newPredicateName, Nil))
          }
          (predicate, argumentMapping)
      }
    val allGroundedPredicates = groundedPredicates flatMap { _._2.values }

    // ----- Tasks
    def groundTaskToGroundedTask(groundTask: GroundTask): Task = groundTask match {
      case g@GroundTask(ReducedTask(name, isPrimitive, _, _, _, _), constants) =>
        val newTaskName = name + ((constants map { _.name }) mkString("[", ",", "]"))
        // ground precondition and effect
        val preconditionLiterals = g.substitutedPreconditions map {
          case GroundLiteral(predicate, isPositive, parameter) =>
            Literal(groundedPredicates(predicate)(parameter), isPositive, Nil)
        }
        val effectLiterals = g.substitutedEffects map {
          case GroundLiteral(predicate, isPositive, parameter) => Literal(groundedPredicates(predicate)(parameter), isPositive, Nil)
        }

        // TODO: here we assume that the grounding we get always fulfills the parameter constraints ... we have to assert this at some point
        ReducedTask(newTaskName, isPrimitive, Nil, Nil, And(preconditionLiterals), And(effectLiterals))
      case _                                                                   => noSupport(FORUMLASNOTSUPPORTED)
    }

    val alreadyGroundedVariableMapping = plan.variableConstraints.variables map { vari => (vari, plan.variableConstraints.getRepresentative(vari)) } collect {
      case (v, c: Constant) => (v, c)
    } toMap

    val allTasksAnalysis = reachabilityAnalysis.reachableGroundedTasks ++ reachabilityAnalysis.additionalTaskNeededToGround ++ reachabilityAnalysis.reachableGroundMethodInitAndGoalActions
    val initAndGoalInitialTask = GroundTask(plan.init.schema, plan.init.schema.parameters map alreadyGroundedVariableMapping) ::
      GroundTask(plan.goal.schema, plan.goal.schema.parameters map alreadyGroundedVariableMapping) :: Nil
    val groundedTasks: Map[Task, Map[Seq[Constant], (Task, GroundTask)]] = (allTasksAnalysis ++ initAndGoalInitialTask).distinct groupBy { _.task } map { case (t, groundActs) =>
      val taskMap: Map[Seq[Constant], (Task, GroundTask)] = groundActs groupBy { _.arguments } map { case (args, taskList) =>
        assert(taskList.length == 1)
        (args, (groundTaskToGroundedTask(taskList.head), taskList.head))
      }
      (t, taskMap)
    }
    val allGroundedTasks = groundedTasks flatMap { _._2.values } collect {
      case (task, groundTask) if !(domain.hiddenTasks contains groundTask.task) && !(initAndGoalInitialTask contains groundTask) => task
    }


    // helper methods
    def groundPS(oldPS: PlanStep, mapVariable: Variable => Constant): PlanStep = PlanStep(oldPS.id, groundedTasks(oldPS.schema)(oldPS.arguments map mapVariable)._1, Nil)

    def groundPlan(plan: Plan, mapVariable: Variable => Constant): Plan = {
      // check whether it is possible to ground the method
      plan.planSteps foreach { ps => assert(groundedTasks(ps.schema) contains (ps.arguments map mapVariable)) }

      // create the inner plan
      val groundedPlanStepMapping = (plan.planSteps map { ps => (ps, groundPS(ps, mapVariable)) }).toMap
      val actualGroundedPlansteps = groundedPlanStepMapping.values.toSeq

      val orderingConstraints = plan.orderingConstraints.originalOrderingConstraints map {
        case OrderingConstraint(before, after) => OrderingConstraint(groundedPlanStepMapping(before), groundedPlanStepMapping(after))
      }

      val causalLinks = plan.causalLinks map { case CausalLink(producer, consumer, condition) =>
        // get the correct ground Literal
        val groundCondition = condition match {
          case Literal(predicate, isPositive, parameterVariables) =>
            val parameterConstants = parameterVariables map mapVariable
            Literal(groundedPredicates(predicate)(parameterConstants), isPositive, Nil)
        }
        CausalLink(groundedPlanStepMapping(producer), groundedPlanStepMapping(consumer), groundCondition)
      }

      Plan(actualGroundedPlansteps, causalLinks, TaskOrdering(orderingConstraints, actualGroundedPlansteps), CSP(Set(), Nil),
           groundedPlanStepMapping(plan.init), groundedPlanStepMapping(plan.goal), plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())
    }


    // ----- Decomposition methods
    val groundedDecompositionMethods = reachabilityAnalysis.reachableGroundMethods ++ reachabilityAnalysis.additionalMethodsNeededToGround map {
      case GroundedDecompositionMethod(liftedMethod, variableBinding) =>
        // ground the abstract actions
        val groundedAbstractTask = groundTaskToGroundedTask(GroundTask(liftedMethod.abstractTask, liftedMethod.abstractTask.parameters map variableBinding))
        SimpleDecompositionMethod(groundedAbstractTask, groundPlan(liftedMethod.subPlan, variableBinding))
    }

    // check whether we have to insert a new abstract task, as the initial plan might not be completely grounded


    val initialPlan = if (reachabilityAnalysis.additionalTaskNeededToGround.isEmpty) {
      groundPlan(plan, alreadyGroundedVariableMapping)
    } else {
      assert(reachabilityAnalysis.additionalTaskNeededToGround.size == 1)
      // ground the plan containing the inital abstract task
      val topTask = reachabilityAnalysis.additionalTaskNeededToGround.head.task
      val topPS = PlanStep(2, topTask, topTask.parameters)
      val planSteps: Seq[PlanStep] = plan.init :: plan.goal :: topPS :: Nil
      val ordering = TaskOrdering(OrderingConstraint.allBetween(plan.init, plan.goal, topPS), planSteps)
      val topPlan = Plan(planSteps, Nil, ordering, plan.variableConstraints, plan.init, plan.goal, plan.isModificationAllowed, plan.isFlawAllowed, Map(), Map())

      groundPlan(topPlan, alreadyGroundedVariableMapping)
    }


    // TODO handle decomposition axioms ?!?
    assert(domain.decompositionAxioms.isEmpty)
    (Domain(Nil, allGroundedPredicates.toSeq, allGroundedTasks.toSeq, groundedDecompositionMethods, Nil), initialPlan)
  }

}
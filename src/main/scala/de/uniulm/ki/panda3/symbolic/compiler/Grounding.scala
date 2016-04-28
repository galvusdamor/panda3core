package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Equal, VariableConstraint}
import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, ReducedTask, Task, Domain}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedReachabilityAnalysis
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
            val newPredicateName = onlyLit.predicate.name + ((onlyLit.parameter map { _.name }) mkString("(", ",", ")"))
            (args, Predicate(newPredicateName, Nil))
          }
          (predicate, argumentMapping)
      }
    val allGroundedPredicates = groundedPredicates flatMap { _._2.values }

    // ----- Tasks
    def groundTaskToGroundedTask(groundTask: GroundTask): Task = groundTask match {
      case g@GroundTask(ReducedTask(name, isPrimitive, _, _, _, _), constants) =>
        val newTaskName = name + ((constants map { _.name }) mkString("(", ",", ")"))
        // ground precondition and effect
        val preconditionLiterals = g.substitutedPreconditions map {
          case GroundLiteral(predicate, isPositive, parameter) => Literal(groundedPredicates(predicate)(parameter), isPositive, Nil)
        }
        val effectLiterals = g.substitutedEffects map {
          case GroundLiteral(predicate, isPositive, parameter) => Literal(groundedPredicates(predicate)(parameter), isPositive, Nil)
        }

        // TODO: here we assume that the grounding we get always fulfills the parameter constraints ... we have to assert this at some point
        ReducedTask(newTaskName, isPrimitive, Nil, Nil, And(preconditionLiterals), And(effectLiterals))
      case _                                                                   => noSupport(FORUMLASNOTSUPPORTED)
    }

    val groundedPrimitiveTasks: Map[Task, Map[Seq[Constant], (Task, GroundTask)]] = reachabilityAnalysis.reachableGroundActions groupBy { _.task } map { case (t, groundActs) =>
      val taskMap: Map[Seq[Constant], (Task, GroundTask)] = groundActs groupBy { _.arguments } map { case (args, taskList) =>
        assert(taskList.length == 1)
        assert(taskList.head.task.isPrimitive) // this can only be a primitive action
        (args, (groundTaskToGroundedTask(taskList.head), taskList.head))
      }
      (t, taskMap)
    }

    // ground abstract tasks, inits and goals naively
    // TODO maybe we should require a TDG?
    val abstractTaskGroundings: Map[Task, Map[Seq[Constant], (Task, GroundTask)]] = ((domain.abstractTasks ++ domain.hiddenTasks ++ (plan.initAndGoal map { _.schema })) map { at =>
      val groundedAT: Map[Seq[Constant], (Task, GroundTask)] = at match {
        case rt@ReducedTask(name, isPrimitive, parameters, parameterConstraints, precondition, effect) =>
          assert(!isPrimitive || (domain.hiddenTasks contains rt) || (plan.initAndGoal exists { _.schema == rt }))
          val groundings = Sort.allPossibleInstantiations(parameters map { _.sort }) filter rt.areParametersAllowed
          (groundings map { args => (args, (groundTaskToGroundedTask(GroundTask(rt, args)), GroundTask(rt, args))) }).toMap
        case _                                                                                         => noSupport(FORUMLASNOTSUPPORTED)
      }
      (at, groundedAT)
    }).toMap
    assert(groundedPrimitiveTasks.keys forall { t => !abstractTaskGroundings.contains(t) })
    val groundedTasks: Map[Task, Map[Seq[Constant], (Task, GroundTask)]] = groundedPrimitiveTasks ++ abstractTaskGroundings

    val allGroundedTasks = (groundedTasks flatMap { _._2.values }).keys


    // helper methods
    def groundPS(oldPS: PlanStep, mapVariable: Variable => Constant): PlanStep = PlanStep(oldPS.id, groundedTasks(oldPS.schema)(oldPS.arguments map mapVariable)._1, Nil)

    def groundPlan(plan: Plan, innerCSP: CSP): Plan = {
      def mapVariable(variable: Variable): Constant = innerCSP.getRepresentative(variable) match {
        case c: Constant => c
        case _           => throw new AssertionError("I've bound all variables to constants, but the CSP didn't return a constant")
      }
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
    val groundedDecompositionMethods = domain.decompositionMethods flatMap {
      case SimpleDecompositionMethod(abstractTask, subPlan) =>
        groundedTasks(abstractTask) flatMap {
          case (constantArguments, (groundedAbstractTask, groundTask)) =>
            val bindArguments = groundTask.task.parameters zip constantArguments map { case (v, c) => Equal(v, c) }
            val boundCSP = subPlan.variableConstraints.addConstraints(bindArguments)
            val unboundVariables = boundCSP.variables filter { v => boundCSP.getRepresentative(v) match {
              case c: Constant    => false
              case repV: Variable => repV == v
            }
            }
            // try to bind all variables to their
            val unboundVariablesWithRemainingValues: Seq[(Variable, Seq[Constant])] = (unboundVariables map { v => (v, boundCSP.reducedDomainOf(v)) }).toSeq
            val allInstantiations = Sort allPossibleInstantiationsWithVariables unboundVariablesWithRemainingValues

            allInstantiations map { instantiation =>
              val additionalConstraints = instantiation map { case (v, c) => Equal(v, c) }
              val innerCSP = boundCSP addConstraints additionalConstraints
              if (innerCSP.isSolvable contains false) None else Some(groundPlan(subPlan, innerCSP))
            } filter { _.isDefined } map { _.get } map { SimpleDecompositionMethod(groundedAbstractTask, _) }
        }
      case _                                                => noSupport(NONSIMPLEMETHOD)
    }

    // TODO handle the case where the initial plan contains variable, e.g. by introducing a new method
    plan.variableConstraints.variables foreach { v => assert(plan.variableConstraints.getRepresentative(v).isInstanceOf[Constant]) }
    // TODO handle decomposition axioms ?!?
    assert(domain.decompositionAxioms.isEmpty)
    (Domain(Nil, allGroundedPredicates.toSeq, allGroundedTasks.toSeq, groundedDecompositionMethods, Nil), groundPlan(plan, plan.variableConstraints))
  }
}
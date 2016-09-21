package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.csp.{CSP, PartialSubstitution}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw._
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{IsModificationAllowed, IsFlawAllowed}
import de.uniulm.ki.util.{DotPrintable, HashMemo}
import de.uniulm.ki.panda3.symbolic.writer._

/**
  * Simple implementation of a plan, based on symbols
  *
  * - planStepParentInDecompositionTree: first entry is the parent, the second respective plan step in the methods subplan
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class Plan(planStepsAndRemovedPlanSteps: Seq[PlanStep], causalLinksAndRemovedCausalLinks: Seq[CausalLink], orderingConstraints: TaskOrdering,
                @Deprecated parameterVariableConstraints: CSP,
                init: PlanStep, goal: PlanStep, isModificationAllowed: IsModificationAllowed, isFlawAllowed: IsFlawAllowed,
                planStepDecomposedByMethod: Map[PlanStep, DecompositionMethod], planStepParentInDecompositionTree: Map[PlanStep, (PlanStep, PlanStep)]) extends
  DomainUpdatable with PrettyPrintable with HashMemo with DotPrintable[PlanDotOptions] {

  assert(planStepsAndRemovedPlanSteps == orderingConstraints.tasks)

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

  lazy val planSteps       : Seq[PlanStep]   = planStepsAndRemovedPlanSteps filter isPresent
  lazy val causalLinks     : Seq[CausalLink] = causalLinksAndRemovedCausalLinks filter { cl => isPresent(cl.producer) && isPresent(cl.consumer) }
  lazy val removedPlanSteps: Seq[PlanStep]   = planStepsAndRemovedPlanSteps filterNot isPresent

  lazy val flaws: Seq[Flaw] = {
    val hardFlaws = causalThreats ++ openPreconditions ++ abstractPlanSteps // ++ notInsertedByDecomposition
    if (hardFlaws.isEmpty) unboundVariables else hardFlaws
  } filter isFlawAllowed

  lazy val planStepsWithoutInitGoal: Seq[PlanStep] = planSteps filter { ps => ps != init && ps != goal }

  lazy val planStepsAndRemovedPlanStepsWithoutInitGoal: Seq[PlanStep] = planStepsAndRemovedPlanSteps filter { ps => ps != init && ps != goal }

  lazy val initAndGoal = init :: goal :: Nil

  lazy val planStepsAndRemovedWithInitAndGoalFirst = (init :: goal :: Nil) ++ planStepsAndRemovedPlanStepsWithoutInitGoal

  lazy val orderingConstraintsWithoutRemovedPlanSteps = orderingConstraints.removePlanSteps(planStepsAndRemovedPlanSteps diff planSteps)

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
  def newInstance(firstFreePlanStepID: Int, firstFreeVariableID: Int, partialSubstitution: PartialSubstitution[Variable], parentPlanStep: PlanStep):
  (Plan, PartialSubstitution[Variable], Map[PlanStep, PlanStep]) = {
    assert(planSteps.size == planStepsAndRemovedPlanSteps.size)
    val oldPlanVariables = variableConstraints.variables.toSeq

    val newVariables = oldPlanVariables zip (firstFreeVariableID until firstFreeVariableID + oldPlanVariables.size) map {
      case (v, id) =>
        if (partialSubstitution(v) == v) Variable(id, v.name, v.sort) else partialSubstitution(v)
    }
    val sub = PartialSubstitution(oldPlanVariables, newVariables)

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
    case AddLiteralsToInitAndGoal(literalsInit, literalsGoal, constraints) =>
      // TODO: currently we only support this operation if all literals are 0-ary
      assert((literalsInit ++ literalsGoal) forall {
        _.parameterVariables.isEmpty
      })
      // build a new schema for init
      val initTask = init.schema match {
        case reduced: ReducedTask => ReducedTask(reduced.name, isPrimitive = true, reduced.parameters, reduced.artificialParametersRepresentingConstants,
                                                 reduced.parameterConstraints ++ constraints,
                                                 reduced.precondition, And[Literal](reduced.effect.conjuncts ++ literalsInit))
        case general: GeneralTask =>
          GeneralTask(general.name, isPrimitive = true, general.parameters, general.artificialParametersRepresentingConstants, general.parameterConstraints ++ constraints,
                      general.precondition, And[Formula](literalsInit :+ general.effect))
      }
      val newInit = PlanStep(init.id, initTask, init.arguments)

      // build a new schema for goal
      val goalTask = goal.schema match {
        case reduced: ReducedTask =>
          ReducedTask(reduced.name, isPrimitive = true, reduced.parameters, reduced.artificialParametersRepresentingConstants,
                      reduced.parameterConstraints ++ constraints, And[Literal](reduced.precondition.conjuncts ++ literalsGoal), reduced.effect)
        case general: GeneralTask =>
          GeneralTask(general.name, isPrimitive = true, general.parameters, general.artificialParametersRepresentingConstants,
                      general.parameterConstraints ++ constraints, And[Formula](literalsGoal :+ general.precondition), general.effect)
      }
      val newGoal = PlanStep(goal.id, goalTask, goal.arguments)

      val exchangeInit = ExchangePlanSteps(init, newInit)
      val exchangeGoal = ExchangePlanSteps(goal, newGoal)

      Plan(planStepsAndRemovedPlanSteps map { _ update exchangeInit update exchangeGoal }, causalLinksAndRemovedCausalLinks map { _ update exchangeInit update exchangeGoal },
           orderingConstraints update exchangeInit update exchangeGoal, variableConstraints, newInit, goal, isModificationAllowed, isFlawAllowed, planStepDecomposedByMethod,
           planStepParentInDecompositionTree)

    case ExchangeTask(taskMap) =>
      // if any planstep will get more arguments ... just add them
      val newPlanSteps = planSteps map { ps => (ps, ps update domainUpdate) }
      val newVariables = newPlanSteps flatMap { case (ps1, ps2) => ps2.arguments drop ps1.arguments.length }

      val newPlan = copy(parameterVariableConstraints = parameterVariableConstraints.addVariables(newVariables))

      Plan(newPlan.planStepsAndRemovedPlanSteps map { _ update domainUpdate }, newPlan.causalLinksAndRemovedCausalLinks map { _ update domainUpdate },
           newPlan.orderingConstraints update domainUpdate, newPlan.parameterVariableConstraints, newPlan.init update domainUpdate, newPlan.goal update domainUpdate,
           newPlan.isModificationAllowed, newPlan.isFlawAllowed,
           newPlan.planStepDecomposedByMethod map { case (a, b) => (a update domainUpdate, b update domainUpdate) },
           newPlan.planStepParentInDecompositionTree map { case (a, (b, c)) => (a update domainUpdate, (b update domainUpdate, c update domainUpdate)) })

    case _ =>
      val newInit = init update domainUpdate
      val newGoal = goal update domainUpdate

      val possiblyInvertedUpdate = domainUpdate match {
        case ExchangeLiteralsByPredicate(map, _) => ExchangeLiteralsByPredicate(map, invertedTreatment = false)
        case RemoveEffects(toRemove, _)          => RemoveEffects(toRemove, invertedTreatment = false)
        case _                                   => domainUpdate
      }

      val newPlanStepsAndRemovedPlanSteps = planSteps map {
        case ps if ps == init => newInit
        case ps if ps == goal => newGoal
        case ps               => ps update possiblyInvertedUpdate
      }

      val newCausalLinksAndRemovedCausalLinks = causalLinksAndRemovedCausalLinks map { _ update possiblyInvertedUpdate }
      val newOrderingConstraints = orderingConstraints update possiblyInvertedUpdate
      val newVariableConstraint = variableConstraints update possiblyInvertedUpdate
      val newPlanStepDecomposedByMethod = planStepDecomposedByMethod map { case (ps, method) => (ps update possiblyInvertedUpdate, method update possiblyInvertedUpdate) }
      val newPlanStepParentInDecompositionTree = planStepParentInDecompositionTree map {
        case (ps, (parent, inPlan)) => (ps update possiblyInvertedUpdate, (parent update possiblyInvertedUpdate, inPlan update possiblyInvertedUpdate))
      }
      Plan(newPlanStepsAndRemovedPlanSteps, newCausalLinksAndRemovedCausalLinks, newOrderingConstraints, newVariableConstraint, newInit, newGoal, isModificationAllowed, isFlawAllowed,
           newPlanStepDecomposedByMethod, newPlanStepParentInDecompositionTree)
  }

  def replaceInitAndGoal(newInit: PlanStep, newGoal: PlanStep): Plan = {
    val topPlanTasks = planStepsAndRemovedPlanStepsWithoutInitGoal :+ newInit :+ newGoal
    val initialPlanInternalOrderings = orderingConstraints.originalOrderingConstraints filterNot { _.containsAny(initAndGoal: _*) }
    val topOrdering = TaskOrdering(initialPlanInternalOrderings ++ OrderingConstraint.allBetween(newInit, newGoal, planStepsAndRemovedPlanStepsWithoutInitGoal: _*), topPlanTasks)
    val newCausalLinks = causalLinksAndRemovedCausalLinks map { case CausalLink(p, c, cond) =>
      def replace(ps: PlanStep): PlanStep = if (ps == init) newInit else if (ps == goal) newGoal else ps
      CausalLink(replace(p), replace(c), cond)
    }
    Plan(topPlanTasks,newCausalLinks, topOrdering, variableConstraints, newInit, newGoal,
         isModificationAllowed, isFlawAllowed, planStepDecomposedByMethod, planStepParentInDecompositionTree)

  }

  def isPresent(planStep: PlanStep): Boolean = !planStepDecomposedByMethod.contains(planStep)

  /* convenience methods to determine usable IDs */
  lazy val getFirstFreePlanStepID: Int = 1 + (planSteps foldLeft 0) { case (m, ps: PlanStep) => math.max(m, ps.id) }
  lazy val getFirstFreeVariableID: Int = 1 + (variableConstraints.variables foldLeft 0) { case (m, v: Variable) => math.max(m, v.id) }

  def isLastPlanStep(planStep: PlanStep): Boolean = planStepsWithoutInitGoal filter { _ != planStep } forall { other => orderingConstraints.lt(other, planStep) }


  /** returns a short information about the object */
  override def shortInfo: String = (planSteps map { "PS " + _.mediumInfo }).mkString("\n") + "\n" + orderingConstraints.shortInfo + "\n" + (causalLinks map { _.longInfo }).mkString("\n") +
    "\n" + variableConstraints.constraints.mkString("\n")

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = shortInfo

  lazy val layersOfDecompositionHierarchy: Seq[Seq[PlanStep]] = {
    def depthInHierarchy(ps: PlanStep): Int = if (!(planStepParentInDecompositionTree contains ps)) 0 else 1 + depthInHierarchy(planStepParentInDecompositionTree(ps)._1)
    val depthMap = removedPlanSteps map { ps => (ps, depthInHierarchy(ps)) } groupBy { _._2 }
    depthMap.toSeq.sortBy(_._1) map { case (_, pss) => pss map { _._1 } sortBy { _.id } }
  }

  def childrenInDecompositionTreeOf(ps: PlanStep): Seq[PlanStep] = planStepsAndRemovedPlanStepsWithoutInitGoal filter { child =>
    if (planStepParentInDecompositionTree contains child) planStepParentInDecompositionTree(child)._1 == ps else false
  } sortBy { _.id }

  /** The DOT representation of the object with options */
  override def dotString(options: PlanDotOptions): String = {
    val dotStringBuilder = new StringBuilder()

    dotStringBuilder append "digraph somePlan{\n"
    dotStringBuilder append "\trankdir=\"LR\";"


    def connectToParent(ps: PlanStep) = if (planStepParentInDecompositionTree contains ps) {
      val (parent, _) = planStepParentInDecompositionTree(ps)
      dotStringBuilder append "\tPS" + parent.id + " -> PS" + ps.id + "[style=dashed,constraint=false];\n"
    }

    dotStringBuilder append ("subgraph cluster_initG{\n\trank=min;\nstyle=invis\n\tPS" + init.id + "[height=10,label=\"init\",shape=box];\n}\n")
    dotStringBuilder append ("subgraph cluster_goalG{\n\trank=max;\nstyle=invis\n\tPS" + goal.id + "[height=10,label=\"goal\",shape=box];\n}\n")

    dotStringBuilder append "subgraph cluster_inner{\nstyle=invis\n"

    // hierarchy graph on top
    if (options.showHierarchy) {
      dotStringBuilder append "\nsubgraph cluster_hierarchy{\nstyle=invis\n"
      // if appropriate also the decomposition hierarchy
      layersOfDecompositionHierarchy.zipWithIndex foreach { case (layer, i) =>
        dotStringBuilder append "\nsubgraph cluster_hierarchy_layer" + i + "{\nstyle=invis\n"
        layer foreach printPS(",style=dotted")
        layer zip layer.tail foreach { case (a, b) => dotStringBuilder append "\tPS" + a.id + " -> PS" + b.id + "[style=invis];\n" }
        dotStringBuilder append "}\n"
      }
      // decomposition arrows
      dotStringBuilder append "\n"
      removedPlanSteps foreach connectToParent
      dotStringBuilder append "}\n"
    }

    dotStringBuilder append "subgraph cluster_mainPlan{\nstyle=invis\n"
    //dotStringBuilder append "\trankdir=\"LR\";"
    // init and goal

    def variablesToString(vars: Seq[Variable]): String = (vars map { v => variableConstraints.getRepresentative(v) match {
      case vv: Variable => toPDDLIdentifier(vv.name)
      case c: Constant  => c.name
    }
    }).mkString(",")

    // ordinary plan steps
    def printPS(addition: String)(ps: PlanStep) = ps match {
      case PlanStep(id, schema, args) =>
        dotStringBuilder append ("\tPS" + id + "[label=\"" + schema.name)
        if (options.showParameters) {
          dotStringBuilder append ("(" + variablesToString(args) + ")")
        }
        dotStringBuilder append "\"" + addition
        if (ps.schema.isPrimitive) dotStringBuilder append ",shape=box"
        dotStringBuilder append "];\n"
    }
    planStepsWithoutInitGoal foreach printPS("")


    // ordering constraints
    dotStringBuilder append "\n"

    def reachableViaCausalLinksFrom(from: PlanStep, to: PlanStep): Boolean = if (from == to) true
    else {
      // TODO might introduce cycles ...
      causalLinksAndRemovedCausalLinks exists { case CausalLink(p, c, _) => if (p == from) reachableViaCausalLinksFrom(c, to) else false }
    }

    val orderingFilterCausalLinks: OrderingConstraint => Boolean =
      if (options.omitImpliedOrderings) {case OrderingConstraint(before, after) => !reachableViaCausalLinksFrom(before, after)} else {case _ => true}
    val orderingFilterHierarchy: OrderingConstraint => Boolean =
      if (options.showHierarchy) {case _ => true} else {case OrderingConstraint(before, after) => isPresent(before) && isPresent(after)}
    val displayedOrderings = orderingConstraints.minimalOrderingConstraints() filter orderingFilterHierarchy filter orderingFilterCausalLinks


    displayedOrderings foreach { case OrderingConstraint(before, after) =>
      if (before != init || after != goal) {
        dotStringBuilder append ("\tPS" + before.id + " -> PS" + after.id + "[style=\"")
        dotStringBuilder append (if (options.showOrdering) "dotted" else "invis")
        dotStringBuilder append "\""
        if (options.showHierarchy && ((isPresent(before) && !isPresent(after)) || (!isPresent(before) && isPresent(after))))
          dotStringBuilder append ",constraint=false"

        dotStringBuilder append "];\n"
      }
    }

    // show causal links
    dotStringBuilder append "\n"
    if (options.showCausalLinks) causalLinks foreach { case CausalLink(producer, consumer, condition) =>
      dotStringBuilder append ("\tPS" + producer.id + " -> PS" + consumer.id + "[label=\"" + (if (condition.isNegative) "not " else "") + condition.predicate.name)
      if (options.showParameters) {
        dotStringBuilder append ("(" + variablesToString(condition.parameterVariables) + ")")
      }
      dotStringBuilder append "\"];\n"
    }

    if (options.showOpenPreconditions) {
      dotStringBuilder append "\n"
      openPreconditions.zipWithIndex foreach { case (OpenPrecondition(_, ps, literal), idx) =>
        dotStringBuilder append "\tOP" + idx + "[label=\"" + literal.predicate.name
        if (options.showParameters) dotStringBuilder append variablesToString(literal.parameterVariables)
        dotStringBuilder append "\",shape=diamond];\n"
        dotStringBuilder append "\tOP" + idx + " -> PS" + ps.id + "[style=dashed];\n"
        dotStringBuilder append "\tPS" + init.id + " -> OP" + idx + "[style=invis];\n"
      }
    }
    // end of the main plan
    dotStringBuilder append "}\n"

    // connect layers
    if (options.showHierarchy) planSteps foreach connectToParent
    // sorting
    dotStringBuilder append "\n"
    /*removedPlanSteps foreach { parent =>
      val children = childrenInDecompositionTreeOf(parent) filterNot  isPresent
      children.take(children.length / 2) foreach { child => dotStringBuilder append "\tPS" + child.id + " -> PS" + parent.id + "[style=invis];\n" }
      //if (children.length % 2 == 1) dotStringBuilder append "\t{rank=same;PS" + children(children.length / 2).id + " -> PS" + parent.id + " }\n"
      children.drop((children.length) / 2) foreach { child => dotStringBuilder append "\tPS" + parent.id + " -> PS" + child.id + "[style=invis];\n" }
    }*/

    // end of the inner graph
    dotStringBuilder append "}\n"



    dotStringBuilder append "}"
    dotStringBuilder.toString
  }

  lazy val groundedInitialState: Seq[GroundLiteral] = init.substitutedEffects map { case Literal(predicate, isPositive, parameters) =>
    GroundLiteral(predicate, isPositive, parameters map { v =>
      val value = variableConstraints.getRepresentative(v)
      assert(value.isInstanceOf[Constant])
      value.asInstanceOf[Constant]
    })
  }

  lazy val groundedInitialStateOnlyPositive: Seq[GroundLiteral] = groundedInitialState filter { _.isPositive }
  lazy val groundedInitialStateOnlyPositiveSet: Set[GroundLiteral] = groundedInitialStateOnlyPositive toSet

  lazy val groundedGoalState: Seq[GroundLiteral] = goal.substitutedPreconditions map { case Literal(predicate, isPositive, parameters) =>
    GroundLiteral(predicate, isPositive, parameters map { v =>
      val value = variableConstraints.getRepresentative(v)
      assert(value.isInstanceOf[Constant])
      value.asInstanceOf[Constant]
    })
  }

  lazy val groundedGoalTask: GroundTask = {
    val arguments = goal.arguments map variableConstraints.getRepresentative map {
      case c: Constant => c
      case _           => noSupport(LIFTEDGOAL)
    }
    GroundTask(goal.schema, arguments)
  }

  override lazy val dotString: String = dotString(PlanDotOptions())
}

case class PlanDotOptions(showParameters: Boolean = true, showOrdering: Boolean = true, omitImpliedOrderings: Boolean = true, showCausalLinks: Boolean = true,
                          showHierarchy: Boolean = false, showOpenPreconditions: Boolean = true) {}
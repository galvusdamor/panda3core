package de.uniulm.ki.panda3.symbolic.plan

import de.uniulm.ki.panda3.symbolic.csp.{CSP, Equal, OfSort, PartialSubstitution}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, GroundTask, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.flaw._
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification
import de.uniulm.ki.panda3.symbolic.search.{NoModifications, NoFlaws}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.search.{IsModificationAllowed, IsFlawAllowed}
import de.uniulm.ki.util.{DirectedGraph, DotPrintable, HashMemo, SimpleDirectedGraph}
import de.uniulm.ki.panda3.symbolic.writer._

/**
  * Simple implementation of a plan, based on symbols
  *
  * - planStepParentInDecompositionTree: first entry is the parent, the second respective plan step in the methods subplan
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off covariant.equals
case class Plan(planStepsAndRemovedPlanSteps: Seq[PlanStep], causalLinksAndRemovedCausalLinks: Seq[CausalLink], orderingConstraints: TaskOrdering,
                @Deprecated parameterVariableConstraints: CSP,
                init: PlanStep, goal: PlanStep, isModificationAllowed: IsModificationAllowed, isFlawAllowed: IsFlawAllowed,
                planStepDecomposedByMethod: Map[PlanStep, DecompositionMethod], planStepParentInDecompositionTree: Map[PlanStep, (PlanStep, PlanStep)],
                dontExpandVariableConstraints: Boolean = false) extends
  DomainUpdatable with PrettyPrintable with HashMemo with DotPrintable[PlanDotOptions] {


  assert(planStepsAndRemovedPlanSteps.distinct.size == planStepsAndRemovedPlanSteps.size)
  assert(planStepsAndRemovedPlanSteps.toSet == orderingConstraints.tasks.toSet)

  assert(planStepsAndRemovedPlanSteps forall { ps => ps.arguments.size == ps.schema.parameters.size })
  //assert(planSteps forall { ps => ps.arguments forall { v => parameterVariableConstraints.variables.contains(v) } })
  planStepsAndRemovedPlanSteps foreach {
    ps =>
      ps.arguments foreach {
        v =>
          assert(parameterVariableConstraints.variables.contains(v), ps.id + " - " + ps.schema.name + ": var " + v)
      }
  }

  planStepParentInDecompositionTree foreach { case (ps, (parent, inMethod)) =>
    assert(planStepsAndRemovedPlanSteps contains ps, "PS " + ps.schema.name + " id: " + ps.id + " prim: " + ps.schema.isPrimitive + " is not contained.")
    assert(planStepsAndRemovedPlanSteps contains parent, "PS " + parent.schema.name + " id: " + parent.id + " prim: " + parent.schema.isPrimitive + " is not contained.")
    assert(planStepDecomposedByMethod(parent).subPlan.planSteps.contains(inMethod), "method " + planStepDecomposedByMethod(parent).name + " does not contain " + inMethod.shortInfo)
  }

  planStepsWithoutInitGoal foreach {
    ps =>
      assert(orderingConstraints.lt(init, ps))
      assert(orderingConstraints.lt(ps, goal))
  }

  assert(orderingConstraints.lt(init, goal))
  assert(orderingConstraints.isConsistent)

  planStepParentInDecompositionTree foreach { case (a, (_, b)) => assert(a.schema == b.schema) }


  lazy val planSteps                          : Seq[PlanStep]   = planStepsAndRemovedPlanSteps filter isPresent
  lazy val planStepTasksSet                   : Set[Task]       = planSteps map { _.schema } toSet
  lazy val planStepsWithoutInitAndGoalTasksSet: Set[Task]       = planStepsWithoutInitGoal map { _.schema } toSet
  lazy val causalLinks                        : Seq[CausalLink] = causalLinksAndRemovedCausalLinks filter { cl => isPresent(cl.producer) && isPresent(cl.consumer) }
  lazy val removedPlanSteps                   : Seq[PlanStep]   = planStepsAndRemovedPlanSteps filterNot isPresent

  lazy val flaws: Seq[Flaw] = {
    val hardFlaws = causalThreats ++ openPreconditions ++ abstractPlanSteps // ++ notInsertedByDecomposition
    if (hardFlaws.isEmpty) unboundVariables else hardFlaws
  } filter isFlawAllowed

  lazy val planStepsWithoutInitGoal: Seq[PlanStep] = planSteps filter { ps => ps != init && ps != goal }
  lazy val planStepSchemaArray     : Array[Task]   = planStepsWithoutInitGoal map { _.schema } toArray

  lazy val planStepsAndRemovedPlanStepsWithoutInitGoal: Seq[PlanStep] = planStepsAndRemovedPlanSteps filter { ps => ps != init && ps != goal }

  lazy val initAndGoal = init :: goal :: Nil

  lazy val planStepsAndRemovedWithInitAndGoalFirst = (init :: goal :: Nil) ++ planStepsAndRemovedPlanStepsWithoutInitGoal

  lazy val orderingConstraintsWithoutRemovedPlanSteps = orderingConstraints.removePlanSteps(planStepsAndRemovedPlanSteps diff planSteps)

  // TODO: this is extremely inefficient
  // add all constraints inherited from tasks to the CSP
  val variableConstraints = if (dontExpandVariableConstraints) parameterVariableConstraints
  else
    planSteps.foldLeft(parameterVariableConstraints)(
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
    ps =>
      ps.substitutedPreconditions map {
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

    val newPlanSteps = planStepMapping map { _._1 }

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
    case SetExpandVariableConstraintsInPlans(dontExpand)                   => this.copy(dontExpandVariableConstraints = dontExpand)
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

    case ExchangeTask(taskMap)                 =>
      // check if we are actually have to change anything
      val allContainedTasks = planSteps map { _.schema } toSet

      if (taskMap.keySet.forall({ t => !allContainedTasks.contains(t) })) this
      else {

        //println("FF " + planSteps.length)
        // if any planstep will get more arguments ... just add them
        val newPlanSteps = planSteps map { ps => (ps, ps update domainUpdate) }
        val newVariables = newPlanSteps flatMap { case (ps1, ps2) => ps2.arguments drop ps1.arguments.length }

        val newPlan = this // copy(parameterVariableConstraints = )

        Plan(newPlan.planStepsAndRemovedPlanSteps map { _ update domainUpdate }, newPlan.causalLinksAndRemovedCausalLinks map { _ update domainUpdate },
             newPlan.orderingConstraints update domainUpdate, parameterVariableConstraints.addVariables(newVariables), newPlan.init update domainUpdate, newPlan.goal update domainUpdate,
             newPlan.isModificationAllowed, newPlan.isFlawAllowed,
             newPlan.planStepDecomposedByMethod map { case (a, b) => (a update domainUpdate, b update domainUpdate) },
             newPlan.planStepParentInDecompositionTree map { case (a, (b, c)) => (a update domainUpdate, (b update domainUpdate, c update domainUpdate)) })
      }
    case PropagateEquality(protectedVariables) =>
      // determine all variables that can be eliminated
      val initialRepresentatives = protectedVariables map { v => v -> v.sort.elements.toSet } toMap
      val (representatives, replacement) =
        (variableConstraints.variables -- protectedVariables).foldLeft[(Map[Variable, Set[Constant]], Seq[(Variable, Variable)])]((initialRepresentatives, Nil))(
          { case ((representativeVariables, deletedVariables), nextVariable) =>
            representativeVariables.keys find { v => variableConstraints.equal(v, nextVariable) } match {
              case Some(v) =>
                val newDomain = representativeVariables(v) intersect nextVariable.sort.elements.toSet
                (representativeVariables + (v -> newDomain), deletedVariables :+ (nextVariable, v))
              case None    => (representativeVariables + (nextVariable -> nextVariable.sort.elements.toSet), deletedVariables)
            }
          })

      val newConstraints = representatives collect { case (v, allowedValues) if v.sort.elements.toSet != allowedValues => OfSort(v, Sort("adhocSort_" + v.name, allowedValues.toSeq, Nil)) }

      val newPlan = this update ExchangeVariables(replacement.toMap)

      newPlan.variableConstraints.constraints foreach { case Equal(_, vari: Variable) => assert(protectedVariables contains vari, protectedVariables + " " + vari); case _ => () }

      newPlan.copy(parameterVariableConstraints = newPlan.parameterVariableConstraints.addConstraints(newConstraints.toSeq))

    case DeleteCausalLinks =>
      // need to run noupdate to simplify tasks
      this.copy(causalLinksAndRemovedCausalLinks = Nil).update(NoUpdate)
    case _                 =>
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

      val postUpdateCorrection = ExchangeTask(Map(init.update(possiblyInvertedUpdate).schema -> newInit.schema, goal.update(possiblyInvertedUpdate).schema -> newGoal.schema))

      val newCausalLinksAndRemovedCausalLinks = causalLinksAndRemovedCausalLinks map { _ update possiblyInvertedUpdate }
      val newOrderingConstraints = orderingConstraints update possiblyInvertedUpdate update postUpdateCorrection
      val newVariableConstraint = parameterVariableConstraints update possiblyInvertedUpdate
      val newPlanStepDecomposedByMethod = planStepDecomposedByMethod map { case (ps, method) => (ps update possiblyInvertedUpdate, method update possiblyInvertedUpdate) }
      val newPlanStepParentInDecompositionTree = planStepParentInDecompositionTree map {
        case (ps, (parent, inPlan)) => (ps update possiblyInvertedUpdate, (parent update possiblyInvertedUpdate, inPlan update possiblyInvertedUpdate))
      }
      Plan(newPlanStepsAndRemovedPlanSteps, newCausalLinksAndRemovedCausalLinks, newOrderingConstraints, newVariableConstraint, newInit, newGoal, isModificationAllowed, isFlawAllowed,
           newPlanStepDecomposedByMethod, newPlanStepParentInDecompositionTree)
  }

  def replaceInitAndGoal(newInit: PlanStep, newGoal: PlanStep, variablesToKeep: Seq[Variable]): Plan = {

    val newInitGoalArguments = newInit.argumentSet ++ newGoal.argumentSet
    val variablesToRemove = (init.arguments ++ goal.arguments) filterNot { v => planStepsWithoutInitGoal exists { _.arguments.contains(v) } } filterNot
      variablesToKeep.contains filterNot newInitGoalArguments
    val variablesToAdd = newInitGoalArguments filterNot parameterVariableConstraints.variables.contains

    val topPlanTasks = planStepsAndRemovedPlanStepsWithoutInitGoal :+ newInit :+ newGoal
    val initialPlanInternalOrderings = orderingConstraints.originalOrderingConstraints filterNot { _.containsAny(initAndGoal: _*) }
    val topOrdering = TaskOrdering(initialPlanInternalOrderings ++ OrderingConstraint.allBetween(newInit, newGoal, planStepsAndRemovedPlanStepsWithoutInitGoal: _*), topPlanTasks)
    val newCausalLinks = causalLinksAndRemovedCausalLinks map { case CausalLink(p, c, cond) =>
      def replace(ps: PlanStep): PlanStep = if (ps == init) newInit else if (ps == goal) newGoal else ps

      CausalLink(replace(p), replace(c), cond)
    }
    Plan(topPlanTasks, newCausalLinks, topOrdering, parameterVariableConstraints update RemoveVariables(variablesToRemove) update AddVariables(variablesToAdd.toSeq), newInit, newGoal,
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
    val minID = planStepsAndRemovedPlanSteps map { _.id } min
    val idAdder = if (minID < 0) -minID + 10 else 0

    val dotStringBuilder = new StringBuilder()

    dotStringBuilder append "digraph somePlan{\n"
    dotStringBuilder append "\trankdir=\"LR\";"


    def connectToParent(ps: PlanStep) = if (planStepParentInDecompositionTree contains ps) {
      val (parent, _) = planStepParentInDecompositionTree(ps)
      dotStringBuilder append "\tPS" + (parent.id + idAdder) + " -> PS" + (ps.id + idAdder) + "[style=dashed,constraint=false];\n"
    }

    dotStringBuilder append ("subgraph cluster_initG{\n\trank=min;\nstyle=invis\n\tPS" + (init.id + idAdder) + "[height=10,label=\"init\",shape=box];\n}\n")
    dotStringBuilder append ("subgraph cluster_goalG{\n\trank=max;\nstyle=invis\n\tPS" + (goal.id + idAdder) + "[height=10,label=\"goal\",shape=box];\n}\n")

    dotStringBuilder append "subgraph cluster_inner{\nstyle=invis\n"

    // hierarchy graph on top
    if (options.showHierarchy) {
      dotStringBuilder append "\nsubgraph cluster_hierarchy{\nstyle=invis\n"
      // if appropriate also the decomposition hierarchy
      layersOfDecompositionHierarchy.zipWithIndex foreach { case (layer, i) =>
        dotStringBuilder append "\nsubgraph cluster_hierarchy_layer" + i + "{\nstyle=invis\n"
        layer foreach printPS(",style=dotted")
        layer zip layer.tail foreach { case (a, b) => dotStringBuilder append "\tPS" + (a.id + idAdder) + " -> PS" + (b.id + idAdder) + "[style=invis];\n" }
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

    def variablesToString(vars: Seq[Variable]): String = (vars map { v =>
      variableConstraints.getRepresentative(v) match {
        case vv: Variable => toPDDLIdentifier(vv.name)
        case c: Constant  => c.name
      }
    }).mkString(",")

    // ordinary plan steps
    def printPS(addition: String)(ps: PlanStep) = ps match {
      case PlanStep(id, schema, args) =>
        dotStringBuilder append ("\tPS" + (id + idAdder) + "[label=\"" + schema.name)
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

    def reachableViaCausalLinksFrom(from: PlanStep, to: PlanStep): Boolean = if (!options.showCausalLinks) false
    else if (from == to) true
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
        dotStringBuilder append ("\tPS" + (before.id + idAdder) + " -> PS" + (after.id + idAdder) + "[style=\"")
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
      dotStringBuilder append ("\tPS" + (producer.id + idAdder) + " -> PS" + (consumer.id + idAdder) + "[label=\"" + (if (condition.isNegative) "not " else "") + condition.predicate.name)
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
        dotStringBuilder append "\tOP" + idx + " -> PS" + (ps.id + idAdder) + "[style=dashed];\n"
        dotStringBuilder append "\tPS" + (init.id + idAdder) + " -> OP" + idx + "[style=invis];\n"
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

  lazy val groundInitialStateOnlyPositivesSetOnlyPredicates: Set[Predicate] = groundedInitialStateOnlyPositiveSet map { _.predicate }

  lazy val groundedInitialTask: GroundTask = {
    val arguments = init.arguments map variableConstraints.getRepresentative map {
      case c: Constant => c
      case _           => noSupport(LIFTEDINIT)
    }
    GroundTask(init.schema, arguments)
  }

  lazy val groundedInitialStateOnlyPositive   : Seq[GroundLiteral] = groundedInitialState filter { _.isPositive }
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


  lazy val normalise: Plan = Plan(planSteps, causalLinks, orderingConstraintsWithoutRemovedPlanSteps, parameterVariableConstraints, init, goal, isModificationAllowed, isFlawAllowed, Map(),
                                  Map())

  // attention, this will essentially infer causal links anew
  lazy val maximalDeordering: Plan = {
    assert(planStepsAndRemovedPlanSteps forall { _.arguments.isEmpty })
    // first step: compute a total ordering of all primitive plan steps
    val primitiveOrdering = orderingConstraints.graph.topologicalOrdering.get.filter(planStepsWithoutInitGoal.contains) :+ goal
    //println(primitiveOrdering mkString "\n")

    def getParentPath(ps: PlanStep): Seq[PlanStep] = if (planStepParentInDecompositionTree.contains(ps)) getParentPath(planStepParentInDecompositionTree(ps)._1) :+ ps else ps :: Nil

    def getCommonParent(ps1: PlanStep, ps2: PlanStep): (PlanStep, (PlanStep, PlanStep)) = {
      val path1 = getParentPath(ps1)
      val path2 = getParentPath(ps2)

      val commonPrefix = path1 zip path2 filter { case (a, b) => a == b } map { _._1 }
      val child1 = path1(commonPrefix.length)
      val child2 = path2(commonPrefix.length)

      val parent = commonPrefix.last
      val methodPS1 = planStepParentInDecompositionTree(child1)._2
      val methodPS2 = planStepParentInDecompositionTree(child2)._2

      //println("method PS " + methodPS1.schema.name + " " + methodPS2.schema.name)

      (parent, (methodPS1, methodPS2))
    }


    // get all the constraints implied by the hierarchy
    val constraintsImpliedByHierarchy: Seq[OrderingConstraint] = if (planStepDecomposedByMethod.isEmpty) Nil else
      primitiveOrdering flatMap { ps1 =>
        primitiveOrdering flatMap { ps2 =>
          if (ps1 != ps2 && ps1 != goal && ps2 != goal) {
            val (parent, (child1, child2)) = getCommonParent(ps1, ps2)
            val methodOfParent = planStepDecomposedByMethod(parent)

            if (methodOfParent.subPlan.orderingConstraints.lt(child1, child2)) {
              assert(primitiveOrdering.indexOf(ps1) < primitiveOrdering.indexOf(ps2), ps1.schema.name + " not before " + ps2.schema.name)
              //println("ORDER " + ps1.schema.name + " < " + ps2.schema.name + " parent " + parent.schema.name)
              OrderingConstraint(ps1, ps2) :: Nil
            } else Nil
          } else Nil
        }
      }

    // now re-infer the causal links
    val inferredCausalLinks: Seq[CausalLink] = primitiveOrdering.zipWithIndex flatMap { case (consumer, iConsumer) =>
      val precs = consumer.substitutedPreconditions
      assert(precs forall { _.isPositive })

      val cls: Seq[CausalLink] = precs map { literal =>
        // walk backwards
        var pos = iConsumer - 1
        var prod: Option[PlanStep] = None
        while (pos != -1) {
          if (primitiveOrdering(pos).substitutedEffects.contains(literal.negate)) pos = 0
          else if (primitiveOrdering(pos).substitutedEffects.contains(literal)) prod = Some(primitiveOrdering(pos))
          pos -= 1
        }

        if (prod.isEmpty) prod = Some(init)

        CausalLink(prod.get, consumer, literal)
      }

      cls
    }

    val inferredCLOrdering = inferredCausalLinks map { case CausalLink(prod, cons, _) => OrderingConstraint(prod, cons) } distinct


    // build the final plan
    val allBetween = OrderingConstraint.allBetween(init, goal, planStepsAndRemovedPlanStepsWithoutInitGoal: _*)
    val taskOrdering = TaskOrdering(constraintsImpliedByHierarchy ++ inferredCLOrdering ++ allBetween, orderingConstraints.tasks)
    val causalPlan = copy(orderingConstraints = taskOrdering, causalLinksAndRemovedCausalLinks = inferredCausalLinks)

    val constraintsToRemoveThreats: Seq[OrderingConstraint] = causalPlan.causalThreats map { case CausalThreat(_, CausalLink(prod, cons, _), threater, _) =>
      if (primitiveOrdering.indexOf(threater) < primitiveOrdering.indexOf(prod)) OrderingConstraint(threater, prod) else OrderingConstraint(cons, threater)
    }

    val nonThreateningTaskOrdering = TaskOrdering(constraintsImpliedByHierarchy ++ inferredCLOrdering ++ allBetween ++ constraintsToRemoveThreats, orderingConstraints.tasks)
    copy(orderingConstraints = nonThreateningTaskOrdering, causalLinksAndRemovedCausalLinks = inferredCausalLinks)
  }

  lazy val decompositionTree: DirectedGraph[PlanStep] =
    SimpleDirectedGraph(planStepsAndRemovedPlanStepsWithoutInitGoal, planStepParentInDecompositionTree.toSeq map { case (a, (b, _)) => a -> b })

  def isConsistentAbstractionSequence(abstraction: Seq[PlanStep]): Boolean = {

    // for all actions in the plan, exactly one action from the abstraction must be on that way
    def abstractionCoundDFS(ps: PlanStep): Int = (if (abstraction contains ps) 1 else 0) + (decompositionTree.edges(ps) map { pss => abstractionCoundDFS(pss) } sum)

    val abstractionIsBlocking = planStepsWithoutInitGoal forall { ps => abstractionCoundDFS(ps) == 1 }

    val abstractionIsOrderedCorrectly = abstraction.zipWithIndex forall { case (ps1, i) => abstraction.drop(i) forall {
      case ps2 =>
        !(planStepsWithoutInitGoal exists { case c1 => planStepsWithoutInitGoal exists { case c2 =>
          decompositionTree.reachable(c1).contains(ps1) && decompositionTree.reachable(c2).contains(ps2) && orderingConstraints.lt(c2, c1)
        }
        })
    }
    }

    abstractionIsBlocking
  }

  def abstractFromPS(abstraction: Seq[PlanStep], ps: PlanStep): Option[Seq[PlanStep]] = if (decompositionTree.sinks contains ps) None else {
    val parent = decompositionTree.edges(ps).head
    // find potential sublings
    val siblings = decompositionTree.reversedEdgesSet(parent)

    val newAbstraction = abstraction collect {
      case `ps`                        => parent
      case x if !(siblings contains x) => x
    }

    Some(newAbstraction).filter(isConsistentAbstractionSequence)
  }

  def goToAbstractionLevel(level: Int): Seq[PlanStep] = {
    val initialAbstraction = orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }

    def reduce(seq: Seq[PlanStep]): Seq[PlanStep] = {
      val psTooConcrete = seq filter { ps => !(decompositionTree.getVerticesWithDistance(ps) exists { case (ps, i) => i <= level && decompositionTree.sinks.contains(ps) }) }

      if (psTooConcrete.isEmpty) seq else {
        psTooConcrete map { ps => abstractFromPS(seq, ps) } find (_.isDefined) match {
          case None    => seq // further abstraction not possible
          case Some(x) => reduce(x.get)
        }
      }
    }

    reduce(initialAbstraction)
  }

  def toToAbstractionWithActions(targetLength: Int): Seq[PlanStep] = {
    val initialAbstraction = orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }

    def reduce(seq: Seq[PlanStep]): Seq[PlanStep] = if (seq.length <= targetLength) seq else {
      val psSortedByConcreteness = seq sortBy { ps => -decompositionTree.getVerticesWithDistance(ps).map(_._2).max }

      psSortedByConcreteness.foldLeft[Option[Seq[PlanStep]]](None)(
        {
          case (Some(x), _) => Some(x)
          case (None, ps)   => abstractFromPS(seq, ps)
        }) match {
        case None    => seq // further abstraction not possible
        case Some(x) => reduce(x)
      }
    }

    reduce(initialAbstraction)
  }

  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[Plan] && this.hashCode == o.hashCode()) {productIterator.sameElements(o.asInstanceOf[Plan].productIterator) } else false

  override lazy val dotString: String = dotString(PlanDotOptions())
}


object Plan {
  /**
    * Creates a totally ordered plan given a sequence of tasks. Currently only supports grounded tasks
    */
  def apply(taskSequence: Seq[PlanStep], init: Task, goal: Task, planStepDecomposedByMethod: Map[PlanStep, DecompositionMethod],
            planStepParentInDecompositionTree: Map[PlanStep, (PlanStep, PlanStep)]): Plan = {
    assert(taskSequence forall { _.schema.parameters.isEmpty })

    val planStepSequence = ((init :: goal :: Nil).zipWithIndex map { case (t, i) => PlanStep(-i - 1, t, Nil) }) ++ taskSequence
    val removedPlanSteps = planStepDecomposedByMethod.keys.toSeq
    val orderedPSSequence = ((planStepSequence.head :: Nil) ++ planStepSequence.drop(2)) :+ planStepSequence(1)
    val totalOrdering = TaskOrdering.totalOrdering(orderedPSSequence)


    Plan(planStepSequence ++ removedPlanSteps, Nil, totalOrdering.addPlanSteps(removedPlanSteps), CSP(Set(), Nil), planStepSequence.head, planStepSequence(1), NoModifications, NoFlaws,
         planStepDecomposedByMethod, planStepParentInDecompositionTree)
  }


  def sequentialPlan(taskSequence: Seq[Task]): Plan = {
    assert(taskSequence forall { _.parameters.isEmpty })

    val noopTask = ReducedTask("noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))

    val planStepSequence = ((noopTask :: noopTask :: Nil) ++ taskSequence).zipWithIndex map { case (t, i) => PlanStep(i, t, Nil) }
    val orderedPSSequence = ((planStepSequence.head :: Nil) ++ planStepSequence.drop(2)) :+ planStepSequence(1)
    val totalOrdering = TaskOrdering.totalOrdering(orderedPSSequence)

    Plan(planStepSequence, Nil, totalOrdering, CSP(Set(), Nil), planStepSequence.head, planStepSequence(1), NoModifications, NoFlaws, Map(), Map())
  }

  def parallelPlan(taskSequence: Seq[Task]): Plan = {
    assert(taskSequence forall { _.parameters.isEmpty })

    val noopTask = ReducedTask("noop", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))

    val planStepSequence = ((noopTask :: noopTask :: Nil) ++ taskSequence).zipWithIndex map { case (t, i) => PlanStep(i, t, Nil) }
    val totalOrdering = TaskOrdering(OrderingConstraint.allBetween(planStepSequence.head, planStepSequence(1), planStepSequence.drop(2): _*), planStepSequence)

    Plan(planStepSequence, Nil, totalOrdering, CSP(Set(), Nil), planStepSequence.head, planStepSequence(1), NoModifications, NoFlaws, Map(), Map())
  }

}

case class PlanDotOptions(showParameters: Boolean = true, showOrdering: Boolean = true, omitImpliedOrderings: Boolean = true, showCausalLinks: Boolean = true,
                          showHierarchy: Boolean = false, showOpenPreconditions: Boolean = true) {}
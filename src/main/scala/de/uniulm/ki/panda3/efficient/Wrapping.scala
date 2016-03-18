package de.uniulm.ki.panda3.efficient

import de.uniulm.ki.panda3.efficient.csp.{EfficientCSP, EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.domain.{EfficientDecompositionMethod, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.flaw._
import de.uniulm.ki.panda3.efficient.plan.modification._
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering
import de.uniulm.ki.panda3.efficient.search.EfficientSearchNode
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.flaw._
import de.uniulm.ki.panda3.symbolic.plan.modification._
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import de.uniulm.ki.panda3.symbolic.plan.{SymbolicPlan, Plan}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.search.SearchNode
import de.uniulm.ki.util.{SimpleDirectedGraph, BiMap}

/**
  * An explicit transformator between the inefficient symbolic part of panda3 and the efficient part thereof
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off number.of.methods
case class Wrapping(symbolicDomain: Domain, initialPlan: Plan) {

  private val domainConstants           : BiMap[Constant, Int]            = BiMap(symbolicDomain.constants.zipWithIndex)
  private val domainSorts               : BiMap[Sort, Int]                = {
    val allSorts = (symbolicDomain.declaredAndUnDeclaredSorts ++ (initialPlan.initAndGoal flatMap { _.arguments map { _.sort } })).distinct
    BiMap(allSorts.zipWithIndex)
  }
  private val domainPredicates          : BiMap[Predicate, Int]           = BiMap(symbolicDomain.predicates.zipWithIndex)
  private val domainTasksObjects        : BiMap[Task, EfficientTask]      = {
    val ordinaryTaskSchemes = symbolicDomain.tasks map { (_, false) }
    val hiddenTaskSchemes = (symbolicDomain.hiddenTasks :+ initialPlan.init.schema :+ initialPlan.goal.schema).distinct map { (_, true) }
    val allTaskSchemes = ordinaryTaskSchemes ++ hiddenTaskSchemes
    BiMap(allTaskSchemes map { case (t, isInitOrGoal) => (t, computeEfficientTask(t, isInitOrGoal)) })
  }
  private val domainTasks               : BiMap[Task, Int]                = BiMap(domainTasksObjects.toMap.keys.toSeq.zipWithIndex)
  private val domainDecompositionMethods: BiMap[DecompositionMethod, Int] = BiMap(symbolicDomain.decompositionMethods.zipWithIndex)


  private def computeEfficientLiteral(literal: Literal, variableMapping: Map[Variable, Int]): EfficientLiteral =
    EfficientLiteral(domainPredicates(literal.predicate), isPositive = literal.isPositive, (literal.parameterVariables map { variableMapping(_) }).toArray)

  private def computeEfficientVariableConstraint(variableConstraint: VariableConstraint, variableMapping: Variable => Int) = variableConstraint match {
    case Equal(v1, v2: Variable)    => EfficientVariableConstraint(EfficientVariableConstraint.EQUALVARIABLE, variableMapping(v1), variableMapping(v2))
    case Equal(v, c: Constant)      => EfficientVariableConstraint(EfficientVariableConstraint.EQUALCONSTANT, variableMapping(v), domainConstants(c))
    case NotEqual(v1, v2: Variable) => EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALVARIABLE, variableMapping(v1), variableMapping(v2))
    case NotEqual(v, c: Constant)   => EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALCONSTANT, variableMapping(v), domainConstants(c))
    case OfSort(v, s)               => EfficientVariableConstraint(EfficientVariableConstraint.OFSORT, variableMapping(v), domainSorts(s))
    case NotOfSort(v, s)            => EfficientVariableConstraint(EfficientVariableConstraint.NOTOFSORT, variableMapping(v), domainSorts(s))

  }


  private def computeEfficientTask(task: Task, isInitOrGoal: Boolean): EfficientTask = {
    val parameterSorts = task.parameters map { v => domainSorts.toMap(v.sort) }
    val variableMap = task.parameters.zipWithIndex.toMap
    val variableConstraints = task.parameterConstraints map { computeEfficientVariableConstraint(_, variableMap) }

    // if there is anything other than a literal conjunct ... we can't handle it
    if (!task.isInstanceOf[ReducedTask]) noSupport(FORUMLASNOTSUPPORTED)

    val reducedTask = task.asInstanceOf[ReducedTask]

    val preconditions = reducedTask.precondition.conjuncts map { computeEfficientLiteral(_, variableMap) }
    val effects = reducedTask.effect.conjuncts map { computeEfficientLiteral(_, variableMap) }

    EfficientTask(isPrimitive = task.isPrimitive, parameterSorts.toArray, variableConstraints.toArray, preconditions.toArray, effects.toArray, isInitOrGoal)
  }


  /**
    * the given variables will be fixed to 0..sz(fixedVariables). This is needed to transform decomposition methods
    */
  private def computeEfficientPlan(plan: Plan, domain: EfficientDomain, fixedVariables: Seq[Variable]): EfficientPlan = {
    // compute variable translation
    val variableOrder: Seq[Variable] = fixedVariables ++ plan.variableConstraints.variables.--(fixedVariables).toSeq.sortBy({ _.id })
    val variablesMap: BiMap[Variable, Int] = BiMap(variableOrder.zipWithIndex)

    // plan steps
    val orderedTasks = (plan.init :: plan.goal :: Nil) ++ plan.planStepWithoutInitGoal
    val planStepTasks = orderedTasks map { ps => domainTasks(ps.schema) }
    val planStepParameters = orderedTasks map { ps => (ps.arguments map { variablesMap(_) }).toArray }
    val planStepDecomposedBy = orderedTasks map {
      _.decomposedByMethod match {
        case Some(method) => domainDecompositionMethods(method)
        case None         => -1
      }
    }
    val planStepParentInDecompositionTree = orderedTasks map {
      _.parentInDecompositionTree match {
        case Some(ps) => unwrap(ps, plan)
        case None     => -1
      }
    }

    // CSP
    val symbolicVariableSorts = variableOrder map { _.sort }
    val usableVariableSorts = symbolicVariableSorts map {
      case s if domainSorts.toMap contains s => s
      case s                                 =>
        val possibleSort = symbolicDomain.getAnySortContainingConstants(s.elements)
        assert(possibleSort.isDefined)
        possibleSort.get
    }
    val necessaryVariableConstraints: Seq[EfficientVariableConstraint] = (symbolicVariableSorts zip usableVariableSorts).zipWithIndex flatMap {
      case ((a, b), _) if a == b        => Nil
      case ((givenSort, chosenSort), v) =>
        val excludedConstants: Set[Constant] = chosenSort.elements.toSet -- givenSort.elements
        excludedConstants map { c => EfficientVariableConstraint(EfficientVariableConstraint.UNEQUALCONSTANT, v, domainConstants(c)) }
    }

    //{ v => domainSorts(v.sort) }
    val efficientCSP = new EfficientCSP(domain)().addVariables((usableVariableSorts map { domainSorts(_) }).toArray)
    plan.variableConstraints.constraints foreach { efficientCSP addConstraint computeEfficientVariableConstraint(_, variablesMap.toMap) }
    necessaryVariableConstraints foreach efficientCSP.addConstraint

    // ordering
    val ordering = new EfficientOrdering().addPlanSteps(orderedTasks.length)
    plan.orderingConstraints.originalOrderingConstraints foreach { case OrderingConstraint(before, after) => ordering.addOrderingConstraint(unwrap(before, plan), unwrap(after, plan)) }

    // causal links
    val causalLinks = plan.causalLinks map { unwrap(_, plan) }

    EfficientPlan(domain, planStepTasks.toArray, planStepParameters.toArray, planStepDecomposedBy.toArray, planStepParentInDecompositionTree.toArray, efficientCSP, ordering,
                  causalLinks.toArray)
  }

  private def newVariableFormEfficient(variableIndex: Int, sortIndex: Int): Variable = Variable(variableIndex, "variable_" + variableIndex, domainSorts.back(sortIndex))

  // DOMAIN

  // compute the efficient domain if the wrapper is initialised
  val efficientDomain: EfficientDomain = {
    // the domain has to be build incrementally
    val domain = new EfficientDomain()

    // compute the parts of the domain
    domain.subSortsForSort = (domainSorts.fromMap.toSeq.sortBy({ _._1 }) map { case (_, sort) => (sort.subSorts map { ss => domainSorts(ss) }).toArray }).toArray
    domain.sortsOfConstant = (domainConstants.fromMap.toSeq.sortBy({ _._1 }) map {
      case (_, c) => (domainSorts.toMap collect { case (s, si) if s.elements.contains(c) => si }).toArray
    }).toArray
    // trigger computation
    domain.recomputeConstantsOfSort()

    domain.predicates = (domainPredicates.fromMap.toSeq.sortBy({ _._1 }) map { case (_, p) => (p.argumentSorts map { domainSorts(_) }).toArray }).toArray

    domain.tasks = (domainTasks.fromMap.toSeq.sortBy({ _._1 }) map { case (_, t) => domainTasksObjects(t) }).toArray

    domain.decompositionMethods = (symbolicDomain.decompositionMethods map {
      case SimpleDecompositionMethod(task, subplan) => EfficientDecompositionMethod(domainTasks(task), computeEfficientPlan(subplan, domain, task.parameters))
      case SHOPDecompositionMethod(_, _, _)         => noSupport(NONSIMPLEMETHOD)
    }).toArray

    domain
  }

  // CONSTANT
  def unwrap(constant: Constant): Int = domainConstants(constant)

  def wrapConstant(constant: Int): Constant = domainConstants.back(constant)

  // SORT
  def unwrap(sort: Sort): Int = domainSorts(sort)

  def wrapSort(sort: Int): Sort = domainSorts.back(sort)

  // TASK
  def unwrap(task: Task): Int = domainTasks(task)

  def wrapTask(task: Int): Task = domainTasks.back(task)

  // PREDICATE
  def unwrap(predicate: Predicate): Int = domainPredicates(predicate)

  def wrapPredicate(predicate: Int): Predicate = domainPredicates.back(predicate)

  // DECOMPOSITIONMETHOD
  def unwrap(method: DecompositionMethod): Int = domainDecompositionMethods(method)

  def wrapDecompositionMethod(method: Int): DecompositionMethod = domainDecompositionMethods.back(method)


  // VARIABLE
  /**
    * This will not work correctly if the plan is part of a decomposition method!!
    *
    * @return returns the number that represents the given variable in the given plan.
    */
  def unwrap(variable: Variable, plan: Plan): Int = plan.variableConstraints.variables.toSeq.sortBy({ _.id }).indexOf(variable)


  private def wrapVariable(variable: Int, allVariables: Seq[Variable]): Variable = allVariables.sortBy({ _.id }).apply(variable)

  /**
    * This will not work correctly if the plan is part of a decomposition method!!
    *
    * @return returns the variable object that corresponds to the number in the efficient representation of the given plan
    */
  def wrapVariable(variable: Int, plan: Plan): Variable = wrapVariable(variable, plan.variableConstraints.variables.toSeq)

  def unwrap(variable: Variable, task: Task): Int = task.parameters.indexOf(variable)

  def wrapVariable(variable: Int, task: Task): Variable = task.parameters(variable)


  // PLANSTEP
  def unwrap(planStep: PlanStep, plan: Plan): Int = ((plan.init :: plan.goal :: Nil) ++ plan.planStepWithoutInitGoal).indexOf(planStep)

  def wrapPlanStep(planStep: Int, plan: Plan): PlanStep = ((plan.init :: plan.goal :: Nil) ++ plan.planStepWithoutInitGoal).apply(planStep)


  // VARIABLE CONSTRAINT
  def unwrap(variableConstraint: VariableConstraint, plan: Plan): EfficientVariableConstraint = computeEfficientVariableConstraint(variableConstraint, { unwrap(_, plan) })


  private def wrap(efficientVariableConstraint: EfficientVariableConstraint, allVariables: Seq[Variable]): VariableConstraint = {
    val left = wrapVariable(efficientVariableConstraint.variable, allVariables)
    val right = efficientVariableConstraint.other

    efficientVariableConstraint.constraintType match {
      case EfficientVariableConstraint.EQUALVARIABLE   => Equal(left, wrapVariable(right, allVariables))
      case EfficientVariableConstraint.EQUALCONSTANT   => Equal(left, wrapConstant(right))
      case EfficientVariableConstraint.UNEQUALVARIABLE => NotEqual(left, wrapVariable(right, allVariables))
      case EfficientVariableConstraint.UNEQUALCONSTANT => NotEqual(left, wrapConstant(right))
      case EfficientVariableConstraint.OFSORT          => OfSort(left, wrapSort(right))
      case EfficientVariableConstraint.NOTOFSORT       => NotOfSort(left, wrapSort(right))
    }
  }

  def wrap(efficientVariableConstraint: EfficientVariableConstraint, plan: Plan): VariableConstraint = wrap(efficientVariableConstraint, plan.variableConstraints.variables.toSeq)

  // CAUSAL LINK
  def wrap(causalLink: EfficientCausalLink, efficientPlan: EfficientPlan): CausalLink = {
    val plan = wrap(efficientPlan)
    wrap(causalLink, plan)
  }

  def wrap(causalLink: EfficientCausalLink, symbolicPlan: Plan): CausalLink = {
    val producer = wrapPlanStep(causalLink.producer, symbolicPlan)
    val consumer = wrapPlanStep(causalLink.consumer, symbolicPlan)

    CausalLink(producer, consumer, producer.substitutedEffects(causalLink.conditionIndexOfProducer))
  }

  def unwrap(causalLink: CausalLink, plan: Plan): EfficientCausalLink = {
    val producerIDX = unwrap(causalLink.producer, plan)
    val consumerIDX = unwrap(causalLink.consumer, plan)
    val producerLiteralIndex = causalLink.producer.indexOfEffect(causalLink.condition, plan.variableConstraints)
    val consumerLiteralIndex = causalLink.consumer.indexOfPrecondition(causalLink.condition, plan.variableConstraints)
    EfficientCausalLink(producerIDX, consumerIDX, producerLiteralIndex, consumerLiteralIndex)
  }


  // PLAN
  def unwrap(plan: Plan): EfficientPlan = computeEfficientPlan(plan, efficientDomain, Nil)

  def wrap(plan: EfficientPlan): Plan = {
    val variables = plan.variableConstraints.variableSorts.zipWithIndex map {
      case (sortIndex, variableIndex) => newVariableFormEfficient(sortIndex, variableIndex)
    }

    val taskCreationGraph = SimpleDirectedGraph(plan.planStepTasks.indices, plan.planStepTasks.indices map { i => (plan.planStepParentInDecompositionTree(i), i) } collect {
      case (a, b) if a != -1 => (a, b)
    })

    // plan steps -> has to be done this way (for with a fold) as we have to ensure that a parent was always already been computed
    val planStepArray: Array[PlanStep] = new Array(plan.planStepTasks.length)
    taskCreationGraph.topologicalOrdering.get foreach { psIndex =>
      val arguments = plan.planStepParameters(psIndex) map { variables(_) }
      val psDecomposedBy = plan.planStepDecomposedByMethod(psIndex)
      val decomposedBy = if (psDecomposedBy != -1) Some(wrapDecompositionMethod(psDecomposedBy)) else None
      val psParent = plan.planStepParentInDecompositionTree(psIndex)
      val parent = if (psParent != -1) Some(planStepArray(psParent)) else None

      planStepArray(psIndex) = PlanStep(psIndex, domainTasks.back(plan.planStepTasks(psIndex)), arguments, decomposedBy, parent)
    }

    // causal links
    val causalLinks = plan.causalLinks map { case EfficientCausalLink(producer, consumer, producerEffectIndex, _) =>
      CausalLink(planStepArray(producer), planStepArray(consumer), planStepArray(producer).substitutedEffects(producerEffectIndex))
    }

    // ordering constraints
    val orderingConstraints =
      plan.planStepTasks.indices flatMap { b => plan.planStepTasks.indices collect { case a if plan.ordering.lt(a, b) => OrderingConstraint(planStepArray(a), planStepArray(b)) } }
    val ordering = new SymbolicTaskOrdering(orderingConstraints, planStepArray)

    // construct the csp
    val possibleValuesConstraints = variables.indices map { v =>
      val possibleConstants = plan.variableConstraints.getRemainingDomain(v) map { domainConstants.back }
      // this is extremely dirty, but probably the most efficient way to do it (the other option would be to find an enclosing sort and add the necessary amound of unequal constraints)
      val tempSort = Sort("variable_" + v + "_sort", possibleConstants.toSeq, Nil)
      OfSort(variables(v), tempSort)
    }
    val unequalConstraints = variables.indices flatMap { v => plan.variableConstraints.getVariableUnequalTo(v) map { w => NotEqual(variables(v), variables(w)) } }
    val csp = new SymbolicCSP(variables.toSet, possibleValuesConstraints ++ unequalConstraints)


    // and return the actual plan
    SymbolicPlan(planStepArray.toSeq, causalLinks, ordering, csp, planStepArray(0), planStepArray(1))
  }


  // SEARCH NODE

  /**
    * Wraps the <br>whole</br> search space described by this node, i.e. itself and all of its children
    */
  def wrap(efficientSearchNode: EfficientSearchNode): SearchNode = {
    // set the essentials
    val wrappedPlan = wrap(efficientSearchNode.plan)
    // TODO: what about the parent??
    val searchNode = new SearchNode(wrappedPlan, null, efficientSearchNode.heuristic)

    // set the things that only exist if the node is not dirty any more


    searchNode.dirty = efficientSearchNode.dirty
    if (!searchNode.dirty) {
      // TODO payload transformator ???
      searchNode.payload = efficientSearchNode.payload

      // the order of the flaws in both representations might not be identical so we need to do a bit of reordering
      searchNode.selectedFlaw = searchNode.plan.flaws indexWhere { flaw => FlawEquivalenceChecker(efficientSearchNode.plan.flaws(efficientSearchNode.selectedFlaw), flaw, this) }
      assert(searchNode.selectedFlaw != -1)
      assert(searchNode.plan.flaws.size == efficientSearchNode.plan.flaws.length)
      // reorder modifications
      searchNode.modifications = searchNode.plan.flaws map { flaw =>
        val otherFlawIndex = efficientSearchNode.plan.flaws indexWhere { efficientFlaw => FlawEquivalenceChecker(efficientFlaw, flaw, this) }
        (efficientSearchNode.modifications(otherFlawIndex) map { wrap(_, searchNode.plan) }).toSeq
      }
      searchNode.children = efficientSearchNode.children map { case (node, i) => (wrap(node), i) }
    }

    searchNode
  }

  // MODIFICATION

  def wrap(efficientModification: EfficientModification, wrappedPlan: Plan): Modification = efficientModification match {
    case EfficientAddOrdering(_, _, before, after)                                                                                                                    =>
      AddOrdering(wrappedPlan, OrderingConstraint(wrapPlanStep(before, wrappedPlan), wrapPlanStep(after, wrappedPlan)))
    case EfficientBindVariable(_, _, variable, constant)                                                                                                              =>
      BindVariableToValue(wrappedPlan, wrapVariable(variable, wrappedPlan), wrapConstant(constant))
    case EfficientDecomposePlanStep(_, _, decomposedPS, addedPlanSteps, newVariableSorts, variableConstraints, efficientCausalLinks, subOrdering, decomposedByMethod) =>
      val newVariables = newVariableSorts zip Range(wrappedPlan.getFirstFreeVariableID, wrappedPlan.getFirstFreeVariableID + newVariableSorts.length) map {
        case (sortIndex, variableIndex) => newVariableFormEfficient(variableIndex, sortIndex)
      }
      val appliedDecompositionMethod = wrapDecompositionMethod(decomposedByMethod(0)._2)
      val nonPresentDecomposedPlanStep = wrapPlanStep(decomposedPS, wrappedPlan).asNonPresent(appliedDecompositionMethod)

      // compute all variables
      val allVariables = (wrappedPlan.variableConstraints.variables ++ newVariables).toSeq
      val addedVariableConstraints = variableConstraints map { wrap(_, allVariables) }
      val (outerConstraints, innerConstraints) = addedVariableConstraints partition { vc =>
        vc.getVariables exists { v => wrappedPlan.variableConstraints.variables contains v }
      }

      // instanciate the new plansteps
      val insertedPlanSteps = addedPlanSteps.zipWithIndex map { case ((schemaID, arguments, _, _), index) =>
        val symbolicArguments = arguments map { wrapVariable(_, allVariables) }
        PlanStep(index + wrappedPlan.getFirstFreePlanStepID, wrapTask(schemaID), symbolicArguments, None, Some(nonPresentDecomposedPlanStep))
      }

      def getPlanStep(id: Int): PlanStep = if (id >= wrappedPlan.getFirstFreePlanStepID) insertedPlanSteps(id - wrappedPlan.getFirstFreePlanStepID) else wrapPlanStep(id, wrappedPlan)

      // causal links
      // we have to build the link ourselves, as at least one of plan steps does not yet exist in the plan
      val allCausalLinks = efficientCausalLinks map { case EfficientCausalLink(producer, consumer, producerIndex, _) =>
        val symbolicProducer: PlanStep = getPlanStep(producer)
        val symbolicConsumer: PlanStep = getPlanStep(consumer)
        CausalLink(symbolicProducer, symbolicConsumer, symbolicProducer.substitutedEffects(producerIndex))
      }

      val (innerLinks, inheritedLinks) = allCausalLinks partition { link => (insertedPlanSteps contains link.producer) && (insertedPlanSteps contains link.consumer) }

      // inserted subplan
      val init = PlanStep(-1, ReducedTask("init", isPrimitive = true, Nil, Nil, And[Literal](Nil), And[Literal](Nil)), Nil, None, None)
      val goal = PlanStep(-1, ReducedTask("init", isPrimitive = true, Nil, Nil, And[Literal](Nil), And[Literal](Nil)), Nil, None, None)
      val subPlanPlanSteps = insertedPlanSteps :+ init :+ goal
      val subPlanOrderingConstraints = subOrdering map { case (before, after) => OrderingConstraint(getPlanStep(before), getPlanStep(after)) }
      val ordering = SymbolicTaskOrdering(OrderingConstraint.allBetween(init, goal, insertedPlanSteps: _*) ++ subPlanOrderingConstraints, subPlanPlanSteps)
      val csp = SymbolicCSP(newVariables.toSet, innerConstraints)
      val subPlan = SymbolicPlan(subPlanPlanSteps, innerLinks, ordering, csp, init, goal)

      // construct the modification
      DecomposePlanStep(wrapPlanStep(decomposedPS, wrappedPlan), nonPresentDecomposedPlanStep, subPlan, outerConstraints, inheritedLinks, appliedDecompositionMethod, wrappedPlan)
    case EfficientInsertCausalLink(_, _, link, constraints)                                                                                                           =>
      val symbolicLink = wrap(link, wrappedPlan)
      val symbolicConstraints = constraints map { wrap(_, wrappedPlan) }
      InsertCausalLink(wrappedPlan, symbolicLink, symbolicConstraints.toSeq)
    case EfficientInsertPlanStepWithLink(_, _, planstep, parameterSorts, link, constraints)                                                                           =>
      val newPlanStepArguments = parameterSorts zip Range(wrappedPlan.getFirstFreeVariableID, wrappedPlan.getFirstFreeVariableID + parameterSorts.length) map {
        case (sortIndex, variableIndex) => newVariableFormEfficient(variableIndex, sortIndex)
      }
      val newPlanStep = PlanStep(wrappedPlan.getFirstFreePlanStepID, wrapTask(planstep._1), newPlanStepArguments, None, None)

      // we have to build the link ourselves, as one of its plan steps does not yet exist in the plan
      val linkConsumer = wrapPlanStep(link.consumer, wrappedPlan)
      val causalLink = CausalLink(newPlanStep, linkConsumer, linkConsumer.substitutedPreconditions(link.conditionIndexOfConsumer))

      // compute all variables
      val allVariables = wrappedPlan.variableConstraints.variables ++ newPlanStepArguments
      val symbolicConstraints = constraints map { wrap(_, allVariables.toSeq) }

      // construct the modification
      InsertPlanStepWithLink(newPlanStep, causalLink, symbolicConstraints, wrappedPlan)
    case EfficientMakeLiteralsUnUnifiable(_, _, variable1, variable2)                                                                                                 =>
      MakeLiteralsUnUnifiable(wrappedPlan, NotEqual(wrapVariable(variable1, wrappedPlan), wrapVariable(variable2, wrappedPlan)))
  }
}

object Wrapping {
  def apply(domAndPlan: (Domain, Plan)): Wrapping = apply(domAndPlan._1, domAndPlan._2)
}


private object FlawEquivalenceChecker {
  /**
    * Checks whether a given efficient flaw is equivalent to a given symbolic flaw
    */
  def apply(efficientFlaw: EfficientFlaw, flaw: Flaw, wrapper: Wrapping): Boolean = (efficientFlaw, flaw) match {
    // abstract plan step
    case (efficientAbstractPlanStep: EfficientAbstractPlanStep, abstractPlanStep: AbstractPlanStep) =>
      efficientAbstractPlanStep.planStep == wrapper.unwrap(abstractPlanStep.ps, abstractPlanStep.plan)
    // causal threat
    case (efficientCausalThreat: EfficientCausalThreat, causalThreat: CausalThreat) =>
      true
    // open precondition
    case (efficientOpenPrecondition: EfficientOpenPrecondition, openPrecondition: OpenPrecondition) =>
      val samePlanStep = efficientOpenPrecondition.planStep == wrapper.unwrap(openPrecondition.planStep, openPrecondition.plan)
      if (samePlanStep)
        efficientOpenPrecondition.preconditionIndex == openPrecondition.planStep.indexOfPrecondition(openPrecondition.precondition, openPrecondition.plan.variableConstraints)
      else false

    // unbound variable
    case (efficientUnboundVariable: EfficientUnboundVariable, unboundVariable: UnboundVariable) =>
      efficientUnboundVariable.variable == wrapper.unwrap(unboundVariable.variable, unboundVariable.plan)
    case _                                                                                      => false
  }
}
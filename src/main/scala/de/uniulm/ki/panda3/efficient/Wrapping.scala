package de.uniulm.ki.panda3.efficient

import de.uniulm.ki.panda3.efficient.csp.{EfficientCSP, EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.domain.{EfficientDecompositionMethod, EfficientDomain, EfficientTask}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.{EfficientPlan, ProblemConfiguration}
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
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints.LTLTrue
import de.uniulm.ki.panda3.symbolic.search._
import de.uniulm.ki.util.{BiMap, SimpleDirectedGraph}

import scala.collection.mutable

/**
  * An explicit transformator between the inefficient symbolic part of panda3 and the efficient part thereof
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off number.of.methods
case class Wrapping(symbolicDomain: Domain, initialPlan: Plan) {

  private val domainConstants           : BiMap[Constant, Int]            = BiMap(symbolicDomain.constants.zipWithIndex)
  private val domainSorts               : BiMap[Sort, Int]                = {
    val initialPlanSorts =
      (initialPlan.planSteps flatMap { ps => (ps.arguments ++ ps.schema.parameters) map { _.sort } }) ++
        (initialPlan.variableConstraints.constraints collect { case OfSort(_, s) => s; case NotOfSort(_, s) => s })
    val allSorts = (symbolicDomain.declaredAndUnDeclaredSorts ++ initialPlanSorts).distinct
    BiMap(allSorts.zipWithIndex)
  }
  private val domainPredicates          : BiMap[Predicate, Int]           = BiMap(symbolicDomain.predicates.zipWithIndex)
  private val domainTasksObjects        : BiMap[Task, EfficientTask]      = {
    val ordinaryTaskSchemes = symbolicDomain.tasks map { (_, false) }
    val hiddenTaskSchemes = ((symbolicDomain.hiddenTasks :+ initialPlan.init.schema :+ initialPlan.goal.schema).distinct map { (_, true) }).distinct
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
    val parameterSorts = task.parameters.toArray map { v => domainSorts.toMap(v.sort) }
    val variableMap = task.parameters.zipWithIndex.toMap
    val variableConstraints = task.parameterConstraints map { computeEfficientVariableConstraint(_, variableMap) }

    // if there is anything other than a literal conjunct ... we can't handle it
    if (!task.isInstanceOf[ReducedTask])
      noSupport(FORUMLASNOTSUPPORTED)

    val reducedTask = task.asInstanceOf[ReducedTask]

    val preconditions = reducedTask.precondition.conjuncts map { computeEfficientLiteral(_, variableMap) }
    val effects = reducedTask.effect.conjuncts map { computeEfficientLiteral(_, variableMap) }

    EfficientTask(isPrimitive = task.isPrimitive, parameterSorts.toArray, variableConstraints.toArray, preconditions.toArray, effects.toArray, allowedToInsert = !isInitOrGoal,
                  initOrGoalTask = isInitOrGoal)
  }


  /**
    * the given variables will be fixed to 0..sz(fixedVariables). This is needed to transform decomposition methods
    */
  private def computeEfficientPlan(plan: Plan, domain: EfficientDomain, fixedVariables: Seq[Variable]): EfficientPlan = {
    // compute variable translation
    val variableOrder: Seq[Variable] = fixedVariables ++ plan.variableConstraints.variables.--(fixedVariables).toSeq.sortBy({ _.id })
    val variablesMap: BiMap[Variable, Int] = BiMap(variableOrder.zipWithIndex)

    // plan steps
    val orderedTasks = (plan.init :: plan.goal :: Nil) ++ plan.planStepsAndRemovedPlanStepsWithoutInitGoal
    val planStepTasks = orderedTasks map { ps => domainTasks(ps.schema) }
    val planStepParameters = orderedTasks map { ps => (ps.arguments map { variablesMap(_) }).toArray }
    val planStepDecomposedBy = orderedTasks map { ps =>
      if (plan.planStepDecomposedByMethod.contains(ps))
        domainDecompositionMethods(plan.planStepDecomposedByMethod(ps))
      else -1
    }
    val decompositionInformationPerPlanStep = orderedTasks map { ps =>
      if (plan.planStepParentInDecompositionTree.contains(ps))
        (unwrap(plan.planStepParentInDecompositionTree(ps)._1, plan), unwrap(plan.planStepParentInDecompositionTree(ps)._2, plan))
      else (-1, -1)
    }
    val planStepParentInDecompositionTree = decompositionInformationPerPlanStep map { _._1 }
    val planStepIsInstanceOfSubPlanPlanStep = decompositionInformationPerPlanStep map { _._2 }

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
    assert(efficientCSP.potentiallyConsistent != plan.variableConstraints.isSolvable.contains(false))

    // ordering
    val ordering = new EfficientOrdering().addPlanSteps(orderedTasks.length)
    plan.orderingConstraints.originalOrderingConstraints foreach { case OrderingConstraint(before, after) => ordering.addOrderingConstraint(unwrap(before, plan), unwrap(after, plan)) }

    // causal links
    val causalLinks = plan.causalLinks map { unwrap(_, plan) }
    val supportedPreconditions = planStepTasks.indices map { ps =>
      val bitSet = mutable.BitSet()
      causalLinks filter { _.consumer == ps } foreach { cl => bitSet.add(cl.conditionIndexOfConsumer) }
      bitSet
    }

    val potentialThreater = plan.causalLinks map { case CausalLink(_, _, Literal(predicate, isPositive, _)) =>
      val threater = orderedTasks.zipWithIndex filter { _._1.substitutedEffects exists { case Literal(lPredicate, lisPositive, _) => predicate == lPredicate && isPositive != lisPositive } }
      mutable.BitSet(threater map { _._2 }: _*)
    }

    val potentialPreconditionSupporter = planStepTasks.zipWithIndex map { case (taskID, ps) =>
      domain.tasks(taskID).precondition.indices map { precondition =>
        val bitSet = mutable.BitSet()
        planStepTasks.zipWithIndex foreach { case (otherTaskID, otherPS) =>
          if (domain.tasksPreconditionCanBeSupportedBy(taskID)(precondition) contains otherTaskID)
            bitSet add otherPS
        }

        bitSet
      } toArray
    }

    // problem configuration
    val problemConfiguration: ProblemConfiguration = (plan.isModificationAllowed, plan.isFlawAllowed) match {
      case (ModificationsByClass(modificationList@_*), FlawsByClass(flawList@_*)) =>
        // TODO contains the assumption that we are doing forward planning ... (to be changed by Daniel if he wants to implement his plan recognition search)
        val taskInsertionAllowed = modificationList contains classOf[InsertPlanStepWithLink]
        val decompositionAllowed = flawList contains classOf[AbstractPlanStep]
        ProblemConfiguration(taskInsertionAllowed, decompositionAllowed)
      case (NoModifications, NoFlaws)                                             => ProblemConfiguration(false, false) // only occurs in method subplans
      case (AllModifications, AllFlaws)                                           => ProblemConfiguration(true, true) // only occurs in tests
      case _                                                                      => noSupport(UNSUPPORTEDPROBLEMTYPE)
    }

    // don't generate HTN arrays if POCL planning, they just consume memory
    val isPOCLPlanning = symbolicDomain.abstractTasks.isEmpty

    if (!isPOCLPlanning) {
      EfficientPlan(domain, planStepTasks.toArray, planStepParameters.toArray, planStepDecomposedBy.toArray, planStepParentInDecompositionTree.toArray, planStepIsInstanceOfSubPlanPlanStep
        .toArray, supportedPreconditions.toArray, potentialPreconditionSupporter.toArray, potentialThreater.toArray, efficientCSP, ordering, causalLinks.toArray, problemConfiguration)()
    } else {
      EfficientPlan(domain, planStepTasks.toArray, planStepParameters.toArray, null, null, null,
                    supportedPreconditions.toArray, potentialPreconditionSupporter.toArray, potentialThreater.toArray, efficientCSP, ordering, causalLinks.toArray,
                    problemConfiguration)(null, null)
    }
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
      case SimpleDecompositionMethod(task, subplan, _) => EfficientDecompositionMethod(domainTasks(task), computeEfficientPlan(subplan, domain, task.parameters))
      case SHOPDecompositionMethod(_, _, _, _, _)      => noSupport(NONSIMPLEMETHOD)
    }).toArray

    // SAS+
    symbolicDomain.sasPlusRepresentation match {
      case Some(sas@SASPlusRepresentation(problem, taskIndices, predicateIndices)) =>
        domain.sasPlusProblem = problem
        // some tasks (like init and goal) might not be part of the SAS+ representation
        domain.taskIndexToSASPlus = domain.tasks.indices.toArray map { t => sas.taskToSASPlusIndex.getOrElse(domainTasks.back(t), -1) }
        domain.predicateIndexToSASPlus = domain.predicates.indices.toArray map { t => sas.predicateToSASPlusIndex.getOrElse(domainPredicates.back(t), -1) }
      case None                                                                    => // do nothing
    }


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

  def unwrap(variable: Variable, task: Task): Int = task.parameters.indexOf(variable)

  /**
    * This will not work correctly if the plan is part of a decomposition method!!
    *
    * @return returns the variable object that corresponds to the number in the efficient representation of the given plan
    */
  def wrapVariable(variable: Int, plan: Plan): Variable = wrapVariable(variable, plan.variableConstraints.variables.toSeq)

  private def sortVariables(variables: Seq[Variable]): Seq[Variable] = variables.sortBy({ _.id })

  private def wrapVariable(variable: Int, allVariables: Seq[Variable]): Variable = wrapVariableSorted(variable, sortVariables(allVariables))

  private def wrapVariableSorted(variable: Int, allVariables: Seq[Variable]): Variable = allVariables.apply(variable)

  def wrapVariable(variable: Int, task: Task): Variable = task.parameters(variable)

  def wrapVariable(variable: Int, decompositionMethod: DecompositionMethod): Variable =
    if (variable < decompositionMethod.abstractTask.parameters.length) decompositionMethod.abstractTask.parameters(variable)
    else
      sortVariables((decompositionMethod.subPlan.variableConstraints.variables -- decompositionMethod.abstractTask.parameters).toSeq)(variable - decompositionMethod.abstractTask.parameters
        .length)

  // PLANSTEP
  def unwrap(planStep: PlanStep, plan: Plan): Int = plan.planStepsAndRemovedWithInitAndGoalFirst.indexOf(planStep)

  def wrapPlanStep(planStep: Int, plan: Plan): PlanStep =
    if (planStep == 0) plan.init
    else if (planStep == 1) plan.goal
    else plan.planStepsAndRemovedPlanStepsWithoutInitGoal.apply(planStep - 2)


  // VARIABLE CONSTRAINT
  def unwrap(variableConstraint: VariableConstraint, plan: Plan): EfficientVariableConstraint = computeEfficientVariableConstraint(variableConstraint, { unwrap(_, plan) })

  private def wrapConstraintVariablesSorted(efficientVariableConstraint: EfficientVariableConstraint, allVariables: Seq[Variable]): VariableConstraint = {
    val left = wrapVariableSorted(efficientVariableConstraint.variable, allVariables)
    val right = efficientVariableConstraint.other

    efficientVariableConstraint.constraintType match {
      case EfficientVariableConstraint.EQUALVARIABLE   => Equal(left, wrapVariableSorted(right, allVariables))
      case EfficientVariableConstraint.EQUALCONSTANT   => Equal(left, wrapConstant(right))
      case EfficientVariableConstraint.UNEQUALVARIABLE => NotEqual(left, wrapVariableSorted(right, allVariables))
      case EfficientVariableConstraint.UNEQUALCONSTANT => NotEqual(left, wrapConstant(right))
      case EfficientVariableConstraint.OFSORT          => OfSort(left, wrapSort(right))
      case EfficientVariableConstraint.NOTOFSORT       => NotOfSort(left, wrapSort(right))
    }
  }

  private def wrap(efficientVariableConstraint: EfficientVariableConstraint, allVariables: Seq[Variable]): VariableConstraint = {
    val sortedVariables = sortVariables(allVariables)
    wrapConstraintVariablesSorted(efficientVariableConstraint, sortedVariables)
  }

  def wrap(efficientVariableConstraint: EfficientVariableConstraint, plan: Plan): VariableConstraint = {
    wrap(efficientVariableConstraint, plan.variableConstraints.variables.toSeq)
  }

  // CAUSAL LINK
  def wrap(causalLink: EfficientCausalLink, efficientPlan: EfficientPlan): CausalLink = wrap(causalLink, wrap(efficientPlan))

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
      case (sortIndex, variableIndex) => newVariableFormEfficient(variableIndex, sortIndex)
    }

    val taskCreationGraph = SimpleDirectedGraph(plan.planStepTasks.indices,
                                                if (plan.planStepParentInDecompositionTree != null)
                                                  plan.planStepTasks.indices map { i => (plan.planStepParentInDecompositionTree(i), i) } collect { case (a, b) if a != -1 => (a, b) }
                                                else Nil)

    // plan steps -> has to be done this way (for with a fold) as we have to ensure that a parent was always already been computed
    val planStepArray: Array[PlanStep] = new Array(plan.planStepTasks.length)
    taskCreationGraph.topologicalOrdering.get foreach { psIndex =>
      val arguments = plan.planStepParameters(psIndex) map { variables(_) }
      //val psDecomposedBy = plan.planStepDecomposedByMethod(psIndex)
      //val psParent = plan.planStepParentInDecompositionTree(psIndex)

      planStepArray(psIndex) = PlanStep(psIndex, domainTasks.back(plan.planStepTasks(psIndex)), arguments)
    }

    // causal links
    val causalLinks = plan.causalLinks map { case EfficientCausalLink(producer, consumer, producerEffectIndex, _) =>
      CausalLink(planStepArray(producer), planStepArray(consumer), planStepArray(producer).substitutedEffects(producerEffectIndex))
    }

    // ordering constraints -- reuse the already computed transitive ordering constraints of the efficient implementation
    val innerArrangement: Array[Array[Byte]] = Array.ofDim(planStepArray.length, planStepArray.length)
    val orderingConstraints = (for (ps1 <- planStepArray.indices; ps2 <- planStepArray.indices) yield {
      val relation = plan.ordering.tryCompare(ps1, ps2)
      innerArrangement(ps1)(ps2) = relation match {
        case None    => TaskOrdering.DONTKNOW
        case Some(x) => x.toByte
      }

      relation match {
        case Some(TaskOrdering.BEFORE) => Some(OrderingConstraint(planStepArray(ps1), planStepArray(ps2)))
        case _                         => None
      }
    }) collect { case Some(x) => x }

    val ordering = new TaskOrdering(orderingConstraints, planStepArray)
    ordering.initialiseExplicitly(0, 0, innerArrangement) // tell the ordering to use the result of the efficient one

    // construct the csp
    val possibleValuesConstraints = variables.indices map { v =>
      val possibleConstants = plan.variableConstraints.getRemainingDomain(v) map { domainConstants.back }
      // this is extremely dirty, but probably the most efficient way to do it (the other option would be to find an enclosing sort and add the necessary amount of unequal constraints)
      val tempSort = Sort("variable_" + v + "_sort", possibleConstants.toSeq, Nil)
      OfSort(variables(v), tempSort)
    }
    val equalConstraints = for (v1 <- variables.indices if plan.variableConstraints.isRepresentativeAVariable(v1);
                                v2 <- Range(v1 + 1, variables.length) if plan.variableConstraints.isRepresentativeAVariable(v2);
                                if plan.variableConstraints.areEqual(v1, v2)) yield Equal(variables(v1), variables(v2))
    val unequalConstraints = variables.indices flatMap { v => plan.variableConstraints.getVariableUnequalTo(v) map { w => NotEqual(variables(v), variables(w)) } }
    val csp = new CSP(variables.toSet, possibleValuesConstraints ++ unequalConstraints ++ equalConstraints)

    // determine the modification stuff
    val allwaysAllowedModifications = classOf[AddOrdering] :: classOf[BindVariableToValue] :: classOf[InsertCausalLink] :: classOf[MakeLiteralsUnUnifiable] :: Nil
    val allowedModifications = allwaysAllowedModifications ++ (if (plan.problemConfiguration.taskInsertionAllowed) classOf[InsertPlanStepWithLink] :: Nil else Nil) ++
      (if (plan.problemConfiguration.decompositionAllowed) classOf[DecomposePlanStep] :: Nil else Nil)
    val allwaysAllowedFlaws = classOf[CausalThreat] :: classOf[OpenPrecondition] :: classOf[UnboundVariable] :: Nil
    val allowedFlaws = allwaysAllowedFlaws ++ (if (plan.problemConfiguration.decompositionAllowed) classOf[AbstractPlanStep] :: Nil else Nil)

    // decomposition history
    val planStepDecomposedByMethod: Map[PlanStep, DecompositionMethod] =
      if (plan.planStepDecomposedByMethod == null)
        Map()
      else (plan.planStepDecomposedByMethod.zipWithIndex collect { case (method, ps) if method != -1 => (planStepArray(ps), wrapDecompositionMethod(method)) }).toMap

    val parentsInDecompositionTree : Map[PlanStep,(PlanStep,PlanStep)]=
      if (plan.planStepParentInDecompositionTree == null)
        Map()
      else
        ((plan.planStepParentInDecompositionTree zip plan.planStepIsInstanceOfSubPlanPlanStep).zipWithIndex collect {
          case ((parent, subPlanPS), ps) if parent != -1 =>
            assert(subPlanPS != -1)
            val appliedDecompositionMethod = planStepDecomposedByMethod(planStepArray(parent))
            (planStepArray(ps), (planStepArray(parent), appliedDecompositionMethod.subPlan.planStepsAndRemovedPlanStepsWithoutInitGoal(subPlanPS - 2)))
        }).toMap

    // TODO: efficient representation can't handle LTL
    // and return the actual plan
    val symbolicPlan = Plan(planStepArray.toSeq, causalLinks, ordering, csp, planStepArray(0), planStepArray(1), ModificationsByClass(allowedModifications: _*),
                            FlawsByClass(allowedFlaws: _*), planStepDecomposedByMethod, parentsInDecompositionTree, false, LTLTrue)
    // sanity checks
    if (symbolicPlan.variableConstraints.isSolvable.getOrElse(true) != plan.variableConstraints.potentiallyConsistent) {
      println(symbolicPlan.variableConstraints.isSolvable + " " + plan.variableConstraints.potentiallyConsistent)
      csp.isSolvable
    }
    assert(symbolicPlan.variableConstraints.isSolvable.getOrElse(true) == plan.variableConstraints.potentiallyConsistent)
    if (symbolicPlan.flaws.size != plan.flaws.length)
      println(symbolicPlan.flaws.size + "==" + plan.flaws.length)
    assert(symbolicPlan.flaws.size == plan.flaws.length)

    symbolicPlan
  }


  // SEARCH NODE

  /**
    * Wraps the <br>whole</br> search space described by this node, i.e. itself and all of its children
    */
  def wrap[Payload](efficientSearchNode: EfficientSearchNode[Payload]): SearchNode = {

    def wrapWithParent(efficientSearchNode: EfficientSearchNode[Payload], parent: SearchNode): SearchNode = {
      // set the essentials
      val searchNode = new SearchNode(efficientSearchNode.id, { _ =>
        val plan = wrap(efficientSearchNode.plan)
        assert(plan.flaws.size == efficientSearchNode.plan.flaws.length)

        plan
      }, parent, if (efficientSearchNode.heuristic.length > 0) efficientSearchNode.heuristic(0) else 0)

      def computeContentIfNotDirty(unit: Unit): Unit = {
        searchNode.dirty = false
        // TODO payload transformator ???
        searchNode setPayload efficientSearchNode.payload

        // the order of the flaws in both representations might not be identical so we need to do a bit of reordering
        searchNode setSelectedFlaw { () =>
          val flawIndex = searchNode.plan.flaws indexWhere { flaw => FlawEquivalenceChecker(efficientSearchNode.plan.flaws(efficientSearchNode.selectedFlaw), flaw, this) }
          assert(flawIndex != -1 || efficientSearchNode.dirty || efficientSearchNode.searchState == SearchState.SOLUTION || efficientSearchNode.searchState == SearchState.DEADEND_HEURISTIC
                   || efficientSearchNode.searchState == SearchState.DEADEND_CSP || efficientSearchNode.searchState == SearchState.DEADEND_UNRESOLVABLEFLAW)
          flawIndex
        }
        // reorder modifications
        searchNode setModifications { () =>
          val modifications = searchNode.plan.flaws.zipWithIndex map { case (flaw, idx) =>
            val otherFlawIndex = efficientSearchNode.plan.flaws indexWhere { efficientFlaw => FlawEquivalenceChecker(efficientFlaw, flaw, this) }

            val efficientDeadModifications = efficientSearchNode.modifications(otherFlawIndex) map { efficientSearchNode.plan.modify } filterNot {
              _.variableConstraints
                .potentiallyConsistent
            } size

            if (!(flaw.resolvents(symbolicDomain).length == efficientSearchNode.modifications(otherFlawIndex).length - efficientDeadModifications)) {
              val symbolicMods = flaw.resolvents(symbolicDomain)
              val efficientMods = efficientSearchNode.modifications(otherFlawIndex)

              println(flaw.resolvents(symbolicDomain).length + " == " + efficientSearchNode.modifications(otherFlawIndex).length + " - " + efficientDeadModifications)
            }

            assert(flaw.resolvents(symbolicDomain).length == efficientSearchNode.modifications(otherFlawIndex).length - efficientDeadModifications)
            (efficientSearchNode.modifications(otherFlawIndex) map { wrap(_, searchNode.plan) }).toSeq
          }
          assert(searchNode.plan.flaws.size == efficientSearchNode.modifications.length)
          assert(modifications.length == searchNode.plan.flaws.size)
          modifications
        }
        searchNode setChildren { () => efficientSearchNode.children map { case (node, i) => (wrapWithParent(node, searchNode), i) } }

        if (efficientSearchNode.searchState != SearchState.INSEARCH && efficientSearchNode.searchState != SearchState.EXPLORED)
          searchNode setSearchState efficientSearchNode.searchState
      }

      if (efficientSearchNode.dirty)
        efficientSearchNode.setNotDirtyCallBack(computeContentIfNotDirty)
      else
        computeContentIfNotDirty()

      // set the things that only exist if the node is not dirty any more
      searchNode.dirty = efficientSearchNode.dirty

      searchNode
    }

    wrapWithParent(efficientSearchNode, null)
  }

  // MODIFICATION

  def wrap(efficientModification: EfficientModification, wrappedPlan: Plan): Modification = efficientModification match {
    case EfficientAddOrdering(_, _, before, after)                                                                                                                       =>
      AddOrdering(wrappedPlan, OrderingConstraint(wrapPlanStep(before, wrappedPlan), wrapPlanStep(after, wrappedPlan)))
    case EfficientBindVariable(_, _, variable, constant)                                                                                                                 =>
      BindVariableToValue(wrappedPlan, wrapVariable(variable, wrappedPlan), wrapConstant(constant))
    case EfficientDecomposePlanStep(_, _, decomposedPS, _, addedPlanSteps, newVariableSorts, variableConstraints, efficientCausalLinks, subOrdering, decomposedByMethod) =>
      val newVariables = newVariableSorts zip Range(wrappedPlan.getFirstFreeVariableID, wrappedPlan.getFirstFreeVariableID + newVariableSorts.length) map {
        case (sortIndex, variableIndex) => newVariableFormEfficient(variableIndex, sortIndex)
      }
      val appliedDecompositionMethod = wrapDecompositionMethod(decomposedByMethod(0)._2)
      val nonPresentDecomposedPlanStep = wrapPlanStep(decomposedPS, wrappedPlan)

      // compute all variables
      val allVariables: Seq[Variable] = sortVariables((wrappedPlan.variableConstraints.variables ++ newVariables).toSeq)
      val addedVariableConstraints = variableConstraints map { wrapConstraintVariablesSorted(_, allVariables) }
      val (outerConstraints, innerConstraints) = addedVariableConstraints partition { vc =>
        vc.getVariables exists { v => wrappedPlan.variableConstraints.variables contains v }
      }

      // instanciate the new plansteps
      val insertedPlanSteps = addedPlanSteps.zipWithIndex map { case ((schemaID, arguments, _, _, _), index) =>
        val symbolicArguments = arguments map { wrapVariableSorted(_, allVariables) }
        PlanStep(index + wrappedPlan.getFirstFreePlanStepID, wrapTask(schemaID), symbolicArguments)
      }
      // and compute the mapping
      // TODO might be buggy due to different task orderings in the efficient decomposition modification
      val planStepMapping = (insertedPlanSteps zip (addedPlanSteps map { addedPS => appliedDecompositionMethod.subPlan.planSteps(addedPS._5) })).toMap

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
      val init = PlanStep(-1, ReducedTask("init", isPrimitive = true, Nil, Nil, Nil, And[Literal](Nil), And[Literal](Nil)), Nil)
      val goal = PlanStep(-2, ReducedTask("goal", isPrimitive = true, Nil, Nil, Nil, And[Literal](Nil), And[Literal](Nil)), Nil)
      val subPlanPlanSteps = insertedPlanSteps :+ init :+ goal
      val subPlanOrderingConstraints = for (before <- addedPlanSteps.indices; after <- addedPlanSteps.indices if subOrdering.get(before)(after) == TaskOrdering.BEFORE) yield
        OrderingConstraint(getPlanStep(before + wrappedPlan.getFirstFreePlanStepID), getPlanStep(after + wrappedPlan.getFirstFreePlanStepID))

      val ordering = TaskOrdering(OrderingConstraint.allBetween(init, goal, insertedPlanSteps: _*) ++ subPlanOrderingConstraints, subPlanPlanSteps)
      val csp = CSP((newVariables ++ nonPresentDecomposedPlanStep.arguments).toSet, innerConstraints)
      val subPlan = Plan(subPlanPlanSteps, innerLinks, ordering, csp, init, goal, NoModifications, NoFlaws, Map[PlanStep, DecompositionMethod](), Map[PlanStep, (PlanStep, PlanStep)](),
                         false, additionalConstraints.LTLTrue)

      // construct the modification
      DecomposePlanStep(wrapPlanStep(decomposedPS, wrappedPlan), nonPresentDecomposedPlanStep, subPlan, outerConstraints, inheritedLinks, appliedDecompositionMethod, planStepMapping,
                        wrappedPlan)
    case EfficientInsertCausalLink(_, _, link, constraints)                                                                                                              =>
      val symbolicLink = wrap(link, wrappedPlan)
      val sortedVariables = sortVariables(wrappedPlan.variableConstraints.variables.toSeq)
      val symbolicConstraints = constraints map { wrapConstraintVariablesSorted(_, sortedVariables) }
      InsertCausalLink(wrappedPlan, symbolicLink, symbolicConstraints.toSeq)
    case EfficientInsertPlanStepWithLink(_, _, planstep, parameterSorts, link, constraints)                                                                              =>
      val newPlanStepArguments = parameterSorts zip Range(wrappedPlan.getFirstFreeVariableID, wrappedPlan.getFirstFreeVariableID + parameterSorts.length) map {
        case (sortIndex, variableIndex) => newVariableFormEfficient(variableIndex, sortIndex)
      }
      val newPlanStep = PlanStep(wrappedPlan.getFirstFreePlanStepID, wrapTask(planstep._1), newPlanStepArguments)

      // we have to build the link ourselves, as one of its plan steps does not yet exist in the plan
      val linkConsumer = wrapPlanStep(link.consumer, wrappedPlan)
      val causalLink = CausalLink(newPlanStep, linkConsumer, linkConsumer.substitutedPreconditions(link.conditionIndexOfConsumer))

      // compute all variables
      val allVariables = sortVariables((wrappedPlan.variableConstraints.variables ++ newPlanStepArguments).toSeq)
      val symbolicConstraints = constraints map { wrap(_, allVariables) }

      // construct the modification
      InsertPlanStepWithLink(newPlanStep, causalLink, symbolicConstraints, wrappedPlan)
    case EfficientMakeLiteralsUnUnifiable(_, _, variable1, variable2)                                                                                                    =>
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
      val threatherSame = efficientCausalThreat.threatingPlanStep == wrapper.unwrap(causalThreat.threater, causalThreat.plan)
      val threatherEffectSame = efficientCausalThreat.indexOfThreatingEffect ==
        causalThreat.threater.indexOfEffect(causalThreat.effectOfThreater, causalThreat.plan.variableConstraints)
      val linkSame = efficientCausalThreat.causalLink == wrapper.unwrap(causalThreat.link, causalThreat.plan)

      threatherSame && threatherEffectSame && linkSame
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
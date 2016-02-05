package de.uniulm.ki.panda3.efficient

import de.uniulm.ki.panda3.efficient.csp.{EfficientCSP, EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.domain.{EfficientDecompositionMethod, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.element.EfficientCausalLink
import de.uniulm.ki.panda3.efficient.plan.ordering.EfficientOrdering
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.plan.ordering.SymbolicTaskOrdering
import de.uniulm.ki.panda3.symbolic.plan.{SymbolicPlan, Plan}
import de.uniulm.ki.panda3.symbolic.plan.element.{CausalLink, OrderingConstraint, PlanStep}
import de.uniulm.ki.util.{SimpleDirectedGraph, BiMap}

/**
  * An explicit transformator between the inefficient symbolic part of panda3 and the efficient part thereof
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//TODO: the sort of a variable might not exist at all, it might just be some temporary sort
// -> add all sorts in the domain  (i.e. in tasks) and just "handle" them if they occur in plans
case class Wrapping(symbolicDomain: Domain, initialPlan: Plan) {

  private val domainConstants           : BiMap[Constant, Int]            = BiMap(symbolicDomain.constants.zipWithIndex)
  private val domainSorts               : BiMap[Sort, Int]                = BiMap(symbolicDomain.sorts.zipWithIndex)
  private val domainPredicates          : BiMap[Predicate, Int]           = BiMap(symbolicDomain.predicates.zipWithIndex)
  private val domainTasksObjects        : BiMap[Task, EfficientTask]      = {
    // get all schemas that are hidden in inits and goals
    val hiddenTasks = (initialPlan.init.schema :: initialPlan.goal.schema :: Nil) ++
      (symbolicDomain.decompositionMethods flatMap { method => method.subPlan.init.schema :: method.subPlan.goal.schema :: Nil })

    BiMap((symbolicDomain.tasks ++ hiddenTasks).distinct map { t => (t, computeEfficientTask(t)) })
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


  private def computeEfficientTask(task: Task): EfficientTask = {
    val parameterSorts = task.parameters map { v => domainSorts.toMap(v.sort) }
    val variableMap = task.parameters.zipWithIndex.toMap
    val variableConstraints = task.parameterConstraints map { computeEfficientVariableConstraint(_, variableMap) }

    // if there is anything other than a literal conjunct ... we can't handle it
    if (!task.isInstanceOf[ReducedTask]) noSupport(FORUMLASNOTSUPPORTED)

    val reducedTask = task.asInstanceOf[ReducedTask]

    val preconditions = reducedTask.precondition.conjuncts map { computeEfficientLiteral(_, variableMap) }
    val effects = reducedTask.effect.conjuncts map { computeEfficientLiteral(_, variableMap) }

    EfficientTask(isPrimitive = task.isPrimitive, parameterSorts.toArray, variableConstraints.toArray, preconditions.toArray, effects.toArray)
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
    val variableSorts = variableOrder map { v => domainSorts(v.sort) }
    val efficientCSP = new EfficientCSP(domain).addVariables(variableSorts.toArray)
    plan.variableConstraints.constraints foreach { computeEfficientVariableConstraint(_, variablesMap.toMap) }

    // ordering
    val ordering = new EfficientOrdering().addPlanSteps(orderedTasks.length)
    plan.orderingConstraints.originalOrderingConstraints foreach { case OrderingConstraint(before, after) => ordering.addOrderingConstraint(unwrap(before, plan), unwrap(after, plan)) }

    // causal links
    val causalLinks = plan.causalLinks map { case CausalLink(producer, consumer, literal) =>
      EfficientCausalLink(unwrap(producer, plan), unwrap(consumer, plan), producer.indexOfEffect(literal, plan.variableConstraints),
                          consumer.indexOfPrecondition(literal, plan.variableConstraints))
    }

    EfficientPlan(domain, planStepTasks.toArray, planStepParameters.toArray, planStepDecomposedBy.toArray, planStepParentInDecompositionTree.toArray, efficientCSP, ordering,
                  causalLinks.toArray)
  }


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

  def unwrap(constant: Constant): Int = domainConstants.toMap(constant)

  def wrapConstant(constant: Int): Constant = domainConstants.back(constant)

  def unwrap(sort: Sort): Int = domainSorts.toMap(sort)

  def wrapSort(sort: Int): Sort = domainSorts.back(sort)

  def unwrap(method: DecompositionMethod): Int = domainDecompositionMethods(method)

  def wrapDecompositionMethod(method: Int): DecompositionMethod = domainDecompositionMethods.back(method)


  /**
    * This will not work correctly if the plan is part of a decomposition method!!
    *
    * @return returns the number that represents the given variable in the given plan.
    */
  def unwrap(variable: Variable, plan: Plan): Int = plan.variableConstraints.variables.toSeq.sortBy({ _.id }).indexOf(variable)

  /**
    * This will not work correctly if the plan is part of a decomposition method!!
    *
    * @return returns the variable object that corresponds to the number in the efficient representation of the given plan
    */
  def wrapVariable(variable: Int, plan: Plan): Variable = plan.variableConstraints.variables.toSeq.sortBy({ _.id }).apply(variable)


  def unwrap(planStep: PlanStep, plan: Plan): Int = ((plan.init :: plan.goal :: Nil) ++ plan.planStepWithoutInitGoal).indexOf(planStep)

  def wrapPlanStep(planStep: Int, plan: Plan): PlanStep = ((plan.init :: plan.goal :: Nil) ++ plan.planStepWithoutInitGoal).apply(planStep)

  def unwrap(variableConstraint: VariableConstraint, plan: Plan): EfficientVariableConstraint = computeEfficientVariableConstraint(variableConstraint, { unwrap(_, plan) })

  def unwrap(plan: Plan): EfficientPlan = computeEfficientPlan(plan, efficientDomain, Nil)

  def wrap(plan: EfficientPlan): Plan = {
    val variables = plan.variableConstraints.variableSorts.zipWithIndex map {
      case (sortIndex, variableIndex) => Variable(variableIndex, "variable_" + variableIndex, domainSorts.back(sortIndex))
    }

    val taskCreationGraph = SimpleDirectedGraph(plan.planStepTasks.indices, plan.planStepTasks.indices map { i => (plan.planStepParentInDecompositonTree(i), i) } collect {
      case (a, b) if a != -1 => (a, b)
    })

    // plan steps -> has to be done this way (for with a fold) as we have to ensure that a parent was always already been computed
    val planStepArray: Array[PlanStep] = new Array(plan.planStepTasks.length)
    taskCreationGraph.topologicalOrdering.get foreach { psIndex =>
      val arguments = plan.planStepParameters(psIndex) map { variables(_) }
      val psDecomposedBy = plan.planStepDecomposedByMethod(psIndex)
      val decomposedBy = if (psDecomposedBy != -1) Some(wrapDecompositionMethod(psDecomposedBy)) else None
      val psParent = plan.planStepParentInDecompositonTree(psIndex)
      val parent = if (psParent != -1) Some(planStepArray(psParent)) else None

      planStepArray(psIndex) = PlanStep(psIndex, domainTasks.back(plan.planStepTasks(psIndex)), arguments, decomposedBy, parent)
    }

    // causal links
    val causalLinks = plan.causalLinks map { case EfficientCausalLink(producer, consumer, producerEffectIndex, _) =>
      CausalLink(planStepArray(producer), planStepArray(consumer), planStepArray(producer).substitutedEffects(producerEffectIndex))
    }

    // ordering constrints
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
}
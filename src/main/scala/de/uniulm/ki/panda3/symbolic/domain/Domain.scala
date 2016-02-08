package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.csp.{OfSort, NotOfSort}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.TaskSchemaTransitionGraph
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.util.{DirectedGraph, SimpleDirectedGraph}

/**
  * Planning domains contain the overall description of a planning problem.
  * They are composed of several list of things that exist in the planning domain.
  *
  * This, however does not include an initial nor goal state nor an initial task network.
  *
  *
  * A planning domain contains:
  * - a list of sorts (or types)
  * - a list of constants
  * - a list of FOL predicates
  * - a list of tasks
  * - a list of decomposition methods
  * - a list of decomposition axioms
  *
  * The specific order of any list does not contain any semantics.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class Domain(sorts: Seq[Sort], predicates: Seq[Predicate], tasks: Seq[Task], decompositionMethods: Seq[DecompositionMethod],
                  decompositionAxioms: Seq[DecompositionAxiom]) extends DomainUpdatable {

  lazy val taskSchemaTransitionGraph: TaskSchemaTransitionGraph        = TaskSchemaTransitionGraph(this)
  lazy val constants                : Seq[Constant]                    = (sorts flatMap { _.elements }).distinct
  lazy val sortGraph                : DirectedGraph[Sort]              = SimpleDirectedGraph(sorts, (sorts map { s => (s, s.subSorts) }).toMap)
  lazy val producersOf              : Map[Predicate, Seq[ReducedTask]] = (predicates map { pred => (pred, tasks collect { case t: ReducedTask => t } filter {
    _.effect.conjuncts exists { _.predicate == pred }
  })
  }).toMap


  /**
    * Determines the sort a constant originally belonged to.
    *
    * If there are multiple ones none is returned!
    */
  def getSortOfConstant(c: Constant): Option[Sort] = {
    val sortsContaining = sorts filter { _.elements contains c }
    val withoutSubSort = sortsContaining filter { s => sortsContaining forall { subs => !s.subSorts.contains(subs) } }
    if (withoutSubSort.size == 1) Some(withoutSubSort.head) else None
  }


  def addConstantsToDomain(constants: Seq[(Sort, Constant)]): Domain = {
    val sortTranslationMap = sortGraph.topologicalOrdering.get.foldRight[Map[Sort, Sort]](Map[Sort, Sort]())({ case (oldSort, translationMap) =>
      val newSort = Sort(oldSort.name, oldSort.elements ++ (constants collect { case (s, c) if s == oldSort => c }), oldSort.subSorts map translationMap)
      translationMap + (oldSort -> newSort)
                                                                                                             })

    val domainUpdate = ExchangeSorts(sortTranslationMap)

    update(domainUpdate)
  }

  def expandSortHierarchy(): DomainUpdate = {
    val sortTranslationMap = sortGraph.topologicalOrdering.get.foldRight[Map[Sort, Sort]](Map[Sort, Sort]())({ case (oldSort, translationMap) =>
      val newSubSorts = oldSort.subSorts map translationMap
      val allConstants = oldSort.elements ++ (newSubSorts flatMap { _.elements })
      val newSort = Sort(oldSort.name, allConstants.toSet.toSeq, newSubSorts)
      translationMap + (oldSort -> newSort)
                                                                                                             })

    ExchangeSorts(sortTranslationMap)
  }

  /** all tasks in the domain that are not contained in the task list of this domain, but occur (nevertheless) in this domain */
  lazy val hiddenTasks = decompositionMethods flatMap { method => method.subPlan.init.schema :: method.subPlan.goal.schema :: Nil }

  /** returns a list containing all declared sorts (i.e. the sorts member of this class) and all sorts that are created ad hoc, e.g. for variables and parameters */
  lazy val declaredAndUnDeclaredSorts: Seq[Sort] = {
    val taskSorts: Seq[Sort] = tasks flatMap { t => t.parameters map { _.sort } }
    val parameterConstraintSorts: Seq[Sort] = tasks flatMap { t => t.parameterConstraints collect {
      case OfSort(_, s)    => s
      case NotOfSort(_, s) => s
    }
    }
    val planVariableSorts: Seq[Sort] = decompositionMethods flatMap { _.subPlan.variableConstraints.variables map { _.sort } }
    val planConstraintSorts: Seq[Sort] = decompositionMethods flatMap {
      _.subPlan.variableConstraints.constraints collect {
        case OfSort(_, s)    => s
        case NotOfSort(_, s) => s
      }
    }

    val allSorts = sorts ++ taskSorts ++ parameterConstraintSorts ++ planVariableSorts ++ planConstraintSorts
    (allSorts filter { sorts.contains(_) }).distinct
  }


  override def update(domainUpdate: DomainUpdate): Domain = domainUpdate match {
    case AddMethod(newMethods)            => Domain(sorts, predicates, tasks, decompositionMethods ++ newMethods, decompositionAxioms)
    case AddPredicate(newPredicates)      => Domain(sorts, predicates ++ newPredicates, tasks, decompositionMethods, decompositionAxioms)
    case AddTask(newTasks)                => Domain(sorts, predicates, tasks ++ newTasks, decompositionMethods, decompositionAxioms)
    case ExchangeTaskSchemaInMethods(map) => Domain(sorts, predicates, tasks, decompositionMethods map { _.update(ExchangeTask(map)) }, decompositionAxioms)
    case _                                => Domain(sorts map { _.update(domainUpdate) }, predicates map { _.update(domainUpdate) }, tasks map { _.update(domainUpdate) },
                                                    decompositionMethods map { _.update(domainUpdate) },
                                                    decompositionAxioms)
  }
}
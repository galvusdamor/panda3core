// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2017 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem
import de.uniulm.ki.panda3.symbolic.csp.{NotOfSort, OfSort}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.TaskSchemaTransitionGraph
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.util.{DirectedGraph, SimpleDirectedGraph}

import scala.annotation.elidable
import scala.annotation.elidable._

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
                  decompositionAxioms: Seq[DecompositionAxiom],
                  mappingToOriginalGrounding: Option[GroundedDomainToDomainMapping] = None,
                  sasPlusRepresentation: Option[SASPlusRepresentation] = None) extends DomainUpdatable {

  // sanity check for the sorts
  @elidable(ASSERTION)
  def assertion(): Boolean = {
    assert(taskSet.size == tasks.size)
    //
    sorts foreach { s => s.subSorts foreach { ss => assert(sorts contains ss) } }
    decompositionMethods foreach { dm =>
      assert(taskSet contains dm.abstractTask, "abstract task " + dm.abstractTask + "not contained")
      assert(dm.subPlan.planStepsAndRemovedPlanSteps.length == dm.subPlan.planSteps.length)
      dm.subPlan.planStepsWithoutInitGoal map { _.schema } foreach { task =>
        if (!(taskSet contains task)) {
          val x = dm
          println("foo")
        }
        assert(taskSet contains task, "Task " + task.name + " is missing in the domain")
      }
    }

    //
    tasks foreach { t =>
      (t.precondition.containedPredicatesWithSign ++ t.effect.containedPredicatesWithSign) map { _._1 } foreach { p =>
        assert(p != null, "List contains null")
        assert(predicateSet contains p, "Predicate " + p.name + " not contained in predicate list")
      }
    }

    //
    sasPlusRepresentation match {
      case None                                                                                            => None
      case Some(rep@SASPlusRepresentation(sasPlusProblem, sasPlusIndexToTaskMap, sasPlusIndexToPredicate)) =>
        sasPlusIndexToTaskMap.values foreach { task => assert(task.isPrimitive, task.name + "must be primitive"); assert(taskSet contains task, task.shortInfo + " not contained") }
        primitiveTasks foreach { task => assert(rep.taskToSASPlusIndex.keySet contains task) }
        sasPlusIndexToTaskMap.keys foreach { i => assert(sasPlusProblem.getGroundedOperatorSignatures.length > i); assert(i >= 0) }

        sasPlusIndexToPredicate.values foreach { p => assert(predicateSet contains p, p.shortInfo + " not contained") }
      //predicates foreach { p => assert(rep.predicateToSASPlusIndex.keySet contains p) }
    }
    true
  }

  assert(assertion())

  lazy val taskSchemaTransitionGraph: TaskSchemaTransitionGraph = TaskSchemaTransitionGraph(this)
  lazy val constants                : Seq[Constant]             = (sorts flatMap { _.elements }).distinct
  lazy val sortGraph                : DirectedGraph[Sort]       = SimpleDirectedGraph(sorts, (sorts map { s => (s, s.subSorts) }).toMap)
  lazy val taskSet                  : Set[Task]                 = tasks.toSet
  lazy val predicateSet             : Set[Predicate]            = predicates.toSet

  // producer and consumer
  lazy val producersOf      : Map[Predicate, Seq[ReducedTask]]                     = (producersOfPosNeg map { case (a, (b, c)) => a -> (b ++ c).toSeq }).withDefaultValue(Nil)
  lazy val producersOfPosNeg: Map[Predicate, (Set[ReducedTask], Set[ReducedTask])] =
    (tasks collect { case t: ReducedTask => t } flatMap { t => t.effect.conjuncts map { t -> _ } } groupBy { _._2.predicate } map { case (p, ts) =>
      p -> (ts partition { _._2.isPositive } match {case (a, b) => (a map { _._1 } toSet, b map { _._1 } toSet)})
    }).withDefaultValue((Set[ReducedTask](), Set[ReducedTask]()))

  lazy val primitiveChangingPredicate: Map[Predicate, (Seq[ReducedTask], Seq[ReducedTask])] = {
    assert(constants.isEmpty, "Domain must be ground")
    predicates map { pred =>
      val adding = producersOfPosNeg(pred)._1 filter { _.isPrimitive }
      val deletingButNotAdding = producersOfPosNeg(pred)._2 diff adding filter { _.isPrimitive }
      pred -> (adding toSeq, deletingButNotAdding toSeq)
    }
  } toMap

  lazy val consumersOf: Map[Predicate, Seq[ReducedTask]] = (predicates map { pred =>
    (pred, tasks collect { case t: ReducedTask => t } filter {
      _.precondition.conjuncts exists { _.predicate == pred }
    })
  }).toMap

  lazy val primitiveConsumerOf: Map[Predicate, Seq[ReducedTask]] = consumersOf map { case (pred, cons) => pred -> cons.filter { _.isPrimitive } }

  lazy val primitiveTasks         : Seq[Task] = tasks filter { _.isPrimitive }
  lazy val abstractTasks          : Seq[Task] = tasks filterNot { _.isPrimitive }
  lazy val choicelessAbstractTasks: Set[Task] = abstractTasks filter { at => methodsForAbstractTasks(at).size == 1 } toSet

  lazy val allGroundedPrimitiveTasks: Seq[GroundTask] = primitiveTasks flatMap { _.instantiateGround }
  lazy val allGroundedAbstractTasks : Seq[GroundTask] = abstractTasks flatMap { _.instantiateGround }

  lazy val methodsWithIndexForAbstractTasks: Map[Task, Seq[(DecompositionMethod, Int)]] = decompositionMethods.zipWithIndex.groupBy(_._1.abstractTask).withDefaultValue(Nil)
  lazy val methodsForAbstractTasks         : Map[Task, Seq[DecompositionMethod]]        = methodsWithIndexForAbstractTasks map { case (a, b) => a -> (b map { _._1 }) } withDefaultValue Nil

  lazy val minimumMethodSize: Int = if (decompositionMethods.nonEmpty) decompositionMethods map { _.subPlan.planStepsWithoutInitGoal.length } min else -1
  lazy val maximumMethodSize: Int = if (decompositionMethods.nonEmpty) decompositionMethods map { _.subPlan.planStepsWithoutInitGoal.length } max else -1

  lazy val isClassical             : Boolean = decompositionMethods.isEmpty && abstractTasks.isEmpty
  lazy val isGround                : Boolean = predicates forall { _.argumentSorts.isEmpty }
  lazy val isTotallyOrdered        : Boolean = decompositionMethods forall { _.subPlan.orderingConstraints.isTotalOrder() }
  lazy val isHybrid                : Boolean =
    (decompositionMethods exists { _.subPlan.causalLinks.nonEmpty }) || (tasks exists { t => t.isAbstract && (!t.precondition.isEmpty || !t.effect.isEmpty) })
  lazy val hasNegativePreconditions: Boolean = tasks exists { _.preconditionsAsPredicateBool exists { !_._2 } }


  lazy val containEitherType: Boolean = false // sorts exists { s => (sortGraph.edgeList count { _._2 == s }) > 1 }

  /** A map showing for each task how deep the decomposition tree has to be, to be able to reach a primitive decomposition */
  lazy val minimumDecompositionHeightToPrimitive: Map[Task, Int] =
    taskSchemaTransitionGraph.condensation.topologicalOrdering.get.reverse.foldLeft(Map[Task, Int]().withDefaultValue(Integer.MAX_VALUE))(
      {
        case (minima, newTasks) if newTasks.size == 1 && newTasks.head.isPrimitive => minima.+((newTasks.head, 0))
        case (minima, newTasks)                                                    =>

          def iterate(currentMinima: Map[Task, Int]): Map[Task, Int] = {
            val newPairs = newTasks map { t =>
              // minimise over all methods
              t -> (methodsForAbstractTasks(t) map { m =>
                // take maximum within a method
                m.subPlan.planStepsWithoutInitGoal map { _.schema } map currentMinima max
              } min)
            } filter { _._2 != Integer.MAX_VALUE } map { case (a, b) => (a, b + 1) }

            val newMinima = currentMinima ++ newPairs
            if (newMinima == currentMinima) currentMinima else iterate(newMinima)
          }

          iterate(minima)
      })

  def minimumDecompositionHeightToPrimitiveForPlan(plan: Plan): Int = plan.planStepsWithoutInitGoal map { ps => minimumDecompositionHeightToPrimitive(ps.schema) } max

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

  /** Returns some sort which contains all the given variables, if multiple exists any one is selected. */
  def getAnySortContainingConstants(cs: Seq[Constant]): Option[Sort] = {
    val sortsContaining = sorts filter { s => cs forall s.elements.contains }
    val withoutSubSort = sortsContaining filter { s => sortsContaining forall { subs => !s.subSorts.contains(subs) } }
    withoutSubSort.headOption
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
    val taskSorts: Seq[Sort] = (tasks ++ hiddenTasks) flatMap { t => t.parameters map { _.sort } }
    val parameterConstraintSorts: Seq[Sort] = tasks flatMap { t =>
      t.parameterConstraints collect {
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
    allSorts.distinct
  }

  override def update(domainUpdate: DomainUpdate): Domain = domainUpdate match {
    case AddMethod(newMethods)               =>
      Domain(sorts, predicates, tasks, decompositionMethods ++ newMethods, decompositionAxioms, mappingToOriginalGrounding, sasPlusRepresentation)
    case AddPredicate(newPredicates)         =>
      Domain(sorts, predicates ++ newPredicates, tasks, decompositionMethods, decompositionAxioms, mappingToOriginalGrounding, sasPlusRepresentation)
    case AddTask(newTasks)                   =>
      Domain(sorts, predicates, tasks ++ newTasks, decompositionMethods, decompositionAxioms, mappingToOriginalGrounding, sasPlusRepresentation)
    case ExchangeTaskSchemaInMethods(map)    =>
      Domain(sorts, predicates, tasks, decompositionMethods map { _.update(ExchangeTask(map)) }, decompositionAxioms, mappingToOriginalGrounding, sasPlusRepresentation)
    case ExchangeLiteralsByPredicate(map, _) =>
      val newPredicates = map.values flatMap { case (a, b) => a :: b :: Nil }
      Domain(sorts, newPredicates.toSeq, tasks map { _.update(domainUpdate) }, decompositionMethods map { _.update(domainUpdate) }, decompositionAxioms, mappingToOriginalGrounding,
             sasPlusRepresentation map { _ update domainUpdate })
    case RemovePredicate(predicatesToRemove) => copy(predicates = predicates filterNot predicatesToRemove, tasks = tasks map { _ update domainUpdate },
                                                     decompositionMethods = decompositionMethods map { _.update(domainUpdate) },
                                                     decompositionAxioms = decompositionAxioms map { _.update(domainUpdate) },
                                                     sasPlusRepresentation = sasPlusRepresentation map { _ update domainUpdate }
                                                    )
    case _                                   => Domain(sorts map { _.update(domainUpdate) }, predicates map { _.update(domainUpdate) }, tasks map { _.update(domainUpdate) },
                                                       decompositionMethods map { _.update(domainUpdate) },
                                                       decompositionAxioms, mappingToOriginalGrounding,
                                                       sasPlusRepresentation map { _ update domainUpdate })
  }

  lazy val classicalDomain: Domain = Domain(sorts, predicates, tasks filter { _.isPrimitive }, Nil, Nil, mappingToOriginalGrounding, sasPlusRepresentation)

  lazy val statistics      : Map[String, Any] = Map(
                                                     "number of constants" -> constants.size,
                                                     "number of sorts" -> sorts.size,
                                                     "number of predicates" -> predicates.size,
                                                     "number of tasks" -> tasks.size,
                                                     "number of abstract tasks" -> abstractTasks.size,
                                                     "number of primitive tasks" -> primitiveTasks.size,
                                                     "number of decomposition methods" -> decompositionMethods.size,
                                                     "number of tasks in largest method" -> maximumMethodSize
                                                   )
  lazy val statisticsString: String           = statistics.mkString("\n")
}

case class GroundedDomainToDomainMapping(taskMapping: Map[Task, GroundTask]) extends DomainUpdatable {
  override def update(domainUpdate: DomainUpdate): GroundedDomainToDomainMapping =
    GroundedDomainToDomainMapping(taskMapping map {case (t,gt) => t.update(domainUpdate) -> gt.update(domainUpdate)})

}

case class SASPlusRepresentation(sasPlusProblem: SasPlusProblem, sasPlusIndexToTask: Map[Int, Task], sasPlusIndexToPredicate: Map[Int, Predicate]) extends DomainUpdatable {
  lazy val taskToSASPlusIndex: Map[Task, Int] = sasPlusIndexToTask map { case (a, b) => b -> a }

  lazy val predicateToSASPlusIndex: Map[Predicate, Int] = sasPlusIndexToPredicate map { case (a, b) => b -> a }

  override def update(domainUpdate: DomainUpdate): SASPlusRepresentation = domainUpdate match {
    case RemovePredicate(predicatesToRemove) =>
      SASPlusRepresentation(sasPlusProblem, sasPlusIndexToTask map { case (i, t) => i -> t.update(domainUpdate) }, sasPlusIndexToPredicate filterNot { predicatesToRemove contains _._2 })
    case _                                   => this.copy(sasPlusIndexToTask = sasPlusIndexToTask map { case (i, t) => (i, t update domainUpdate) })
  }
}
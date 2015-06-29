package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.domain.datastructures.TaskSchemaTransitionGraph
import de.uniulm.ki.panda3.domain.updates.{DomainUpdate, ExchangeSorts}
import de.uniulm.ki.panda3.logic.{Constant, DecompositionAxiom, Predicate, Sort}
import de.uniulm.ki.util.{DirectedGraph, SimpleDirectedGraphGraph}

/**
 * Planning domains contain the overall description of a planning problem.
 * They are composed of several list of things that exist in the planning domain.
 *
 * This, however does not include an initial nor goal state nor an initial task network. These are part of the [[de.uniulm.ki.panda3.problem.Problem]] description.
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

  lazy val taskSchemaTransitionGraph: TaskSchemaTransitionGraph = TaskSchemaTransitionGraph(this)
  lazy val constants: Seq[Constant] = (sorts flatMap {_.elements}).distinct
  lazy val sortGraph: DirectedGraph[Sort] = SimpleDirectedGraphGraph(sorts, (sorts map { s => (s, s.subSorts) }).toMap)


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
      val newSubsorts = oldSort.subSorts map translationMap
      val allConstants = oldSort.elements ++ (newSubsorts flatMap {_.elements})
      val newSort = Sort(oldSort.name, allConstants.toSet.toSeq, newSubsorts)
      translationMap + (oldSort -> newSort)
    })

    ExchangeSorts(sortTranslationMap)
  }

  override def update(domainUpdate: DomainUpdate): Domain = Domain(sorts map {_.update(domainUpdate)}, predicates map {_.update(domainUpdate)}, tasks map {_.update(domainUpdate)},
                                                                   decompositionMethods map {_.update(domainUpdate)}, decompositionAxioms)
}
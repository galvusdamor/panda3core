package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.domain.datastructures.TaskSchemaTransitionGraph
import de.uniulm.ki.panda3.logic.{Constant, DecompositionAxiom, Predicate, Sort}

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
case class Domain(sorts: Seq[Sort], constants: Seq[Constant], predicates: Seq[Predicate], tasks: Seq[Task], decompositionMethods: Seq[DecompositionMethod],
                  decompositionAxioms: Seq[DecompositionAxiom]) {

  lazy val taskSchemaTransitionGraph: TaskSchemaTransitionGraph = TaskSchemaTransitionGraph(this)

  lazy val allConstants: Seq[Constant] = sorts flatMap {_.elements}

}
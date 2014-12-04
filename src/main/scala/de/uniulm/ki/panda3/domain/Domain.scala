package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.logic.{Constant, DecompositionAxiom, Predicate, Sort}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Domain(sorts: Seq[Sort], constants: Seq[Constant], predicates: Seq[Predicate], tasks: Seq[Task], decompositionMethods: Seq[DecompositionMethod],
                  decompositionAxioms: Seq[DecompositionAxiom]) {

}

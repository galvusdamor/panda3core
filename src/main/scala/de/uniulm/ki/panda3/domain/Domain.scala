package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.logic.{Constant, DecompositionAxiom, Predicate, Sort}

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Domain(sorts : Iterable[Sort], constants : Iterable[Constant], predicates : Iterable[Predicate], tasks : Iterable[Task], decompositionMethods : Iterable[DecompositionMethod],
                  decompositionAxioms : Iterable[DecompositionAxiom]) {

}

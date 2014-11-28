package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.csp.Variable

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class DecompositionAxiom(abstractPredicate: Predicate, parameterVariables: Seq[Variable], rightHandSide: Formula) {

}

package de.uniulm.ki.panda3.logic

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Quantor extends Formula {

}

case class All(variable: Variable, formula: Formula) {

}

case class Exists(variable: Variable, formula: Formula) {

}
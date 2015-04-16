package de.uniulm.ki.panda3.logic

/**
 * Sorts aggregate constants of First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Sort(name: String, elements: Seq[Constant], parentSort: Option[Sort]) {

}
package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.csp.Variable
import de.uniulm.ki.panda3.logic.Literal

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Task(name: String, isPrimitive: Boolean, parameters: Seq[Variable], preconditions: Seq[Literal], effects: Seq[Literal]) {

}
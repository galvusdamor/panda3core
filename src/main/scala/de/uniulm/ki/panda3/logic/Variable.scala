package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.csp.CSP

/**
 * Represents variables of a [[CSP]].
 * Each variable has a name and it belongs to some [[Sort]].
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Variable(id: Int, name: String, sort: Sort) extends Value {
}

package de.uniulm.ki.panda3.symbolic.domain.updates

import de.uniulm.ki.panda3.symbolic.logic.Variable

/**
 * add variables to a csp
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AddVariables(newVariables: Seq[Variable]) extends DomainUpdate {

}

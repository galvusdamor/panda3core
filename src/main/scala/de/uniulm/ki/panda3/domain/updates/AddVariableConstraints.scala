package de.uniulm.ki.panda3.domain.updates

import de.uniulm.ki.panda3.csp.VariableConstraint

/**
 * adds variable Constraints to a CSP
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AddVariableConstraints(newConstraints: Seq[VariableConstraint]) extends DomainUpdate {

}

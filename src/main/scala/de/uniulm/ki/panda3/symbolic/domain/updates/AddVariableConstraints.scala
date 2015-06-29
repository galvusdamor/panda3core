package de.uniulm.ki.panda3.symbolic.domain.updates

import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint

/**
 * adds variable Constraints to a CSP
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AddVariableConstraints(newConstraints: Seq[VariableConstraint]) extends DomainUpdate {

}

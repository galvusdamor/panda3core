package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
 * Values are the generic superclass of both constants and variables, but not of more complex expressions derived from them
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Value extends DomainUpdatable with PrettyPrintable {
  override def update(domainUpdate: DomainUpdate): Value

  val isConstant: Boolean
}

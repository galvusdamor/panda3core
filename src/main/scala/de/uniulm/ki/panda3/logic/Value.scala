package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 * Values are the generic superclass of both constants and variables, but not of more complex expressions derived from them
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Value extends DomainUpdatable {
  override def update(domainUpdate: DomainUpdate): Value
}

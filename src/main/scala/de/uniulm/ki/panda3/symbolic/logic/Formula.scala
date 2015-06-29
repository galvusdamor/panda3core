package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Formula extends DomainUpdatable {
  override def update(domainUpdate: DomainUpdate): Formula
}

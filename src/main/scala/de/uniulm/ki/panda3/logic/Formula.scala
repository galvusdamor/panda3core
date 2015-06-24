package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Formula extends DomainUpdatable {
  override def update(domainUpdate: DomainUpdate): Formula
}

package de.uniulm.ki.panda3.domain

import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 * represents the capability, that a class can be updated with respect to a change in the domain
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait DomainUpdatable {

  def update(domainUpdate: DomainUpdate): DomainUpdatable
}

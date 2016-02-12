package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
 * represents the capability, that a class can be updated with respect to a change in the domain
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait DomainUpdatable {

  def update(domainUpdate: DomainUpdate): DomainUpdatable
}
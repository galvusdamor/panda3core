package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 * Predicate of First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Predicate(name: String, argumentSorts: Seq[Sort]) extends DomainUpdatable {

  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Predicate = Predicate(name, argumentSorts map {_.update(domainUpdate)})
}
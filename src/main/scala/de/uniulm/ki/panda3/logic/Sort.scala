package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.{DomainUpdate, ExchangeSorts}

/**
 * Sorts aggregate constants of First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Sort(name: String, elements: Seq[Constant], subSorts: Seq[Sort]) extends DomainUpdatable {

  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Sort = domainUpdate match {
    case ExchangeSorts(map) => map(this)
  }
}
package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.PrettyPrintable
import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.{DomainUpdate, ExchangeSorts}

/**
 * Sorts aggregate constants of First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Sort(name: String, elements: Seq[Constant], subSorts: Seq[Sort]) extends DomainUpdatable with PrettyPrintable {

  override def update(domainUpdate: DomainUpdate): Sort = domainUpdate match {
    case ExchangeSorts(map) => if (map.contains(this)) map(this) else this
    case _                  => this
  }

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = name

  /** returns a more detailed information about the object */
  override def longInfo: String = name + ": " + (elements map {_.shortInfo}).mkString(", ") + "; subsorts: " + (subSorts map {_.shortInfo}).mkString(", ")
}
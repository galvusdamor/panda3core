package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
 * Represents variables of a [[CSP]].
 * Each variable has a name and it belongs to some [[Sort]].
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Variable(id: Int, name: String, sort: Sort) extends Value with PrettyPrintable {
  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Variable = Variable(id, name, sort.update(domainUpdate))

  override val isConstant: Boolean = false

  /** returns a short information about the object */
  override def shortInfo: String = name + ":" + id

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = shortInfo + ":" + sort.shortInfo
}

package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.csp.CSP
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 * Represents variables of a [[CSP]].
 * Each variable has a name and it belongs to some [[Sort]].
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Variable(id: Int, name: String, sort: Sort) extends Value {
  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Variable = Variable(id, name, sort.update(domainUpdate))
}

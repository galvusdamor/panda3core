package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 * An object of first order logic we use to represented states.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Constant(name: String) extends Value {
  override def update(domainUpdate: DomainUpdate): Constant = this
}

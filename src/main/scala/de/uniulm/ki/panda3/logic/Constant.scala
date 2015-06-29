package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.PrettyPrintable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 * An object of first order logic we use to represented states.
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Constant(name: String) extends Value with PrettyPrintable {
  override def update(domainUpdate: DomainUpdate): Constant = this

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = name

  /** returns a more detailed information about the object */
  override def longInfo: String = name

  override val isConstant: Boolean = true
}

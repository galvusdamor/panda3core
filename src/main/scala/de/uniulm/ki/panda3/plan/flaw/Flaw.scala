package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.PrettyPrintable
import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.modification.Modification

/**
 * The abstract super class of all flaws
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Flaw extends PrettyPrintable {

  val plan: Plan

  def resolvents(domain: Domain): Seq[Modification]

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a detailed information about the object */
  override def longInfo: String = shortInfo
}

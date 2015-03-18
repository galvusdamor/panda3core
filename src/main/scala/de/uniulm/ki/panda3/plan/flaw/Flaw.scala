package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.domain.Domain
import de.uniulm.ki.panda3.plan.Plan
import de.uniulm.ki.panda3.plan.modification.Modification

/**
 * The abstract super class of all flaws
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Flaw {

  val plan: Plan

  def resolvants(domain: Domain): Seq[Modification]

}

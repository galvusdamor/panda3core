package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.util.HashMemo

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait Formula extends DomainUpdatable with PrettyPrintable{
  val isEmpty : Boolean

  override def update(domainUpdate: DomainUpdate): Formula

  val containedVariables : Set[Variable]

  def containedPredicatesWithSign : Set[(Predicate,Seq[Variable], Boolean)]

  def compileQuantors() : (Formula, Seq[Variable])
}
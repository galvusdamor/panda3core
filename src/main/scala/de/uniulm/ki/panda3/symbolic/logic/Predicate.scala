package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.domain.{Domain, DomainUpdatable}
import de.uniulm.ki.util.HashMemo

/**
 * Predicate of First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Predicate(name: String, argumentSorts: Seq[Sort]) extends DomainUpdatable with PrettyPrintable with HashMemo {

  def instantiate(domain: Domain, variablesForConstants: Map[Constant, Variable]): Seq[Literal] = {
    val allParameterCombinations = argumentSorts.foldLeft[Seq[Seq[Variable]]](Nil :: Nil)({ case (args, sort) => sort.elements flatMap { c => args map {_ :+ variablesForConstants(c)} } })
    allParameterCombinations map {Literal(this, true, _)}
  }


  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Predicate = Predicate(name, argumentSorts map {_.update(domainUpdate)})

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = name

  /** returns a more detailed information about the object */
  override def longInfo: String = name + (argumentSorts map {_.shortInfo}).mkString(", ")
}
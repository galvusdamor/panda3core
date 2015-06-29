package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.PrettyPrintable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.domain.{Domain, DomainUpdatable}

/**
 * Predicate of First Order Logic
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class Predicate(name: String, argumentSorts: Seq[Sort]) extends DomainUpdatable with PrettyPrintable {

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
// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.domain.{Domain, DomainUpdatable}
import de.uniulm.ki.util.{Internable, HashMemo}

import scala.collection.mutable

/**
  * Predicate of First Order Logic
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off covariant.equals
case class Predicate(name: String, argumentSorts: Seq[Sort]) extends DomainUpdatable with PrettyPrintable with HashMemo with Ordered[Predicate] {

  //println("PREDICATE " + name)
  //Thread.dumpStack()
  //System.in.read()

  lazy val allPossibleParameterCombinations: Seq[Seq[Constant]] = Sort allPossibleInstantiations argumentSorts

  lazy val instantiateGround: Seq[GroundLiteral] = allPossibleParameterCombinations flatMap { params => GroundLiteral(this, true, params) :: GroundLiteral(this, false, params) :: Nil }

  def instantiateWithVariables(variablesForConstants: Map[Constant, Variable]): Seq[Literal] = {
    val allParameterCombinations = allPossibleParameterCombinations map { _ map variablesForConstants }
    allParameterCombinations map { Literal(this, true, _) }
  }

  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Predicate = Predicate(name, argumentSorts map { _.update(domainUpdate) })

  //override def update(domainUpdate: DomainUpdate): Predicate = Predicate.intern((name, argumentSorts map { _.update(domainUpdate) }))

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = name

  /** returns a more detailed information about the object */
  override def longInfo: String = name + (argumentSorts map { _.shortInfo }).mkString(", ")


  override def equals(o: scala.Any): Boolean = if (!o.isInstanceOf[Predicate]) false
  else if (this.hashCode == o.hashCode()) true
  else {
    val oPredicate = o.asInstanceOf[Predicate]
    if (this.name != oPredicate.name) false else this.argumentSorts.sameElements(oPredicate.argumentSorts)
  }

  override def compare(that: Predicate): Int = this.name compare that.name
}

object Predicate extends Internable[(String, Seq[Sort]), Predicate] {
  override protected val applyTuple = (Predicate.apply _).tupled
}

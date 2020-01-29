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


package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.panda3.symbolic.logic._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait ActionCost extends DomainUpdatable {
  def evaluateOnGrounding(variableValues: (Variable => Constant), costValues: Map[GroundLiteral, Int]): ConstantActionCost

  def hasCostOne: Boolean

  def hasCostZero: Boolean

  def getFixedCost: Int

  override def update(domainUpdate: DomainUpdate) : ActionCost
}

case class ConstantActionCost(cost: Int) extends ActionCost {
  def evaluateOnGrounding(variableValues: (Variable => Constant), costValues: Map[GroundLiteral, Int]): ConstantActionCost = this

  val hasCostOne  : Boolean = cost == 1
  val hasCostZero : Boolean = cost == 0
  val getFixedCost: Int     = cost

  override def update(domainUpdate: DomainUpdate) : ConstantActionCost = this
}

case class FunctionalActionCost(predicate: Predicate, arguments: Seq[Value]) extends ActionCost {
  def evaluateOnGrounding(variableValues: (Variable => Constant), costValues: Map[GroundLiteral, Int]): ConstantActionCost = {
    // build ground literal
    val groundLiteral = GroundLiteral(predicate, isPositive = true,
                                      arguments map {
                                        case c: Constant => c
                                        case v: Variable => variableValues(v)
                                      })

    ConstantActionCost(costValues.getOrElse(groundLiteral, 0))
  }

  val hasCostOne  : Boolean = false
  val hasCostZero : Boolean = false
  def getFixedCost: Int     = {
    assert(false, "non-fixed cost")
    ???
  }

  override def update(domainUpdate: DomainUpdate) : FunctionalActionCost = FunctionalActionCost(predicate update domainUpdate, arguments.map(_.update(domainUpdate)))
}
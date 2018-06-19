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

import de.uniulm.ki.panda3.symbolic.logic._

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasExampleDomain1 {

  // sorts
  val sort1         : Sort        = Sort("sort 1", constantSort1(1) +: constantSort1(2) +: constantSort1(3) +: constantSort1(4) +: Vector(), Nil)
  // predicates
  val predicate1    : Predicate   = Predicate("predicate1", sort1 :: Nil)
  // tasks
  val task1         : ReducedTask = ReducedTask("task1", isPrimitive = true, variableSort1(2) :: Nil, Nil,Nil,
                                                precondition = And[Literal](Literal(predicate1, isPositive = false, variableSort1(2) :: Nil) :: Nil),
                                                effect = And[Literal](Literal(predicate1, isPositive = true, variableSort1(2) :: Nil) :: Nil))
  val init          : ReducedTask = ReducedTask("init", isPrimitive = true, variableSort1(3) :: Nil, Nil, Nil,precondition = And[Literal](Nil),
                                                effect = And[Literal](Literal(predicate1, isPositive = false, variableSort1(3) :: Nil) :: Nil))
  val goal1         : ReducedTask = ReducedTask("goal", isPrimitive = true, variableSort1(4) :: Nil, Nil,Nil,
                                                precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(4) :: Nil) :: Nil), effect = And[Literal](Nil))
  ////////////////////////////
  // the actual domain
  ////////////////////////////
  val exampleDomain1: Domain      = Domain(sort1 :: Nil, predicate1 :: Nil, task1 :: Nil, Nil, Nil)

  // constants
  def constantSort1(i: Int): Constant = Constant("constant_" + i + "_sort_1")


  ////////////////////////////
  // instance
  ///////////////////////////
  // variables
  def variableSort1(i: Int): Variable = Variable(i, "variable_" + i + "_sort1", sort1)

  // variables
  def instance_variableSort1(i: Int): Variable = Variable(i, "instance_variable_" + i + "_sort1", sort1)
}

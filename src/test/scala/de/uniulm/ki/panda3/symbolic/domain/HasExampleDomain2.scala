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

import de.uniulm.ki.panda3.symbolic.logic.{And, Literal, Predicate}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off magic.number
trait HasExampleDomain2 extends HasExampleDomain1 {

  // predicates
  val predicate2: Predicate = Predicate("predicate2", sort1 :: Nil)


  // tasks
  val task2: ReducedTask = ReducedTask("task2", isPrimitive = true, variableSort1(5) :: Nil, Nil, Nil,precondition = And[Literal](Nil), effect = And[Literal](
    Literal(predicate1, isPositive = false, variableSort1(5) :: Nil) :: Literal(predicate2, isPositive = true, variableSort1(5) :: Nil) :: Nil), ConstantActionCost(0))
  val goal2: ReducedTask = ReducedTask("goal", isPrimitive = true, variableSort1(6) :: Nil, Nil,Nil,
                                precondition = And[Literal](Literal(predicate1, isPositive = true, variableSort1(6) :: Nil) :: Literal(predicate2, isPositive
                                  = true, variableSort1(6) :: Nil) :: Nil), effect = And[Literal](Nil), ConstantActionCost(0))


  val exampleDomain2: Domain = Domain(sort1 :: Nil, predicate1 :: predicate2 :: Nil, task1 :: task2 :: Nil, Nil, Nil, Map())
}

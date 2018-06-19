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

package de.uniulm.ki.util

import org.scalatest.FlatSpec
import de.uniulm.ki.util._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class PackageTest extends FlatSpec{

  "Computing all Mappings bewteen two Seqs" must "yield all possible combinations"in {
    val listA = 0 :: 1 :: 2 :: Nil
    val listB = 10 :: 11 :: 12 :: Nil

    val allPossible = allMappings(listA,listB)

    assert(allPossible.length == 6)
    assert(allPossible contains ((2,10) :: (1,11) :: (0,12) :: Nil))
    assert(allPossible contains ((2,10) :: (1,12) :: (0,11) :: Nil))
    assert(allPossible contains ((2,11) :: (1,10) :: (0,12) :: Nil))
    assert(allPossible contains ((2,11) :: (1,12) :: (0,10) :: Nil))
    assert(allPossible contains ((2,12) :: (1,10) :: (0,11) :: Nil))
    assert(allPossible contains ((2,12) :: (1,11) :: (0,10) :: Nil))
  }
}

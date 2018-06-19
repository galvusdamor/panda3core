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

package de.uniulm.ki.panda3

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object efficient {

  /**
    * Switches a constant between its negative representation and its positive. Back and forth.
    *  Normally the constants are numbered 0..k-1. In their negative representation they are numbered -1..-k.
    */
  def switchConstant(c: Int): Int = (-c) - 1

}

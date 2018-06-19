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
package object symbolic {
  val FORUMLASNOTSUPPORTED: String = "arbitrary formulas (in preconditions and effects)."
  val NONSIMPLEMETHOD: String  = "non-simple decomposition methods"
  val REINSTANTIATINGPLANSINOUTSIDEMETHODS = "re-instantiating plan that are not part of methods"
  val UNSUPPORTEDPROBLEMTYPE = "any problem type apart from non-hierachical, pure-hierarchical and hybrid domains"
  val LIFTEDGOAL = "a lifted goal description"
  val LIFTEDINIT = "a lifted init description"

  def noSupport(message: String): Nothing = throw new UnsupportedOperationException("The current version of PANDA3 does not support " + message)
}

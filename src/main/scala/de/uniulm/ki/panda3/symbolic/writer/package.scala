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

package de.uniulm.ki.panda3.symbolic

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
package object writer {

  def toPDDLIdentifier(id: String): String = {
    val removedSigns = id map { c => if (c == '-') c else if (c == '?') c else if (c >= 'a' && c <= 'z') c else if (c >= 'A' && c <= 'Z') c else if (c >= '0' && c <= '9') c else '_' }
    if (removedSigns.charAt(0) >= '0' && removedSigns.charAt(0) <= '9') "p" + removedSigns else removedSigns
  }
}

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

package de.uniulm.ki.panda3.symbolic.search

import de.uniulm.ki.panda3.symbolic.plan.flaw.{AbstractPlanStep, Flaw}
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

/**
  * Functions that determine how the search is to be conducted
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */


abstract class IsModificationAllowed extends (Modification => Boolean) {}

object NoModifications extends IsModificationAllowed {
  override def apply(v1: Modification): Boolean = false
}

object AllModifications extends IsModificationAllowed {
  override def apply(v1: Modification): Boolean = true
}

case class ModificationsByClass(allowedModifications: Class[_]*) extends IsModificationAllowed {
  override def apply(v1: Modification): Boolean = allowedModifications contains v1.getClass
}

abstract class IsFlawAllowed extends (Flaw => Boolean) {}

object NoFlaws extends IsFlawAllowed {
  override def apply(v1: Flaw): Boolean = false
}

object AllFlaws extends IsFlawAllowed {
  override def apply(v1: Flaw): Boolean = true
}

case class FlawsByClass(allowedFlaws: Class[_]*) extends IsFlawAllowed {
  override def apply(v1: Flaw): Boolean = allowedFlaws contains v1.getClass
}

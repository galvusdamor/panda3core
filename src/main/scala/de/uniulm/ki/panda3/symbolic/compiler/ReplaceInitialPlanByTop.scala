// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2017 the original author or authors.
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

package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{Task, Domain, DecompositionMethod}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object ReplaceInitialPlanByTop extends DecompositionMethodTransformer[Unit] {

  override protected val allowToRemoveTopMethod = false

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, unit: Unit, originalDomain: Domain):
  (Seq[DecompositionMethod], Seq[Task]) = (methods :+ topMethod, Nil)

  override protected val transformationName: String = "artificialTop"
}


object ForceGroundedInitTop extends DecompositionMethodTransformer[Unit] {

  override protected val allowToRemoveTopMethod = false

  override protected def transformMethods(methods: Seq[DecompositionMethod], topMethod: DecompositionMethod, unit: Unit, originalDomain: Domain):
  (Seq[DecompositionMethod], Seq[Task]) = (methods :+ topMethod, Nil)

  override protected val transformationName: String = "forceTop"
}
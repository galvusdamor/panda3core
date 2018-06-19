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

package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{SASPlusGrounding, GroundedPrimitiveReachabilityAnalysis, PrimitiveReachabilityAnalysis}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
sealed trait AnalysisType {
  type ResultType
}

object SymbolicLiftedReachability extends AnalysisType {type ResultType = PrimitiveReachabilityAnalysis}

object SymbolicGroundedReachability extends AnalysisType {type ResultType = GroundedPrimitiveReachabilityAnalysis}

object SymbolicGroundedTaskDecompositionGraph extends AnalysisType {type ResultType = TaskDecompositionGraph}

object EfficientGroundedTDG extends AnalysisType {type ResultType = EfficientGroundedTaskDecompositionGraph}

object EfficientGroundedPlanningGraph extends AnalysisType {type ResultType = de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraph}

object EfficientGroundedPlanningGraphForRelax extends AnalysisType {
  type ResultType = de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraph
}

object SASPInput extends AnalysisType {type ResultType = SASPlusGrounding}

case class AnalysisMap(map: Map[AnalysisType, Any]) extends (AnalysisType => Any) {
  // this assertion is useless due to Java Type erasure
  //map foreach { case (anaType, elem) => assert(elem.isInstanceOf[anaType.ResultType]) }

  def apply(analysis: AnalysisType): analysis.ResultType = map(analysis).asInstanceOf[analysis.ResultType]

  def getOrElse(analysis: AnalysisType): Option[analysis.ResultType] = if (map.contains(analysis)) Some(map(analysis).asInstanceOf[analysis.ResultType]) else None

  def contains(analysis: AnalysisType): Boolean = map contains analysis

  def +(analysis: (AnalysisType, Any)): AnalysisMap = AnalysisMap(map + analysis)
}

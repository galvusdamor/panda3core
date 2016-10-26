package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, PrimitiveReachabilityAnalysis}

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

case class AnalysisMap(map: Map[AnalysisType, Any]) extends (AnalysisType => Any) {
  // this assertion is useless due to Java Type erasure
  //map foreach { case (anaType, elem) => assert(elem.isInstanceOf[anaType.ResultType]) }

  def apply(analysis: AnalysisType): analysis.ResultType = map(analysis).asInstanceOf[analysis.ResultType]

  def contains(analysis: AnalysisType): Boolean = map contains analysis

  def +(analysis: (AnalysisType, Any)): AnalysisMap = AnalysisMap(map + analysis)
}
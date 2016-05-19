package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.NaiveGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis, PrimitiveReachabilityAnalysis}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
sealed trait AnalysisType {
  type ResultType
}

object SymbolicLiftedReachability extends AnalysisType {type ResultType = PrimitiveReachabilityAnalysis}

object SymbolicGroundedReachability extends AnalysisType {type ResultType = GroundedPrimitiveReachabilityAnalysis}

object SymbolicGroundedTaskDecompositionGraph extends AnalysisType {type ResultType = NaiveGroundedTaskDecompositionGraph}

object EfficientGroundedTDG extends AnalysisType {type ResultType = EfficientGroundedTaskDecompositionGraph}


case class AnalysisMap(map: Map[AnalysisType, Any]) extends (AnalysisType => Any) {
  map foreach { case (anaType, elem) => assert(elem.isInstanceOf[anaType.ResultType]) }

  def apply(analysis: AnalysisType): analysis.ResultType = map(analysis).asInstanceOf[analysis.ResultType]

  def contains(analysis: AnalysisType): Boolean = map contains analysis

  def +(analysis: (AnalysisType, Any)): AnalysisMap = AnalysisMap(map + analysis)
}
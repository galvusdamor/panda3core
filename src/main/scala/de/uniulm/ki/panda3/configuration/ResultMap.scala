package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}


/**
  * all results a search might produce
  */
sealed trait ResultType {
  type ResultType
}

/** Timings are measured in milliseconds */
object ProcessingTimings extends ResultType {type ResultType = Map[String, Long]}

object SearchStatus extends ResultType {type ResultType = SearchState}

object SearchResult extends ResultType {type ResultType = Option[Plan]}

object SearchStatistics extends ResultType {type ResultType = Map[String,Int]}

object SearchSpace extends ResultType {type ResultType = SearchNode}

object SolutionInternalString extends ResultType {type ResultType = Option[String]}

object SolutionDotString extends ResultType {type ResultType = Option[String]}


object Timings {
  val PARSING                = "parsing"
  val FILEPARSER             = "parsing:file parser"
  val PARSER_SORT_EXPANSION  = "parsing:sort expansion"
  val PARSER_CWA             = "parsing:closed world assumption"
  val PARSER_SHOP_METHODS    = "parsing:shop methods"
  val PARSER_FLATTEN_FORMULA = "parsing:flatten methods"


  val PREPROCESSING                  = "preprocessing"
  val LIFTED_REACHABILITY_ANALYSIS   = "preprocessing:lifted reachabiltiy analysis"
  val GROUNDED_REACHABILITY_ANALYSIS = "preprocessing:grounded reachabiltiy analysis"
  val GROUNDED_TDG_ANALYSIS          = "preprocessing:grounded task decomposition graph analysis"
  val GROUNDING                      = "preprocessing:grounding"


  val HEURISTICS_PREPARATION = "heuristics preparation"

  val SEARCH                     = "search"
  val SEARCH_FLAW_SELECTOR       = "search:flaw selector"
  val SEARCH_FLAW_COMPUTATION    = "search:flaw computation"
  val SEARCH_FLAW_RESOLVER       = "search:flaw resolver computation"
  val SEARCH_GENERATE_SUCCESSORS = "search:apply modifications"
}

object Information {
  val NUMBER_OF_NODES = "number of search nodes"
  val NUMBER_OF_DISCARDED_NODES = "number of discarded nodes"
}

case class ResultMap(map: Map[ResultType, Any]) extends (ResultType => Any) {
  def apply(result: ResultType): result.ResultType = map(result).asInstanceOf[result.ResultType]
}
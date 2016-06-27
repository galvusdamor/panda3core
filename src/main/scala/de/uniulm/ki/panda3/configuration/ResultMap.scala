package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.symbolic.domain.Domain
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

object SearchStatistics extends ResultType {type ResultType = Map[String, Int]}

object SearchSpace extends ResultType {type ResultType = SearchNode}

object SolutionInternalString extends ResultType {type ResultType = Option[String]}

object SolutionDotString extends ResultType {type ResultType = Option[String]}

object PreprocessedDomainAndPlan extends ResultType {type ResultType = (Domain,Plan)}


object Timings {
  val PARSING                = "01 parsing:00:total"
  val FILEPARSER             = "01 parsing:01:file parser"
  val PARSER_SORT_EXPANSION  = "01 parsing:02:sort expansion"
  val PARSER_CWA             = "01 parsing:03:closed world assumption"
  val PARSER_SHOP_METHODS    = "01 parsing:04:shop methods"
  val PARSER_FLATTEN_FORMULA = "01 parsing:05:flatten formula"


  val PREPROCESSING                  = "02 preprocessing:00:total"
  val COMPILE_NEGATIVE_PRECONFITIONS = "02 preprocessing:01:compile negative preconditions"
  val LIFTED_REACHABILITY_ANALYSIS   = "02 preprocessing:11:lifted reachabiltiy analysis"
  val GROUNDED_REACHABILITY_ANALYSIS = "02 preprocessing:12:grounded reachabiltiy analysis"
  val GROUNDED_TDG_ANALYSIS          = "02 preprocessing:23:grounded task decomposition graph analysis"
  val GROUNDING                      = "02 preprocessing:84:grounding"


  val HEURISTICS_PREPARATION = "03 heuristics preparation:00:total"

  val SEARCH_PREPARATION               = "10 search preparation:00:total"
  val COMPUTE_EFFICIENT_REPRESENTATION = "10 search preparation:01:compute efficient representation"

  val SEARCH                          = "20 search:00:total"
  val SEARCH_FLAW_RESOLVER_ESTIMATION = "20 search:01:flaw estimation"
  val SEARCH_FLAW_COMPUTATION         = "20 search:02:flaw computation"
  val SEARCH_FLAW_SELECTOR            = "20 search:10:flaw selector"
  val SEARCH_FLAW_RESOLVER            = "20 search:20:flaw resolver computation"
  val SEARCH_GENERATE_SUCCESSORS      = "20 search:30:apply modifications"
  val SEARCH_COMPUTE_HEURISTIC        = "20 search:40:compute heuristic"
}

object Information {
  val NUMBER_OF_NODES           = "10 search nodes:00:total"
  val NUMBER_OF_EXPANDED_NODES  = "10 search nodes:01:expanded"
  val NUMBER_OF_DISCARDED_NODES = "10 search nodes:02:discarded nodes"
}

case class ResultMap(map: Map[ResultType, Any]) extends (ResultType => Any) {
  def apply(result: ResultType): result.ResultType = map(result).asInstanceOf[result.ResultType]
}
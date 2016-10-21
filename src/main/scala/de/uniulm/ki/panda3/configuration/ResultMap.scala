package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}


/**
  * all results a search might produce
  */
sealed trait ResultType {
  type ResultType
}

/** Timings are measured in milliseconds */
object ProcessingTimings extends ResultType {type ResultType = TimeCapsule}

object SearchStatus extends ResultType {type ResultType = SearchState}

object SearchResult extends ResultType {type ResultType = Option[Plan]}

object AllFoundPlans extends ResultType {type ResultType = Seq[Plan]}

object AllFoundSolutionPathsWithHStar extends ResultType {type ResultType = Seq[Seq[(SearchNode, Int)]]}

object SearchStatistics extends ResultType {type ResultType = InformationCapsule}

object SearchSpace extends ResultType {type ResultType = SearchNode}

object SolutionInternalString extends ResultType {type ResultType = Option[String]}

object SolutionDotString extends ResultType {type ResultType = Option[String]}

object PreprocessedDomainAndPlan extends ResultType {type ResultType = (Domain, Plan)}

object FinalTaskDecompositionGraph extends ResultType {type ResultType = TaskDecompositionGraph}


object Timings {
  val TOTAL_TIME = "00 total:00:total"

  val PARSING                   = "01 parsing:00:total"
  val FILEPARSER                = "01 parsing:01:file parser"
  val PARSER_SORT_EXPANSION     = "01 parsing:02:sort expansion"
  val PARSER_CWA                = "01 parsing:03:closed world assumption"
  val PARSER_SHOP_METHODS       = "01 parsing:04:shop methods"
  val PARSER_ELIMINATE_EQUALITY = "01 parsing:05:eliminate identical variables"
  val PARSER_FLATTEN_FORMULA    = "01 parsing:06:flatten formula"


  val PREPROCESSING                   = "02 preprocessing:00:total"
  val COMPILE_NEGATIVE_PRECONFITIONS  = "02 preprocessing:01:compile negative preconditions"
  val COMPILE_UNIT_METHODS            = "02 preprocessing:02:compile unit methods"
  val COMPILE_ORDER_IN_METHODS        = "02 preprocessing:03:compile order in methods"
  val LIFTED_REACHABILITY_ANALYSIS    = "02 preprocessing:11:lifted reachabiltiy analysis"
  val GROUNDED_REACHABILITY_ANALYSIS  = "02 preprocessing:12:grounded reachabiltiy analysis"
  val GROUNDED_PLANNINGGRAPH_ANALYSIS = "02 preprocessing:13:grounded planning graph analysis"
  val GROUNDED_TDG_ANALYSIS           = "02 preprocessing:23:grounded task decomposition graph analysis"
  val GROUNDING                       = "02 preprocessing:84:grounding"


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
  val SEARCH_COMPUTE_FILTER           = "20 search:50:compute filter"

  val allTimings = TOTAL_TIME :: PARSING :: FILEPARSER :: PARSER_SORT_EXPANSION :: PARSER_CWA :: PARSER_SHOP_METHODS :: PARSER_ELIMINATE_EQUALITY :: PARSER_FLATTEN_FORMULA ::
    PREPROCESSING :: COMPILE_NEGATIVE_PRECONFITIONS :: COMPILE_UNIT_METHODS :: COMPILE_ORDER_IN_METHODS :: LIFTED_REACHABILITY_ANALYSIS :: GROUNDED_REACHABILITY_ANALYSIS ::
    GROUNDED_PLANNINGGRAPH_ANALYSIS :: GROUNDED_TDG_ANALYSIS :: HEURISTICS_PREPARATION :: SEARCH_PREPARATION :: COMPUTE_EFFICIENT_REPRESENTATION :: SEARCH ::
    SEARCH_FLAW_RESOLVER_ESTIMATION :: SEARCH_FLAW_COMPUTATION :: SEARCH_FLAW_SELECTOR :: SEARCH_FLAW_RESOLVER :: SEARCH_GENERATE_SUCCESSORS :: SEARCH_COMPUTE_HEURISTIC :: Nil
}

object Information {
  val NUMBER_OF_NODES           = "10 search nodes:00:total"
  val NUMBER_OF_EXPANDED_NODES  = "10 search nodes:01:expanded"
  val NUMBER_OF_DISCARDED_NODES = "10 search nodes:02:discarded nodes"

  val NUMBER_OF_CONSTANTS         = "30 problem:01:number of constants"
  val NUMBER_OF_PREDICATES        = "30 problem:02:number of predicates"
  val NUMBER_OF_ACTIONS           = "30 problem:03:number of actions"
  val NUMBER_OF_ABSTRACT_ACTIONS  = "30 problem:04:number of abstract actions"
  val NUMBER_OF_PRIMITIVE_ACTIONS = "30 problem:05:number of primitive actions"
  val NUMBER_OF_METHODS           = "30 problem:06:number of methods"

  val PLAN_SIZE = "20 search plans:01:number of plansteps"
}

case class ResultMap(map: Map[ResultType, Any]) extends (ResultType => Any) {
  def apply(result: ResultType): result.ResultType = map(result).asInstanceOf[result.ResultType]
}
package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}


/**
  * all results a search might produce
  */
sealed trait ResultType extends DefaultLongInfo {
  type ResultType
}

/** Timings are measured in milliseconds */
object ProcessingTimings extends ResultType {
  type ResultType = TimeCapsule

  override def longInfo: String = "timings"
}

object SearchStatus extends ResultType {
  type ResultType = SearchState

  override def longInfo: String = "search status"
}

object SearchResult extends ResultType {
  type ResultType = Option[Plan]

  override def longInfo: String = "search result"
}


object InternalSearchResult extends ResultType {
  type ResultType = Option[Plan]

  override def longInfo: String = "internal search result (may contain heavily compiled actions etc)"
}

object AllFoundPlans extends ResultType {
  type ResultType = Seq[Plan]

  override def longInfo: String = "all plans"
}

object AllFoundSolutionPathsWithHStar extends ResultType {
  type ResultType = Seq[Seq[(SearchNode, Int)]]

  override def longInfo: String = "all plants with H* paths"
}

object SearchStatistics extends ResultType {
  type ResultType = InformationCapsule

  override def longInfo: String = "statistics"
}

object SearchSpace extends ResultType {
  type ResultType = SearchNode

  override def longInfo: String = "search space"
}

object SolutionInternalString extends ResultType {
  type ResultType = Option[String]

  override def longInfo: String = "solution string"
}

object SolutionDotString extends ResultType {
  type ResultType = Option[String]

  override def longInfo: String = "solution dot"
}

object PreprocessedDomainAndPlan extends ResultType {
  type ResultType = (Domain, Plan)

  override def longInfo: String = "preprocessed domain and plan"
}

object UnprocessedDomainAndPlan extends ResultType {
  type ResultType = (Domain, Plan)

  override def longInfo: String = "unprocessed domain and plan"
}

object FinalTaskDecompositionGraph extends ResultType {
  type ResultType = TaskDecompositionGraph

  override def longInfo: String = "final TDG"
}

object FinalGroundedReachability extends ResultType {
  type ResultType = GroundedPrimitiveReachabilityAnalysis

  override def longInfo: String = "final grounded reachability"
}


object Timings {
  val TOTAL_TIME = "00 total:00:total"

  val PARSING                   = "01 parsing:00:total"
  val FILEPARSER                = "01 parsing:01:file parser"
  val PARSER_SORT_EXPANSION     = "01 parsing:02:sort expansion"
  val PARSER_CWA                = "01 parsing:03:closed world assumption"
  val PARSER_SHOP_METHODS       = "01 parsing:04:shop methods"
  val PARSER_ELIMINATE_EQUALITY = "01 parsing:05:eliminate identical variables"
  val PARSER_STRIP_HYBRID       = "01 parsing:06:strip domain of hybridity"
  val PARSER_FLATTEN_FORMULA    = "01 parsing:07:flatten formula"


  val PREPROCESSING                   = "02 preprocessing:00:total"
  val COMPILE_NEGATIVE_PRECONFITIONS  = "02 preprocessing:01:compile negative preconditions"
  val COMPILE_UNIT_METHODS            = "02 preprocessing:02:compile unit methods"
  val COMPILE_ORDER_IN_METHODS        = "02 preprocessing:03:compile order in methods"
  val SPLIT_PARAMETERS                = "02 preprocessing:04:split parameter"
  val TOP_TASK                        = "02 preprocessing:99:create artificial top task"
  val LIFTED_REACHABILITY_ANALYSIS    = "02 preprocessing:11:lifted reachabiltiy analysis"
  val GROUNDED_PLANNINGGRAPH_ANALYSIS = "02 preprocessing:12:grounded planning graph analysis"
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

  val VERIFY_TOTAL     = "40 sat:00:total"
  val GENERATE_FORMULA = "40 sat:10:generate formula"
  val TRANSFORM_DIMACS = "40 sat:20:transform to DIMACS"
  val WRITE_FORMULA    = "40 sat:30:write formula"
  val SAT_SOLVER       = "40 sat:40:SAT solver"
}

object Information {
  val DOMAIN_NAME  = "00 global:00:domain"
  val PROBLEM_NAME = "00 global:01:problem"
  val RANDOM_SEED  = "00 global:02:randomseed"
  val SOLVED_STATE = "00 global:90:planner result"
  val ERROR        = "00 global:99:error"

  val SOLUTION_LENGTH = "01 solution:01:number of primitive plan steps"

  val ACYCLIC                      = "02 properties:01:acyclic"
  val MOSTLY_ACYCLIC               = "02 properties:02:mostly acyclic"
  val REGULAR                      = "02 properties:03:regular"
  val TAIL_RECURSIVE               = "02 properties:04:tail recursive"
  val TOTALLY_ORDERED              = "02 properties:05:totally ordered"
  val MINIMUM_DECOMPOSITION_HEIGHT = "02 properties:10:minimum decomposition height"

  val NUMBER_OF_NODES           = "10 search nodes:00:total"
  val NUMBER_OF_EXPANDED_NODES  = "10 search nodes:01:expanded"
  val NUMBER_OF_DISCARDED_NODES = "10 search nodes:02:discarded nodes"

  val PLAN_SIZE = "20 search plans:01:number of plansteps"

  val DECOMPOSITION_MODIFICATIONS = "21 tdg heuristic:10:executed decompositions"
  val ADD_ORDERING_MODIFICATIONS = "21 tdg heuristic:11:executed add orderings"
  val ADD_CAUSAL_LINK_MODIFICATIONS = "21 tdg heuristic:12:executed add causal link"
  val ONLY_ONE_DECOMPOSITION      = "21 tdg heuristic:20:only one decomposition"
  val TDG_COMPUTED_HEURISTIC      = "21 tdg heuristic:21:TDG computed heuristic"

  val TDG_COMPUTATION_INCREASED_H           = "21 tdg heuristic:80:TDG recomputation increased heuristic"
  val TDG_COMPUTATION_INCREASED_H_RELATIVE_INCREMENT = "21 tdg heuristic:81:TDG recomputation relative increment (excluding infinity cases)"
  val TDG_COMPUTATION_INCREASED_TO_INFINITY = "21 tdg heuristic:82:TDG recomputation increased heuristic to infinity"
  //val ONLY_ONE_DECOMPOSITION = "21 tdg heuristic:10:"

  val NUMBER_OF_CONSTANTS         = "30 problem:01:number of constants"
  val NUMBER_OF_PREDICATES        = "30 problem:02:number of predicates"
  val NUMBER_OF_ACTIONS           = "30 problem:03:number of actions"
  val NUMBER_OF_ABSTRACT_ACTIONS  = "30 problem:04:number of abstract actions"
  val NUMBER_OF_PRIMITIVE_ACTIONS = "30 problem:05:number of primitive actions"
  val NUMBER_OF_METHODS           = "30 problem:06:number of methods"

  val PLAN_LENGTH             = "40 sat:00:plan length"
  val NUMBER_OF_VARIABLES     = "40 sat:01:number of variables"
  val NUMBER_OF_CLAUSES       = "40 sat:02:number of clauses"
  val ICAPS_K                 = "40 sat:10:K ICAPS"
  val LOG_K                   = "40 sat:11:K LOG"
  val TSTG_K                  = "40 sat:12:K task schema transition graph"
  val DP_K                    = "40 sat:13:K DP"
  val OFFSET_K                = "40 sat:14:K offset"
  val ACTUAL_K                = "40 sat:15:K chosen value"
  val STATE_FORMULA           = "40 sat:20:state formula"
  val ORDER_CLAUSES           = "40 sat:21:order clauses"
  val METHOD_CHILDREN_CLAUSES = "40 sat:22:method children clauses"
  val NUMBER_OF_PATHS         = "40 sat:30:number of paths"
  val MAX_PLAN_LENGTH         = "40 sat:31:maximum plan length"
  val SOLVED                  = "40 sat:90:solved"
  val TIMEOUT                 = "40 sat:91:timeout"

}

case class ResultMap(map: Map[ResultType, Any]) extends (ResultType => Any) {
  def apply(result: ResultType): result.ResultType = map(result).asInstanceOf[result.ResultType]
}
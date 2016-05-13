package de.uniulm.ki.panda3.configuration

import java.io.InputStream
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.search.EfficientSearchNode
import de.uniulm.ki.panda3.symbolic
import de.uniulm.ki.panda3.symbolic.compiler.pruning.{PruneDecompositionMethods, PruneHierarchy}
import de.uniulm.ki.panda3.symbolic.compiler.{Grounding, ToPlainFormulaRepresentation, SHOPMethodCompiler, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, PrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.NaiveGroundedTaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{EverythingIsReachable, GroundedForwardSearchReachabilityAnalysis, LiftedForwardSearchReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PlanningConfiguration(
                                  parsingConfiguration: ParsingConfiguration,
                                  preprocessingConfiguration: PreprocessingConfiguration,
                                  searchConfiguration: SearchConfiguration,
                                  postprocessingConfiguration: PostprocessingConfiguration) {

  import Timings._

  /**
    * Runs the complete planner (including the parser) and returs the results
    */
  def runResultSearch(domain: InputStream, problem: InputStream, timeCapsule: TimeCapsule = new TimeCapsule()): ResultMap = runSearchHandle(domain, problem, None, timeCapsule)._4()

  /**
    * Runs the complete planner and returs the results
    */
  def runResultSearch(domain: Domain, problem: Plan, timeCapsule: TimeCapsule): ResultMap = runSearchHandle(domain, problem, None, timeCapsule)._4()

  /**
    * Runs the complete planner (including the parser) and returns a handle to the search and a waiting function for the result
    */
  def runSearchHandle(domain: InputStream, problem: InputStream, releaseSemaphoreEvery: Option[Int], timeCapsule: TimeCapsule = new TimeCapsule()):
  (SearchNode, Semaphore, AbortFunction, Unit => ResultMap) = {
    val parsedDomainAndProblem = runParsing(domain, problem, timeCapsule)
    runSearchHandle(parsedDomainAndProblem._1._1, parsedDomainAndProblem._1._2, releaseSemaphoreEvery, parsedDomainAndProblem._2)
  }

  /**
    * Runs the complete planner and returns a handle to the search and a waiting function for the result
    */
  def runSearchHandle(domain: Domain, problem: Plan, releaseSemaphoreEvery: Option[Int], timeCapsule: TimeCapsule):
  (SearchNode, Semaphore, AbortFunction, Unit => ResultMap) = {
    // run the preprocessing step
    val ((domainAndPlan, preprocessedAnalysisMap), _) = runPreprocessing(domain, problem, timeCapsule)

    // !!!! ATTENTION we use side effects for the sake of simplicity
    var analysisMap = preprocessedAnalysisMap

    // some heuristics need additional preprocessing, e.g. to build datastructures they need
    timeCapsule start HEURISTICS_PREPARATION
    // TDG based heuristics need the TDG
    if (searchConfiguration.heuristic contains TDGMinimumModification) if (!(analysisMap contains SymbolicGroundedTaskDecompositionGraph)) {
      timeCapsule start GROUNDED_TDG_ANALYSIS
      analysisMap = runNaiveGroundedTaskDecompositionGraph(domainAndPlan._1, domainAndPlan._2, analysisMap)
      timeCapsule stop GROUNDED_TDG_ANALYSIS
    }
    timeCapsule stop HEURISTICS_PREPARATION


    // create the information container
    val informationCapsule = new InformationCapsule

    // now we have to decide which representation to use for the search
    if (!searchConfiguration.efficientSearch) {
      val (searchTreeRoot, nodesProcessed, resultfunction, abortFunction) = searchConfiguration.searchAlgorithm match {
        case DFSType => symbolic.search.DFS.startSearch(domainAndPlan._1, domainAndPlan._2, searchConfiguration.nodeLimit, releaseSemaphoreEvery, searchConfiguration.printSearchInfo,
                                                        informationCapsule, timeCapsule)
        case _       => throw new UnsupportedOperationException("Any other symbolic search algorithm besides DFS is not supported.")
      }

      (searchTreeRoot, nodesProcessed, abortFunction, { _ =>
        val actualResult: Option[Plan] = resultfunction(())
        runPostProcessing(timeCapsule, informationCapsule, searchTreeRoot, actualResult)
      })
    } else {
      val wrapper = Wrapping(domainAndPlan)
      val initialPlan = wrapper.unwrap(domainAndPlan._2)

      ???
    }
  }

  //  case class SearchStatus() extends ResultType {type ResultType = SearchState}

  //case class SearchResult() extends ResultType {type ResultType = Option[Plan]}

  //case class SearchStatistics() extends ResultType {type ResultType = Any}

  //case class SearchSpace() extends ResultType {type ResultType = SearchNode}

  //case class SolutionInternalString() extends ResultType {type ResultType = Option[String]}

  //case class SolutionDotString() extends ResultType {type ResultType = Option[String]}


  def runPostProcessing(timeCapsule: TimeCapsule, informationCapsule: InformationCapsule, rootNode: SearchNode, result: Option[Plan]): ResultMap =
    ResultMap(postprocessingConfiguration.resultsToProduce map { resultType => (resultType, resultType match {
      case ProcessingTimings => timeCapsule.timeMap
      case SearchStatus      => if (result.isDefined) SearchState.SOLUTION
      else if (searchConfiguration.nodeLimit.isEmpty || searchConfiguration.nodeLimit.get > informationCapsule.informationMap(Information.NUMBER_OF_NODES))
        SearchState.UNSOLVABLE
      else SearchState.INSEARCH

      case SearchResult           => result
      case SearchStatistics       => informationCapsule.informationMap
      case SearchSpace            => rootNode
      case SolutionInternalString => result match {case Some(plan) => Some(plan.longInfo); case _ => None}
      case SolutionDotString      => result match {case Some(plan) => Some(plan.dotString); case _ => None}
    })
    } toMap
             )


  /**
    * runs the parser
    */
  def runParsing(domain: InputStream, problem: InputStream, timeCapsule: TimeCapsule = new TimeCapsule()): ((Domain, Plan), TimeCapsule) = {
    // parse the problem and run the main function
    timeCapsule start PARSING

    timeCapsule start FILEPARSER
    val parsedDomainAndProblem = parsingConfiguration.parserType match {
      case XMLParserType  => XMLParser.asParser.parseDomainAndProblem(domain, problem)
      case HDDLParserType => HDDLParser.parseDomainAndProblem(domain, problem)
    }
    timeCapsule stop FILEPARSER

    timeCapsule start PARSER_SORT_EXPANSION
    val sortsExpandedDomainAndProblem = if (parsingConfiguration.expandSortHierarchy) {
      val sortExpansion = parsedDomainAndProblem._1.expandSortHierarchy()
      (parsedDomainAndProblem._1.update(sortExpansion), parsedDomainAndProblem._2.update(sortExpansion))
    } else parsedDomainAndProblem
    timeCapsule stop PARSER_SORT_EXPANSION

    timeCapsule start PARSER_CWA
    val cwaApplied = if (parsingConfiguration.closedWorldAssumption) ClosedWorldAssumption.transform(sortsExpandedDomainAndProblem, ()) else sortsExpandedDomainAndProblem
    timeCapsule stop PARSER_CWA

    timeCapsule start PARSER_SHOP_METHODS
    val simpleMethod = if (parsingConfiguration.compileSHOPMethods) SHOPMethodCompiler.transform(cwaApplied, ()) else cwaApplied
    timeCapsule stop PARSER_SHOP_METHODS

    timeCapsule start PARSER_FLATTEN_FORMULA
    val flattened = if (parsingConfiguration.toPlainFormulaRepresentation) ToPlainFormulaRepresentation.transform(simpleMethod, ()) else simpleMethod
    timeCapsule stop PARSER_FLATTEN_FORMULA

    timeCapsule stop PARSING
    (flattened, timeCapsule)
  }


  private def runLiftedForwardSearchReachabilityAnalysis(domain: Domain, problem: Plan, analysisMap: AnalysisMap): AnalysisMap = {
    val liftedRelaxedInitialState = problem.init.schema.effectsAsPredicateBool
    val liftedReachabilityAnalysis = LiftedForwardSearchReachabilityAnalysis(domain, liftedRelaxedInitialState.toSet)
    // add analysis to map
    analysisMap + (SymbolicLiftedReachability -> liftedReachabilityAnalysis)
  }

  private def runGroundedForwardSearchReachabilityAnalysis(domain: Domain, problem: Plan, analysisMap: AnalysisMap): AnalysisMap = {
    val groundedInitialState = problem.groundedInitialState
    val groundedReachabilityAnalysis = GroundedForwardSearchReachabilityAnalysis(domain, groundedInitialState.toSet)()
    // add analysis to map
    analysisMap + (SymbolicGroundedReachability -> groundedReachabilityAnalysis)
  }

  private def runNaiveGroundedTaskDecompositionGraph(domain: Domain, problem: Plan, analysisMap: AnalysisMap): AnalysisMap = {
    val groundedReachabilityAnalysis =
      if (analysisMap contains SymbolicGroundedReachability) analysisMap(SymbolicGroundedReachability)
      else EverythingIsReachable(domain, problem.groundedInitialState.toSet)

    val tdg = NaiveGroundedTaskDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true)

    analysisMap + (SymbolicGroundedTaskDecompositionGraph -> tdg)
  }


  def runPreprocessing(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): (((Domain, Plan), AnalysisMap), TimeCapsule) = {
    // start the timer
    timeCapsule startOrLetRun PREPROCESSING

    val emptyAnalysis = AnalysisMap(Map())

    // lifted reachability analysis
    timeCapsule start LIFTED_REACHABILITY_ANALYSIS
    val liftedResult = if (preprocessingConfiguration.liftedReachability) {
      val newAnalysisMap = runLiftedForwardSearchReachabilityAnalysis(domain, problem, emptyAnalysis)
      val disallowedTasks = domain.primitiveTasks filterNot newAnalysisMap(SymbolicLiftedReachability).reachableLiftedPrimitiveActions.contains
      (PruneHierarchy.transform(domain, problem, disallowedTasks.toSet), newAnalysisMap)
    } else ((domain, problem), emptyAnalysis)
    timeCapsule stop LIFTED_REACHABILITY_ANALYSIS

    // grounded reachability analysis
    timeCapsule start GROUNDED_REACHABILITY_ANALYSIS
    val groundedResult = if (preprocessingConfiguration.groundedReachability) {
      val newAnalysisMap = runGroundedForwardSearchReachabilityAnalysis(liftedResult._1._1, liftedResult._1._2, liftedResult._2)
      val disallowedTasks = liftedResult._1._1.primitiveTasks filterNot newAnalysisMap(SymbolicGroundedReachability).reachableLiftedPrimitiveActions.contains
      (PruneHierarchy.transform(liftedResult._1._1, liftedResult._1._2, disallowedTasks.toSet), newAnalysisMap)
    } else liftedResult
    timeCapsule stop GROUNDED_REACHABILITY_ANALYSIS

    // naive task decomposition graph
    timeCapsule start GROUNDED_TDG_ANALYSIS
    val tdgResult = if (preprocessingConfiguration.naiveGroundedTaskDecompositionGraph) {
      // get the reachability analysis, if there is none, just use the trivial one
      val newAnalysisMap = runNaiveGroundedTaskDecompositionGraph(groundedResult._1._1, groundedResult._1._2, groundedResult._2)

      (PruneDecompositionMethods.transform(groundedResult._1._1, groundedResult._1._2, newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedMethods), newAnalysisMap)
    } else groundedResult
    timeCapsule stop GROUNDED_TDG_ANALYSIS

    if (!preprocessingConfiguration.iterateReachabilityAnalysis || tdgResult._1._1.tasks.length == domain.tasks.length) {
      // finished reachability analysis now we have to ground
      if (preprocessingConfiguration.groundDomain) {
        val tdg = tdgResult._2(SymbolicGroundedTaskDecompositionGraph)

        timeCapsule start GROUNDING
        val result = (Grounding.transform(tdgResult._1, tdg), emptyAnalysis) // since we grounded the domain every analysis we performed so far becomes invalid
        timeCapsule stop GROUNDING
        timeCapsule stop PREPROCESSING
        (result, timeCapsule)
      } else (tdgResult, timeCapsule)
    } else runPreprocessing(tdgResult._1._1, tdgResult._1._2, timeCapsule)
  }
}

/**
  * all available search algorithms
  */
sealed trait ParserType

object XMLParserType extends ParserType

object HDDLParserType extends ParserType

case class ParsingConfiguration(
                                 parserType: ParserType,
                                 expandSortHierarchy: Boolean = true,
                                 closedWorldAssumption: Boolean = true,
                                 compileSHOPMethods: Boolean = true,
                                 toPlainFormulaRepresentation: Boolean = true
                               ) {}


case class PreprocessingConfiguration(
                                       liftedReachability: Boolean,
                                       groundedReachability: Boolean,
                                       planningGraph: Boolean,
                                       naiveGroundedTaskDecompositionGraph: Boolean,
                                       iterateReachabilityAnalysis: Boolean,
                                       groundDomain: Boolean
                                     ) {
  assert(!groundDomain || naiveGroundedTaskDecompositionGraph, "A grounded reachability analysis (grounded TDG) must be performed in order to ground.")
}

/**
  * all available search algorithms
  */
sealed trait SearchAlgorithmType

object BFSType extends SearchAlgorithmType

object DFSType extends SearchAlgorithmType

object AStarType extends SearchAlgorithmType

object GreedyType extends SearchAlgorithmType

object DijkstraType extends SearchAlgorithmType

/**
  * all available heuristics
  */
sealed trait SearchHeuristic {}

object NumberOfFlaws extends SearchHeuristic

object NumberOfPlanSteps extends SearchHeuristic

object WeightedFlaws extends SearchHeuristic

object TDGMinimumModification extends SearchHeuristic


case class SearchConfiguration(
                                nodeLimit: Option[Int],
                                efficientSearch: Boolean,
                                searchAlgorithm: SearchAlgorithmType,
                                heuristic: Option[SearchHeuristic],
                                printSearchInfo: Boolean
                              ) {}

case class PostprocessingConfiguration(resultsToProduce: Set[ResultType]) {}
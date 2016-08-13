package de.uniulm.ki.panda3.configuration

import java.io.InputStream
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientTDGFromGroundedSymbolic
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraphFromSymbolic
import de.uniulm.ki.panda3.efficient.heuristic._
import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.compiler.pruning.{PruneDecompositionMethods, PruneEffects, PruneHierarchy}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.{EverythingIsHiearchicallyReachable, EverythingIsHiearchicallyReachableBasedOnPrimitiveReachability,
NaiveGroundedTaskDecompositionGraph}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, DomainPropertyAnalyser}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.hpddl.HPDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.panda3.{efficient, symbolic}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PlanningConfiguration(printGeneralInformation: Boolean, printAdditionalData: Boolean,
                                 parsingConfiguration: ParsingConfiguration,
                                 preprocessingConfiguration: PreprocessingConfiguration,
                                 searchConfiguration: SearchConfiguration,
                                 postprocessingConfiguration: PostprocessingConfiguration) {

  assert(!(searchConfiguration.heuristic contains ADD) || preprocessingConfiguration.planningGraph)

  import Timings._

  /**
    * Runs the complete planner (including the parser) and returs the results
    */
  def runResultSearch(domain: InputStream, problem: InputStream, timeCapsule: TimeCapsule = new TimeCapsule()): ResultMap = runSearchHandle(domain, problem, None, timeCapsule)._6()

  /**
    * Runs the complete planner and returs the results
    */
  def runResultSearch(domain: Domain, problem: Plan, timeCapsule: TimeCapsule): ResultMap = runSearchHandle(domain, problem, None, timeCapsule)._6()

  /**
    * Runs the complete planner (including the parser) and returns a handle to the search and a waiting function for the result
    */
  def runSearchHandle(domain: InputStream, problem: InputStream, releaseSemaphoreEvery: Option[Int], timeCapsule: TimeCapsule = new TimeCapsule()):
  (Domain, SearchNode, Semaphore, AbortFunction, InformationCapsule, Unit => ResultMap) = {
    timeCapsule startOrLetRun TOTAL_TIME
    val parsedDomainAndProblem = runParsing(domain, problem, timeCapsule)
    runSearchHandle(parsedDomainAndProblem._1._1, parsedDomainAndProblem._1._2, releaseSemaphoreEvery, parsedDomainAndProblem._2)
  }

  /**
    * Runs the complete planner and returns a handle to the search and a waiting function for the result
    */
  def runSearchHandle(domain: Domain, problem: Plan, releaseSemaphoreEvery: Option[Int], timeCapsule: TimeCapsule):
  (Domain, SearchNode, Semaphore, AbortFunction, InformationCapsule, Unit => ResultMap) = {
    timeCapsule startOrLetRun TOTAL_TIME
    // run the preprocessing step
    val (domainAndPlanFullyParsed, _) = runParsingPostProcessing(domain, problem, timeCapsule)
    val ((domainAndPlan, preprocessedAnalysisMap), _) = runPreprocessing(domainAndPlanFullyParsed._1, domainAndPlanFullyParsed._2, timeCapsule)

    // !!!! ATTENTION we use side effects for the sake of simplicity
    var analysisMap = preprocessedAnalysisMap

    // if the map contains a tdg we will print the domain analysis statistic
    if (analysisMap contains SymbolicGroundedTaskDecompositionGraph) {
      val tdg = analysisMap(SymbolicGroundedTaskDecompositionGraph)
      val domainStructureAnalysis = DomainPropertyAnalyser(domainAndPlan._1, tdg)

      extra("Domain is acyclic: " + domainStructureAnalysis.isAcyclic + "\n")
      extra("Domain is mostly acyclic: " + domainStructureAnalysis.isMostlyAcyclic + "\n")
      extra("Domain is regular: " + domainStructureAnalysis.isRegular + "\n")
      extra("Domain is tail recursive: " + domainStructureAnalysis.isTailRecursive + "\n")

    }


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
        case DFSType | BFSType =>
          val searchObject = searchConfiguration.searchAlgorithm match {
            case DFSType => symbolic.search.DFS
            case BFSType => symbolic.search.BFS
          }

          searchObject.startSearch(domainAndPlan._1, domainAndPlan._2, searchConfiguration.nodeLimit, searchConfiguration.timeLimit,
                                   releaseSemaphoreEvery, searchConfiguration.printSearchInfo,
                                   postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                   informationCapsule, timeCapsule)
        case _                 => throw new UnsupportedOperationException("Any other symbolic search algorithm besides DFS is not supported.")
      }

      (domainAndPlan._1, searchTreeRoot, nodesProcessed, abortFunction, informationCapsule, { _ =>
        val actualResult: Option[Plan] = resultfunction(())
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, searchTreeRoot, actualResult, domainAndPlan)
      })
    } else {
      // EFFICIENT SEARCH
      timeCapsule start COMPUTE_EFFICIENT_REPRESENTATION
      val wrapper = Wrapping(domainAndPlan)
      val efficientInitialPlan = wrapper.unwrap(domainAndPlan._2)
      timeCapsule stop COMPUTE_EFFICIENT_REPRESENTATION

      // in some cases we need to re-do some steps of the preparation as we have to transfer them into the efficient representation
      timeCapsule start HEURISTICS_PREPARATION
      if ((searchConfiguration.heuristic contains TDGMinimumModification) || (searchConfiguration.heuristic contains TDGMinimumADD))
        analysisMap = createEfficientTDGFromSymbolic(wrapper, analysisMap)
      if ((searchConfiguration.heuristic contains ADD) || (searchConfiguration.heuristic contains ADDReusing) || (searchConfiguration.heuristic contains TDGMinimumADD)) {
        // do the whole preparation, i.e. planning graph
        val initialState = domainAndPlan._2.groundedInitialState filter { _.isPositive } toSet
        val symbolicPlanningGraph = GroundedPlanningGraph(domainAndPlan._1, initialState, GroundedPlanningGraphConfiguration())
        analysisMap = analysisMap +(EfficientGroundedPlanningGraph, EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraph, wrapper))
      }
      timeCapsule stop HEURISTICS_PREPARATION



      val (searchTreeRoot, nodesProcessed, resultfunction, abortFunction) = searchConfiguration.searchAlgorithm match {
        case algo => algo match {
          case BFSType                                        => efficient.search.BFS.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                                                                                  searchConfiguration.nodeLimit, searchConfiguration.timeLimit, releaseSemaphoreEvery,
                                                                                                  searchConfiguration.printSearchInfo,
                                                                                                  postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                                                                                  informationCapsule, timeCapsule)
          case DijkstraType | DFSType                         =>
            // just use the zero heuristic

            val heuristicSearch = efficient.search.HeuristicSearch(AlwaysZeroHeuristic, true, false, invertCosts = searchConfiguration.searchAlgorithm == DFSType)
            heuristicSearch.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                        searchConfiguration.nodeLimit, searchConfiguration.timeLimit, releaseSemaphoreEvery,
                                        searchConfiguration.printSearchInfo,
                                        postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                        informationCapsule, timeCapsule)
          case AStarActionsType | AStarDepthType | GreedyType =>
            // prepare the heuristic
            val heuristicInstance = searchConfiguration.heuristic match {
              case Some(heuristic) => heuristic match {
                case NumberOfFlaws          => EfficientNumberOfFlaws
                case NumberOfPlanSteps      => EfficientNumberOfPlanSteps
                case WeightedFlaws          => ???
                case TDGMinimumModification => MinimumModificationEffortHeuristic(analysisMap(EfficientGroundedTDG), wrapper.efficientDomain)
                case TDGMinimumADD          =>
                  // TODO experimental
                  val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
                  val initialState = domainAndPlan._2.groundedInitialState collect { case GroundLiteral(task, true, args) =>
                    (wrapper.unwrap(task), args map wrapper.unwrap toArray)
                  }
                  val reusing = if (heuristic == ADDReusing) true else false
                  // TODO check that we have compiled negative preconditions away
                  MinimumADDHeuristic(analysisMap(EfficientGroundedTDG), AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, reusing), wrapper
                    .efficientDomain)


                case ADD | ADDReusing =>
                  val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
                  val initialState = domainAndPlan._2.groundedInitialState collect { case GroundLiteral(task, true, args) =>
                    (wrapper.unwrap(task), args map wrapper.unwrap toArray)
                  }
                  val reusing = if (heuristic == ADDReusing) true else false
                  // TODO check that we have compiled negative preconditions away
                  AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, reusing)
              }
              case None            => throw new UnsupportedOperationException("In order to use a heuristic search procedure, a heuristic must be defined.")
            }

            val useDepthCosts = algo match {case AStarDepthType => true; case _ => false}
            val useActionCosts = algo match {case AStarActionsType => true; case _ => false}

            val heuristicSearch = efficient.search.HeuristicSearch(heuristicInstance, addNumberOfPlanSteps = useActionCosts, addDepth = useDepthCosts)
            heuristicSearch.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                        searchConfiguration.nodeLimit, searchConfiguration.timeLimit, releaseSemaphoreEvery,
                                        searchConfiguration.printSearchInfo,
                                        postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                        informationCapsule, timeCapsule)
          case _                                              => throw new UnsupportedOperationException("Any other efficient search algorithm besides BFS is not supported.")
        }
      }

      val wrappedSearchTreeRoot = wrapper.wrap(searchTreeRoot)
      (domainAndPlan._1, wrappedSearchTreeRoot, nodesProcessed, abortFunction, informationCapsule, { _ =>
        val actualResult: Option[Plan] = resultfunction(()) map { wrapper.wrap }
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, wrappedSearchTreeRoot, actualResult, domainAndPlan)
      })
    }
  }

  def runPostProcessing(timeCapsule: TimeCapsule, informationCapsule: InformationCapsule, rootNode: SearchNode, result: Option[Plan], domainAndPlan: (Domain, Plan)): ResultMap =
    ResultMap(postprocessingConfiguration.resultsToProduce map { resultType => (resultType, resultType match {
      case ProcessingTimings => timeCapsule
      case SearchStatus      => if (result.isDefined) SearchState.SOLUTION
      else if (searchConfiguration.nodeLimit.isEmpty || searchConfiguration.nodeLimit.get > informationCapsule(Information.NUMBER_OF_NODES))
        SearchState.UNSOLVABLE
      else SearchState.INSEARCH // TODO account for the case we ran out of time

      case SearchResult              => result
      case SearchStatistics          => informationCapsule
      case SearchSpace               => rootNode
      case SolutionInternalString    => result match {case Some(plan) => Some(plan.longInfo); case _ => None}
      case SolutionDotString         => result match {case Some(plan) => Some(plan.dotString); case _ => None}
      case PreprocessedDomainAndPlan => domainAndPlan
    })
    } toMap
             )


  /**
    * runs the parser
    */
  def runParsing(domain: InputStream, problem: InputStream, timeCapsule: TimeCapsule = new TimeCapsule()): ((Domain, Plan), TimeCapsule) = {
    // parse the problem and run the main function
    timeCapsule start PARSING

    info("Parsing domain ... ")
    timeCapsule start FILEPARSER
    val parsedDomainAndProblem = parsingConfiguration.parserType match {
      case XMLParserType  => XMLParser.asParser.parseDomainAndProblem(domain, problem)
      case HDDLParserType => HDDLParser.parseDomainAndProblem(domain, problem)
      case HPDDLParserType => HPDDLParser.parseDomainAndProblem(domain, problem)
    }
    timeCapsule stop FILEPARSER
    info("done\n")
    (parsedDomainAndProblem, timeCapsule)
  }

  def runParsingPostProcessing(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): ((Domain, Plan), TimeCapsule) = {
    info("Preparing internal domain representation ... ")

    timeCapsule startOrLetRun PARSING
    timeCapsule start PARSER_SORT_EXPANSION
    val sortsExpandedDomainAndProblem = if (parsingConfiguration.expandSortHierarchy) {
      val sortExpansion = domain.expandSortHierarchy()
      (domain.update(sortExpansion), problem.update(sortExpansion))
    } else (domain, problem)
    timeCapsule stop PARSER_SORT_EXPANSION

    timeCapsule start PARSER_CWA
    val cwaApplied = if (parsingConfiguration.closedWorldAssumption) ClosedWorldAssumption.transform(sortsExpandedDomainAndProblem, true) else sortsExpandedDomainAndProblem
    timeCapsule stop PARSER_CWA

    timeCapsule start PARSER_SHOP_METHODS
    val simpleMethod = if (parsingConfiguration.compileSHOPMethods) SHOPMethodCompiler.transform(cwaApplied, ()) else cwaApplied
    timeCapsule stop PARSER_SHOP_METHODS

    timeCapsule start PARSER_FLATTEN_FORMULA
    val flattened = if (parsingConfiguration.toPlainFormulaRepresentation) ToPlainFormulaRepresentation.transform(simpleMethod, ()) else simpleMethod
    timeCapsule stop PARSER_FLATTEN_FORMULA

    timeCapsule stop PARSING
    info("done.\n")
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

  private def runGroundedPlanningGraph(domain: Domain, problem: Plan, analysisMap: AnalysisMap): AnalysisMap = {
    val groundedInitialState = problem.groundedInitialState filter { _.isPositive }
    val groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis = GroundedPlanningGraph(domain, groundedInitialState.toSet, GroundedPlanningGraphConfiguration())
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

  private def createEfficientTDGFromSymbolic(wrapping: Wrapping, analysisMap: AnalysisMap): AnalysisMap = {
    val groundedTDG = analysisMap(SymbolicGroundedTaskDecompositionGraph)
    val tdg = EfficientTDGFromGroundedSymbolic(groundedTDG, wrapping)

    analysisMap + (EfficientGroundedTDG -> tdg)
  }


  private def runReachabilityAnalyses(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): (((Domain, Plan), AnalysisMap), TimeCapsule) = {
    val emptyAnalysis = AnalysisMap(Map())

    // lifted reachability analysis
    timeCapsule start LIFTED_REACHABILITY_ANALYSIS
    val liftedResult = if (preprocessingConfiguration.liftedReachability) {
      info("Lifted reachability analysis ... ")
      val newAnalysisMap = runLiftedForwardSearchReachabilityAnalysis(domain, problem, emptyAnalysis)
      val disallowedTasks = domain.primitiveTasks filterNot newAnalysisMap(SymbolicLiftedReachability).reachableLiftedPrimitiveActions.contains
      val hierarchyPruned = PruneHierarchy.transform(domain, problem: Plan, disallowedTasks.toSet)
      val pruned = PruneEffects.transform(hierarchyPruned, domain.primitiveTasks.toSet)
      info("done.\n")
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else ((domain, problem), emptyAnalysis)
    timeCapsule stop LIFTED_REACHABILITY_ANALYSIS

    // grounded reachability analysis
    timeCapsule start (if (preprocessingConfiguration.groundedReachability) GROUNDED_REACHABILITY_ANALYSIS else GROUNDED_PLANNINGGRAPH_ANALYSIS)
    val groundedResult = if (preprocessingConfiguration.groundedReachability || preprocessingConfiguration.planningGraph) {
      if (preprocessingConfiguration.groundedReachability) info("Grounded reachability analysis ... ") else info("Grounded planning graph analysis ... ")
      val newAnalysisMap =
        if (preprocessingConfiguration.groundedReachability) runGroundedForwardSearchReachabilityAnalysis(liftedResult._1._1, liftedResult._1._2, liftedResult._2)
        else runGroundedPlanningGraph(liftedResult._1._1, liftedResult._1._2, liftedResult._2)

      val disallowedTasks = liftedResult._1._1.primitiveTasks filterNot newAnalysisMap(SymbolicGroundedReachability).reachableLiftedPrimitiveActions.contains
      val pruned = PruneHierarchy.transform(liftedResult._1._1, liftedResult._1._2, disallowedTasks.toSet)
      info("done.\n")
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else liftedResult
    timeCapsule stop (if (preprocessingConfiguration.groundedReachability) GROUNDED_REACHABILITY_ANALYSIS else GROUNDED_PLANNINGGRAPH_ANALYSIS)

    // naive task decomposition graph
    timeCapsule start GROUNDED_TDG_ANALYSIS
    val tdgResult = if (preprocessingConfiguration.naiveGroundedTaskDecompositionGraph) {
      info("Naive TDG ... ")
      // get the reachability analysis, if there is none, just use the trivial one
      val newAnalysisMap = runNaiveGroundedTaskDecompositionGraph(groundedResult._1._1, groundedResult._1._2, groundedResult._2)
      val pruned = PruneDecompositionMethods.transform(groundedResult._1._1, groundedResult._1._2, newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedMethods)
      info("done.\n")
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else groundedResult
    timeCapsule stop GROUNDED_TDG_ANALYSIS

    if (!preprocessingConfiguration.iterateReachabilityAnalysis || tdgResult._1._1.tasks.length == domain.tasks.length) (tdgResult, timeCapsule)
    else runReachabilityAnalyses(tdgResult._1._1, tdgResult._1._2, timeCapsule)
  }


  def runPreprocessing(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): (((Domain, Plan), AnalysisMap), TimeCapsule) = {
    // start the timer
    timeCapsule start PREPROCESSING
    extra("Initial domain\n" + domain.statisticsString + "\n")

    // removing negative preconditions
    timeCapsule start COMPILE_NEGATIVE_PRECONFITIONS
    val compilationResult = if (preprocessingConfiguration.compileNegativePreconditions) {
      info("Compiling negative preconditions ... ")
      val compiled = RemoveNegativePreconditions.transform(domain, problem, ())
      info("done.\n")
      extra(compiled._1.statisticsString + "\n")
      compiled
    } else (domain, problem)
    timeCapsule stop COMPILE_NEGATIVE_PRECONFITIONS

    // initial run of the reachability analysis on the domain, until it has converged
    val ((domainAndPlan, analysisMap), _) = runReachabilityAnalyses(compilationResult._1, compilationResult._2, timeCapsule)

    // finished reachability analysis now we have to ground
    if (preprocessingConfiguration.groundDomain) {
      val tdg = if (analysisMap.contains(SymbolicGroundedTaskDecompositionGraph)) analysisMap(SymbolicGroundedTaskDecompositionGraph)
      else if (!analysisMap.contains(SymbolicGroundedReachability)) EverythingIsHiearchicallyReachable(domainAndPlan._1, domainAndPlan._2)
      else {
        val groundedReachability = analysisMap(SymbolicGroundedReachability)
        EverythingIsHiearchicallyReachableBasedOnPrimitiveReachability(domainAndPlan._1, domainAndPlan._2, groundedReachability)
      }

      info("Grounding ... ")
      timeCapsule start GROUNDING
      val result = Grounding.transform(domainAndPlan, tdg) // since we grounded the domain every analysis we performed so far becomes invalid

      timeCapsule stop GROUNDING
      info("done.\n")
      extra(result._1.statisticsString + "\n")

      val ((groundedDomainAndPlan, groundedAnalysisMap), _) = runReachabilityAnalyses(result._1, result._2, timeCapsule)

      timeCapsule stop PREPROCESSING
      ((groundedDomainAndPlan, groundedAnalysisMap), timeCapsule)
    } else {
      timeCapsule stop PREPROCESSING
      ((domainAndPlan, analysisMap), timeCapsule)
    }
  }

  private def info(s: String): Unit = if (printGeneralInformation) print(s)

  private def extra(s: String): Unit = if (printAdditionalData) print(s)
}

/**
  * all available search algorithms
  */
sealed trait ParserType

object XMLParserType extends ParserType

object HDDLParserType extends ParserType
object HPDDLParserType extends ParserType

case class ParsingConfiguration(
                                 parserType: ParserType,
                                 expandSortHierarchy: Boolean = true,
                                 closedWorldAssumption: Boolean = true,
                                 compileSHOPMethods: Boolean = true,
                                 toPlainFormulaRepresentation: Boolean = true
                               ) {}


case class PreprocessingConfiguration(
                                       compileNegativePreconditions: Boolean,
                                       liftedReachability: Boolean,
                                       groundedReachability: Boolean,
                                       planningGraph: Boolean,
                                       naiveGroundedTaskDecompositionGraph: Boolean,
                                       iterateReachabilityAnalysis: Boolean,
                                       groundDomain: Boolean
                                     ) {
  //assert(!groundDomain || naiveGroundedTaskDecompositionGraph, "A grounded reachability analysis (grounded TDG) must be performed in order to ground.")
  assert(!(planningGraph && groundedReachability), "Don't use both the naive grounded reachability and the planning graph.")
}

/**
  * all available search algorithms
  */
sealed trait SearchAlgorithmType

object BFSType extends SearchAlgorithmType

object DFSType extends SearchAlgorithmType

object AStarActionsType extends SearchAlgorithmType

object AStarDepthType extends SearchAlgorithmType

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

object TDGMinimumADD extends SearchHeuristic

object ADD extends SearchHeuristic

object ADDReusing extends SearchHeuristic


case class SearchConfiguration(
                                nodeLimit: Option[Int],
                                timeLimit: Option[Int],
                                efficientSearch: Boolean,
                                searchAlgorithm: SearchAlgorithmType,
                                heuristic: Option[SearchHeuristic],
                                printSearchInfo: Boolean
                              ) {}

case class PostprocessingConfiguration(resultsToProduce: Set[ResultType]) {}
package de.uniulm.ki.panda3.configuration

import java.io.InputStream
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientTDGFromGroundedSymbolic
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraphFromSymbolic
import de.uniulm.ki.panda3.efficient.heuristic._
import de.uniulm.ki.panda3.efficient.domain.EfficientExtractedMethodPlan
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.{EfficientGroundedTaskDecompositionGraph, EfficientTDGFromGroundedSymbolic}
import de.uniulm.ki.panda3.efficient.heuristic.{MinimumModificationEffortHeuristic, EfficientNumberOfPlanSteps, EfficientNumberOfFlaws, AlwaysZeroHeuristic}
import de.uniulm.ki.panda3.efficient.search.EfficientSearchNode
import de.uniulm.ki.panda3.efficient.search.flawSelector.{EfficientFlawSelector, AbstractFirstWithDeferred, LeastCostFlawRepair}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.parser.FileTypeDetector
import de.uniulm.ki.panda3.symbolic.parser.oldpddl.OldPDDLParser
import de.uniulm.ki.panda3.{efficient, symbolic}
import de.uniulm.ki.panda3.symbolic.compiler.pruning.{PruneEffects, PruneDecompositionMethods, PruneHierarchy}
import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.compiler.pruning.{PruneDecompositionMethods, PruneEffects, PruneHierarchy}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, DomainPropertyAnalyser}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.hpddl.HPDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter
import de.uniulm.ki.panda3.{efficient, symbolic}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}
import de.uniulm.ki.util._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PlanningConfiguration(printGeneralInformation: Boolean, printAdditionalData: Boolean,
                                 parsingConfiguration: ParsingConfiguration = ParsingConfiguration(),
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
    * Runs the complete planner and returns the results
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
      extra("Domain is totally ordered: " + domainStructureAnalysis.isTotallyOrdered + "\n")

    }


    // some heuristics need additional preprocessing, e.g. to build datastructures they need
    timeCapsule start HEURISTICS_PREPARATION
    // TDG based heuristics need the TDG
    if (searchConfiguration.heuristic contains TDGMinimumModification) if (!(analysisMap contains SymbolicGroundedTaskDecompositionGraph)) {
      timeCapsule start GROUNDED_TDG_ANALYSIS
      analysisMap = runGroundedTaskDecompositionGraph(domainAndPlan._1, domainAndPlan._2, analysisMap, preprocessingConfiguration.groundedTaskDecompositionGraph.get)
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
        val actualResult: Seq[Plan] = resultfunction(())
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, searchTreeRoot, actualResult, domainAndPlan, analysisMap)
      })
    } else {
      // EFFICIENT SEARCH
      timeCapsule start COMPUTE_EFFICIENT_REPRESENTATION
      val wrapper = Wrapping(domainAndPlan)
      val efficientInitialPlan = wrapper.unwrap(domainAndPlan._2)
      timeCapsule stop COMPUTE_EFFICIENT_REPRESENTATION

      // in some cases we need to re-do some steps of the preparation as we have to transfer them into the efficient representation
      timeCapsule start HEURISTICS_PREPARATION
      // compute the efficient TDG if needed during search
      if ((searchConfiguration.heuristic contains TDGMinimumModification) || (searchConfiguration.heuristic contains TDGMinimumADD) ||
        (searchConfiguration.heuristic contains TDGMinimumAction))
        analysisMap = createEfficientTDGFromSymbolic(wrapper, analysisMap)

      if ((searchConfiguration.heuristic contains ADD) || (searchConfiguration.heuristic contains ADDReusing) || (searchConfiguration.heuristic contains TDGMinimumADD) ||
        (searchConfiguration.heuristic contains Relax)) {
        // do the whole preparation, i.e. planning graph
        val initialState = domainAndPlan._2.groundedInitialState filter { _.isPositive } toSet
        val symbolicPlanningGraph = GroundedPlanningGraph(domainAndPlan._1, initialState, GroundedPlanningGraphConfiguration())
        analysisMap = analysisMap +(EfficientGroundedPlanningGraph, EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraph, wrapper))
      }
      timeCapsule stop HEURISTICS_PREPARATION


      val flawSelector = searchConfiguration.flawSelector match {
        case LCFR => LeastCostFlawRepair
      }

      val (searchTreeRoot, nodesProcessed, resultfunction, abortFunction) = searchConfiguration.searchAlgorithm match {
        case algo => algo match {
          case BFSType                                        => efficient.search.BFS.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                                                                                  searchConfiguration.nodeLimit, searchConfiguration.timeLimit, releaseSemaphoreEvery,
                                                                                                  searchConfiguration.printSearchInfo,
                                                                                                  postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                                                                                  informationCapsule, timeCapsule)
          case DijkstraType | DFSType                         =>
            // just use the zero heuristic
            val heuristicSearch = efficient.search.HeuristicSearch(AlwaysZeroHeuristic, flawSelector, addNumberOfPlanSteps = true, addDepth = false,
                                                                   continueOnSolution = searchConfiguration.continueOnSolution,
                                                                   invertCosts = searchConfiguration.searchAlgorithm == DFSType)

            heuristicSearch.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                        searchConfiguration.nodeLimit, searchConfiguration.timeLimit, releaseSemaphoreEvery,
                                        searchConfiguration.printSearchInfo,
                                        postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                        informationCapsule, timeCapsule)
          case AStarActionsType | AStarDepthType | GreedyType =>
            // prepare the heuristic
            val heuristicInstance = searchConfiguration.heuristic match {
              case Some(heuristic) => heuristic match {
                case NumberOfFlaws                => EfficientNumberOfFlaws
                case NumberOfPlanSteps            => EfficientNumberOfPlanSteps
                case WeightedFlaws                => ???
                case TDGMinimumModification       => MinimumModificationEffortHeuristic(analysisMap(EfficientGroundedTDG), wrapper.efficientDomain)
                case LiftedTDGMinimumModification => TSTGHeuristic(wrapper.efficientDomain)
                case TDGMinimumAction             => MinimumActionCount(analysisMap(EfficientGroundedTDG), wrapper.efficientDomain)
                case TDGMinimumADD                =>
                  // TODO experimental
                  val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
                  val initialState = domainAndPlan._2.groundedInitialState collect { case GroundLiteral(task, true, args) =>
                    (wrapper.unwrap(task), args map wrapper.unwrap toArray)
                  }
                  val reusing = if (heuristic == ADDReusing) true else false
                  // TODO check that we have compiled negative preconditions away
                  MinimumADDHeuristic(analysisMap(EfficientGroundedTDG), AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, reusing), wrapper
                    .efficientDomain)


                case ADD | ADDReusing | Relax =>
                  val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
                  val initialState = domainAndPlan._2.groundedInitialState collect { case GroundLiteral(task, true, args) =>
                    (wrapper.unwrap(task), args map wrapper.unwrap toArray)
                  }

                  searchConfiguration.heuristic.get match {
                    case ADD | ADDReusing =>
                      val reusing = if (heuristic == ADDReusing) true else false
                      // TODO check that we have compiled negative preconditions away
                      AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, reusing)
                    case Relax            =>
                      RelaxHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray)
                  }
              }
              case None            => throw new UnsupportedOperationException("In order to use a heuristic search procedure, a heuristic must be defined.")
            }

            val useDepthCosts = algo match {case AStarDepthType => true; case _ => false}
            val useActionCosts = algo match {case AStarActionsType => true; case _ => false}

            val heuristicSearch = efficient.search.HeuristicSearch(heuristicInstance, flawSelector, addNumberOfPlanSteps = useActionCosts, addDepth = useDepthCosts,
                                                                   continueOnSolution = searchConfiguration.continueOnSolution)
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
        val actualResult: Seq[Plan] = resultfunction(()) map { wrapper.wrap }
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, wrappedSearchTreeRoot, actualResult, domainAndPlan, analysisMap)
      })
    }
  }

  def runPostProcessing(timeCapsule: TimeCapsule, informationCapsule: InformationCapsule, rootNode: SearchNode, result: Seq[Plan], domainAndPlan: (Domain, Plan),
                        analysisMap: AnalysisMap): ResultMap =
    ResultMap(postprocessingConfiguration.resultsToProduce map { resultType => (resultType, resultType match {
      case ProcessingTimings              => timeCapsule
      case SearchStatus                   => if (result.nonEmpty) SearchState.SOLUTION
      else if (timeCapsule.integralDataMap()(Timings.SEARCH) >= 1000 * searchConfiguration.timeLimit.getOrElse(Integer.MAX_VALUE / 1000))
        SearchState.TIMEOUT
      else if (searchConfiguration.nodeLimit.isEmpty || searchConfiguration.nodeLimit.get > informationCapsule(Information.NUMBER_OF_NODES))
        SearchState.UNSOLVABLE
      else SearchState.INSEARCH
      case SearchResult                   => result.headOption
      case AllFoundPlans                  => result
      case SearchStatistics               => informationCapsule
      case SearchSpace                    => rootNode
      case SolutionInternalString         => if (result.nonEmpty) Some(result.head.longInfo) else None
      case SolutionDotString              => if (result.nonEmpty) Some(result.head.dotString) else None
      case FinalTaskDecompositionGraph    => analysisMap(SymbolicGroundedTaskDecompositionGraph)
      case PreprocessedDomainAndPlan      => domainAndPlan
      case AllFoundSolutionPathsWithHStar =>
        // we have to find all solutions paths, so first compute the solution state of each node to only traverse the paths to the actual solutions
        rootNode.recomputeSearchState()

        def getAllSolutions(node: SearchNode): (Int, Seq[Seq[(SearchNode, Int)]]) = {
          assert(node.searchState == SearchState.SOLUTION)
          if (node.children.isEmpty) (node.plan.planSteps.length, ((node, 0) :: Nil) :: Nil)
          else {
            val solvableChildren = node.children filter { _._1.searchState == SearchState.SOLUTION } map { _._1 }
            val children = solvableChildren map getAllSolutions
            val minimalLength = children map { _._1 } min

            // semantic empty line
            (minimalLength, children flatMap { _._2 } map { case p => p.+:(node, minimalLength - node.plan.planSteps.length) })
          }
        }

        if (rootNode.searchState == SearchState.SOLUTION) getAllSolutions(rootNode)._2 else Nil
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
      case XMLParserType   => XMLParser.asParser.parseDomainAndProblem(domain, problem)
      case HDDLParserType  => HDDLParser.parseDomainAndProblem(domain, problem)
      case HPDDLParserType => HPDDLParser.parseDomainAndProblem(domain, problem)
      case OldPDDLType => OldPDDLParser.parseDomainAndProblem(domain,problem)
      case AutoDetectParserType => FileTypeDetector(info).parseDomainAndProblem(domain, problem)
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

    timeCapsule start PARSER_ELIMINATE_EQUALITY
    val identity = if (parsingConfiguration.eliminateEquality) RemoveIdenticalVariables.transform(simpleMethod, ()) else simpleMethod
    timeCapsule stop PARSER_ELIMINATE_EQUALITY

    timeCapsule start PARSER_FLATTEN_FORMULA
    val flattened = if (parsingConfiguration.toPlainFormulaRepresentation) ToPlainFormulaRepresentation.transform(identity, ()) else simpleMethod
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

  private def runGroundedTaskDecompositionGraph(domain: Domain, problem: Plan, analysisMap: AnalysisMap, tdgType: TDGGeneration): AnalysisMap = {
    val groundedReachabilityAnalysis =
      if (analysisMap contains SymbolicGroundedReachability) analysisMap(SymbolicGroundedReachability)
      else EverythingIsReachable(domain, problem.groundedInitialState.toSet)

    val tdg = tdgType match {
      case NaiveTDG   => NaiveGroundedTaskDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true)
      case TopDownTDG => TopDownTaskDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true)
      case TwoWayTDG => TwoStepDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true)
    }

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
      val reachable = newAnalysisMap(SymbolicLiftedReachability).reachableLiftedPrimitiveActions.toSet
      val disallowedTasks = domain.primitiveTasks filterNot reachable.contains
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

      val reachable = newAnalysisMap(SymbolicGroundedReachability).reachableLiftedPrimitiveActions.toSet
      val disallowedTasks = liftedResult._1._1.primitiveTasks filterNot reachable.contains
      val pruned = PruneHierarchy.transform(liftedResult._1._1, liftedResult._1._2, disallowedTasks.toSet)
      info("done.\n")
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else liftedResult
    timeCapsule stop (if (preprocessingConfiguration.groundedReachability) GROUNDED_REACHABILITY_ANALYSIS else GROUNDED_PLANNINGGRAPH_ANALYSIS)

    // naive task decomposition graph
    timeCapsule start GROUNDED_TDG_ANALYSIS
    val tdgResult = if (preprocessingConfiguration.groundedTaskDecompositionGraph.isDefined) {
      if (preprocessingConfiguration.groundedTaskDecompositionGraph contains NaiveTDG) info("Naive TDG ... ") else info("Top Down TDG ...")
      // get the reachability analysis, if there is none, just use the trivial one
      val newAnalysisMap = runGroundedTaskDecompositionGraph(groundedResult._1._1, groundedResult._1._2, groundedResult._2, preprocessingConfiguration.groundedTaskDecompositionGraph.get)
      val methodsPruned = PruneDecompositionMethods.transform(groundedResult._1._1, groundedResult._1._2, newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedMethods)
      val removedTasks = groundedResult._1._1.tasks.toSet diff newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedActions.toSet
      val tasksPruned = PruneHierarchy.transform(methodsPruned._1, methodsPruned._2, removedTasks)

      //val pruned = PruneDecompositionMethods.transform(groundedResult._1._1, groundedResult._1._2, newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedMethods)
      info("done.\n")
      extra(tasksPruned._1.statisticsString + "\n")
      (tasksPruned, newAnalysisMap)
    } else groundedResult
    timeCapsule stop GROUNDED_TDG_ANALYSIS

    if (!preprocessingConfiguration.iterateReachabilityAnalysis || tdgResult._1._1.tasks.length == domain.tasks.length) (tdgResult, timeCapsule)
    else runReachabilityAnalyses(tdgResult._1._1, tdgResult._1._2, timeCapsule)
  }


  def runPreprocessing(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): (((Domain, Plan), AnalysisMap), TimeCapsule) = {
    // start the timer
    timeCapsule start PREPROCESSING
    extra("Initial domain\n" + domain.statisticsString + "\n")

    case class CompilerConfiguration[T](domainTransformer: DomainTransformer[T], information: T, name: String, timingName: String) {
      def run(domain: Domain, plan: Plan) = domainTransformer.transform(domain, plan, information)
    }

    val compilerToBeApplied: Seq[CompilerConfiguration[_]] =
      (if (preprocessingConfiguration.compileNegativePreconditions)
        CompilerConfiguration(RemoveNegativePreconditions, (), "negative preconditions", COMPILE_NEGATIVE_PRECONFITIONS) :: Nil
      else Nil) ::
        (if (preprocessingConfiguration.compileOrderInMethods.isDefined)
          CompilerConfiguration(TotallyOrderAllMethods, preprocessingConfiguration.compileOrderInMethods.get, "order in methods", COMPILE_ORDER_IN_METHODS) :: Nil
        else Nil) ::
        Nil flatten

    val (compiledDomain, compiledProblem) = compilerToBeApplied.foldLeft((domain, problem))(
      { case ((dom, prob), cc@CompilerConfiguration(compiler, option, message, timingString)) =>
        timeCapsule start timingString
        info("Compiling " + message + " ... ")
        val compiled = cc.run(dom, prob)
        info("done.\n")
        extra(compiled._1.statisticsString + "\n")
        timeCapsule stop timingString
        compiled
      }
                                                                                           )

    // initial run of the reachability analysis on the domain, until it has converged
    val ((domainAndPlan, analysisMap), _) = runReachabilityAnalyses(compiledDomain, compiledProblem, timeCapsule)

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

      // compile unit methods
      timeCapsule start COMPILE_UNIT_METHODS
      val unitMethodsCompiled = if (preprocessingConfiguration.compileUnitMethods) {
        info("Compiling unit methods ... ")
        val compiled = RemoveUnitMethods.transform(result._1, result._2, ())
        info("done.\n")
        extra(compiled._1.statisticsString + "\n")
        compiled
      } else (result._1, result._2)
      timeCapsule stop COMPILE_UNIT_METHODS



      val ((groundedDomainAndPlan, groundedAnalysisMap), _) = runReachabilityAnalyses(unitMethodsCompiled._1, unitMethodsCompiled._2, timeCapsule)

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

object OldPDDLType extends ParserType

object AutoDetectParserType extends ParserType

case class ParsingConfiguration(
                                 parserType: ParserType = AutoDetectParserType,
                                 expandSortHierarchy: Boolean = true,
                                 closedWorldAssumption: Boolean = true,
                                 compileSHOPMethods: Boolean = true,
                                 eliminateEquality: Boolean = true,
                                 toPlainFormulaRepresentation: Boolean = true
                               ) {}

sealed trait TDGGeneration

object NaiveTDG extends TDGGeneration

object TopDownTDG extends TDGGeneration

object TwoWayTDG extends TDGGeneration

case class PreprocessingConfiguration(
                                       compileNegativePreconditions: Boolean,
                                       compileUnitMethods: Boolean,
                                       compileOrderInMethods: Option[TotallyOrderingOption],
                                       liftedReachability: Boolean,
                                       groundedReachability: Boolean,
                                       planningGraph: Boolean,
                                       groundedTaskDecompositionGraph: Option[TDGGeneration],
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

object LiftedTDGMinimumModification extends SearchHeuristic

object TDGMinimumADD extends SearchHeuristic

object TDGMinimumAction extends SearchHeuristic

object ADD extends SearchHeuristic

object ADDReusing extends SearchHeuristic

object Relax extends SearchHeuristic

/**
  * all available flaw selectors
  */
sealed trait SearchFlawSelector {}

object LCFR extends SearchFlawSelector

case class SearchConfiguration(
                                nodeLimit: Option[Int],
                                timeLimit: Option[Int],
                                searchAlgorithm: SearchAlgorithmType,
                                heuristic: Option[SearchHeuristic],
                                flawSelector: SearchFlawSelector,
                                efficientSearch: Boolean = true,
                                continueOnSolution: Boolean = false,
                                printSearchInfo: Boolean = true
                              ) {}

case class PostprocessingConfiguration(resultsToProduce: Set[ResultType]) {
  if (resultsToProduce contains AllFoundSolutionPathsWithHStar) assert(resultsToProduce contains SearchSpace,
                                                                       "If we have to produce paths to the solutions, we have to keep the search space")

}
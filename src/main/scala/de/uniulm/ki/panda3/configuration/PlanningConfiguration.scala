package de.uniulm.ki.panda3.configuration

import java.io.InputStream
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraphFromSymbolic
import de.uniulm.ki.panda3.efficient.heuristic._

import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientTDGFromGroundedSymbolic
import de.uniulm.ki.panda3.efficient.heuristic.filter.RecomputeHTN
import de.uniulm.ki.panda3.efficient.heuristic.{AlwaysZeroHeuristic, EfficientNumberOfFlaws, EfficientNumberOfPlanSteps}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.search.flawSelector.{SequentialEfficientFlawSelector, RandomFlawSelector, UMCPFlawSelection, LeastCostFlawRepair}
import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.RCG
import de.uniulm.ki.panda3.symbolic.parser.FileTypeDetector
import de.uniulm.ki.panda3.symbolic.parser.oldpddl.OldPDDLParser
import de.uniulm.ki.panda3.symbolic.sat.verify.{Solvertype, SATRunner}
import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.compiler.pruning.{PruneDecompositionMethods, PruneEffects, PruneHierarchy}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, DomainPropertyAnalyser, GroundedDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.hpddl.HPDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.panda3.{efficient, symbolic}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}

import scala.collection.JavaConversions
import scala.util.Random

import scala.collection.JavaConversions

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PlanningConfiguration(printGeneralInformation: Boolean, printAdditionalData: Boolean,
                                 parsingConfiguration: ParsingConfiguration = ParsingConfiguration(),
                                 preprocessingConfiguration: PreprocessingConfiguration,
                                 searchConfiguration: SearchConfiguration,
                                 postprocessingConfiguration: PostprocessingConfiguration) {

  searchConfiguration match {

    case search: PlanBasedSearch => assert(!(search.heuristic contains ADD) || preprocessingConfiguration.groundedReachability.contains(PlanningGraph) ||
                                             preprocessingConfiguration.groundedReachability.contains(PlanningGraphWithMutexes))
    case _                       => ()
  }


  import Timings._

  /**
    * Runs the complete planner (including the parser) and returns the results
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

    // create the information container
    val informationCapsule = new InformationCapsule

    // !!!! ATTENTION we use side effects for the sake of simplicity
    var analysisMap = preprocessedAnalysisMap

    // if the map contains a tdg we will print the domain analysis statistic
    if (analysisMap contains SymbolicGroundedTaskDecompositionGraph) {
      val tdg = analysisMap(SymbolicGroundedTaskDecompositionGraph)
      val domainStructureAnalysis = DomainPropertyAnalyser(domainAndPlan._1, tdg)

      informationCapsule.set(Information.ACYCLIC, if (domainStructureAnalysis.isAcyclic) "true" else "false")
      informationCapsule.set(Information.MOSTLY_ACYCLIC, if (domainStructureAnalysis.isMostlyAcyclic) "true" else "false")
      informationCapsule.set(Information.REGULAR, if (domainStructureAnalysis.isRegular) "true" else "false")
      informationCapsule.set(Information.TAIL_RECURSIVE, if (domainStructureAnalysis.isTailRecursive) "true" else "false")
      informationCapsule.set(Information.TOTALLY_ORDERED, if (domainStructureAnalysis.isTotallyOrdered) "true" else "false")

      extra("Domain is acyclic: " + domainStructureAnalysis.isAcyclic + "\n")
      extra("Domain is mostly acyclic: " + domainStructureAnalysis.isMostlyAcyclic + "\n")
      extra("Domain is regular: " + domainStructureAnalysis.isRegular + "\n")
      extra("Domain is tail recursive: " + domainStructureAnalysis.isTailRecursive + "\n")
      extra("Domain is totally ordered: " + domainStructureAnalysis.isTotallyOrdered + "\n")
    }

    //informationCapsule.set(Information.MINIMUM_DECOMPOSITION_HEIGHT, domainAndPlan._1.minimumDecompositionHeightToPrimitiveForPlan(domainAndPlan._2))


    // write domain statistics into the information capsule
    informationCapsule.set(Information.NUMBER_OF_CONSTANTS, domain.constants.length)
    informationCapsule.set(Information.NUMBER_OF_PREDICATES, domain.predicates.length)
    informationCapsule.set(Information.NUMBER_OF_ACTIONS, domain.tasks.length)
    informationCapsule.set(Information.NUMBER_OF_ABSTRACT_ACTIONS, domain.abstractTasks.length)
    informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_ACTIONS, domain.primitiveTasks.length)
    informationCapsule.set(Information.NUMBER_OF_METHODS, domain.decompositionMethods.length)


    searchConfiguration match {
      case search: PlanBasedSearch        =>
        // some heuristics need additional preprocessing, e.g. to build datastructures they need
        timeCapsule start HEURISTICS_PREPARATION
        // TDG based heuristics need the TDG
        if (search.heuristic.exists { case x: TDGBasedHeuristic => true; case _ => false }) if (!(analysisMap contains SymbolicGroundedTaskDecompositionGraph)) {
          timeCapsule start GROUNDED_TDG_ANALYSIS
          analysisMap = runGroundedTaskDecompositionGraph(domainAndPlan._1, domainAndPlan._2, analysisMap, preprocessingConfiguration.groundedTaskDecompositionGraph.get)
          timeCapsule stop GROUNDED_TDG_ANALYSIS
        }
        timeCapsule stop HEURISTICS_PREPARATION

        // now we have to decide which representation to use for the search
        if (!search.efficientSearch) {
          val (searchTreeRoot, nodesProcessed, resultfunction, abortFunction) = search.searchAlgorithm match {
            case DFSType | BFSType =>
              val searchObject = search.searchAlgorithm match {
                case DFSType => symbolic.search.DFS
                case BFSType => symbolic.search.BFS
              }

              searchObject.startSearch(domainAndPlan._1, domainAndPlan._2, search.nodeLimit, search.timeLimit,
                                       releaseSemaphoreEvery, search.printSearchInfo,
                                       postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                       informationCapsule, timeCapsule)
            case _                 => throw new UnsupportedOperationException("Any other symbolic search algorithm besides DFS or BFS is not supported.")
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
          if (search.heuristic.exists { case x: TDGBasedHeuristic => true; case _ => false })
            analysisMap = createEfficientTDGFromSymbolic(wrapper, analysisMap)

          val efficientPGNeeded = search.heuristic exists {
            case ADD | ADDReusing | Relax | TDGMinimumADD(_) | LiftedTDGMinimumADD(_, _) => true
            case f: TDGBasedHeuristic                                                    =>
              f.innerHeuristic.exists({ case ADD | ADDReusing | Relax | TDGMinimumADD(_) | LiftedTDGMinimumADD(_, _) => true; case _ => false })

            case _ => false
          }

          if (efficientPGNeeded) {
            // do the whole preparation, i.e. planning graph
            val initialState = domainAndPlan._2.groundedInitialState filter {
              _.isPositive
            } toSet
            val symbolicPlanningGraph = GroundedPlanningGraph(domainAndPlan._1, initialState, GroundedPlanningGraphConfiguration())
            analysisMap = analysisMap +(EfficientGroundedPlanningGraph, EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraph, wrapper))
          }
          timeCapsule stop HEURISTICS_PREPARATION


          val flawSelector = search.flawSelector match {
            case LCFR                  => LeastCostFlawRepair
            case RandomFlaw(seed)      => RandomFlawSelector(new Random(seed))
            case UMCPFlaw              => UMCPFlawSelection
            case s: SequentialSelector =>
              val subSelectorArray = s.sequence map {
                case LCFR             => LeastCostFlawRepair
                case RandomFlaw(seed) => RandomFlawSelector(new Random(seed))
              } toArray

              SequentialEfficientFlawSelector(subSelectorArray)
          }

          val (searchTreeRoot, nodesProcessed, resultfunction, abortFunction) = search.searchAlgorithm match {
            case algo => algo match {
              case BFSType                                              => efficient.search.BFS.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                                                                                            search.nodeLimit, search.timeLimit, releaseSemaphoreEvery,
                                                                                                            search.printSearchInfo,
                                                                                                            postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                                                                                            informationCapsule, timeCapsule)
              case DijkstraType | DFSType                               =>
                // just use the zero heuristic
                val heuristicSearch = efficient.search.HeuristicSearch(Array[EfficientHeuristic[Unit]](AlwaysZeroHeuristic), 0, Array(), flawSelector,
                                                                       addNumberOfPlanSteps = true, addDepth = false,
                                                                       continueOnSolution = search.continueOnSolution,
                                                                       invertCosts = search.searchAlgorithm == DFSType)

                heuristicSearch.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                            search.nodeLimit, search.timeLimit, releaseSemaphoreEvery,
                                            search.printSearchInfo,
                                            postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                            informationCapsule, timeCapsule)
              case AStarActionsType(_) | AStarDepthType(_) | GreedyType =>
                // prepare the heuristic
                val heuristicInstance: Array[EfficientHeuristic[AnyVal]] =
                  search.heuristic map { h => constructEfficientHeuristic(h, wrapper, analysisMap, domainAndPlan).asInstanceOf[EfficientHeuristic[AnyVal]] } toArray
                val weight = algo match {
                  case AStarActionsType(w) => w
                  case AStarDepthType(w)   => w
                  case GreedyType          => 1
                }

                // prepare filters
                val filters = search.pruningTechniques map {
                  case TreeFFFilter                      => filter.TreeFF(wrapper.efficientDomain)
                  case RecomputeHierarchicalReachability => RecomputeHTN
                } toArray

                val useDepthCosts = algo match {
                  case AStarDepthType(_) => true
                  case _                 => false
                }
                val useActionCosts = algo match {
                  case AStarActionsType(_) => true
                  case _                   => false
                }

                val heuristicSearch = efficient.search.HeuristicSearch[AnyVal](heuristicInstance, weight, filters, flawSelector, addNumberOfPlanSteps = useActionCosts,
                                                                               addDepth = useDepthCosts, continueOnSolution = search.continueOnSolution)
                heuristicSearch.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                            search.nodeLimit, search.timeLimit, releaseSemaphoreEvery,
                                            search.printSearchInfo,
                                            postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                            informationCapsule, timeCapsule)
              case _                                                    => throw new UnsupportedOperationException("Any other efficient search algorithm besides BFS is not supported.")
            }
          }

          val wrappedSearchTreeRoot = wrapper.wrap(searchTreeRoot)
          (domainAndPlan._1, wrappedSearchTreeRoot, nodesProcessed, abortFunction, informationCapsule, { _ =>
            val actualResult: Seq[Plan] = resultfunction(()) map { wrapper.wrap }
            timeCapsule stop TOTAL_TIME
            runPostProcessing(timeCapsule, informationCapsule, wrappedSearchTreeRoot, actualResult, domainAndPlan, analysisMap)
          })
        }
      case progression: ProgressionSearch =>

        val progressionInstance = new htnPlanningInstance()
        val groundTasks = domainAndPlan._1.primitiveTasks map { t => GroundTask(t, Nil) }
        val groundLiterals = domainAndPlan._1.predicates map { p => GroundLiteral(p, true, Nil) }
        val groundMethods = domainAndPlan._1.methodsForAbstractTasks map { case (at, ms) =>
          at -> JavaConversions.setAsJavaSet(ms map { m => GroundedDecompositionMethod(m, Map()) } toSet)
        }


        val (doBFS, doDFS, aStar) = progression.searchAlgorithm match {
          case BFSType                          => (true, false, false)
          case DFSType                          => assert(progression.heuristic.isEmpty); (false, true, false)
          case AStarActionsType(weight: Double) => assert(weight == 1); (false, false, true)
          case AStarDepthType(weight: Double)   => assert(false); (false, false, false)
          case GreedyType                       => (false, false, false)
          case DijkstraType                     => assert(false); (false, false, false)
        }

        // scalastyle:off null
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
          val solutionFound = progressionInstance.plan(domainAndPlan._2, JavaConversions.mapAsJavaMap(groundMethods), JavaConversions.setAsJavaSet(groundTasks.toSet),
                                                       JavaConversions.setAsJavaSet(groundLiterals.toSet), informationCapsule, timeCapsule,
                                                       progression.abstractTaskSelectionStrategy,
                                                       progression.heuristic.getOrElse(null), doBFS, doDFS, aStar, progression.deleteRelaxed,
                                                       progression.timeLimit.getOrElse(Int.MaxValue).toLong * 1000)

          timeCapsule stop TOTAL_TIME
          runPostProcessing(timeCapsule, informationCapsule, null, if (solutionFound) null :: Nil else Nil, domainAndPlan, analysisMap)
        })

      case satSearch: SATSearch =>
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
          val runner = SATRunner(domainAndPlan._1, domainAndPlan._2, satSearch.solverType, timeCapsule, informationCapsule)
          val (solved, finished) = runner.runWithTimeLimit(satSearch.timeLimit.map({ a => 1000L * a }), satSearch.maximumPlanLength, 0, defineK = satSearch.overrideK, checkSolution =
            satSearch.checkResult)

          informationCapsule.set(Information.SOLVED, if (solved) "true" else "false")
          informationCapsule.set(Information.TIMEOUT, if (finished) "false" else "true")

          timeCapsule stop TOTAL_TIME
          runPostProcessing(timeCapsule, informationCapsule, null, if (solved) null :: Nil else Nil, domainAndPlan, analysisMap)
        })

      case NoSearch => (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, analysisMap)
      })
    }
  }


  private def constructEfficientHeuristic(heuristicConfig: SearchHeuristic, wrapper: Wrapping, analysisMap: AnalysisMap, domainAndPlan: (Domain, Plan))
  : EfficientHeuristic[_] = {
    // if we need the ADD heuristic as a building block create it
    val optionADD = heuristicConfig match {
      case LiftedTDGMinimumADD(_, _) | TDGMinimumADD(_) =>
        // TODO experimental
        val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
        val initialState = domainAndPlan._2.groundedInitialState collect { case GroundLiteral(task, true, args) =>
          (wrapper.unwrap(task), args map wrapper.unwrap toArray)
        }
        // TODO check that we have compiled negative preconditions away
        Some(AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, resuingAsVHPOP = false))
      case _                                            => None
    }


    // get the inner heuristic if one exists
    val innerHeuristic = heuristicConfig match {
      case inner: SearchHeuristicWithInner => inner.innerHeuristic map { h => constructEfficientHeuristic(h, wrapper, analysisMap, domainAndPlan) }
      case _                               => None
    }

    val innerHeuristicAsMinimisationOverGrounding = innerHeuristic match {
      case Some(h) => h match {
        case x: MinimisationOverGroundingsBasedHeuristic[Unit] => Some(x)
        case _                                                 => None // TODO assert if necessary
      }
      case _       => None
    }

    heuristicConfig match {
      case RandomHeuristic(seed)     => EfficientRandomHeuristic(new Random(seed))
      case NumberOfFlaws             => EfficientNumberOfFlaws
      case NumberOfOpenPreconditions => EfficientNumberOfOpenPreconditions
      case NumberOfPlanSteps         => EfficientNumberOfPlanSteps
      case NumberOfAbstractPlanSteps => EfficientNumberOfAbstractPlanSteps
      case UMCPHeuristic             => EfficientUMCPHeuristic
      case WeightedFlaws             => ???
      // HTN heuristics
      case TDGMinimumModificationWithCycleDetection(_) =>
        MinimumModificationEffortHeuristicWithCycleDetection(analysisMap(EfficientGroundedTDG), wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case TDGPreconditionRelaxation(_)                =>
        PreconditionRelaxationTDGHeuristic(analysisMap(EfficientGroundedTDG), wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case TDGMinimumAction(_)                         =>
        MinimumActionCount(analysisMap(EfficientGroundedTDG), wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case TDGMinimumADD(_)                            =>
        MinimumADDHeuristic(analysisMap(EfficientGroundedTDG), optionADD.get, wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)

      case LiftedTDGMinimumModificationWithCycleDetection(NeverRecompute, _) =>
        PreComputingLiftedMinimumModificationEffortHeuristicWithCycleDetection(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGPreconditionRelaxation(NeverRecompute, _)                =>
        PreComputingLiftedPreconditionRelaxationTDGHeuristic(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGMinimumAction(NeverRecompute, _)                         =>
        PreComputingLiftedMinimumActionCount(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGMinimumADD(NeverRecompute, _)                            =>
        PreComputingLiftedMinimumADD(wrapper.efficientDomain, optionADD.get, innerHeuristicAsMinimisationOverGrounding)

      case LiftedTDGMinimumModificationWithCycleDetection(ReachabilityRecompute, _) =>
        ReachabilityRecomputingLiftedMinimumModificationEffortHeuristicWithCycleDetection(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGPreconditionRelaxation(ReachabilityRecompute, _)                =>
        ReachabilityRecomputingLiftedPreconditionRelaxationTDGHeuristic(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGMinimumAction(ReachabilityRecompute, _)                         =>
        ReachabilityRecomputingLiftedMinimumActionCount(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGMinimumADD(ReachabilityRecompute, _)                            =>
        ReachabilityRecomputingLiftedMinimumADD(wrapper.efficientDomain, optionADD.get, innerHeuristicAsMinimisationOverGrounding)

      case LiftedTDGMinimumModificationWithCycleDetection(CausalLinkRecompute, _) =>
        CausalLinkRecomputingLiftedMinimumModificationEffortHeuristicWithCycleDetection(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGPreconditionRelaxation(CausalLinkRecompute, _)                =>
        CausalLinkRecomputingLiftedPreconditionRelaxationTDGHeuristic(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGMinimumAction(CausalLinkRecompute, _)                         =>
        CausalLinkRecomputingLiftedMinimumActionCount(wrapper.efficientDomain, innerHeuristicAsMinimisationOverGrounding)
      case LiftedTDGMinimumADD(CausalLinkRecompute, _)                            =>
        CausalLinkRecomputingLiftedMinimumADD(wrapper.efficientDomain, optionADD.get, innerHeuristicAsMinimisationOverGrounding)



      // classical heuristics
      case ADD | ADDReusing | Relax =>
        val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
        val initialState = domainAndPlan._2.groundedInitialState collect { case GroundLiteral(task, true, args) =>
          (wrapper.unwrap(task), args map wrapper.unwrap toArray)
        }

        heuristicConfig match {
          case ADD | ADDReusing =>
            val reusing = if (heuristicConfig == ADDReusing) true else false
            // TODO check that we have compiled negative preconditions away
            AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, reusing)
          case Relax            =>
            RelaxHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray)
        }
    }
  }

  def runPostProcessing(timeCapsule: TimeCapsule, informationCapsule: InformationCapsule, rootNode: SearchNode, result: Seq[Plan], domainAndPlan: (Domain, Plan),
                        analysisMap: AnalysisMap): ResultMap =
    ResultMap(postprocessingConfiguration.resultsToProduce map { resultType => (resultType, resultType match {
      case ProcessingTimings => timeCapsule

      case SearchStatus                   =>
        val determinedSearchSate =
          if (informationCapsule.dataMap().contains(Information.ERROR)) SearchState.INSEARCH
          else if (result.nonEmpty) SearchState.SOLUTION
          else if (timeCapsule.integralDataMap().contains(Timings.SEARCH) && timeCapsule.integralDataMap()(Timings.SEARCH) >=
            1000 * searchConfiguration.timeLimit.getOrElse(Integer.MAX_VALUE / 1000))
            SearchState.TIMEOUT
          else {
            searchConfiguration match {
              case search: PlanBasedSearch =>
                if (search.nodeLimit.isEmpty || search.nodeLimit.get > informationCapsule(Information.NUMBER_OF_NODES))
                  SearchState.UNSOLVABLE
                else SearchState.INSEARCH
              case _                       => SearchState.INSEARCH
            }
          }
        // write search state into the information capsule
        informationCapsule.set(Information.SOLVED_STATE, determinedSearchSate.toString)

        determinedSearchSate
      case SearchResult                   => result.headOption
      case AllFoundPlans                  => result
      case SearchStatistics               => informationCapsule
      case SearchSpace                    => rootNode
      case SolutionInternalString         => if (result.nonEmpty) Some(result.head.longInfo) else None
      case SolutionDotString              => if (result.nonEmpty) Some(result.head.dotString) else None
      case FinalTaskDecompositionGraph    => analysisMap(SymbolicGroundedTaskDecompositionGraph)
      case FinalGroundedReachability      => analysisMap(SymbolicGroundedReachability)
      case PreprocessedDomainAndPlan      => domainAndPlan
      case AllFoundSolutionPathsWithHStar =>
        // we have to find all solutions paths, so first compute the solution state of each node to only traverse the paths to the actual solutions
        rootNode.recomputeSearchState()

        def getAllSolutions(node: SearchNode): (Int, Seq[Seq[(SearchNode, Int)]]) = {
          assert(node.searchState == SearchState.SOLUTION)
          if (node.children.isEmpty) (node.plan.planSteps.length, ((node, 0) :: Nil) :: Nil)
          else {
            val solvableChildren = node.children filter {
              _._1.searchState == SearchState.SOLUTION
            } map {
              _._1
            }
            val children = solvableChildren map getAllSolutions
            val minimalLength = children map {
              _._1
            } min

            // semantic empty line
            (minimalLength, children flatMap {
              _._2
            } map { case p => p.+:(node, minimalLength - node.plan.planSteps.length) })
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
      case XMLParserType        => XMLParser.asParser.parseDomainAndProblem(domain, problem)
      case HDDLParserType       => HDDLParser.parseDomainAndProblem(domain, problem)
      case HPDDLParserType      => HPDDLParser.parseDomainAndProblem(domain, problem)
      case OldPDDLType          => OldPDDLParser.parseDomainAndProblem(domain, problem)
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

    timeCapsule start PARSER_STRIP_HYBRID
    val noHybrid = if (parsingConfiguration.stripHybrid) StripHybrid.transform(identity, ()) else simpleMethod
    timeCapsule stop PARSER_STRIP_HYBRID

    timeCapsule start PARSER_FLATTEN_FORMULA
    val flattened = if (parsingConfiguration.toPlainFormulaRepresentation) ToPlainFormulaRepresentation.transform(noHybrid, ()) else simpleMethod
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

  private def runGroundedPlanningGraph(domain: Domain, problem: Plan, useMutexes: Boolean, analysisMap: AnalysisMap): AnalysisMap = {
    val groundedInitialState = problem.groundedInitialState filter {
      _.isPositive
    }
    val groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis = GroundedPlanningGraph(domain, groundedInitialState.toSet,
                                                                                                    GroundedPlanningGraphConfiguration(computeMutexes = useMutexes))
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
      case TwoWayTDG  => TwoStepDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true)
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
    if (preprocessingConfiguration.groundedReachability.isDefined) timeCapsule start GROUNDED_PLANNINGGRAPH_ANALYSIS
    val groundedResult = if (preprocessingConfiguration.groundedReachability.isDefined) {

      // output info text
      val infoText = preprocessingConfiguration.groundedReachability.get match {
        case NaiveGroundedReachability => "Naive grounded reachability analysis"
        case PlanningGraph             => "Grounded planning graph"
        case PlanningGraphWithMutexes  => "Grounded planning graph with mutexes"
      }
      info(infoText + " ... ")

      // run the actual analysis
      val newAnalysisMap =
        preprocessingConfiguration.groundedReachability.get match {
          case NaiveGroundedReachability                => runGroundedForwardSearchReachabilityAnalysis(liftedResult._1._1, liftedResult._1._2, liftedResult._2)
          case PlanningGraph | PlanningGraphWithMutexes =>
            val useMutexes = preprocessingConfiguration.groundedReachability.get match {
              case PlanningGraph            => false
              case PlanningGraphWithMutexes => true
            }
            runGroundedPlanningGraph(liftedResult._1._1, liftedResult._1._2, useMutexes, liftedResult._2)
        }

      val reachable = newAnalysisMap(SymbolicGroundedReachability).reachableLiftedPrimitiveActions.toSet
      val disallowedTasks = liftedResult._1._1.primitiveTasks filterNot reachable.contains
      val pruned = PruneHierarchy.transform(liftedResult._1._1, liftedResult._1._2, disallowedTasks.toSet)
      info("done.\n")
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else liftedResult
    if (preprocessingConfiguration.groundedReachability.isDefined) timeCapsule stop GROUNDED_PLANNINGGRAPH_ANALYSIS

    // naive task decomposition graph
    timeCapsule start GROUNDED_TDG_ANALYSIS
    val tdgResult = if (preprocessingConfiguration.groundedTaskDecompositionGraph.isDefined) {
      val actualConfig = if (groundedResult._1._1.isGround) NaiveTDG else preprocessingConfiguration.groundedTaskDecompositionGraph.get
      info(actualConfig.toString + " ... ")
      // get the reachability analysis, if there is none, just use the trivial one
      val newAnalysisMap = runGroundedTaskDecompositionGraph(groundedResult._1._1, groundedResult._1._2, groundedResult._2, actualConfig)
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
        (if (preprocessingConfiguration.splitIndependedParameters)
          CompilerConfiguration(SplitIndependentParameters, (), "split parameters", SPLIT_PARAMETERS) :: Nil
        else Nil) ::
        // this one has to be the last
        (if (preprocessingConfiguration.compileInitialPlan)
          CompilerConfiguration(ReplaceInitialPlanByTop, (), "initial plan", TOP_TASK) :: Nil
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
                                 stripHybrid: Boolean = false,
                                 toPlainFormulaRepresentation: Boolean = true
                               ) {}

sealed trait TDGGeneration

object NaiveTDG extends TDGGeneration {
  override def toString: String = "Naive TDG"
}

object TopDownTDG extends TDGGeneration {
  override def toString: String = "Top Down TDG"
}

object TwoWayTDG extends TDGGeneration {
  override def toString: String = "Two Way TDG"
}

sealed trait GroundedReachabilityMode

object NaiveGroundedReachability extends GroundedReachabilityMode

object PlanningGraph extends GroundedReachabilityMode

object PlanningGraphWithMutexes extends GroundedReachabilityMode

case class PreprocessingConfiguration(
                                       compileNegativePreconditions: Boolean,
                                       compileUnitMethods: Boolean,
                                       compileOrderInMethods: Option[TotallyOrderingOption],
                                       compileInitialPlan: Boolean,
                                       splitIndependedParameters: Boolean,
                                       liftedReachability: Boolean,
                                       groundedReachability: Option[GroundedReachabilityMode],
                                       groundedTaskDecompositionGraph: Option[TDGGeneration],
                                       iterateReachabilityAnalysis: Boolean,
                                       groundDomain: Boolean
                                     ) {
  //assert(!groundDomain || naiveGroundedTaskDecompositionGraph, "A grounded reachability analysis (grounded TDG) must be performed in order to ground.")
}

/**
  * all available search algorithms
  */
sealed trait SearchAlgorithmType

object BFSType extends SearchAlgorithmType

object DFSType extends SearchAlgorithmType

sealed trait WeightedSearchAlgorithmType extends SearchAlgorithmType {
  def weight: Double
}

case class AStarActionsType(weight: Double) extends WeightedSearchAlgorithmType

case class AStarDepthType(weight: Double) extends WeightedSearchAlgorithmType

object GreedyType extends SearchAlgorithmType

object DijkstraType extends SearchAlgorithmType

/**
  * all available heuristics
  */
sealed trait SearchHeuristic {}

sealed trait SearchHeuristicWithInner extends SearchHeuristic {
  def innerHeuristic: Option[SearchHeuristic]
}

case class RandomHeuristic(seed: Long) extends SearchHeuristic

// general heuristics
object NumberOfFlaws extends SearchHeuristic

object NumberOfOpenPreconditions extends SearchHeuristic

object NumberOfPlanSteps extends SearchHeuristic

object NumberOfAbstractPlanSteps extends SearchHeuristic

object WeightedFlaws extends SearchHeuristic

object UMCPHeuristic extends SearchHeuristic

// TDG heuristics
sealed trait TDGBasedHeuristic extends SearchHeuristicWithInner

case class TDGMinimumModificationWithCycleDetection(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic

case class TDGPreconditionRelaxation(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic

case class TDGMinimumADD(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic

case class TDGMinimumAction(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic

// works only with TSTG

sealed trait RecomputationMode

object NeverRecompute extends RecomputationMode

object ReachabilityRecompute extends RecomputationMode

object CausalLinkRecompute extends RecomputationMode

case class LiftedTDGMinimumModificationWithCycleDetection(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None) extends SearchHeuristicWithInner

case class LiftedTDGPreconditionRelaxation(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None) extends SearchHeuristicWithInner

case class LiftedTDGMinimumAction(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None) extends SearchHeuristicWithInner

case class LiftedTDGMinimumADD(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None) extends SearchHeuristicWithInner


// POCL heuristics
object ADD extends SearchHeuristic

object ADDReusing extends SearchHeuristic

object Relax extends SearchHeuristic


// PANDAPRO heuristics
object SimpleCompositionRPG extends SearchHeuristic

case class RelaxedCompositionGraph(useTDReachability: Boolean, heuristicExtraction: RCG.heuristicExtraction, producerSelectionStrategy: RCG.producerSelection) extends SearchHeuristic

object CompositionRPGHTN extends SearchHeuristic

object GreedyProgression extends SearchHeuristic

object DeleteRelaxedHTN extends SearchHeuristic


sealed trait PruningTechnique

object TreeFFFilter extends PruningTechnique

object RecomputeHierarchicalReachability extends PruningTechnique

/**
  * all available flaw selectors
  */
sealed trait SearchFlawSelector {}

object LCFR extends SearchFlawSelector

object UMCPFlaw extends SearchFlawSelector

case class RandomFlaw(seed: Long) extends SearchFlawSelector

case class SequentialSelector(sequence: SearchFlawSelector*) extends SearchFlawSelector

sealed trait SearchConfiguration {
  def timeLimit: Option[Int]

}

case class PlanBasedSearch(
                            nodeLimit: Option[Int],
                            timeLimit: Option[Int],
                            searchAlgorithm: SearchAlgorithmType,
                            heuristic: Seq[SearchHeuristic],
                            pruningTechniques: Seq[PruningTechnique],
                            flawSelector: SearchFlawSelector,
                            efficientSearch: Boolean = true,
                            continueOnSolution: Boolean = false,
                            printSearchInfo: Boolean = true

                          ) extends SearchConfiguration {
}

case class ProgressionSearch(timeLimit: Option[Int],
                             searchAlgorithm: SearchAlgorithmType,
                             heuristic: Option[SearchHeuristic],
                             abstractTaskSelectionStrategy: PriorityQueueSearch.abstractTaskSelection,
                             deleteRelaxed: Boolean = false) extends SearchConfiguration {}

case class SATSearch(timeLimit: Option[Int],
                     solverType: Solvertype,
                     maximumPlanLength: Int,
                     overrideK: Option[Int] = None,
                     checkResult: Boolean = false
                    ) extends SearchConfiguration {}

object NoSearch extends SearchConfiguration {
  override val timeLimit: Option[Int] = Some(0)
}

case class PostprocessingConfiguration(resultsToProduce: Set[ResultType]) {
  if (resultsToProduce contains AllFoundSolutionPathsWithHStar) assert(resultsToProduce contains SearchSpace,
                                                                       "If we have to produce paths to the solutions, we have to keep the search space")

}
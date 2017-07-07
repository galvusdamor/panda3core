package de.uniulm.ki.panda3.configuration

import java.io.InputStream
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraphFromSymbolic
import de.uniulm.ki.panda3.efficient.heuristic._

import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientTDGFromGroundedSymbolic
import de.uniulm.ki.panda3.efficient.heuristic.filter.{PlanLengthLimit, RecomputeHTN}
import de.uniulm.ki.panda3.efficient.heuristic.{AlwaysZeroHeuristic, EfficientNumberOfFlaws, EfficientNumberOfPlanSteps}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.search.flawSelector._
import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.RCG
import de.uniulm.ki.panda3.symbolic.{DefaultLongInfo, PrettyPrintable}
import de.uniulm.ki.panda3.symbolic.parser.FileTypeDetector
import de.uniulm.ki.panda3.symbolic.parser.oldpddl.OldPDDLParser
import de.uniulm.ki.panda3.symbolic.sat.verify.{CRYPTOMINISAT, MINISAT, Solvertype, SATRunner}
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
                                 randomSeed: Long = 42, timeLimit: Option[Int] = None,
                                 parsingConfiguration: ParsingConfiguration = ParsingConfiguration(),
                                 preprocessingConfiguration: PreprocessingConfiguration,
                                 searchConfiguration: SearchConfiguration,
                                 postprocessingConfiguration: PostprocessingConfiguration) extends Configuration {

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
    val (domainAndPlanFullyParsed, unprocessedDomain, _) = runParsingPostProcessing(domain, problem, timeCapsule)
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

    // write randomseed into the info capsule
    informationCapsule.set(Information.RANDOM_SEED, randomSeed.toString)


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

              searchObject.startSearch(domainAndPlan._1, domainAndPlan._2, search.nodeLimit, timeLimit,
                                       releaseSemaphoreEvery, search.printSearchInfo,
                                       postprocessingConfiguration.resultsToProduce contains SearchSpace,
                                       informationCapsule, timeCapsule)
            case _                 => throw new UnsupportedOperationException("Any other symbolic search algorithm besides DFS or BFS is not supported.")
          }

          (domainAndPlan._1, searchTreeRoot, nodesProcessed, abortFunction, informationCapsule, { _ =>
            val actualResult: Seq[Plan] = resultfunction(())
            timeCapsule stop TOTAL_TIME
            runPostProcessing(timeCapsule, informationCapsule, searchTreeRoot, actualResult, domainAndPlan, unprocessedDomain, analysisMap)
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
            val initialState = domainAndPlan._2.groundedInitialState filter { _.isPositive } toSet
            val symbolicPlanningGraph = GroundedPlanningGraph(domainAndPlan._1, initialState, GroundedPlanningGraphConfiguration(computeMutexes = preprocessingConfiguration
              .groundedReachability.contains(PlanningGraphWithMutexes)))
            analysisMap = analysisMap +(EfficientGroundedPlanningGraph, EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraph, wrapper))
          }
          timeCapsule stop HEURISTICS_PREPARATION


          val flawSelector = search.flawSelector match {
            case LCFR                  => LeastCostFlawRepair
            case CausalThreat          => CausalThreatSelector
            case FrontFlaw             => FrontFlawFirst
            case NewestFlaw            => NewestFlawFirst
            case RandomFlaw            => RandomFlawSelector(new Random(randomSeed))
            case UMCPFlaw              => UMCPFlawSelection
            case s: SequentialSelector =>
              val subSelectorArray = s.sequence map {
                case LCFR         => LeastCostFlawRepair
                case RandomFlaw   => RandomFlawSelector(new Random(randomSeed))
                case CausalThreat => CausalThreatSelector
                case FrontFlaw    => FrontFlawFirst
                case NewestFlaw   => NewestFlawFirst
              } toArray

              SequentialEfficientFlawSelector(subSelectorArray)
          }

          val (searchTreeRoot, nodesProcessed, resultfunction, abortFunction) = search.searchAlgorithm match {
            case algo => algo match {
              case BFSType                                              => efficient.search.BFS.startSearch(wrapper.efficientDomain, efficientInitialPlan,
                                                                                                            search.nodeLimit, timeLimit, releaseSemaphoreEvery,
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
                                            search.nodeLimit, timeLimit, releaseSemaphoreEvery,
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
                  case PlanLengthFilter(limit)           => PlanLengthLimit(limit)
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
                                            search.nodeLimit, timeLimit, releaseSemaphoreEvery,
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
            runPostProcessing(timeCapsule, informationCapsule, wrappedSearchTreeRoot, actualResult, domainAndPlan, unprocessedDomain, analysisMap)
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
                                                       timeLimit.getOrElse(Int.MaxValue).toLong * 1000)

          timeCapsule stop TOTAL_TIME
          runPostProcessing(timeCapsule, informationCapsule, null, if (solutionFound) null :: Nil else Nil, domainAndPlan, unprocessedDomain, analysisMap)
        })

      case satSearch: SATSearch =>
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
          val runner = SATRunner(domainAndPlan._1, domainAndPlan._2, satSearch.solverType, timeCapsule, informationCapsule)
          val (solved, finished) = runner.runWithTimeLimit(timeLimit.map({ a => 1000L * a }), satSearch.maximumPlanLength, 0, defineK = satSearch.overrideK, checkSolution =
            satSearch.checkResult)

          informationCapsule.set(Information.SOLVED, if (solved) "true" else "false")
          informationCapsule.set(Information.TIMEOUT, if (finished) "false" else "true")

          timeCapsule stop TOTAL_TIME
          runPostProcessing(timeCapsule, informationCapsule, null, if (solved) null :: Nil else Nil, domainAndPlan, unprocessedDomain, analysisMap)
        })

      case NoSearch => (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
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
      case RandomHeuristic           => EfficientRandomHeuristic(new Random(randomSeed))
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

      case LiftedTDGMinimumActionCompareToWithoutRecompute(usePR) => ComparingTSTGHeuristic(wrapper.efficientDomain, usePR)


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
                        unprocessedDomainAndPlan: (Domain, Plan),
                        analysisMap: AnalysisMap): ResultMap =
    ResultMap(postprocessingConfiguration.resultsToProduce map { resultType => (resultType, resultType match {
      case ProcessingTimings => timeCapsule

      case SearchStatus                   =>
        val determinedSearchSate =
          if (informationCapsule.dataMap().contains(Information.ERROR)) SearchState.INSEARCH
          else if (result.nonEmpty) SearchState.SOLUTION
          else if (timeCapsule.integralDataMap().contains(Timings.TOTAL_TIME) && timeCapsule.integralDataMap()(Timings.TOTAL_TIME) >=
            1000 * timeLimit.getOrElse(Integer.MAX_VALUE / 1000))
            SearchState.TIMEOUT
          else {
            searchConfiguration match {
              case search: PlanBasedSearch =>
                if (informationCapsule.dataMap().contains(Information.SEARCH_SPACE_FULLY_EXPLORED)) SearchState.UNSOLVABLE else SearchState.INSEARCH
              case _                       => SearchState.INSEARCH
            }
          }
        // write search state into the information capsule
        informationCapsule.set(Information.SOLVED_STATE, determinedSearchSate.toString)

        determinedSearchSate
      case InternalSearchResult           => result.headOption
      case SearchResult                   =>
        // start process of translating the solution back to something readable (i.e. lifted)
        result.headOption
      case AllFoundPlans                  => result
      case SearchStatistics               => informationCapsule
      case SearchSpace                    => rootNode
      case SolutionInternalString         => if (result.nonEmpty) Some(result.head.longInfo) else None
      case SolutionDotString              => if (result.nonEmpty) Some(result.head.dotString) else None
      case FinalTaskDecompositionGraph    => analysisMap(SymbolicGroundedTaskDecompositionGraph)
      case FinalGroundedReachability      => analysisMap(SymbolicGroundedReachability)
      case PreprocessedDomainAndPlan      => domainAndPlan
      case UnprocessedDomainAndPlan       => unprocessedDomainAndPlan
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

  def runParsingPostProcessing(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): ((Domain, Plan), (Domain, Plan), TimeCapsule) = {
    info("Preparing internal domain representation ... ")

    timeCapsule startOrLetRun PARSING
    timeCapsule start PARSER_SORT_EXPANSION
    val sortsExpandedDomainAndProblem = if (parsingConfiguration.expandSortHierarchy) {
      val sortExpansion = domain.expandSortHierarchy()
      (domain.update(sortExpansion), problem.update(sortExpansion))
    } else (domain, problem)
    timeCapsule stop PARSER_SORT_EXPANSION

    timeCapsule start PARSER_STRIP_HYBRID
    val noHybrid = if (parsingConfiguration.stripHybrid) StripHybrid.transform(sortsExpandedDomainAndProblem, ()) else sortsExpandedDomainAndProblem
    if (parsingConfiguration.stripHybrid) assert(!noHybrid._1.isHybrid)
    timeCapsule stop PARSER_STRIP_HYBRID

    timeCapsule start PARSER_SHOP_METHODS
    val simpleMethod = if (parsingConfiguration.compileSHOPMethods) SHOPMethodCompiler.transform(noHybrid, ()) else noHybrid
    timeCapsule stop PARSER_SHOP_METHODS

    timeCapsule start PARSER_FLATTEN_FORMULA
    val flattened = if (parsingConfiguration.toPlainFormulaRepresentation) ToPlainFormulaRepresentation.transform(simpleMethod, ()) else simpleMethod
    timeCapsule stop PARSER_FLATTEN_FORMULA

    timeCapsule start PARSER_CWA
    val cwaApplied = if (parsingConfiguration.closedWorldAssumption) ClosedWorldAssumption.transform(flattened, true) else flattened
    timeCapsule stop PARSER_CWA

    timeCapsule start PARSER_ELIMINATE_EQUALITY
    val identity = if (parsingConfiguration.eliminateEquality) RemoveIdenticalVariables.transform(cwaApplied, ()) else cwaApplied
    timeCapsule stop PARSER_ELIMINATE_EQUALITY

    timeCapsule stop PARSING
    info("done.\n")
    (identity,noHybrid, timeCapsule)
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
        (if (preprocessingConfiguration.splitIndependentParameters)
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


  /** returns a detailed information about the object */
  override def longInfo: String = "Planning Configuration\n======================\n" +
    alignConfig(("\tprintGeneralInformation", printGeneralInformation) ::("\tprintAdditionalData", printAdditionalData) ::
                  ("\trandom seed", randomSeed) ::("\ttime limit (in seconds)", timeLimit.getOrElse("none")) :: Nil) + "\n\n" + {
    parsingConfiguration.longInfo + "\n\n" + preprocessingConfiguration.longInfo + "\n\n" + searchConfiguration.longInfo + "\n\n" + postprocessingConfiguration.longInfo
  }.split("\n").map(x => "\t" + x).mkString("\n")

  import PlanningConfiguration._

  protected override def localModifications: Seq[(String, (Option[String]) => PlanningConfiguration.this.type)] = localModificationsByKey ++ predefinedConfigurations

  protected def localModificationsByKey: Seq[(String, (Option[String]) => PlanningConfiguration.this.type)] =
    Seq(
         "-printGeneralInfo" -> { l => assert(l.isEmpty); this.copy(printGeneralInformation = true).asInstanceOf[this.type] },
         "-noGeneralInfo" -> { l => assert(l.isEmpty); this.copy(printGeneralInformation = false).asInstanceOf[this.type] },
         "-printAdditionalInfo" -> { l => assert(l.isEmpty); this.copy(printAdditionalData = true).asInstanceOf[this.type] },
         "-noAdditionalInfo" -> { l => assert(l.isEmpty); this.copy(printAdditionalData = false).asInstanceOf[this.type] },

         "-noSearch" -> { l => assert(l.isEmpty); this.copy(searchConfiguration = NoSearch).asInstanceOf[this.type] },
         "-planSearch" -> { l => assert(l.isEmpty); this.copy(searchConfiguration = defaultPlanSearchConfiguration).asInstanceOf[this.type] },
         "-progression" -> { l => assert(l.isEmpty); this.copy(searchConfiguration = defaultProgressionConfiguration).asInstanceOf[this.type] },
         "-SAT" -> { l => assert(l.isEmpty); this.copy(searchConfiguration = defaultSATConfiguration).asInstanceOf[this.type] },

         "-seed" -> { l => assert(l.isDefined, "No seed provided"); this.copy(randomSeed = l.get.toInt).asInstanceOf[this.type] },
         "-timelimit" -> { l => assert(l.isDefined, "No time limit provided"); this.copy(timeLimit = Some(l.get.toInt)).asInstanceOf[this.type] }
       )

  protected def predefinedConfigurations: Seq[(String, (Option[String]) => PlanningConfiguration.this.type)] =
    (PredefinedConfigurations.parsingConfigs.toSeq map { case (k, p) => k -> { l: Option[String] => assert(l.isEmpty); this.copy(parsingConfiguration = p).asInstanceOf[this.type] } }) ++
      (PredefinedConfigurations.preprocessConfigs.toSeq map { case (k, p) =>
        k -> { l: Option[String] => assert(l.isEmpty); this.copy(preprocessingConfiguration = p).asInstanceOf[this.type] }
      }) ++
      (PredefinedConfigurations.defaultConfigurations.toSeq map {
        case (k, (parse, pre, search)) => k -> { l: Option[String] => assert(l.isEmpty);
          this.copy(parsingConfiguration = parse, preprocessingConfiguration = pre, searchConfiguration =
            search).asInstanceOf[this.type]
        }
      })


  override def potentialRecursiveChildren: Seq[Configuration] = (searchConfiguration match {
    case NoSearch =>
      defaultPlanSearchConfiguration ::
        defaultProgressionConfiguration ::
        defaultSATConfiguration ::
        NoSearch :: Nil
    case x        => x :: Nil
  }) ++ (parsingConfiguration :: preprocessingConfiguration :: postprocessingConfiguration :: Nil)

  protected override def recursiveMethods(conf: Configuration): (conf.type) => PlanningConfiguration.this.type = conf match {
    case _: ParsingConfiguration        => {case p: ParsingConfiguration => this.copy(parsingConfiguration = p).asInstanceOf[this.type]}
    case _: PreprocessingConfiguration  => {case p: PreprocessingConfiguration => this.copy(preprocessingConfiguration = p).asInstanceOf[this.type]}
    case _: SearchConfiguration         => {case p: SearchConfiguration => this.copy(searchConfiguration = p).asInstanceOf[this.type]}
    case _: PostprocessingConfiguration => {case p: PostprocessingConfiguration => this.copy(postprocessingConfiguration = p).asInstanceOf[this.type]}
  }
}

object PlanningConfiguration {
  private val defaultPlanSearchConfiguration  = PlanBasedSearch(None, BFSType, Nil, Nil, LCFR)
  private val defaultProgressionConfiguration = ProgressionSearch(BFSType, None, PriorityQueueSearch.abstractTaskSelection.random)
  private val defaultSATConfiguration         = SATSearch(MINISAT, 0)
}

/**
  * all available search algorithms
  */
sealed trait ParserType extends Configuration

object XMLParserType extends ParserType {override def longInfo: String = "XML Parser (PANDA1/2's format)"}

object HDDLParserType extends ParserType {override def longInfo: String = "HDDL Parser (Daniel's format)"}

object HPDDLParserType extends ParserType {override def longInfo: String = "HPDDL Parser (Ron's format)"}

object OldPDDLType extends ParserType {override def longInfo: String = "Conformant PDDL Parser (allows non PDDL 2.1 input)"}

object AutoDetectParserType extends ParserType {override def longInfo: String = "autodetect file-type"}

case class ParsingConfiguration(
                                 parserType: ParserType = AutoDetectParserType,
                                 expandSortHierarchy: Boolean = true,
                                 closedWorldAssumption: Boolean = true,
                                 compileSHOPMethods: Boolean = true,
                                 eliminateEquality: Boolean = true,
                                 stripHybrid: Boolean = false,
                                 toPlainFormulaRepresentation: Boolean = true
                               ) extends Configuration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Parsing Configuration\n---------------------\n" +
    alignConfig(("Parser", parserType.longInfo) ::("Expand Sort Hierarchy", expandSortHierarchy) ::
                  ("ClosedWordAssumption", closedWorldAssumption) ::
                  ("CompileSHOPMethods", compileSHOPMethods) ::
                  ("Eliminate Equality", eliminateEquality) ::
                  ("Strip Hybridity", stripHybrid) ::
                  ("To Plain Formula Representation", toPlainFormulaRepresentation) :: Nil
               )

  protected override def localModifications: Seq[(String, (Option[String]) => ParsingConfiguration.this.type)] =
    Seq(
         "-parser" -> { p => assert(p.isDefined)
           val parser = p.get.toLowerCase match {
             case "xml"           => XMLParserType
             case "hddl" | "pddl" => HDDLParserType
             case "hpddl"         => HPDDLParserType
             case "old-pddl"      => OldPDDLType
             case "auto"          => AutoDetectParserType
           }
           this.copy(parserType = parser).asInstanceOf[this.type]
         },

         "-expandSortHierarchy" -> { p => (if (p.isEmpty) this.copy(expandSortHierarchy = true) else this.copy(expandSortHierarchy = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontExpandSortHierarchy" -> { p => assert(p.isEmpty); this.copy(expandSortHierarchy = false).asInstanceOf[this.type] },

         "-closedWorldAssumption" -> { p => (if (p.isEmpty) this.copy(closedWorldAssumption = true) else this.copy(closedWorldAssumption = p.get.toBoolean)).asInstanceOf[this.type] },
         "-noClosedWorldAssumption" -> { p => assert(p.isEmpty); this.copy(closedWorldAssumption = false).asInstanceOf[this.type] },

         "-compileSHOPMethods" -> { p => (if (p.isEmpty) this.copy(compileSHOPMethods = true) else this.copy(compileSHOPMethods = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontCompileSHOPMethods" -> { p => assert(p.isEmpty); this.copy(compileSHOPMethods = false).asInstanceOf[this.type] },

         "-eliminateEquality" -> { p => (if (p.isEmpty) this.copy(eliminateEquality = true) else this.copy(eliminateEquality = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontEliminateEquality" -> { p => assert(p.isEmpty); this.copy(eliminateEquality = false).asInstanceOf[this.type] },

         "-stripHybrid" -> { p => (if (p.isEmpty) this.copy(stripHybrid = true) else this.copy(stripHybrid = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontStripHybrid" -> { p => assert(p.isEmpty); this.copy(stripHybrid = false).asInstanceOf[this.type] },

         "-toPlainFormulaRepresentation" -> { p =>
           (if (p.isEmpty) this.copy(toPlainFormulaRepresentation = true) else this.copy(toPlainFormulaRepresentation = p.get.toBoolean)).asInstanceOf[this.type]
         },
         "-generalFormulaRepresentation" -> { p => assert(p.isEmpty); this.copy(toPlainFormulaRepresentation = false).asInstanceOf[this.type] }
       )
}

sealed trait TDGGeneration extends Configuration

object NaiveTDG extends TDGGeneration {override def toString: String = "Naive TDG"}

object TopDownTDG extends TDGGeneration {override def toString: String = "Top Down TDG"}

object TwoWayTDG extends TDGGeneration {override def toString: String = "Two Way TDG"}

sealed trait GroundedReachabilityMode extends Configuration

object NaiveGroundedReachability extends GroundedReachabilityMode {override def longInfo: String = "Naive Reachability"}

object PlanningGraph extends GroundedReachabilityMode {override def longInfo: String = "Planning Graph"}

object PlanningGraphWithMutexes extends GroundedReachabilityMode {override def longInfo: String = "Planning Graph with Mutexes"}

case class PreprocessingConfiguration(
                                       compileNegativePreconditions: Boolean,
                                       compileUnitMethods: Boolean,
                                       compileOrderInMethods: Option[TotallyOrderingOption],
                                       compileInitialPlan: Boolean,
                                       splitIndependentParameters: Boolean,
                                       liftedReachability: Boolean,
                                       groundedReachability: Option[GroundedReachabilityMode],
                                       groundedTaskDecompositionGraph: Option[TDGGeneration],
                                       iterateReachabilityAnalysis: Boolean,
                                       groundDomain: Boolean
                                     ) extends Configuration {

  override protected def localModifications: Seq[(String, (Option[String]) => PreprocessingConfiguration.this.type)] =
    Seq(
         "-compileNegativePreconditions" -> { p =>
           (if (p.isEmpty) this.copy(compileNegativePreconditions = true) else this.copy(compileNegativePreconditions = p.get.toBoolean)).asInstanceOf[this.type]
         },
         "-dontCompileNegativePreconditions" -> { p => assert(p.isEmpty); this.copy(compileNegativePreconditions = false).asInstanceOf[this.type] },

         "-compileUnitMethods" -> { p => (if (p.isEmpty) this.copy(compileUnitMethods = true) else this.copy(compileUnitMethods = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontCompileUnitMethods" -> { p => assert(p.isEmpty); this.copy(compileUnitMethods = false).asInstanceOf[this.type] },

         "-totallyOrder" -> { p => assert(p.isDefined)
           val orderingOption: TotallyOrderingOption = p.get match {
             case "all"                        => AllOrderings
             case "all-necessary"              => AllNecessaryOrderings
             case "one"                        => OneRandomOrdering()
             case x if x.startsWith("at-most") => AtMostKOrderings(x.replace("=", " ").split(" ")(1).toInt)
           }
           this.copy(compileOrderInMethods = Some(orderingOption)).asInstanceOf[this.type]
         },
         "-dontTotallyOrder" -> { p => assert(p.isEmpty); this.copy(compileOrderInMethods = None).asInstanceOf[this.type] },

         "-compileInitialPlan" -> { p => (if (p.isEmpty) this.copy(compileInitialPlan = true) else this.copy(compileInitialPlan = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontCompileInitialPlan" -> { p => assert(p.isEmpty); this.copy(compileInitialPlan = false).asInstanceOf[this.type] },

         "-splitIndependentParameters" -> { p =>
           (if (p.isEmpty) this.copy(splitIndependentParameters = true) else this.copy(splitIndependentParameters = p.get.toBoolean)).asInstanceOf[this.type]
         },
         "-dontSplitIndependentParameters" -> { p => assert(p.isEmpty); this.copy(splitIndependentParameters = false).asInstanceOf[this.type] },

         "-liftedReachability" -> { p => (if (p.isEmpty) this.copy(liftedReachability = true) else this.copy(liftedReachability = p.get.toBoolean)).asInstanceOf[this.type] },
         "-noLiftedReachability" -> { p => assert(p.isEmpty); this.copy(liftedReachability = false).asInstanceOf[this.type] },

         "-groundedReachability" -> { p => assert(p.isDefined)
           val mode = p.get match {
             case "planningGraph" | "pg"             => PlanningGraph
             case "planningGraphWithMutexes" | "pgm" => PlanningGraphWithMutexes
             case "naive"                            => NaiveGroundedReachability // this one does not have a short-cut, but who uses it anyway?!
           }
           this.copy(groundedReachability = Some(mode)).asInstanceOf[this.type]
         },
         "-planningGraph" -> { p => assert(p.isEmpty); this.copy(groundedReachability = Some(PlanningGraph)).asInstanceOf[this.type] },
         "-planningGraphWithMutexes" -> { p => assert(p.isEmpty); this.copy(groundedReachability = Some(PlanningGraphWithMutexes)).asInstanceOf[this.type] },
         "-noGroundedReachability" -> { p => assert(p.isEmpty); this.copy(groundedReachability = None).asInstanceOf[this.type] },

         "-groundedTaskDecompositionGraph" -> { p => assert(p.isDefined)
           val mode = p.get match {
             case "topDown" => TopDownTDG
             case "twoWay"  => TwoWayTDG
             case "naive"   => NaiveTDG // this one does not have a short-cut, but who uses it anyway?!
           }
           this.copy(groundedTaskDecompositionGraph = Some(mode)).asInstanceOf[this.type]
         },
         "-topDownTDG" -> { p => assert(p.isEmpty); this.copy(groundedTaskDecompositionGraph = Some(TopDownTDG)).asInstanceOf[this.type] },
         "-twoWayTDG" -> { p => assert(p.isEmpty); this.copy(groundedTaskDecompositionGraph = Some(TwoWayTDG)).asInstanceOf[this.type] },
         "-tdg" -> { p => assert(p.isEmpty); this.copy(groundedTaskDecompositionGraph = Some(TwoWayTDG)).asInstanceOf[this.type] },
         "-noHierarchicalReachability" -> { p => assert(p.isEmpty); this.copy(groundedTaskDecompositionGraph = None).asInstanceOf[this.type] },

         "-iterateReachabilityAnalysis" -> { p =>
           (if (p.isEmpty) this.copy(iterateReachabilityAnalysis = true) else this.copy(iterateReachabilityAnalysis = p.get.toBoolean)).asInstanceOf[this.type]
         },
         "-dontIterateReachabilityAnalysis" -> { p => assert(p.isEmpty); this.copy(iterateReachabilityAnalysis = false).asInstanceOf[this.type] },

         "-groundDomain" -> { p => (if (p.isEmpty) this.copy(groundDomain = true) else this.copy(groundDomain = p.get.toBoolean)).asInstanceOf[this.type] },
         "-liftedDomain" -> { p => assert(p.isEmpty); this.copy(groundDomain = false).asInstanceOf[this.type] }
       )

  override def longInfo: String = "Preprocessing Configuration\n---------------------------\n" +
    alignConfig(("Compile negative preconditions", compileNegativePreconditions) ::
                  ("Compile unit methods", compileUnitMethods) ::
                  ("Compile order in methods", if (compileOrderInMethods.isEmpty) "false" else compileOrderInMethods.get) ::
                  ("Compile initial plan", compileInitialPlan) ::
                  ("Iterate reachability analysis", iterateReachabilityAnalysis) ::
                  ("Split indipendent parameters", splitIndependentParameters) ::
                  ("Lifted Reachability Analysis", liftedReachability) ::
                  ("Grounded Reachability Analysis", if (groundedReachability.isEmpty) "false" else groundedReachability.get.longInfo) ::
                  ("Grounded Task Decomposition Graph", if (groundedTaskDecompositionGraph.isEmpty) "false" else groundedTaskDecompositionGraph.get) ::
                  ("Iterate reachability analysis", iterateReachabilityAnalysis) ::
                  ("Ground domain", groundDomain) ::
                  Nil)

}

/**
  * all available search algorithms
  */
sealed trait SearchAlgorithmType extends DefaultLongInfo

object SearchAlgorithmType {
  def parse(text: String): SearchAlgorithmType = text.toLowerCase match {
    case "bfs"                                                   => BFSType
    case "dfs"                                                   => DFSType
    case "greedy"                                                => GreedyType
    case "dijkstra" | "uniform-cost"                             => DijkstraType
    case "astar" | "a*"                                          => AStarActionsType(weight = 1)
    case "depth-astar" | "depth-a*" | "astar-depth" | "a*-depth" => AStarDepthType(weight = 1)
    case x if x.startsWith("astar") || x.startsWith("a*")        => AStarActionsType(weight = x.replace(')', '(').split("\\(")(1).toDouble)
    case x if x.startsWith("depth-astar") || x.startsWith("depth-a*") ||
      x.startsWith("astar-depth") || x.startsWith("a*-depth")    => AStarDepthType(weight = x.replace(')', '(').split("\\(")(1).toDouble)
  }
}

object BFSType extends SearchAlgorithmType {override def longInfo: String = "BFS"}

object DFSType extends SearchAlgorithmType {override def longInfo: String = "DFS"}

sealed trait WeightedSearchAlgorithmType extends SearchAlgorithmType {
  def weight: Double
}

case class AStarActionsType(weight: Double) extends WeightedSearchAlgorithmType {override def longInfo: String = "A*-Action" + (if (weight != 1) ", weight=" + weight else "")}

case class AStarDepthType(weight: Double) extends WeightedSearchAlgorithmType {override def longInfo: String = "A*-Depth" + (if (weight != 1) ", weight=" + weight else "")}

object GreedyType extends SearchAlgorithmType {override def longInfo: String = "Greedy"}

object DijkstraType extends SearchAlgorithmType {override def longInfo: String = "Dijkstra"}


object ArgumentListParser {
  def parse[T](text: String, extractor: (String, Map[String, String]) => T): Seq[T] = text.replace(" ", ";").split(";") map { singleH =>
    val hList = singleH.replace(')', '(').split("\\(")
    val hName = hList.head
    val hParameterMap = if (hList.length == 1) Map[String, String]()
    else hList(1).split(",") map { case parameter =>
      val kv = parameter.split("=")
      kv.head -> (if (kv.length == 1) "" else kv(1))
    } toMap

    extractor(hName, hParameterMap)
  }
}

/**
  * all available heuristics
  */
sealed trait SearchHeuristic extends DefaultLongInfo

object SearchHeuristic {
  def parse(text: String): Seq[SearchHeuristic] = ArgumentListParser.parse(text, { case (hName, hParameterMap) =>
    hName.toLowerCase match {
      case "random"                                      => RandomHeuristic
      case "#flaw" | "number-of-flaws"                   => NumberOfFlaws
      case "#oc" | "number-of-open-preconditions"        => NumberOfOpenPreconditions
      case "#ps" | "number-of-plan-steps"                => NumberOfPlanSteps
      case "#abstract" | "number-of-abstract-plan-steps" => NumberOfAbstractPlanSteps
      case "umcp-h" | "umcp"                             => UMCPHeuristic
      // tdg heuristics which ground in each step
      case "grounding-mme-with-cycle-detection" => TDGMinimumModificationWithCycleDetection()
      case "grounding-pr"                       => TDGPreconditionRelaxation()
      case "grounding-tdg-minimum-add"          => TDGMinimumADD()
      case "grounding-tdg-mac"                  => TDGMinimumAction()
      // tdg heurisitcs which ground once
      case "mme-with-cycle-detection" => LiftedTDGMinimumModificationWithCycleDetection(mode = RecomputationMode.parse(hParameterMap.getOrElse("recompute", "never")))
      case "pr" | "tdg-pr"            => LiftedTDGPreconditionRelaxation(mode = RecomputationMode.parse(hParameterMap.getOrElse("recompute", "never")))
      case "tdg-minimum-add"          => LiftedTDGMinimumADD(mode = RecomputationMode.parse(hParameterMap.getOrElse("recompute", "never")))
      case "tdg-mac" | "mac"          => LiftedTDGMinimumAction(mode = RecomputationMode.parse(hParameterMap.getOrElse("recompute", "never")))
      // this heuristic only exists for evaluation purposes
      case "tdg-mac-compare" | "mac-compare" => LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false)
      case "tdg-pr-compare" | "pr-compare"   => LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true)
      // POCL heuristics
      case "add"                                             => ADD
      case "add_r" | "add-r" | "add_reusing" | "add-reusing" => ADDReusing
      case "relax"                                           => Relax

      // pandaPRO
      case "crpg" | "scrpg" | "composition-rpg" => SimpleCompositionRPG
      case "rcg" | "relaxed-composition-graph"  => RelaxedCompositionGraph(
                                                                            useTDReachability = hParameterMap.getOrElse("td-reachability", "true").toBoolean,
                                                                            heuristicExtraction = RCG.heuristicExtraction.parse(hParameterMap.getOrElse("extraction", "ff")),
                                                                            producerSelectionStrategy = RCG.producerSelection.parse(hParameterMap.getOrElse("selection", "fcfs")))
      case "crpghtn"                            => CompositionRPGHTN
      case "greedy-progression"                 => GreedyProgression
      case "delete-relaxed-htn"                 => DeleteRelaxedHTN
    }
  })
}

sealed trait SearchHeuristicWithInner extends SearchHeuristic {
  def innerHeuristic: Option[SearchHeuristic]
}

object RandomHeuristic extends SearchHeuristic {override val longInfo: String = "random"}

// general heuristics
object NumberOfFlaws extends SearchHeuristic {override val longInfo: String = "#Flaw"}

object NumberOfOpenPreconditions extends SearchHeuristic {override val longInfo: String = "#OC"}

object NumberOfPlanSteps extends SearchHeuristic {override val longInfo: String = "#PS"}

object NumberOfAbstractPlanSteps extends SearchHeuristic {override val longInfo: String = "#Abstract-PS"}

object WeightedFlaws extends SearchHeuristic {override val longInfo: String = "???"}

object UMCPHeuristic extends SearchHeuristic {override val longInfo: String = "UMCP-H"}

// TDG heuristics
sealed trait TDGBasedHeuristic extends SearchHeuristicWithInner

case class TDGMinimumModificationWithCycleDetection(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic {override val longInfo: String = "grounding-mme-with-cycle"}

case class TDGPreconditionRelaxation(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic {override val longInfo: String = "grounding-pr"}

case class TDGMinimumADD(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic {override val longInfo: String = "grounding-tdg-add"}

case class TDGMinimumAction(innerHeuristic: Option[SearchHeuristic] = None) extends TDGBasedHeuristic {override val longInfo: String = "grounding-tdg-mac"}

// works only with TSTG

sealed trait RecomputationMode extends DefaultLongInfo

object RecomputationMode {
  def parse(text: String): RecomputationMode = text.toLowerCase match {
    case "never"        => NeverRecompute
    case "reachability" => ReachabilityRecompute
    case "causal-link"  => CausalLinkRecompute
  }
}

object NeverRecompute extends RecomputationMode {override val longInfo: String = "never"}

object ReachabilityRecompute extends RecomputationMode {override val longInfo: String = "reachability"}

object CausalLinkRecompute extends RecomputationMode {override val longInfo: String = "causal-link"}

case class LiftedTDGMinimumModificationWithCycleDetection(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None)
  extends SearchHeuristicWithInner {override val longInfo: String = "mme-with-cycle-detection(recompute=" + mode.longInfo + ")"}

case class LiftedTDGPreconditionRelaxation(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None)
  extends SearchHeuristicWithInner {override val longInfo: String = "tdg-pr(recompute=" + mode.longInfo + ")"}

case class LiftedTDGMinimumAction(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None)
  extends SearchHeuristicWithInner {override val longInfo: String = "tdg-mac(recompute=" + mode.longInfo + ")"}

case class LiftedTDGMinimumADD(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None)
  extends SearchHeuristicWithInner {override val longInfo: String = "tdg-minimum-add(recompute=" + mode.longInfo + ")"}


// EVAL heuristics
case class LiftedTDGMinimumActionCompareToWithoutRecompute(usePR: Boolean) extends SearchHeuristic {override val longInfo: String = "tdg-mac-comparison"}


// POCL heuristics
object ADD extends SearchHeuristic {override val longInfo: String = "add"}

object ADDReusing extends SearchHeuristic {override val longInfo: String = "add_r"}

object Relax extends SearchHeuristic {override val longInfo: String = "relax"}


// PANDAPRO heuristics
object SimpleCompositionRPG extends SearchHeuristic {override val longInfo: String = "random"}

case class RelaxedCompositionGraph(useTDReachability: Boolean, heuristicExtraction: RCG.heuristicExtraction, producerSelectionStrategy: RCG.producerSelection)
  extends SearchHeuristic {override val longInfo: String = "rcg"}

object CompositionRPGHTN extends SearchHeuristic {override val longInfo: String = "crpg-htn"}

object GreedyProgression extends SearchHeuristic {override val longInfo: String = "greedy-progression"}

object DeleteRelaxedHTN extends SearchHeuristic {override val longInfo: String = "delete-relaxed-htn"}


sealed trait PruningTechnique extends DefaultLongInfo

object PruningTechnique {
  def parse(text: String): Seq[PruningTechnique] = ArgumentListParser.parse(text, { case (hName, hParameterMap) =>
    hName.toLowerCase match {
      case "tree-ff"                                      => TreeFFFilter
      case "recompute-htn" | "recompute-htn-reachability" => RecomputeHierarchicalReachability
      case "planLength"                                   => PlanLengthFilter(hParameterMap("limit").toInt)
    }
  })
}

object TreeFFFilter extends PruningTechnique {override val longInfo: String = "tree-ff"}

case class PlanLengthFilter(limit: Int) extends PruningTechnique {override val longInfo: String = "planLength(<=" + limit + ")"}

object RecomputeHierarchicalReachability extends PruningTechnique {override val longInfo: String = "recompute-htn-reachability"}

/**
  * all available flaw selectors
  */
sealed trait SearchFlawSelector extends DefaultLongInfo

object SearchFlawSelector {
  def parse(text: String): SearchFlawSelector = {
    val selectorSequence = ArgumentListParser.parse(text, { case (hName, hParameterMap) =>
      hName.toLowerCase match {
        case "lcfr"            => LCFR
        case "umcp" | "umcp-f" => UMCPFlaw
        case "random"          => RandomFlaw
      }
    })

    if (selectorSequence.length == 1) selectorSequence.head else SequentialSelector(selectorSequence: _*)
  }
}

object LCFR extends SearchFlawSelector {override val longInfo: String = "lcfr"}

object UMCPFlaw extends SearchFlawSelector {override val longInfo: String = "umcp-f"}

object RandomFlaw extends SearchFlawSelector {override val longInfo: String = "random"}

object FrontFlaw extends SearchFlawSelector {override val longInfo: String = "front-flaw"}

object NewestFlaw extends SearchFlawSelector {override val longInfo: String = "newest-flaw"}

object CausalThreat extends SearchFlawSelector {override val longInfo: String = "causal-threat first"}


case class SequentialSelector(sequence: SearchFlawSelector*) extends SearchFlawSelector {override val longInfo: String = sequence map { _.longInfo } mkString " -> "}

sealed trait SearchConfiguration extends Configuration {}

case class PlanBasedSearch(
                            nodeLimit: Option[Int],
                            searchAlgorithm: SearchAlgorithmType,
                            heuristic: Seq[SearchHeuristic],
                            pruningTechniques: Seq[PruningTechnique],
                            flawSelector: SearchFlawSelector,
                            efficientSearch: Boolean = true,
                            continueOnSolution: Boolean = false,
                            printSearchInfo: Boolean = true
                          ) extends SearchConfiguration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Plan-based Search Configuration\n-------------------------------\n" +
    alignConfig(("Node limit", nodeLimit.getOrElse("none")) ::
                  ("Search Algorithm", searchAlgorithm) ::
                  ("Heuristic", heuristic.map(_.longInfo).mkString(" -> ")) ::
                  ("Flaw selector", flawSelector.longInfo) ::
                  ("Pruning", pruningTechniques.map(_.longInfo).mkString(", ")) ::
                  ("Efficient search", efficientSearch) ::
                  ("Continue on solution", continueOnSolution) ::
                  ("Print search info", printSearchInfo) ::
                  Nil)


  override protected def localHelpTexts: Map[String, String] = super.localHelpTexts

  override protected def localModifications: Seq[(String, (Option[String]) => PlanBasedSearch.this.type)] =
    Seq(
         "-efficientSearch" -> { p => (if (p.isEmpty) this.copy(efficientSearch = true) else this.copy(efficientSearch = p.get.toBoolean)).asInstanceOf[this.type] },
         "-symbolicSearch" -> { p => assert(p.isEmpty); this.copy(efficientSearch = false).asInstanceOf[this.type] },

         "-continueOnSolution" -> { p => (if (p.isEmpty) this.copy(continueOnSolution = true) else this.copy(continueOnSolution = p.get.toBoolean)).asInstanceOf[this.type] },
         "-stopAtSoltuion" -> { p => assert(p.isEmpty); this.copy(continueOnSolution = false).asInstanceOf[this.type] },

         "-printSearchInfo" -> { p => (if (p.isEmpty) this.copy(printSearchInfo = true) else this.copy(printSearchInfo = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontPrintSearchInfo" -> { p => assert(p.isEmpty); this.copy(printSearchInfo = false).asInstanceOf[this.type] },

         "-search" -> { algo => assert(algo.isDefined); this.copy(searchAlgorithm = SearchAlgorithmType.parse(algo.get)).asInstanceOf[this.type] },

         "-heuristic" -> { h => assert(h.isDefined); this.copy(heuristic = SearchHeuristic.parse(h.get)).asInstanceOf[this.type] },
         "-h" -> { h => assert(h.isDefined); this.copy(heuristic = SearchHeuristic.parse(h.get)).asInstanceOf[this.type] },

         "-flaw" -> { f => assert(f.isDefined); this.copy(flawSelector = SearchFlawSelector.parse(f.get)).asInstanceOf[this.type] },
         "-f" -> { f => assert(f.isDefined); this.copy(flawSelector = SearchFlawSelector.parse(f.get)).asInstanceOf[this.type] },

         "-prune" -> { p => assert(p.isDefined); this.copy(pruningTechniques = PruningTechnique.parse(p.get)).asInstanceOf[this.type] }
       )
}

case class ProgressionSearch(searchAlgorithm: SearchAlgorithmType,
                             heuristic: Option[SearchHeuristic],
                             abstractTaskSelectionStrategy: PriorityQueueSearch.abstractTaskSelection,
                             deleteRelaxed: Boolean = false) extends SearchConfiguration {

  override protected def localModifications: Seq[(String, (Option[String]) => ProgressionSearch.this.type)] =
    Seq(
         "-search" -> { algo => assert(algo.isDefined); this.copy(searchAlgorithm = SearchAlgorithmType.parse(algo.get)).asInstanceOf[this.type] },
         "-heuristic" -> { h => assert(h.isDefined)
           val parsedHeuristics = SearchHeuristic.parse(h.get)
           assert(parsedHeuristics.length == 1)
           this.copy(heuristic = Some(parsedHeuristics.head)).asInstanceOf[this.type]
         },
         "-h" -> { h => assert(h.isDefined)
           val parsedHeuristics = SearchHeuristic.parse(h.get)
           assert(parsedHeuristics.length == 1)
           this.copy(heuristic = Some(parsedHeuristics.head)).asInstanceOf[this.type]
         },
         "-deleteRelaxed" -> { p => (if (p.isEmpty) this.copy(deleteRelaxed = true) else this.copy(deleteRelaxed = p.get.toBoolean)).asInstanceOf[this.type] },
         "-dontDeleteRelax" -> { p => assert(p.isEmpty); this.copy(deleteRelaxed = false).asInstanceOf[this.type] },
         "-abstractSelection" -> { p => assert(p.isDefined); this.copy(abstractTaskSelectionStrategy = PriorityQueueSearch.abstractTaskSelection.parse(p.get)).asInstanceOf[this.type] }
       )

  /** returns a detailed information about the object */
  override def longInfo: String = "Progression-search Configuration\n--------------------------------\n" +
    alignConfig(("Search Algorithm", searchAlgorithm) ::
                  ("Heuristic", if (heuristic.isDefined) heuristic.get.longInfo else "none") ::
                  ("Abstract task selection strategy", abstractTaskSelectionStrategy) ::
                  ("Delete-relaxed", deleteRelaxed) ::
                  Nil)

}

case class SATSearch(solverType: Solvertype,
                     maximumPlanLength: Int,
                     overrideK: Option[Int] = None,
                     checkResult: Boolean = false
                    ) extends SearchConfiguration {

  protected override def localModifications: Seq[(String, (Option[String] => this.type))] =
    Seq(
         "-planlength" -> { l => this.copy(maximumPlanLength = l.get.toInt).asInstanceOf[this.type] },
         "-overrideK" -> { l => this.copy(overrideK = Some(l.get.toInt)).asInstanceOf[this.type] },
         "-dontOverrideK" -> { l => assert(l.isEmpty); this.copy(overrideK = None).asInstanceOf[this.type] },
         "-checkResult" -> { l => this.copy(checkResult = true).asInstanceOf[this.type] },
         "-solver" -> { l =>
           val solver = l.get.toLowerCase match {
             case "minisat"       => MINISAT
             case "cryptominisat" => CRYPTOMINISAT
           }
           this.copy(solverType = solver).asInstanceOf[this.type]
         }
       )

  /** returns a detailed information about the object */
  override def longInfo: String = "SAT-Planning Configuration\n--------------------------\n" +
    alignConfig(("solver", solverType.longInfo) ::
                  ("maximum plan length", maximumPlanLength) ::
                  ("override K", overrideK.getOrElse("false")) ::
                  ("check result", checkResult) :: Nil)

}

object NoSearch extends SearchConfiguration {
  /** returns a detailed information about the object */
  override def longInfo: String = "No Search"
}

case class PostprocessingConfiguration(resultsToProduce: Set[ResultType]) extends Configuration {
  if (resultsToProduce contains AllFoundSolutionPathsWithHStar) assert(resultsToProduce contains SearchSpace,
                                                                       "If we have to produce paths to the solutions, we have to keep the search space")

  /** returns a detailed information about the object */
  override def longInfo: String = "Post-processing Configuration\n-----------------------------\n" + resultsToProduce.map({ _.longInfo }).mkString("\n")

  override protected def localModifications: Seq[(String, (Option[String]) => PostprocessingConfiguration.this.type)] =
    Seq(
         "-timings" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + ProcessingTimings).asInstanceOf[this.type] },
         "-outputStatus" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + SearchStatus).asInstanceOf[this.type] },
         "-outputPlan" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + SearchResult).asInstanceOf[this.type] },
         "-outputInternalPlan" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + InternalSearchResult).asInstanceOf[this.type] },
         "-outputAllPlans" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + AllFoundPlans).asInstanceOf[this.type] },
         "-outputAllPlansWithH*" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + AllFoundSolutionPathsWithHStar).asInstanceOf[this.type] },
         "-statistics" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + SearchStatistics).asInstanceOf[this.type] },
         "-searchspace" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + SearchSpace).asInstanceOf[this.type] },
         "-outputPlanInternal" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + SolutionInternalString).asInstanceOf[this.type] },
         "-planToDot" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + SolutionDotString).asInstanceOf[this.type] },
         "-finalDomainAndPlan" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + PreprocessedDomainAndPlan).asInstanceOf[this.type] },
         "-initialDomainAndPlan" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + UnprocessedDomainAndPlan).asInstanceOf[this.type] },
         "-finalTDG" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + FinalTaskDecompositionGraph).asInstanceOf[this.type] },
         "-finalReachability" -> { l => assert(l.isEmpty); this.copy(resultsToProduce = resultsToProduce + FinalGroundedReachability).asInstanceOf[this.type] }
       )
}

trait Configuration extends DefaultLongInfo {
  final lazy val optionStrings: Seq[String] = modifyOnOptionStringOrdered.map(_._1)

  protected def localModifications: Seq[(String, (Option[String] => this.type))] = Nil

  protected def localHelpTexts: Map[String, String] = Map()

  def potentialRecursiveChildren: Seq[Configuration] = Nil

  protected def recursiveMethods(conf: Configuration): (conf.type => this.type) = Map()

  protected final lazy val modifyOnOptionStringOrdered: Seq[(String, (Option[String] => this.type))] =
    localModifications ++ potentialRecursiveChildren.flatMap(
      { child => child.modifyOnOptionStringOrdered.map(
        { case (k, f) =>
          val function: (Option[String] => this.type) = {case p: Option[String] => recursiveMethods(child)(f(p))}
          k -> function
        })
      })

  final lazy val modifyOnOptionString: Map[String, (Option[String] => this.type)] = modifyOnOptionStringOrdered.toMap

  final lazy val helpTexts: Map[String, String] = localHelpTexts ++ potentialRecursiveChildren.flatMap(_.helpTexts)

  /** returns a detailed information about the object */
  override def longInfo: String = "-- literally nothing --"

  protected def alignConfig(configs: Seq[(String, Any)]): String = {
    val keyMaxLength = configs map { _._1.length } max

    configs map { case (k, v) => k + (Range(0, 1 + keyMaxLength - k.length) map { _ => " " }).mkString("") + ": " + v.toString } mkString "\n"
  }
}
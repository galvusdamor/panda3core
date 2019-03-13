// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.configuration

import java.io.InputStream
import java.lang.management.{ManagementFactory, MemoryPoolMXBean, MemoryType}
import java.util.UUID
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability.EfficientTDGFromGroundedSymbolic
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.{EfficientGroundedPlanningGraph, EfficientGroundedPlanningGraphFromSymbolic}
import de.uniulm.ki.panda3.efficient.heuristic.filter.{PlanLengthLimit, RecomputeHTN}
import de.uniulm.ki.panda3.efficient.heuristic.{AlwaysZeroHeuristic, EfficientNumberOfFlaws, EfficientNumberOfPlanSteps, _}
import de.uniulm.ki.panda3.efficient.search.flawSelector._
import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.gphRcFFMulticount
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic.SasHeuristics
import de.uniulm.ki.panda3.progression.htn.ProPlanningInstance
import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints._
import de.uniulm.ki.panda3.symbolic.compiler._
import de.uniulm.ki.panda3.symbolic.compiler.pruning._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, HierarchyTyping, SASPlusGrounding}
import de.uniulm.ki.panda3.symbolic.domain.updates.{AddPredicate, ExchangeTask, RemovePredicate}
import de.uniulm.ki.panda3.symbolic.htn2strips.HTN2STRIPS
import de.uniulm.ki.panda3.symbolic.logic.{And, GroundLiteral, Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.parser.FileTypeDetector
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.parser.hpddl.HPDDLParser
import de.uniulm.ki.panda3.symbolic.parser.oldpddl.OldPDDLParser
import de.uniulm.ki.panda3.symbolic.parser.xml.XMLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.plan.ordering.TaskOrdering
import de.uniulm.ki.panda3.symbolic.sat.verify._
import de.uniulm.ki.panda3.symbolic.plan.modification.InsertPlanStepWithLink
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.panda3.symbolic.writer.anml.ANMLWriter
import de.uniulm.ki.panda3.symbolic.writer.gtohp.GTOHPWriter
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter
import de.uniulm.ki.panda3.symbolic.writer.shop2.SHOP2Writer
import de.uniulm.ki.panda3.symbolic.writer.simplehddl.SimpleHDDLWriter
import de.uniulm.ki.panda3.{MainGregor, efficient, symbolic}
import de.uniulm.ki.util.{InformationCapsule, TimeCapsule}
import de.uniulm.ki.util._

import scala.collection.{JavaConversions, JavaConverters, Seq}
import scala.util.Random

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PlanningConfiguration(printGeneralInformation: Boolean, printAdditionalData: Boolean,
                                 randomSeed: Long = 42, timeLimit: Option[Int] = None,
                                 parsingConfiguration: ParsingConfiguration = ParsingConfiguration(),
                                 preprocessingConfiguration: PreprocessingConfiguration,
                                 searchConfiguration: SearchConfiguration,
                                 postprocessingConfiguration: PostprocessingConfiguration,
                                 externalProgramPaths: Map[ExternalProgram, String] = Map()) extends Configuration {

  searchConfiguration match {

    case search: PlanBasedSearch => assert(!(search.heuristic contains ADD) || preprocessingConfiguration.groundedReachability.contains(PlanningGraph) ||
                                             preprocessingConfiguration.groundedReachability.contains(PlanningGraphWithMutexes))
    case _                       => ()
  }

  lazy val timeLimitInMilliseconds: Option[Long] = timeLimit.map(_.toLong * 1000)

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
  //scalastyle:off method.length
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
      informationCapsule.set(Information.LAST_TASK_IN_METHODS, if (domainStructureAnalysis.hasLastTaskInAllMethods) "true" else "false")

      extra("Domain is acyclic: " + domainStructureAnalysis.isAcyclic + "\n")
      extra("Domain is mostly acyclic: " + domainStructureAnalysis.isMostlyAcyclic + "\n")
      extra("Domain is regular: " + domainStructureAnalysis.isRegular + "\n")
      extra("Domain is tail recursive: " + domainStructureAnalysis.isTailRecursive + "\n")
      extra("Domain is totally ordered: " + domainStructureAnalysis.isTotallyOrdered + "\n")
      extra("Domain has last task in all methods: " + domainStructureAnalysis.hasLastTaskInAllMethods + "\n")
    }

    //informationCapsule.set(Information.MINIMUM_DECOMPOSITION_HEIGHT, domainAndPlan._1.minimumDecompositionHeightToPrimitiveForPlan(domainAndPlan._2))


    // write domain statistics into the information capsule
    informationCapsule.set(Information.NUMBER_OF_CONSTANTS, domainAndPlan._1.constants.length)
    informationCapsule.set(Information.NUMBER_OF_PREDICATES, domainAndPlan._1.predicates.length)
    informationCapsule.set(Information.NUMBER_OF_ACTIONS, domainAndPlan._1.tasks.length)
    informationCapsule.set(Information.NUMBER_OF_ABSTRACT_ACTIONS, domainAndPlan._1.abstractTasks.length)
    informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_ACTIONS, domainAndPlan._1.primitiveTasks.length)
    informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_SHOP_ACTIONS, domainAndPlan._1.numberOfPrimitiveSHOPTasks)
    informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_NON_SHOP_ACTIONS, domainAndPlan._1.primitiveTasks.length - domainAndPlan._1.numberOfPrimitiveSHOPTasks)
    informationCapsule.set(Information.NUMBER_OF_METHODS, domainAndPlan._1.decompositionMethods.length)
    informationCapsule.set(Information.SIZE_OF_LARGEST_METHOD, domainAndPlan._1.maximumMethodSize)
    informationCapsule.set(Information.AVERAGE_METHOD_SIZE, domainAndPlan._1.averageMethodSize)

    // write randomseed into the info capsule
    informationCapsule.set(Information.RANDOM_SEED, randomSeed.toString)

    // determine show much time I have left
    val remainingTime: Long = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
    println("Time remaining for planner " + remainingTime + "ms")


    searchConfiguration match {
      case search: PlanBasedSearch        =>
        // some heuristics need additional preprocessing, e.g. to build datastructures they need
        timeCapsule start HEURISTICS_PREPARATION
        // TDG based heuristics need the TDG
        if (search.heuristic.exists { case x: TDGBasedHeuristic => true; case _ => false }) if (!(analysisMap contains SymbolicGroundedTaskDecompositionGraph)) {
          timeCapsule start GROUNDED_TDG_ANALYSIS
          val _tempResult = runGroundedTaskDecompositionGraph(domainAndPlan._1, domainAndPlan._2, analysisMap, preprocessingConfiguration.groundedTaskDecompositionGraph.get)
          analysisMap = _tempResult._1
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
            case ADD | ADDReusing | TDGMinimumADD(_) | LiftedTDGMinimumADD(_, _) => true
            case f: TDGBasedHeuristic                                            =>
              f.innerHeuristic.exists({ case ADD | ADDReusing | TDGMinimumADD(_) | LiftedTDGMinimumADD(_, _) => true; case _ => false })
            case _                                                               => false
          }
          val efficientRelaxPGNeeded = search.heuristic exists {
            case Relax                => true
            case f: TDGBasedHeuristic =>
              f.innerHeuristic.exists({ case Relax => true; case _ => false })
            case _                    => false
          }

          if (efficientPGNeeded || efficientRelaxPGNeeded) {
            // do the whole preparation, i.e. planning graph
            val initialState = domainAndPlan._2.groundedInitialState filter { _.isPositive } toSet
            val pgConfig = GroundedPlanningGraphConfiguration(computeMutexes = preprocessingConfiguration.groundedReachability.contains(PlanningGraphWithMutexes))
            val pgConfigRelax = GroundedPlanningGraphConfiguration(isSerial = true)

            val symbolicPlanningGraph = GroundedPlanningGraph(domainAndPlan._1, initialState, pgConfig)
            val symbolicPlanningGraphRelax = GroundedPlanningGraph(domainAndPlan._1, initialState, pgConfigRelax)

            if (efficientPGNeeded) {
              println("Creating efficient PG for heuristic ... ")
              analysisMap = analysisMap + (EfficientGroundedPlanningGraph, EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraph, wrapper))
            }
            if (efficientRelaxPGNeeded) {
              println("Creating efficient serial PG for Relax heuristic ... ")
              analysisMap = analysisMap + (EfficientGroundedPlanningGraphForRelax, EfficientGroundedPlanningGraphFromSymbolic(symbolicPlanningGraphRelax, wrapper))
            }
          }
          timeCapsule stop HEURISTICS_PREPARATION


          val flawSelector = search.flawSelector match {
            case LCFR                  => LeastCostFlawRepair
            case CausalThreat          => CausalThreatSelector
            case FrontFlaw             => FrontFlawFirst
            case NewestFlaw            => NewestFlawFirst
            case RandomFlaw            => RandomFlawSelector(new Random(randomSeed))
            case UMCPFlaw              => UMCPFlawSelection
            case OneModFlaw            => OneModFlawSelector
            case OpenPrecFlaw          => OCFlawSelector
            case AbstractTaskFlaw      => AbstractTaskFlawSelector
            case s: SequentialSelector =>
              val subSelectorArray = s.sequence map {
                case LCFR             => LeastCostFlawRepair
                case RandomFlaw       => RandomFlawSelector(new Random(randomSeed))
                case CausalThreat     => CausalThreatSelector
                case FrontFlaw        => FrontFlawFirst
                case NewestFlaw       => NewestFlawFirst
                case OneModFlaw       => OneModFlawSelector
                case OpenPrecFlaw     => OCFlawSelector
                case AbstractTaskFlaw => AbstractTaskFlawSelector
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

        val progressionInstance = new ProPlanningInstance()
        val groundMethods = domainAndPlan._1.methodsForAbstractTasks map { case (at, ms) =>
          at -> JavaConversions.setAsJavaSet(ms collect { case s: SimpleDecompositionMethod => s } toSet)
        }


        // scalastyle:off null
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
          val primitiveSolution = progressionInstance.plan(domainAndPlan._1, domainAndPlan._2, JavaConversions.mapAsJavaMap(groundMethods),
                                                           informationCapsule, timeCapsule,
                                                           progression.abstractTaskSelectionStrategy,
                                                           progression.heuristic.getOrElse(null),
                                                           progression.searchAlgorithm,
                                                           externalProgramPaths.getOrElse(PANDASEARCHENGINE, ""),
                                                           randomSeed,
                                                           timeLimit.getOrElse(Int.MaxValue).toLong * 1000)

          timeCapsule stop TOTAL_TIME

          // build a plan from the primitive solution
          val foundPlan = if (primitiveSolution == null) Nil else Plan.sequentialPlan(primitiveSolution) :: Nil

          runPostProcessing(timeCapsule, informationCapsule, null, foundPlan, domainAndPlan, unprocessedDomain, analysisMap)
        })

      case satSearch: SATSearch                          =>
        // set the at-most-one encoding to the one chosen in the input
        AtMostOneType.chosenType = satSearch.atMostOneEncodingMethod

        // create the partial int-representation that might be needed by an exists-step based encoding
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>

          val combinedFormula = (satSearch.ltlFormula, domainAndPlan._2.ltlConstraint) match {
            case (None, LTLTrue)    => None
            case (Some(x), LTLTrue) => Some(x)
            case (None, x)          => Some(x)
            case (Some(x), y)       => Some(LTLAnd(x :: y :: Nil))
          }

          val separatedFormulaeBeforeRandomSelection = combinedFormula match {
            case None          => Nil
            case Some(formula) =>
              val formulaInNNF = formula.nnf.parseAndGround(domainAndPlan._1, domainAndPlanFullyParsed._1, Map()).nnf.simplify
              formulaInNNF match {
                case LTLAnd(conj) => conj
                case x            => x :: Nil
              }
          }

          val rand = new Random(randomSeed)

          val separatedFormulae: Seq[LTLFormula] = separatedFormulaeBeforeRandomSelection
          /*if (separatedFormulaeBeforeRandomSelection.length <= 3) separatedFormulaeBeforeRandomSelection else {
            Range(0, 3).foldLeft[(Seq[LTLFormula], Seq[LTLFormula])]((separatedFormulaeBeforeRandomSelection, Nil))(
              { case ((l, sel), _) =>
                val r = rand.nextInt(l.length)
                (l.patch(r, Nil, 1), sel :+ l(r))
              }
                                                                                                                   )._2
          }*/


          if (separatedFormulae.nonEmpty) {
            //domainAndPlan._2.ltlConstraint match {case LTLAnd(x) => println(x map { _.longInfo } mkString "\n")}

            //println(domainAndPlan._1.predicates map { "\t" + _.name } mkString "\n")

            println("LTL Requirements: ")
            println(separatedFormulae map { "\t" + _.longInfo } mkString "\n")
          }

          // if a formula is provided, translate it into a Büchi automaton
          val automaton: Seq[LTLAutomaton[_, _]] = satSearch.formulaEncoding match {
            case AlternatingAutomatonEncoding | BüchiEncoding =>
              val automata: Seq[LTLAutomaton[_, _]] = separatedFormulae map { f =>
                satSearch.formulaEncoding match {
                  case BüchiEncoding                => BüchiAutomaton(domainAndPlan._1, f)
                  case AlternatingAutomatonEncoding => AlternatingAutomaton(domainAndPlan._1, f)
                }
              }

              separatedFormulae.zip(automata).zipWithIndex foreach { case ((f, a), i) =>
                info("Using LTL Formula in NNF: " + f.longInfo + "\n")
                Dot2PdfCompiler.writeDotToFile(a, "ltl_automaton" + i + ".pdf")
              }

              //System exit 0
              automata
            case _                                            => Nil
          }

          val directLTLEncoding: Seq[AdditionalSATConstraint] = satSearch.formulaEncoding match {
            case AlternatingAutomatonEncoding | BüchiEncoding => Nil
            case MattmüllerEncoding                           => separatedFormulae.zipWithIndex map { case (f, i) => LTLMattmüllerEncoding(f, "matt_" + i, improvedChains = false) }
            case MattmüllerImprovedEncoding                   => separatedFormulae.zipWithIndex map { case (f, i) => LTLMattmüllerEncoding(f, "matt_" + i, improvedChains = true) }
            case OnParallelEncoding                           => separatedFormulae.zipWithIndex map { case (f, i) => LTLOnParallelEncoding(f, "onparallel_" + i) }
          }

          val additionalEdgesInDisablingGraph: Seq[AdditionalEdgesInDisablingGraph] = (directLTLEncoding collect { case x: AdditionalEdgesInDisablingGraph => x }) ++
            (automaton collect { case x: AlternatingAutomaton => AlternatingAutomatonFormulaEncoding(x, "none") })
          val withRelevantPredicates: Seq[WithRelevantPredicates] = directLTLEncoding collect { case x: WithRelevantPredicates => x }

          val intProblem = IntProblem(domainAndPlan._1, domainAndPlan._2, additionalEdgesInDisablingGraph, withRelevantPredicates)

          val referencePlan: Option[Seq[Task]] = satSearch.planToMinimiseDistanceTo map { _ map { taskName: String => domainAndPlan._1.tasks.find(_.name == taskName).get } }

          val runner = SATRunner(domainAndPlan._1, domainAndPlan._2, intProblem, satSearch.solverType, externalProgramPaths.get(satSearch.solverType),
                                 automaton, directLTLEncoding, separatedFormulae,
                                 referencePlan, satSearch.planDistanceMetric,
                                 satSearch.reductionMethod, satSearch.usePDTMutexes,
                                 timeCapsule, informationCapsule, satSearch.encodingToUse,
                                 postprocessingConfiguration.resultsToProduce.contains(SearchResultWithDecompositionTree),
                                 randomSeed, satSearch.threads)


          // depending on whether we are doing a single or a full run, we have either to do a loop or just one run
          val (solution, error) = satSearch.runConfiguration match {
            case SingleSATRun(maximumPlanLength, overrideK) =>
              runner.runWithTimeLimit(remainingTime, remainingTime, maximumPlanLength, 0, defineK = overrideK, checkSolution = satSearch.checkResult) match {case (a, b, c) => (a, b)}
            case FullSATRun()                               =>
              // start with K = 0 and loop
              var solution: Option[(Seq[PlanStep], Map[PlanStep, DecompositionMethod], Map[PlanStep, (PlanStep, PlanStep)])] = None
              var solutionK: Int = -1
              var error: Boolean = false
              print("Computing minimum decomposition height: ")
              var currentK = if (domainAndPlan._2.planStepSchemaArray.isEmpty) 0 else
                domainAndPlan._2.planStepSchemaArray map domainAndPlan._1.minimumDecompositionHeightToPrimitive max

              println(currentK)
              var remainingTime: Long = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
              var usedTime: Long = remainingTime //(remainingTime / Math.max(1, 20.0 / (currentK + 1))).toLong
            var expansion: Boolean = true
              while ( /*(*/ solution.isEmpty /*|| (solution.isDefined && currentK < solutionK + 6))*/ && !error && expansion && usedTime > 0) {
                println("\nRunning SAT search with K = " + currentK)
                //println("Time remaining for SAT search " + remainingTime + "ms")
                //println("Time used for this run " + usedTime + "ms\n\n")

                val (satResult, satError, expansionPossible) = runner.runWithTimeLimit(usedTime, remainingTime, if (domainAndPlan._1.isClassical) Math.pow(2, currentK).toInt else -1,
                                                                                       0, defineK = Some(currentK), checkSolution = satSearch.checkResult)
                //println("ERROR " + satError)
                error |= satError
                if (solution.isEmpty && satResult.isDefined) solutionK = currentK
                solution = satResult
                expansion = expansionPossible

                if (domainAndPlan._1.isClassical) currentK += 1
                else currentK += 1

                remainingTime = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
                usedTime = remainingTime // (remainingTime / Math.max(1, 20.0 / (currentK + 1))).toLong
              }

              informationCapsule.set(Information.ACTUAL_K, solutionK)

              (solution, false)

            case FullLengthSATRun(optimise) =>

              //VerifyEncoding.computeTheoreticalK(domainAndPlan._1,domainAndPlan._2,1)
              //System exit 0

              //////////////////////////////////////////////// 1. step run a satisficing search
              // start with K = 0 and loop
              var solution: Option[(Seq[PlanStep], Map[PlanStep, DecompositionMethod], Map[PlanStep, (PlanStep, PlanStep)])] = None
              var solutionK: Int = -1
              var error: Boolean = false
              var currentK = if (domainAndPlan._2.planStepSchemaArray.isEmpty) 0 else
                domainAndPlan._2.planStepSchemaArray map domainAndPlan._1.minimumDecompositionHeightToPrimitive max
              var remainingTime: Long = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
              var usedTime: Long = remainingTime
              var expansion: Boolean = true
              while (solution.isEmpty && !error && expansion && usedTime > 0) {
                println("\nRunning SAT search with K = " + currentK)
                //println("Time remaining for SAT search " + remainingTime + "ms")
                //println("Time used for this run " + usedTime + "ms\n\n")

                val (satResult, satError, expansionPossible) = runner.runWithTimeLimit(usedTime, remainingTime, if (domainAndPlan._1.isClassical) Math.pow(2, currentK).toInt else -1,
                                                                                       0, defineK = Some(currentK), checkSolution = satSearch.checkResult)
                //println("ERROR " + satError)
                error |= satError
                if (solution.isEmpty && satResult.isDefined) solutionK = currentK
                solution = satResult
                expansion = expansionPossible

                if (domainAndPlan._1.isClassical) currentK += 1
                else currentK += 1

                remainingTime = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
                usedTime = remainingTime // (remainingTime / Math.max(1, 20.0 / (currentK + 1))).toLong
              }

              informationCapsule.set(Information.ACTUAL_K, solutionK)

              var betterPossible = true
              var upperBound = -1
              var lowerBound = -1

              if (solution.nonEmpty) {
                //println(solution.get._1 map {ps => ps.schema.name + " " + ps.schema.cost} mkString("\n"))
                upperBound = solution.get._1.map(ps => ps.schema.cost match {case ConstantActionCost(x) => x}).sum
                lowerBound = VerifyEncoding.lowerBoundOnNonPlanExistence(domainAndPlan._1, domainAndPlan._2, currentK - 1)
                ////////////////////////////////////////////// 2. step optimise
                println("Starting cost optimisation, using binary search = " + optimise)
                println("=====================================================================")
                println("  upper bound = " + upperBound)
                println("  lower bound = " + lowerBound)

                while (((!optimise && betterPossible) || (optimise && upperBound > lowerBound + 1)) && !error && usedTime > 0) {
                  val lengthToCheck = if (optimise) (upperBound + lowerBound) / 2 else upperBound - 1

                  println("\nRunning SAT search with cost bound = " + lengthToCheck)
                  println("==================================================")
                  println("  upper bound = " + upperBound)
                  println("  lower bound = " + lowerBound)
                  //println("Time remaining for SAT search " + remainingTime + "ms")
                  //println("Time used for this run " + usedTime + "ms\n\n")

                  val (satResult, satError, expansionPossible) = runner.runWithTimeLimit(usedTime, remainingTime, lengthToCheck, 0, defineK = None, checkSolution = satSearch.checkResult)
                  //println("ERROR " + satError)
                  error |= satError

                  remainingTime = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
                  usedTime = remainingTime // (remainingTime / Math.max(1, 20.0 / (currentK + 1))).toLong

                  if (satResult.isDefined) {
                    solution = satResult
                    if (!optimise) {
                      upperBound -= 1
                    } else {
                      upperBound = lengthToCheck
                    }
                  } else {
                    if (!optimise)
                      betterPossible = false
                    else lowerBound = lengthToCheck
                  }
                }
              }

              // I could not prove optimality
              if ((!optimise && betterPossible) || (optimise && upperBound > lowerBound + 1) || error || usedTime < 0 || solution.isEmpty) (None, false) else {
                informationCapsule.set(Information.SOLUTION_LENGTH, solution.get._1.length)
                (solution, false)
              }

            case OptimalSATRun(overrideK) if satSearch.encodingToUse == POCLDirectEncoding || satSearch.encodingToUse == POCLDeleteEncoding =>
              // TODO: 1 is just a placeholder for "generate the base formula"
              runner.runWithTimeLimit(remainingTime, remainingTime, 1, 0, defineK = overrideK, checkSolution = satSearch.checkResult, runOptimiser = true) match {case (a, b, c) => (a, b)}

            case OptimalSATRun(overrideK) =>
              // start with K = 0 and loop
              var solution: Option[(Seq[PlanStep], Map[PlanStep, DecompositionMethod], Map[PlanStep, (PlanStep, PlanStep)])] = None
              var error: Boolean = false
              var currentLength = 1
              var remainingTime: Long = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
              var usedTime: Long = remainingTime
              while (solution.isEmpty && !error && usedTime > 0 && domainAndPlan._1.primitiveTasks.nonEmpty) {
                println("\nRunning SAT search with length = " + currentLength)
                println("Time remaining for SAT search " + remainingTime + "ms")
                println("Time used for this run " + usedTime + "ms\n\n")

                val (satResult, satError, expansionPossible) = runner.runWithTimeLimit(usedTime, remainingTime, currentLength, 0, defineK = overrideK, checkSolution = satSearch.checkResult)
                println("ERROR " + satError)

                error |= satError
                solution = satResult
                currentLength += 1
                remainingTime = timeLimitInMilliseconds.getOrElse(Long.MaxValue) - timeCapsule.getCurrentElapsedTimeInThread(TOTAL_TIME)
                usedTime = remainingTime / Math.max(1, 10 / (currentLength + 1))
              }

              (solution, false)
          }

          informationCapsule.set(Information.SOLVED, if (solution.isDefined) "true" else "false")
          informationCapsule.set(Information.TIMEOUT, if (error) "true" else "false")

          timeCapsule stop TOTAL_TIME
          val potentialPlan = solution match {
            case Some((planSteps, appliedDecompositions, parentsInDecompositionTree)) =>

              Plan(planSteps, domainAndPlan._2.init.schema, domainAndPlan._2.goal.schema, appliedDecompositions, parentsInDecompositionTree) :: Nil

            case None => Nil
          }
          runPostProcessing(timeCapsule, informationCapsule, null, potentialPlan, domainAndPlan, unprocessedDomain, analysisMap)
        })
      case SATPlanVerification(solverType, planToVerity) =>
        assert(domainAndPlan._1.isGround)
        val planStepStrings: Seq[String] = planToVerity.split(";")
        // check whether the given plan steps are contained in the grounding
        val notContainedPSs = planStepStrings filterNot { t => domainAndPlan._1.tasks.exists(_.name == t) }

        if (notContainedPSs.nonEmpty) {
          println("Plan to verify contains actions " + notContainedPSs.mkString(", ") + " which are not delete-relaxed reachable.")
          (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
            informationCapsule.set(Information.PLAN_VERIFIED, "false")
            runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
          })
        } else {

          val taskSequenceToVerify: Seq[Task] = planStepStrings map { t => domainAndPlan._1.tasks.find(_.name == t).get }

          (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
            val runner = VerifyRunner(domainAndPlan._1, domainAndPlan._2, solverType)

            val (isSolution, runCompleted) = runner.runWithTimeLimit(remainingTime, taskSequenceToVerify, 0, includeGoal = true, None, timeCapsule, informationCapsule)

            informationCapsule.set(Information.PLAN_VERIFIED, isSolution.toString)
            runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
          })
        }

      case HTN2STRIPSSearch =>
        val length = MainGregor.length
        val depthBound = VerifyEncoding.computeTheoreticalK(domainAndPlan._1, domainAndPlan._2, length)

        println("depth: " + depthBound)
        val pb = HTN2STRIPS.computeProgressionBoundForDepth(domainAndPlan._1, domainAndPlan._2, depthBound)
        //val pb = HTN2STRIPS.computeProgressionBoundForDepth(domainAndPlan._1,domainAndPlan._2,100)
        println("pb: " + pb)


        System exit 0
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
          timeCapsule stop TOTAL_TIME
          runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
        })
      case FAPESearch       =>

        val notTranslatable = domainAndPlan._1.taskSchemaTransitionGraph.stronglyConnectedComponents exists { _.size > 1 }
        if (notTranslatable) {
          println("TSTG contains non-self recursion ... can't translate into anything FAPE would understand ... ")
          // return nothing meaningful
          (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
            timeCapsule stop TOTAL_TIME
            runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
          })
        } else {
          val writer = ANMLWriter

          val domainString = writer.writeDomain(domainAndPlan._1)
          val problemString = writer.writeProblem(domainAndPlan._1, domainAndPlan._2)

          // check for unsolvability in principle

          val uuid = UUID.randomUUID().toString
          writeStringToFile(domainString, "foo" + uuid + ".dom.anml")
          writeStringToFile(problemString, "foo" + uuid + ".0.pb.anml")


          val result = {
            import sys.process._
            // run FAPE
            var output: String = ""

            timeCapsule start SEARCH_FAPE

            //("java -Xmx10G -Xms10G -jar " + externalProgramPaths(FAPE) + "/fape-planning-assembly-1.0.jar -t 600 foo.0.pb.anml") #| "grep iter, --after 1" ! new ProcessLogger {
            ("java -Xmx4G -Xms4G -XX:+UseSerialGC -jar " + externalProgramPaths(FAPE) + "/fape-planning-assembly-1.0.jar -t 600 foo" + uuid + ".0.pb.anml") ! new ProcessLogger {
              //("java -Xmx1G -Xms1G -jar " + externalProgramPaths(FAPE) + "/fape-planning-assembly-1.0.jar -t 600 foo" + uuid + ".0.pb.anml") ! new ProcessLogger {
              override def err(s: => String): Unit = output = output + s + "\n"

              override def out(s: => String): Unit = output = output + s + "\n"

              override def buffer[T](f: => T): T = f
            }

            ("rm foo" + uuid + ".dom.anml") !

            ("rm foo" + uuid + ".0.pb.anml") !

            timeCapsule stop SEARCH_FAPE

            output
          }
          val timeout = "TIMEOUT"
          val unsol = "INFEASIBLE"

          println(result)

          //System exit 0

          if (result.contains(timeout) || result.contains(unsol) || !result.contains("=== Actions ===")) {
            // return nothing meaningful
            (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
              timeCapsule stop TOTAL_TIME
              runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
            })
          } else {
            // return something signifying a solution
            (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
              timeCapsule stop TOTAL_TIME
              runPostProcessing(timeCapsule, informationCapsule, null, null :: Nil, domainAndPlan, unprocessedDomain, analysisMap)
            })

          }
        }


      case SHOP2Search =>
        val writer = SHOP2Writer

        val domainString = writer.writeDomain(domainAndPlan._1)
        val problemString = writer.writeProblem(domainAndPlan._1, domainAndPlan._2)

        // check for unsolvability in principle

        val uuid = UUID.randomUUID().toString
        writeStringToFile(domainString, "fooD" + uuid)
        writeStringToFile(problemString, "fooP" + uuid)
        //System exit 0
        writeStringToFile("#!/bin/bash\n\n" +
                            "mkdir .shop" + uuid + "\n" +
                            "cd .shop" + uuid + "\n" +
                            "java -jar " + externalProgramPaths(SHOP2) + " ../fooD" + uuid + "\n" +
                            "java -jar " + externalProgramPaths(SHOP2) + " -r ../fooP" + uuid + "\n" +
                            "javac -cp " + externalProgramPaths(SHOP2) + " *java\n" +
                            "rm *java\n" +
                            "echo starting SHOP\n" +
                            "java -Xmx4G -Xms4G -Xss4G -XX:+UseSerialGC -cp " + externalProgramPaths(SHOP2) + ":. problem\n" +
                            //"java -Xmx1G -Xms1G -Xss1G -XX:+UseSerialGC -cp " + externalProgramPaths(SHOP2) + ":. problem\n" +
                            "rm *class", "run" + uuid + ".sh"
                         )

        val result = {
          import sys.process._
          // run SHOP2
          var output: String = ""

          timeCapsule start SEARCH_SHOP

          ("timeout " + remainingTime / 1000 + "s bash run" + uuid + ".sh") ! new ProcessLogger {
            override def err(s: => String): Unit = output = output + s + "\n"

            override def out(s: => String): Unit = output = output + s + "\n"

            override def buffer[T](f: => T): T = f
          }

          ("rm fooD" + uuid) !

          ("rm fooP" + uuid) !

          ("rm run" + uuid + ".sh") !

          ("rm -rf .shop" + uuid) !

          timeCapsule stop SEARCH_SHOP

          output
        }
        val timeout = "TIMEOUT"
        val unsol = "INFEASIBLE"

        println(result)

        if (!(result contains "1 plan(s) were found:")) {
          // return nothing meaningful
          (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
            timeCapsule stop TOTAL_TIME
            runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
          })
        } else {
          // return something signifying a solution
          (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
            timeCapsule stop TOTAL_TIME
            runPostProcessing(timeCapsule, informationCapsule, null, null :: Nil, domainAndPlan, unprocessedDomain, analysisMap)
          })

        }


      case GTOHPSearch =>
        val gtohpWriter = GTOHPWriter("dom", "prob")
        val domainString = gtohpWriter.writeDomain(domainAndPlan._1)
        val problemString = gtohpWriter.writeProblem(domainAndPlan._1, domainAndPlan._2)


        //println(problemString.split("\n").zipWithIndex.map({ case (s, i) => i + ": " + s }).mkString("\n"))
        //println(domainString.split("\n").zipWithIndex.map({ case (s, i) => i + ": " + s }).mkString("\n"))

        val uuid = UUID.randomUUID().toString
        val domFile = "fooD" + uuid + ".pddl"
        val probFile = "fooP" + uuid + ".pddl"
        writeStringToFile(domainString, domFile)
        writeStringToFile(problemString, probFile)

        //System exit 0

        val result = {
          import sys.process._
          // run SHOP2
          var output: String = ""

          timeCapsule start SEARCH_SHOP

          ("timeout " + remainingTime / 1000 + "s java -jar " + externalProgramPaths(GTOHP) + " -e gtohp -d " + domFile + " -p " + probFile) ! new ProcessLogger {
            override def err(s: => String): Unit = output = output + s + "\n"

            override def out(s: => String): Unit = output = output + s + "\n"

            override def buffer[T](f: => T): T = f
          }

          //("rm " + domFile) !

          //("rm " + probFile) !

          timeCapsule stop SEARCH_SHOP

          output
        }

        println(result)

        System exit 0


        // TODO return result
        (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
          timeCapsule stop TOTAL_TIME
          runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
        })
      case NoSearch    => (domainAndPlan._1, null, null, null, informationCapsule, { _ =>
        timeCapsule stop TOTAL_TIME
        runPostProcessing(timeCapsule, informationCapsule, null, Nil, domainAndPlan, unprocessedDomain, analysisMap)
      })
    }
  }


  private def constructEfficientHeuristic(heuristicConfig: SearchHeuristic, wrapper: Wrapping, analysisMap: AnalysisMap, domainAndPlan: (Domain, Plan)): EfficientHeuristic[_] = {
    // if we need the ADD heuristic as a building block create it
    val optionADD = heuristicConfig match {
      case LiftedTDGMinimumADD(_, _) | TDGMinimumADD(_) =>
        // TODO experimental
        val efficientPlanningGraph = analysisMap(EfficientGroundedPlanningGraph)
        val initialState = domainAndPlan._2.groundedInitialState collect {
          case GroundLiteral(task, true, args) =>
            (wrapper.unwrap(task), args map wrapper.unwrap toArray)
        }
        // TODO check that we have compiled negative preconditions away
        Some(AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, initialState.toArray, resuingAsVHPOP = false))
      case _                                            => None
    }


    // get the inner heuristic if one exists
    val innerHeuristic = heuristicConfig match {
      case inner: SearchHeuristicWithInner => inner.innerHeuristic map {
        h => constructEfficientHeuristic(h, wrapper, analysisMap, domainAndPlan)
      }
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
      case UMCPBFSHeuristic          => EfficientUMCPBFHeuristic
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
        val efficientPlanningGraph: EfficientGroundedPlanningGraph =
          analysisMap(if (heuristicConfig == Relax) EfficientGroundedPlanningGraphForRelax else EfficientGroundedPlanningGraph).asInstanceOf[EfficientGroundedPlanningGraph]
        val initialState = domainAndPlan._2.groundedInitialState collect {
          case GroundLiteral(task, true, args) =>
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

      case POCLTransformation(innerHeuristic) => POCLTransformationHeuristic(innerHeuristic, wrapper.efficientDomain,
                                                                             wrapper.efficientDomain.tasks(wrapper.unwrap(wrapper.initialPlan).planStepTasks(0)))
    }
  }

  def runPostProcessing(timeCapsule: TimeCapsule, informationCapsule: InformationCapsule, rootNode: SearchNode, result: Seq[Plan], domainAndPlan: (Domain, Plan),
                        unprocessedDomainAndPlan: (Domain, Plan),
                        analysisMap: AnalysisMap): ResultMap =
    ResultMap(postprocessingConfiguration.resultsToProduce map { resultType =>
      (resultType, resultType match {
        case ProcessingTimings => timeCapsule

        case SearchStatus                                     =>
          val determinedSearchSate =
            if (informationCapsule.dataMap().contains(Information.ERROR)) SearchState.INSEARCH
            else if (result.nonEmpty) SearchState.SOLUTION
            else if (timeCapsule.integralDataMap().contains(Timings.TOTAL_TIME) && timeCapsule.integralDataMap()(Timings.TOTAL_TIME) >=
              1000 * timeLimit.getOrElse(Integer.MAX_VALUE / 1000))
              SearchState.TIMEOUT
            else {
              searchConfiguration match {
                case search: PlanBasedSearch   => if (informationCapsule.dataMap().contains(Information.SEARCH_SPACE_FULLY_EXPLORED)) SearchState.UNSOLVABLE else SearchState.INSEARCH
                case search: ProgressionSearch => if (informationCapsule.dataMap().contains(Information.SEARCH_SPACE_FULLY_EXPLORED)) SearchState.UNSOLVABLE else SearchState.INSEARCH
                case sat: SATSearch            => SearchState.UNSOLVABLE
                case _                         => SearchState.INSEARCH
              }
            }
          // write search state into the information capsule
          informationCapsule.set(Information.SOLVED_STATE, determinedSearchSate.toString)

          determinedSearchSate
        case InternalSearchResult                             => result.headOption
        case SearchResult | SearchResultWithDecompositionTree =>
          // start process of translating the solution back to something readable (i.e. lifted)
          result.headOption
        case SearchResultInVerificationFormat                 => result.headOption.map({ p =>
          p.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive } map { ps => ps.schema.name } mkString ";"
                                                                                       }).getOrElse("")
        case AllFoundPlans                                    => result
        case SearchStatistics                                 =>
          // write memory info
          val pools: Seq[MemoryPoolMXBean] = JavaConverters.asScalaBuffer(ManagementFactory.getMemoryPoolMXBeans)
          val heapMemory = pools collect { case memoryPoolMXBean if memoryPoolMXBean.getType == MemoryType.HEAP => memoryPoolMXBean.getPeakUsage.getUsed } sum

          informationCapsule.set(Information.PEAKMEMORY, heapMemory.toString)

          informationCapsule
        case SearchSpace                                      => rootNode
        case SolutionInternalString                           => if (result.nonEmpty) Some(result.head.shortInfo) else None
        case SolutionDotString                                => if (result.nonEmpty) Some(result.head.dotString) else None
        case FinalTaskDecompositionGraph                      => analysisMap(SymbolicGroundedTaskDecompositionGraph)
        case FinalGroundedReachability                        => analysisMap(SymbolicGroundedReachability)
        case PreprocessedDomainAndPlan                        => domainAndPlan
        case UnprocessedDomainAndPlan                         => unprocessedDomainAndPlan
        case AllFoundSolutionPathsWithHStar                   =>
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

    timeCapsule start PARSER_INHERIT_PRECONDITIONS
    val inheritedConditions = if (parsingConfiguration.inheritPreconditions) InheritMethodPreconditions.transform(noHybrid, ()) else noHybrid
    timeCapsule stop PARSER_INHERIT_PRECONDITIONS

    timeCapsule start PARSER_SHOP_METHODS
    val simpleMethod = if (parsingConfiguration.compileSHOPMethods) SHOPMethodCompiler.transform(inheritedConditions, ()) else inheritedConditions
    timeCapsule stop PARSER_SHOP_METHODS

    timeCapsule start PARSER_FLATTEN_FORMULA
    val flattened = if (parsingConfiguration.reduceGneralTasks) ReduceGeneralTasks.transform(simpleMethod, ()) else simpleMethod
    timeCapsule stop PARSER_FLATTEN_FORMULA

    timeCapsule start PARSER_CWA
    val cwaApplied =
      if (parsingConfiguration.closedWorldAssumption)
        ClosedWorldAssumption.transform(flattened, (true, protectedPredicatesFromConfiguration ++ flattened._2.ltlConstraint.allPredicatesNames))
      else flattened
    timeCapsule stop PARSER_CWA

    timeCapsule start PARSER_ELIMINATE_EQUALITY
    val identity = if (parsingConfiguration.eliminateEquality) RemoveIdenticalVariables.transform(cwaApplied, ()) else cwaApplied
    timeCapsule stop PARSER_ELIMINATE_EQUALITY

    timeCapsule stop PARSING
    info("done.\n")
    (identity, noHybrid, timeCapsule)
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

  private def runGroundedPlanningGraph(domain: Domain, problem: Plan, useMutexes: Boolean, analysisMap: AnalysisMap, typing: HierarchyTyping): AnalysisMap = {
    val groundedInitialState = problem.groundedInitialStateOnlyPositive filter { _.isPositive }
    val chosenTyping = if (problem.isModificationAllowed(InsertPlanStepWithLink(null, null, null, null))) None else Some(typing)
    val config = GroundedPlanningGraphConfiguration(computeMutexes = useMutexes, hierarchyTyping = chosenTyping, debuggingMode = DebuggingMode.Disabled)
    val groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis = GroundedPlanningGraph(domain, groundedInitialState.toSet, config)
    // add analysis to map
    analysisMap + (SymbolicGroundedReachability -> groundedReachabilityAnalysis)
  }

  private def runGroundedTaskDecompositionGraph(domain: Domain, problem: Plan, analysisMap: AnalysisMap, tdgType: TDGGeneration): (AnalysisMap, Option[String]) = {
    val groundedReachabilityAnalysis =
      if (analysisMap contains SymbolicGroundedReachability) analysisMap(SymbolicGroundedReachability)
      else EverythingIsReachable(domain, problem.groundedInitialState.toSet)

    var message = ""

    def messageFunction(mess: String): Unit = message = mess

    // on a grounded domain, always use the top down TDG
    val tdg =
      if (domain.isGround) TopDownTaskDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true, messageFunction)
      else tdgType match {
        case NaiveTDG    => NaiveGroundedTaskDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true, messageFunction)
        case TopDownTDG  => TopDownTaskDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true, messageFunction)
        case BottomUpTDG => TwoStepDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true, omitTopDownStep = true, messageFunction)
        case TwoWayTDG   => TwoStepDecompositionGraph(domain, problem, groundedReachabilityAnalysis, prunePrimitive = true, omitTopDownStep = false, messageFunction)
      }
    //System exit 0

    (analysisMap + (SymbolicGroundedTaskDecompositionGraph -> tdg), if (message != "") Some(message) else None)
  }

  private def createEfficientTDGFromSymbolic(wrapping: Wrapping, analysisMap: AnalysisMap): AnalysisMap = {
    val groundedTDG = analysisMap(SymbolicGroundedTaskDecompositionGraph)
    val tdg = EfficientTDGFromGroundedSymbolic(groundedTDG, wrapping)

    analysisMap + (EfficientGroundedTDG -> tdg)
  }


  private def runReachabilityAnalyses(domain: Domain, problem: Plan, runForGrounder: Boolean, timeCapsule: TimeCapsule = new TimeCapsule(),
                                      firstAnalysis: Boolean = false, savedSASPlusParser: Option[SasPlusProblem] = None): (((Domain, Plan), AnalysisMap), TimeCapsule) = {
    val emptyAnalysis = AnalysisMap(Map())

    assert(problem.planStepsAndRemovedPlanStepsWithoutInitGoal forall { domain.tasks contains _.schema })


    timeCapsule start REMOVING_UNNECESSARY_PREDICATES
    val predicatesRemoved = if (domain.isGround && preprocessingConfiguration.removeUnnecessaryPredicates) {
      info("Removing unnecessary predicates ... ")
      val compiled = PrunePredicates.transform(domain, problem, protectedPredicatesFromConfiguration ++ problem.ltlConstraint.allPredicatesNames)
      info("done.\n")
      extra(compiled._1.statisticsString + "\n")
      compiled
    } else (domain, problem)
    timeCapsule stop REMOVING_UNNECESSARY_PREDICATES

    assert(predicatesRemoved._2.planStepsAndRemovedPlanStepsWithoutInitGoal forall { predicatesRemoved._1.tasks contains _.schema })

    // lifted reachability analysis
    timeCapsule start LIFTED_REACHABILITY_ANALYSIS
    // only run lifted reachability if domain is actually lifted ...
    if (preprocessingConfiguration.liftedReachability && predicatesRemoved._1.isGround) info("Omitting lifted reachability analysis ... \n")
    val liftedResult = if (preprocessingConfiguration.liftedReachability && !predicatesRemoved._1.isGround) {
      info("Lifted reachability analysis and domain cleanup ... ")
      val newAnalysisMap = runLiftedForwardSearchReachabilityAnalysis(predicatesRemoved._1, predicatesRemoved._2, emptyAnalysis)
      val reachable = newAnalysisMap(SymbolicLiftedReachability).reachableLiftedPrimitiveActions.toSet
      val disallowedTasks = domain.primitiveTasks filterNot reachable.contains
      val hierarchyPruned = PruneHierarchy.transform(predicatesRemoved._1, predicatesRemoved._2, disallowedTasks.toSet)
      val pruned = PruneEffects.transform(hierarchyPruned, (domain.primitiveTasks.toSet, protectedPredicatesFromConfiguration ++ problem.ltlConstraint.allPredicatesNames))
      info("done.\n")
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else (predicatesRemoved, emptyAnalysis)
    timeCapsule stop LIFTED_REACHABILITY_ANALYSIS

    assert(liftedResult._1._2.planStepsAndRemovedPlanStepsWithoutInitGoal forall {
      liftedResult._1._1.tasks contains _.schema
    })

    // convert to SAS+
    val sasPlusResult = if (preprocessingConfiguration.convertToSASP && firstAnalysis &&
      (!liftedResult._1._1.containEitherType || !preprocessingConfiguration.allowSASPFromStrips)) {
      import sys.process._

      info("Converting to SAS+ ... ")
      val sasStart = System.currentTimeMillis()

      // 1. step write pddl part of the domain to file
      val classicalDomain = liftedResult._1._1.classicalDomain

      val artificialGoal = Predicate("__goal", Nil)
      val goalLiteral = Literal(artificialGoal, true, Nil)

      val pddlDomain = Domain(classicalDomain.sorts, classicalDomain.predicates :+ artificialGoal,
                              classicalDomain.tasks map {
                                case rt: ReducedTask => rt.copy(effect = And(rt.effect.conjuncts :+ goalLiteral))
                              }, Nil, Nil, ???, None, None)

      val withGoalPlan = {
        val oldGoal = liftedResult._1._2.goal.schema match {
          case rt: ReducedTask => rt
        }
        val newGoal = oldGoal.copy(precondition = And(oldGoal.precondition.conjuncts :+ goalLiteral))

        liftedResult._1._2.update(ExchangeTask(Map(oldGoal -> newGoal)))
      }

      val pddlPlan = Plan(withGoalPlan.initAndGoal, Nil, TaskOrdering(OrderingConstraint(withGoalPlan.init, withGoalPlan.goal) :: Nil, withGoalPlan.initAndGoal),
                          withGoalPlan.variableConstraints, withGoalPlan.init, withGoalPlan.goal, withGoalPlan.isModificationAllowed, withGoalPlan.isFlawAllowed, Map(), Map(),
                          false, LTLTrue)

      // FD can't handle either in the sort hierarchy, so we have to use predicates when writing them ...
      val uuid = UUID.randomUUID().toString

      System.getProperty("os.name") match {
        case osname if osname.toLowerCase startsWith "windows" =>
          ("cmd.exe /c mkdir .fd" + uuid) !! // create directory
        case _                                                 => // OSes made by people who can think straight
          ("mkdir .fd" + uuid) !! // create directory
      }

      val separator = System.getProperty("os.name") match {
        case osname if osname.toLowerCase startsWith "windows" => "\\"
        case _                                                 => "/" // normal OSes
      }

      writeStringToFile(HDDLWriter("tosasp", "tosasp01").writeDomain(pddlDomain, includeAllConstants = false, writeEitherWithPredicates = true, noConstantReplacement = false),
                        ".fd" + uuid + separator + "__sasdomain.pddl")
      writeStringToFile(HDDLWriter("tosasp", "tosasp01").writeProblem(pddlDomain, pddlPlan, writeEitherWithPredicates = true),
                        ".fd" + uuid + separator + "__sasproblem.pddl")

      val sasPlusParser = {
        // we need a path to FD
        assert(externalProgramPaths contains FastDownward, "no path to fast downward is specified")
        val fdPath = externalProgramPaths(FastDownward)

        val path = if (fdPath.startsWith(separator) || fdPath.contains(":")) fdPath else ".." + separator + fdPath

        //System exit 0


        System.getProperty("os.name") match {
          case osname if osname.toLowerCase startsWith "windows" =>
            writeStringToFile("cd .fd" + uuid + "\n" +
                                "python " + path + "\\src\\translate\\translate.py __sasdomain.pddl __sasproblem.pddl", "runFD" + uuid + ".bat")
            ("cmd.exe /c  runFD" + uuid + ".bat") !!

          case _ => // OSes made by people who can think straight
            writeStringToFile("#!/bin/bash\n" +
                                "cd .fd" + uuid + "\n" +
                                "python " + path + "/src/translate/translate.py __sasdomain.pddl __sasproblem.pddl", "runFD" + uuid + ".sh")

            ("bash runFD" + uuid + ".sh") !!
          // semantic empty line
        }


        // semantic empty line
        val sasreader = new SasPlusProblem(".fd" + uuid + separator + "output.sas")
        sasreader.prepareEfficientRep()
        //ProPlanningInstance.sasp = sasreader
        //sasreader.prepareSymbolicRep(domain,problem)

        System.getProperty("os.name") match {
          case osname if osname.toLowerCase startsWith "windows" =>
            ("cmd.exe /c  rmdir /S /Q .fd" + uuid) !

            ("cmd.exe /c del runFD" + uuid + ".bat") !!
          case _                                                 => // Linux and the like
            ("rm -rf .fd" + uuid) !

            ("rm runFD" + uuid + ".sh") !!
          // semantic empty line
        }
        SASPlusGrounding(liftedResult._1._1, liftedResult._1._2, sasreader)
      }
      val newAnalysisMap = liftedResult._2 + (SASPInput -> sasPlusParser) + (SymbolicGroundedReachability -> sasPlusParser)
      val reachable =
        if (newAnalysisMap.contains(SymbolicLiftedReachability)) newAnalysisMap(SymbolicLiftedReachability).reachableLiftedPrimitiveActions.toSet else domain.primitiveTasks.toSet
      val disallowedTasks = domain.primitiveTasks filterNot reachable.contains
      val hierarchyPruned = PruneHierarchy.transform(domain, problem: Plan, disallowedTasks.toSet)
      val pruned = PruneEffects.transform(hierarchyPruned, (domain.primitiveTasks.toSet, protectedPredicatesFromConfiguration ++ problem.ltlConstraint.allPredicatesNames))

      info("done (" + (System.currentTimeMillis() - sasStart) + " ms).\n")
      info("\tNumber of Grounded Actions " + sasPlusParser.reachableGroundPrimitiveActions.length + "\n")
      extra(pruned._1.statisticsString + "\n")

      // for now
      (pruned, newAnalysisMap)
    } else {
      val newAnalysisMap = savedSASPlusParser match {
        case Some(sasreader) =>
          // we have to adapt the parser to the new domain since action descriptions might have changed
          val parser = SASPlusGrounding(liftedResult._1._1, liftedResult._1._2, sasreader)
          liftedResult._2 + (SASPInput -> parser) + (SymbolicGroundedReachability -> parser)
        case None            => liftedResult._2
      }

      (liftedResult._1, newAnalysisMap)
    }


    // grounded reachability analysis
    if (preprocessingConfiguration.groundedReachability.isDefined) timeCapsule start GROUNDED_PLANNINGGRAPH_ANALYSIS
    val groundedResult = if (preprocessingConfiguration.groundedReachability.isDefined ||
      (preprocessingConfiguration.convertToSASP && sasPlusResult._1._1.containEitherType && preprocessingConfiguration.allowSASPFromStrips)) {

      // output info text
      val (infoText, actualType) = preprocessingConfiguration.groundedReachability match {
        case Some(NaiveGroundedReachability)     => ("Naive grounded reachability analysis", NaiveGroundedReachability)
        case Some(PlanningGraph)                 => ("Grounded planning graph", PlanningGraph)
        case Some(PlanningGraphWithMutexes)      => ("Grounded planning graph with mutexes", PlanningGraphWithMutexes)
        case Some(IntegerPlanningGraph)          => ("Planning graph with integer representation", IntegerPlanningGraph)
        case Some(NativeGroundingImplementation) => ("Native planning graph", NativeGroundingImplementation)
        case None                                => ("SAS+ fall-back: Grounded planning graph", PlanningGraph)
      }
      info(infoText + " ... ")
      //System.in.read()

      // run typing
      val typing = HierarchyTyping(sasPlusResult._1._1, sasPlusResult._1._2)
      typing.initialise()

      // run the actual analysis
      val newAnalysisMap =
        actualType match {
          case NaiveGroundedReachability                => runGroundedForwardSearchReachabilityAnalysis(sasPlusResult._1._1, sasPlusResult._1._2, sasPlusResult._2)
          case PlanningGraph | PlanningGraphWithMutexes =>
            val useMutexes = actualType match {
              case PlanningGraph            => false
              case PlanningGraphWithMutexes => true
            }
            val x = runGroundedPlanningGraph(sasPlusResult._1._1, sasPlusResult._1._2, useMutexes, sasPlusResult._2, typing)
            /*/*if (firstAnalysis) {
              println("Writing File")
              writeStringToFile(x(SymbolicGroundedReachability).reachableGroundPrimitiveActions.map(_.shortInfo).mkString("\n"), "allPrimAct.txt")
              println("File written")
            }*/

            val compiledDomain = ReplaceInitialPlanByTop(sasPlusResult._1._1, sasPlusResult._1._2, ())

            // interface for the C++ implementation of PG and TDG
            val string = SimpleHDDLWriter.writeProblem(compiledDomain._1, compiledDomain._2)

            println("\n========================\n")
            //println(string)
            val actions = x(SymbolicGroundedReachability).reachableGroundPrimitiveActions.distinct
            val literals = x(SymbolicGroundedReachability).reachableGroundLiterals.filter(_.isPositive).distinct

            val stringBuilder = new StringBuilder
            stringBuilder.append(actions.length + " " + literals.length + "\n")
            actions.map({ case GroundTask(t, args) => t.name + " " + args.map(_.name).mkString(" ") + "\n" }).sorted.foreach(stringBuilder.append)
            literals.map({ case GroundLiteral(l, s, args) => l.name + " " + args.map(_.name).mkString(" ") + "\n"}).sorted.foreach(stringBuilder.append)

            //println(string)
            //println(stringBuilder.toString())

            val uuid = UUID.randomUUID().toString
            writeStringToFile(string, "instance_" + uuid + ".in")
            writeStringToFile(stringBuilder.toString(), "instance_" + uuid + ".ans")

            println("UUID: " + uuid)



            System exit 0*/

            x
          case IntegerPlanningGraph                     =>
            /*val wrapper = Wrapping(sasPlusResult._1)
            val pgConfig = EFGPGConfiguration(serial = false, computeMutexes = false, new Array(0), new Array(0))
            val initialState = wrapper.unwrap(wrapper.initialPlan).groundInitialState
            println("StartPG")
            val pg = EfficientGroundedPlanningGraphImplementation(wrapper.efficientDomain, initialState, pgConfig)
            println("Done")
            println(pg.factSpikeIDs mkString "\n")*/
            System exit 0
            null

          case NativeGroundingImplementation =>
            val compiledDomain = ReplaceInitialPlanByTop(sasPlusResult._1._1, sasPlusResult._1._2, ())

            // interface for the C++ implementation of PG and TDG
            val string = SimpleHDDLWriter.writeProblem(compiledDomain._1, compiledDomain._2)

            println("\n========================\n")
            println(string)

            System exit 0
            null
        }

      val time0 = System.currentTimeMillis()
      val reachable = newAnalysisMap(SymbolicGroundedReachability).reachableLiftedPrimitiveActions.toSet
      val disallowedTasks = sasPlusResult._1._1.primitiveTasks filterNot reachable.contains
      val pruned = PruneHierarchy.transform(sasPlusResult._1._1, sasPlusResult._1._2, disallowedTasks.toSet)
      val time1 = System.currentTimeMillis()
      info("done in " + (time1 - time0) + "ms.\n")
      info("\tNumber of Grounded Actions " + newAnalysisMap(SymbolicGroundedReachability).reachableGroundPrimitiveActions.length + "\n")
      info("\tNumber of Grounded Literals " + newAnalysisMap(SymbolicGroundedReachability).reachableGroundLiterals.filter(_.isPositive).length + "\n")

      //println(newAnalysisMap(SymbolicGroundedReachability).reachableGroundPrimitiveActions.groupBy(_.task.name) map {case (n,l) => n -> l.size } mkString "\n")
      //println(newAnalysisMap(SymbolicGroundedReachability).reachableGroundLiterals.filter(_.isPositive).map(_.longInfo).mkString("\n"))

      //System exit 0
      extra(pruned._1.statisticsString + "\n")
      (pruned, newAnalysisMap)
    } else sasPlusResult
    if (preprocessingConfiguration.groundedReachability.isDefined) timeCapsule stop GROUNDED_PLANNINGGRAPH_ANALYSIS

    assert(groundedResult._1._2.planStepsAndRemovedPlanStepsWithoutInitGoal forall {
      groundedResult._1._1.tasks contains _.schema
    })

    // naive task decomposition graph
    timeCapsule start GROUNDED_TDG_ANALYSIS
    val tdgResult = if (preprocessingConfiguration.groundedTaskDecompositionGraph.isDefined) {
      val actualConfig = preprocessingConfiguration.groundedTaskDecompositionGraph.get
      info(actualConfig.toString + " ... ")
      // get the reachability analysis, if there is none, just use the trivial one
      val (newAnalysisMap, message) = runGroundedTaskDecompositionGraph(groundedResult._1._1, groundedResult._1._2, groundedResult._2, actualConfig)
      val methodsPruned = PruneDecompositionMethods.transform(groundedResult._1._1, groundedResult._1._2, newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedMethods)
      val removedTasks = groundedResult._1._1.tasks.toSet diff newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedActions.toSet
      val tasksPruned = PruneHierarchy.transform(methodsPruned._1, methodsPruned._2, removedTasks)

      //val pruned = PruneDecompositionMethods.transform(groundedResult._1._1, groundedResult._1._2, newAnalysisMap(SymbolicGroundedTaskDecompositionGraph).reachableLiftedMethods)
      info("done.\n")
      message match {
        case Some(m) => info(m + "\n")
        case None    =>
      }
      extra(tasksPruned._1.statisticsString + "\n")
      (tasksPruned, newAnalysisMap)
    } else groundedResult
    timeCapsule stop GROUNDED_TDG_ANALYSIS

    assert(tdgResult._1._2.planStepsAndRemovedPlanStepsWithoutInitGoal forall { tdgResult._1._1.tasks contains _.schema })

    val groundedCompilersToBeApplied: Seq[CompilerConfiguration[_]] =
      if (!runForGrounder || !preprocessingConfiguration.groundDomain) (if (preprocessingConfiguration.compileUselessAbstractTasks)
        CompilerConfiguration(RemoveChoicelessAbstractTasks, (), "expand choiceless abstract tasks", USELESS_CHOICELESS_TASKS) :: Nil
      else Nil) ::
        //(CompilerConfiguration(PruneUselessAbstractTasks, (), "abstract tasks without methods", USELESS_ABSTRACT_TASKS) :: Nil) ::
        // this one has to be the last
        (if (preprocessingConfiguration.compileInitialPlan)
          CompilerConfiguration(ReplaceInitialPlanByTop, (), "initial plan", TOP_TASK) :: Nil
        else Nil) ::
        (if (preprocessingConfiguration.removeNoOps)
          CompilerConfiguration(PruneNoops, (), "remove no-ops", REMOVE_NOOPS) :: Nil
        else Nil) ::
        (if (searchConfiguration match {case SHOP2Search => true; case _ => false})
          CompilerConfiguration(CompileGoalIntoAction, (), "goal", TOP_TASK) :: CompilerConfiguration(ForceGroundedInitTop, (), "force top", TOP_TASK) :: Nil
        else Nil) ::
        Nil flatten
      else Nil

    // don't run compilation if we are still ground
    val compiledResult = groundedCompilersToBeApplied.foldLeft(tdgResult._1)({
                                                                               case ((dom, prob), cc@CompilerConfiguration(compiler, option, message, timingString)) =>
                                                                                 timeCapsule start timingString
                                                                                 info("Compiling " + message + " ... ")
                                                                                 val compiled = cc.run(dom, prob)
                                                                                 info("done.\n")
                                                                                 extra(compiled._1.statisticsString + "\n")
                                                                                 timeCapsule stop timingString
                                                                                 compiled
                                                                             })

    if (!preprocessingConfiguration.iterateReachabilityAnalysis || compiledResult._1.tasks.length == domain.tasks.length ||
      (compiledResult._1.abstractTasks.nonEmpty && compiledResult._1.decompositionMethods.isEmpty) || !compiledResult._1.isGround) // if we are still lifted, don't iterate the reachability
      ((compiledResult, tdgResult._2), timeCapsule)
    else runReachabilityAnalyses(compiledResult._1, compiledResult._2, runForGrounder, timeCapsule, firstAnalysis = false,
                                 savedSASPlusParser = tdgResult._2.getOrElse(SASPInput) map { _.sasPlusProblem })
  }

  private case class CompilerConfiguration[T](domainTransformer: DomainTransformer[T], information: T, name: String, timingName: String) {
    def run(domain: Domain, plan: Plan) = domainTransformer.transform(domain, plan, information)
  }

  def runPreprocessing(domain: Domain, problem: Plan, timeCapsule: TimeCapsule = new TimeCapsule()): (((Domain, Plan), AnalysisMap), TimeCapsule) = {
    // start the timer
    timeCapsule start PREPROCESSING
    extra("Initial domain\n" + domain.statisticsString + "\n")


    val compilerToBeApplied: Seq[CompilerConfiguration[_]] =
      (if ((preprocessingConfiguration.compileNegativePreconditions && !preprocessingConfiguration.convertToSASP) ||
        (preprocessingConfiguration.convertToSASP && domain.containEitherType && preprocessingConfiguration.allowSASPFromStrips))
      // don't do this if we are going to use SAS+ conversion
        CompilerConfiguration(RemoveNegativePreconditions, (), "negative preconditions", COMPILE_NEGATIVE_PRECONFITIONS) :: Nil
      else Nil) ::
        (if (preprocessingConfiguration.compileOrderInMethods.isDefined)
          CompilerConfiguration(TotallyOrderAllMethods, preprocessingConfiguration.compileOrderInMethods.get, "order in methods", COMPILE_ORDER_IN_METHODS) :: Nil
        else Nil) ::
        (if (preprocessingConfiguration.splitIndependentParameters)
          CompilerConfiguration(SplitIndependentParameters, (), "split parameters", SPLIT_PARAMETERS) :: Nil
        else Nil) ::
        Nil flatten

    val (compiledDomain, compiledProblem) = compilerToBeApplied.foldLeft((domain, problem))({
                                                                                              case ((dom, prob), cc@CompilerConfiguration(compiler, option, message, timingString)) =>
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
    val ((domainAndPlan, analysisMap), _) = runReachabilityAnalyses(compiledDomain, compiledProblem, runForGrounder = true, timeCapsule, firstAnalysis = true)

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

      val groundingResult: (Domain, Plan) = {
        if (preprocessingConfiguration.convertToSASP && (!domainAndPlan._1.containEitherType || !preprocessingConfiguration.allowSASPFromStrips)) {
          val sasPlusGrounder: SASPlusGrounding = analysisMap(SASPInput)
          // since we grounded the domain every analysis we performed so far becomes invalid
          val groundedDomainAndProblem = Grounding.transform(domainAndPlan, (tdg, sasPlusGrounder.tasksWithMultipleGroundings))

          assert(groundedDomainAndProblem._1.mappingToOriginalGrounding.isDefined)
          val flatTaskToGroundedTask = groundedDomainAndProblem._1.mappingToOriginalGrounding.get.taskMapping

          val exchangeMap = flatTaskToGroundedTask collect {
            case (currentTask, groundTask) if sasPlusGrounder.groundedTasksToNewGroundTasksMapping contains(groundTask.task.name, groundTask.arguments) =>
              currentTask -> sasPlusGrounder.groundedTasksToNewGroundTasksMapping((groundTask.task.name, groundTask.arguments))
          }

          val sasPlusDomain = groundedDomainAndProblem._1.update(AddPredicate(sasPlusGrounder.sasPlusPredicates)).update(ExchangeTask(exchangeMap)).
            update(RemovePredicate(groundedDomainAndProblem._1.predicates.toSet))
          val sasPlusPlan = groundedDomainAndProblem._2.update(ExchangeTask(exchangeMap))

          sasPlusPlan.init.schema.addEffectsAsPredicate foreach { p => assert(sasPlusDomain.predicateSet.contains(p), "Effect " + p.name + " of init is not a valid predicate.") }


          // as we have done the TDG grounding after the SAS+ process, there might be tasks in mapping that have already been pruned
          val saspRepresentation = SASPlusRepresentation(sasPlusGrounder.sasPlusProblem,
                                                         sasPlusGrounder.sasPlusTaskIndexToNewGroundTask filter { case (i, t) => sasPlusDomain.taskSet contains t },
                                                         sasPlusGrounder.sasPlusPredicates.zipWithIndex map { case (p, i) => i -> p } toMap)

          (sasPlusDomain.copy(sasPlusRepresentation = Some(saspRepresentation)), sasPlusPlan)
        } else {
          val groundedDomainAndProblem = Grounding.transform(domainAndPlan, (tdg, Map())) // since we grounded the domain every analysis we performed so far becomes invalid
          // generate simple SAS+ representation from strips
          val (saspProblem, sasOperatorOrdering, sasPredicateOrdering) = SasPlusProblem.generateFromSTRIPS(groundedDomainAndProblem._1, groundedDomainAndProblem._2)
          val saspRepresentation = SASPlusRepresentation(saspProblem,
                                                         sasOperatorOrdering.zipWithIndex map { case (t, i) => i -> t } toMap,
                                                         sasPredicateOrdering.zipWithIndex map { case (p, i) => i -> p } toMap
                                                        )
          (groundedDomainAndProblem._1.copy(sasPlusRepresentation = Some(saspRepresentation)), groundedDomainAndProblem._2)
        }
      }

      // if we are doing a plan verification curtail the model here, i.e. remove all unreachable primitive tasks

      val result = searchConfiguration match {
        case SATPlanVerification(_, plan) =>
          val planActions = plan.split(";")
          val primitivesNotOccurringInPlan = groundingResult._1.primitiveTasks filterNot { t => groundingResult._2.planStepTasksSet.contains(t) || planActions.contains(t.name) }
          PruneHierarchy.transform(groundingResult, primitivesNotOccurringInPlan.toSet)
        case _                            => groundingResult
      }

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

      val ((groundedDomainAndPlan, groundedAnalysisMap), _) =
        if (!preprocessingConfiguration.stopDirectlyAfterGrounding) runReachabilityAnalyses(unitMethodsCompiled._1, unitMethodsCompiled._2, runForGrounder = false, timeCapsule)
        else ((unitMethodsCompiled, AnalysisMap(Map())), null)

      timeCapsule start COMPILE_METHODS_WITH_IDENTICAL_TASKS
      val methodsWithIdenticalTasks = if (
        searchConfiguration.isInstanceOf[SATSearch] && postprocessingConfiguration.resultsToProduce.contains(SearchResultWithDecompositionTree) && !groundedDomainAndPlan._1.isClassical) {
        info("Compiling methods with identical tasks ... ")
        val compiled = MakeTasksInMethodsUnique.transform(groundedDomainAndPlan._1, groundedDomainAndPlan._2, ())
        info("done.\n")
        extra(compiled._1.statisticsString + "\n")
        compiled
      } else groundedDomainAndPlan
      timeCapsule stop COMPILE_METHODS_WITH_IDENTICAL_TASKS

      timeCapsule start REMOVING_UNNECESSARY_PREDICATES
      val predicatedPruned = if (preprocessingConfiguration.removeUnnecessaryPredicates) {
        info("Removing unnecessary predicates ... ")
        val compiled = PrunePredicates.transform(methodsWithIdenticalTasks._1, methodsWithIdenticalTasks._2,
                                                 protectedPredicatesFromConfiguration ++ methodsWithIdenticalTasks._2.ltlConstraint.allPredicatesNames)
        info("done.\n")
        extra(compiled._1.statisticsString + "\n")
        compiled
      } else methodsWithIdenticalTasks
      timeCapsule stop REMOVING_UNNECESSARY_PREDICATES

      val lastCompilersToBeApplied = (if (preprocessingConfiguration.ensureMethodsHaveAtMostTwoTasks)
        CompilerConfiguration(TwoTaskPerMethod, (), "force two tasks per method", TOP_TASK) :: Nil
      else Nil) ::
        (if (preprocessingConfiguration.removeNoOps)
          CompilerConfiguration(PruneNoops, (), "remove no-ops", REMOVE_NOOPS) :: Nil
        else Nil) ::
        (if (preprocessingConfiguration.ensureMethodsHaveLastTask)
          CompilerConfiguration(EnsureEveryMethodHasLastTask, (), "ensure last task", LAST_TASK) :: Nil
        else Nil) ::
        Nil flatten


      // don't run compilation if we are still ground
      val compiledResult = lastCompilersToBeApplied.foldLeft(predicatedPruned)(
        { case ((dom, prob), cc@CompilerConfiguration(compiler, option, message, timingString)) =>
          timeCapsule start timingString
          info("Compiling " + message + " ... ")
          val compiled = cc.run(dom, prob)
          info("done.\n")
          extra(compiled._1.statisticsString + "\n")
          timeCapsule stop timingString
          compiled
        })

      val noopCount = compiledResult._1.primitiveTasks.count(_.isNoOp)
      println("Tasks " + compiledResult._1.primitiveTasks.length + " - " + noopCount)


      //System exit 0


      timeCapsule stop PREPROCESSING
      ((compiledResult, groundedAnalysisMap), timeCapsule)
    } else {
      timeCapsule stop PREPROCESSING
      ((domainAndPlan, analysisMap), timeCapsule)
    }
  }

  private def info(s: String): Unit = if (printGeneralInformation) print(s)

  private def extra(s: String): Unit = if (printAdditionalData) print(s)


  /** returns a detailed information about the object */
  override def longInfo: String = "Planning Configuration\n======================\n" +
    alignConfig(("\tprintGeneralInformation", printGeneralInformation) :: ("\tprintAdditionalData", printAdditionalData) ::
                  ("\trandom seed", randomSeed) :: ("\ttime limit (in seconds)", timeLimit.getOrElse("none")) :: Nil) + "\n\n" +
    "\texternal programs:\n" + alignConfig(externalProgramPaths.toSeq map { case (prog, path) => ("\t\t" + prog.longInfo, path) }) + "\n\n" + {
    parsingConfiguration.longInfo + "\n\n" + preprocessingConfiguration.longInfo + "\n\n" + searchConfiguration.longInfo + "\n\n" + postprocessingConfiguration.longInfo
  }.split("\n").map(x => "\t" + x).mkString("\n")

  import PlanningConfiguration._

  protected override def localModifications: Seq[(String, (ParameterMode, (Option[String]) => PlanningConfiguration.this.type))] = localModificationsByKey ++ predefinedConfigurations

  protected def localModificationsByKey: Seq[(String, (ParameterMode, (Option[String]) => PlanningConfiguration.this.type))] =
    Seq(


         "-printGeneralInfo" -> (NoParameter, { l: Option[String] => this.copy(printGeneralInformation = true).asInstanceOf[this.type] }),
         "-noGeneralInfo" -> (NoParameter, { l: Option[String] => this.copy(printGeneralInformation = false).asInstanceOf[this.type] }),
         "-printDomainInfo" -> (NoParameter, { l: Option[String] => this.copy(printAdditionalData = true).asInstanceOf[this.type] }),
         "-noDomainInfo" -> (NoParameter, { l: Option[String] => this.copy(printAdditionalData = false).asInstanceOf[this.type] }),

         "-noProblemSolving" -> (NoParameter, { l: Option[String] => this.copy(searchConfiguration = NoSearch).asInstanceOf[this.type] }),
         "-planningProcedure" -> (NecessaryParameter, { l: Option[String] =>
           l.get.toLowerCase match {
             case "panda3"      => this.copy(searchConfiguration = defaultPlanSearchConfiguration).asInstanceOf[this.type]
             case "progression" => this.copy(searchConfiguration = defaultProgressionConfiguration).asInstanceOf[this.type]
             case "sat"         => this.copy(searchConfiguration = defaultPlanSearchConfiguration).asInstanceOf[this.type]
           }
         }),

         "-seed" -> (NecessaryParameter, { l: Option[String] => this.copy(randomSeed = l.get.toInt).asInstanceOf[this.type] }),
         "-timelimit" -> (NecessaryParameter, { l: Option[String] => this.copy(timeLimit = Some(l.get.toInt)).asInstanceOf[this.type] }),

         // paths to external programs
         "-programPath" -> (NecessaryParameter, { l: Option[String] =>
           val splittedPath = l.get.split("=")
           assert(splittedPath.length == 2, "paths must be specified in the program=path format")
           val program = splittedPath.head match {
             case "fd" | "fast-downward" | "fastdownward" => FastDownward
             case "riss6"                                 => RISS6
             case "mapleCOMSPS"                           => MapleCOMSPS
             case "FAPE"                                  => FAPE
             case "SHOP2"                                 => SHOP2
             case "minisat"                               => MINISAT
             case "cryptominisat"                         => CRYPTOMINISAT
             case "cryptominisat55"                       => CRYPTOMINISAT55
             case "MapleLCMDistChronoBT"                  => MapleLCMDistChronoBT
             case "Maple_LCM_Scavel"                      => Maple_LCM_Scavel
             case "expMC_LRB_VSIDS_Switch_2500"           => ExpMC_LRB_VSIDS_Switch_2500
             case "ReasonLS"                              => ReasonLS
             case "CaDiCaL"                               => CADICAL
             case "searchEngine" | "searchengine"         => PANDASEARCHENGINE
           }
           this.copy(externalProgramPaths = externalProgramPaths.+((program, splittedPath(1)))).asInstanceOf[this.type]
         })
       )

  def predefinedConfigurations: Seq[(String, (ParameterMode, (Option[String]) => PlanningConfiguration.this.type))] =
    Seq(
         ("-systemConfig", (NecessaryParameter, {
           l: Option[String] =>
             val key = l.get

             val newConf: PlanningConfiguration.this.type = if (PredefinedConfigurations.parsingConfigs.contains(key))
               this.copy(parsingConfiguration = PredefinedConfigurations.parsingConfigs(key)).asInstanceOf[this.type]
             else if (PredefinedConfigurations.preprocessConfigs.contains(key))
               this.copy(preprocessingConfiguration = PredefinedConfigurations.preprocessConfigs(key)).asInstanceOf[this.type]
             else if (PredefinedConfigurations.defaultConfigurations.contains(key)) {
               val (parse, pre, search) = PredefinedConfigurations.defaultConfigurations(key)
               this.copy(parsingConfiguration = parse,
                         preprocessingConfiguration = pre,
                         searchConfiguration = search).asInstanceOf[this.type]
             }
             else {
               println("Unknown system configuration \"" + key + "\". Most likely, you have either a typeO in here, or you forgot to specify a non-optional parameter."
                         + " Use \"-help\" followed by the option/key for details.")
               System exit 0
               this // unreachable
             }

             newConf
         }))
       )


  override def potentialRecursiveChildren: Seq[Configuration] = {
    val allDefaultConfigurations: Seq[SearchConfiguration] =
      defaultPlanSearchConfiguration ::
        defaultProgressionConfiguration ::
        defaultSATConfiguration ::
        defaultVerifyConfiguration ::
        NoSearch ::
        FAPESearch ::
        SHOP2Search :: Nil


    (allDefaultConfigurations filterNot { _.isInstanceOf[searchConfiguration.type] }) ++
      (parsingConfiguration :: preprocessingConfiguration :: searchConfiguration :: postprocessingConfiguration :: Nil)
  }

  protected override def recursiveMethods(conf: Configuration): (conf.type) => PlanningConfiguration.this.type = conf match {
    case _: ParsingConfiguration        => {
      case p: ParsingConfiguration => this.copy(parsingConfiguration = p).asInstanceOf[this.type]
    }
    case _: PreprocessingConfiguration  => {
      case p: PreprocessingConfiguration => this.copy(preprocessingConfiguration = p).asInstanceOf[this.type]
    }
    case _: SearchConfiguration         => {
      case p: SearchConfiguration => this.copy(searchConfiguration = p).asInstanceOf[this.type]
    }
    case _: PostprocessingConfiguration => {
      case p: PostprocessingConfiguration => this.copy(postprocessingConfiguration = p).asInstanceOf[this.type]
    }
  }


  def checkConfigurationIntegrity(): Boolean = {
    var configOk = true
    if (!parsingConfiguration.stripHybrid) {
      // if you don't remove causal links, certain options may not be used
      if (preprocessingConfiguration.splitIndependentParameters) {
        println("Not stripping hybridity (i.e. causal links in the model) disallows parameter splitting")
        configOk = false
      }

      if (preprocessingConfiguration.compileUselessAbstractTasks) {
        println("Not stripping hybridity (i.e. causal links in the model) disallows removal of useless abstract tasks")
        configOk = false
      }

    }

    configOk
  }

  private val protectedPredicatesFromConfiguration: Set[String] = searchConfiguration match {
    case SATSearch(_, _, Some(f), _, _, _, _, _, _, _, _, _) => f.nnf.allPredicatesNames
    case _                                                   => Set()
  }
}

object PlanningConfiguration {
  val defaultPlanSearchConfiguration = PlanBasedSearch(None, DFSType, Nil, Nil, LCFR)
  private val defaultProgressionConfiguration = ProgressionSearch(BFSType, None, PriorityQueueSearch.abstractTaskSelection.random)
  private val defaultSATConfiguration         = SATSearch(MINISAT, SingleSATRun(), atMostOneEncodingMethod = SequentialEncoding)
  val defaultVerifyConfiguration = SATPlanVerification(MINISAT, "")
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
                                 inheritPreconditions: Boolean = false,
                                 reduceGneralTasks: Boolean = true
                               ) extends Configuration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Parsing Configuration\n---------------------\n" +
    alignConfig(("Parser", parserType.longInfo) :: ("Expand Sort Hierarchy", expandSortHierarchy) ::
                  ("ClosedWordAssumption", closedWorldAssumption) ::
                  ("CompileSHOPMethods", compileSHOPMethods) ::
                  ("Eliminate Equality", eliminateEquality) ::
                  ("Strip Hybridity", stripHybrid) ::
                  ("Reduce General Tasks", reduceGneralTasks) :: Nil
               )

  protected override def localModifications: Seq[(String, (ParameterMode, (Option[String]) => ParsingConfiguration.this.type))] =
    Seq(
         "-parser" -> (NecessaryParameter, { p: Option[String] =>
           val parser = p.get.toLowerCase match {
             case "xml"           => XMLParserType
             case "hddl" | "pddl" => HDDLParserType
             case "hpddl"         => HPDDLParserType
             case "old-pddl"      => OldPDDLType
             case "auto"          => AutoDetectParserType
           }
           this.copy(parserType = parser).asInstanceOf[this.type]
         }),

         // these are for testing, debugging, and translating domains
         "-expandSortHierarchy" -> (NoParameter, { p: Option[String] => this.copy(expandSortHierarchy = true).asInstanceOf[this.type] }),
         "-dontExpandSortHierarchy" -> (NoParameter, { p: Option[String] => this.copy(expandSortHierarchy = false).asInstanceOf[this.type] }),

         "-closedWorldAssumption" -> (NoParameter, { p: Option[String] => this.copy(closedWorldAssumption = true).asInstanceOf[this.type] }),
         "-noClosedWorldAssumption" -> (NoParameter, { p: Option[String] => this.copy(closedWorldAssumption = false).asInstanceOf[this.type] }),

         "-compileSHOPMethods" -> (NoParameter, { p: Option[String] => this.copy(compileSHOPMethods = true).asInstanceOf[this.type] }),
         "-dontCompileSHOPMethods" -> (NoParameter, { p: Option[String] => this.copy(compileSHOPMethods = false).asInstanceOf[this.type] }),

         "-toPlainFormulaRepresentation" -> (NoParameter, { p: Option[String] => this.copy(reduceGneralTasks = true).asInstanceOf[this.type] }),
         "-generalFormulaRepresentation" -> (NoParameter, { p: Option[String] => assert(p.isEmpty); this.copy(reduceGneralTasks = false).asInstanceOf[this.type] }),

         // these are actual options
         "-eliminateEquality" -> (NoParameter, { p: Option[String] => this.copy(eliminateEquality = true).asInstanceOf[this.type] }),
         "-dontEliminateEquality" -> (NoParameter, { p: Option[String] => this.copy(eliminateEquality = false).asInstanceOf[this.type] }),

         "-stripHybrid" -> (NoParameter, { p: Option[String] => this.copy(stripHybrid = true).asInstanceOf[this.type] }),
         "-dontStripHybrid" -> (NoParameter, { p: Option[String] => this.copy(stripHybrid = false).asInstanceOf[this.type] })
       )
}

sealed trait TDGGeneration extends Configuration

object NaiveTDG extends TDGGeneration {override def toString: String = "Naive TDG"}

object TopDownTDG extends TDGGeneration {override def toString: String = "Top Down TDG"}

object BottomUpTDG extends TDGGeneration {override def toString: String = "Bottom Up TDG"}

object TwoWayTDG extends TDGGeneration {override def toString: String = "Two Way TDG"}

sealed trait GroundedReachabilityMode extends Configuration

object NaiveGroundedReachability extends GroundedReachabilityMode {override def longInfo: String = "Naive Reachability"}

object PlanningGraph extends GroundedReachabilityMode {override def longInfo: String = "Planning Graph (mutex-free)"}

object PlanningGraphWithMutexes extends GroundedReachabilityMode {override def longInfo: String = "Planning Graph (with mutexes)"}

object IntegerPlanningGraph extends GroundedReachabilityMode {override def longInfo: String = "Integer Planning Graph"}

object NativeGroundingImplementation extends GroundedReachabilityMode {override def longInfo: String = "Integer Planning Graph"}

case class PreprocessingConfiguration(
                                       compileNegativePreconditions: Boolean,
                                       compileUnitMethods: Boolean,
                                       compileOrderInMethods: Option[TotallyOrderingOption],
                                       compileInitialPlan: Boolean,
                                       ensureMethodsHaveAtMostTwoTasks: Boolean,
                                       ensureMethodsHaveLastTask: Boolean,
                                       removeUnnecessaryPredicates: Boolean,
                                       removeNoOps: Boolean,
                                       convertToSASP: Boolean,
                                       allowSASPFromStrips: Boolean,
                                       splitIndependentParameters: Boolean,
                                       compileUselessAbstractTasks: Boolean,
                                       liftedReachability: Boolean,
                                       groundedReachability: Option[GroundedReachabilityMode],
                                       groundedTaskDecompositionGraph: Option[TDGGeneration],
                                       iterateReachabilityAnalysis: Boolean,
                                       groundDomain: Boolean,
                                       stopDirectlyAfterGrounding: Boolean
                                     ) extends Configuration {
  assert(!convertToSASP || groundedReachability.isEmpty, "You can't use both SAS+ and a grouded PG")

  //assert(!convertToSASP || !compileNegativePreconditions, "You can't use both SAS+ and remove negative preconditions")

  //assert(!groundDomain || naiveGroundedTaskDecompositionGraph, "A grounded reachability analysis (grounded TDG) must be performed in order to ground.")

  override protected def localModifications: Seq[(String, (ParameterMode, (Option[String]) => PreprocessingConfiguration.this.type))] =
    Seq(
         "-compileNegPreconditions" -> (NoParameter, { p: Option[String] => this.copy(compileNegativePreconditions = true).asInstanceOf[this.type] }),
         "-dontCompileNegPreconditions" -> (NoParameter, { p: Option[String] => this.copy(compileNegativePreconditions = false).asInstanceOf[this.type] }),

         "-compileUnitMethods" -> (NoParameter, { p: Option[String] => this.copy(compileUnitMethods = true).asInstanceOf[this.type] }),
         "-dontCompileUnitMethods" -> (NoParameter, { p: Option[String] => this.copy(compileUnitMethods = false).asInstanceOf[this.type] }),

         "-totallyOrder" -> (NecessaryParameter, { p: Option[String] =>
           val orderingOption: TotallyOrderingOption = p.get match {
             case "all"                        => AllOrderings
             case "all-necessary"              => AllNecessaryOrderings
             case "one"                        => OneRandomOrdering()
             case x if x.startsWith("at-most") => AtMostKOrderings(x.replace("=", " ").split(" ")(1).toInt)
           }
           this.copy(compileOrderInMethods = Some(orderingOption)).asInstanceOf[this.type]
         }),
         "-dontTotallyOrder" -> (NoParameter, { p: Option[String] => this.copy(compileOrderInMethods = None).asInstanceOf[this.type] }),

         "-compileInitialPlan" -> (NoParameter, { p: Option[String] => this.copy(compileInitialPlan = true).asInstanceOf[this.type] }),
         "-dontCompileInitialPlan" -> (NoParameter, { p: Option[String] => this.copy(compileInitialPlan = false).asInstanceOf[this.type] }),

         "-splitIndependentParameters" -> (NoParameter, { p: Option[String] => this.copy(splitIndependentParameters = true).asInstanceOf[this.type] }),
         "-dontSplitIndependentParameters" -> (NoParameter, { p: Option[String] => this.copy(splitIndependentParameters = false).asInstanceOf[this.type] }),

         "-removeUnnecessaryPredicates" -> (NoParameter, { p: Option[String] => this.copy(removeUnnecessaryPredicates = true).asInstanceOf[this.type] }),
         "-dontRemoveUnnecessaryPredicates" -> (NoParameter, { p: Option[String] => this.copy(removeUnnecessaryPredicates = false).asInstanceOf[this.type] }),

         "-removeNoOps" -> (NoParameter, { p: Option[String] => this.copy(removeNoOps = true).asInstanceOf[this.type] }),
         "-dontRemoveNoOps" -> (NoParameter, { p: Option[String] => this.copy(removeNoOps = false).asInstanceOf[this.type] }),

         "-ensureLastTaskInMethods" -> (NoParameter, { p: Option[String] => this.copy(ensureMethodsHaveLastTask = true).asInstanceOf[this.type] }),
         "-dontEnsureLastTaskInMethods" -> (NoParameter, { p: Option[String] => this.copy(ensureMethodsHaveLastTask = false).asInstanceOf[this.type] }),


         "-domainCleanup" -> (NoParameter, { p: Option[String] => this.copy(liftedReachability = true).asInstanceOf[this.type] }),
         "-noDomainCleanup" -> (NoParameter, { p: Option[String] => this.copy(liftedReachability = false).asInstanceOf[this.type] }),

         "-sas+" -> (NoParameter, { p: Option[String] => this.copy(convertToSASP = true).asInstanceOf[this.type] }),
         "-nosas+" -> (NoParameter, { p: Option[String] => this.copy(convertToSASP = false).asInstanceOf[this.type] }),

         "-allowSAS+FromStrips" -> (NoParameter, { p: Option[String] => this.copy(allowSASPFromStrips = true).asInstanceOf[this.type] }),
         "-dontallowSAS+FromStrips" -> (NoParameter, { p: Option[String] => this.copy(allowSASPFromStrips = false).asInstanceOf[this.type] }),


         "-primitiveReachability" -> (NecessaryParameter, { p: Option[String] =>
           assert(p.isDefined)
           val mode = p.get match {
             case "planningGraph" | "pg"             => PlanningGraph
             case "planningGraphWithMutexes" | "pgm" => PlanningGraphWithMutexes
             case "naive"                            => NaiveGroundedReachability // this one does not have a short-cut, but who uses it anyway?!
           }
           this.copy(groundedReachability = Some(mode)).asInstanceOf[this.type]
         }),
         "-planningGraph" -> (NoParameter, { p: Option[String] => this.copy(groundedReachability = Some(PlanningGraph)).asInstanceOf[this.type] }),
         "-planningGraphWithMutexes" -> (NoParameter, { p: Option[String] => this.copy(groundedReachability = Some(PlanningGraphWithMutexes)).asInstanceOf[this.type] }),
         "-noPrimitiveReachability" -> (NoParameter, { p: Option[String] => this.copy(groundedReachability = None).asInstanceOf[this.type] }),

         "-hierarchicalReachability" -> (NecessaryParameter, { p: Option[String] =>
           assert(p.isDefined)
           val mode = p.get match {
             case "topDown" => TopDownTDG
             case "twoWay"  => TwoWayTDG
             case "naive"   => NaiveTDG // this one does not have a short-cut, but who uses it anyway?!
           }
           this.copy(groundedTaskDecompositionGraph = Some(mode)).asInstanceOf[this.type]
         }),
         "-topDownTDG" -> (NoParameter, { p: Option[String] => this.copy(groundedTaskDecompositionGraph = Some(TopDownTDG)).asInstanceOf[this.type] }),
         "-twoWayTDG" -> (NoParameter, { p: Option[String] => this.copy(groundedTaskDecompositionGraph = Some(TwoWayTDG)).asInstanceOf[this.type] }),
         "-tdg" -> (NoParameter, { p: Option[String] => this.copy(groundedTaskDecompositionGraph = Some(TwoWayTDG)).asInstanceOf[this.type] }),
         "-noHierarchicalReachability" -> (NoParameter, { p: Option[String] => this.copy(groundedTaskDecompositionGraph = None).asInstanceOf[this.type] }),

         "-iterateReachabilityAnalysis" -> (NoParameter, { p: Option[String] => this.copy(iterateReachabilityAnalysis = true).asInstanceOf[this.type] }),
         "-dontIterateReachabilityAnalysis" -> (NoParameter, { p: Option[String] => this.copy(iterateReachabilityAnalysis = false).asInstanceOf[this.type] }),

         "-stopAfterGrounding" -> (NoParameter, { p: Option[String] => this.copy(stopDirectlyAfterGrounding = true).asInstanceOf[this.type] }),
         "-dontStopAfterGrounding" -> (NoParameter, { p: Option[String] => this.copy(stopDirectlyAfterGrounding = false).asInstanceOf[this.type] }),

         "-compileUselessAbstracts" -> (NoParameter, { p: Option[String] => this.copy(compileUselessAbstractTasks = true).asInstanceOf[this.type] }),
         "-dontCompileUselessAbstracts" -> (NoParameter, { p: Option[String] => this.copy(compileUselessAbstractTasks = false).asInstanceOf[this.type] }),

         "-groundPlanning" -> (NoParameter, { p: Option[String] => this.copy(groundDomain = true).asInstanceOf[this.type] }),
         "-liftedPlanning" -> (NoParameter, { p: Option[String] => this.copy(groundDomain = false).asInstanceOf[this.type] })
       )

  override def longInfo: String = "Preprocessing Configuration\n---------------------------\n" +
    alignConfig(("Compile negative preconditions", compileNegativePreconditions) ::
                  ("Compile unit methods", compileUnitMethods) ::
                  ("Compile order in methods", if (compileOrderInMethods.isEmpty) "false" else compileOrderInMethods.get) ::
                  ("Compile initial plan", compileInitialPlan) ::
                  ("Ensure Methods Have Last Task", ensureMethodsHaveLastTask) ::
                  ("Split independent parameters", splitIndependentParameters) ::
                  ("Remove unnecessary predicates", removeUnnecessaryPredicates) ::
                  ("Expand choiceless abstract tasks", compileUselessAbstractTasks) ::
                  ("Domain Cleanup", liftedReachability) ::
                  ("Convert to SAS+", convertToSASP) ::
                  ("Grounded Reachability Analysis", if (groundedReachability.isEmpty) "false" else groundedReachability.get.longInfo) ::
                  ("Grounded Task Decomposition Graph", if (groundedTaskDecompositionGraph.isEmpty) "false" else groundedTaskDecompositionGraph.get) ::
                  ("Iterate reachability analysis", iterateReachabilityAnalysis) ::
                  ("Ground domain", groundDomain) ::
                  ("Iterate reachability analysis", iterateReachabilityAnalysis) ::
                  ("Stop directly after grounding", stopDirectlyAfterGrounding) ::
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
    case x if x.startsWith("externalsearch")                     => ExternalSearchEngine(x.replace(')', '(').split("\\(")(1))
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

case class ExternalSearchEngine(uuid: String) extends SearchAlgorithmType {override def longInfo: String = "Write model for external search engine"}

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
      case "random-heuristic"                            => RandomHeuristic
      case "#flaw" | "number-of-flaws"                   => NumberOfFlaws
      case "#oc" | "number-of-open-preconditions"        => NumberOfOpenPreconditions
      case "#ps" | "number-of-plan-steps"                => NumberOfPlanSteps
      case "#abstract" | "number-of-abstract-plan-steps" => NumberOfAbstractPlanSteps
      case "umcp-h" | "umcp"                             => UMCPHeuristic
      // tdg heuristics which ground in each step
      case "grounding-mme-with-cycle-detection" => TDGMinimumModificationWithCycleDetection()
      case "tdg-m-lifted"                       => TDGPreconditionRelaxation()
      case "grounding-tdg-minimum-add"          => TDGMinimumADD()
      case "tdg-c-lifted"                       => TDGMinimumAction()
      // tdg heurisitcs which ground once
      case "mme-with-cycle-detection" => LiftedTDGMinimumModificationWithCycleDetection(mode = RecomputationMode.parse(hParameterMap.getOrElse("recompute", "never")))
      case "tdg-minimum-add"          => LiftedTDGMinimumADD(mode = RecomputationMode.parse(hParameterMap.getOrElse("recompute", "never")))
      // IJCAI 2016 heuristics
      case "tdg-m" | "tdg-m-ground" | "tdg-m-ignore-parameters" => LiftedTDGPreconditionRelaxation(mode = NeverRecompute)
      case "tdg-c" | "tdg-c-ground" | "tdg-c-ignore-parameters" => LiftedTDGMinimumAction(mode = NeverRecompute)
      case "tdg-m-r" | "tdg-m-ground-recompute"                 => LiftedTDGPreconditionRelaxation(mode = ReachabilityRecompute)
      case "tdg-c-r" | "tdg-c-ground-recompute"                 => LiftedTDGMinimumAction(mode = ReachabilityRecompute)
      // this heuristic only exists for evaluation purposes
      case "tdg-mac-compare" | "mac-compare" => LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false)
      case "tdg-pr-compare" | "pr-compare"   => LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true)
      // POCL heuristics
      case "add"                   => ADD
      case "add-r" | "add-reusing" => ADDReusing
      case "relax"                 => Relax

      // pandaPRO
      case "hhmcff" | "relaxed-composition_with_multicount_ff" => RelaxedCompositionGraph(
                                                                                           useTDReachability = hParameterMap.getOrElse("td-reachability", "true").toBoolean,
                                                                                           heuristicExtraction = gphRcFFMulticount.heuristicExtraction
                                                                                             .parse(hParameterMap.getOrElse("extraction", "ff")),
                                                                                           producerSelectionStrategy = gphRcFFMulticount.producerSelection
                                                                                             .parse(hParameterMap.getOrElse("selection", "fcfs")))
      case "greedy-progression"                                => GreedyProgression
      case "hhrc"                                              =>
        val h = hParameterMap.get("h") match {
          case Some("ff")         => SasHeuristics.hFF
          case Some("add")        => SasHeuristics.hAdd
          case Some("max")        => SasHeuristics.hMax
          case Some("lm-cut")     => SasHeuristics.hLmCut
          case Some("inc-lm-cut") => SasHeuristics.hIncLmCut
          case None               => assert(false); null
        }

        HierarchicalHeuristicRelaxedComposition(h)
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

object UMCPBFSHeuristic extends SearchHeuristic {override val longInfo: String = "UMCP-BFS-Heuristic"}

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
  extends SearchHeuristicWithInner {override val longInfo: String = "tdg-m(recompute=" + mode.longInfo + ")"}

case class LiftedTDGMinimumAction(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None)
  extends SearchHeuristicWithInner {override val longInfo: String = "tdg-c(recompute=" + mode.longInfo + ")"}

case class LiftedTDGMinimumADD(mode: RecomputationMode, innerHeuristic: Option[SearchHeuristic] = None)
  extends SearchHeuristicWithInner {override val longInfo: String = "tdg-minimum-add(recompute=" + mode.longInfo + ")"}


// EVAL heuristics
case class LiftedTDGMinimumActionCompareToWithoutRecompute(usePR: Boolean) extends SearchHeuristic {override val longInfo: String = "tdg-mac-comparison"}


// POCL heuristics
object ADD extends SearchHeuristic {override val longInfo: String = "add"}

object ADDReusing extends SearchHeuristic {override val longInfo: String = "add_r"}

object Relax extends SearchHeuristic {override val longInfo: String = "relax"}

case class POCLTransformation(classicalHeuristic: SasHeuristics) extends SearchHeuristic {override val longInfo: String = "add"}

// PANDAPRO heuristics
case class RelaxedCompositionGraph(useTDReachability: Boolean, heuristicExtraction: gphRcFFMulticount.heuristicExtraction, producerSelectionStrategy: gphRcFFMulticount.producerSelection)
  extends SearchHeuristic {override val longInfo: String = "hhMcFF"}

object GreedyProgression extends SearchHeuristic {override val longInfo: String = "greedy-progression"}

case class HierarchicalHeuristicRelaxedComposition(classicalHeuristic: SasHeuristics)
  extends SearchHeuristic {override val longInfo: String = "hhRC(" + classicalHeuristic.toString + ")"}


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
        case "lcfr"                                  => LCFR
        case "umcp" | "umcp-f"                       => UMCPFlaw
        case "random-flaw"                           => RandomFlaw
        case "front-flaw"                            => FrontFlaw
        case "newest-flaw"                           => NewestFlaw
        case "causal-threat" | "causal-threat-first" => CausalThreat
        case "open-precs"                            => OpenPrecFlaw
        case "abstract-task"                         => AbstractTaskFlaw
        case "one-mod"                               => OneModFlaw
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

object OneModFlaw extends SearchFlawSelector {override val longInfo: String = "one-modification flaws"}

object AbstractTaskFlaw extends SearchFlawSelector {override val longInfo: String = "abstract-task flaws"}

object OpenPrecFlaw extends SearchFlawSelector {override val longInfo: String = "open-preconditions flaws"}


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
                  ("Search Algorithm", searchAlgorithm.longInfo) ::
                  ("Heuristic", if (heuristic.isEmpty) "none" else heuristic.map(_.longInfo).mkString(" -> ")) ::
                  ("Flaw selector", flawSelector.longInfo) ::
                  ("Pruning", if (pruningTechniques.isEmpty) "off" else pruningTechniques.map(_.longInfo).mkString(", ")) ::
                  ("Efficient search", efficientSearch) ::
                  ("Continue on solution", continueOnSolution) ::
                  ("Print search info", printSearchInfo) ::
                  Nil)


  override protected def localModifications: Seq[(String, (ParameterMode, (Option[String]) => PlanBasedSearch.this.type))] =
    Seq(
         "-nodeLimit" -> (NecessaryParameter, { p: Option[String] => this.copy(nodeLimit = Some(p.get.toInt)).asInstanceOf[this.type] }),

         "-efficientSearch" -> (NoParameter, { p: Option[String] => this.copy(efficientSearch = true).asInstanceOf[this.type] }),
         "-symbolicSearch" -> (NoParameter, { p: Option[String] => assert(p.isEmpty); this.copy(efficientSearch = false).asInstanceOf[this.type] }),

         "-continueOnSolution" -> (NoParameter, { p: Option[String] => this.copy(continueOnSolution = true).asInstanceOf[this.type] }),
         "-stopAtSoltuion" -> (NoParameter, { p: Option[String] => assert(p.isEmpty); this.copy(continueOnSolution = false).asInstanceOf[this.type] }),

         "-printSearchInfo" -> (NoParameter, { p: Option[String] => this.copy(printSearchInfo = true).asInstanceOf[this.type] }),
         "-noSearchInfo" -> (NoParameter, { p: Option[String] => this.copy(printSearchInfo = false).asInstanceOf[this.type] }),

         "-searchAlgorithm" -> (NecessaryParameter, { algo: Option[String] => this.copy(searchAlgorithm = SearchAlgorithmType.parse(algo.get)).asInstanceOf[this.type] }),

         "-heuristic" -> (NecessaryParameter, { h: Option[String] => this.copy(heuristic = SearchHeuristic.parse(h.get)).asInstanceOf[this.type] }),
         "-h" -> (NecessaryParameter, { h: Option[String] => this.copy(heuristic = SearchHeuristic.parse(h.get)).asInstanceOf[this.type] }),

         "-flawSelection" -> (NecessaryParameter, { f: Option[String] => this.copy(flawSelector = SearchFlawSelector.parse(f.get)).asInstanceOf[this.type] }),
         "-f" -> (NecessaryParameter, { f: Option[String] => this.copy(flawSelector = SearchFlawSelector.parse(f.get)).asInstanceOf[this.type] }),

         "-prune" -> (NecessaryParameter, { p: Option[String] => this.copy(pruningTechniques = PruningTechnique.parse(p.get)).asInstanceOf[this.type] })
       )
}

case class ProgressionSearch(searchAlgorithm: SearchAlgorithmType,
                             heuristic: Option[SearchHeuristic],
                             abstractTaskSelectionStrategy: PriorityQueueSearch.abstractTaskSelection) extends SearchConfiguration {

  override protected def localModifications: Seq[(String, (ParameterMode, (Option[String]) => ProgressionSearch.this.type))] =
    Seq(
         "-search" -> (NecessaryParameter, { algo: Option[String] => this.copy(searchAlgorithm = SearchAlgorithmType.parse(algo.get)).asInstanceOf[this.type] }),
         "-heuristic" -> (NecessaryParameter, { h: Option[String] =>
           val parsedHeuristics = SearchHeuristic.parse(h.get)
           assert(parsedHeuristics.length == 1)
           this.copy(heuristic = Some(parsedHeuristics.head)).asInstanceOf[this.type]
         }),
         "-h" -> (NecessaryParameter, { h: Option[String] =>
           val parsedHeuristics = SearchHeuristic.parse(h.get)
           assert(parsedHeuristics.length == 1)
           this.copy(heuristic = Some(parsedHeuristics.head)).asInstanceOf[this.type]
         }),
         "-abstractSelection" ->
           (NecessaryParameter, { p: Option[String] => this.copy(abstractTaskSelectionStrategy = PriorityQueueSearch.abstractTaskSelection.parse(p.get)).asInstanceOf[this.type] })
       )

  /** returns a detailed information about the object */
  override def longInfo: String = "Progression-search Configuration\n--------------------------------\n" +
    alignConfig(("Search Algorithm", searchAlgorithm) ::
                  ("Heuristic", if (heuristic.isDefined) heuristic.get.longInfo else "none") ::
                  ("Abstract task selection strategy", abstractTaskSelectionStrategy) ::
                  Nil)
}

sealed trait SATReductionMethod extends DefaultLongInfo

object OnlyNormalise extends SATReductionMethod {override def longInfo: String = "only normalise "}

object FFReduction extends SATReductionMethod {override def longInfo: String = "FF reduction"}

object H2Reduction extends SATReductionMethod {override def longInfo: String = "H2 reduction"}

object FFReductionWithFullTest extends SATReductionMethod {override def longInfo: String = "FF reduction with full reachability test"}


sealed trait SATRunConfiguration extends DefaultLongInfo

case class SingleSATRun(maximumPlanLength: Int = 1, overrideK: Option[Int] = None) extends SATRunConfiguration {override def longInfo: String = "XYZ"}

case class FullSATRun() extends SATRunConfiguration {override def longInfo: String = "full run"}

case class FullLengthSATRun(optimise: Boolean) extends SATRunConfiguration {override def longInfo: String = "full length run"}

case class OptimalSATRun(overrideK: Option[Int]) extends SATRunConfiguration {override def longInfo: String = "optimal run"}


sealed trait PlanDistanceMetric

case class MissingOperators(maximumDifference: Int) extends PlanDistanceMetric

case class MissingTaskInstances(maximumDifference: Int) extends PlanDistanceMetric

case class MinimumCommonSubplan(minimumSimilarity: Int, ignoreOrder: Boolean = false) extends PlanDistanceMetric

sealed trait LTLEncodingMethod

object BüchiEncoding extends LTLEncodingMethod

object AlternatingAutomatonEncoding extends LTLEncodingMethod

object MattmüllerEncoding extends LTLEncodingMethod

object MattmüllerImprovedEncoding extends LTLEncodingMethod

object OnParallelEncoding extends LTLEncodingMethod

case class SATSearch(solverType: Solvertype,
                     runConfiguration: SATRunConfiguration,
                     ltlFormula: Option[LTLFormula] = None,
                     formulaEncoding: LTLEncodingMethod = MattmüllerEncoding,
                     planToMinimiseDistanceTo: Option[Seq[String]] = None,
                     planDistanceMetric: Seq[PlanDistanceMetric] = Nil,
                     checkResult: Boolean = true,
                     reductionMethod: SATReductionMethod = OnlyNormalise,
                     encodingToUse: POEncoding = POCLDeleteEncoding,
                     atMostOneEncodingMethod: AtMostOneType,
                     usePDTMutexes: Boolean = false,
                     threads: Int = 1
                    ) extends SearchConfiguration {

  protected lazy val getSingleRun: SingleSATRun = runConfiguration match {
    case s: SingleSATRun => s
    case _               => SingleSATRun()
  }

  protected lazy val getFullRun: FullSATRun = runConfiguration match {
    case f: FullSATRun => f
    case _             => FullSATRun()
  }

  protected override def localModifications: Seq[(String, (ParameterMode, (Option[String] => this.type)))] =
    Seq(
         "-planlength" -> (NecessaryParameter, { l: Option[String] => this.copy(runConfiguration = getSingleRun.copy(maximumPlanLength = l.get.toInt)).asInstanceOf[this.type] }),
         "-overrideK" -> (NecessaryParameter, { l: Option[String] => this.copy(runConfiguration = getSingleRun.copy(overrideK = Some(l.get.toInt))).asInstanceOf[this.type] }),
         "-dontOverrideK" -> (NoParameter, { l: Option[String] => this.copy(runConfiguration = getSingleRun.copy(overrideK = None)).asInstanceOf[this.type] }),

         "-fullRun" -> (NoParameter, { l: Option[String] => this.copy(runConfiguration = getFullRun).asInstanceOf[this.type] }),


         "-checkResult" -> (NoParameter, { l: Option[String] => this.copy(checkResult = true).asInstanceOf[this.type] }),
         "-solver" -> (NecessaryParameter, { l: Option[String] =>
           val solver = l.get.toLowerCase match {
             case "minisat"       => MINISAT
             case "cryptominisat" => CRYPTOMINISAT
             case "riss6"         => RISS6
             case "maplecomsps"   => MapleCOMSPS
           }
           this.copy(solverType = solver).asInstanceOf[this.type]
         }),

         "-runType" -> (NecessaryParameter, { l: Option[String] =>
           val newRunType: Seq[SATRunConfiguration] = ArgumentListParser.parse(l.get.toLowerCase, { case (hName, hParameterMap) =>
             hName match {
               case "singlerun" => SingleSATRun(hParameterMap.getOrElse("planlength", "-1").toInt, hParameterMap.get("k").map(_.toInt))
             }
           })

           assert(newRunType.size == 1)

           this.copy(runConfiguration = newRunType.head).asInstanceOf[this.type]
         }),

         "-reduction" -> (NecessaryParameter, { l: Option[String] =>
           val reduction = l.get.toLowerCase match {
             case "normalise" => OnlyNormalise
             case "ff"        => FFReduction
             case "h2"        => H2Reduction
             case "ff-full"   => FFReductionWithFullTest
           }
           this.copy(reductionMethod = reduction).asInstanceOf[this.type]
         }),

         "-atMostOneEncoding" -> (NecessaryParameter, { l: Option[String] =>
           val encoding = l.get.toLowerCase match {
             case "binomial"   => BinomialEncoding
             case "binary"     => BinaryEncoding
             case "commander"  => CommanderEncoding
             case "sequential" => SequentialEncoding
           }
           this.copy(atMostOneEncodingMethod = encoding).asInstanceOf[this.type]
         })

       )

  /** returns a detailed information about the object */
  override def longInfo: String = "SAT-Planning Configuration\n--------------------------\n" +
    alignConfig((("solver", solverType.longInfo) :: Nil) ++
                  (runConfiguration match {
                    case single: SingleSATRun            =>
                      ("maximum plan length", single.maximumPlanLength) ::
                        ("override K", single.overrideK.getOrElse("false")) :: Nil
                    case fullRun: FullSATRun             =>
                      ("full planner run", "true") :: Nil
                    case fullLengthRun: FullLengthSATRun =>
                      ("full planner run", "true") :: ("optimise", fullLengthRun.optimise.toString) :: Nil
                    case optimal: OptimalSATRun          =>
                      ("optimal planner run", "true") ::
                        ("override K", optimal.overrideK.getOrElse("false")) :: Nil
                  }
                    ) ++ (
      ("reduction method", reductionMethod.longInfo) ::
        ("check result", checkResult) :: Nil))

}


case class SATPlanVerification(solverType: Solvertype, planToVerify: String) extends SearchConfiguration {

  protected override def localModifications: Seq[(String, (ParameterMode, (Option[String] => this.type)))] =
    Seq(
         "-satSolver" -> (NecessaryParameter, { l: Option[String] =>
           val solver = l.get.toLowerCase match {
             case "minisat"       => MINISAT
             case "cryptominisat" => CRYPTOMINISAT
             case "riss6"         => RISS6
             case "maplecomsps"   => MapleCOMSPS
           }
           this.copy(solverType = solver).asInstanceOf[this.type]
         }),
         "-planToVerify" -> (NecessaryParameter, { l: Option[String] =>
           this.copy(planToVerify = l.get).asInstanceOf[this.type]
         })
       )

  /** returns a detailed information about the object */
  override def longInfo: String = "SAT-Plan-Verification Configuration\n--------------------------\n" +
    alignConfig(("solver", solverType.longInfo) :: ("plan", planToVerify) :: Nil)
}


object FAPESearch extends SearchConfiguration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Use FAPE for the search"
}

object HTN2STRIPSSearch extends SearchConfiguration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Use HTN2STRIPS for search"
}

object SHOP2Search extends SearchConfiguration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Use JSHOP2 for the search"
}

object GTOHPSearch extends SearchConfiguration {
  /** returns a detailed information about the object */
  override def longInfo: String = "Use JSHOP2 for the search"
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

  override protected def localModifications: Seq[(String, (ParameterMode, (Option[String]) => PostprocessingConfiguration.this.type))] =
    Seq(
         "-timings" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + ProcessingTimings).asInstanceOf[this.type] }),
         "-outputStatus" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SearchStatus).asInstanceOf[this.type] }),
         "-outputPlan" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SearchResult).asInstanceOf[this.type] }),
         "-outputPlanWithHierarchy" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SearchResultWithDecompositionTree).asInstanceOf[this.type] }),
         "-outputPlanForVerification" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SearchResultInVerificationFormat).asInstanceOf[this.type] }),
         "-outputInternalPlan" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + InternalSearchResult).asInstanceOf[this.type] }),
         "-outputAllPlans" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + AllFoundPlans).asInstanceOf[this.type] }),
         "-outputAllPlansWithH*" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + AllFoundSolutionPathsWithHStar).asInstanceOf[this.type] }),
         "-statistics" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SearchStatistics).asInstanceOf[this.type] }),
         "-searchspace" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SearchSpace).asInstanceOf[this.type] }),
         "-outputPlanInternal" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SolutionInternalString).asInstanceOf[this.type] }),
         "-planToDot" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + SolutionDotString).asInstanceOf[this.type] }),
         "-finalDomainAndPlan" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + PreprocessedDomainAndPlan).asInstanceOf[this.type] }),
         "-initialDomainAndPlan" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + UnprocessedDomainAndPlan).asInstanceOf[this.type] }),
         "-finalTDG" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + FinalTaskDecompositionGraph).asInstanceOf[this.type] }),
         "-finalReachability" -> (NoParameter, { l: Option[String] => this.copy(resultsToProduce = resultsToProduce + FinalGroundedReachability).asInstanceOf[this.type] })
       )
}

sealed trait ParameterMode

object NoParameter extends ParameterMode

object OptionalParameter extends ParameterMode

object NecessaryParameter extends ParameterMode

trait Configuration extends DefaultLongInfo {
  final lazy val optionStrings: Seq[String] = modifyOnOptionStringOrdered.map(_._1)

  // name of the option, true if it can take a parameter
  protected def localModifications: Seq[(String, (ParameterMode, (Option[String] => this.type)))] = Nil

  def potentialRecursiveChildren: Seq[Configuration] = Nil

  protected def recursiveMethods(conf: Configuration): (conf.type => this.type) = Map()

  protected final lazy val modifyOnOptionStringOrdered: Seq[(String, (ParameterMode, (Option[String] => this.type)))] =
    localModifications ++ potentialRecursiveChildren.flatMap(
      { child =>
        child.modifyOnOptionStringOrdered.map(
          { case (k, (op, f)) =>
            val function: (Option[String] => this.type) = {case p: Option[String] => recursiveMethods(child)(f(p))}
            (k, (op, function))
          })
      })

  final lazy val modifyOnOptionString: Map[String, (ParameterMode, (Option[String] => this.type))] = modifyOnOptionStringOrdered.toMap

  /** returns a detailed information about the object */
  override def longInfo: String = "-- literally nothing --"

  protected def alignConfig(configs: Seq[(String, Any)]): String = {
    val keyMaxLength = if (configs.isEmpty) 0 else configs map { _._1.length } max

    configs map { case (k, v) => k + (Range(0, 1 + keyMaxLength - k.length) map { _ => " " }).mkString("") + ": " + v.toString } mkString "\n"
  }
}

sealed trait ExternalProgram extends DefaultLongInfo

object FastDownward extends ExternalProgram {override val longInfo: String = "Fast Downward"}

object FAPE extends ExternalProgram {override val longInfo: String = "FAPE"}

object SHOP2 extends ExternalProgram {override val longInfo: String = "J-SHOP2"}

object GTOHP extends ExternalProgram {override val longInfo: String = "GTOHP"}

object PANDASEARCHENGINE extends ExternalProgram {override val longInfo: String = "PANDA search engine"}

sealed trait Solvertype extends DefaultLongInfo with ExternalProgram


object MINISAT extends Solvertype {override val longInfo: String = "minisat"}

object CRYPTOMINISAT extends Solvertype {override val longInfo: String = "cryptominisat"}

object RISS6 extends Solvertype {override val longInfo: String = "riss6"}

object CADICAL extends Solvertype {override val longInfo: String = "Cadical"}


sealed trait DefaultDIMACSSolver extends Solvertype

object MapleCOMSPS extends DefaultDIMACSSolver {override val longInfo: String = "MapleCOMSPS"}

object MapleLCMDistChronoBT extends DefaultDIMACSSolver {override val longInfo: String = "MapleLCMDistChronoBT"}

object Maple_LCM_Scavel extends DefaultDIMACSSolver {override val longInfo: String = "Maple_LCM_Scavel"}

object ExpMC_LRB_VSIDS_Switch_2500 extends DefaultDIMACSSolver {override val longInfo: String = "expMC_LRB_VSIDS_Switch_2500"}

object ReasonLS extends DefaultDIMACSSolver {override val longInfo: String = "ReasonLS"}

object RISS7 extends DefaultDIMACSSolver {override val longInfo: String = "RISS7"}

object CRYPTOMINISAT55 extends DefaultDIMACSSolver {override val longInfo: String = "cryptominisat5.5"}
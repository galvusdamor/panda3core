package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.RCG
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.panda3.symbolic.compiler.{AllNecessaryOrderings, AllOrderings, TotallyOrderingOption}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraph, GroundedPlanningGraphConfiguration}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.util._

import scala.collection.{mutable, JavaConversions}


/**
  *
  *
  * @author Daniel Höller
  */
object ProMain {

  def main(args: Array[String]) {

    println("This is Panda3")


    if (args.length < 2) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output file. If the file extension is .dot a dot file will be" +
                " written, else a pdf.")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)
    if (args.length >= 3) {
      val randomseed = args(2)
      htnPlanningInstance.randomSeed = Integer.parseInt(randomseed)
    }
    //val outputPDF = args(2)
    val outputPDF = "dot.dot"

    //val domFile ="/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/anytime/d-0007-provide-temp-heat-5-tlt.hddl";
    //val probFile="/media/dh/Volume/repositories/private-documents/evaluation-domains/monroe/monroe-100-corpus/anytime/p-0007-provide-temp-heat-5-tlt.hddl";
    //val domFile ="/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile="/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"

    //val domFile ="/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp";
    //val probFile = "/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport//p00-htn.lisp";

    //val domFile = "/media/dh/Volume/repositories/private-documents/papers/2017-panda-pro/domains/simple-finite-domain-2.lisp";
    //val probFile = "/media/dh/Volume/repositories/private-documents/papers/2017-panda-pro/domains/simple-finite-problem-2.lisp";

    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)


    val proConfig: SearchConfiguration = if (args.length >= 4) {

      val abstractSelector = args(4) match {
        case "-randomSelection"      => PriorityQueueSearch.abstractTaskSelection.random
        case "-methodCountSelection" => PriorityQueueSearch.abstractTaskSelection.methodCount
        case "-decomDepthSelection"  => PriorityQueueSearch.abstractTaskSelection.decompDepth
      }

      val heuristicExtraction = if (args.length >= 6) args(5) match {
        case "-ff"    => RCG.heuristicExtraction.ff
        case "-multicount" => RCG.heuristicExtraction.multicount
      } else RCG.heuristicExtraction.multicount

      val producerSelector = if (args.length >= 7) args(6) match {
        case "-numPrec"    => RCG.producerSelection.numOfPreconditions
        case "-difficulty" => RCG.producerSelection.actionDifficulty
        case "-fcfs"       => RCG.producerSelection.firstCome
      } else null


      args(3) match {
        case "-greedyProgression" => ProgressionSearch(Some(30 * 60), GreedyType, Some(GreedyProgression), abstractTaskSelectionStrategy = abstractSelector)
        case "-greedyRCG"         => ProgressionSearch(Some(30 * 60), GreedyType, Some(RelaxedCompositionGraph(useTDReachability = false,heuristicExtraction = heuristicExtraction, producerSelectionStrategy = producerSelector)),
                                                       abstractTaskSelectionStrategy = abstractSelector)
        case "-greedyRCGTDR"      => ProgressionSearch(Some(30 * 60), GreedyType, Some(RelaxedCompositionGraph(useTDReachability = true,heuristicExtraction = heuristicExtraction, producerSelectionStrategy = producerSelector)),
                                                       abstractTaskSelectionStrategy = abstractSelector)
        case "-astarRCG"          => ProgressionSearch(Some(30 * 60), AStarActionsType(1),
                                                       Some(RelaxedCompositionGraph(useTDReachability = false,heuristicExtraction = heuristicExtraction, producerSelectionStrategy = producerSelector)),
                                                       abstractTaskSelectionStrategy = abstractSelector)
        case "-astarRCGTDR"       => ProgressionSearch(Some(30 * 60), AStarActionsType(1),
                                                       Some(RelaxedCompositionGraph(useTDReachability = true,heuristicExtraction = heuristicExtraction, producerSelectionStrategy = producerSelector)),
                                                       abstractTaskSelectionStrategy = abstractSelector)
      }
    } else NoSearch


    // create the configuration
    val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                             ParsingConfiguration(eliminateEquality = true, stripHybrid = true),
                                             PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                                        compileOrderInMethods = None,
                                                                        splitIndependedParameters = true,
                                                                        liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                                        groundedTaskDecompositionGraph = Some(TwoWayTDG), // None,
                                                                        iterateReachabilityAnalysis = false, groundDomain = true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(TDGMinimumModification), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, GreedyType, Some(TDGMinimumModification), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(TDGMinimumAction), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(NumberOfFlaws), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, GreedyType, Some(NumberOfFlaws), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, DijkstraType, None, true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(ADD), printSearchInfo = true),
                                             //PlanBasedSearch(Some(-1), Some(1), BFSType, None, LCFR),
                                             proConfig,
                                             //ProgressionSearch(Some(30 * 60), AStarActionsType(1), Some(RelaxedCompositionGraphWithTDReachability)),
                                             //SearchConfiguration(Some(-100), Some(-100), efficientSearch = false, BFSType, None, printSearchInfo = true),

                                             PostprocessingConfiguration(Set(ProcessingTimings,
                                                                             SearchStatistics,
                                                                             PreprocessedDomainAndPlan)))

    //System.in.read()


    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

    results(SearchStatistics).set(Information.DOMAIN_NAME, new File(domFile).getName)
    results(SearchStatistics).set(Information.PROBLEM_NAME, new File(probFile).getName)
    results(SearchStatistics).set(Information.RANDOM_SEED, htnPlanningInstance.randomSeed)


    //println("Panda says: " + results(SearchStatus))
    //println(results(SearchStatistics).shortInfo)
    //println("----------------- TIMINGS -----------------")
    //println(results(ProcessingTimings).shortInfo)


    println("###" + results(SearchStatistics).keyValueListString() + ";" + results(ProcessingTimings).keyValueListString())
  }
}
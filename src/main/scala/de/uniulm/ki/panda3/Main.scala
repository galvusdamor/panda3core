package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.panda3.symbolic.compiler.{AllOrderings, TotallyOrderingOption}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraphConfiguration, GroundedPlanningGraph}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.util._

import scala.collection.JavaConversions


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Main {

  def main(args: Array[String]) {

    println("This is Panda3")


    /*if (args.length < 2) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output file. If the file extension is .dot a dot file will be" +
                " written, else a pdf.")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)
    if (args.length == 3) {
      val randomseed = args(2)
      htnPlanningInstance.randomSeed = Integer.parseInt(randomseed)
    }*/
    //val outputPDF = args(2)
    val outputPDF = "dot.dot"

    //val domFile ="/home/dh/Schreibtisch/debug-planner/d-0048-provide-temp-heat-4.hddl";
    //val probFile="/home/dh/Schreibtisch/debug-planner/p-0048-provide-temp-heat-4.hddl";
    val domFile ="/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    val probFile="/home/dh/IdeaProjects/panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"

    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    // create the configuration
    val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                             ParsingConfiguration(eliminateEquality = true),
                                             PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false, compileOrderInMethods = None,
                                                                        liftedReachability = true, groundedReachability = true, planningGraph = false,
                                                                        groundedTaskDecompositionGraph = Some(TopDownTDG), // None,
                                                                        iterateReachabilityAnalysis = false, groundDomain = true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(TDGMinimumModification), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, GreedyType, Some(TDGMinimumModification), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(TDGMinimumAction), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(NumberOfFlaws), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, GreedyType, Some(NumberOfFlaws), true),
                                             //SearchConfiguration(None, None, efficientSearch = true, DijkstraType, None, true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(ADD), printSearchInfo = true),
                                             //PlanBasedSearch(Some(-1), Some(1), BFSType, None, LCFR),
                                              ProgressionSearch(None),
                                             //SearchConfiguration(Some(-100), Some(-100), efficientSearch = false, BFSType, None, printSearchInfo = true),

                                             PostprocessingConfiguration(Set(ProcessingTimings,
                                                                             SearchStatistics,
                                                                             PreprocessedDomainAndPlan)))

    //System.in.read()


    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

    //println("Panda says: " + results(SearchStatus))
    //println(results(SearchStatistics).shortInfo)
    //println("----------------- TIMINGS -----------------")
    //println(results(ProcessingTimings).shortInfo)

    println(results(SearchStatistics).keyValueListString())
    println(results(ProcessingTimings).keyValueListString())
  }
}
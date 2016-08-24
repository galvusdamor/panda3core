package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraphFromSymbolic
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraphConfiguration, GroundedPlanningGraph}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.panda3.efficient.heuristic.AddHeuristic
import de.uniulm.ki.util._


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object Main {
  def main(args: Array[String]) {

    /*println("This is Panda3")

    if (args.length != 3) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output file. If the file extension is .dot a dot file will be" +
                " written, else a pdf.")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)
    val outputPDF = args(2)*/

    //val domFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
    //val outputPDF = "/home/dhoeller/Schreibtisch/test.pdf"
    val outputPDF = "/home/gregor/test.pdf"
    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_domain.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/AssemblyTask_problem.xml"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_Small.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/ThesisExampleProblem.xml"
    //val domFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem-test-split1.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/4--1--3.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_domain.hddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_problem.hddl"

    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p01-domain.pddl"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p01.pddl"
    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/domain/driverlog.pddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/problems/pfile1"
    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/ZenoTravel/domain/zenotravelStrips.pddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/ZenoTravel/problems/pfile7"
    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/domain/stripsSat.pddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/problems/pfile4"
    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PROMELA-PHILO/domain/P01_DOMAIN.PDDL"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PROMELA-PHILO/problems/P01_PHIL2.PDDL"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/pegsol/domain/domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/pegsol/problems/p01.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/tidybot/domain/domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/tidybot/problems/p01.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/nomystery/domain/domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC7/nomystery/problems/p01.pddl"

    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/domain-htn.lisp"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/p01-htn.lisp"

    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p00-htn.lisp"

    //val domFile = "gripperDomain.pddl"
    //val probFile = "easyGripperProblem.pddl"
    //val domFile = "../panda3core_with_planning_graph/testDomain1.pddl"
    //val probFile = "../panda3core_with_planning_graph/testProblem1.pddl"


    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    // create the configuration
    val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                             ParsingConfiguration(HDDLParserType),
                                             PreprocessingConfiguration(compileNegativePreconditions = true,
                                                                        liftedReachability = true, groundedReachability = false, planningGraph = true,
                                                                        naiveGroundedTaskDecompositionGraph = false,
                                                                        iterateReachabilityAnalysis = true, groundDomain = true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(TDGMinimumADD), true),
                                             SearchConfiguration(Some(100000), None, efficientSearch = true, DijkstraType, None, true, true),
                                             //SearchConfiguration(Some(100000), None, efficientSearch = true, AStarActionsType, Some(ADD), true, printSearchInfo = true),
                                             //SearchConfiguration(Some(500000), None, efficientSearch = true, BFSType, None, printSearchInfo = true),
                                             PostprocessingConfiguration(Set(ProcessingTimings,
                                                                             SearchStatus, SearchResult, AllFoundPlans, AllFoundSolutionPathsWithHStar,
                                                                             SearchStatistics,
                                                                             SearchSpace,
                                                                             PreprocessedDomainAndPlan,
                                                                             SolutionInternalString,
                                                                             SolutionDotString)))

    //System.in.read()


    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

    println("Panda says: " + results(SearchStatus))
    println(results(SearchStatistics).shortInfo)
    println("----------------- TIMINGS -----------------")
    println(results(ProcessingTimings).shortInfo)

    // get all found plans
    val foundPlans = results(AllFoundPlans)
    println("Found in total " + foundPlans.length + " plans")

    // get all found plans
    val foundPaths = results(AllFoundSolutionPathsWithHStar)
    println("Found in total " + foundPaths.length + " paths with lengths")
    println(foundPaths map { _.length } map { "\t" + _ } mkString "\n")

    assert(foundPaths.length == foundPaths.length)
    if (foundPaths.nonEmpty) {
      val lengthOfInitialInitialPlan = foundPaths.head.head._1.plan.planSteps.length

      foundPaths foreach { p =>
        assert(p.last._1.plan.flaws.isEmpty)
        assert(p.head._1.plan.planSteps.length == lengthOfInitialInitialPlan)
      }
    }

    val wrapper = Wrapping(results(PreprocessedDomainAndPlan))
    val initialState = wrapper.initialPlan.groundedInitialStateOnlyPositive
    val planningGraph = GroundedPlanningGraph(wrapper.symbolicDomain, initialState.toSet, GroundedPlanningGraphConfiguration())
    val efficientPlanningGraph = EfficientGroundedPlanningGraphFromSymbolic(planningGraph, wrapper)
    val efficientInitialState = wrapper.initialPlan.groundedInitialState collect { case GroundLiteral(task, true, args) =>
      (wrapper.unwrap(task), args map wrapper.unwrap toArray)
    }
    val addHeuristic = AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, efficientInitialState.toArray, resuingAsVHPOP = false)


    val pathsWithHStarAndADD: Seq[Seq[(SearchNode, (Int, Int))]] =
      foundPaths map { p => p map { case (node, hstar) => (node, (hstar, addHeuristic.computeHeuristic(wrapper.unwrap(node.plan)).toInt)) } }


    if (results(SearchStatus) == SearchState.SOLUTION) {
      val solution = results(SearchResult).get
      //println(solution.planSteps.length)
      // write output
      if (outputPDF.endsWith("dot")) {
        writeStringToFile(solution.dotString, new File(outputPDF))
      } else {
        Dot2PdfCompiler.writeDotToFile(solution, outputPDF)
      }
    }


    var doneCounter = 0
    // check the tree
    def dfs(searchNode: SearchNode): Unit = if (!searchNode.dirty) {
      doneCounter += 1
      if (doneCounter % 10 == 0) println("traversed " + doneCounter)
      println("STATE: " + searchNode.searchState)
      //searchNode.modifications.length // force computation (and check of assertions)
      searchNode.children foreach { x => dfs(x._1) }
    }

    //System.in.read()

    /*if (searchConfig.postprocessingConfiguration.resultsToProduce contains SearchSpace) {
      results(SearchSpace).recomputeSearchState()
      dfs(results(SearchSpace))
    }*/
  }


}
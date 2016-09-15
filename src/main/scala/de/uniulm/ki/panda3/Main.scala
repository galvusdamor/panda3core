package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.datastructures.primitivereachability.EfficientGroundedPlanningGraphFromSymbolic
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraphConfiguration, GroundedPlanningGraph}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
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

    println("This is Panda3")

    /*if (args.length != 2) {
      println("This programm needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the name of the output file. If the file extension is .dot a dot file will be" +
                " written, else a pdf.")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)*/
    //val outputPDF = args(2)


    //val outputPDF = "/home/dhoeller/Schreibtisch/test.pdf"
    val outputPDF = "/home/gregor/test.pdf"


    //val domFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "/home/gregor/temp/model/domaineasy3.lisp"
    //val probFile = "/home/gregor/temp/model/problemeasy3.lisp"
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
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-AirplanesHub.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-RegularTruck-4Locations.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/4--4--4.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/5--5--5.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/6--2--2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/8--3--4.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant2-hierarchical.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant3-hierarchical.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant4-hierarchical.xml"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_domain.hddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/domain/primitivereachability/planningGraphTest02_problem.hddl"

    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p01-domain.pddl"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p03.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/transport-strips/domain/p01-domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/transport-strips/problems/p01.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p01-domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p05.pddl"
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

    //val domFile = "domain.lisp"
    //val probFile = "problems/p-0001-clear-road-wreck.lisp" // SOL 185
    //val probFile = "problems/p-0002-plow-road.lisp"    // SOL 74
    //val probFile = "problems/p-0003-set-up-shelter.lisp"   // SOL 46752
    //val probFile = "problems/p-0004-provide-medical-attention.lisp"  // SOL 10
    //val probFile = "problems/p-0005-clear-road-wreck.lisp"  // SOL 116
    //val probFile = "problems/p-0006-clear-road-wreck.lisp"   // SOL 77
    //val probFile = "problems/p-0007-provide-temp-heat.lisp"    // SOL 2790
    //val probFile = "problems/p-0008-provide-medical-attention.lisp"  // SOL 230
    //val probFile = "problems/p-0009-quell-riot.lisp"   // SOL 84
    //val probFile = "problems/p-0010-set-up-shelter.lisp"   // SOL GROUND 62423
    //val probFile = "problems/p-0011-plow-road.lisp"    // SOL 73
    //val probFile = "problems/p-0012-plow-road.lisp"     // SOL 84
    //val probFile = "problems/p-0013-clear-road-hazard.lisp" // SOL 308
    //val probFile = "problems/p-0014-fix-power-line.lisp"      // SOL 28
    //val probFile = "problems/p-0015-clear-road-hazard.lisp"     // SOL 417
    //val probFile = "problems/p-0016-fix-power-line.lisp"        // SOL 30
    //val probFile = "problems/p-0017-clear-road-tree.lisp"       // BUG
    //val probFile = "problems/p-0018-fix-power-line.lisp"        // SOL 30
    //val probFile = "problems/p-0019-clear-road-wreck.lisp"      // SOL 182
    //val probFile = "problems/p-0020-set-up-shelter.lisp"    // SOL 23971
    //val probFile = "problems/p-0021-plow-road.lisp"   // SOL 80
    //val probFile = "problems/p-0022-provide-medical-attention.lisp" // SOL 230
    //val probFile = "problems/p-0023-plow-road.lisp" // SOL 89
    //val probFile = "problems/p-0024-plow-road.lisp"   // SOL 6
    //val probFile = "problems/p-0025-clear-road-wreck.lisp"  // SOL 125
    //val probFile = "problems/p-0026-clear-road-tree.lisp"   // BUG
    //val probFile = "problems/p-0027-plow-road.lisp"         // SOL 50
    //val probFile = "problems/p-0028-set-up-shelter.lisp"      // SOL 18268
    //val probFile = "problems/p-0029-clear-road-tree.lisp"     // BUG
    //val probFile = "problems/p-0030-provide-temp-heat.lisp"   // TIMEOUT   (also on frodo)
    //val probFile = "problems/p-0030-provide-temp-heat.lisp"   // TIMEOUT
    //val probFile = "problems/p-0031-provide-temp-heat.lisp"   // TIMEOUT
    //val probFile = "problems/p-0032-plow-road.lisp"   // SOL 80
    //val probFile = "problems/p-0033-provide-medical-attention.lisp"   // SOL 146
    //val probFile = "problems/p-0034-provide-medical-attention.lisp"   // SOL 10
    //val probFile = "problems/p-0035-fix-power-line.lisp"   // SOL 28
    //val probFile = "problems/p-0036-clear-road-wreck.lisp"   // SOL 201
    //val probFile = "problems/p-0037-clear-road-hazard.lisp"   // SOL 508
    //val probFile = "problems/p-0038-plow-road.lisp"   // SOL 89
    //val probFile = "problems/p-0039-plow-road.lisp"   // SOL 73
    //val probFile = "problems/p-0040-provide-medical-attention.lisp"   // SOL 10
    //val probFile = "problems/p-0041-clear-road-wreck.lisp"   // SOL 955
    //val probFile = "problems/p-0042-clear-road-wreck.lisp"   // SOL 115
    //val probFile = "problems/p-0043-set-up-shelter.lisp"   // SOL 10
    //val probFile = "problems/p-0044-plow-road.lisp"   // SOL 457
    //val probFile = "problems/p-0045-plow-road.lisp"   // SOL 468
    //val probFile = "problems/p-0046-clear-road-wreck.lisp"   // SOL 8098
    //val probFile = "problems/p-0047-provide-temp-heat.lisp"   // TIMEOUT
    //val probFile = "problems/p-0048-provide-temp-heat.lisp"   // TIMEOUT
    //val probFile = "problems/p-0049-plow-road.lisp"   // SOL 97
    //val probFile = "problems/p-0050-clear-road-hazard.lisp"   // SOL 271
    //val probFile = "problems/p-0051-plow-road.lisp"   // BUG
    //val probFile = "problems/p-0052-provide-temp-heat.lisp"   // SOL 10
    //val probFile = "problems/p-0053-provide-medical-attention.lisp"   // SOL 185
    //val probFile = "problems/p-0054-clear-road-hazard.lisp"   // SOL 148
    //val probFile = "problems/p-0055-fix-power-line.lisp"   // SOL 30
    //val probFile = "problems/p-0056-provide-medical-attention.lisp"   // SOL 101
    //val probFile = "problems/p-0057-clear-road-wreck.lisp"   // SOL 47
    //val probFile = "problems/p-0058-fix-water-main.lisp"   // BUG
    //val probFile = "problems/p-0059-clear-road-hazard.lisp"   // SOL 608
    //val probFile = "problems/p-0060-clear-road-wreck.lisp"   // SOL 135
    //val probFile = "problems/p-0061-plow-road.lisp"   // SOL 50
    //val probFile = "problems/p-0062-clear-road-hazard.lisp"   // SOL 50

    //val probFile = "p-0002-plow-road.lisp"
    //val probFile = "p-0003-set-up-shelter.lisp"
    //val probFile = "p-0005-clear-road-wreck.lisp"
    //val domFile = "../panda3core_with_planning_graph/testDomain1.pddl"
    //val probFile = "../panda3core_with_planning_graph/testProblem1.pddl"


    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/towers/domain/domain.hpddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/towers/problems/pfile_03.pddl"

    //val domFile =
    //val probFile = "/home/gregor/p-0002-plow-road.lisp"
    //val probFile = "p-0002-plow-road.lisp"

    //val domFile = "/home/gregor/d-0111-plow-road-verify.hddl"
    //val probFile = "/home/gregor/p-0111-plow-road-verify.hddl"
    //val domFile = "/home/gregor/d-0009-quell-riot-4.hddl"
    //val probFile = "/home/gregor/p-0009-quell-riot-4.hddltlt"


    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    // create the configuration
    val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                             ParsingConfiguration(HDDLParserType),
                                             PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false, compileOrderInMethods = false,
                                                                        liftedReachability = true, groundedReachability = false, planningGraph = false,
                                                                        groundedTaskDecompositionGraph = None, //Some(TopDownTDG), // None,
                                                                        iterateReachabilityAnalysis = false, groundDomain = false),
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
      foundPaths map { p => p map { case (node, hstar) => (node, (hstar, addHeuristic.computeHeuristic(wrapper.unwrap(node.plan), (), null)._1.toInt)) } }

    println("SOLUTION SEQUENCE")
    val plan = results(SearchResult).get

    def psToString(ps: PlanStep): String = {
      val name = ps.schema.name
      val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

      name + args.mkString("(", ",", ")")
    }

    println(plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive } map psToString mkString "\n")

    println("FIRST DECOMPOSITION")
    val initSchema = results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.head.schema
    val initPS = plan.planStepsAndRemovedPlanSteps find { _.schema == initSchema } get
    val realGoalSchema = plan.planStepDecomposedByMethod(initPS).subPlan.planStepsWithoutInitGoal.head.schema
    val realGoal = plan.planStepsAndRemovedPlanSteps find { _.schema == realGoalSchema } get

    println(plan.planStepDecomposedByMethod(initPS).name + " into " + psToString(realGoal))

    //println("Longest Path " + results(PreprocessedDomainAndPlan)._1.taskSchemaTransitionGraph.longestPathLength.get)
    //println("Maximum Method size " + results(PreprocessedDomainAndPlan)._1.maximumMethodSize)


    if (results(SearchStatus) == SearchState.SOLUTION) {
      val solution = results(SearchResult).get
      //println(solution.planSteps.length)
      // write output
      if (outputPDF.endsWith("dot")) {
        writeStringToFile(solution.dotString(PlanDotOptions(showHierarchy = true)), new File(outputPDF))
      } else {
        Dot2PdfCompiler.writeDotToFile(solution.dotString(PlanDotOptions(showHierarchy = true)), outputPDF)
      }
    }


    var doneCounter = 0
    // check the tree
    def dfs(searchNode: SearchNode): Unit = if (!searchNode.dirty) {
      searchNode.plan
      searchNode.plan.flaws

      searchNode.plan.flaws map { _.resolvents(results(PreprocessedDomainAndPlan)._1) }
      searchNode.modifications

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
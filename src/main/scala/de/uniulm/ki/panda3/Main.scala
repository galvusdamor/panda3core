package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.heuristic.filter.TreeFF
import de.uniulm.ki.panda3.progression.htn.htnPlanningInstance
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.progression.relaxedPlanningGraph.RCG
import de.uniulm.ki.panda3.symbolic.compiler.{OneRandomOrdering, AllNecessaryOrderings, AllOrderings}
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.sat.verify.{MINISAT, CRYPTOMINISAT}
import de.uniulm.ki.panda3.symbolic.search.{SearchNode, SearchState}
import de.uniulm.ki.panda3.symbolic.writer.hddl.HDDLWriter
import de.uniulm.ki.util._


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off
object Main {

  def main(args: Array[String]) {

    println("This is Panda3")

    /*if (args.length < 2) {
      println("This program needs exactly three arguments\n\t1. the domain file\n\t2. the problem file\n\t3. the random seed.")
      //println("This program needs exactly two arguments\n\t1. the domain file\n\t2. the problem file")
      System.exit(1)
    }
    val domFile = args(0)
    val probFile = args(1)*/

    val randomseed = if (args.length == 3) args(2).toInt else 10
    val planLength = randomseed
    htnPlanningInstance.randomSeed = randomseed
    //val outputPDF = args(2)
    val outputPDF = "dot.pdf"

    //val domFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/media/dhoeller/Daten/Repositories/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/domains/CyberSecurity.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/problems/AufbewahrungDesPasswortsMitITN.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/problems/AufbewahrungDesPasswortsOhneITN.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/problems/SicherheitslückenInProgrammenMitITN.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/problems/SicherheitslückenInProgrammenOhneITN.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/problems/VernetzungVonRechnernMitMitITN.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/CyberSecurity/problems/VernetzungVonRechnernMitOhneITN.xml"

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
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/04-OrganizeMeeting_Large.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/ThesisExampleProblem.xml"
    //val domFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/verkabelung.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem-test-split1.lisp"
    //val probFile = "/home/gregor/Dokumente/svn/miscellaneous/A1-Vorprojekt/Planungsdomaene/problem1.lisp"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/satellite2.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/satellite2-P-abstract-2obs-2sat-2mod.xml"

    val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"
    val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/4obs-4sat-4mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/2obs-2sat-2mod.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking/domains/woodworking-legal-fewer-htn-groundings.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking/problems/00--p01-variant.xml"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p02-domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p02.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/transport-strips/domain/p01-domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/transport-strips/problems/p01.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/domain/p01-domain.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC6/pegsol-strips/problems/p05.pddl"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/domain/driverlog.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/DriverLog/problems/pfile2"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/ZenoTravel/domain/zenotravelStrips.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/ZenoTravel/problems/pfile2"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/domain/stripsSat.pddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC3/Satellite/problems/pfile2"
    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PROMELA-PHILO/domain/P01_DOMAIN.PDDL"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/pddl/IPC4/PROMELA-PHILO/problems/P01_PHIL2.PDDL"

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


    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking/domains/woodworking-legal-fewer-htn-groundings.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking/problems/07--p03-part1.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/AssemblyHierarchical/domains/verkabelung_domain_noComplexOperations.pddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/AssemblyHierarchical/problems/genericLinearProblem_depth30.pddl"

    //val domFile = "/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp"
    //val probFile = "/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p01-htn.lisp"

    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)



    val postprocessing = PostprocessingConfiguration(Set(ProcessingTimings,
                                                         SearchStatistics,
                                                         SearchStatus,
                                                         //SearchResult,
                                                         //FinalGroundedReachability,
                                                         PreprocessedDomainAndPlan))

    // planning config is given via stdin
    val searchConfig = if (args.length > 3) {
      assert(args.length == 6, "PANDA needs exactly 6 arguments in this configuration")
      PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                            PredefinedConfigurations.parsingConfigs(args(3)),
                            PredefinedConfigurations.preprocessConfigs(args(4)),
                            PredefinedConfigurations.searchConfigs(args(5)),
                            postprocessing
                           )
    } else PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                 ParsingConfiguration(eliminateEquality = false, stripHybrid = true),
                                 PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                            compileInitialPlan = true,
                                                            //compileOrderInMethods = Some(AllNecessaryOrderings),
                                                            compileOrderInMethods = None, //Some(OneRandomOrdering()),
                                                            splitIndependedParameters = true,
                                                            liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                            groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                            iterateReachabilityAnalysis = false, groundDomain = true),
                                 //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(TDGMinimumModification), true),
                                 //SearchConfiguration(None, None, efficientSearch = true, GreedyType, Some(TDGMinimumModification), true),
                                 //PlanBasedSearch(None, None, AStarActionsType(1), Some(TDGMinimumADD), Nil, LCFR),
                                 //SearchConfiguration(None, None, efficientSearch = true, AStarActionsType, Some(NumberOfFlaws), true),
                                 //SearchConfiguration(None, None, efficientSearch = true, GreedyType, Some(NumberOfFlaws), true),
                                 //PlanBasedSearch(None, None, DijkstraType, Nil, Nil, LCFR),
                                 //PlanBasedSearch(None, Some(30 * 60), GreedyType, UMCPHeuristic :: Nil, Nil, UMCPFlaw),
                                 //PlanBasedSearch(None, Some(5000), AStarActionsType(1), ADD :: Nil, Nil, LCFR),
                                 //PlanBasedSearch(None, None, AStarActionsType(1), Some(NumberOfFlaws), Nil, LCFR),
                                 //PlanBasedSearch(None, Some(30 * 60), AStarActionsType, Some(TDGMinimumAction), Nil, LCFR),
                                 //PlanBasedSearch(None, Some(30 * 60), AStarActionsType(1), ADD :: Nil, Nil, LCFR),
                                 //PlanBasedSearch(None, None, AStarDepthType(1), Some(TDGMinimumADD(Some(ADDReusing))), Nil, SequentialSelector(LCFR,RandomFlaw(6))),
                                 //PlanBasedSearch(None, None, AStarDepthType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: RandomHeuristic(1) :: Nil, Nil, LCFR),
                                 //PlanBasedSearch(None, None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: RandomHeuristic(1) :: Nil, Nil, LCFR),
                                 //PlanBasedSearch(None, None, AStarDepthType(1), LiftedTDGPreconditionRelaxation(NeverRecompute) :: RandomHeuristic(1) :: Nil, Nil, LCFR),
                                 //NoSearch,
                                 //PlanBasedSearch(None, Some(30 * 60), AStarDepthType(1), LiftedTDGMinimumADD(NeverRecompute, Some(ADDReusing)) :: Nil, Nil, LCFR),
                                 //ProgressionSearch(Some(30 * 60), DFSType, None, abstractTaskSelectionStrategy =  PriorityQueueSearch.abstractTaskSelection.branchOverAll),
                                 //ProgressionSearch(Some(30 * 60), AStarActionsType(1), Some(RelaxedCompositionGraph(true,RCG.heuristicExtraction.ff, RCG.producerSelection
                                 //  .numOfPreconditions)),
                                 //                  PriorityQueueSearch.abstractTaskSelection.random),
                                 //ProgressionSearch(Some(200), AStarActionsType(1), Some(GreedyProgression)),
                                 //SATSearch(Some(30 * 60), CRYPTOMINISAT(), planLength, Some(planLength)),
                                 SATSearch(Some(100000), MINISAT(), 12, Some(12)),
                                 //SATSearch(Some(30 * 60 * 1000), MINISAT(), 30, Some(10)),
                                 //SearchConfiguration(Some(-100), Some(-100), efficientSearch = false, BFSType, None, printSearchInfo = true),
                                 postprocessing)
    //System.in.read()


    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)
    // add general information
    results(SearchStatistics).set(Information.DOMAIN_NAME, new File(domFile).getName)
    results(SearchStatistics).set(Information.PROBLEM_NAME, new File(probFile).getName)
    results(SearchStatistics).set(Information.RANDOM_SEED, randomseed)


    println("Panda says: " + results(SearchStatus))
    println(results(SearchStatistics).shortInfo)
    println("----------------- TIMINGS -----------------")
    println(results(ProcessingTimings).shortInfo)


    // output data in a machine readable format
    println("###" + results(SearchStatistics).keyValueListString() + DataCapsule.SEPARATOR + results(ProcessingTimings).keyValueListString())


    // get all found plans
    //val foundPlans = results(AllFoundPlans)
    //println("Found in total " + foundPlans.length + " plans")

    /*// get all found plans
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

    val initialState = wrapper.initialPlan.groundedInitialStateOnlyPositive
    val planningGraph = GroundedPlanningGraph(wrapper.symbolicDomain, initialState.toSet, GroundedPlanningGraphConfiguration())
    val efficientPlanningGraph = EfficientGroundedPlanningGraphFromSymbolic(planningGraph, wrapper)
    val efficientInitialState = wrapper.initialPlan.groundedInitialState collect { case GroundLiteral(task, true, args) =>
      (wrapper.unwrap(task), args map wrapper.unwrap toArray)
    }
    val addHeuristic = AddHeuristic(efficientPlanningGraph, wrapper.efficientDomain, efficientInitialState.toArray, resuingAsVHPOP = false)
    val relaxHeuristic = RelaxHeuristic(efficientPlanningGraph, wrapper.efficientDomain, efficientInitialState.toArray)


    val pathsWithHStarAndADD: Seq[Seq[(SearchNode, (Int, Int,Int))]] =
      foundPaths map { p => p map { case (node, hstar) => (node, (hstar, addHeuristic.computeHeuristic(wrapper.unwrap(node.plan), (), null)._1.toInt,
        relaxHeuristic.computeHeuristic(wrapper.unwrap(node.plan), (), null)._1.toInt)) } }

    */
    if (results.map.contains(SearchResult) && results(SearchResult).isDefined) {

      println("SOLUTION SEQUENCE")
      val plan = results(SearchResult).get

      //println(HDDLWriter("foo","bar").writePlan(plan,indentation = false,problemMode = true))

      assert(plan.planSteps forall { _.schema.isPrimitive })

      def psToString(ps: PlanStep): String = {
        val name = ps.schema.name
        val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

        name + args.mkString("(", ",", ")")
      }

      println((plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }).zipWithIndex map { case (ps, i) => i + ": " + psToString(ps) } mkString "\n")

      if (results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.nonEmpty) {
        println("FIRST DECOMPOSITION")
        val initSchema = results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.head.schema
        val initPS = plan.planStepsAndRemovedPlanSteps find { _.schema == initSchema } get
        val realGoalSchema = plan.planStepDecomposedByMethod(initPS).subPlan.planStepsWithoutInitGoal.head.schema
        val realGoal = plan.planStepsAndRemovedPlanSteps find { _.schema == realGoalSchema } get

        println(plan.planStepDecomposedByMethod(initPS).name + " into " + psToString(realGoal))

        //println("Longest Path " + results(PreprocessedDomainAndPlan)._1.taskSchemaTransitionGraph.longestPathLength.get)
        //println("Maximum Method size " + results(PreprocessedDomainAndPlan)._1.maximumMethodSize)
      }
    }

    if (results.map.contains(SearchResult) && results(SearchStatus) == SearchState.SOLUTION) {
      val solution = results(SearchResult).get
      //println(solution.planSteps.length)
      // write output
      if (outputPDF.endsWith("dot")) {
        writeStringToFile(solution.dotString(PlanDotOptions(showHierarchy = false)), new File(outputPDF))
      } else {
        Dot2PdfCompiler.writeDotToFile(solution.dotString(PlanDotOptions(showHierarchy = true)), outputPDF)
      }
    }


    var doneCounter = 0
    // check the tree

    val wrapper = Wrapping(results(PreprocessedDomainAndPlan))
    val tff = TreeFF(wrapper.efficientDomain)

    def dfs(searchNode: SearchNode): Unit = if (!searchNode.dirty) {
      val effPlan = wrapper.unwrap(searchNode.plan)

      if (searchNode.searchState == SearchState.SOLUTION && !tff.isPossiblySolvable(effPlan)) {
        println("BÄÄ")

        val parPlan = wrapper.unwrap(searchNode.parent.plan)
        println("PARENT " + parPlan.numberOfAllPlanSteps)
        println(tff.isPossiblySolvable(parPlan))

        println("\n\n\nCHILD " + effPlan.numberOfAllPlanSteps)
        println(tff.isPossiblySolvable(effPlan))


        System exit 0
      }

      //searchNode.plan
      //searchNode.plan.flaws

      //searchNode.plan.flaws map { _.resolvents(results(PreprocessedDomainAndPlan)._1) }
      //searchNode.modifications

      doneCounter += 1
      if (doneCounter % 10 == 0) println("traversed " + doneCounter)
      println("STATE: " + searchNode.searchState)
      //searchNode.modifications.length // force computation (and check of assertions)
      searchNode.children foreach { x => dfs(x._1) }
    }

    //System.in.read()

    if (searchConfig.postprocessingConfiguration.resultsToProduce contains SearchSpace) {
      results(SearchSpace).recomputeSearchState()
      dfs(results(SearchSpace))
    }
  }
}
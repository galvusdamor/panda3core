package de.uniulm.ki.panda3.configuration

import java.io.FileInputStream

import de.uniulm.ki.panda3.symbolic.search.SearchState
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class FullStackTest extends FlatSpec {
  val postprocessing = PostprocessingConfiguration(Set(ProcessingTimings, SearchStatistics, SearchStatus, SearchResult))

  val grounded = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                            compileOrderInMethods = None,
                                            splitIndependedParameters = false,
                                            liftedReachability = true, groundedReachability = false, planningGraph = true,
                                            groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                            iterateReachabilityAnalysis = false, groundDomain = true)

  val lifted = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                          compileOrderInMethods = None,
                                          splitIndependedParameters = false,
                                          liftedReachability = true, groundedReachability = false, planningGraph = true,
                                          groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                          iterateReachabilityAnalysis = false, groundDomain = false)


  val algos =
    ("BFS", grounded, PlanBasedSearch(None, Some(30 * 60), BFSType, Nil, Nil, LCFR)) ::
      ("DFS", grounded, PlanBasedSearch(None, Some(30 * 60), DFSType, Nil, Nil, LCFR)) ::
      ("PR", grounded, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)) ::
      ("PR-Recompute-Reach", grounded, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)) ::
      ("PR-Recompute-CL", grounded, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil, Nil, LCFR)) ::
      ("ActionCount", grounded, PlanBasedSearch(None, Some(30 * 60), AStarActionsType(2),LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)) ::
      ("ActionCount-Recompute-Reach", grounded, PlanBasedSearch(None, Some(30 * 60), AStarActionsType(2),LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)) ::
      ("ActionCount-Recompute-CL", grounded, PlanBasedSearch(None, Some(30 * 60), AStarActionsType(2),LiftedTDGMinimumAction(CausalLinkRecompute) :: Nil, Nil, LCFR)) ::
      ("TDGADD", grounded, PlanBasedSearch(None, Some(30 * 60), AStarActionsType(2),LiftedTDGMinimumADD(NeverRecompute) :: Nil, Nil, LCFR)) ::
      ("TDGADD-Recompute-Reach", grounded, PlanBasedSearch(None, Some(30 * 60), AStarActionsType(2),LiftedTDGMinimumADD(ReachabilityRecompute) :: Nil, Nil, LCFR)) ::
      ("TDGADD-Recompute-CL", grounded, PlanBasedSearch(None, Some(30 * 60), AStarActionsType(2),LiftedTDGMinimumADD(CausalLinkRecompute) :: Nil, Nil, LCFR)) ::
      ("lifted-DFS", lifted, PlanBasedSearch(None, Some(30 * 60), DFSType, Nil, Nil, LCFR)) ::
      ("lifted-PR", lifted, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),TDGPreconditionRelaxation() :: Nil, Nil, LCFR)) ::
      ("lifted-MMESCC", lifted, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),TDGMinimumModificationWithCycleDetection() :: Nil, Nil, LCFR)) ::
      ("lifted-ADD", lifted, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),TDGMinimumADD() :: Nil, Nil, LCFR)) ::
      ("lifted-ActionCount", lifted, PlanBasedSearch(None, Some(30 * 60), AStarDepthType(2),TDGMinimumAction() :: Nil, Nil, LCFR)) ::
      ("PRO-cRPG", grounded, ProgressionSearch(Some(30 * 60), AStarActionsType(1),Some(CompositionRPG))) ::
      //("PRO-cRPGHTN", grounded, ProgressionSearch(Some(30 * 60), AStarActionsType(1),CompositionRPGHTN))) ::
      //("PRO-greedyProgression", grounded, ProgressionSearch(Some(30 * 60), AStarActionsType(1),GreedyProgression))) ::
      Nil


  val instances =
    ("Smartphone-VeryVerySmall",
      "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml",
      "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml") ::
      ("Woodworking-00",
        "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/woodworking-legal-fewer-htn-groundings.xml",
        "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/00--p01-variant.xml") ::
      ("Woodworking-01",
        "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/woodworking-legal-fewer-htn-groundings.xml",
        "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/01--p01-complete.xml") ::
      Nil


  algos foreach { case (algoText, preprocess, search) =>
    instances foreach { case (instanceText, domain, problem) =>
      algoText must "deliver correct results on " + instanceText in {
        val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                                 ParsingConfiguration(eliminateEquality = false, stripHybrid = false),
                                                 preprocess,
                                                 search,
                                                 postprocessing)


        val results: ResultMap = searchConfig.runResultSearch(new FileInputStream(domain),
                                                              new FileInputStream(problem))

        assert(results(SearchStatus) == SearchState.SOLUTION)
      }
    }
  }
}
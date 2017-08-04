package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedCompositionGraph.ProRcgFFMulticount
import de.uniulm.ki.panda3.symbolic.compiler.OneOfTheNecessaryOrderings


import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic.SasHeuristics
import de.uniulm.ki.panda3.symbolic.compiler.AllNecessaryOrderings


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PredefinedConfigurations {

  val hybridParsing = ParsingConfiguration(eliminateEquality = true, stripHybrid = false)
  val htnParsing    = ParsingConfiguration(eliminateEquality = true, stripHybrid = true)

  val parsingConfigs = Map(
                            "-hybrid" -> hybridParsing,
                            "-htn" -> htnParsing
                          )


  val groundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileInitialPlan = true,
                                                       convertToSASP = false,
                                                       compileOrderInMethods = None,
                                                       splitIndependentParameters = true,
                                                       compileUselessAbstractTasks = true,
                                                       liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = true, groundDomain = true)


  val sasPlusPreprocess = PreprocessingConfiguration(compileNegativePreconditions = false, compileUnitMethods = false,
                                                     compileOrderInMethods = None,
                                                     compileInitialPlan = true, splitIndependentParameters = true,
                                                     liftedReachability = true, convertToSASP = true, compileUselessAbstractTasks = true,
                                                     groundedReachability = None,
                                                     groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                     iterateReachabilityAnalysis = false, groundDomain = true)

  val orderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = Some(AllNecessaryOrderings),
                                                               //compileOrderInMethods = None, //Some(OneRandomOrdering()),
                                                               compileInitialPlan = false, convertToSASP = false, splitIndependentParameters = true,
                                                               compileUselessAbstractTasks = false,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true)
  val liftedPreprocess            = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               compileUselessAbstractTasks = false,
                                                               liftedReachability = true, convertToSASP = false, groundedReachability = Some(PlanningGraphWithMutexes),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = false)

  val oneshortOrderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                                       compileInitialPlan = true,
                                                                       convertToSASP = false,
                                                                       compileOrderInMethods = Some(OneOfTheNecessaryOrderings),
                                                                       splitIndependentParameters = true,
                                                                       compileUselessAbstractTasks = true,
                                                                       liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                       iterateReachabilityAnalysis = true, groundDomain = true)

  val oneshortOrderingGroundingPreprocessWithSASPlus = PreprocessingConfiguration(compileNegativePreconditions = false, compileUnitMethods = false,
                                                                                  compileInitialPlan = true,
                                                                                  convertToSASP = true,
                                                                                  compileOrderInMethods = Some(OneOfTheNecessaryOrderings),
                                                                                  splitIndependentParameters = true,
                                                                                  compileUselessAbstractTasks = true,
                                                                                  liftedReachability = true, groundedReachability = None,
                                                                                  groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                                  iterateReachabilityAnalysis = true, groundDomain = true)


  val preprocessConfigs = Map(
                               "-ordering" -> orderingGroundingPreprocess,
                               "-ground" -> groundingPreprocess,
                               "-SAS+" -> sasPlusPreprocess,
                               "-lifted" -> liftedPreprocess
                             )


  // TODO old stuff ... should probably be deleted


  val globalTimelimit = 10 * 60


  // Greedy
  val GreedyADD                = PlanBasedSearch(None, GreedyType, ADD :: Nil, Nil, LCFR)
  val GreedyADDReusing         = PlanBasedSearch(None, GreedyType, ADDReusing :: Nil, Nil, LCFR)
  val GreedyRelax              = PlanBasedSearch(None, GreedyType, Relax :: Nil, Nil, LCFR)
  val GreedyAOpenPreconditions = PlanBasedSearch(None, GreedyType, NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val GreedyAPRLiftedPR                = PlanBasedSearch(None, GreedyType, LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val GreedyAPRLiftedPRReachability    = PlanBasedSearch(None, GreedyType, LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPR             = PlanBasedSearch(None, GreedyType, LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPRReachability = PlanBasedSearch(None, GreedyType, LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  val GreedyAPRLiftedPRCompare    = PlanBasedSearch(None, GreedyType, LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPRCompare = PlanBasedSearch(None, GreedyType, LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false) :: Nil, Nil, LCFR)

  // A*
  val AStarADD                = PlanBasedSearch(None, AStarActionsType(1), ADD :: Nil, Nil, LCFR)
  val AStarADDReusing         = PlanBasedSearch(None, AStarActionsType(1), ADDReusing :: Nil, Nil, LCFR)
  val AStarRelax              = PlanBasedSearch(None, AStarActionsType(1), Relax :: Nil, Nil, LCFR)
  val AStarAOpenPreconditions = PlanBasedSearch(None, AStarActionsType(1), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val AStarAPRLiftedPR                = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val AStarAPRLiftedPRReachability    = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val AStarActionLiftedPR             = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val AStarActionLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  val AStarAPRLiftedPRCompare    = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true) :: Nil, Nil, LCFR)
  val AStarActionLiftedPRCompare = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false) :: Nil, Nil, LCFR)

  // GREEDY A*
  val planSearchAStarAPR           = PlanBasedSearch(None, AStarActionsType(2), TDGPreconditionRelaxation() :: Nil, Nil, LCFR)
  val planSearchAStarTDGAction     = PlanBasedSearch(None, AStarActionsType(2), TDGMinimumAction() :: Nil, Nil, LCFR)
  val planSearchAStarTDGADD        = PlanBasedSearch(None, AStarActionsType(2), TDGMinimumADD() :: Nil, Nil, LCFR)
  val planSearchAStarTDGADDReusing = PlanBasedSearch(None, AStarActionsType(2), TDGMinimumADD(Some(ADDReusing)) :: Nil, Nil, LCFR)


  val planSearchAStarAPRLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarAPRLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarAPRLiftedPRCausalLink   = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarActionLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarActionLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarActionLiftedPRCausalLink   = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(CausalLinkRecompute) :: Nil, Nil, LCFR)


  val greedyAStarAPRLiftedPRCompare    = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true) :: Nil, Nil, LCFR)
  val greedyAStarActionLiftedPRCompare = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false) :: Nil, Nil, LCFR)


  val planSearchDijkstra                = PlanBasedSearch(None, DijkstraType, Nil, Nil, LCFR)
  val planSearchDFS                     = PlanBasedSearch(None, DFSType, Nil, Nil, LCFR)
  val planSearchBFS                     = PlanBasedSearch(None, BFSType, Nil, Nil, LCFR)
  val planSearchAStarOpenPreconditions  = PlanBasedSearch(None, AStarDepthType(2), NumberOfOpenPreconditions :: Nil, Nil, LCFR)
  val planSearchAStarAOpenPreconditions = PlanBasedSearch(None, AStarActionsType(2), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val umcpBF = PlanBasedSearch(None, BFSType, Nil, Nil, UMCPFlaw)
  val umcpDF = PlanBasedSearch(None, DFSType, Nil, Nil, UMCPFlaw)
  val umcpH  = PlanBasedSearch(None, GreedyType, UMCPHeuristic :: Nil, Nil, UMCPFlaw)

  val planSearchAStarADD        = PlanBasedSearch(None, AStarActionsType(2), ADD :: Nil, Nil, LCFR)
  val planSearchAStarADDReusing = PlanBasedSearch(None, AStarActionsType(2), ADDReusing :: Nil, Nil, LCFR)
  val planSearchAStarRelax      = PlanBasedSearch(None, AStarActionsType(2), Relax :: Nil, Nil, LCFR)

  val shop2 = ProgressionSearch(DFSType, None, abstractTaskSelectionStrategy = PriorityQueueSearch.abstractTaskSelection.branchOverAll)

  def pandaProConfig(algorithm: SearchAlgorithmType, sasHeuristic: SasHeuristics): ProgressionSearch =
    ProgressionSearch(algorithm, Some(HierarchicalHeuristicRelaxedComposition(sasHeuristic)), PriorityQueueSearch.abstractTaskSelection.random)


  val searchConfigs = Map(
                           "-GAStarAPR" -> planSearchAStarAPR,
                           "-GAStarTDGAction" -> planSearchAStarTDGAction,
                           "-GAStarTDGADD" -> planSearchAStarTDGADD,
                           "-GAStarTDGADDReusing" -> planSearchAStarTDGADDReusing,


                           "-GAStarAPRLiftedPR" -> planSearchAStarAPRLiftedPR,
                           "-GAStarAPRLiftedPRReachability" -> planSearchAStarAPRLiftedPRReachability,
                           "-GAStarAPRLiftedPRCausalLink" -> planSearchAStarAPRLiftedPRCausalLink,

                           "-GAStarActionLiftedPR" -> planSearchAStarActionLiftedPR,
                           "-GAStarActionLiftedPRReachability" -> planSearchAStarActionLiftedPRReachability,
                           "-GAStarActionLiftedPRCausalLink" -> planSearchAStarActionLiftedPRCausalLink,

                           "-Dijkstra" -> planSearchDijkstra,
                           "-DFS" -> planSearchDFS,
                           "-BFS" -> planSearchBFS,

                           "-GAStarOpenPreconditions" -> planSearchAStarOpenPreconditions,

                           "-umcpBF" -> umcpBF,
                           "-umcpDF" -> umcpDF,
                           "-umcpH" -> umcpH,

                           // Greedy A*
                           "-GAStarADD" -> planSearchAStarADD,
                           "-GAStarADDReusing" -> planSearchAStarADDReusing,
                           "-GAStarRelax" -> planSearchAStarRelax,
                           "-GAStarAOpenPreconditions" -> planSearchAStarAOpenPreconditions,
                           "-GAStarAPRLiftedPR" -> planSearchAStarAPRLiftedPR,
                           "-GAStarAPRLiftedPRReachability" -> planSearchAStarAPRLiftedPRReachability,
                           "-GAStarActionLiftedPR" -> planSearchAStarActionLiftedPR,
                           "-GAStarActionLiftedPRReachability" -> planSearchAStarActionLiftedPRReachability,

                           // A*
                           "-AStarADD" -> AStarADD,
                           "-AStarADDReusing" -> AStarADDReusing,
                           "-AStarRelax" -> AStarRelax,
                           "-AStarAOpenPreconditions" -> AStarAOpenPreconditions,
                           "-AStarAPRLiftedPR" -> AStarAPRLiftedPR,
                           "-AStarAPRLiftedPRReachability" -> AStarAPRLiftedPRReachability,
                           "-AStarActionLiftedPR" -> AStarActionLiftedPR,
                           "-AStarActionLiftedPRReachability" -> AStarActionLiftedPRReachability,

                           // Greedy
                           "-GreedyADD" -> GreedyADD,
                           "-GreedyADDReusing" -> GreedyADDReusing,
                           "-GreedyRelax" -> GreedyRelax,
                           "-GreedyAOpenPreconditions" -> GreedyAOpenPreconditions,
                           "-GreedyAPRLiftedPR" -> GreedyAPRLiftedPR,
                           "-GreedyAPRLiftedPRReachability" -> GreedyAPRLiftedPRReachability,
                           "-GreedyActionLiftedPR" -> GreedyActionLiftedPR,
                           "-GreedyActionLiftedPRReachability" -> GreedyActionLiftedPRReachability

                         )

  val defaultConfigurations: Map[String, (ParsingConfiguration, PreprocessingConfiguration, SearchConfiguration)] =
    Map(
         // Old Panda
         "-astar-panda-MAC" ->(htnParsing, groundingPreprocess, AStarActionLiftedPR),
         "-astar-panda-MAC-PR" ->(htnParsing, groundingPreprocess, AStarActionLiftedPRReachability),
         "-astar-panda-MME" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPR),
         "-astar-panda-MME-PR" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability),

         "-greedy-panda-MAC" ->(htnParsing, groundingPreprocess, GreedyActionLiftedPR),
         "-greedy-panda-MAC-PR" ->(htnParsing, groundingPreprocess, GreedyActionLiftedPRReachability),
         "-greedy-panda-MME" ->(htnParsing, groundingPreprocess, GreedyAPRLiftedPR),
         "-greedy-panda-MME-PR" ->(htnParsing, groundingPreprocess, GreedyAPRLiftedPRReachability),

         "-gastar-panda-MAC" ->(htnParsing, groundingPreprocess, planSearchAStarActionLiftedPR),
         "-gastar-panda-MAC-PR" ->(htnParsing, groundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "-gastar-panda-MME" ->(htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPR),
         "-gastar-panda-MME-PR" ->(htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPRReachability),

         "-Dijkstra" ->(htnParsing, groundingPreprocess, planSearchDijkstra),
         "-DFS" ->(htnParsing, groundingPreprocess, planSearchDFS),
         "-BFS" ->(htnParsing, groundingPreprocess, planSearchBFS),

         "-umcpBF" ->(htnParsing, groundingPreprocess, umcpBF),
         "-umcpDF" ->(htnParsing, groundingPreprocess, umcpDF),
         "-umcpH" ->(htnParsing, groundingPreprocess, umcpH),

         // SHOP
         "-shop2" ->(htnParsing, sasPlusPreprocess, shop2),


         // A*

         "-AStarADD" ->(htnParsing, groundingPreprocess, AStarADD),
         "-AStarADDReusing" ->(htnParsing, groundingPreprocess, AStarADDReusing),
         "-AStarRelax" ->(htnParsing, groundingPreprocess, AStarRelax),
         "-AStarAOpenPreconditions" ->(htnParsing, groundingPreprocess, AStarAOpenPreconditions),

         "-AStarAPRLiftedPR" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPR),
         "-AStarAPRLiftedPRReachability" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability),
         "-AStarActionLiftedPR" ->(htnParsing, groundingPreprocess, AStarActionLiftedPR),
         "-AStarActionLiftedPRReachability" ->(htnParsing, groundingPreprocess, AStarActionLiftedPRReachability),

         // GA*
         "-GAStarADD" ->(htnParsing, groundingPreprocess, planSearchAStarADD),
         "-GAStarADDReusing" ->(htnParsing, groundingPreprocess, planSearchAStarADDReusing),
         "-GAStarRelax" ->(htnParsing, groundingPreprocess, planSearchAStarRelax),
         "-GAStarAOpenPreconditions" ->(htnParsing, groundingPreprocess, planSearchAStarAOpenPreconditions),

         "-GAStarActionLiftedPR" ->(htnParsing, groundingPreprocess, planSearchAStarActionLiftedPR),
         "-GAStarActionLiftedPRReachability" ->(htnParsing, groundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "-GAStarAPRLiftedPR" ->(htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPR),
         "-GAStarAPRLiftedPRReachability" ->(htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPRReachability),

         //  compare

         "-AStar-MAC-Recompute-Compare" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPRCompare),
         "-AStar-PR-Recompute-Compare" ->(htnParsing, groundingPreprocess, AStarActionLiftedPRCompare),
         "-GreedyAStar-MAC-Recompute-Compare" ->(htnParsing, groundingPreprocess, greedyAStarAPRLiftedPRCompare),
         "-GreedyAStar-PR-Recompute-Compare" ->(htnParsing, groundingPreprocess, greedyAStarActionLiftedPRCompare),

         // PRO
         "-GreedyAStarPro-hhRC-lm-cut" ->(htnParsing, sasPlusPreprocess, ProgressionSearch(AStarActionsType(2),
                                                                                           Some(HierarchicalHeuristicRelaxedComposition(SasHeuristics.hLmCut)), PriorityQueueSearch
                                                                                             .abstractTaskSelection.random)),


         // configurations to test totSAT
         "-oneshortTOTsat" ->(htnParsing, oneshortOrderingGroundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise)),
         "-oneshortTOTsatFF" ->(htnParsing, oneshortOrderingGroundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = FFReduction)),
         "-oneshortTOTsatH2" ->(htnParsing, oneshortOrderingGroundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = H2Reduction)),
         "-oneshortTOTsatFFFull" ->(htnParsing, oneshortOrderingGroundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = FFReductionWithFullTest)),
         "-poclSat" ->(htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise)),
         "-poclSatSAS+" ->(htnParsing, sasPlusPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise)),
         "-classicalSat" ->(htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, forceClassicalEncoding = true)),
         "-classicalSatSAS+" ->(htnParsing, sasPlusPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, forceClassicalEncoding = true)),

         // Greedy-A*
         "-oneshortTOTGreedyAStarMAC-Rec" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "-oneshortTOTGreedyAStarMAC" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchAStarActionLiftedPR),
         "-oneshortTOTGreedyAStarPR-Rec" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "-oneshortTOTGreedyAStarPR" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchAStarActionLiftedPR),

         // A*
         "-oneshortTOTAStarMAC-Rec" ->(htnParsing, oneshortOrderingGroundingPreprocess, AStarActionLiftedPRReachability),
         "-oneshortTOTAStarMAC" ->(htnParsing, oneshortOrderingGroundingPreprocess, AStarActionLiftedPR),
         "-oneshortTOTAStarPR-Rec" ->(htnParsing, oneshortOrderingGroundingPreprocess, AStarAPRLiftedPRReachability),
         "-oneshortTOTAStarPR" ->(htnParsing, oneshortOrderingGroundingPreprocess, AStarAPRLiftedPR),

         // bullshit configurations
         "-oneshortTOTDijkstra" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchDijkstra),
         "-oneshortTOTDFS" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchDFS),
         "-oneshortTOTBFS" ->(htnParsing, oneshortOrderingGroundingPreprocess, planSearchBFS),

         // UMCP
         "-oneshortTOTumcpBF" ->(htnParsing, oneshortOrderingGroundingPreprocess, umcpBF),
         "-oneshortTOTumcpDF" ->(htnParsing, oneshortOrderingGroundingPreprocess, umcpDF),
         "-oneshortTOTumcpH" ->(htnParsing, oneshortOrderingGroundingPreprocess, umcpH),

         // SHOP
         "-oneshortTOTshop2" ->(htnParsing, oneshortOrderingGroundingPreprocessWithSASPlus, shop2),

         // plan verification a la ICAPS'17
         "-verify" ->(htnParsing, groundingPreprocess, SATPlanVerification(CRYPTOMINISAT, "")),


         ///// PANDA Pro
         "-astar-pro-FF" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hFF)),
         "-astar-pro-lmcut" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hLmCut)),
         "-astar-pro-lmcut-inc" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hIncLmCut)),
         "-astar-pro-add" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hAdd)),

         "-gastar-pro-FF" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hFF)),
         "-gastar-pro-lmcut" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hLmCut)),
         "-gastar-pro-lmcut-inc" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hIncLmCut)),
         "-gastar-pro-add" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hAdd)),

         "-g3astar-pro-FF" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hFF)),
         "-g3astar-pro-lmcut" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hLmCut)),
         "-g3astar-pro-lmcut-inc" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hIncLmCut)),
         "-g3astar-pro-add" ->(htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hAdd)),

         "-greedy-pro-FF" ->(htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hFF)),
         "-greedy-pro-lmcut" ->(htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hLmCut)),
         "-greedy-pro-lmcut-inc" ->(htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hIncLmCut)),
         "-greedy-pro-add" ->(htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hAdd))

       )

}
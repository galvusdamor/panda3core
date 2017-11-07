package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.progression.heuristics.htn.RelaxedComposition.gphRcFFMulticount
import de.uniulm.ki.panda3.symbolic.compiler.OneOfTheNecessaryOrderings
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic.SasHeuristics
import de.uniulm.ki.panda3.symbolic.compiler.AllNecessaryOrderings
import de.uniulm.ki.panda3.symbolic.sat.verify._


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PredefinedConfigurations {

  val hybridParsing = ParsingConfiguration(eliminateEquality = false, stripHybrid = false)
  val htnParsing    = ParsingConfiguration(eliminateEquality = false, stripHybrid = true)

  val parsingConfigs = Map(
                            "-hybrid" -> hybridParsing,
                            "-htn" -> htnParsing
                          )


  val groundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileInitialPlan = true,
                                                       removeUnnecessaryPredicates = false,
                                                       convertToSASP = false, allowSASPFromStrips = false,
                                                       compileOrderInMethods = None,
                                                       splitIndependentParameters = true,
                                                       compileUselessAbstractTasks = true,
                                                       liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = true, groundDomain = true, stopDirectlyAfterGrounding = false)


  val sasPlusPreprocess = PreprocessingConfiguration(compileNegativePreconditions = false, compileUnitMethods = false,
                                                     compileOrderInMethods = None,
                                                     compileInitialPlan = true, splitIndependentParameters = true,
                                                     removeUnnecessaryPredicates = false,
                                                     liftedReachability = true, convertToSASP = true, allowSASPFromStrips = false,
                                                     compileUselessAbstractTasks = true,
                                                     groundedReachability = None,
                                                     groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                     iterateReachabilityAnalysis = false, groundDomain = true, stopDirectlyAfterGrounding = false)

  val sasPlusPreprocessFallback = PreprocessingConfiguration(compileNegativePreconditions = false, compileUnitMethods = false,
                                                             compileOrderInMethods = None,
                                                             compileInitialPlan = true, splitIndependentParameters = true,
                                                             removeUnnecessaryPredicates = false,
                                                             liftedReachability = true, convertToSASP = true, allowSASPFromStrips = true,
                                                             compileUselessAbstractTasks = true,
                                                             groundedReachability = None,
                                                             groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                             iterateReachabilityAnalysis = false, groundDomain = true, stopDirectlyAfterGrounding = false)

  val orderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = Some(AllNecessaryOrderings),
                                                               //compileOrderInMethods = None, //Some(OneRandomOrdering()),
                                                               compileInitialPlan = false, removeUnnecessaryPredicates = false,
                                                               convertToSASP = false, allowSASPFromStrips = false,
                                                               splitIndependentParameters = true,
                                                               compileUselessAbstractTasks = false,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true, stopDirectlyAfterGrounding = false)
  val liftedPreprocess            = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               compileUselessAbstractTasks = false,
                                                               removeUnnecessaryPredicates = false,
                                                               liftedReachability = true, convertToSASP = false, allowSASPFromStrips = false,
                                                               groundedReachability = None,
                                                               groundedTaskDecompositionGraph = None,
                                                               iterateReachabilityAnalysis = false, groundDomain = false, stopDirectlyAfterGrounding = false)

  val oneshortOrderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                                       compileInitialPlan = true,
                                                                       convertToSASP = false,
                                                                       removeUnnecessaryPredicates = false,
                                                                       allowSASPFromStrips = false,
                                                                       compileOrderInMethods = Some(OneOfTheNecessaryOrderings),
                                                                       splitIndependentParameters = true,
                                                                       compileUselessAbstractTasks = true,
                                                                       liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                       iterateReachabilityAnalysis = true, groundDomain = true, stopDirectlyAfterGrounding = false)

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

  def AStarAPRLiftedPR(greediness: Int) = PlanBasedSearch(None, AStarActionsType(greediness), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)

  def AStarAPRLiftedPRReachability(greediness: Int) = PlanBasedSearch(None, AStarActionsType(greediness), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)

  def AStarActionLiftedPR(greediness: Int) = PlanBasedSearch(None, AStarActionsType(greediness), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)

  def AStarActionLiftedPRReachability(greediness: Int) = PlanBasedSearch(None, AStarActionsType(greediness), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

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

  val shop2         = ProgressionSearch(DFSType, None, abstractTaskSelectionStrategy = PriorityQueueSearch.abstractTaskSelection.branchOverAll)
  val shop2Improved = ProgressionSearch(DFSType, None, abstractTaskSelectionStrategy = PriorityQueueSearch.abstractTaskSelection.random)

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
                           "-AStarAPRLiftedPR" -> AStarAPRLiftedPR(1),
                           "-AStarAPRLiftedPRReachability" -> AStarAPRLiftedPRReachability(1),
                           "-AStarActionLiftedPR" -> AStarActionLiftedPR(1),
                           "-AStarActionLiftedPRReachability" -> AStarActionLiftedPRReachability(1),

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
         "-astar-panda-MAC" -> (htnParsing, groundingPreprocess, AStarActionLiftedPR(1)),
         "-astar-panda-MAC-PR" -> (htnParsing, groundingPreprocess, AStarActionLiftedPRReachability(1)),
         "-astar-panda-MME" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPR(1)),
         "-astar-panda-MME-PR" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability(1)),

         "-greedy-panda-MAC" -> (htnParsing, groundingPreprocess, GreedyActionLiftedPR),
         "-greedy-panda-MAC-PR" -> (htnParsing, groundingPreprocess, GreedyActionLiftedPRReachability),
         "-greedy-panda-MME" -> (htnParsing, groundingPreprocess, GreedyAPRLiftedPR),
         "-greedy-panda-MME-PR" -> (htnParsing, groundingPreprocess, GreedyAPRLiftedPRReachability),

         "-gastar-panda-MAC" -> (htnParsing, groundingPreprocess, planSearchAStarActionLiftedPR),
         "-gastar-panda-MAC-PR" -> (htnParsing, groundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "-gastar-panda-MME" -> (htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPR),
         "-gastar-panda-MME-PR" -> (htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPRReachability),

         "-g3astar-panda-MAC" -> (htnParsing, groundingPreprocess, AStarActionLiftedPR(3)),
         "-g3astar-panda-MAC-PR" -> (htnParsing, groundingPreprocess, AStarActionLiftedPRReachability(3)),
         "-g3astar-panda-MME" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPR(3)),
         "-g3astar-panda-MME-PR" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability(3)),

         "-Dijkstra" -> (htnParsing, groundingPreprocess, planSearchDijkstra),
         "-DFS" -> (htnParsing, groundingPreprocess, planSearchDFS),
         "-BFS" -> (htnParsing, groundingPreprocess, planSearchBFS),

         "-umcpBF" -> (htnParsing, groundingPreprocess, umcpBF),
         "-umcpDF" -> (htnParsing, groundingPreprocess, umcpDF),
         "-umcpH" -> (htnParsing, groundingPreprocess, umcpH),

         // SHOP
         "-shop2" -> (htnParsing, sasPlusPreprocess, shop2),
         "-shop2Improved" -> (htnParsing, sasPlusPreprocess, shop2Improved),
         "-shop2-strips" -> (htnParsing, groundingPreprocess, shop2),
         "-shop2Improved-strips" -> (htnParsing, groundingPreprocess, shop2Improved),


         // A*

         "-AStarADD" -> (htnParsing, groundingPreprocess, AStarADD),
         "-AStarADDReusing" -> (htnParsing, groundingPreprocess, AStarADDReusing),
         "-AStarRelax" -> (htnParsing, groundingPreprocess, AStarRelax),
         "-AStarAOpenPreconditions" -> (htnParsing, groundingPreprocess, AStarAOpenPreconditions),

         "-AStarAPRLiftedPR" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPR(1)),
         "-AStarAPRLiftedPRReachability" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability(1)),
         "-AStarActionLiftedPR" -> (htnParsing, groundingPreprocess, AStarActionLiftedPR(1)),
         "-AStarActionLiftedPRReachability" -> (htnParsing, groundingPreprocess, AStarActionLiftedPRReachability(1)),

         // GA*
         "-GAStarADD" -> (htnParsing, groundingPreprocess, planSearchAStarADD),
         "-GAStarADDReusing" -> (htnParsing, groundingPreprocess, planSearchAStarADDReusing),
         "-GAStarRelax" -> (htnParsing, groundingPreprocess, planSearchAStarRelax),
         "-GAStarAOpenPreconditions" -> (htnParsing, groundingPreprocess, planSearchAStarAOpenPreconditions),

         "-GAStarActionLiftedPR" -> (htnParsing, groundingPreprocess, planSearchAStarActionLiftedPR),
         "-GAStarActionLiftedPRReachability" -> (htnParsing, groundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "-GAStarAPRLiftedPR" -> (htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPR),
         "-GAStarAPRLiftedPRReachability" -> (htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPRReachability),

         //  compare

         "-AStar-MAC-Recompute-Compare" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPRCompare),
         "-AStar-PR-Recompute-Compare" -> (htnParsing, groundingPreprocess, AStarActionLiftedPRCompare),
         "-GreedyAStar-MAC-Recompute-Compare" -> (htnParsing, groundingPreprocess, greedyAStarAPRLiftedPRCompare),
         "-GreedyAStar-PR-Recompute-Compare" -> (htnParsing, groundingPreprocess, greedyAStarActionLiftedPRCompare),

         // PRO
         "-GreedyAStarPro-hhRC-lm-cut" -> (htnParsing, sasPlusPreprocess, ProgressionSearch(AStarActionsType(2),
                                                                                            Some(HierarchicalHeuristicRelaxedComposition(SasHeuristics.hLmCut)), PriorityQueueSearch
                                                                                              .abstractTaskSelection.random)),


         // configurations to test totSAT
         "-poclDirectsat" -> (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise,
                                                                         encodingToUse = POCLDirectEncoding)),
         "-poclDirectsatRiss6" -> (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = POCLDirectEncoding)),
         "-poclDirectsatMaple" -> (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise,
                                                                              encodingToUse = POCLDirectEncoding)),

         "-poclDeletesat" -> (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse =
           POCLDeleterEncoding)),
         "-poclDeletesatRiss6" -> (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = POCLDeleterEncoding)),
         "-poclDeletesatMaple" -> (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse =
           POCLDeleterEncoding)),

         /*"-satFF" -> (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = FFReduction)),
         "-satFFRiss6" -> (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = FFReduction)),
         "-satFFMaple" -> (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = FFReduction)),
         "-satH2" -> (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = H2Reduction)),
         "-satH2Riss6" -> (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = H2Reduction)),
         "-satH2Maple" -> (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = H2Reduction)),
         "-satFFFull" -> (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = FFReductionWithFullTest)),
         "-satFFFullRiss6" -> (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = FFReductionWithFullTest)),
         "-satFFFullMaple" -> (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod =        FFReductionWithFullTest)),
         */


         "-treeBeforeSat" ->
           (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = TreeBeforeEncoding)),
         "-treeBeforeSatRiss6" ->
           (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = TreeBeforeEncoding)),
         "-treeBeforeSatMaple" ->
           (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = TreeBeforeEncoding)),

         "-classicalFSat" ->
           (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalForbiddenEncoding)),
         "-classicalFSatRiss6" ->
           (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalForbiddenEncoding)),
         "-classicalFSatMaple" ->
           (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalForbiddenEncoding)),

         "-classicalFImpSat" ->
           (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalImplicationEncoding)),
         "-classicalFImpSatRiss6" ->
           (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalImplicationEncoding)),
         "-classicalFImpSatMaple" ->
           (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalImplicationEncoding)),

         "-classicalN4Sat" ->
           (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalN4Encoding)),
         "-classicalN4SatRiss6" ->
           (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalN4Encoding)),
         "-classicalN4SatMaple" ->
           (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = ClassicalN4Encoding)),

         "-statePOSat" -> (htnParsing, groundingPreprocess, SATSearch(CRYPTOMINISAT, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = POStateEncoding)),
         "-statePOSatRiss6" -> (htnParsing, groundingPreprocess, SATSearch(RISS6, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = POStateEncoding)),
         "-statePOSatMaple" -> (htnParsing, groundingPreprocess, SATSearch(MapleCOMSPS, FullSATRun(), checkResult = true, reductionMethod = OnlyNormalise, encodingToUse = POStateEncoding)),


         // plan verification a la ICAPS'17
         "-verify" -> (htnParsing, groundingPreprocess, SATPlanVerification(CRYPTOMINISAT, "")),


         ///// PANDA Pro
         "-astar-pro-CG" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hCG)),
         "-astar-pro-FF" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hFF)),
         "-astar-pro-FF-ha" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hFFwithHA)),
         "-astar-pro-lmcut" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hLmCut)),
         "-astar-pro-lmcut-inc" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hIncLmCut)),
         "-astar-pro-add" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hAdd)),

         "-astar-pro-strips-FF" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hFF)),
         "-astar-pro-strips-lmcut" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hLmCut)),
         "-astar-pro-strips-add" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hAdd)),

         "-gastar-pro-CG" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hCG)),
         "-gastar-pro-FF" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hFF)),
         "-gastar-pro-FF-ha" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hFFwithHA)),
         "-gastar-pro-lmcut" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hLmCut)),
         "-gastar-pro-lmcut-inc" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hIncLmCut)),
         "-gastar-pro-add" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hAdd)),

         "-gastar-pro-strips-FF" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hFF)),
         "-gastar-pro-strips-lmcut" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hLmCut)),
         "-gastar-pro-strips-add" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(2), SasHeuristics.hAdd)),

         "-g3astar-pro-FF" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hFF)),
         "-g3astar-pro-lmcut" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hLmCut)),
         "-g3astar-pro-lmcut-inc" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hIncLmCut)),
         "-g3astar-pro-add" -> (htnParsing, sasPlusPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hAdd)),

         "-g3astar-pro-strips-FF" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hFF)),
         "-g3astar-pro-strips-lmcut" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hLmCut)),
         "-g3astar-pro-strips-add" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(3), SasHeuristics.hAdd)),

         "-pro-strips-filter" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hFilter)),
         "-pro-lmcut-opt" -> (htnParsing, groundingPreprocess, pandaProConfig(AStarActionsType(1), SasHeuristics.hLmCutOpt)),

         "-greedy-pro-CG" -> (htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hCG)),
         "-greedy-pro-FF" -> (htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hFF)),
         "-greedy-pro-FF-ha" -> (htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hFFwithHA)),
         "-greedy-pro-lmcut" -> (htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hLmCut)),
         "-greedy-pro-lmcut-inc" -> (htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hIncLmCut)),
         "-greedy-pro-add" -> (htnParsing, sasPlusPreprocess, pandaProConfig(GreedyType, SasHeuristics.hAdd)),

         "-greedy-pro-strips-FF" -> (htnParsing, groundingPreprocess, pandaProConfig(GreedyType, SasHeuristics.hFF)),
         "-greedy-pro-strips-lmcut" -> (htnParsing, groundingPreprocess, pandaProConfig(GreedyType, SasHeuristics.hLmCut)),
         "-greedy-pro-strips-add" -> (htnParsing, groundingPreprocess, pandaProConfig(GreedyType, SasHeuristics.hAdd)),

         "-shop2Original" -> (htnParsing, groundingPreprocess, SHOP2Search),
         "-shop2OriginalLifted" -> (htnParsing, liftedPreprocess, SHOP2Search),

         "-fape" -> (htnParsing, groundingPreprocess, FAPESearch),
         "-fapeLifted" -> (htnParsing, liftedPreprocess, FAPESearch)
       )

}

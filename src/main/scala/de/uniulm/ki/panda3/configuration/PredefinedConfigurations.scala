package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.symbolic.compiler.AllNecessaryOrderings

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PredefinedConfigurations {

  val globalTimelimit = 30 * 60

  val hybridParsing = ParsingConfiguration(eliminateEquality = true, stripHybrid = false)
  val htnParsing    = ParsingConfiguration(eliminateEquality = true, stripHybrid = true)

  val parsingConfigs = Map(
                            "-hybrid" -> hybridParsing,
                            "-htn" -> htnParsing
                          )

  val orderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = Some(AllNecessaryOrderings),
                                                               //compileOrderInMethods = None, //Some(OneRandomOrdering()),
                                                               splitIndependedParameters = true,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true)
  val groundingPreprocess         = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               splitIndependedParameters = true,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true)
  val liftedPreprocess            = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               splitIndependedParameters = true,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = false)

  val preprocessConfigs = Map(
                               "-ordering" -> orderingGroundingPreprocess,
                               "-ground" -> groundingPreprocess,
                               "-lifted" -> liftedPreprocess
                             )


  // Greedy
  val GreedyADD                = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, ADD :: Nil, Nil, LCFR)
  val GreedyADDReusing         = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, ADDReusing :: Nil, Nil, LCFR)
  val GreedyRelax              = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Relax :: Nil, Nil, LCFR)
  val GreedyAOpenPreconditions = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val GreedyAPRLiftedPR                = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val GreedyAPRLiftedPRReachability    = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  // A*
  val AStarADD                = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), ADD :: Nil, Nil, LCFR)
  val AStarADDReusing         = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), ADDReusing :: Nil, Nil, LCFR)
  val AStarRelax              = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), Relax :: Nil, Nil, LCFR)
  val AStarAOpenPreconditions = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val AStarAPRLiftedPR                = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val AStarAPRLiftedPRReachability    = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val AStarActionLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val AStarActionLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  // GREEDY A*
  val planSearchAStarPR            = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), TDGPreconditionRelaxation() :: Nil, Nil, LCFR)
  val planSearchAStarAPR           = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGPreconditionRelaxation() :: Nil, Nil, LCFR)
  val planSearchAStarTDGAction     = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGMinimumAction() :: Nil, Nil, LCFR)
  val planSearchAStarTDGADD        = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGMinimumADD() :: Nil, Nil, LCFR)
  val planSearchAStarTDGADDReusing = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGMinimumADD(Some(ADDReusing)) :: Nil, Nil, LCFR)


  val planSearchAStarPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarAPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarAPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarAPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarActionLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarActionLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarActionLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumAction(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarADDLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarADDLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarADDLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarADDReusingLiftedPR = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(NeverRecompute, Some(ADDReusing)) :: Nil, Nil, LCFR)

  val planSearchDijkstra                = PlanBasedSearch(None, Some(globalTimelimit), DijkstraType, Nil, Nil, LCFR)
  val planSearchDFS                     = PlanBasedSearch(None, Some(globalTimelimit), DFSType, Nil, Nil, LCFR)
  val planSearchBFS                     = PlanBasedSearch(None, Some(globalTimelimit), BFSType, Nil, Nil, LCFR)
  val planSearchAStarOpenPreconditions  = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), NumberOfOpenPreconditions :: Nil, Nil, LCFR)
  val planSearchAStarAOpenPreconditions = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val umcpBF = PlanBasedSearch(None, Some(globalTimelimit), BFSType, Nil, Nil, UMCPFlaw)
  val umcpDF = PlanBasedSearch(None, Some(globalTimelimit), DFSType, Nil, Nil, UMCPFlaw)
  val umcpH  = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, UMCPHeuristic :: Nil, Nil, UMCPFlaw)

  val planSearchAStarADD        = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), ADD :: Nil, Nil, LCFR)
  val planSearchAStarADDReusing = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), ADDReusing :: Nil, Nil, LCFR)
  val planSearchAStarRelax      = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), Relax :: Nil, Nil, LCFR)


  val searchConfigs = Map(
                           "-GAStarPR" -> planSearchAStarPR,
                           "-GAStarAPR" -> planSearchAStarAPR,
                           "-GAStarTDGAction" -> planSearchAStarTDGAction,
                           "-GAStarTDGADD" -> planSearchAStarTDGADD,
                           "-GAStarTDGADDReusing" -> planSearchAStarTDGADDReusing,

                           "-GAStarPRLiftedPR" -> planSearchAStarPRLiftedPR,
                           "-GAStarPRLiftedPRReachability" -> planSearchAStarPRLiftedPRReachability,
                           "-GAStarPRLiftedPRCausalLink" -> planSearchAStarPRLiftedPRCausalLink,

                           "-GAStarAPRLiftedPR" -> planSearchAStarAPRLiftedPR,
                           "-GAStarAPRLiftedPRReachability" -> planSearchAStarAPRLiftedPRReachability,
                           "-GAStarAPRLiftedPRCausalLink" -> planSearchAStarAPRLiftedPRCausalLink,

                           "-GAStarActionLiftedPR" -> planSearchAStarActionLiftedPR,
                           "-GAStarActionLiftedPRReachability" -> planSearchAStarActionLiftedPRReachability,
                           "-GAStarActionLiftedPRCausalLink" -> planSearchAStarActionLiftedPRCausalLink,

                           "-GAStarADDLiftedPR" -> planSearchAStarADDLiftedPR,
                           "-GAStarADDLiftedPRReachability" -> planSearchAStarADDLiftedPRReachability,
                           "-GAStarADDLiftedPRCausalLink" -> planSearchAStarADDLiftedPRCausalLink,

                           "-GAStarADDReusingLiftedPR" -> planSearchAStarADDReusingLiftedPR,

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

}
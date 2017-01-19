package de.uniulm.ki.panda3.configuration

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PredefinedConfigurations {

  val globalTimelimit = 10 * 60

  val hybridParsing = ParsingConfiguration(eliminateEquality = true, stripHybrid = false)
  val htnParsing    = ParsingConfiguration(eliminateEquality = true, stripHybrid = true)

  val parsingConfigs = Map(
                            "-hybrid" -> hybridParsing,
                            "-htn" -> htnParsing
                          )

  val groundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileOrderInMethods = None,
                                                       splitIndependedParameters = false,
                                                       liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = false, groundDomain = true)
  val liftedPreprocess    = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileOrderInMethods = None,
                                                       splitIndependedParameters = false,
                                                       liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = false, groundDomain = false)

  val preprocessConfigs = Map(
                               "-ground" -> groundingPreprocess,
                               "-lifted" -> liftedPreprocess
                             )


  val planSearchAStarPR            = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), TDGPreconditionRelaxation() :: Nil , Nil, LCFR)
  val planSearchAStarAPR            = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGPreconditionRelaxation() :: Nil , Nil, LCFR)
  val planSearchAStarTDGAction     = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGMinimumAction() :: Nil , Nil, LCFR)
  val planSearchAStarTDGADD        = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGMinimumADD() :: Nil , Nil, LCFR)
  val planSearchAStarTDGADDReusing = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), TDGMinimumADD(Some(ADDReusing)) :: Nil , Nil, LCFR)


  val planSearchAStarPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil , Nil, LCFR)

  val planSearchAStarAPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarAPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarAPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil , Nil, LCFR)

  val planSearchAStarActionLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumAction(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarActionLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarActionLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumAction(CausalLinkRecompute) :: Nil , Nil, LCFR)

  val planSearchAStarADDLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarADDLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(ReachabilityRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarADDLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(CausalLinkRecompute) :: Nil , Nil, LCFR)

  val planSearchAStarADDReusingLiftedPR = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), LiftedTDGMinimumADD(NeverRecompute, Some(ADDReusing)) :: Nil , Nil, LCFR)

  val planSearchDijkstra               = PlanBasedSearch(None, Some(globalTimelimit), DijkstraType, Nil, Nil, LCFR)
  val planSearchDFS                    = PlanBasedSearch(None, Some(globalTimelimit), DFSType, Nil, Nil, LCFR)
  val planSearchBFS                    = PlanBasedSearch(None, Some(globalTimelimit), BFSType, Nil, Nil, LCFR)
  val planSearchAStarOpenPreconditions = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType(2), NumberOfOpenPreconditions :: Nil , Nil, LCFR)
  val planSearchAStarAOpenPreconditions = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), NumberOfOpenPreconditions :: Nil , Nil, LCFR)

  val umcpBF = PlanBasedSearch(None, Some(globalTimelimit), BFSType, Nil, Nil, UMCPFlaw)
  val umcpDF = PlanBasedSearch(None, Some(globalTimelimit), DFSType, Nil, Nil, UMCPFlaw)
  val umcpH  = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, UMCPHeuristic :: Nil , Nil, UMCPFlaw)

  val planSearchAStarADD        = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), ADD :: Nil , Nil, LCFR)
  val planSearchAStarADDReusing = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), ADDReusing :: Nil , Nil, LCFR)
  val planSearchAStarRelax      = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(2), Relax :: Nil , Nil, LCFR)


  val searchConfigs = Map(
                           "-AStarPR" -> planSearchAStarPR,
                           "-AStarAPR" -> planSearchAStarAPR,
                           "-AStarTDGAction" -> planSearchAStarTDGAction,
                           "-AStarTDGADD" -> planSearchAStarTDGADD,
                           "-planSearchAStarTDGADDReusing" -> planSearchAStarTDGADDReusing,

                           "-AStarPRLiftedPR" -> planSearchAStarPRLiftedPR,
                           "-AStarPRLiftedPRReachability" -> planSearchAStarPRLiftedPRReachability,
                           "-AStarPRLiftedPRCausalLink" -> planSearchAStarPRLiftedPRCausalLink,

                           "-AStarAPRLiftedPR" -> planSearchAStarAPRLiftedPR,
                           "-AStarAPRLiftedPRReachability" -> planSearchAStarAPRLiftedPRReachability,
                           "-AStarAPRLiftedPRCausalLink" -> planSearchAStarAPRLiftedPRCausalLink,

                           "-AStarActionLiftedPR" -> planSearchAStarActionLiftedPR,
                           "-AStarActionLiftedPRReachability" -> planSearchAStarActionLiftedPRReachability,
                           "-AStarActionLiftedPRCausalLink" -> planSearchAStarActionLiftedPRCausalLink,

                           "-AStarADDLiftedPR" -> planSearchAStarADDLiftedPR,
                           "-AStarADDLiftedPRReachability" -> planSearchAStarADDLiftedPRReachability,
                           "-AStarADDLiftedPRCausalLink" -> planSearchAStarADDLiftedPRCausalLink,

                           "-planSearchAStarADDReusingLiftedPR" -> planSearchAStarADDReusingLiftedPR,

                           "-Dijkstra" -> planSearchDijkstra,
                           "-DFS" -> planSearchDFS,
                           "-BFS" -> planSearchBFS,

                           "-AStarOpenPreconditions" -> planSearchAStarOpenPreconditions,
                           "-AStarAOpenPreconditions" -> planSearchAStarAOpenPreconditions,

                           "-umcpBF" -> umcpBF,
                           "-umcpDF" -> umcpDF,
                           "-umcpH" -> umcpH,

                           "-planSearchAStarADD" -> planSearchAStarADD,
                           "-planSearchAStarADDReusing" -> planSearchAStarADDReusing,
                           "-planSearchAStarRelax" -> planSearchAStarRelax

                         )


  val planSearchAStarMMESCC           = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), TDGMinimumModificationWithCycleDetection() :: Nil , Nil, LCFR)
  val planSearchAStarPRLiftedPRFilter = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil,
                                                        RecomputeHierarchicalReachability :: Nil, LCFR)
  val planSearchAStarMMESCCLifted     = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGMinimumModificationWithCycleDetection(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchAStarActionsLifted    = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil , Nil, LCFR)


  val planSearchGreedyPR                     = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, TDGPreconditionRelaxation() :: Nil , Nil, LCFR)
  val planSearchGreedyMMESCC                 = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, TDGMinimumModificationWithCycleDetection() :: Nil , Nil, LCFR)
  val planSearchGreedyTDGADD                 = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, TDGMinimumADD() :: Nil , Nil, LCFR)
  val planSearchGreedyPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchGreedyPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil , Nil, LCFR)
  val planSearchGreedyPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil , Nil, LCFR)
  val planSearchGreedyPRLiftedPRFilter       = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil,
                                                               RecomputeHierarchicalReachability :: Nil, LCFR)
  val planSearchGreedyMMESCCLifted           = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGMinimumModificationWithCycleDetection(NeverRecompute) :: Nil , Nil, LCFR)
  val planSearchGreedyActionsLifted          = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, LiftedTDGMinimumAction(NeverRecompute) :: Nil , Nil, LCFR)
}
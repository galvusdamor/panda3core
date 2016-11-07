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
                                                       liftedReachability = true, groundedReachability = false, planningGraph = true,
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = false, groundDomain = true)
  val liftedPreprocess    = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileOrderInMethods = None,
                                                       splitIndependedParameters = false,
                                                       liftedReachability = true, groundedReachability = false, planningGraph = true,
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = false, groundDomain = false)

  val preprocessConfigs = Map(
                               "-ground" -> groundingPreprocess,
                               "-lifted" -> liftedPreprocess
                             )


  val planSearchAStarPR        = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType, Some(TDGPreconditionRelaxation), Nil, LCFR)
  val planSearchAStarTDGAction = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(TDGMinimumAction), Nil, LCFR)
  val planSearchAStarTDGADD    = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(TDGMinimumADD), Nil, LCFR)


  val planSearchAStarPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType, Some(LiftedTDGPreconditionRelaxation(NeverRecompute)), Nil, LCFR)
  val planSearchAStarPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGPreconditionRelaxation(ReachabilityRecompute)), Nil, LCFR)
  val planSearchAStarPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGPreconditionRelaxation(CausalLinkRecompute)), Nil, LCFR)

  val planSearchAStarActionLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType, Some(LiftedTDGMinimumAction(NeverRecompute)), Nil, LCFR)
  val planSearchAStarActionLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGMinimumAction(ReachabilityRecompute)), Nil, LCFR)
  val planSearchAStarActionLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGMinimumAction(CausalLinkRecompute)), Nil, LCFR)

  val planSearchAStarADDLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), AStarDepthType, Some(LiftedTDGMinimumADD(NeverRecompute)), Nil, LCFR)
  val planSearchAStarADDLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGMinimumADD(ReachabilityRecompute)), Nil, LCFR)
  val planSearchAStarADDLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGMinimumADD(CausalLinkRecompute)), Nil, LCFR)

  val planSearchDijkstra               = PlanBasedSearch(None, Some(globalTimelimit), DijkstraType, None, Nil, LCFR)
  val planSearchDFS                    = PlanBasedSearch(None, Some(globalTimelimit), DFSType, None, Nil, LCFR)
  val planSearchBFS                    = PlanBasedSearch(None, Some(globalTimelimit), BFSType, None, Nil, LCFR)
  val planSearchAStarOpenPreconditions = PlanBasedSearch(None, Some(globalTimelimit), DijkstraType, Some(NumberOfOpenPreconditions), Nil, LCFR)

  val umcpBF = PlanBasedSearch(None, Some(globalTimelimit), BFSType, None, Nil, UMCPFlaw)
  val umcpDF = PlanBasedSearch(None, Some(globalTimelimit), DFSType, None, Nil, UMCPFlaw)
  val umcpH  = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(UMCPHeuristic), Nil, UMCPFlaw)


  val searchConfigs = Map(
                           "-AStarPR" -> planSearchAStarPR,
                           "-AStarTDGAction" -> planSearchAStarTDGAction,
                           "-AStarTDGADD" -> planSearchAStarTDGADD,

                           "-AStarPRLiftedPR" -> planSearchAStarPRLiftedPR,
                           "-AStarPRLiftedPRReachability" -> planSearchAStarPRLiftedPRReachability,
                           "-AStarPRLiftedPRCausalLink" -> planSearchAStarPRLiftedPRCausalLink,

                           "-AStarActionLiftedPR" -> planSearchAStarActionLiftedPR,
                           "-AStarActionLiftedPRReachability" -> planSearchAStarActionLiftedPRReachability,
                           "-AStarActionLiftedPRCausalLink" -> planSearchAStarActionLiftedPRCausalLink,

                           "-AStarADDLiftedPR" -> planSearchAStarADDLiftedPR,
                           "-AStarADDLiftedPRReachability" -> planSearchAStarADDLiftedPRReachability,
                           "-AStarADDLiftedPRCausalLink" -> planSearchAStarADDLiftedPRCausalLink,

                           "-Dijkstra" -> planSearchDijkstra,
                           "-DFS" -> planSearchDFS,
                           "-BFS" -> planSearchBFS,

                           "-AStarOpenPreconditions" -> planSearchAStarOpenPreconditions,

                           "-umcpBF" -> umcpBF,
                           "-umcpDF" -> umcpDF,
                           "-umcpH" -> umcpH
                         )


  val planSearchAStarMMESCC           = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(TDGMinimumModificationWithCycleDetection), Nil, LCFR)
  val planSearchAStarPRLiftedPRFilter = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGPreconditionRelaxation(NeverRecompute)),
                                                        RecomputeHierarchicalReachability :: Nil, LCFR)
  val planSearchAStarMMESCCLifted     = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGMinimumModificationWithCycleDetection(NeverRecompute)), Nil, LCFR)
  val planSearchAStarActionsLifted    = PlanBasedSearch(None, Some(globalTimelimit), AStarActionsType, Some(LiftedTDGMinimumAction(NeverRecompute)), Nil, LCFR)


  val planSearchGreedyPR                     = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(TDGPreconditionRelaxation), Nil, LCFR)
  val planSearchGreedyMMESCC                 = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(TDGMinimumModificationWithCycleDetection), Nil, LCFR)
  val planSearchGreedyTDGADD                 = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(TDGMinimumADD), Nil, LCFR)
  val planSearchGreedyPRLiftedPR             = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(LiftedTDGPreconditionRelaxation(NeverRecompute)), Nil, LCFR)
  val planSearchGreedyPRLiftedPRReachability = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(LiftedTDGPreconditionRelaxation(ReachabilityRecompute)), Nil, LCFR)
  val planSearchGreedyPRLiftedPRCausalLink   = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(LiftedTDGPreconditionRelaxation(CausalLinkRecompute)), Nil, LCFR)
  val planSearchGreedyPRLiftedPRFilter       = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(LiftedTDGPreconditionRelaxation(NeverRecompute)),
                                                               RecomputeHierarchicalReachability :: Nil, LCFR)
  val planSearchGreedyMMESCCLifted           = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(LiftedTDGMinimumModificationWithCycleDetection(NeverRecompute)), Nil, LCFR)
  val planSearchGreedyActionsLifted          = PlanBasedSearch(None, Some(globalTimelimit), GreedyType, Some(LiftedTDGMinimumAction(NeverRecompute)), Nil, LCFR)


}
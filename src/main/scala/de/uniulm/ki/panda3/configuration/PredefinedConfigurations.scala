package de.uniulm.ki.panda3.configuration

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


  val groundingPreprocess         = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true)
  val orderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = Some(AllNecessaryOrderings),
                                                               //compileOrderInMethods = None, //Some(OneRandomOrdering()),
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true)
  val liftedPreprocess            = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = false)

  val preprocessConfigs = Map(
                               "-ordering" -> orderingGroundingPreprocess,
                               "-ground" -> groundingPreprocess,
                               "-lifted" -> liftedPreprocess
                             )


  val defaultConfigurations: Map[String, (ParsingConfiguration, PreprocessingConfiguration, SearchConfiguration)] =
    Map(
         "-panda-MAC" ->(htnParsing, groundingPreprocess, PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR))
       )


  // TODO old stuff ... should probably be deleted


  val globalTimelimit = 30 * 60


  // Greedy
  val GreedyADD                = PlanBasedSearch(None, GreedyType, ADD :: Nil, Nil, LCFR)
  val GreedyADDReusing         = PlanBasedSearch(None, GreedyType, ADDReusing :: Nil, Nil, LCFR)
  val GreedyRelax              = PlanBasedSearch(None, GreedyType, Relax :: Nil, Nil, LCFR)
  val GreedyAOpenPreconditions = PlanBasedSearch(None, GreedyType, NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val GreedyAPRLiftedPR                = PlanBasedSearch(None, GreedyType, LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val GreedyAPRLiftedPRReachability    = PlanBasedSearch(None, GreedyType, LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPR             = PlanBasedSearch(None, GreedyType, LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val GreedyActionLiftedPRReachability = PlanBasedSearch(None, GreedyType, LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  // A*
  val AStarADD                = PlanBasedSearch(None, AStarActionsType(1), ADD :: Nil, Nil, LCFR)
  val AStarADDReusing         = PlanBasedSearch(None, AStarActionsType(1), ADDReusing :: Nil, Nil, LCFR)
  val AStarRelax              = PlanBasedSearch(None, AStarActionsType(1), Relax :: Nil, Nil, LCFR)
  val AStarAOpenPreconditions = PlanBasedSearch(None, AStarActionsType(1), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  val AStarAPRLiftedPR                = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val AStarAPRLiftedPRReachability    = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val AStarActionLiftedPR             = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val AStarActionLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  // GREEDY A*
  val planSearchAStarPR            = PlanBasedSearch(None, AStarDepthType(2), TDGPreconditionRelaxation() :: Nil, Nil, LCFR)
  val planSearchAStarAPR           = PlanBasedSearch(None, AStarActionsType(2), TDGPreconditionRelaxation() :: Nil, Nil, LCFR)
  val planSearchAStarTDGAction     = PlanBasedSearch(None, AStarActionsType(2), TDGMinimumAction() :: Nil, Nil, LCFR)
  val planSearchAStarTDGADD        = PlanBasedSearch(None, AStarActionsType(2), TDGMinimumADD() :: Nil, Nil, LCFR)
  val planSearchAStarTDGADDReusing = PlanBasedSearch(None, AStarActionsType(2), TDGMinimumADD(Some(ADDReusing)) :: Nil, Nil, LCFR)


  val planSearchAStarPRLiftedPR             = PlanBasedSearch(None, AStarDepthType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarPRLiftedPRReachability = PlanBasedSearch(None, AStarDepthType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarPRLiftedPRCausalLink   = PlanBasedSearch(None, AStarDepthType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarAPRLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarAPRLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarAPRLiftedPRCausalLink   = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarActionLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarActionLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarActionLiftedPRCausalLink   = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarADDLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumADD(NeverRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarADDLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumADD(ReachabilityRecompute) :: Nil, Nil, LCFR)
  val planSearchAStarADDLiftedPRCausalLink   = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumADD(CausalLinkRecompute) :: Nil, Nil, LCFR)

  val planSearchAStarADDReusingLiftedPR = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumADD(NeverRecompute, Some(ADDReusing)) :: Nil, Nil, LCFR)

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
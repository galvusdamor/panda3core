// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2017 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.configuration

import de.uniulm.ki.panda3.symbolic.compiler.OneOfTheNecessaryOrderings
import de.uniulm.ki.panda3.symbolic.compiler.AllNecessaryOrderings


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PredefinedConfigurations {

  val hybridParsing = ParsingConfiguration(eliminateEquality = true, stripHybrid = false)
  val htnParsing    = ParsingConfiguration(eliminateEquality = true, stripHybrid = true)

  val parsingConfigs = Map(
                            "hybrid" -> hybridParsing,
                            "htn" -> htnParsing
                          )


  val groundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileInitialPlan = true,
                                                       compileOrderInMethods = None,
                                                       splitIndependentParameters = true,
                                                       compileUselessAbstractTasks = true,
                                                       liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = true, groundDomain = true)

  val orderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = Some(AllNecessaryOrderings),
                                                               //compileOrderInMethods = None, //Some(OneRandomOrdering()),
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               compileUselessAbstractTasks = false,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = true)

  val liftedPreprocess            = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                               compileOrderInMethods = None,
                                                               compileInitialPlan = false, splitIndependentParameters = true,
                                                               compileUselessAbstractTasks = false,
                                                               liftedReachability = true, groundedReachability = Some(PlanningGraphWithMutexes),
                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                               iterateReachabilityAnalysis = false, groundDomain = false)

  val oneshortOrderingGroundingPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                                       compileInitialPlan = true,
                                                                       compileOrderInMethods = Some(OneOfTheNecessaryOrderings),
                                                                       splitIndependentParameters = true,
                                                                       compileUselessAbstractTasks = true,
                                                                       liftedReachability = true, groundedReachability = Some(PlanningGraph),
                                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                       iterateReachabilityAnalysis = true, groundDomain = true)

  val oneshortOrderingGroundingPreprocessWithSASPlus = PreprocessingConfiguration(compileNegativePreconditions = false, compileUnitMethods = false,
                                                                                  compileInitialPlan = true,
                                                                                  compileOrderInMethods = Some(OneOfTheNecessaryOrderings),
                                                                                  splitIndependentParameters = true,
                                                                                  compileUselessAbstractTasks = true,
                                                                                  liftedReachability = true, groundedReachability = None,
                                                                                  groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                                  iterateReachabilityAnalysis = true, groundDomain = true)


  val preprocessConfigs = Map(
                               "ordering" -> orderingGroundingPreprocess,
                               "ground" -> groundingPreprocess,
                               "lifted" -> liftedPreprocess
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


  val defaultConfigurations: Map[String, (ParsingConfiguration, PreprocessingConfiguration, SearchConfiguration)] =
    Map(
         //"panda-MAC" ->(htnParsing, groundingPreprocess, PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)),
         "IJCAI-2017-AdmissibleHeuristics(uniform)" ->(htnParsing, groundingPreprocess, planSearchDijkstra),
         "IJCAI-2017-AdmissibleHeuristics(DFS)" ->(htnParsing, groundingPreprocess, planSearchDFS),
         "IJCAI-2017-AdmissibleHeuristics(BFS)" ->(htnParsing, groundingPreprocess, planSearchBFS),

      "IJCAI-2017-AdmissibleHeuristics(umcp-BF)" ->(htnParsing, groundingPreprocess, umcpBF),
      "IJCAI-2017-AdmissibleHeuristics(umcp-DF)" ->(htnParsing, groundingPreprocess, umcpDF),
      "IJCAI-2017-AdmissibleHeuristics(umcp-H)" ->(htnParsing, groundingPreprocess, umcpH),

         "umcp(BF)" ->(htnParsing, groundingPreprocess, umcpBF),
         "umcp(DF)" ->(htnParsing, groundingPreprocess, umcpDF),
         "umcp(h)" ->(htnParsing, groundingPreprocess, umcpH),


         // A*
         "IJCAI-2017-AdmissibleHeuristics(astar,add)" ->(htnParsing, groundingPreprocess, AStarADD),
         "IJCAI-2017-AdmissibleHeuristics(astar,add-r)" ->(htnParsing, groundingPreprocess, AStarADDReusing),
         "IJCAI-2017-AdmissibleHeuristics(astar,relax)" ->(htnParsing, groundingPreprocess, AStarRelax),
         "IJCAI-2017-AdmissibleHeuristics(astar,oc)" ->(htnParsing, groundingPreprocess, AStarAOpenPreconditions),

         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-c)" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-c-rec)" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability),
         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-m)" ->(htnParsing, groundingPreprocess, AStarActionLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-m-rec)" ->(htnParsing, groundingPreprocess, AStarActionLiftedPRReachability),

         // GA*
         "IJCAI-2017-AdmissibleHeuristics(gastar,add)" ->(htnParsing, groundingPreprocess, planSearchAStarADD),
         "IJCAI-2017-AdmissibleHeuristics(gastar,add-r)" ->(htnParsing, groundingPreprocess, planSearchAStarADDReusing),
         "IJCAI-2017-AdmissibleHeuristics(gastar,relax)" ->(htnParsing, groundingPreprocess, planSearchAStarRelax),
         "IJCAI-2017-AdmissibleHeuristics(gastar,oc)" ->(htnParsing, groundingPreprocess, planSearchAStarAOpenPreconditions),

         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-c)" ->(htnParsing, groundingPreprocess, planSearchAStarActionLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-c-rec)" ->(htnParsing, groundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-m)" ->(htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-m-rec)" ->(htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPRReachability),

         //  compare

         "IJCAI-2017-astar-TDG-m-Recompute-Compare" ->(htnParsing, groundingPreprocess, AStarAPRLiftedPRCompare),
         "IJCAI-2017-astar-TDG-c-Recompute-Compare" ->(htnParsing, groundingPreprocess, AStarActionLiftedPRCompare),
         "IJCAI-2017-gastar-TDG-m-Recompute-Compare" ->(htnParsing, groundingPreprocess, greedyAStarAPRLiftedPRCompare),
         "IJCAI-2017-gastar-TDG-c-Recompute-Compare" ->(htnParsing, groundingPreprocess, greedyAStarActionLiftedPRCompare),


         // plan verification á la ICAPS'17
         "ICAPS-2017-verify" ->(htnParsing, groundingPreprocess, SATPlanVerification(CRYPTOMINISAT, ""))

       )

}
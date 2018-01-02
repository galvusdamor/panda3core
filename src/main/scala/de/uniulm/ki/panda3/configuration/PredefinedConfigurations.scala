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

  val liftedPreprocess = PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
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

  // A*
  private val AStarADD                = PlanBasedSearch(None, AStarActionsType(1), ADD :: Nil, Nil, LCFR)
  private val AStarADDReusing         = PlanBasedSearch(None, AStarActionsType(1), ADDReusing :: Nil, Nil, LCFR)
  private val AStarRelax              = PlanBasedSearch(None, AStarActionsType(1), Relax :: Nil, Nil, LCFR)
  private val AStarAOpenPreconditions = PlanBasedSearch(None, AStarActionsType(1), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  private val AStarAPRLiftedPR                = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  private val AStarAPRLiftedPRReachability    = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)
  private val AStarActionLiftedPR             = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  private val AStarActionLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)

  private val AStarAPRLiftedPRCompare    = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true) :: Nil, Nil, LCFR)
  private val AStarActionLiftedPRCompare = PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false) :: Nil, Nil, LCFR)

  // GREEDY A*
  private val planSearchAStarAPRLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(NeverRecompute) :: Nil, Nil, LCFR)
  private val planSearchAStarAPRLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGPreconditionRelaxation(ReachabilityRecompute) :: Nil, Nil, LCFR)

  private val planSearchAStarActionLiftedPR             = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)
  private val planSearchAStarActionLiftedPRReachability = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumAction(ReachabilityRecompute) :: Nil, Nil, LCFR)


  private val greedyAStarAPRLiftedPRCompare    = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = true) :: Nil, Nil, LCFR)
  private val greedyAStarActionLiftedPRCompare = PlanBasedSearch(None, AStarActionsType(2), LiftedTDGMinimumActionCompareToWithoutRecompute(usePR = false) :: Nil, Nil, LCFR)


  private val planSearchDijkstra                = PlanBasedSearch(None, DijkstraType, Nil, Nil, LCFR)
  private val planSearchDFS                     = PlanBasedSearch(None, DFSType, Nil, Nil, LCFR)
  private val planSearchBFS                     = PlanBasedSearch(None, BFSType, Nil, Nil, LCFR)
  private val planSearchAStarAOpenPreconditions = PlanBasedSearch(None, AStarActionsType(2), NumberOfOpenPreconditions :: Nil, Nil, LCFR)

  private val umcpBFBuggyIJCAI = PlanBasedSearch(None, BFSType, Nil, Nil, UMCPFlaw)
  private val umcpBF           = PlanBasedSearch(None, GreedyType, UMCPBFSHeuristic :: Nil, Nil, UMCPFlaw)
  private val umcpDF           = PlanBasedSearch(None, DFSType, Nil, Nil, UMCPFlaw)
  private val umcpH            = PlanBasedSearch(None, GreedyType, UMCPHeuristic :: Nil, Nil, UMCPFlaw)

  private val planSearchAStarADD        = PlanBasedSearch(None, AStarActionsType(2), ADD :: Nil, Nil, LCFR)
  private val planSearchAStarADDReusing = PlanBasedSearch(None, AStarActionsType(2), ADDReusing :: Nil, Nil, LCFR)
  private val planSearchAStarRelax      = PlanBasedSearch(None, AStarActionsType(2), Relax :: Nil, Nil, LCFR)


  val defaultConfigurations: Map[String, (ParsingConfiguration, PreprocessingConfiguration, SearchConfiguration)] =
    Map(
         //"panda-MAC" ->(htnParsing, groundingPreprocess, PlanBasedSearch(None, AStarActionsType(1), LiftedTDGMinimumAction(NeverRecompute) :: Nil, Nil, LCFR)),
         "IJCAI-2017-AdmissibleHeuristics(uniform)" -> (htnParsing, groundingPreprocess, planSearchDijkstra),
         "IJCAI-2017-AdmissibleHeuristics(DFS)" -> (htnParsing, groundingPreprocess, planSearchDFS),
         "IJCAI-2017-AdmissibleHeuristics(BFS)" -> (htnParsing, groundingPreprocess, planSearchBFS),

         "IJCAI-2017-AdmissibleHeuristics(umcp-BF)" -> (htnParsing, groundingPreprocess, umcpBFBuggyIJCAI),
         "IJCAI-2017-AdmissibleHeuristics(umcp-DF)" -> (htnParsing, groundingPreprocess, umcpDF),
         "IJCAI-2017-AdmissibleHeuristics(umcp-H)" -> (htnParsing, groundingPreprocess, umcpH),

         "UMCP(BF)" ->(htnParsing, groundingPreprocess, umcpBF),
         "UMCP(DF)" ->(htnParsing, groundingPreprocess, umcpDF),
         "UMCP(h)" ->(htnParsing, groundingPreprocess, umcpH),

         // A*
         "IJCAI-2017-AdmissibleHeuristics(astar,add)" -> (htnParsing, groundingPreprocess, AStarADD),
         "IJCAI-2017-AdmissibleHeuristics(astar,add-r)" -> (htnParsing, groundingPreprocess, AStarADDReusing),
         "IJCAI-2017-AdmissibleHeuristics(astar,relax)" -> (htnParsing, groundingPreprocess, AStarRelax),
         "IJCAI-2017-AdmissibleHeuristics(astar,oc)" -> (htnParsing, groundingPreprocess, AStarAOpenPreconditions),

         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-c)" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-c-rec)" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPRReachability),
         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-m)" -> (htnParsing, groundingPreprocess, AStarActionLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(astar,TDG-m-rec)" -> (htnParsing, groundingPreprocess, AStarActionLiftedPRReachability),

         // GA*
         "IJCAI-2017-AdmissibleHeuristics(gastar,add)" -> (htnParsing, groundingPreprocess, planSearchAStarADD),
         "IJCAI-2017-AdmissibleHeuristics(gastar,add-r)" -> (htnParsing, groundingPreprocess, planSearchAStarADDReusing),
         "IJCAI-2017-AdmissibleHeuristics(gastar,relax)" -> (htnParsing, groundingPreprocess, planSearchAStarRelax),
         "IJCAI-2017-AdmissibleHeuristics(gastar,oc)" -> (htnParsing, groundingPreprocess, planSearchAStarAOpenPreconditions),

         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-c)" -> (htnParsing, groundingPreprocess, planSearchAStarActionLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-c-rec)" -> (htnParsing, groundingPreprocess, planSearchAStarActionLiftedPRReachability),
         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-m)" -> (htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPR),
         "IJCAI-2017-AdmissibleHeuristics(gastar,TDG-m-rec)" -> (htnParsing, groundingPreprocess, planSearchAStarAPRLiftedPRReachability),

         //  compare

         // this is some quiet technical stuff for quiet advanced analyzations regarding recomputation
         // (there are special tables for for that in the paper).

         "IJCAI-2017-astar-TDG-m-Recompute-Compare" -> (htnParsing, groundingPreprocess, AStarAPRLiftedPRCompare),
         "IJCAI-2017-astar-TDG-c-Recompute-Compare" -> (htnParsing, groundingPreprocess, AStarActionLiftedPRCompare),
         "IJCAI-2017-gastar-TDG-m-Recompute-Compare" -> (htnParsing, groundingPreprocess, greedyAStarAPRLiftedPRCompare),
         "IJCAI-2017-gastar-TDG-c-Recompute-Compare" -> (htnParsing, groundingPreprocess, greedyAStarActionLiftedPRCompare),


         // plan verification á la ICAPS'17
         "ICAPS-2017-verify" -> (htnParsing, groundingPreprocess, SATPlanVerification(CRYPTOMINISAT, ""))

       )

}
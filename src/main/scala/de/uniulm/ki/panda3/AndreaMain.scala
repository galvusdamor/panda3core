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

package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration.PredefinedConfigurations.pandaProConfig
import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic.SasHeuristics
import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch

//import de.uniulm.ki.panda3.efficient.heuristic.filter.TreeFF
//import de.uniulm.ki.panda3.progression.htn.search.searchRoutine.PriorityQueueSearch
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep

//import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints._

//import de.uniulm.ki.panda3.symbolic.sat.ltl._
import de.uniulm.ki.panda3.symbolic.search.SearchState
import de.uniulm.ki.util._


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off
object MainAndrea {

  def main(args: Array[String]) {

    val outputPDF = "dot.pdf"

    // TRANSPORT
    val domFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\transport\\domains\\domain-htn.lisp".replace('\\', File.separatorChar)
    //val probFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\transport\\problems\\pfile1-mitZiel".replace('\\', File.separatorChar)
    val probFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\transport\\problems\\pfile3".replace('\\', File.separatorChar)
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/Satellite/domains/satellite2.hddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/Satellite/problems/8obs-3sat-4mod.hddl"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/Woodworking/domains/woodworking-legal-fewer-htn-groundings.hddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/Woodworking/problems/00--p01-variant.hddl"



    // Rover
    //val domFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\rover\\domains\\rover-domain.lisp".replace('\\', File.separatorChar)
    //val probFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\rover\\problems\\pfile3".replace('\\', File.separatorChar)

    //val domFile = "D:\\IdeaProjects\\panda3core\\Domains\\Woodworking\\woodworking-legal-fewer-htn-groundings.hddl"
    //val probFile = "D:\\IdeaProjects\\panda3core\\Domains\\Woodworking\\01--p01-complete.hddl"
    //val probFile = "D:\\IdeaProjects\\panda3core\\Domains\\Woodworking\\02--p02-part1.hddl"
    //val probFile = "D:\\IdeaProjects\\panda3core\\Domains\\Woodworking\\03--p02-part2.hddl"
    //val probFile = "D:\\\\IdeaProjects\\\\panda3core\\\\Domains\\\\Woodworking\\\\01--p01-complete.hddl"
    //val probFile = "D:\\IdeaProjects\\panda3core\\Domains\\Woodworking\\04--p02-part3.hddl"




    //val domFile = "/home/gregor/Workspace/Woodworking/domains/woodworking-legal-fewer-htn-groundings.hddl"
    //val probFile = "/home/gregor/Workspace/Woodworking/problems/00--p01-variant.hddl"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/SmartPhone/domains/SmartPhone-HierarchicalNoAxioms.hddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/SmartPhone/problems/01-OrganizeMeeting_VeryVerySmall.hddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/SmartPhone/problems/02-OrganizeMeeting_VerySmall.hddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/SmartPhone/problems/03-OrganizeMeeting_Small.hddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/HDDL/SmartPhone/problems/04-OrganizeMeeting_Large.hddl"

    // ROVER
    //val domFile = "rover-domain.hddl"
    //val probFile = "pfile1.hddl"

    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    System.out.println("#0 \"domain\"=\"" + domFile.substring(domFile.lastIndexOf("/") + 1) + "\";\"problem\"=\"" + probFile.substring(probFile.lastIndexOf("/") + 1) + "\"")

    val postprocessing = PostprocessingConfiguration(Set(ProcessingTimings,
                                                         SearchStatistics,
                                                         SearchStatus,
                                                         //SearchResult,
                                                         PreprocessedDomainAndPlan))

    // planning config is given via stdin
    val searchConfig: PlanningConfiguration =
      PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true, randomSeed = 44, timeLimit = Some(1000),
                            parsingConfiguration = ParsingConfiguration(parserType = AutoDetectParserType, eliminateEquality = false, stripHybrid = true),
                            PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false,
                                                       compileInitialPlan = true,
                                                       convertToSASP = true,
                                                       allowSASPFromStrips = false,
                                                       compileOrderInMethods = None,
                                                       ensureMethodsHaveLastTask = false,
                                                       ensureMethodsHaveAtMostTwoTasks = true,
                                                       removeUnnecessaryPredicates = false,
                                                       splitIndependentParameters = true,
                                                       compileUselessAbstractTasks = false,
                                                       liftedReachability = true,
                                                       groundedReachability = None,
                                                       groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                       iterateReachabilityAnalysis = true, groundDomain = true, stopDirectlyAfterGrounding = false),
                            //PredefinedConfigurations.groundingPreprocess,
                            //PredefinedConfigurations.sasPlusConfig(AStarActionsType(2), SasHeuristics.hMS),
                            //PredefinedConfigurations.pandaProConfig(AStarActionsType(2), SasHeuristics.hMS),
                            ProgressionSearch(AStarActionsType(2), Some(HierarchicalMergeAndShrink(filterWithADD = true)), PriorityQueueSearch.abstractTaskSelection.random),
                            //ProgressionSearch(AStarActionsType(2), Some(HierarchicalHeuristicRelaxedComposition(SasHeuristics.hFF)), PriorityQueueSearch.abstractTaskSelection.random),
                            postprocessing,
                            Map(FastDownward -> "c:\\Fast-Downward-c46aa75d513e"))
                            //Map(FastDownward -> "../../fd"))


    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)
    // add general information
    results(SearchStatistics).set(Information.DOMAIN_NAME, new File(domFile).getName)
    results(SearchStatistics).set(Information.PROBLEM_NAME, new File(probFile).getName)


    println("Panda says: " + results(SearchStatus))
    println(results(SearchStatistics).shortInfo)

    println("----------------- TIMINGS -----------------")
    println(results(ProcessingTimings).shortInfo)


    // output data in a machine readable format
    println("###" + results(SearchStatistics).keyValueListString() + DataCapsule.SEPARATOR + results(ProcessingTimings).keyValueListString())


    if (results.map.contains(SearchResult) && results(SearchResult).isDefined) {

      println("SOLUTION SEQUENCE")
      val plan = results(SearchResult).get

      def psToString(ps: PlanStep): String = {
        val name = ps.schema.name
        val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

        name + args.mkString("(", ",", ")")
      }

      println((plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }).zipWithIndex map { case (ps, i) => i + ": " + psToString(ps) } mkString "\n")

      assert(plan.planSteps forall { _.schema.isPrimitive })

    }
  }
}
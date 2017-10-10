package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.progression.heuristics.sasp.SasHeuristic.SasHeuristics

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
    val domFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\transport\\domains\\domain-htn.lisp".replace('\\',File.separatorChar)
    val probFile = "src\\test\\java\\UUBenchmarksets\\fromHTN\\transport\\problems\\pfile1".replace('\\',File.separatorChar)

    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    System.out.println("#0 \"domain\"=\"" + domFile.substring(domFile.lastIndexOf("/") + 1) + "\";\"problem\"=\"" + probFile.substring(probFile.lastIndexOf("/") + 1) + "\"")

    val postprocessing = PostprocessingConfiguration(Set(ProcessingTimings,
      SearchStatistics,
      SearchStatus,
      SearchResult,
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
          splitIndependentParameters = true,
          compileUselessAbstractTasks = true,
          liftedReachability = true,
          groundedReachability = None,
          groundedTaskDecompositionGraph = Some(TwoWayTDG),
          iterateReachabilityAnalysis = true, groundDomain = true),
        PredefinedConfigurations.sasPlusConfig(AStarActionsType(2), SasHeuristics.hMS),
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
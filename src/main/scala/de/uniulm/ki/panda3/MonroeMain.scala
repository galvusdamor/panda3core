package de.uniulm.ki.panda3

import java.io._

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object MonroeMain {

  def main(args: Array[String]): Unit = {
    val dir = new File("../02-translation/")
    val domains = dir.listFiles() filter { _.getName startsWith "d-" }

    val resultStream = new PrintStream(new FileOutputStream("monroe.csv"))

    resultStream.print("instance,solvestate,toplevel,")

    Timings.allTimings map { "," + _ } foreach resultStream.print
    resultStream.println()
    resultStream.flush()


    domains foreach { domFile =>
      val probFile = new File(domFile.getAbsolutePath.replaceAll("/d-", "/p-"))

      val domInputStream = new FileInputStream(domFile)
      val probInputStream = new FileInputStream(probFile)

      // create the configuration
      val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                               ParsingConfiguration(HDDLParserType),
                                               PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false, compileOrderInMethods = false,
                                                                          liftedReachability = true, groundedReachability = false, planningGraph = false,
                                                                          groundedTaskDecompositionGraph = None, //Some(TopDownTDG), // None,
                                                                          iterateReachabilityAnalysis = true, groundDomain = false),
                                               SearchConfiguration(None, Some(5 * 60), efficientSearch = true, GreedyType, Some(NumberOfFlaws), printSearchInfo = true),
                                               PostprocessingConfiguration(Set(ProcessingTimings,
                                                                               SearchStatus, SearchResult,
                                                                               SearchStatistics,
                                                                               //SearchSpace,
                                                                               PreprocessedDomainAndPlan,
                                                                               SolutionInternalString,
                                                                               SolutionDotString)))
      //System.in.read()

      val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

      println("Panda says: " + results(SearchStatus))
      println(results(SearchStatistics).shortInfo)
      println("----------------- TIMINGS -----------------")
      println(results(ProcessingTimings).shortInfo)

      println("SOLUTION SEQUENCE")
      val toplevelTask = if (results(SearchResult).nonEmpty) {
        val plan = results(SearchResult).get

        def psToString(ps: PlanStep): String = {
          val name = ps.schema.name
          val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

          name + args.mkString("(", ";", ")")
        }

        println(plan.orderingConstraints.graph.topologicalOrdering.get map psToString mkString "\n")

        println("FIRST DECOMPOSITION")
        val initSchema = results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.head.schema
        val initPS = plan.planStepsAndRemovedPlanSteps find { _.schema == initSchema } get
        val realGoalSchema = plan.planStepDecomposedByMethod(initPS).subPlan.planStepsWithoutInitGoal.head.schema
        val realGoal = plan.planStepsAndRemovedPlanSteps find { _.schema == realGoalSchema } get

        println(plan.planStepDecomposedByMethod(initPS).name + " into " + psToString(realGoal))

        psToString(realGoal)
      } else "unknown"

      resultStream.print(domFile.getName + "," + results(SearchStatus) + "," + toplevelTask)
      Timings.allTimings map { "," +  results(ProcessingTimings).integralDataMap().getOrElse(_, Integer.MAX_VALUE) } foreach resultStream.print
      resultStream.println()
      resultStream.flush()
    }
  }
}
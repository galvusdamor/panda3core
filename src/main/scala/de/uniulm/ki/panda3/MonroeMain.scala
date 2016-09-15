package de.uniulm.ki.panda3

import java.io._

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.panda3.symbolic.logic.Constant
import de.uniulm.ki.panda3.symbolic.search.SearchState
import de.uniulm.ki.util.{TimeCapsule, InformationCapsule}


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object MonroeMain {

  def main(args: Array[String]): Unit = {

    val run = args(0).toInt

    //val dir = new File("../02-translation/")
    //val dir = new File("../02b-tlt/" + run + "/")
    val dir = new File(".")
    val domains = dir.listFiles() filter { _.getName startsWith "d-" }

    val resultStream = new PrintStream(new FileOutputStream("monroe" + run + ".csv"))

    resultStream.print("instance,solvestate,toplevel,nump")

    Timings.allTimings map { "," + _ } foreach resultStream.print
    resultStream.println()
    resultStream.flush()

    //filter { _.getName contains "full-pref" }
    domains filter { d => d.getName.split("-")(1).toInt % 4 == run } foreach { domFile =>
      val probFile = new File(domFile.getAbsolutePath.replaceAll("/d-", "/p-") + "tlt")
      println("\n\n\nDOMAIN: " + domFile.getName)

      val domInputStream = new FileInputStream(domFile)
      val probInputStream = new FileInputStream(probFile)

      // create the configuration
      val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                               ParsingConfiguration(HDDLParserType),
                                               PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false, compileOrderInMethods = false,
                                                                          liftedReachability = true, groundedReachability = false, planningGraph = false,
                                                                          groundedTaskDecompositionGraph = None, //Some(TopDownTDG),
                                                                          iterateReachabilityAnalysis = true, groundDomain = false),
                                               SearchConfiguration(None, Some(5 * 60), efficientSearch = true, GreedyType, Some(LiftedTDGMinimumModification), true, printSearchInfo = true),
                                               //SearchConfiguration(None, Some(5), efficientSearch = true, DFSType, None, printSearchInfo = true),
                                               //SearchConfiguration(None, Some(5 * 60), efficientSearch = true, GreedyType, Some(NumberOfFlaws), printSearchInfo =true),
                                               PostprocessingConfiguration(Set(ProcessingTimings,
                                                                               SearchStatus, SearchResult,
                                                                               SearchStatistics,
                                                                               //SearchSpace,
                                                                               PreprocessedDomainAndPlan,
                                                                               SolutionInternalString,
                                                                               SolutionDotString)))
      //System.in.read()

      val results: ResultMap = try {
        //throw new OutOfMemoryError()
        searchConfig.runResultSearch(domInputStream, probInputStream)
      } catch {
        case t: Throwable => t.printStackTrace()
          ResultMap(Map(SearchStatus -> SearchState.UNSOLVABLE, SearchStatistics -> new InformationCapsule(), ProcessingTimings -> new TimeCapsule(), SearchResult -> None))
      }


      println("Panda says: " + results(SearchStatus))
      println(results(SearchStatistics).shortInfo)
      println("----------------- TIMINGS -----------------")
      println(results(ProcessingTimings).shortInfo)

      println("SOLUTION SEQUENCE")
      val toplevelTask = if (results(SearchResult).nonEmpty) {
        val plan = results(SearchResult).get

        // check executability
        val initalState = plan.groundedInitialStateOnlyPositive.toSet
        val ordering = plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }

        val goal = ordering.foldLeft(initalState)(
          {
            case (state, ps) =>
              val constantArgs = ps.arguments map plan.variableConstraints.getRepresentative map { case c: Constant => c }
              val groundTask = GroundTask(ps.schema, constantArgs)

              assert(groundTask.substitutedPreconditionsSet subsetOf state)
              val removed = state -- groundTask.substitutedDelEffects

              removed ++ groundTask.substitutedAddEffects
          })

        assert(plan.groundedGoalState.toSet subsetOf goal)


        def psToString(ps: PlanStep): String = {
          val name = ps.schema.name
          val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

          name + args.mkString("(", ";", ")")
        }

        println(plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive } map psToString mkString "\n")
        println("ABSTRACTS")
        println(plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isAbstract } map psToString mkString "\n")

        println("FIRST DECOMPOSITION")
        val initSchema = results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.head.schema
        val initPS = plan.planStepsAndRemovedPlanSteps find { _.schema == initSchema } get
        val realGoalSchema = plan.planStepDecomposedByMethod(initPS).subPlan.planStepsWithoutInitGoal.head.schema
        val realGoal = plan.planStepsAndRemovedPlanSteps find { _.schema == realGoalSchema } get

        println(plan.planStepDecomposedByMethod(initPS).name + " into " + psToString(realGoal))

        psToString(realGoal)
      } else "unknown"

      val pActions = if (results.map contains PreprocessedDomainAndPlan) results(PreprocessedDomainAndPlan)._1.tasks count { _.name startsWith "p_" } else -1
      resultStream.print(domFile.getName + "," + results(SearchStatus) + "," + toplevelTask + "," + pActions)
      Timings.allTimings map { "," + results(ProcessingTimings).integralDataMap().getOrElse(_, Integer.MAX_VALUE) } foreach resultStream.print
      resultStream.println()
      resultStream.flush()
    }
  }

}
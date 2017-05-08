package de.uniulm.ki.panda3

import java.io._

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.compiler.TotallyOrderingOption
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.panda3.symbolic.logic.Constant
import de.uniulm.ki.panda3.symbolic.search.SearchState
import de.uniulm.ki.util.{TimeCapsule, InformationCapsule}


/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object MonroeMain {

  val allTimings = Timings.TOTAL_TIME :: Timings.PARSING :: Timings.FILEPARSER :: Timings.PARSER_SORT_EXPANSION :: Timings.PARSER_CWA :: Timings.PARSER_SHOP_METHODS ::
    Timings.PARSER_ELIMINATE_EQUALITY :: Timings.PARSER_FLATTEN_FORMULA :: Timings.PREPROCESSING :: Timings.COMPILE_NEGATIVE_PRECONFITIONS :: Timings.COMPILE_UNIT_METHODS ::
    Timings.COMPILE_ORDER_IN_METHODS :: Timings.LIFTED_REACHABILITY_ANALYSIS ::
    Timings.GROUNDED_PLANNINGGRAPH_ANALYSIS :: Timings.GROUNDED_TDG_ANALYSIS :: Timings.HEURISTICS_PREPARATION :: Timings.SEARCH_PREPARATION :: Timings.COMPUTE_EFFICIENT_REPRESENTATION ::
    Timings.SEARCH :: Timings.SEARCH_FLAW_RESOLVER_ESTIMATION :: Timings.SEARCH_FLAW_COMPUTATION :: Timings.SEARCH_FLAW_SELECTOR :: Timings.SEARCH_FLAW_RESOLVER ::
    Timings.SEARCH_GENERATE_SUCCESSORS :: Timings.SEARCH_COMPUTE_HEURISTIC :: Nil


  def main(args: Array[String]): Unit = {

    assert(false)

    val run = args(0).toInt

    //val dir = new File("../02-translation/")
    //val dir = new File("../02b-tlt/" + run + "/")
    val dir = new File(".")
    val domains = dir.listFiles() filter { _.getName startsWith "d-" }

    val resultStream = new PrintStream(new FileOutputStream("monroe" + run + ".csv"))

    resultStream.print("instance,solvestate,toplevel,primitivePlan,nump")

    allTimings map { "," + _ } foreach resultStream.print
    resultStream.println()
    resultStream.flush()

    //filter { _.getName contains "full-pref" }
    domains filter { d => d.getName.split("-")(1).toInt % 4 == run } foreach { domFile =>
      val probFile = new File(domFile.getAbsolutePath.replaceAll("/d-", "/p-"))
      println("\n\n\nDOMAIN: " + domFile.getName)

      val domInputStream = new FileInputStream(domFile)
      val probInputStream = new FileInputStream(probFile)

      // create the configuration
      val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true, randomSeed = 42, timeLimit = Some(5 * 60),
                                               ParsingConfiguration(),
                                               PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = false, compileOrderInMethods = None,
                                                                          compileInitialPlan = false,convertToSASP = false, splitIndependentParameters = false,
                                                                          liftedReachability = true, groundedReachability = None,
                                                                          groundedTaskDecompositionGraph = None, //Some(TopDownTDG),
                                                                          iterateReachabilityAnalysis = true, groundDomain = false),
                                               PlanBasedSearch(None, GreedyType, LiftedTDGMinimumModificationWithCycleDetection(NeverRecompute) :: Nil, Nil, LCFR),
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
      val (toplevelTask, primitiveSequence) = if (results(SearchResult).nonEmpty) {
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

        val prim = ordering map psToString mkString ":"

        (psToString(realGoal), prim)
      } else ("unknown", "none")

      val pActions = if (results.map contains PreprocessedDomainAndPlan) results(PreprocessedDomainAndPlan)._1.tasks count { _.name startsWith "p_" } else -1
      resultStream.print(domFile.getName + "," + results(SearchStatus) + "," + toplevelTask + "," + primitiveSequence + ", " + pActions)
      allTimings map { "," + results(ProcessingTimings).integralDataMap().getOrElse(_, Integer.MAX_VALUE) } foreach resultStream.print
      resultStream.println()
      resultStream.flush()
    }
  }

}
package de.uniulm.ki.panda3

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.search.{SearchState}
import de.uniulm.ki.util._


/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off
object Main {


  case class RunConfiguration(domFile: Option[String] = None, probFile: Option[String] = None, outputFile: Option[String] = None,
                              config: PlanningConfiguration = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                                                                    randomSeed = 42, timeLimit = None,
                                                                                    ParsingConfiguration(),
                                                                                    PreprocessingConfiguration(compileNegativePreconditions = false,
                                                                                                               compileUnitMethods = false,
                                                                                                               compileInitialPlan = false,
                                                                                                               removeUnnecessaryPredicates = false,
                                                                                                               ensureMethodsHaveLastTask = false,
                                                                                                               convertToSASP = false,
                                                                                                               allowSASPFromStrips = false,
                                                                                                               compileOrderInMethods = None,
                                                                                                               splitIndependentParameters = false,
                                                                                                               compileUselessAbstractTasks = false,
                                                                                                               liftedReachability = false,
                                                                                                               groundedReachability = None,
                                                                                                               groundedTaskDecompositionGraph = None,
                                                                                                               iterateReachabilityAnalysis = false,
                                                                                                               groundDomain = false,
                                                                                                               stopDirectlyAfterGrounding = false),
                                                                                    NoSearch, PostprocessingConfiguration(Set()))) extends PrettyPrintable {

    def processCommandLineArguments(args: Seq[String]): RunConfiguration = {
      // determine which arguments belong together
      val groupedArguments = args.foldLeft[(Option[String], Seq[Either[String, (String, String)]])]((None, Nil))(
        {
          case ((Some(command), grouped), nextArgument) =>
            if (this.config.modifyOnOptionString contains command)
              this.config.modifyOnOptionString(command)._1 match {
                case NoParameter                                        => (Some(nextArgument), grouped :+ Left(command))
                case OptionalParameter if !nextArgument.startsWith("-") => (None, grouped :+ Right(command, nextArgument))
                case OptionalParameter if nextArgument.startsWith("-")  => (Some(nextArgument), grouped :+ Left(command))
                case NecessaryParameter                                 => (None, grouped :+ Right(command, nextArgument))
              } else (Some(nextArgument), grouped :+ Left(command))

          case ((None, grouped), nextArgument) if !nextArgument.startsWith("-") => (None, grouped :+ Left(nextArgument))
          case ((None, grouped), nextArgument) if nextArgument.startsWith("-")  => (Some(nextArgument), grouped)
        }) match {
        case (None, l)          => l
        case (Some(command), l) =>
          this.config.modifyOnOptionString.getOrElse(command,(NoParameter,()))._1 match {
            case NecessaryParameter => assert(false, "no argument provided for " + command); l // this will never be reached. it is just for the sake of completeness
            case _                  => l :+ Left(command)
          }
      }

      groupedArguments.foldLeft(this)(
        { case (conf, option) => option match {
          case Left(opt) if !opt.startsWith("-")                              =>
            opt match {
              case dom if conf.domFile.isEmpty    => conf.copy(domFile = Some(dom))
              case prob if conf.probFile.isEmpty  => conf.copy(probFile = Some(prob))
              case out if conf.outputFile.isEmpty => conf.copy(outputFile = Some(out))
              case _                              =>
                println("PANDA was given a fourth non-option argument \"" + opt + "\". Only three (domain-, problem-, and output-file) will be processed. Ignoring option")
                conf
            }
          case Left(opt) if opt.equals("-cputime") || opt.equals("-walltime") =>
            // use side effect
            opt match {
              case "-cputime"  => TimeCapsule.timeTakingMode = ThreadCPUTime
              case "-walltime" => TimeCapsule.timeTakingMode = WallTime
            }

            conf
          case _                                                              => // this is a real option

            // get the key
            val (key, value) = option match {
              case Left(command)       => (command, None)
              case Right((command, v)) => (command, Some(v))
            }

            // treat special keys
            val y = key match {
              case _ =>
                if (conf.config.modifyOnOptionString.contains(key))
                  conf.copy(config = conf.config.modifyOnOptionString(key)._2(value))
                else {
                  println("Option \"" + key + "\" unavailable in current circumstance")
                  println(conf.config.modifyOnOptionString.keySet)
                  println(conf.longInfo)
                  System exit 1
                  conf
                }
            }

            y
        }
        })
    }

    /** returns a string by which this object may be referenced */
    override def shortInfo: String = mediumInfo

    /** returns a string that can be utilized to define the object */
    override def mediumInfo: String = longInfo

    /** returns a detailed information about the object */
    override def longInfo: String = "Planner Configuration\n=====================\nDomain: " + domFile.getOrElse("none") + "\nProblem: " + probFile.getOrElse("none") + "\nOutput: " +
      outputFile.getOrElse("none") + "\n\n" + config.longInfo
  }

  var globalReportCounter: Int = 0

  def writeCapsulesToStdOut(dataCapsule: Option[DataCapsule], timeCapsule: Option[TimeCapsule]): Unit =
    synchronized {
                   val dataString = dataCapsule match {
                     case Some(dc) => dc.keyValueListString()
                     case None     => ""
                   }
                   val timeString = timeCapsule match {
                     case Some(tc) => tc.keyValueListString()
                     case None     => ""
                   }
                   // output data in a machine readable format
                   println("#" + globalReportCounter + " " +
                             dataString + (if (dataString.nonEmpty && timeString.nonEmpty) DataCapsule.SEPARATOR else "") + timeString)
                   globalReportCounter += 1

                 }

  def main(args: Array[String]) {
    //PlanningConfiguration(true,true,null,null,SATSearch(None,null,0).modifyOnOptionString("-planlength")(Some("2")),null)

    println("This is PANDA3\nBelieve us: It is great!")
    println("\nCopyright: Ulm University 2014-2017")
    println("Developer: Gregor Behnke, Daniel Höller, Kristof Mickeleit, Tobias Welz, Kadir Dede, Matthias Englert, Mario Schmautz and Thomas Geier")
    println("Thanks to Pascal Bercher for his moral support while writing PANDA3\n\n")

    val initialConfiguration = RunConfiguration()

    // test if we have to print the help
    if (args.length > 0 && args(0) == "-help") {
      if (args.length > 1) {
        val helpForKey = args(1)
        println("Help for key " + helpForKey + "\n" + initialConfiguration.config.helpTexts(helpForKey))
      } else {
        println("Available Keys (specific help can be requested using -help KEY):")
        println(initialConfiguration.config.optionStrings map { s => "\t" + s } mkString "\n")
      }
      System exit 0
    }

    val plannerConfiguration = initialConfiguration.processCommandLineArguments(args)

    println(plannerConfiguration.longInfo)

    // write domain and problem name to the output
    {
      val informationCapsule = new InformationCapsule
      informationCapsule.set(Information.DOMAIN_NAME, new File(plannerConfiguration.domFile.get).getName)
      informationCapsule.set(Information.PROBLEM_NAME, new File(plannerConfiguration.probFile.get).getName)

      writeCapsulesToStdOut(Some(informationCapsule), None)
    }


    val domInputStream = new FileInputStream(plannerConfiguration.domFile.get)
    val probInputStream = new FileInputStream(plannerConfiguration.probFile.get)



    val results: ResultMap = plannerConfiguration.config.runResultSearch(domInputStream, probInputStream)

    if (results.map.contains(SearchStatus))
      println("Panda says: " + results(SearchStatus))


    if (results.map.contains(SearchStatistics)) {
      println(results(SearchStatistics).shortInfo)
    }
    if (results.map.contains(ProcessingTimings)) {
      println("----------------- TIMINGS -----------------")
      println(results(ProcessingTimings).shortInfo)
    }

    if (results.map.contains(SearchStatistics) && results.map.contains(ProcessingTimings)) {
      // output data in a machine readable format
      writeCapsulesToStdOut(Some(results(SearchStatistics)), Some(results(ProcessingTimings)))
    }

    if (results.map.contains(SearchResult) && results(SearchResult).isDefined) {

      println("SOLUTION SEQUENCE")
      val plan = results(SearchResult).get

      assert(plan.planSteps forall { _.schema.isPrimitive })

      def psToString(ps: PlanStep): String = {
        val name = ps.schema.name
        val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

        name + args.mkString("(", ",", ")")
      }

      println((plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }).zipWithIndex map { case (ps, i) => i + ": " + psToString(ps) } mkString "\n")


      if (results.map.contains(PreprocessedDomainAndPlan) && results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.nonEmpty) {
        println("FIRST DECOMPOSITION")
        val initSchema = results(PreprocessedDomainAndPlan)._2.planStepsWithoutInitGoal.head.schema
        val initPS = plan.planStepsAndRemovedPlanSteps find { _.schema == initSchema } get
        val realGoalSchema = plan.planStepDecomposedByMethod(initPS).subPlan.planStepsWithoutInitGoal.head.schema
        val realGoal = plan.planStepsAndRemovedPlanSteps find { _.schema == realGoalSchema } get

        println(plan.planStepDecomposedByMethod(initPS).name + " into " + psToString(realGoal))
      }
    }

    if (results.map.contains(SearchResultInVerificationFormat)){
      println("%%R " + results(SearchResultInVerificationFormat))
    }

    if (results.map.contains(SearchResult) && results.map.contains(SearchStatus) && results(SearchStatus) == SearchState.SOLUTION && plannerConfiguration.outputFile.isDefined) {
      val solution = results(SearchResult).get
      //println(solution.planSteps.length)
      // write output
      if (plannerConfiguration.outputFile.get.endsWith("dot")) {
        writeStringToFile(solution.dotString(PlanDotOptions(showHierarchy = false)), new File(plannerConfiguration.outputFile.get))
      } else {
        Dot2PdfCompiler.writeDotToFile(solution.dotString(PlanDotOptions(showHierarchy = true)), plannerConfiguration.outputFile.get)
      }
    }
  }
}
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

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.plan.PlanDotOptions
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.search.SearchState
import de.uniulm.ki.util._

import scala.io.Source


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
                                                                                    PreprocessingConfiguration(compileNegativePreconditions = true,
                                                                                                               compileUnitMethods = false,
                                                                                                               compileInitialPlan = false,
                                                                                                               convertToSASP = false,
                                                                                                               allowSASPFromStrips = false,
                                                                                                               removeUnnecessaryPredicates = true,
                                                                                                               ensureMethodsHaveLastTask = false,
                                                                                                               compileOrderInMethods = None,
                                                                                                               splitIndependentParameters = true,
                                                                                                               compileUselessAbstractTasks = false,
                                                                                                               liftedReachability = true,
                                                                                                               groundedReachability = Some(PlanningGraph),
                                                                                                               groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                                                               iterateReachabilityAnalysis = true,
                                                                                                               groundDomain = true),
                                                                                    PlanningConfiguration.defaultPlanSearchConfiguration,
                                                                                    PostprocessingConfiguration(Set(SearchStatus, SearchResult,
                                                                                                                    ProcessingTimings,
                                                                                                                    SearchStatistics)))) extends PrettyPrintable {

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

          case ((None, grouped), nextArgument) if !nextArgument.startsWith("-") =>
            (None, grouped :+ Left(nextArgument))
          case ((None, grouped), nextArgument) if nextArgument.startsWith("-")  =>
            (Some(nextArgument), grouped)
        }) match {
        case (None, l)          => l
        case (Some(command), l) =>
          this.config.modifyOnOptionString.getOrElse(command, (NoParameter, ()))._1 match {
            case NecessaryParameter => assert(false, "no argument provided for " + command); l // this will never be reached. it is just for the sake of completeness
            case _                  => l :+ Left(command)
          }
      }

      groupedArguments.foldLeft(this)(
        { case (conf, option) => option match {
          case Left(opt) if !opt.startsWith("-") =>
            opt match {
              case dom if conf.domFile.isEmpty    => conf.copy(domFile = Some(dom))
              case prob if conf.probFile.isEmpty  => conf.copy(probFile = Some(prob))
              case out if conf.outputFile.isEmpty => conf.copy(outputFile = Some(out))
              case _                              =>
                println("PANDA was given a fourth non-option argument \"" + opt + "\". Only three (domain-, problem-, and output-file) will be processed. Ignoring option")
                conf
            }
          case _                                 => // this is a real option

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
                  println("Currently only the following options are available:")

                  println(conf.config.modifyOnOptionString.keySet.toSeq.filter(helpDB.contains).sorted map { x => "\t" + x } mkString "\n")
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

  var lineWidth: Int = 80

  def transformTo80Chars(text: String): String = {
    val lineSplitted = text.replace("\t", "    ") split "\n"

    val redistributedLines = lineSplitted flatMap { line =>
      val initialIndent = line.takeWhile(c => c == ' ')
      val noIndent: Seq[String] = line.drop(initialIndent.length).split(" ")
      val (lines, lastLine) = noIndent.drop(1).foldLeft[(Seq[String], String)]((Nil, initialIndent + noIndent.head))(
        {
          case ((list, buf), c) =>
            val newBuf = buf + " " + c
            if (newBuf.length > lineWidth) (list :+ buf, initialIndent + c) else (list, newBuf)
        }
                                                                                                                    )

      lines :+ lastLine
    }

    redistributedLines mkString "\n"
  }

  val helpDB: Map[String, (String, String, Seq[String], String, Seq[String])] = {
    val dbLines: Seq[String] = Source.fromInputStream(getClass.getResourceAsStream("helpdb.txt")).getLines().toSeq

    val parsed: Seq[Seq[String]] = dbLines.foldLeft[(Seq[Seq[String]], Seq[String])]((Nil, Nil))(
      {
        case ((processed, current), newLine) =>
          val x: Seq[String] = current :+ (if (newLine.trim == "\\n") "" else newLine)
          if (newLine.isEmpty || newLine.trim.startsWith("%")) (processed, current)
          else if (newLine.startsWith("$")) (processed :+ x, Nil)
          else (processed, x)
      })._1

    val entries: Seq[(String, (String, String, Seq[String], String, Seq[String]))] = parsed map { entry =>
      val keys: Seq[String] = entry.head split " "
      val short: String = entry(1)
      val optionsHeader: String = entry(2)
      val long: String = entry.drop(3).dropRight(1).mkString("\n")
      val children: Seq[String] = entry.last.split(" ").drop(1)

      (keys, (short, long, optionsHeader, children))
    } flatMap { case (as, (s, l, o, c)) => as map { a => (a, (s, l, as.filter(_ != a), o, c)) } }

    val entryMap = Map(entries: _*)

    // consistency
    entries foreach { case (key, (_, _, _, _, children)) => children foreach { c =>
      assert(entryMap contains c, "No explanation found for key \"" + c + "\" occurring in key \"" + key + "\"")
    }
    }

    entries foreach {
      case ("main", _)                => // it's ok
      case (key, (_, _, alter, _, _)) =>
      //assert(alter :+ key exists { k => entries exists(_._2._5 contains k)}, "Key \"" + key + "\" does not occur as part of the explanation tree.")
    }

    entryMap
  }


  def getHelpTextFor(item: String): String = {
    val (_, longText, _, optionsText, children) = helpDB(item)

    if (children.isEmpty) longText else {
      val childrenTexts = children map { c =>
        val alternates = helpDB(c)._3
        val optionText = if (alternates.isEmpty) c else c + "|" + alternates.mkString("|")
        optionText -> helpDB(c)._1
      }
      val maxChildLength = childrenTexts map { _._1.length } max

      val childrenLines = childrenTexts map { case (i, t) =>
        "\t" + i + (Range(i.length, maxChildLength + 4) map { _ => " " }).mkString("") + t
      }

      longText + "\n\n\n" + optionsText + "\n" + childrenLines.mkString("\n")
    }
  }


  def main(originalArgs: Array[String]) {
    val args = originalArgs filter { _ != "-no80" }
    if (originalArgs contains "-no80") lineWidth = Integer.MAX_VALUE

    val titleLines = Source.fromInputStream(getClass.getResourceAsStream("help.txt")).getLines().toSeq
    val shortTitle = transformTo80Chars(titleLines.take(6).mkString("\n")) + "\n\n"
    val longTitle = transformTo80Chars(titleLines.mkString("\n"))


    val initialConfiguration = RunConfiguration()

    // test if we have to print the help
    if (args.length > 0 && (args(0) == "-help" || args(0) == "--help")) {
      println(shortTitle)
      if (args.length > 1) {
        val helpForKey = args(1)
        println("Help for option or key \"" + helpForKey + "\"\n")
        println(transformTo80Chars(getHelpTextFor(helpForKey)))
      } else {
        //println("Available Keys (specific help can be requested using -help KEY):")
        //println(initialConfiguration.config.optionStrings map { s => "\t" + s } mkString "\n")
        println(transformTo80Chars(getHelpTextFor("main")))
      }
      System exit 0
    } else if (args.length > 0 && (args(0) == "-contributors" || args(0) == "--contributors")) {
      println(shortTitle)
      println(transformTo80Chars(Source.fromInputStream(getClass.getResourceAsStream("contributors.txt")).getLines().mkString("\n")))
      System exit 0
    } else if (args.length > 0 && (args(0) == "-licence" || args(0) == "--licence")) {
      println(shortTitle)
      if (args.length == 1)
        println(transformTo80Chars(Source.fromInputStream(getClass.getResourceAsStream("licences.txt")).getLines().mkString("\n")))
      else {
        println(transformTo80Chars(Source.fromInputStream(getClass.getResourceAsStream("licence" + args(1) + ".txt")).getLines().mkString("\n")))
      }
      System exit 0
    }

    // print the general information of the planner
    println(longTitle)
    println()
    println()


    val plannerConfiguration = initialConfiguration.processCommandLineArguments(args)

    println(plannerConfiguration.longInfo)

    if (plannerConfiguration.domFile.isEmpty) {
      println("No domain file given. Exiting ... ")
      System exit 0
    }

    if (plannerConfiguration.probFile.isEmpty) {
      println("No problem file given. Exiting ... ")
      System exit 0
    }

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


    if (results.map.contains(SearchStatistics))
      println(results(SearchStatistics).shortInfo)
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
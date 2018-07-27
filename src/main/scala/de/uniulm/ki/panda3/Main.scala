// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
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
                                                                                    PredefinedConfigurations.defaultConfigurations("ICAPS-2018-RC(FF,gastar)")._1,
                                                                                    //ParsingConfiguration(stripHybrid = true),
                                                                                    /*PreprocessingConfiguration(compileNegativePreconditions = true,
                                                                                                               compileUnitMethods = false,
                                                                                                               compileInitialPlan = false,
                                                                                                               ensureMethodsHaveAtMostTwoTasks = false,
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
                                                                                                               groundDomain = true,
                                                                                                               stopDirectlyAfterGrounding = false),*/
                                                                                    PredefinedConfigurations.defaultConfigurations("ICAPS-2018-RC(FF,gastar)")._2,
                                                                                    //PlanningConfiguration.defaultPlanSearchConfiguration,
                                                                                    PredefinedConfigurations.defaultConfigurations("ICAPS-2018-RC(FF,gastar)")._3,
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
                  val text = "Unknown option \"" + key + "\"; maybe a typeO? In case it is not, please consult the following help:"
                  println(text)
                  println(text.indices map { _ => "=" } mkString "")
                  //println(conf.config.modifyOnOptionString.keySet.toSeq.filter(helpDB.contains).sorted map { x => "\t" + x } mkString "\n")
                  println()
                  println(transformTo80Chars(getHelpTextFor("main")))
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
      var indentMode = false
      var multiLine = false
      val initialIndent = line.takeWhile(c => c == ' ')
      val indentLength = line.trim.lastIndexOf("  ") + initialIndent.length + 2
      val indent = new String(Array.fill[Char](indentLength)(' '))
      val noIndent: Seq[String] = line.drop(initialIndent.length).split(" ")
      if (helpDB.keys exists { k => noIndent.head.startsWith(k) }) {
        indentMode = true
      }
      val (lines, lastLine) = noIndent.drop(1).foldLeft[(Seq[String], String)]((Nil, initialIndent + noIndent.head))(
        {
          case ((list, buf), c) =>
            val newBuf = buf + " " + c
            if (newBuf.length > lineWidth) {
              if (indentMode) {
                multiLine = true
                (list :+ buf, indent + c)
              } else {
                (list :+ buf, initialIndent + c)
              }
            } else {
              (list, newBuf)
            }
        }
                                                                                                                    )
      if (multiLine && !lastLine.startsWith(indent)) {
        lines :+ indent ++ lastLine
      } else {
        lines :+ lastLine
      }
    }

    redistributedLines mkString "\n"
  }

  val helpDB: Map[String, (String, String, Seq[String], String, Seq[String], String)] = {
    val dbLines: Seq[String] = Source.fromInputStream(getClass.getResourceAsStream("helpdb.txt"), "UTF-8").getLines().toSeq

    val parsed: Seq[Seq[String]] = dbLines.foldLeft[(Seq[Seq[String]], Seq[String])]((Nil, Nil))(
      {
        case ((processed, current), newLine) =>
          val x: Seq[String] = current :+ (if (newLine.trim == "\\n") "" else newLine)
          if (newLine.isEmpty || newLine.trim.startsWith("%")) (processed, current)
          else if (newLine.startsWith("$")) (processed :+ x, Nil)
          else (processed, x)
      })._1

    val entries: Seq[(String, (String, String, Seq[String], String, Seq[String], String))] = parsed map { entry =>
      val keys: Seq[String] = entry.head split " "
      val offset: Int = if (entry(1).startsWith("!")) 1 else 0
      val options: String = if (offset == 1) entry(1).drop(1) else ""
      val short: String = entry(1 + offset)
      val optionsHeader: String = entry(entry.length - 2)
      val long: String = entry.drop(2 + offset).dropRight(2).mkString("\n")
      val children: Seq[String] = entry.last.split(" ").drop(1)

      (keys, (short, long, optionsHeader, children, options))
    } flatMap { case (as, (s, l, o, c, op)) => as map { a => (a, (s, l, as.filter(_ != a), o, c, op)) } }

    val entryMap = Map(entries: _*)

    // consistency
    entries foreach { case (key, (_, _, _, _, children, _)) => children foreach { c =>
      assert(entryMap contains c, "No explanation found for key \"" + c + "\" occurring in key \"" + key + "\"")
    }
    }

    // all options must be existant
    val internalKeys: Set[String] = RunConfiguration().config.modifyOnOptionString.keySet
    entries filter { _._1.startsWith("-") } foreach {
      case ("-help", _) => true
      case (k, _)       => assert(internalKeys.contains(k), "PANDA's code does not know of key \"" + k + "\"")
    }

    entries foreach {
      case ("main", _)                   => // it's ok
      case (key, (_, _, alter, _, _, _)) =>
        assert(alter :+ key exists { k => entries exists (_._2._5 contains k) }, "Key \"" + key + "\" does not occur as part of the explanation tree.")
    }

    entryMap
  }


  def getHelpTextFor(item: String): String = if (!helpDB.contains(item)) "No entry found for \"" + item + "\"" else {
    val (_, longText, _, optionsText, children, options) = helpDB(item)

    if (children.isEmpty) longText else {
      def generateChildrenLines(childrenToDisplay: Seq[String]): String = {

        val childrenTexts = childrenToDisplay map { c =>
          val alternates = helpDB(c)._3
          val optionText = (if (alternates.isEmpty) c else c + "|" + alternates.mkString("|")) + helpDB(c)._6
          optionText -> helpDB(c)._1

        }

        val maxChildLength = childrenTexts map { _._1.length } max
        val childrenLines = childrenTexts map { case (i, t) => " " + i + (Range(i.length, maxChildLength + 2) map { _ => " " }).mkString("") + t }

        childrenLines.mkString("\n")
      }

      // if we are displaying main, we have to group our children.
      longText + "\n\n\n" + (if (item == "main") {
        //val confPSS = RunConfiguration().copy(config = RunConfiguration().config.copy(searchConfiguration = ))
        //val confVerify = RunConfiguration().copy(config = RunConfiguration().config.copy(searchConfiguration = ))
        val confList = (PlanningConfiguration.defaultPlanSearchConfiguration, "PANDA3") :: (PlanningConfiguration.defaultVerifyConfiguration, "PANDA verifier") :: Nil
        val (allConfs, separateConfs) = children partition { k =>
          confList.forall(_._1.modifyOnOptionString.contains(k)) || confList.forall(!_._1.modifyOnOptionString.contains(k))
        }
        val headerText = "Available options for "


        headerText + "all PANDA components:\n" + generateChildrenLines(allConfs) + "\n" +
          (confList map { case (conf, text) =>
            val childrenToShow = separateConfs filter conf.modifyOnOptionString.contains
            if (childrenToShow.isEmpty) "" else "\n" + headerText + text + ":\n" + generateChildrenLines(childrenToShow)
          }).filter(_ != "").mkString("\n")

      } else optionsText + "\n" + generateChildrenLines(children))
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
    } else if (args.length > 0 && (args(0) == "-licence" || args(0) == "--licence" || args(0) == "-license" || args(0) == "--license")) {
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
    println("PANDA was called with: " + args.mkString("\"", " ", "\""))
    println()
    println()


    val plannerConfiguration = initialConfiguration.processCommandLineArguments(args)

    println(plannerConfiguration.longInfo)

    if (!plannerConfiguration.config.checkConfigurationIntegrity()) {
      System exit 0
    }

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

    // if we have found some kind of a search result, try to output it.
    if (results.map.contains(SearchResult) && results(SearchResult).isDefined) {

      println("SOLUTION SEQUENCE")
      val plan = results(SearchResult).get

      assert(plan.planSteps forall { _.schema.isPrimitive })

      def psToString(ps: PlanStep): String = {
        val name = ps.schema.name
        val args = ps.arguments map plan.variableConstraints.getRepresentative map { _.shortInfo }

        if (args.isEmpty && name.count(_ == '[') == 1 && name.count(_ == ']') == 1 && name.indexOf('[') < name.indexOf(']'))
          name.replace('[', '(').replace(']', ')')
        else
          name + args.mkString("(", ",", ")")
      }

      val selectedSolution: Seq[PlanStep] = plan.orderingConstraints.graph.topologicalOrdering.get filter { _.schema.isPrimitive }

      println(selectedSolution.zipWithIndex map { case (ps, i) => i + ": " + psToString(ps) } mkString "\n")

      // if we have also found a hierarchy, output that hierarchy
      if (plannerConfiguration.config.postprocessingConfiguration.resultsToProduce.contains(SearchResultWithDecompositionTree)) {
        val planStepIndices: Map[PlanStep, Int] = selectedSolution.zipWithIndex.toMap ++
          plan.planStepsAndRemovedPlanSteps.filter(_.schema.isAbstract).zipWithIndex.map({ case (ps, ind) => ps -> (ind + selectedSolution.length) })

        val rootNodes = planStepIndices filterNot { case (ps, _) => plan.planStepParentInDecompositionTree.contains(ps) } keys

        println("DECOMPOSITION HIERARCHY")
        println("Root tasks: " + rootNodes.map(planStepIndices).mkString(" "))

        plan.planStepsAndRemovedPlanSteps.filter(_.schema.isAbstract) foreach { case abstractPS =>
          val children = plan.planStepParentInDecompositionTree.filter(_._2._1 == abstractPS)
          val appliedMethod = plan.planStepDecomposedByMethod(abstractPS).name.replace('[', '(').replace(']', ')')

          println(planStepIndices(abstractPS) + ": " + psToString(abstractPS) + " [" + appliedMethod + "] -> " + children.keys.map(planStepIndices).mkString(" "))
        }
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

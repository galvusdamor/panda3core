package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.{RandomPlanGenerator, Task}
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.io.Source

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class VerifyRunner(domFile: String, probFile: String, configNumber: Int, parserType: ParserType) {

  import sys.process._

  lazy val (solutionPlan, domain, initialPlan, preprocessTime) = {
    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    val searchConfig = configNumber match {
      case x if x < 0 => SearchConfiguration(Some(1), None, efficientSearch = true, AStarDepthType, Some(TDGMinimumModification), printSearchInfo = true)
      case 1          => SearchConfiguration(None, None, efficientSearch = true, AStarDepthType, Some(TDGMinimumModification), printSearchInfo = true)
      case 2          => SearchConfiguration(None, None, efficientSearch = true, DijkstraType, None, printSearchInfo = true)
      case 3          => SearchConfiguration(None, None, efficientSearch = true, AStarDepthType, Some(TDGMinimumAction), printSearchInfo = true)
    }

    // create the configuration
    val planningConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                               ParsingConfiguration(parserType),
                                               PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = true,
                                                                          liftedReachability = true, groundedReachability = false, planningGraph = true,
                                                                          groundedTaskDecompositionGraph = Some(TopDownTDG),
                                                                          iterateReachabilityAnalysis = false, groundDomain = true),
                                               searchConfig,
                                               PostprocessingConfiguration(Set(ProcessingTimings,
                                                                               SearchStatus, SearchResult,
                                                                               SearchStatistics,
                                                                               //SearchSpace,
                                                                               SolutionInternalString,
                                                                               SolutionDotString,
                                                                               PreprocessedDomainAndPlan)))

    val results: ResultMap = planningConfig.runResultSearch(domInputStream, probInputStream)

    println(results(ProcessingTimings).longInfo)
    println(results(SearchStatistics).longInfo)

    val (processedDomain, processedInitialPlan) = results(PreprocessedDomainAndPlan)
    val ordering = if (configNumber >= 0) {
      val solution = results(SearchResult).get
      // convenience output
      Dot2PdfCompiler.writeDotToFile(solution.dotString, "/home/gregor/solution.pdf")

      //val allOrderings = solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.graph.allTotalOrderings.get
      solution.orderingConstraintsWithoutRemovedPlanSteps.graph.topologicalOrdering.get map { _.schema }

    } else Range(0, -configNumber + 1) map { _ => processedDomain.tasks.head }

    val parsingAndPreprocessingTime = results(ProcessingTimings).integralDataMap()(Timings.PARSING) + results(ProcessingTimings).integralDataMap()(Timings.PREPROCESSING)

    // return the solution and the domain
    (ordering, processedDomain, processedInitialPlan, parsingAndPreprocessingTime)
  }

  def runWithTimeLimit(timelimit: Long, sequenceToVerify: Seq[Task], offsetToK: Int, includeGoal: Boolean = true): (Boolean, Boolean, TimeCapsule, InformationCapsule) = {
    val runner = new Runnable {
      var result: Option[(Boolean, TimeCapsule, InformationCapsule)] = None

      override def run(): Unit = {
        result = Some(VerifyRunner.this.run(sequenceToVerify, offsetToK, includeGoal))
      }
    }
    // start thread
    val thread = new Thread(runner)
    thread.start()

    // wait
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime <= timelimit && runner.result.isEmpty) Thread.sleep(1000)
    thread.stop()

    if (runner.result.isEmpty) (false, false, new TimeCapsule, new InformationCapsule) else (runner.result.get._1, true, runner.result.get._2, runner.result.get._3)
  }


  def run(sequenceToVerify: Seq[Task], offSetToK: Int, includeGoal: Boolean = true, verify: Boolean = true): (Boolean, TimeCapsule, InformationCapsule) = {
    println("PANDA is given the following sequence")
    println(sequenceToVerify map { _.name } mkString "\n")


    // check whether the given sequence is executable ...
    if (verify) {
      val groundTasks = sequenceToVerify map { task => GroundTask(task, Nil) }
      val finalState = groundTasks.foldLeft(initialPlan.groundedInitialState)(
        { case (state, action) =>
          action.substitutedPreconditions foreach { prec => assert(state contains prec, "action " + action.task.name + " prec " + prec.predicate.name) }

          (state diff action.substitutedDelEffects) ++ action.substitutedAddEffects
        })

      if (includeGoal) initialPlan.groundedGoalTask.substitutedPreconditions foreach { goalLiteral => assert(finalState contains goalLiteral, "GOAL: " + goalLiteral.predicate.name) }
    }



    val timeCapsule = new TimeCapsule
    val informationCapsule = new InformationCapsule

    informationCapsule.set(VerifyRunner.PLAN_LENGTH, sequenceToVerify.length)

    //val ordering = Range(0, 8) map { _ => domain.tasks.head }


    // start verification
    val encoder = VerifyEncoding(domain, initialPlan, sequenceToVerify, offSetToK) // use theoretical value //(3)
    println("K " + encoder.K)
    informationCapsule.set(VerifyRunner.ICAPS_K, VerifyEncoding.computeICAPSK(domain, initialPlan, sequenceToVerify))
    informationCapsule.set(VerifyRunner.TSTG_K, VerifyEncoding.computeTSTGK(domain, initialPlan, sequenceToVerify))
    informationCapsule.set(VerifyRunner.LOG_K, VerifyEncoding.computeMethodSize(domain, initialPlan, sequenceToVerify))
    informationCapsule.set(VerifyRunner.OFFSET_K, offSetToK)
    informationCapsule.set(VerifyRunner.ACTUAL_K, encoder.K)
    println(informationCapsule.longInfo)


    timeCapsule start VerifyRunner.VERIFY_TOTAL
    timeCapsule start VerifyRunner.GENERATE_FORMULA
    val usedFormula = encoder.decompositionFormula ++ encoder.stateTransitionFormula ++ encoder.initialState ++ (if (includeGoal) encoder.goalState else Nil) ++ (
      if (verify) encoder.givenActionsFormula else encoder.noAbstractsFormula)
    timeCapsule stop VerifyRunner.GENERATE_FORMULA

    timeCapsule start VerifyRunner.TRANSFORM_DIMACS
    val cnfString = encoder.miniSATString(usedFormula)
    timeCapsule stop VerifyRunner.TRANSFORM_DIMACS

    timeCapsule start VerifyRunner.WRITE_FORMULA
    writeStringToFile(cnfString, new File("__cnfString"))
    timeCapsule stop VerifyRunner.WRITE_FORMULA

    informationCapsule.set(VerifyRunner.NUMBER_OF_VARIABLES, (usedFormula flatMap { _.disjuncts map { _._1 } } distinct).size)
    informationCapsule.set(VerifyRunner.NUMBER_OF_CLAUSES, usedFormula.length)

    //writeStringToFile(usedFormula mkString "\n", new File("__formulaString"))

    timeCapsule start VerifyRunner.SAT_SOLVER
    try {
      println("Starting minisat")
      "minisat __cnfString __res.txt" !
    } catch {
      case rt: RuntimeException => println("Minisat exitcode problem ...")
    }
    timeCapsule stop VerifyRunner.SAT_SOLVER
    timeCapsule stop VerifyRunner.VERIFY_TOTAL

    // postprocessing

    val minisatOutput = Source.fromFile("__res.txt").mkString
    "rm __cnfString __res.txt" !
    val minisatResult = minisatOutput.split("\n")(0)
    println("MiniSAT says: " + minisatResult)
    /*if (minisatResult == "SAT") {
      val minisatAssignment = minisatOutput.split("\n")(1)
      val literals = (minisatAssignment.split(" ") filter { _ != 0 } map { _.toInt }).toSet

      // iterate through layers
      val nodes = Range(-1, encoder.numberOfLayers) flatMap { layer => Range(0, encoder.numberOfActionsPerLayer) map { pos =>
        domain.tasks map { task =>
          val actionString = encoder.action(layer, pos, task)
          val isPres = if (encoder.atoms contains actionString) literals contains (1 + (encoder.atoms indexOf actionString)) else false
          (actionString, isPres)
        } find { _._2 }
      } filter { _.isDefined } map { _.get._1 }
      }

      val edges: Seq[(String, String)] = Range(-1, encoder.numberOfLayers) flatMap { layer => Range(0, encoder.numberOfActionsPerLayer) flatMap { pos => Range(0, encoder
        .numberOfActionsPerLayer) flatMap {
        father =>
          Range(0, encoder.DELTA) flatMap { childIndex =>
            val childString = encoder.childWithIndex(layer, pos, father, childIndex)
            if ((encoder.atoms contains childString) && (literals contains (1 + (encoder.atoms indexOf childString)))) {
              // find parent and myself
              val fatherStringOption = nodes find { _.startsWith("action^" + (layer - 1) + "_" + father) }
              assert(fatherStringOption.isDefined, "action^" + (layer - 1) + "_" + father + " is not present but is a fathers")
              val childStringOption = nodes find { _.startsWith("action^" + layer + "_" + pos) }
              assert(childStringOption.isDefined, "action^" + layer + "_" + pos + " is not present but is a child")
              (fatherStringOption.get, childStringOption.get) :: Nil
            } else Nil
          }
      }
      }
      }

      def changeSATNameToActionName(satName: String): SimpleGraphNode = {
        val actionID = satName.split(",")(1).toInt
        if (actionID >= 0) SimpleGraphNode(satName, domain.tasks(actionID).name) else SimpleGraphNode(satName, satName)
      }

      val decompGraphNames = SimpleDirectedGraph(nodes map changeSATNameToActionName, edges map { case (a, b) => (changeSATNameToActionName(a), changeSATNameToActionName(b)) })
      val decompGraph = SimpleDirectedGraph(nodes, edges)
      Dot2PdfCompiler.writeDotToFile(decompGraph, "decomp.pdf")
      Dot2PdfCompiler.writeDotToFile(decompGraphNames, "decompName.pdf")

      val allTrueAtoms = encoder.atoms.zipWithIndex filter { case (atom, index) => literals contains (index + 1) } map { _._1 }

      writeStringToFile(allTrueAtoms mkString "\n", new File("true.txt"))

      // extract the state trace
      val layerPredicates = allTrueAtoms filter { _ startsWith "predicate" } map { p =>
        val split = p.split(",")
        val layer = split.head.split("_")(1).toInt

        (layer, domain.predicates(split(1).toInt).name)
      } sorted

      //println(layerPredicates mkString "\n")
    }*/

    (minisatResult == "SAT", timeCapsule, informationCapsule)
  }
}

object VerifyRunner {

  val VERIFY_TOTAL     = "99 verify:00:total"
  val GENERATE_FORMULA = "99 verify:10:generate formula"
  val TRANSFORM_DIMACS = "99 verify:20:transform to DIMACS"
  val WRITE_FORMULA    = "99 verify:30:write formula"
  val SAT_SOLVER       = "99 verify:40:SAT solver"

  val allTime = (VERIFY_TOTAL :: GENERATE_FORMULA :: TRANSFORM_DIMACS :: WRITE_FORMULA :: SAT_SOLVER :: Nil).sorted

  val PLAN_LENGTH         = "99 verify:00:plan length"
  val NUMBER_OF_VARIABLES = "99 verify:01:number of variables"
  val NUMBER_OF_CLAUSES   = "99 verify:02:number of clauses"
  val ICAPS_K             = "99 verify:10:K ICAPS"
  val LOG_K               = "99 verify:11:K LOG"
  val TSTG_K              = "99 verify:12:K task schema transition graph"
  val OFFSET_K            = "99 verify:13:K offset"
  val ACTUAL_K            = "99 verify:14:K chosen value"

  val allData = (PLAN_LENGTH :: NUMBER_OF_VARIABLES :: NUMBER_OF_CLAUSES :: ICAPS_K :: LOG_K :: TSTG_K :: OFFSET_K :: ACTUAL_K :: Nil).sorted

  // domains to test
  //val prefix = "/home/gregor/Workspace/panda2-system/domains/XML/"
  val prefix = ""

  val problemsToVerify: Seq[(String, ParserType, Seq[(String, Int)])] =
    ("UM-Translog/domains/UMTranslog.xml", XMLParserType,
      ("UM-Translog/problems/UMTranslog-P-1-AirplanesHub.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-Airplane.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-ArmoredRegularTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-AutoTraincar-bis.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-AutoTraincar.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-AutoTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-FlatbedTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-HopperTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-MailTraincar.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-RefrigeratedRegularTraincar.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-RefrigeratedTankerTraincarHub.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-RefrigeratedTankerTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-Regular2TrainStations2PostOffices.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-RegularTruck-2Regions.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-RegularTruck-3Locations.xml", 1) ::
        //("UM-Translog/problems/UMTranslog-P-1-RegularTruck-4Locations.xml", 1) ::   // TDG pruned and panda2 it is unsolvable
        ("UM-Translog/problems/UMTranslog-P-1-RegularTruckCustom.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-RegularTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-TankerTraincarHub.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-1-TankerTruck.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-2-ParcelsChemicals.xml", 1) ::
        ("UM-Translog/problems/UMTranslog-P-2-RegularTruck.xml", 1) ::
        Nil) ::
      ("Satellite/domains/satellite2.xml", XMLParserType,
        ("Satellite/problems/4--1--3.xml", 1) ::
          ("Satellite/problems/4--2--3.xml", 1) ::
          ("Satellite/problems/4--4--4.xml", 1) ::
          //("Satellite/problems/5--2--2.xml", 3) :: DONT KNOW - probably to hard
          //("Satellite/problems/5--5--5.xml", 1) :: DONT KNOW
          //("Satellite/problems/6--2--2.xml", 1) :: DONT KNOW
          //("Satellite/problems/8--3--4.xml", 1) :: DONT KNOW
          ("Satellite/problems/sat-A.xml", 1) ::
          ("Satellite/problems/sat-B.xml", 1) ::
          ("Satellite/problems/sat-C.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-1obs-1sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-1obs-2sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-2obs-1sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-2obs-1sat-2mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-2obs-2sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-1sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-1sat-2mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-1sat-3mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-2sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-2sat-2mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-2sat-3mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-3sat-1mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-3sat-2mod.xml", 1) ::
          ("Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml", 1) ::
          //("Satellite/problems/satellite2-P-goal-1-simple.xml", 1) ::     non initial HTN
          //("Satellite/problems/satellite2-P-goal-1.xml", 1) ::            non initial HTN
          //("Satellite/problems/satellite2-P-goal-2-simple.xml", 1) ::     non initial HTN
          //("Satellite/problems/satellite2-P-goal-2.xml", 1) ::            non initial HTN
          //("Satellite/problems/satellite2-P-goal-3.xml", 1) ::            non initial HTN
          //("Satellite/problems/satellite2-P-goal-4.xml", 1) ::            non initial HTN
          //("Satellite/problems/satellite2-P-goal-5.xml", 1) ::            non initial HTN
          ("Satellite/problems/satellite2-P-linkingTest.xml", 1) ::
          Nil) ::
      ("SmartPhone/domains/SmartPhone-HierarchicalNoAxioms.xml", XMLParserType,
        ("SmartPhone/problems/OrganizeMeeting_VeryVerySmall.xml", 1) ::
          ("SmartPhone/problems/OrganizeMeeting_VerySmall.xml", 2) ::
          ("SmartPhone/problems/ThesisExampleProblem.xml", 1) ::
          Nil) ::
      ("Woodworking-Socs/domains/woodworking-socs.xml", XMLParserType,
        ("Woodworking-Socs/problems/p01-hierarchical-socs.xml", 1) ::
          ("Woodworking-Socs/problems/p02-variant1-hierarchical.xml", 1) ::
          ("Woodworking-Socs/problems/p02-variant2-hierarchical.xml", 1) ::
          ("Woodworking-Socs/problems/p02-variant3-hierarchical.xml", 1) ::
          ("Woodworking-Socs/problems/p02-variant4-hierarchical.xml", 1) ::
          Nil) ::
      Nil

  //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
  //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
  //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"


  val timeLimit: Long = 10 * 60 * 1000
  val minOffset: Int  = -2
  val maxOffset: Int  = 4

  val numberOfRandomSeeds: Int = 5

  def writeHead(): String = {
    val builder = new StringBuilder
    builder append ("domain" + ",")
    builder append ("problem" + ",")
    builder append ("isSolution" + ",")
    builder append ("satResult" + ",")
    builder append ("completed" + ",")

    // problem statistics
    allData foreach { d => builder append (d + ",") }
    // actual data
    builder append ("preprocessTime" + ",")
    // time
    allTime foreach { t => builder append (t + ",") }

    val line = builder.toString()
    line.substring(0, line.length - 1)
  }

  def writeSingleRun(timeCapsule: TimeCapsule, informationCapsule: InformationCapsule, preprocessTime: Long, isSolution: Boolean, satResult: Boolean, completed: Boolean,
                     domain: String, problem: String): String = {
    val builder = new StringBuilder
    builder append (domain.split("/").last.replaceAll(".xml", "") + ",")
    builder append (problem.split("/").last.replaceAll(".xml", "") + ",")
    builder append (isSolution + ",")
    builder append (satResult + ",")
    builder append (completed + ",")

    // problem statistics
    allData foreach { d => builder append (informationCapsule.integralDataMap().getOrElse(d, Integer.MAX_VALUE) + ",") }
    // actual data
    builder append (preprocessTime + ",")
    // time
    allTime foreach { t => builder append (timeCapsule.integralDataMap().getOrElse(t, Integer.MAX_VALUE) + ",") }

    val line = builder.toString()
    line.substring(0, line.length - 1)
  }

  def runEvaluation(): Unit = {
    val result = problemsToVerify flatMap { case (domainFile, parserType, problems) => problems flatMap { case (problemFile, config) =>
      println("RUN " + domainFile + " " + problemFile)

      val runner = VerifyRunner(prefix + domainFile, prefix + problemFile, config, parserType)

      val solutionLines = Range(minOffset, maxOffset + 1) map { offsetToK =>
        val (isPlan, completed, time, information) = runner.runWithTimeLimit(timeLimit, runner.solutionPlan, offsetToK)

        println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
        println("Preprocess " + runner.preprocessTime)
        println(time.longInfo)
        println(information.longInfo)

        writeSingleRun(time, information, runner.preprocessTime, isSolution = true, satResult = isPlan, completed = completed, domainFile, problemFile)
      }

      val nonSolutionLines = Range(0, numberOfRandomSeeds) flatMap { randomSeed => Range(minOffset, maxOffset + 1) map { offsetToK =>
        val randomPlanGenerator = RandomPlanGenerator(runner.domain, runner.initialPlan)
        val randomPlan = randomPlanGenerator.randomExecutablePlan(runner.solutionPlan.length, randomSeed)

        val (isPlan, completed, time, information) = runner.runWithTimeLimit(timeLimit, randomPlan, offsetToK, includeGoal = false)

        println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
        println("Preprocess " + runner.preprocessTime)
        println(time.longInfo)
        println(information.longInfo)

        writeSingleRun(time, information, runner.preprocessTime, isSolution = false, satResult = isPlan, completed = completed, domainFile, problemFile)
      }
      }
      nonSolutionLines ++ solutionLines
    }
    } mkString "\n"


    writeStringToFile(writeHead + "\n" + result + "\n", "result.csv")
  }

  def runPlanner(domFile: String, probFile: String, length: Int): Unit = {
    val runner = VerifyRunner(domFile, probFile, -length, HDDLParserType)

    runner.run(runner.solutionPlan, 0, includeGoal = true, verify = false)
  }

  def main(args: Array[String]) {
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/OrganizeMeeting_Small.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/ThesisExampleProblem.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/sat-C.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"

    val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp"
    val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p00-htn.lisp"


    //val domFile = args(0)
    //val probFile = args(1)

    //runPlanner(domFile,probFile,10)
    runEvaluation()
  }

}
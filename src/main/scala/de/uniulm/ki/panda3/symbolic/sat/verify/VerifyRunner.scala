package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.io.Source

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class VerifyRunner(domFile: String, probFile: String, configNumber: Int) {

  import sys.process._

  lazy val (solutionPlan, domain, initialPlan, preprocessTime) = {
    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    val searchConfig = configNumber match {
      case 1 => SearchConfiguration(None, None, efficientSearch = true, AStarDepthType, Some(TDGMinimumModification), printSearchInfo = true)
      case 2 => SearchConfiguration(None, None, efficientSearch = true, DijkstraType, None, printSearchInfo = true)
    }

    // create the configuration
    val planningConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                               ParsingConfiguration(XMLParserType),
                                               PreprocessingConfiguration(compileNegativePreconditions = true,
                                                                          liftedReachability = true, groundedReachability = false, planningGraph = true,
                                                                          naiveGroundedTaskDecompositionGraph = true,
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

    val solution = results(SearchResult).get
    val (processedDomain, processedInitialPlan) = results(PreprocessedDomainAndPlan)

    // convenience output
    Dot2PdfCompiler.writeDotToFile(solution.dotString, "/home/gregor/solution.pdf")
    println(results(ProcessingTimings).longInfo)
    println(results(SearchStatistics).longInfo)

    //val allOrderings = solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.graph.allTotalOrderings.get
    val ordering = solution.orderingConstraintsWithoutRemovedPlanSteps.graph.topologicalOrdering.get map { _.schema }

    // check whether we actually got a solution
    val groundTasks = ordering map { task => GroundTask(task, Nil) }
    val finalState = groundTasks.foldLeft(processedInitialPlan.groundedInitialState)(
      { case (state, action) =>
        action.substitutedPreconditions foreach { prec => assert(state contains prec, "action " + action.task.name + " prec " + prec.predicate.name) }

        (state diff action.substitutedDelEffects) ++ action.substitutedAddEffects
      })

    processedInitialPlan.groundedGoalTask.substitutedPreconditions foreach { goalLiteral => assert(finalState contains goalLiteral, "GOAL: " + goalLiteral.predicate.name) }


    // return the solution and the domain
    (ordering, processedDomain, processedInitialPlan, results(ProcessingTimings).integralDataMap()(Timings.TOTAL_TIME))
  }

  def run(sequenceToVerify: Seq[Task]): (Boolean, TimeCapsule, InformationCapsule) = {
    val timeCapsule = new TimeCapsule
    val informationCapsule = new InformationCapsule

    informationCapsule.set(VerifyRunner.PLAN_LENGTH, sequenceToVerify.length)

    //val ordering = Range(0, 8) map { _ => domain.tasks.head }

    println("PANDA found the following solution")
    println(sequenceToVerify map { _.name } mkString "\n")

    // start verification
    val encoder = VerifyEncoding(domain, initialPlan, sequenceToVerify)() // use theoretical value //(3)
    println("K " + encoder.K)
    informationCapsule.set(VerifyRunner.ICAPS_K, VerifyEncoding.computeICAPSK(domain, initialPlan, sequenceToVerify))
    informationCapsule.set(VerifyRunner.TSTG_K, VerifyEncoding.computeTSTGK(domain, initialPlan, sequenceToVerify))
    informationCapsule.set(VerifyRunner.LOG_K, VerifyEncoding.computeMethodSize(domain, initialPlan, sequenceToVerify))




    timeCapsule start VerifyRunner.VERIFY_TOTAL
    timeCapsule start VerifyRunner.GENERATE_FORMULA
    val usedFormula = encoder.decompositionFormula ++ encoder.stateTransitionFormula ++ encoder.initialAndGoalState ++ encoder.givenActionsFormula
    //val usedFormula = encoder.decompositionFormula ++ encoder.stateTransitionFormula ++ encoder.initialAndGoalState ++ encoder.noAbstractsFormula
    timeCapsule stop VerifyRunner.GENERATE_FORMULA

    timeCapsule start VerifyRunner.WRITE_FORMULA
    val cnfString = encoder.miniSATString(usedFormula)
    writeStringToFile(cnfString, new File("__cnfString"))
    timeCapsule stop VerifyRunner.WRITE_FORMULA

    informationCapsule.set(VerifyRunner.NUMBER_OF_VARIABLES,(usedFormula flatMap {_.disjuncts map {_._1}} distinct).size)
    informationCapsule.set(VerifyRunner.NUMBER_OF_CLAUSES,usedFormula.length)

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
    if (minisatResult == "SAT") {
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
    }

    (minisatResult == "SAT", timeCapsule, informationCapsule)
  }
}

object VerifyRunner {

  val VERIFY_TOTAL     = "99 verify:00:total"
  val GENERATE_FORMULA = "99 verify:10:generate formula"
  val WRITE_FORMULA    = "99 verify:20:write formula"
  val SAT_SOLVER       = "99 verify:30:SAT solver"

  val PLAN_LENGTH         = "99 verify:00:plan length"
  val NUMBER_OF_VARIABLES = "99 verify:01:number of variables"
  val NUMBER_OF_CLAUSES   = "99 verify:02:number of clauses"
  val ICAPS_K             = "99 verify:10:K ICAPS"
  val LOG_K               = "99 verify:11:K LOG"
  val TSTG_K              = "99 verify:12:K task schema transition graph"

  def main(args: Array[String]) {
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"

    val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/OrganizeMeeting_Small.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/ThesisExampleProblem.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/sat-C.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"

    //val domFile = args(0)
    //val probFile = args(1)

    val runner = VerifyRunner(domFile, probFile, 1)

    val (isPlan,time,information) = runner.run(runner.solutionPlan)

    println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
    println("Preprocess " + runner.preprocessTime)
    println(time.longInfo)
    println(information.longInfo)

  }
}
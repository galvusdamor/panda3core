package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.io.Source

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class VerifyRunner(domFile: String, probFile: String) {

  import sys.process._

  def run(): Unit = {
    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    // create the configuration
    val searchConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true,
                                             ParsingConfiguration(XMLParserType),
                                             PreprocessingConfiguration(compileNegativePreconditions = true,
                                                                        liftedReachability = true, groundedReachability = false, planningGraph = true,
                                                                        naiveGroundedTaskDecompositionGraph = true,
                                                                        iterateReachabilityAnalysis = false, groundDomain = true),
                                             //SearchConfiguration(None, None, efficientSearch = true, AStarDepthType, Some(TDGMinimumModification), printSearchInfo = true),
                                             SearchConfiguration(Some(1), None, efficientSearch = true, DijkstraType, None, printSearchInfo = true),
                                             PostprocessingConfiguration(Set(ProcessingTimings,
                                                                             SearchStatus, SearchResult,
                                                                             SearchStatistics,
                                                                             //SearchSpace,
                                                                             SolutionInternalString,
                                                                             SolutionDotString,
                                                                             PreprocessedDomainAndPlan)))

    //System.in.read()

    val results: ResultMap = searchConfig.runResultSearch(domInputStream, probInputStream)

    //val solutionPlan = results(SearchResult).get
    val (domain, initialPlan) = results(PreprocessedDomainAndPlan)

    println(results(ProcessingTimings).longInfo)
    println(results(SearchStatistics).longInfo)


    //Dot2PdfCompiler.writeDotToFile(solutionPlan.dotString, "/home/gregor/solution.pdf")

    //println(solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.isTotalOrder())
    //println(solutionPlan.planSteps.length)

    //println(solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.graph.dotString)
    //val allOrderings = solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.graph.allTotalOrderings.get

    //println(allOrderings map {ord => ord map {_.schema.name} mkString " "} mkString "\n")

    //val ordering = solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.graph.topologicalOrdering.get map { _.schema }
    val ordering = Range(0,11) map { _ => domain.tasks.head}

    val groundTasks = ordering map { task => GroundTask(task, Nil) }
    /*val finalState = groundTasks.foldLeft(initialPlan.groundedInitialState)(
      { case (state, action) =>
        action.substitutedPreconditions foreach {prec => assert(state contains prec, "action " + action.task.name + " prec " + prec.predicate.name) }

        (state diff action.substitutedDelEffects) ++ action.substitutedAddEffects
      })

    initialPlan.groundedGoalTask.substitutedPreconditions foreach {goalLiteral => assert(finalState contains goalLiteral, "GOAL: " + goalLiteral.predicate.name)}
    */

    println("PANDA found the following solution")
    println(ordering map { _.name } mkString "\n")

    // start verification
    val encoder = VerifyEncoding(domain, initialPlan, ordering)(6)
    println("K " + encoder.K + " DELTA " + encoder.DELTA)
    println("Theoretical K " + VerifyEncoding.computeTheoreticalK(domain, initialPlan, ordering))

    Dot2PdfCompiler.writeDotToFile(domain.taskSchemaTransitionGraph, "/home/gregor/tstg.pdf")

    val startTime = System.currentTimeMillis()
    //val usedFormula = encoder.decompositionFormula ++ encoder.stateTransitionFormula ++ encoder.initialAndGoalState ++ encoder.givenActionsFormula
    val usedFormula = encoder.decompositionFormula ++ encoder.stateTransitionFormula ++ encoder.initialAndGoalState ++ encoder.noAbstractsFormula
    val formulaTime = System.currentTimeMillis()
    val cnfString = encoder.miniSATString(usedFormula)
    writeStringToFile(cnfString, new File("__cnfString"))
    writeStringToFile(usedFormula.toString(), new File("__formulaString"))
    val cnfStringTime = System.currentTimeMillis()




    try {
      println("Starting minisat")
      "minisat __cnfString __res.txt" !
    } catch {
      case rt: RuntimeException => println("Minisat exitcode problem ...")
    }
    val satSolverTime = System.currentTimeMillis()
    println("Variables : " + encoder.atoms.length + " Constraints: " + encoder.decompositionFormula.length)
    println("Time needed to compute the formula: " + (formulaTime - startTime) + "ms")
    println("Time needed to convert the formula into a string: " + (cnfStringTime - formulaTime) + "ms")
    println("Time needed to solve the SAT instance: " + (satSolverTime - cnfStringTime) + "ms")
    println("Total Time: " + (satSolverTime - startTime) + "ms")

    val minisatOutput = Source.fromFile("__res.txt").mkString
    //"rm __cnfString __res.txt" !
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
            //literals contains (1 + (encoder.atoms indexOf actionString)) else false
            //(actionString, isPres)
          }
      }
      }
      }

      val decompGraph = SimpleDirectedGraph(nodes, edges)
      Dot2PdfCompiler.writeDotToFile(decompGraph, "/home/gregor/decomp.pdf")


      val allTrueAtoms = encoder.atoms.zipWithIndex filter { case (atom, index) => literals contains (index + 1) } map { _._1 }

      writeStringToFile(allTrueAtoms mkString "\n", new File("/home/gregor/true.txt"))
    }

    // print action mapping to numbers:
    //println(domain.tasks map { t => t.name + " -> " + encoder.taskIndex(t) } mkString "\n")
    //println(domain.predicates map { t => t.name + " -> " + encoder.predicateIndex(t) } mkString "\n")


  }
}

object VerifyRunner {
  def main(args: Array[String]) {
    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-Airplane.xml"

    //val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/SmartPhone-HierarchicalNoAxioms.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/ThesisExampleProblem.xml"

    val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/sat-C.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"

    val runner = VerifyRunner(domFile, probFile)

    runner.run()
  }
}
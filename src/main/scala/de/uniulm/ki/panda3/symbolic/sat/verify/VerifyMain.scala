package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io._

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.{DefaultLongInfo, PrettyPrintable}
import de.uniulm.ki.panda3.symbolic.compiler.{AllOrderings, OneRandomOrdering}
import de.uniulm.ki.panda3.symbolic.domain.{RandomPlanGenerator, Task}
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.io.Source
import scala.util.Random

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off method.length cyclomatic.complexity
case class VerifyRunner(domFile: String, probFile: String, configNumber: Int, parserType: ParserType, satsolver: Solvertype) {

  import sys.process._

  lazy val (solutionPlan, domain, initialPlan, preprocessTime) = {
    val domInputStream = new FileInputStream(domFile)
    val probInputStream = new FileInputStream(probFile)

    val (searchConfig, usePlanningGraph) = configNumber match {
      case x if x < 0 => (PlanBasedSearch(Some(0), DFSType, Nil, Nil, LCFR, efficientSearch = false), false)
      case 1          => (PlanBasedSearch(None, AStarDepthType(1), TDGMinimumModificationWithCycleDetection() :: Nil, Nil, LCFR), true)
      case 2          => (PlanBasedSearch(None, DijkstraType, Nil, Nil, LCFR), true)
      case 3          => (PlanBasedSearch(None, AStarDepthType(1), TDGMinimumAction() :: Nil, Nil, LCFR), true)
      case 4          => (PlanBasedSearch(None, AStarDepthType(1), TDGMinimumModificationWithCycleDetection() :: Nil, Nil, LCFR), false)
      case 5          => (PlanBasedSearch(None, DijkstraType, Nil, Nil, LCFR), false)
      case 6          => (PlanBasedSearch(None, GreedyType, TDGMinimumModificationWithCycleDetection() :: Nil, Nil, LCFR), false)
    }

    // create the configuration
    val planningConfig = PlanningConfiguration(printGeneralInformation = true, printAdditionalData = true, randomSeed = 42, timeLimit = if (configNumber < 0) Some(0) else None,
                                               ParsingConfiguration(stripHybrid = false, eliminateEquality = false),
                                               PreprocessingConfiguration(compileNegativePreconditions = true, compileUnitMethods = usePlanningGraph,
                                                                          compileOrderInMethods = None, compileInitialPlan = false, splitIndependentParameters = false,
                                                                          convertToSASP = false, compileUselessAbstractTasks = false,
                                                                          liftedReachability = true,
                                                                          groundedReachability = Some(if (usePlanningGraph) PlanningGraphWithMutexes else PlanningGraph),
                                                                          groundedTaskDecompositionGraph = Some(TwoWayTDG),
                                                                          iterateReachabilityAnalysis = true, groundDomain = true),
                                               searchConfig,
                                               PostprocessingConfiguration(Set(ProcessingTimings,
                                                                               SearchStatus, SearchResult,
                                                                               SearchStatistics,
                                                                               //SearchSpace,
                                                                               SolutionInternalString,
                                                                               SolutionDotString,
                                                                               PreprocessedDomainAndPlan,
                                                                               FinalTaskDecompositionGraph)))

    val results: ResultMap = planningConfig.runResultSearch(domInputStream, probInputStream)

    println(results(ProcessingTimings).longInfo)
    println(results(SearchStatistics).longInfo)


    val processedTDG = results(FinalTaskDecompositionGraph)
    val (processedDomain, processedInitialPlan) = results(PreprocessedDomainAndPlan)
    //ot2PdfCompiler.writeDotToFile(processedTDG.dotString(DirectedGraphDotOptions(labelNodesWithNumbers = true)), "tdg.pdf")

    //assert(processedDomain.isTotallyOrdered, "Not totally ordered")

    val ordering = if (configNumber >= 0) {
      val solution = results(SearchResult).get
      // convenience output
      //Dot2PdfCompiler.writeDotToFile(solution.dotString, "/home/gregor/solution.pdf")

      //val allOrderings = solutionPlan.orderingConstraintsWithoutRemovedPlanSteps.graph.allTotalOrderings.get
      solution.orderingConstraintsWithoutRemovedPlanSteps.graph.topologicalOrdering.get map { _.schema }
    } else Range(0, -configNumber + 1) map { _ => processedDomain.tasks.head }


    //println(VerifyEncoding.computeTDG(processedDomain, processedInitialPlan, processedTDG, ordering.length))

    val parsingAndPreprocessingTime = results(ProcessingTimings).integralDataMap()(Timings.PARSING) + results(ProcessingTimings).integralDataMap()(Timings.PREPROCESSING)

    // return the solution and the domain
    (ordering, processedDomain, processedInitialPlan, parsingAndPreprocessingTime)
  }

  def runWithTimeLimit(timelimit: Long, sequenceToVerify: Seq[Task], offsetToK: Int, includeGoal: Boolean = true, verify: Boolean = true, defineK: Option[Int] = None):
  (Boolean, Boolean, TimeCapsule, InformationCapsule) = {
    val runner = new Runnable {
      var result: Option[(Boolean, TimeCapsule, InformationCapsule)] = None

      override def run(): Unit = {
        result = Some(VerifyRunner.this.run(sequenceToVerify, offsetToK, includeGoal, verify, defineK))
      }
    }
    // start thread
    val thread = new Thread(runner)
    thread.start()

    // wait
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime <= timelimit && runner.result.isEmpty && thread.isAlive) Thread.sleep(1000)
    thread.stop()

    if (runner.result.isEmpty) (false, false, new TimeCapsule, new InformationCapsule) else (runner.result.get._1, true, runner.result.get._2, runner.result.get._3)
  }


  def checkIfTaskSequenceIsAValidPlan(sequenceToVerify: Seq[Task], checkGoal: Boolean = true): Unit = {
    val groundTasks = sequenceToVerify map { task => GroundTask(task, Nil) }
    val finalState = groundTasks.foldLeft(initialPlan.groundedInitialState)(
      { case (state, action) =>
        action.substitutedPreconditions foreach { prec => assert(state contains prec, "action " + action.task.name + " prec " + prec.predicate.name) }

        (state diff action.substitutedDelEffects) ++ action.substitutedAddEffects
      })

    if (checkGoal) initialPlan.groundedGoalTask.substitutedPreconditions foreach { goalLiteral => assert(finalState contains goalLiteral, "GOAL: " + goalLiteral.predicate.name) }
  }

  def run(sequenceToVerify: Seq[Task], offSetToK: Int, includeGoal: Boolean = true, verify: Boolean = true, defineK: Option[Int] = None): (Boolean, TimeCapsule, InformationCapsule) = try {
    if (verify) {
      println("PANDA is given the following sequence")
      println(sequenceToVerify map { _.name } mkString "\n")
    }

    // check whether the given sequence is executable ...
    //if (verify) checkIfTaskSequenceIsAValidPlan(sequenceToVerify, includeGoal)


    val timeCapsule = new TimeCapsule
    val informationCapsule = new InformationCapsule

    informationCapsule.set(VerifyRunner.PLAN_LENGTH, sequenceToVerify.length)
    informationCapsule.set(Information.NUMBER_OF_CONSTANTS, domain.constants.length)
    informationCapsule.set(Information.NUMBER_OF_PREDICATES, domain.predicates.length)
    informationCapsule.set(Information.NUMBER_OF_ACTIONS, domain.tasks.length)
    informationCapsule.set(Information.NUMBER_OF_ABSTRACT_ACTIONS, domain.abstractTasks.length)
    informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_ACTIONS, domain.primitiveTasks.length)
    informationCapsule.set(Information.NUMBER_OF_METHODS, domain.decompositionMethods.length)

    //val ordering = Range(0, 8) map { _ => domain.tasks.head }


    // start verification
    val encoder = //TreeEncoding(domain, initialPlan, sequenceToVerify.length, offSetToK)
      if (domain.isTotallyOrdered && initialPlan.orderingConstraints.isTotalOrder() && !verify)
        TotallyOrderedEncoding(timeCapsule, domain, initialPlan, OnlyNormalise, sequenceToVerify.length, offSetToK, defineK)
      else if (verify)
        GeneralEncoding(timeCapsule, domain, initialPlan, sequenceToVerify, offSetToK, defineK)
      else TreeEncoding(timeCapsule, domain, initialPlan, sequenceToVerify.length, offSetToK, defineK)

    // (3)
    println("K " + encoder.K)
    informationCapsule.set(VerifyRunner.ICAPS_K, VerifyEncoding.computeICAPSK(domain, initialPlan, sequenceToVerify.length))
    informationCapsule.set(VerifyRunner.TSTG_K, VerifyEncoding.computeTSTGK(domain, initialPlan, sequenceToVerify.length))
    informationCapsule.set(VerifyRunner.DP_K, VerifyEncoding.computeTDG(domain, initialPlan, sequenceToVerify.length, Math.max, 0))
    informationCapsule.set(VerifyRunner.LOG_K, VerifyEncoding.computeMethodSize(domain, initialPlan, sequenceToVerify.length))
    informationCapsule.set(VerifyRunner.OFFSET_K, offSetToK)
    informationCapsule.set(VerifyRunner.ACTUAL_K, encoder.K)
    println(informationCapsule.longInfo)

    timeCapsule start VerifyRunner.VERIFY_TOTAL
    timeCapsule start VerifyRunner.GENERATE_FORMULA
    //println("READY")
    //System.in.read()
    val stateFormula = encoder.stateTransitionFormula ++ encoder.initialState ++ (if (includeGoal) encoder.goalState else Nil) ++ (
      if (verify) encoder.givenActionsFormula else encoder.noAbstractsFormula)
    val usedFormula = encoder.decompositionFormula ++ stateFormula
    //println("Done")
    //System.in.read()
    timeCapsule stop VerifyRunner.GENERATE_FORMULA

    timeCapsule start VerifyRunner.TRANSFORM_DIMACS
    println("READY TO WRITE")
    val writer = new BufferedWriter(new FileWriter(new File(VerifyRunner.fileDir + "__cnfString")))
    val atomMap = encoder.miniSATString(usedFormula.toArray, writer)
    println("FLUSH")
    writer.flush()
    writer.close()
    println("CLOSE")
    timeCapsule stop VerifyRunner.TRANSFORM_DIMACS

    encoder match {
      case pathbased: PathBasedEncoding[_, _] =>
        //println(tot.primitivePaths map { case (a, b) => (a, b map { _.name }) } mkString "\n")
        informationCapsule.set(VerifyRunner.NUMBER_OF_PATHS, pathbased.primitivePaths.length)
        println("NUMBER OF PATHS " + pathbased.primitivePaths.length)
      case _                                  =>
    }

    encoder match {
      case tot: TotallyOrderedEncoding => informationCapsule.set(VerifyRunner.MAX_PLAN_LENGTH, tot.primitivePaths.length)
      case tree: TreeEncoding          => informationCapsule.set(VerifyRunner.MAX_PLAN_LENGTH, tree.taskSequenceLength)
      case _                           =>
    }

    println(timeCapsule.integralDataMap())

    //System exit 0

    //timeCapsule start VerifyRunner.WRITE_FORMULA
    //writeStringToFile(cnfString, new File("__cnfString"))
    //timeCapsule stop VerifyRunner.WRITE_FORMULA

    //writeStringToFile(usedFormula mkString "\n", new File("__formulaString"))

    timeCapsule start VerifyRunner.SAT_SOLVER
    try {
      satsolver match {
        case MINISAT       =>
          println("Starting minisat")
          ("minisat " + VerifyRunner.fileDir + "__cnfString " + VerifyRunner.fileDir + "__res.txt") !
        case CRYPTOMINISAT =>
          println("Starting cryptominisat5")
          ("cryptominisat5 --verb 0 " + VerifyRunner.fileDir + "__cnfString") #> new File(VerifyRunner.fileDir + "__res.txt") !
      }
    } catch {
      case rt: RuntimeException => println("Minisat exitcode problem ...")
    }
    timeCapsule stop VerifyRunner.SAT_SOLVER
    timeCapsule stop VerifyRunner.VERIFY_TOTAL


    val formulaVariables: Seq[String] = (usedFormula flatMap { _.disjuncts map { _._1 } }).distinct
    informationCapsule.set(VerifyRunner.NUMBER_OF_VARIABLES, formulaVariables.size)
    informationCapsule.set(VerifyRunner.NUMBER_OF_CLAUSES, usedFormula.length)
    informationCapsule.set(VerifyRunner.STATE_FORMULA, stateFormula.length)
    informationCapsule.set(VerifyRunner.ORDER_CLAUSES, encoder.decompositionFormula count { _.disjuncts forall { case (a, _) => a.startsWith("before") || a.startsWith("childof") } })
    informationCapsule.set(VerifyRunner.METHOD_CHILDREN_CLAUSES, encoder.numberOfChildrenClauses)




    // postprocessing

    val solverOutput = Source.fromFile(VerifyRunner.fileDir + "__res.txt").mkString
    val (solveState, assignment) = satsolver match {
      case MINISAT       =>
        val splitted = solverOutput.split("\n")
        if (splitted.length == 1) (splitted(0), "") else (splitted(0), splitted(1))
      case CRYPTOMINISAT =>
        val cleanString = solverOutput.replaceAll("s ", "").replaceAll("v ", "")
        val splitted = cleanString.split("\n", 2)

        if (splitted.length == 1) (splitted.head, "")
        else (splitted.head, splitted(1).replaceAll("\n", " "))
    }

    // delete files
    ("rm " + VerifyRunner.fileDir + "__cnfString " + VerifyRunner.fileDir + "__res.txt") !

    // report on the result
    println("MiniSAT says: " + solveState)
    val solved = solveState == "SAT" || solveState == "SATISFIABLE"


    // postprocessing
    /* if (solved) {
       // things that are independent from the solver type
       val literals: Set[Int] = (assignment.split(" ") filter { _ != "" } map { _.toInt } filter { _ != 0 }).toSet

       val allTrueAtoms: Set[String] = (atomMap filter { case (atom, index) => literals contains (index + 1) }).keys.toSet
       writeStringToFile(allTrueAtoms mkString "\n", new File("true.txt"))

       val (graphNodes, graphEdges) = encoder match {
         case g: GeneralEncoding =>
           // iterate through layers
           val nodes = Range(-1, encoder.numberOfLayers) flatMap { layer => Range(0, g.numberOfActionsPerLayer) map { pos => domain.tasks map { task =>
             val actionString = g.action(layer, pos, task)
             val isPres = if (atomMap contains actionString) literals contains (1 + atomMap(actionString)) else false
             (actionString, isPres)
           } find { _._2 }
           } filter { _.isDefined } map { _.get._1 }
           }

           val edges: Seq[(String, String)] = Range(-1, encoder.numberOfLayers) flatMap { layer => Range(0, g.numberOfActionsPerLayer) flatMap { pos => Range(0, g
             .numberOfActionsPerLayer) flatMap {
             father =>
               Range(0, encoder.DELTA) flatMap { childIndex =>
                 val childString = g.childWithIndex(layer, pos, father, childIndex)
                 if ((atomMap contains childString) && (literals contains (1 + atomMap(childString)))) {
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

           (nodes, edges)

         case pbe: PathBasedEncoding =>
           val nodes = formulaVariables filter { _.startsWith("action!") } filter allTrueAtoms.contains

           val edges = nodes flatMap { parent => nodes flatMap { child =>
             val parentLayer = parent.split("!").last.split("_").head.toInt
             val childLayer = child.split("!").last.split("_").head.toInt

             if (parentLayer + 1 != childLayer) Nil
             else {
               val parentPathString = parent.split("_").last.split(",").head
               val childPathString = child.split("_").last.split(",").head

               assert(parentPathString.count({ case ';' => true; case _ => false }) + 1 == childPathString.count({ case ';' => true; case _ => false }))
               val parentPath = parentPathString.split(";") map { _.toInt }
               val childPath = childPathString.split(";") map { _.toInt }

               if (parentPath sameElements childPath.take(parentPath.length)) (parent, child) :: Nil else Nil
             }
           }
           }
           val primitiveSolution = pbe match {
             case tot: TotallyOrderedEncoding =>
               // check executability of the plan

               val graph = SimpleDirectedGraph(nodes, edges)

               graph.sinks sortWith { case (t1, t2) =>
                 val path1 = t1.split("_").last.split(",").head.split(";") map { _.toInt }
                 val path2 = t2.split("_").last.split(",").head.split(";") map { _.toInt }
                 PathBasedEncoding.pathSortingFunction(path1, path2)
               } map { t => val actionIDX = t.split(",").last.toInt; domain.tasks(actionIDX) }

             case tree: TreeEncoding =>
               val primitiveActions = allTrueAtoms filter { _.startsWith("action^") }
               println(primitiveActions mkString "\n")
               val actionsPerPosition = primitiveActions groupBy { _.split("_")(1).split(",")(0).toInt }
               val actionSequence = actionsPerPosition.keySet.toSeq.sorted map { pos => assert(actionsPerPosition(pos).size == 1); actionsPerPosition(pos).head }
               val taskSequence = actionSequence map { t => val actionIDX = t.split(",").last.toInt; domain.tasks(actionIDX) }

               val pathToPos = allTrueAtoms filter { _.startsWith("pathToPos_") }
               println(pathToPos mkString "\n")
               val active = allTrueAtoms filter { _.startsWith("active") }
               println(active mkString "\n")
               assert(pathToPos.size == taskSequence.length)
               assert(active.size == taskSequence.length, "ACTIVE " + active.size + " vs " + taskSequence.length)

               val graph = SimpleDirectedGraph(nodes, edges)
               println(graph.sinks mkString "\n")
               assert(graph.sinks.length == taskSequence.length, "SINKS " + graph.sinks.length + " vs " + taskSequence.length)
               val nextPredicates = allTrueAtoms filter { _.startsWith("next") }
               println(nextPredicates mkString "\n")
               assert(nextPredicates.size == taskSequence.length + 1, "NEXT " + nextPredicates.size + " vs " + (taskSequence.length + 1))

               val nextRel: Seq[(Array[Int], Array[Int])] =
                 nextPredicates map { n => n.split("_").drop(1) } map { case l => (l.head, l(1)) } map { case (a, b) => (a.split(";") map { _.toInt }, b.split(";") map { _.toInt }) } toSeq

               println(nextRel map { case (a, b) => (a mkString ",") + ", " + (b mkString ",") } mkString "\n")
               nextRel foreach { case (a, b) => assert(!(a sameElements b), "IDENTICAL " + (a mkString ",") + ", " + (b mkString ",")) }



               val lastPath = nextRel.indices.foldLeft((Array(-1), nextRel))(
                 { case ((current, pairs), _) =>
                   val (nextNext, remaining) = pairs partition { _._1 sameElements current }
                   assert(nextNext.length == 1)

                   (nextNext.head._2, remaining)
                 })

               assert(lastPath._1 sameElements Integer.MAX_VALUE :: Nil)

               taskSequence
           }

           primitiveSolution foreach { t => assert(t.isPrimitive) }
           println("CHECKING primitive solution of length " + primitiveSolution.length + " ...")
           println(primitiveSolution map { _.name } mkString "\n")
           checkIfTaskSequenceIsAValidPlan(primitiveSolution, checkGoal = true)
           println(" done.")

           (nodes, edges)
       }

       def changeSATNameToActionName(satName: String): SimpleGraphNode = {
         val actionID = satName.split(",").last.toInt
         if (actionID >= 0) SimpleGraphNode(satName, (if (domain.tasks(actionID).isPrimitive) "!" else "") + domain.tasks(actionID).name) else SimpleGraphNode(satName, satName)
         //if (actionID >= 0) SimpleGraphNode(satName, "") else SimpleGraphNode(satName, satName)
       }

       val decompGraphNames = SimpleDirectedGraph(graphNodes map changeSATNameToActionName, graphEdges map { case (a, b) => (changeSATNameToActionName(a), changeSATNameToActionName(b)) })
       val decompGraph = SimpleDirectedGraph(graphNodes, graphEdges)
       writeStringToFile(decompGraphNames.dotString, "decompName.dot")
       Dot2PdfCompiler.writeDotToFile(decompGraph, "decomp.pdf")
       Dot2PdfCompiler.writeDotToFile(decompGraphNames, "decompName.pdf")

       // no isolated nodes
       decompGraphNames.vertices foreach { v =>
         val (indeg, outdeg) = decompGraphNames.degrees(v)
         assert(indeg + outdeg != 0, "unconnected action " + v)
       }

       if (encoder.isInstanceOf[PathBasedEncoding]) {
         // check integrity of the methods
         decompGraphNames.vertices filter { v => decompGraphNames.degrees(v)._2 != 0 } foreach { v =>
           // either it is primitive
           val nei = decompGraphNames.edges(v)
           val myAction = domain.tasks(v.id.split(",").last.toInt)
           if (myAction.isPrimitive) {
             assert(nei.size == 1)
             assert(nei.head.name == v.name)
           } else {
             val subTasks: Seq[Task] = nei map { n => domain.tasks(n.id.split(",").last.toInt) }
             val tasksSchemaCount = subTasks groupBy { p => p }
             val possibleMethods = domain.methodsForAbstractTasks(myAction) map { _.subPlan.planStepsWithoutInitGoal } filter { planSteps =>
               val sameSize = planSteps.length == subTasks.length
               val planSchemaCount = planSteps groupBy { _.schema }
               val sameTasks = tasksSchemaCount.size == planSchemaCount.size && (tasksSchemaCount.keys forall planSchemaCount.contains)

               if (sameSize && sameTasks) tasksSchemaCount.keys forall { t => planSchemaCount(t).size == tasksSchemaCount(t).size } else false
             }
             assert(possibleMethods.nonEmpty, "Node " + v + " has no valid decomposition")
           }
         }

         // check order of methods
         decompGraphNames.vertices filter { v => decompGraphNames.degrees(v)._2 != 0 } foreach { v =>
           // either it is primitive
           val nei = decompGraphNames.edges(v)
           val myAction = domain.tasks(v.id.split(",").last.toInt)
           if (myAction.isPrimitive) {
             assert(nei.size == 1)
             assert(nei.head.name == v.name)
           } else if (encoder.isInstanceOf[TotallyOrderedEncoding]) {
             val subTasks: Seq[Task] = nei map { n => (n.id, domain.tasks(n.id.split(",").last.toInt)) } sortWith { case ((t1, _), (t2, _)) =>
               val path1 = t1.split("_").last.split(",").head.split(";") map { _.toInt }
               val path2 = t2.split("_").last.split(",").head.split(";") map { _.toInt }
               PathBasedEncoding.pathSortingFunction(path1, path2)
             } map { _._2 }

             val orderedMethods =
               domain.methodsForAbstractTasks(myAction) map { _.subPlan } map { plan =>
                 assert(plan.orderingConstraints.graph.allTotalOrderings.get.size == 1)
                 plan.orderingConstraints.graph.allTotalOrderings.get.head map { _.schema }
               } filter { _ == subTasks }

             assert(orderedMethods.nonEmpty, "Node " + v + " has no correctly ordered decomposition")
           }
         }
       }
     }*/

    (solved, timeCapsule, informationCapsule)
  } catch {
    case t: Throwable =>
      t.printStackTrace()
      (false, new TimeCapsule, new InformationCapsule)
  }
}

object VerifyRunner {

  val VERIFY_TOTAL     = "99 verify:00:total"
  val GENERATE_FORMULA = "99 verify:10:generate formula"
  val TRANSFORM_DIMACS = "99 verify:20:transform to DIMACS"
  val WRITE_FORMULA    = "99 verify:30:write formula"
  val SAT_SOLVER       = "99 verify:40:SAT solver"

  val allTime = (VERIFY_TOTAL :: GENERATE_FORMULA :: TRANSFORM_DIMACS :: /*WRITE_FORMULA :: */ SAT_SOLVER :: Nil).sorted

  val PLAN_LENGTH             = "99 verify:00:plan length"
  val NUMBER_OF_VARIABLES     = "99 verify:01:number of variables"
  val NUMBER_OF_CLAUSES       = "99 verify:02:number of clauses"
  val ICAPS_K                 = "99 verify:10:K ICAPS"
  val LOG_K                   = "99 verify:11:K LOG"
  val TSTG_K                  = "99 verify:12:K task schema transition graph"
  val DP_K                    = "99 verify:13:K DP"
  val OFFSET_K                = "99 verify:14:K offset"
  val ACTUAL_K                = "99 verify:15:K chosen value"
  val STATE_FORMULA           = "99 verify:20:state formula"
  val ORDER_CLAUSES           = "99 verify:21:order clauses"
  val METHOD_CHILDREN_CLAUSES = "99 verify:22:method children clauses"
  val NUMBER_OF_PATHS         = "99 verify:30:number of paths"
  val MAX_PLAN_LENGTH         = "99 verify:31:maximum plan length"

  val allData              = (PLAN_LENGTH :: NUMBER_OF_VARIABLES :: NUMBER_OF_CLAUSES :: ICAPS_K :: LOG_K :: TSTG_K :: OFFSET_K :: ACTUAL_K :: STATE_FORMULA :: ORDER_CLAUSES ::
    METHOD_CHILDREN_CLAUSES :: NUMBER_OF_PATHS :: MAX_PLAN_LENGTH :: Nil).sorted
  val allProblemProperties =
    (Information.NUMBER_OF_CONSTANTS ::
      Information.NUMBER_OF_PREDICATES ::
      Information.NUMBER_OF_ACTIONS ::
      Information.NUMBER_OF_ABSTRACT_ACTIONS ::
      Information.NUMBER_OF_PRIMITIVE_ACTIONS ::
      Information.NUMBER_OF_METHODS :: Nil).sorted

  // domains to test
  //val prefix = "/home/gregor/Workspace/panda2-system/domains/XML/"
  val prefix = ""

  val fileDir = "/dev/shm/"
  //val fileDir = "/media/tmpfs/"
  //val fileDir = ""

  val problemsToVerify: Seq[(String, ParserType, Seq[(String, Int)])] =

  /*("blocksworld/domain/domain.hpddl", HPDDLParserType,
    ("blocksworld/problems/pfile_005.pddl",1) ::
    ("blocksworld/problems/pfile_010.pddl",1) ::
    ("blocksworld/problems/pfile_015.pddl",1) ::
    ("blocksworld/problems/pfile_020.pddl",1) ::
    ("blocksworld/problems/pfile_025.pddl",1) ::
    ("blocksworld/problems/pfile_030.pddl",1) ::
    ("blocksworld/problems/pfile_035.pddl",1) ::
    ("blocksworld/problems/pfile_040.pddl",1) ::
    ("blocksworld/problems/pfile_045.pddl",1) ::
    ("blocksworld/problems/pfile_050.pddl",1) ::
    ("blocksworld/problems/pfile_055.pddl",1) ::
    ("blocksworld/problems/pfile_060.pddl",1) ::
    ("blocksworld/problems/pfile_065.pddl",1) ::
    ("blocksworld/problems/pfile_070.pddl",1) ::
    ("blocksworld/problems/pfile_075.pddl",1) ::
    ("blocksworld/problems/pfile_080.pddl",1) ::
    ("blocksworld/problems/pfile_085.pddl",1) ::
    ("blocksworld/problems/pfile_090.pddl",1) ::
    ("blocksworld/problems/pfile_095.pddl",1) ::
    ("blocksworld/problems/pfile_100.pddl",1) ::
    Nil
    ) ::
    ("robot/domain/domain.hpddl", HPDDLParserType,
      ("robot/problems/pfile_05_010.pddl", 1) ::
        ("robot/problems/pfile_10_020.pddl", 1) ::
        ("robot/problems/pfile_15_030.pddl", 1) ::
        ("robot/problems/pfile_20_040.pddl", 1) ::
        ("robot/problems/pfile_25_050.pddl", 1) ::
        ("robot/problems/pfile_30_060.pddl", 1) ::
        ("robot/problems/pfile_35_070.pddl", 1) ::
        ("robot/problems/pfile_40_080.pddl", 1) ::
        ("robot/problems/pfile_45_090.pddl", 1) ::
        ("robot/problems/pfile_50_100.pddl", 1) ::
        Nil
      ) ::*/
  /* ("UM-Translog/domains/UMTranslog.xml", XMLParserType,
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
         //("Satellite/problems/5--2--2.xml", 3) :: //DONT KNOW - probably to hard
         //("Satellite/problems/5--5--5.xml", 1) :: //DONT KNOW
         //("Satellite/problems/6--2--2.xml", 1) :: //DONT KNOW
         //("Satellite/problems/8--3--4.xml", 1) :: //DONT KNOW
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
         Nil
       ) ::
     ("SmartPhone/domains/SmartPhone-HierarchicalNoAxioms.xml", XMLParserType,
       ("SmartPhone/problems/OrganizeMeeting_VeryVerySmall.xml", 1) ::
         ("SmartPhone/problems/OrganizeMeeting_VerySmall.xml", 1) ::
         //("SmartPhone/problems/OrganizeMeeting_Small.xml", 2) ::
         //("SmartPhone/problems/OrganizeMeeting_Large.xml", 2) ::
         ("SmartPhone/problems/ThesisExampleProblem.xml", 1) ::
         Nil) ::
     ("Woodworking-Socs/domains/woodworking-socs.xml", XMLParserType,
       ("Woodworking-Socs/problems/p01-hierarchical-socs.xml", 1) ::
         ("Woodworking-Socs/problems/p02-variant1-hierarchical.xml", 1) ::
         ("Woodworking-Socs/problems/p02-variant2-hierarchical.xml", 1) ::
         ("Woodworking-Socs/problems/p02-variant3-hierarchical.xml", 1) ::
         ("Woodworking-Socs/problems/p02-variant4-hierarchical.xml", 1) ::
         Nil) ::*/
    ("domain.lisp", HDDLParserType,
      ("problems/p-0001-clear-road-wreck.lisp", 4) :: // SOL 185
        ("problems/p-0002-plow-road.lisp", 4) :: // SOL 74
        ("problems/p-0003-set-up-shelter.lisp", 6) :: // SOL 46752
        ("problems/p-0004-provide-medical-attention.lisp", 4) :: // SOL 10
        ("problems/p-0005-clear-road-wreck.lisp", 4) :: // SOL 116
        ("problems/p-0006-clear-road-wreck.lisp", 4) :: // SOL 77
        //("problems/p-0007-provide-temp-heat.lisp", 4) :: // SOL 2790
        ("problems/p-0008-provide-medical-attention.lisp", 4) :: // SOL 230
        ("problems/p-0009-quell-riot.lisp", 4) :: // SOL 84
        //  ("problems/p-0010-set-up-shelter.lisp", 6) :: // SOL GROUND 62423
        ("problems/p-0011-plow-road.lisp", 4) :: // SOL 73
        ("problems/p-0012-plow-road.lisp", 4) :: // SOL 84
        ("problems/p-0013-clear-road-hazard.lisp", 6) :: // SOL 308
        ("problems/p-0014-fix-power-line.lisp", 4) :: // SOL 28
        ("problems/p-0015-clear-road-hazard.lisp", 6) :: // SOL 417
        ("problems/p-0016-fix-power-line.lisp", 4) :: // SOL 30
        //("problems/p-0017-clear-road-tree.lisp",4) ::      // BUG
        ("problems/p-0018-fix-power-line.lisp", 4) :: // SOL 30
        ("problems/p-0019-clear-road-wreck.lisp", 4) :: // SOL 182
        ("problems/p-0020-set-up-shelter.lisp", 6) :: // SOL 23971
        ("problems/p-0021-plow-road.lisp", 4) :: // SOL 80
        ("problems/p-0022-provide-medical-attention.lisp", 4) :: // SOL 230
        ("problems/p-0023-plow-road.lisp", 4) :: // SOL 89
        ("problems/p-0024-plow-road.lisp", 4) :: // SOL 6
        ("problems/p-0025-clear-road-wreck.lisp", 4) :: // SOL 125
        //("problems/p-0026-clear-road-tree.lisp",4) ::  // BUG
        ("problems/p-0027-plow-road.lisp", 4) :: // SOL 50
        ("problems/p-0028-set-up-shelter.lisp", 4) :: // SOL 18268
        //("problems/p-0029-clear-road-tree.lisp",4) ::    // BUG
        //("problems/p-0030-provide-temp-heat.lisp", 4) :: // TIMEOUT   (also on frodo)
        //("problems/p-0030-provide-temp-heat.lisp", 4) :: // TIMEOUT
        //("problems/p-0031-provide-temp-heat.lisp", 4) :: // TIMEOUT
        ("problems/p-0032-plow-road.lisp", 4) :: // SOL 80
        ("problems/p-0033-provide-medical-attention.lisp", 4) :: // SOL 146
        ("problems/p-0034-provide-medical-attention.lisp", 4) :: // SOL 10
        ("problems/p-0035-fix-power-line.lisp", 4) :: // SOL 28
        ("problems/p-0036-clear-road-wreck.lisp", 4) :: // SOL 201
        ("problems/p-0037-clear-road-hazard.lisp", 6) :: // SOL 508
        ("problems/p-0038-plow-road.lisp", 4) :: // SOL 89
        ("problems/p-0039-plow-road.lisp", 4) :: // SOL 73
        ("problems/p-0040-provide-medical-attention.lisp", 4) :: // SOL 10
        ("problems/p-0041-clear-road-wreck.lisp", 6) :: // SOL 10
        ("problems/p-0042-clear-road-wreck.lisp", 4) :: // SOL 10
        ("problems/p-0043-set-up-shelter.lisp", 6) :: // SOL 10
        ("problems/p-0044-plow-road.lisp", 6) :: // SOL 457
        ("problems/p-0045-plow-road.lisp", 4) :: // SOL 468
        ("problems/p-0046-clear-road-wreck.lisp", 6) :: // SOL 8098
        //("problems/p-0047-provide-temp-heat.lisp", 4) :: // TIMEOUT
        //("problems/p-0048-provide-temp-heat.lisp", 4) :: // TIMEOUT
        ("problems/p-0049-plow-road.lisp", 4) :: // SOL 97
        ("problems/p-0050-clear-road-hazard.lisp", 6) :: // SOL 271
        //("problems/p-0051-plow-road.lisp", 4) :: // ??
        //("problems/p-0052-provide-temp-heat.lisp", 4) :: // SOL 10
        ("problems/p-0053-provide-medical-attention.lisp", 4) :: // SOL 185
        ("problems/p-0054-clear-road-hazard.lisp", 6) :: // SOL 148
        ("problems/p-0055-fix-power-line.lisp", 4) :: // SOL 30
        ("problems/p-0056-provide-medical-attention.lisp", 4) :: // SOL 101
        ("problems/p-0057-clear-road-wreck.lisp", 4) :: // SOL 47
        //("problems/p-0058-fix-water-main.lisp",4) ::   // BUG
        ("problems/p-0059-clear-road-hazard.lisp", 6) :: // SOL 608
        ("problems/p-0060-clear-road-wreck.lisp", 4) :: // SOL 135
        ("problems/p-0061-plow-road.lisp", 4) :: // SOL 50
        ("problems/p-0062-clear-road-hazard.lisp", 6) :: // SOL 50
        Nil
      ) ::
      Nil

  //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
  //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
  //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"


  val timeLimit: Long = 30 * 60 * 1000
  val minOffset: Int  = 0
  val maxOffset: Int  = 0
  val minPlan  : Int  = 10
  val maxPlan  : Int  = 200
  val stepPlan : Int  = 10

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
    allProblemProperties foreach { d => builder append (d + ",") }
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
    allProblemProperties foreach { d => builder append (informationCapsule.integralDataMap().getOrElse(d, Integer.MAX_VALUE) + ",") }
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

      val runner = VerifyRunner(prefix + domainFile, prefix + problemFile, config, parserType, CRYPTOMINISAT)

      /*val solutionLines = Range(minOffset, maxOffset + 1) map { offsetToK =>
        val (isPlan, completed, time, information) = runner.runWithTimeLimit(timeLimit, runner.solutionPlan, offsetToK)

        println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
        println("Preprocess " + runner.preprocessTime)
        println(time.longInfo)
        println(information.longInfo)

        if (offsetToK == 0)
          println("MAXOFFSET result: " + isPlan)

        writeSingleRun(time, information, runner.preprocessTime, isSolution = true, satResult = isPlan, completed = completed, domainFile, problemFile)
      }*/

      /*val nonSolutionLines = Range(0, numberOfRandomSeeds) flatMap { randomSeed => Range(minOffset, maxOffset + 1) map { offsetToK =>
        val randomPlanGenerator = RandomPlanGenerator(runner.domain, runner.initialPlan)
        val randomPlan = randomPlanGenerator.randomExecutablePlan(runner.solutionPlan.length, randomSeed)

        val (isPlan, completed, time, information) = runner.runWithTimeLimit(timeLimit, randomPlan, offsetToK, includeGoal = false)

        println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
        println("Preprocess " + runner.preprocessTime)
        println(time.longInfo)
        println(information.longInfo)

        writeSingleRun(time, information, runner.preprocessTime, isSolution = false, satResult = isPlan, completed = completed, domainFile, problemFile)
      }
      }*/


      val almostSolutionLines = Range(0, numberOfRandomSeeds) flatMap { randomSeed =>
        val solPlan = runner.solutionPlan
        val r = new Random(randomSeed)
        val newAction = runner.domain.primitiveTasks(r.nextInt(runner.domain.primitiveTasks.length))
        val newPos = r.nextInt(solPlan.length)

        val planToRefute = solPlan.updated(newPos, newAction)

        Range(minOffset, maxOffset + 1) map { offsetToK =>


          val (isPlan, completed, time, information) = runner.runWithTimeLimit(timeLimit, planToRefute, offsetToK, includeGoal = false)

          println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
          println("Preprocess " + runner.preprocessTime)
          println(time.longInfo)
          println(information.longInfo)

          writeSingleRun(time, information, runner.preprocessTime, isSolution = false, satResult = isPlan, completed = completed, domainFile, problemFile)
        }
      }
      //nonSolutionLines ++ solutionLines
      almostSolutionLines
    }
    } mkString "\n"


    writeStringToFile(writeHead + "\n" + result + "\n", "result.csv")
  }

  def runPlannerEvaluation(): Unit = {
    val resultStream = new PrintStream(new FileOutputStream("sat_planning.csv"))

    resultStream.println(writeHead())
    resultStream.flush()

    val result = problemsToVerify foreach { case (domainFile, parserType, problems) => problems foreach { case (problemFile, config) =>
      Range(minPlan, maxPlan + 1, stepPlan) foreach { planLength =>
        println("RUN " + domainFile + " " + problemFile + "PLANLEN " + planLength)
        val runner = VerifyRunner(prefix + domainFile, prefix + problemFile, -planLength, parserType, CRYPTOMINISAT)

        Range(minOffset, maxOffset + 1) foreach { offsetToK =>
          val (isPlan, completed, time, information) = runner.runWithTimeLimit(timeLimit, runner.solutionPlan, 0, includeGoal = true, verify = false)

          println("PANDA says: " + (if (isPlan) "it is a solution" else "it is not a solution"))
          println("Preprocess " + runner.preprocessTime)
          println(time.longInfo)
          println(information.longInfo)

          if (offsetToK == 0) println("MAXOFFSET result: " + isPlan)

          resultStream.println(writeSingleRun(time, information, runner.preprocessTime, isSolution = true, satResult = isPlan, completed = completed, domainFile, problemFile))
          resultStream.flush()
        }
      }
    }
    }

    //val runner = VerifyRunner(domFile, probFile, -length, HPDDLParserType, CRYPTOMINISAT())
    //val runner = VerifyRunner(domFile, probFile, -length, HDDLParserType, CRYPTOMINISAT())
    //val runner = VerifyRunner(domFile, probFile, -length, XMLParserType, CRYPTOMINISAT())

    //val (_, time, info) = runner.run(runner.solutionPlan, offSetToK = offset, includeGoal = true, verify = false)
    //val (_, time, info) = runner.run(runner.solutionPlan, offSetToK = 0, includeGoal = true, verify = false)

    //println(time.longInfo)
    //println(info.longInfo)
  }

  def runPlanner(domFile: String, probFile: String, length: Int, offset: Int): Unit = {
    //val runner = VerifyRunner(domFile, probFile, -length, HPDDLParserType, CRYPTOMINISAT)
    //val runner = VerifyRunner(domFile, probFile, -length, HDDLParserType, CRYPTOMINISAT)
    val runner = VerifyRunner(domFile, probFile, -length, XMLParserType, CRYPTOMINISAT)

    val (_, time, info) = runner.run(runner.solutionPlan, offSetToK = offset, includeGoal = true, verify = false)
    //val (_, time, info) = runner.run(runner.solutionPlan, offSetToK = 0, includeGoal = true, verify = false)

    println(time.longInfo)
    println(info.longInfo)
  }

  def main(args: Array[String]) {

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/domains/woodworking-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p01-hierarchical-socs.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Woodworking-Socs/problems/p02-variant1-hierarchical.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/domains/UMTranslog.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/UM-Translog/problems/UMTranslog-P-1-RefrigeratedTankerTraincarHub.xml"

    //val domFile = "/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/towers/domain/domain.hpddl"
    val domFile = "/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/blocksworld/domain/domain.hpddl"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VeryVerySmall.xml"
    //val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/parser/xml/OrganizeMeeting_VerySmall.xml"
    //val probFile = "/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/towers/problems/pfile_06.pddl"
    val probFile = "/home/gregor/Workspace/Panda3/panda3core/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/blocksworld/problems/pfile_015.pddl"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/OrganizeMeeting_Large.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/SmartPhone/problems/ThesisExampleProblem.xml"

    //val domFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/domains/satellite2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/sat-C.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-2obs-2sat-2mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/satellite2-P-abstract-3obs-3sat-3mod.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/5--5--5.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/6--2--2.xml"
    //val probFile = "/home/gregor/Workspace/panda2-system/domains/XML/Satellite/problems/8--3--4.xml"

    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/domain-htn.lisp"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hpddl/htn-strips-pairs/IPC7-Transport/p01-htn.lisp"
    //val domFile = "IPC7-Transport/domain-htn.lisp"
    //val probFile = "IPC7-Transport/p01-htn.lisp"

    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/blocksworld/domain/domain.hpddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/blocksworld/problems/pfile_020.pddl"

    //val domFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/towers/domain/domain.hpddl"
    //val probFile = "../panda3core_with_planning_graph/src/test/resources/de/uniulm/ki/panda3/symbolic/parser/hddl/towers/problems/pfile_03.pddl"

    //val domFile = args(0)
    //val probFile = args(1)
    //val len = args(2).toInt
    //val offset = args(3).toInt

    //val domFile = "domain-block.hpddl-2"
    //val probFile = "pfile_020.pddl"
    //val probFile = "pfile_005.pddl-2"

    // 3 -> 12

    //runPlanner(domFile, probFile, len, offset)
    runPlanner(domFile, probFile, 40, 0)
    //runEvaluation()
    //runPlannerEvaluation()
  }

}

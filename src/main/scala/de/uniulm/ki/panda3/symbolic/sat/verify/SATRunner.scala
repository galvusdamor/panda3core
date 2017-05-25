package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{File, FileWriter, BufferedWriter}
import java.util.UUID
import java.util.concurrent.Semaphore

import de.uniulm.ki.panda3.configuration.Timings._
import de.uniulm.ki.panda3.symbolic.domain.ReducedTask
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.util._

import scala.collection.{JavaConversions, Seq}
import scala.io.Source

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off method.length cyclomatic.complexity
case class SATRunner(domain: Domain, initialPlan: Plan, satSolver: Solvertype, reductionMethod: SATReductionMethod, timeCapsule: TimeCapsule, informationCapsule: InformationCapsule) {

  private val fileDir = "/dev/shm/"


  import sys.process._

  private var satProcess: Process = null

  private var expansionPossible = true


  def runWithTimeLimit(timelimit: Long, timeLimitForLastRun: Long,
                       planLength: Int, offsetToK: Int, includeGoal: Boolean = true, defineK: Option[Int] = None, checkSolution: Boolean = false): (Option[Seq[Task]], Boolean, Boolean) = {

    val timerSemaphore = new Semaphore(0)

    val runner = new Runnable {
      var result   : Option[Option[Seq[Task]]] = None

      override def run(): Unit = {
        timeCapsule switchTimerToCurrentThread Timings.TOTAL_TIME
        result = Some(SATRunner.this.run(planLength: Int, offsetToK, includeGoal, defineK, checkSolution))
        timerSemaphore.acquire()
      }
    }
    // start thread
    val threadGroup = new ThreadGroup("sat group")
    val thread = new Thread(threadGroup, runner)
    thread.start()

    // wait
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime <= (if (expansionPossible) timelimit else timeLimitForLastRun) && runner.result.isEmpty && thread.isAlive) Thread.sleep(100)

    if (satProcess != null) {
      println("Kill SAT solver")
      satProcess.destroy()
      "killall -9 cryptominisat5" !
    }

    timeCapsule switchTimerToCurrentThread(Timings.TOTAL_TIME, Some(if (expansionPossible) timelimit else timeLimitForLastRun))
    timerSemaphore.release()

    JavaConversions.mapAsScalaMap(Thread.getAllStackTraces).keys filter { t => thread.getThreadGroup == t.getThreadGroup } foreach { t => t.stop() }



    if (runner.result.isEmpty) {
      val errorState = System.currentTimeMillis() - startTime > (if (expansionPossible) timelimit else timeLimitForLastRun)
      if (errorState) Thread.sleep(500)
      (None, errorState, expansionPossible)
    } else (runner.result.get, false, expansionPossible)
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

  def run(planLength: Int, offSetToK: Int, includeGoal: Boolean = true, defineK: Option[Int] = None, checkSolution: Boolean = false): Option[Seq[Task]] =
    try {
      informationCapsule.set(Information.PLAN_LENGTH, planLength)
      informationCapsule.set(Information.NUMBER_OF_CONSTANTS, domain.constants.length)
      informationCapsule.set(Information.NUMBER_OF_PREDICATES, domain.predicates.length)
      informationCapsule.set(Information.NUMBER_OF_ACTIONS, domain.tasks.length)
      informationCapsule.set(Information.NUMBER_OF_ABSTRACT_ACTIONS, domain.abstractTasks.length)
      informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_ACTIONS, domain.primitiveTasks.length)
      informationCapsule.set(Information.NUMBER_OF_METHODS, domain.decompositionMethods.length)

      // start verification
      val encoder = //TreeEncoding(domain, initialPlan, sequenceToVerify.length, offSetToK)
        if (domain.isTotallyOrdered && initialPlan.orderingConstraints.isTotalOrder())
          TotallyOrderedEncoding(timeCapsule, domain, initialPlan, reductionMethod, planLength, offSetToK, defineK)
        //else GeneralEncoding(domain, initialPlan, Range(0,planLength) map {_ => null.asInstanceOf[Task]}, offSetToK, defineK).asInstanceOf[VerifyEncoding]
        else SOGPOCLEncoding(timeCapsule, domain, initialPlan, planLength, reductionMethod, offSetToK, defineK).asInstanceOf[VerifyEncoding]
      //else SOGClassicalEncoding(domain, initialPlan, planLength, offSetToK, defineK).asInstanceOf[VerifyEncoding]

      // (3)
      /*println("K " + encoder.K)
      informationCapsule.set(Information.ICAPS_K, VerifyEncoding.computeICAPSK(domain, initialPlan, planLength))
      informationCapsule.set(Information.TSTG_K, VerifyEncoding.computeTSTGK(domain, initialPlan, planLength))
      informationCapsule.set(Information.DP_K, VerifyEncoding.computeTDG(domain, initialPlan, planLength, Math.max, 0))
      informationCapsule.set(Information.LOG_K, VerifyEncoding.computeMethodSize(domain, initialPlan, planLength))*/
      informationCapsule.set(Information.OFFSET_K, offSetToK)
      informationCapsule.set(Information.ACTUAL_K, encoder.K)

      //println(informationCapsule.longInfo)

      timeCapsule start Timings.VERIFY_TOTAL
      timeCapsule start Timings.GENERATE_FORMULA
      //println("READY")
      //System.in.read()
      val stateFormula = encoder.stateTransitionFormula ++ encoder.initialState ++ (if (includeGoal) encoder.goalState else Nil) ++ encoder.noAbstractsFormula
      val usedFormula = (encoder.decompositionFormula ++ stateFormula).toArray
      println("NUMBER OF CLAUSES " + usedFormula.length)
      println("NUMBER OF STATE CLAUSES " + stateFormula.length)
      println("NUMBER OF DECOMPOSITION CLAUSES " + encoder.decompositionFormula.length)

      expansionPossible = encoder.expansionPossible
      //println("Done")
      //System.in.read()
      timeCapsule stop Timings.GENERATE_FORMULA

      //writeStringToFile(usedFormula map { c => c.disjuncts map { case (a, p) => (if (!p) "not " else "") + a } mkString "\t" } mkString "\n", "formula.txt")

      timeCapsule start Timings.TRANSFORM_DIMACS
      println("READY TO WRITE")
      val uniqFileIdentifier = UUID.randomUUID().toString
      println("UUID " + uniqFileIdentifier)
      val writer = new BufferedWriter(new FileWriter(new File(fileDir + "__cnfString" + uniqFileIdentifier)))
      val atomMap: Map[String, Int] = encoder.miniSATString(usedFormula, writer)
      println("FLUSH")
      writer.flush()
      writer.close()
      println("CLOSE")
      timeCapsule stop Timings.TRANSFORM_DIMACS

      val tritivallUnsatisfiable = encoder match {
        case pathbased: PathBasedEncoding[_, _] =>
          //println(tot.primitivePaths map { case (a, b) => (a, b map { _.name }) } mkString "\n")
          informationCapsule.set(Information.NUMBER_OF_PATHS, pathbased.primitivePaths.length)
          println("NUMBER OF PATHS " + pathbased.primitivePaths.length)

          pathbased.primitivePaths.length == 0
        case _                                  => false
      }

      encoder match {
        case tot: TotallyOrderedEncoding => informationCapsule.set(Information.MAX_PLAN_LENGTH, tot.primitivePaths.length)
        case tree: TreeEncoding          => informationCapsule.set(Information.MAX_PLAN_LENGTH, tree.taskSequenceLength)
        case _                           =>
      }

      //println(timeCapsule.integralDataMap())


      // if we can't reach a primitive decomposition the whole PDT will be pruned, resulting in a trivially satisfiable SAT formula,
      // but the planning problem is clearly unsatisfiable
      if (tritivallUnsatisfiable) {
        println("Problem is trivially unsatisfiable ... exiting")
        timeCapsule stop Timings.VERIFY_TOTAL
        None
      } else {
        //System exit 0

        //timeCapsule start VerifyRunner.WRITE_FORMULA
        //writeStringToFile(cnfString, new File("__cnfString"))
        //timeCapsule stop VerifyRunner.WRITE_FORMULA

        //writeStringToFile(usedFormula map {_.disjuncts mkString "\t"} mkString "\n", new File("__formulaString"))

        try {
          val stdout = new StringBuilder
          val stderr = new StringBuilder
          val logger = ProcessLogger({ s => stdout append (s + "\n") }, { s => stderr append (s + "\n") })

          satSolver match {
            case MINISAT       =>
              println("Starting minisat")
              writeStringToFile("#!/bin/bash\n/usr/bin/time -f '%U %S' minisat " + fileDir + "__cnfString" + uniqFileIdentifier + " " + fileDir + "__res" + uniqFileIdentifier + ".txt",
                                fileDir + "__run" + uniqFileIdentifier)
            case CRYPTOMINISAT =>
              println("Starting cryptominisat5")
              writeStringToFile("#!/bin/bash\n/usr/bin/time -f '%U %S' cryptominisat5 --verb 0 " + fileDir + "__cnfString" + uniqFileIdentifier, fileDir + "__run" + uniqFileIdentifier)

            case RISS6 =>
              println("Starting riss6")
              // -config=Riss6:-no-enabled_cp3
              writeStringToFile("#!/bin/bash\n/usr/bin/time -f '%U %S' c/home/gregor/Riss6/bin/riss6 -verb=0 " + fileDir + "__cnfString" + uniqFileIdentifier,
                                fileDir + "__run" + uniqFileIdentifier)
          }

          satProcess = ("bash " + fileDir + "__run" + uniqFileIdentifier).run(logger)

          // wait for termination
          satProcess.exitValue()
          satSolver match {
            case CRYPTOMINISAT | RISS6 =>
              writeStringToFile(stdout.toString(), new File(fileDir + "__res" + uniqFileIdentifier + ".txt"))
            case _                     =>
          }
          // remove runscript
          ("rm " + fileDir + "__run" + uniqFileIdentifier) !

          // get time measurement
          val totalTime = (stderr.split('\n')(1).split(' ') map { _.toDouble * 1000 } sum).toInt
          println("Time command gave the following runtime for the solver: " + totalTime)

          timeCapsule.addTo(SAT_SOLVER, totalTime)
          timeCapsule.addTo(TOTAL_TIME, totalTime)
          timeCapsule.addTo(VERIFY_TOTAL, totalTime)

        } catch {
          case rt: RuntimeException => println("Minisat exitcode problem ..." + rt.toString)
        }
        timeCapsule stop Timings.VERIFY_TOTAL

        print("Logging statistical information about the run ... ")
        val formulaVariables: Seq[String] = atomMap.keys.toSeq
        informationCapsule.set(Information.NUMBER_OF_VARIABLES, formulaVariables.size)
        informationCapsule.set(Information.NUMBER_OF_CLAUSES, usedFormula.length)
        informationCapsule.set(Information.STATE_FORMULA, stateFormula.length)
        informationCapsule.set(Information.ORDER_CLAUSES, encoder.decompositionFormula count { _.disjuncts forall { case (a, _) => a.startsWith("before") || a.startsWith("childof") } })
        informationCapsule.set(Information.METHOD_CHILDREN_CLAUSES, encoder.numberOfChildrenClauses)
        println("done")

        // postprocessing
        print("Reading solver output ... ")
        val t1 = System.currentTimeMillis()
        val solverSource = Source.fromFile(fileDir + "__res" + uniqFileIdentifier + ".txt")
        val t2 = System.currentTimeMillis()
        val solverOutput = solverSource.mkString
        val t3 = System.currentTimeMillis()
        println("done")
        //println("done  " + (t2 - t1) + " " + (t3 - t2))
        print("Preparing solver output ... ")
        val t4 = System.currentTimeMillis()
        val (solveState, literals) = satSolver match {
          case MINISAT               =>
            val splitted = solverOutput.split("\n")
            if (splitted.length == 1) (splitted(0), Set[Int]()) else (splitted(0), (splitted(1).split(" ") filter { _ != "" } map { _.toInt } filter { _ != 0 }).toSet)
          case CRYPTOMINISAT | RISS6 =>
            val stateSplit = solverOutput.split("\n", 2)
            val cleanState = stateSplit.head.replaceAll("s ", "")

            if (stateSplit.length == 1) (cleanState, Set[Int]())
            else {
              val lits = stateSplit(1).split(" ").collect({ case s if s != "" && s != "\nv" && s != "v" && s != "0" && s != "0\n" => s.toInt }).toSet

              (cleanState, lits)
            }
        }
        val t5 = System.currentTimeMillis()
        println("done")
        //println("done " + (t5 - t4))

        // delete files
        //("rm " + fileDir + "__cnfString" + uniqFileIdentifier + " " + fileDir + "__res" + uniqFileIdentifier + ".txt") !

        // report on the result
        println("SAT-Solver says: " + solveState)
        val solved = solveState == "SAT" || solveState == "SATISFIABLE"


        // postprocessing
        if (solved) {
          println("")
          val allTrueAtoms: Set[String] = (atomMap filter { case (atom, index) => literals contains (index + 1) }).keys.toSet
          //writeStringToFile(allTrueAtoms mkString "\n", new File("true.txt"))

          println("extracting solution")
          val (graphNodes, graphEdges, solutionSequence) = extractSolutionAndDecompositionGraph(encoder, atomMap, literals, formulaVariables, allTrueAtoms)

          if (checkSolution) runSolutionIntegrityCheck(encoder, graphNodes, graphEdges)

          // return the found solution
          Some(solutionSequence)
        } else None
      }
    } catch {
      case t: Throwable =>
        t.printStackTrace()
        None
    }


  private def extractSolutionAndDecompositionGraph(encoder: VerifyEncoding, atomMap: Map[String, Int], literals: Set[Int], formulaVariables: Seq[String],
                                                   allTrueAtoms: Set[String]): (Seq[String], Seq[(String, String)], Seq[Task]) = encoder match {
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

      (nodes, edges, ???)

    case pbe: PathBasedEncoding[_, _] =>
      val nodes = formulaVariables filter { _.startsWith("action!") } filter allTrueAtoms.contains

      val edges = nodes flatMap { parent => nodes flatMap { child =>
        val parentLayer = parent.split("!").last.split("_").head.toInt
        val childLayer = child.split("!").last.split("_").head.toInt

        if (parentLayer + 1 != childLayer) Nil
        else {
          val parentPathString = parent.split("_").last.split(",").head
          val childPathString = child.split("_").last.split(",").head

          val parentPath = parentPathString.split(";").filter(_.nonEmpty) map { _.toInt }
          val childPath = childPathString.split(";").filter(_.nonEmpty) map { _.toInt }

          assert(parentPath.length + 1 == childPath.length)

          if (parentPath sameElements childPath.take(parentPath.length)) (parent, child) :: Nil else Nil
        }
      }
      }
      val primitiveSolution: Seq[Task] = encoder match {
        case tot: TotallyOrderedEncoding =>
          // check executability of the plan

          val graph = SimpleDirectedGraph(nodes, edges)

          Dot2PdfCompiler.writeDotToFile(graph, "graph.pdf")

          /*graph.vertices foreach { t => val actionIDX = t.split(",").last.toInt;
            println("task " + t + " " + actionIDX + " " + domain.tasks(actionIDX).name)
            domain.tasks(actionIDX) }*/

          graph.sinks sortWith { case (t1, t2) =>
            val path1 = t1.split("_").last.split(",").head.split(";") map { _.toInt }
            val path2 = t2.split("_").last.split(",").head.split(";") map { _.toInt }
            PathBasedEncoding.pathSortingFunction(path1, path2)
          } map { t => val actionIDX = t.split(",").last.toInt; domain.tasks(actionIDX) }

        case tree: TotallyOrderedEncoding =>
          val primitiveActions = allTrueAtoms filter { _.startsWith("action^") }
          //println("Primitive Actions: \n" + (primitiveActions mkString "\n"))
          val actionsPerPosition = primitiveActions groupBy { _.split("_")(1).split(",")(0).toInt }
          val actionSequence = actionsPerPosition.keySet.toSeq.sorted map { pos => assert(actionsPerPosition(pos).size == 1); actionsPerPosition(pos).head }
          val taskSequence = actionSequence map { t => val actionIDX = t.split(",").last.toInt; domain.tasks(actionIDX) }

          taskSequence

        case sogTree: SOGEncoding =>

          def actionStringToInfoString(t: String): String = {
            val actionIDX = t.split(",").last.toInt
            domain.tasks(actionIDX).name + " " + domain.tasks(actionIDX).isPrimitive + " " + t
          }

          val graph = SimpleDirectedGraph(nodes, edges)
          //println(graph.sinks filterNot { t => t.contains("-1") || t.contains("-2") } map actionStringToInfoString mkString "\n")

          sogTree match {

            case tree: SOGPOCLEncoding      =>
              // extract partial order from formula
              val orderClauses = allTrueAtoms filter { _ startsWith "before" } map { _.split("_").tail } map { x => (x(0), x(1)) } toSeq

              val pathsToSinks: Map[String, (Task, String)] = graph.sinks map { x =>
                val actionID = x.split(",").last.toInt
                val action = if (actionID == domain.tasks.length) ReducedTask("init", true, Nil, Nil, Nil, And(Nil), And(Nil))
                else if (actionID == domain.tasks.length + 1) ReducedTask("goal", true, Nil, Nil, Nil, And(Nil), And(Nil))
                else domain.tasks(actionID)
                val pathID = x.split("_")(1).split(",").head

                pathID ->(action, pathID)
              } toMap

              val v: Seq[(Task, String)] = pathsToSinks.values.toSeq.distinct
              val e: Seq[((Task, String), (Task, String))] = orderClauses collect { case (before, after) if pathsToSinks.contains(before) && pathsToSinks.contains(after) =>
                (pathsToSinks(before), pathsToSinks(after))
              }

              val partiallyOrderedSolution = SimpleDirectedGraph(v, e).transitiveReduction

              val graphString = partiallyOrderedSolution.dotString(options = DirectedGraphDotOptions(), nodeRenderer = {case (task, _) => task.name})
              Dot2PdfCompiler.writeDotToFile(graphString, "solutionOrder.pdf")


              // take a topological ordering (any should to it ...) and remove init and goal
              val withGoal = partiallyOrderedSolution.topologicalOrdering.get.tail
              withGoal.take(withGoal.length - 1) map { _._1 }
            case tree: SOGClassicalEncoding =>
              val primitiveActions = allTrueAtoms filter { _.startsWith("action^") }
              //println("Primitive Actions: \n" + (primitiveActions mkString "\n"))
              val actionsPerPosition = primitiveActions groupBy { _.split("_")(1).split(",")(0).toInt }
              val actionSequence = actionsPerPosition.keySet.toSeq.sorted map { pos => assert(actionsPerPosition(pos).size == 1); actionsPerPosition(pos).head }
              val taskSequence = actionSequence map { t => val actionIDX = t.split(",").last.toInt; domain.tasks(actionIDX) }


              //println("Primitive Sequence with paths")
              //println(actionSequence map actionStringToInfoString mkString "\n")

              val innerActions = allTrueAtoms filter { _.startsWith("action!") } filterNot { t => t.contains("-1") || t.contains("-2") }
              //println("Inner actions with paths")
              //println(innerActions map actionStringToInfoString mkString "\n")


              val pathToPos = allTrueAtoms filter { _.startsWith("pathToPos_") }
              //println(pathToPos mkString "\n")
              val active = allTrueAtoms filter { _.startsWith("active") }
              //println(active mkString "\n")
              assert(pathToPos.size == taskSequence.length)
              assert(active.size == taskSequence.length, "ACTIVE " + active.size + " vs " + taskSequence.length)

              //assert(graph.sinks.length == taskSequence.length, "SINKS " + graph.sinks.length + " vs " + taskSequence.length)
              val nextPredicates = allTrueAtoms filter { _.startsWith("next") }
              //println(nextPredicates mkString "\n")
              //assert(nextPredicates.size == taskSequence.length + 1, "NEXT " + nextPredicates.size + " vs " + (taskSequence.length + 1))

              val nextRel: Seq[(Array[Int], Array[Int])] =
                nextPredicates map { n => n.split("_").drop(1) } map { case l => (l.head, l(1)) } map { case (a, b) => (a.split(";") map { _.toInt }, b.split(";") map {
                  _.toInt
                })
                } toSeq

              //println(nextRel map { case (a, b) => (a mkString ",") + ", " + (b mkString ",") } mkString "\n")
              //nextRel foreach { case (a, b) => assert(!(a sameElements b), "IDENTICAL " + (a mkString ",") + ", " + (b mkString ",")) }

              val lastPath = nextRel.indices.foldLeft((Array(-1), nextRel))(
                { case ((current, pairs), _) =>
                  val (nextNext, remaining) = pairs partition { _._1 sameElements current }
                  assert(nextNext.length == 1)

                  (nextNext.head._2, remaining)
                })

              //assert(lastPath._1 sameElements Integer.MAX_VALUE :: Nil)

              taskSequence
          }
      }

      print("\n\nCHECKING primitive solution of length " + primitiveSolution.length + " ...")
      //println("\n")
      //println(primitiveSolution map { _.name } mkString "\n")

      primitiveSolution foreach { t => assert(t.isPrimitive) }
      checkIfTaskSequenceIsAValidPlan(primitiveSolution, checkGoal = true)
      println(" done.")

      (nodes, edges, primitiveSolution)
  }


  private def runSolutionIntegrityCheck(encoder: VerifyEncoding, graphNodes: Seq[String], graphEdges: Seq[(String, String)]): Unit = {
    def changeSATNameToActionName(satName: String): SimpleGraphNode = {
      val actionID = satName.split(",").last.toInt
      if (actionID == domain.tasks.length) SimpleGraphNode(satName, "init")
      else if (actionID == domain.tasks.length + 1) SimpleGraphNode(satName, "init")
      else if (actionID >= 0) SimpleGraphNode(satName, (if (domain.tasks(actionID).isPrimitive) "!" else "") + domain.tasks(actionID).name) else SimpleGraphNode(satName, satName)
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
      if (!v.id.contains("-1") && !v.id.contains("-2"))
        assert(indeg + outdeg != 0, "unconnected action " + v)
    }

    if (encoder.isInstanceOf[PathBasedEncoding[_, _]]) {
      // check integrity of the methods
      decompGraphNames.vertices filter { v => decompGraphNames.degrees(v)._2 != 0 } foreach { v =>
        // either it is primitive
        val nei = decompGraphNames.edges(v)
        val myAction = domain.tasks(v.id.split(",").last.toInt)
        if (myAction.isPrimitive) {
          assert(nei.size == 1)
          assert(nei.head.name == v.name)
        } else {
          val subTasks: Seq[Task] = nei map { n => n.id.split(",").last.toInt } collect {case i if domain.tasks.length > i => domain.tasks(i)}
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
  }

}

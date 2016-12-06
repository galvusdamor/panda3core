package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{File, FileWriter, BufferedWriter}
import java.util.UUID

import de.uniulm.ki.panda3.configuration.{Timings, ResultMap, Information}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.io.Source

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off method.length cyclomatic.complexity
case class SATRunner(domain: Domain, initialPlan: Plan, satSolver: Solvertype, timeCapsule: TimeCapsule, informationCapsule: InformationCapsule) {

  private val fileDir = "/dev/shm/"


  import sys.process._

  def runWithTimeLimit(timelimit: Option[Long], planLength: Int, offsetToK: Int, includeGoal: Boolean = true, defineK: Option[Int] = None, checkSolution: Boolean = false):
  (Boolean, Boolean) = {
    val runner = new Runnable {
      var result: Option[Boolean] = None

      override def run(): Unit = {
        result = Some(SATRunner.this.run(planLength: Int, offsetToK, includeGoal, defineK, checkSolution))
      }
    }
    // start thread
    val thread = new Thread(runner)
    thread.start()

    // wait
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime <= timelimit.getOrElse(Long.MaxValue) && runner.result.isEmpty && thread.isAlive) Thread.sleep(1000)
    thread.stop()

    if (runner.result.isEmpty) (false, false) else (runner.result.get, true)
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

  def run(planLength: Int, offSetToK: Int, includeGoal: Boolean = true, defineK: Option[Int] = None, checkSolution: Boolean = false): (Boolean) = try {

    informationCapsule.set(Information.PLAN_LENGTH, planLength)
    informationCapsule.set(Information.NUMBER_OF_CONSTANTS, domain.constants.length)
    informationCapsule.set(Information.NUMBER_OF_PREDICATES, domain.predicates.length)
    informationCapsule.set(Information.NUMBER_OF_ACTIONS, domain.tasks.length)
    informationCapsule.set(Information.NUMBER_OF_ABSTRACT_ACTIONS, domain.abstractTasks.length)
    informationCapsule.set(Information.NUMBER_OF_PRIMITIVE_ACTIONS, domain.primitiveTasks.length)
    informationCapsule.set(Information.NUMBER_OF_METHODS, domain.decompositionMethods.length)

    // start verification
    val encoder = //TreeEncoding(domain, initialPlan, sequenceToVerify.length, offSetToK)
      if (domain.isTotallyOrdered && initialPlan.orderingConstraints.isTotalOrder()) TotallyOrderedEncoding(domain, initialPlan, planLength, offSetToK, defineK)
      else TreeEncoding(domain, initialPlan, planLength, offSetToK, defineK)

    // (3)
    println("K " + encoder.K)
    informationCapsule.set(Information.ICAPS_K, VerifyEncoding.computeICAPSK(domain, initialPlan, planLength))
    informationCapsule.set(Information.TSTG_K, VerifyEncoding.computeTSTGK(domain, initialPlan, planLength))
    informationCapsule.set(Information.DP_K, VerifyEncoding.computeTDG(domain, initialPlan, planLength, Math.max, 0))
    informationCapsule.set(Information.LOG_K, VerifyEncoding.computeMethodSize(domain, initialPlan, planLength))
    informationCapsule.set(Information.OFFSET_K, offSetToK)
    informationCapsule.set(Information.ACTUAL_K, encoder.K)
    println(informationCapsule.longInfo)

    timeCapsule start Timings.VERIFY_TOTAL
    timeCapsule start Timings.GENERATE_FORMULA
    //println("READY")
    //System.in.read()
    val stateFormula = encoder.stateTransitionFormula ++ encoder.initialState ++ (if (includeGoal) encoder.goalState else Nil) ++ encoder.noAbstractsFormula
    val usedFormula = encoder.decompositionFormula ++ stateFormula
    //println("Done")
    //System.in.read()
    timeCapsule stop Timings.GENERATE_FORMULA

    timeCapsule start Timings.TRANSFORM_DIMACS
    println("READY TO WRITE")
    val uniqFileIdentifier = UUID.randomUUID().toString
    println("UUID " + uniqFileIdentifier)
    val writer = new BufferedWriter(new FileWriter(new File(fileDir + "__cnfString" + uniqFileIdentifier)))
    val atomMap = encoder.miniSATString(usedFormula, writer)
    println("FLUSH")
    writer.flush()
    writer.close()
    println("CLOSE")
    timeCapsule stop Timings.TRANSFORM_DIMACS

    encoder match {
      case pathbased: PathBasedEncoding =>
        //println(tot.primitivePaths map { case (a, b) => (a, b map { _.name }) } mkString "\n")
        informationCapsule.set(Information.NUMBER_OF_PATHS, pathbased.primitivePaths.length)
        println("NUMBER OF PATHS " + pathbased.primitivePaths.length)
      case _                            =>
    }

    encoder match {
      case tot: TotallyOrderedEncoding => informationCapsule.set(Information.MAX_PLAN_LENGTH, tot.primitivePaths.length)
      case tree: TreeEncoding          => informationCapsule.set(Information.MAX_PLAN_LENGTH, tree.taskSequenceLength)
      case _                           =>
    }

    println(timeCapsule.integralDataMap())

    //System exit 0

    //timeCapsule start VerifyRunner.WRITE_FORMULA
    //writeStringToFile(cnfString, new File("__cnfString"))
    //timeCapsule stop VerifyRunner.WRITE_FORMULA

    //writeStringToFile(usedFormula mkString "\n", new File("__formulaString"))

    timeCapsule start Timings.SAT_SOLVER
    try {
      satSolver match {
        case MINISAT()       =>
          println("Starting minisat")
          ("minisat " + fileDir + "__cnfString" + uniqFileIdentifier + " " + fileDir + "__res" + uniqFileIdentifier + ".txt") !
        case CRYPTOMINISAT() =>
          println("Starting cryptominisat5")
          ("cryptominisat5 --verb 0 " + fileDir + "__cnfString" + uniqFileIdentifier) #> new File(fileDir + "__res" + uniqFileIdentifier + ".txt") !
      }
    } catch {
      case rt: RuntimeException => println("Minisat exitcode problem ...")
    }
    timeCapsule stop Timings.SAT_SOLVER
    timeCapsule stop Timings.VERIFY_TOTAL


    val formulaVariables: Seq[String] = (usedFormula flatMap { _.disjuncts map { _._1 } }).distinct
    informationCapsule.set(Information.NUMBER_OF_VARIABLES, formulaVariables.size)
    informationCapsule.set(Information.NUMBER_OF_CLAUSES, usedFormula.length)
    informationCapsule.set(Information.STATE_FORMULA, stateFormula.length)
    informationCapsule.set(Information.ORDER_CLAUSES, encoder.decompositionFormula count { _.disjuncts forall { case (a, _) => a.startsWith("before") || a.startsWith("childof") } })
    informationCapsule.set(Information.METHOD_CHILDREN_CLAUSES, encoder.numberOfChildrenClauses)

    // postprocessing
    val solverOutput = Source.fromFile(fileDir + "__res" + uniqFileIdentifier + ".txt").mkString
    val (solveState, assignment) = satSolver match {
      case MINISAT()       =>
        val splitted = solverOutput.split("\n")
        if (splitted.length == 1) (splitted(0), "") else (splitted(0), splitted(1))
      case CRYPTOMINISAT() =>
        val cleanString = solverOutput.replaceAll("s ", "").replaceAll("v ", "")
        val splitted = cleanString.split("\n", 2)

        if (splitted.length == 1) (splitted.head, "")
        else (splitted.head, splitted(1).replaceAll("\n", " "))
    }

    // delete files
    ("rm " + fileDir + "__cnfString" + uniqFileIdentifier + " " + fileDir + "__res" + uniqFileIdentifier + ".txt") !

    // report on the result
    println("MiniSAT says: " + solveState)
    val solved = solveState == "SAT" || solveState == "SATISFIABLE"


    // postprocessing
    if (solved && checkSolution) {
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
    }

    solved
  } catch {
    case t: Throwable =>
      t.printStackTrace()
      false
  }
}

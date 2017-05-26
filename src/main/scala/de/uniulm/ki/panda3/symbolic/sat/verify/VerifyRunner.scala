package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io._

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.{DefaultLongInfo, PrettyPrintable}
import de.uniulm.ki.panda3.symbolic.compiler.{AllOrderings, OneRandomOrdering}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, RandomPlanGenerator, Task}
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}
import de.uniulm.ki.util._

import scala.collection.Seq
import scala.io.Source
import scala.util.Random

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// scalastyle:off method.length cyclomatic.complexity
case class VerifyRunner(domain : Domain, initialPlan: Plan, configNumber: Int, parserType: ParserType, satsolver: Solvertype) {

  import sys.process._

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

  val allTime = (VERIFY_TOTAL :: GENERATE_FORMULA :: TRANSFORM_DIMACS :: SAT_SOLVER :: Nil).sorted

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

  val prefix = ""

  val fileDir = "/dev/shm/"

}

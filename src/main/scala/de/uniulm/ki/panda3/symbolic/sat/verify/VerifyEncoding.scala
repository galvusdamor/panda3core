package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{BufferedWriter, OutputStream, File, FileInputStream}

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.compiler.{ExpandSortHierarchy, ClosedWorldAssumption, SHOPMethodCompiler, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.util._

import scala.collection._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * This works only if grounded ....
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off number.of.methods
trait VerifyEncoding {
  def domain: Domain

  def initialPlan: Plan

  def taskSequenceLength: Int

  def offsetToK: Int

  val K: Int = VerifyEncoding.computeTheoreticalK(domain, initialPlan, taskSequenceLength) + offsetToK

  def numberOfChildrenClauses : Int

  domain.tasks foreach { t => assert(t.parameters.isEmpty) }
  domain.predicates foreach { p => assert(p.argumentSorts.isEmpty) }
  //assert(initialPlan.init.substitutedEffects.length == domain.predicates.length)

  val DELTA = domain.maximumMethodSize
  lazy val numberOfLayers = K


  protected val taskIndices          : Map[Task, Int]                = domain.tasks.zipWithIndex.toMap.withDefaultValue(-1)
  protected val predicateIndices     : Map[Predicate, Int]           = domain.predicates.zipWithIndex.toMap
  protected val methodIndices        : Map[DecompositionMethod, Int] = domain.decompositionMethods.zipWithIndex.toMap
  protected val methodPlanStepIndices: Map[Int, Map[PlanStep, Int]]  = (domain.decompositionMethods map { method =>
    (methodIndices(method), method.subPlan.planStepsWithoutInitGoal.zipWithIndex.toMap)
  }).toMap

  protected def methodIndex(method: DecompositionMethod): Int = methodIndices(method)

  def predicateIndex(predicate: Predicate): Int = predicateIndices(predicate)

  def taskIndex(task: Task): Int = taskIndices(task)


  // LOGICALS ABBREVIATIONS
  protected def atLeastOneOf(atoms: Seq[String]): Clause = Clause(atoms map { (_, true) })

  protected var atMostCounter = 0

  protected def atMostOneOf(atoms: Seq[String]): Seq[Clause] = {
    val buffer = new ArrayBuffer[Clause]()
    val numberOfBits: Int = Math.ceil(Math.log(atoms.length) / Math.log(2)).toInt
    val bits = Range(0, numberOfBits) map { b => ("atMost_" + atMostCounter + "_" + b, b) }

    atoms.zipWithIndex foreach { case (atom, index) =>
      bits foreach { case (bitString, b) =>
        if ((index & (1 << b)) == 0) buffer append Clause((atom, false) ::(bitString, false) :: Nil)
        else buffer append Clause((atom, false) ::(bitString, true) :: Nil)
      }
    }

    atMostCounter += 1
    buffer.toSeq

    /*val atomArray = atoms.toArray
    val buffer = new ArrayBuffer[Clause]()

    var i = 0
    while (i < atomArray.length) {
      var j = i + 1
      while (j < atomArray.length) {
        buffer append Clause((atomArray(i), false) ::(atomArray(j), false) :: Nil)
        j += 1
      }
      i += 1
    }
    println("AT MOST " + atoms.length)
    buffer.toSeq*/
  }

  protected def exactlyOneOf(atoms: Seq[String]): Seq[Clause] = atMostOneOf(atoms) :+ atLeastOneOf(atoms)

  protected def impliesNot(left: String, right: String): Clause = Clause((left, false) ::(right, false) :: Nil)

  protected def notImpliesNot(left: Seq[String], right: String): Clause = Clause((left map { (_, true) }) :+(right, false))

  protected def impliesTrueAntNotToNot(leftTrue: String, leftFalse: String, right: String): Seq[Clause] = Clause((leftTrue, false) ::(leftFalse, true) ::(right, false) :: Nil) :: Nil

  protected def impliesAllNot(left: String, right: Seq[String]): Seq[Clause] = right map { impliesNot(left, _) }

  protected def notImpliesAllNot(left: Seq[String], right: Seq[String]): Seq[Clause] = right map { notImpliesNot(left, _) }

  protected def impliesSingle(left: String, right: String): Clause = Clause((left, false) ::(right, true) :: Nil)


  protected def impliesRightAnd(leftConjunct: Seq[String], rightConjunct: Seq[String]): Seq[Clause] = {
    val negLeft = leftConjunct map { (_, false) }
    rightConjunct map { r => Clause(negLeft :+(r, true)) }
  }

  protected def impliesRightOr(leftConjunct: Seq[String], rightConjunct: Seq[String]): Clause = {
    val negLeft = leftConjunct map { (_, false) }
    Clause(negLeft ++ (rightConjunct map { x => (x, true) }))
  }

  protected def allImply(left: Seq[String], target: String): Seq[Clause] = left flatMap { x => impliesRightAnd(x :: Nil, target :: Nil) }


  def possibleAndImpossibleActionsPerLayer(layer: Int): (Seq[Task], Seq[Task]) = if (domain.taskSchemaTransitionGraph.isAcyclic) {
    val actionsInDistance = initialPlan.planStepsWithoutInitGoal map { _.schema } flatMap { domain.taskSchemaTransitionGraph.getVerticesInDistance(_, layer + 1) }
    val possibleActions = (actionsInDistance ++ domain.primitiveTasks).distinct
    (possibleActions, domain.tasks filterNot possibleActions.contains)
  } else
    (domain.tasks, Nil)

  def possibleMethodsWithIndexPerLayer(layer: Int): (Seq[(DecompositionMethod, Int)], Seq[(DecompositionMethod, Int)]) = {
    val possibleActions = possibleAndImpossibleActionsPerLayer(layer)._1
    domain.decompositionMethods.zipWithIndex partition { case (m, _) => possibleActions contains m.abstractTask }
  }

  def decompositionFormula: Seq[Clause]

  def givenActionsFormula: Seq[Clause]

  def noAbstractsFormula: Seq[Clause]

  def stateTransitionFormula: Seq[Clause]

  def initialState: Seq[Clause]

  def goalState: Seq[Clause]


  def miniSATString(formulasSeq: Seq[Clause], writer: BufferedWriter): Map[String, Int] = {
    val formulas = formulasSeq.toArray
    println("NUMBER OF CLAUSES " + formulasSeq.length)

    // generate the atoms to int map
    val atomIndices = new mutable.HashMap[String, Int]()
    var i = 0
    while (i < formulas.length) {
      val lits = formulas(i).disjuncts
      var j = 0
      while (j < lits.length) {
        val l = lits(j)._1
        if (!(atomIndices contains l))
          atomIndices(l) = atomIndices.size
        j += 1
      }
      i += 1
    }

    // generate the DIMACS string

    val header = "p cnf " + atomIndices.size + " " + formulas.length + "\n"

    //val stringBuffer = new StringBuffer()
    //stringBuffer append header
    writer write header

    i = 0
    while (i < formulas.length) {
      val lits = formulas(i).disjuncts
      var j = 0
      while (j < lits.length) {
        val atomInt = (atomIndices(lits(j)._1) + 1) * (if (lits(j)._2) 1 else -1)
        //stringBuffer append atomInt
        writer write ("" + atomInt)
        //stringBuffer append ' '
        writer write ' '
        j += 1
      }
      //stringBuffer append "0\n"
      writer write "0\n"
      i += 1
    }

    //stringBuffer.toString
    atomIndices.toMap
  }
}

object VerifyEncoding {

  def computeICAPSK(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = 2 * taskSequenceLength * (domain.abstractTasks.length + 1)

  def computeTSTGK(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = domain.taskSchemaTransitionGraph.longestPathLength match {case Some(x) => x; case _ => Integer.MAX_VALUE }

  def computeMethodSize(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = {
    // recognize the case where only top has a unit method
    val (minMethodSize, heightIncrease) = if (plan.planStepsWithoutInitGoal.size == 1) {
      val (topFromMethods, otherMethods) = domain.decompositionMethods partition { _.abstractTask == plan.planStepsWithoutInitGoal.head.schema }
      val topToMethods = domain.decompositionMethods filter { _.subPlan.planStepsWithoutInitGoal exists { _.schema == plan.planStepsWithoutInitGoal.head.schema } }


      if (topFromMethods.size == 1 && topToMethods.isEmpty) (otherMethods map { _.subPlan.planStepsWithoutInitGoal.length } min, 1) else (domain.minimumMethodSize, 0)
    } else (domain.minimumMethodSize, 0)


    if (minMethodSize >= 2) Math.ceil(Math.log(taskSequenceLength) / Math.log(domain.minimumMethodSize)).toInt + heightIncrease else Integer.MAX_VALUE
  }

  def computeTheoreticalK(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = {
    val icapsPaperLimit = computeICAPSK(domain, plan, taskSequenceLength)
    val TSTGPath = computeTSTGK(domain, plan, taskSequenceLength)
    val minimumMethodSize = computeMethodSize(domain, plan, taskSequenceLength)

    println("LEN " + taskSequenceLength)
    println(icapsPaperLimit)
    println(TSTGPath)
    println(minimumMethodSize)

    Math.min(icapsPaperLimit, Math.min(TSTGPath, minimumMethodSize))
  }
}

case class Clause(disjuncts: Seq[(String, Boolean)]) {}

object Clause {
  def apply(atom: String): Clause = Clause((atom, true) :: Nil)

  def apply(literal: (String, Boolean)): Clause = Clause(literal :: Nil)
}
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

package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{BufferedWriter, File, FileInputStream, OutputStream}

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.compiler.{ClosedWorldAssumption, ExpandSortHierarchy, SHOPMethodCompiler, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.panda3.symbolic.sat.verify.Clause.atomIndices
import de.uniulm.ki.util._

import scala.collection._
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source

/**
  * This works only if grounded ....
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off number.of.methods
trait VerifyEncoding {

  def timeCapsule: TimeCapsule

  def domain: Domain

  def intProblem: IntProblem

  def initialPlan: Plan

  def taskSequenceLength: Int

  lazy val maxNumberOfActions = taskSequenceLength

  def offsetToK: Int

  def overrideK: Option[Int]

  lazy val K: Int = if (overrideK.isDefined) overrideK.get else VerifyEncoding.computeTheoreticalK(domain, initialPlan, maxNumberOfActions) + offsetToK

  def numberOfChildrenClauses: Int

  def numberOfPrimitiveTransitionSystemClauses: Int

  def expansionPossible: Boolean

  //domain.tasks foreach { t => assert(t.parameters.isEmpty) }
  //domain.predicates foreach { p => assert(p.argumentSorts.isEmpty) }
  //assert(initialPlan.init.substitutedEffects.length == domain.predicates.length)

  lazy val DELTA          = domain.maximumMethodSize
  lazy val numberOfLayers = K


  protected lazy val taskIndices          : Map[Task, Int]                = (domain.tasks :+ initialPlan.init.schema :+ initialPlan.goal.schema).zipWithIndex.toMap.withDefaultValue(-1)
  //println(taskIndices.toSeq.sortBy(_._2) map {case (t,i) => i + " " + t.name} mkString "\n")
  protected lazy val predicateIndices     : Map[Predicate, Int]           = domain.predicates.zipWithIndex.toMap
  protected lazy val methodIndices        : Map[DecompositionMethod, Int] = domain.decompositionMethods.zipWithIndex.toMap
  //println(methodIndices.toSeq.sortBy(_._2) map {case (t,i) => i + " " + t.name} mkString "\n")
  protected lazy val methodPlanStepIndices: Map[Int, Map[PlanStep, Int]]  = (domain.decompositionMethods map { method =>
    (methodIndices(method), method.subPlan.planStepsWithoutInitGoal.zipWithIndex.toMap)
  }).toMap

  protected def methodIndex(method: DecompositionMethod): Int = methodIndices(method)

  def predicateIndex(predicate: Predicate): Int = predicateIndices(predicate)

  def taskIndex(task: Task): Int = taskIndices(task)


  // LOGICALS ABBREVIATIONS
  def atLeastOneOf(atoms: Seq[String]): Clause = Clause(atoms map { (_, true) })

  protected var atMostCounter = 0

  // at most one of, but only if qualifier is true
  def atMostOneOf(atoms: Seq[String], qualifier: Option[String] = None): Seq[Clause] = {
    val buffer = new ArrayBuffer[Clause]()
    atMostCounter += 1

    val qualifierList: Seq[(String, Boolean)] = qualifier match {
      case None    => Nil
      case Some(q) => (q, false) :: Nil
    }

    AtMostOneType.chosenType match {
      case BinaryEncoding =>
        val numberOfBits: Int = Math.ceil(Math.log(atoms.length) / Math.log(2)).toInt
        val bits = Range(0, numberOfBits) map { b => ("atMost_" + atMostCounter + "_" + b, b) }

        atoms.zipWithIndex foreach { case (atom, index) =>
          bits foreach { case (bitString, b) =>
            if ((index & (1 << b)) == 0) buffer append Clause(((atom, false) :: (bitString, false) :: Nil) ++ qualifierList)
            else buffer append Clause(((atom, false) :: (bitString, true) :: Nil) ++ qualifierList)
          }
        }

      case BinomialEncoding =>
        val atomArray = atoms.toArray
        var i = 0
        while (i < atomArray.length) {
          var j = i + 1
          while (j < atomArray.length) {
            buffer append Clause((atomArray(i), false) :: (atomArray(j), false) :: Nil ++ qualifierList)
            j += 1
          }
          i += 1
        }

      case CommanderEncoding  =>
        // group into lists of three
        val groups = atoms.sliding(3, 3).toSeq

        groups foreach { group => group foreach { a1 => group foreach { a2 => if (a1 != a2) buffer append Clause((a1, false) :: (a2, false) :: Nil ++ qualifierList) } } }

        if (groups.length > 1) {
          val groupAtoms: Seq[String] = groups.zipWithIndex map {
            case (g, i) =>
              val groupAtom = "atMost_" + atMostCounter + "_ g_" + i

              buffer append impliesRightOr(groupAtom :: Nil ++ qualifierList.map(_._1), g)
              buffer appendAll notImpliesAllNot(groupAtom :: Nil, g, qualifierList.map(_._1))

              groupAtom
          } toSeq

          buffer appendAll atMostOneOf(groupAtoms)
        }
      case SequentialEncoding =>
        assert(qualifierList.isEmpty)
        buffer appendAll atMostKOf(atoms, 1)
    }

    buffer.toSeq
  }

  def atMostKOf(atoms: Seq[String], K: Int): Seq[Clause] = {
    val buffer = new ListBuffer[Clause]()
    atMostCounter += 1
    val N = atoms.length

    val registers: Array[Array[String]] = Range(0, N + 1) map { i => Range(0, K + 1) map { j => "atMost_" + atMostCounter + "_" + i + "_" + j } toArray } toArray

    //println("AT MOST")
    val registersInts: Array[Array[Int]] = registers map { _ map { r => Clause.atomIndices.getOrElseUpdate(r, Clause.atomIndices.size) + 1 } }
    val atomInts: Array[Int] = atoms.map({ a => Clause.atomIndices.getOrElseUpdate(a, Clause.atomIndices.size) + 1 }).toArray
    //println("INTS")

    var i = 1
    while (i < N + 1) {
      if (i < N) buffer append Clause(Array(-atomInts(i - 1), registersInts(i)(1)))
      buffer append Clause(Array(-atomInts(i - 1), -registersInts(i - 1)(K)))

      var j = 1
      while (i > 1 && j < K + 1) {
        buffer append Clause(Array(-registersInts(i - 1)(j), registersInts(i)(j)))
        if (j > 1) buffer append Clause(Array(-atomInts(i - 1), -registersInts(i - 1)(j - 1), registersInts(i)(j)))
        j += 1
      }
      i += 1
    }

    var j = 2
    while (j < K + 1) {
      buffer append Clause(Array(-registersInts(1)(j)))
      j += 1
    }

    /*Range(1, N) foreach { i => buffer append Clause((atoms(i - 1), false) :: (registers(i)(1), true) :: Nil) }
    Range(2, K + 1) foreach { j => buffer append Clause((registers(1)(j), false)) }
    Range(2, N) foreach { i => Range(1, K + 1) foreach { j => buffer append Clause((registers(i - 1)(j), false) :: (registers(i)(j), true) :: Nil) } }
    Range(2, N) foreach { i => Range(2, K + 1) foreach { j => buffer append Clause((atoms(i - 1), false) :: (registers(i - 1)(j - 1), false) :: (registers(i)(j), true) :: Nil) } }
    Range(1, N + 1) foreach { i => buffer append Clause((atoms(i - 1), false) :: (registers(i - 1)(K), false) :: Nil) }*/

    buffer
  }

  def exactlyOneOf(atoms: Seq[String]): Seq[Clause] = atMostOneOf(atoms).+:(atLeastOneOf(atoms))

  def impliesNot(left: String, right: String): Clause = Clause((left, false) :: (right, false) :: Nil)

  def impliesNot(left: Seq[String], right: String): Clause = Clause((left map { l => (l, false) }).+:(right, false))

  def notImplies(left: Seq[String], right: String): Clause = Clause((left map { (_, true) }).+:((right, true)))

  def notImpliesNot(left: Seq[String], right: String): Clause = Clause((left map { (_, true) }).+:((right, false)))


  def impliesTrueAntNotToNot(leftTrue: String, leftFalse: String, right: String): Seq[Clause] = Clause((leftTrue, false) :: (leftFalse, true) :: (right, false) :: Nil) :: Nil

  def impliesLeftTrueAndFalseImpliesTrue(leftTrue: Seq[String], leftFalse: Seq[String], right: String): Clause =
    Clause(leftTrue.map((_, false)) ++ leftFalse.map((_, true)) ++ ((right, true) :: Nil))

  def impliesAllNot(left: String, right: Seq[String]): Seq[Clause] = right map { impliesNot(left, _) }

  def impliesAllNot(left: Seq[String], right: Seq[String]): Seq[Clause] = right map { impliesNot(left, _) }

  def notImpliesAllNot(left: Seq[String], right: Seq[String], qualifier: Seq[String] = Nil): Seq[Clause] = {
    val leftList = left.map((_, true)) ++ qualifier.map((_, false))

    right map { r => Clause(leftList.:+((r, false))) }
  }

  def impliesSingle(left: String, right: String): Clause = Clause((left, false) :: (right, true) :: Nil)


  def impliesRightAnd(leftConjunct: Seq[String], rightConjunct: Seq[String]): Seq[Clause] = {
    val negLeft = leftConjunct map { (_, false) }
    rightConjunct map { r => Clause(negLeft.:+(r, true)) }
  }

  def impliesRightNotAll(leftConjunct: Seq[String], rightConjunct: Seq[String]): Clause = {
    val negLeft = leftConjunct map { (_, false) }
    val negRight = rightConjunct map { (_, false) }
    Clause(negLeft ++ negRight)
  }

  def impliesRightAndSingle(leftConjunct: Seq[String], right: String): Clause = {
    val negLeft = leftConjunct map { (_, false) }
    Clause(negLeft.+:(right, true))
  }

  def impliesRightOr(leftConjunct: Seq[String], rightDisjunct: Seq[String]): Clause = {
    val negLeft = leftConjunct map { (_, false) }
    Clause(negLeft ++ (rightDisjunct map { x => (x, true) }))
  }

  def allImply(left: Seq[String], target: String): Seq[Clause] = left flatMap { x => impliesRightAnd(x :: Nil, target :: Nil) }


  lazy val possibleAndImpossibleActionsPerLayer: Map[Int, (Seq[Task], Seq[Task])] = Range(-1, K) map { layer =>
    val possibleAndImpossibleActions = if (domain.taskSchemaTransitionGraph.isAcyclic) {
      val actionsInDistance = initialPlan.planStepsWithoutInitGoal map { _.schema } flatMap { domain.taskSchemaTransitionGraph.getVerticesInDistance(_, layer + 1) }
      val possibleActions = (actionsInDistance ++ domain.primitiveTasks).toSet
      (possibleActions.toSeq, domain.tasks filterNot possibleActions.contains)
    } else
      (domain.tasks, Nil)

    layer -> possibleAndImpossibleActions
  } toMap

  lazy val possibleMethodsWithIndexPerLayer: Map[Int, (Seq[(DecompositionMethod, Int)], Seq[(DecompositionMethod, Int)])] = Range(-1, K) map { layer =>
    val possibleActions = possibleAndImpossibleActionsPerLayer(layer)._1
    layer -> (domain.decompositionMethods.zipWithIndex partition { case (m, _) => possibleActions contains m.abstractTask })
  } toMap

  def decompositionFormula: Seq[Clause]

  def givenActionsFormula: Seq[Clause]

  def noAbstractsFormula: Seq[Clause]

  def stateTransitionFormula: Seq[Clause]

  def initialState: Seq[Clause]

  def goalState: Seq[Clause]

  def planLengthDependentFormula(actualPlanLength: Int): Seq[Clause] = Nil

  def miniSATString(formulas: Array[Clause], writer: BufferedWriter): scala.Predef.Map[String, Int] = {

    // generate the atoms to int map
    /*val atomIndices = new mutable.HashMap[String, Int]()
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
    }*/

    // generate the DIMACS string

    val header = "p cnf " + Clause.atomIndices.size + " " + formulas.length + "\n"

    //val stringBuffer = new StringBuffer()
    //stringBuffer append header
    writer write header

    var i = 0
    while (i < formulas.length) {
      val lits = formulas(i).disjuncts
      var j = 0
      while (j < lits.length) {
        //val atomInt = (atomIndices(lits(j)._1) + 1) * (if (lits(j)._2) 1 else -1)
        //stringBuffer append atomInt
        writer write ("" + lits(j))
        //stringBuffer append ' '
        writer write ' '
        j += 1
      }
      //stringBuffer append "0\n"
      writer write "0\n"
      i += 1
    }

    //stringBuffer.toString
    Clause.atomIndices.toMap
  }
}

object VerifyEncoding {

  def computeICAPSK(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = 2 * taskSequenceLength * (domain.abstractTasks.length + 1)

  def computeTSTGK(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = domain.taskSchemaTransitionGraph.longestPathLength match {case Some(x) => x; case _ => Integer.MAX_VALUE}

  def computeMethodSize(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = {
    // recognize the case where only top has a unit method
    val (minMethodSize, heightIncrease) = if (plan.planStepsWithoutInitGoal.size == 1) {
      val nonTopMethods = domain.decompositionMethods filterNot { _.abstractTask == plan.planStepsWithoutInitGoal.head.schema }
      val topToMethods = domain.decompositionMethods filter { _.subPlan.planStepsWithoutInitGoal exists { _.schema == plan.planStepsWithoutInitGoal.head.schema } }


      if (topToMethods.isEmpty) {
        if (nonTopMethods.isEmpty) (Integer.MAX_VALUE, 1) else (nonTopMethods map { _.subPlan.planStepsWithoutInitGoal.length } min, 1)
      } else (domain.minimumMethodSize, 0)
    } else (domain.minimumMethodSize, 0)

    if (minMethodSize >= 2) Math.ceil((taskSequenceLength - plan.planStepsWithoutInitGoal.length).toDouble / (minMethodSize - 1)).toInt + heightIncrease else Integer.MAX_VALUE
  }

  def computeTDG(domain: Domain, initialPlan: Plan, taskSequenceLength: Int, accumulate: (Int, Int) => Int, initialValue: Int): Int = {

    def printMap(map: Map[Task, Map[Int, Int]]): Unit = {
      println("\nMAP")
      val blacklist = Nil //SHOPMethodCompiler.SHOP_METHOD_PRECONDITION_PREFIX :: "direct_" :: "plug" :: Nil
      println(map.toSeq.sortBy(_._1.name) collect { case (t, m) if !blacklist.exists(t.name.startsWith) =>
        t.name + " map:\n\t" + m.toSeq.sorted.map(x => x._1 + "->" + x._2).mkString(" ")
      } mkString "\n")
    }

    def recomputePlan(plan: Plan, map: Map[Task, Map[Int, Int]], zeroCostMode: Boolean, currentSCC: Set[Task], minCosideredLength: Int): Map[Int, Int] = {
      //val sortedTasks = plan.planStepSchemaArrayWithoutMethodPreconditions sortBy { map(_).size } toArray
      val sortedTasks = plan.planStepSchemaArray sortBy { map(_).size } toArray

      Range(minCosideredLength, taskSequenceLength + 1) map { totalLength =>
        val cached = new mutable.HashMap[(Int, Int), Option[Int]]()
        //println("============\nLength " + totalLength + " Min " + minCosideredLength)

        // try to distribute it to all tasks in the plan
        def minimumByDistribution(currentTask: Int, remainingLength: Int): Option[Int] = if (cached contains(currentTask, remainingLength)) cached((currentTask, remainingLength))
        else if (currentTask == sortedTasks.length) {if (remainingLength == 0) Some(0) else None } else {
          //println("\tDP " + currentTask + " " + remainingLength)
          val firstTaskMap = map(sortedTasks(currentTask))
          //println("\t" + firstTaskMap)
          val subValues = Range(0, remainingLength + 1) collect { case l if firstTaskMap contains l =>
            val recVal = minimumByDistribution(currentTask + 1, remainingLength - l)
            if (totalLength == 0 || (l == totalLength && sortedTasks.length > 1 && currentSCC.contains(sortedTasks(currentTask)))) {
              if (zeroCostMode) (l, recVal) else (l, None)
            } else {
              if (zeroCostMode && l != 0) (l, None) else (l, recVal)
            }
          }
          //println(subValues)
          val definedSubValues = subValues collect { case (l, Some(subHeight)) => Math.max(subHeight, firstTaskMap(l)) }
          val result = if (definedSubValues.isEmpty) None else Some(definedSubValues max)
          cached((currentTask, remainingLength)) = result
          result
        }

        //println(cached)

        totalLength -> minimumByDistribution(0, totalLength)
      } collect { case (length, Some(height)) => length -> (1 + height) } toMap
    }

    def recomputeTask(task: Task, m: Map[Task, Map[Int, Int]], zeroCostMode: Boolean, currentSCC: Set[Task], minCosideredLength: Int): (Map[Int, Int], Boolean) =
      if (task.isPrimitive) (m(task), false)
      else {
        val methodMaps: Seq[Map[Int, Int]] = domain.methodsForAbstractTasks(task) map { method =>
          val r = recomputePlan(method.subPlan, m, zeroCostMode, currentSCC, minCosideredLength)
          //println(task.name + " " + method.name + " " + method.subPlan.planStepSchemaArray.map(_.name).mkString(" ") + " " + r)
          r
        }
        val newMap: Map[Int, Int] = (methodMaps :+ m(task)).reduce[Map[Int, Int]]({ case (m1, m2) => m1 ++ m2.map({ case (l, h) => l -> accumulate(h, m1.getOrElse(l, initialValue)) }) })

        (newMap, newMap != m(task))
      }

    val condensedTSTG = domain.taskSchemaTransitionGraph.condensation
    val condensationTopSort: Seq[Set[Task]] = condensedTSTG.topologicalOrdering.get.reverse // this will always work, since it is a condensation

    // run through the topological sorting of the condensation
    val expandedMap = condensationTopSort.foldLeft(Map[Task, Map[Int, Int]]())(
      { case (map, scc) =>
        //println("\n\n\n\n\n\ndeal with " + scc.map(_.name).mkString(" "))
        var initalised = scc.foldLeft(map)({ case (m, t) => m + (t -> (if (t.isPrimitive) Map(t.cost.getFixedCost -> 0) else Map())) })

        var zeroCostChanged = true
        var zeroCostMode = false
        var minCosideredLength = 0
        while (zeroCostChanged) {
          var r = 0
          var changed = true
          zeroCostChanged = !zeroCostMode
          while (changed && (!zeroCostMode || r < scc.size + 1)) {
            //println("Recompute")
            //println("Round " + r + " " + zeroCostMode + " " + changed)
            val allUpdate =
              scc.map({ case task => (task, recomputeTask(task, initalised, zeroCostMode = zeroCostMode, scc, minCosideredLength)) })

            changed = allUpdate.exists(_._2._2)
            if (zeroCostMode) zeroCostChanged |= changed
            if (changed) {
              initalised = allUpdate.foldLeft(initalised)({ case (cm, (t, (m, c))) => if (c) cm + (t -> m) else cm })
            }
            //printMap(initalised)

            r += 1
            //if (r == 5) System exit 0
            //println("End: " + changed  + " " + r + " " + scc.size + " " + zeroCostMode)
          }

          //if (r == 0 && !zeroCostChanged) System exit 0

          if (zeroCostMode) minCosideredLength += 1
          //else minCosideredLength = 0
          zeroCostMode = !zeroCostMode

        }

        //if (scc.map(_.name).contains("goto[location_1;]")) System exit 0

        initalised
      })

    val initialPlanMap = recomputePlan(initialPlan, expandedMap, zeroCostMode = false, Set(), taskSequenceLength)

    //printMap(expandedMap)

    //println("INITIAL PLAN")
    //println(initialPlan.planStepsWithoutInitGoal map {_.schema.name} mkString "\n")

    if (initialPlanMap.isEmpty) 0 else initialPlanMap.values.max
    // just to be sure for edge cases ..
  }

  def computeTheoreticalK(domain: Domain, plan: Plan, taskSequenceLength: Int): Int = {
    println("LEN " + taskSequenceLength)
    val icapsPaperLimit = computeICAPSK(domain, plan, taskSequenceLength)
    println("ICAPS: " + icapsPaperLimit)
    val TSTGPath = computeTSTGK(domain, plan, taskSequenceLength)
    println("TSTG: " + TSTGPath)
    val minimumMethodSize = computeMethodSize(domain, plan, taskSequenceLength)
    println("Method: " + minimumMethodSize)
    val tdg = computeTDG(domain, plan, taskSequenceLength, Math.max, 0)
    //val tdgmin = computeTDG(domain, plan, taskSequenceLength, Math.min, Integer.MAX_VALUE)

    println("DP max: " + tdg)
    //println("DP min: " + tdgmin)
    //System exit 0

    val minK = Math.min(icapsPaperLimit, Math.min(TSTGPath, Math.min(minimumMethodSize, tdg)))

    println("Taking minimum: " + minK)

    minK
  }

  def lowerBoundOnNonPlanExistence(domain: Domain, plan: Plan, largestKForWhichNoPlanExisted: Int): Int = {
    //computeTheoreticalK(domain, plan, 20)
    //System exit 0

    var lower = 0
    var found = false
    while (!found) {
      // compute K
      lower += 1
      val K = computeTheoreticalK(domain, plan, lower)
      if (K >= largestKForWhichNoPlanExisted) found = true
    }
    lower - 1
  }

}

case class Clause(disjuncts: Array[Int]) {
  override def toString: String = "Clause(" + disjuncts.mkString(",") + ")"
}

object Clause {
  val atomIndices = new mutable.HashMap[String, Int]()

  def clearCache(): Unit = atomIndices.clear()

  def apply(disjuncts: Array[(String, Boolean)]): Clause = {
    val compressed = new Array[Int](disjuncts.length)
    var i = 0
    while (i < disjuncts.length) {
      val atomIndex = atomIndices.getOrElseUpdate(disjuncts(i)._1, atomIndices.size)
      if (disjuncts(i)._2)
        compressed(i) = atomIndex + 1
      else
        compressed(i) = -1 * atomIndex - 1
      i += 1
    }
    Clause(compressed)
  }

  def apply(disjuncts: Seq[(String, Boolean)]): Clause = Clause(disjuncts.toArray)

  def apply(atom: String): Clause = Clause((atom, true) :: Nil)

  def apply(atoms: Array[String]): Clause = Clause(atoms map { a => (a, true) })

  def apply(literal: (String, Boolean)): Clause = Clause(literal :: Nil)
}


sealed trait AtMostOneType

object AtMostOneType {
  //val chosenType: AtMostOneType = BinomialEncoding
  //val chosenType: AtMostOneType = BinaryEncoding
  //val chosenType: AtMostOneType = CommanderEncoding
  var chosenType: AtMostOneType = SequentialEncoding
}

object BinomialEncoding extends AtMostOneType

object BinaryEncoding extends AtMostOneType

object CommanderEncoding extends AtMostOneType

object SequentialEncoding extends AtMostOneType
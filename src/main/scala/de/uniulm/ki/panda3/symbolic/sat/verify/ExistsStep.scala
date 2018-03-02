package de.uniulm.ki.panda3.symbolic.sat.verify

import java.util

import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.{GroundedPlanningGraph, GroundedPlanningGraphConfiguration}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints.AlternatingAutomatonFormulaEncoding
import de.uniulm.ki.util.{DirectedGraph, Dot2PdfCompiler, SimpleDirectedGraph, TimeCapsule}

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class ExistsStep(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, intProblem: IntProblem, taskSequenceLengthQQ: Int,
                      ltlEncodings: Seq[AdditionalEdgesInDisablingGraph]) extends LinearPrimitivePlanEncoding {
  override lazy val offsetToK = 0

  override lazy val overrideK = None

  override lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  override val numberOfChildrenClauses = 0 // none

  override val expansionPossible = Math.pow(2, domain.predicates.length) > taskSequenceLength

  override val decompositionFormula = Nil

  override val givenActionsFormula = Nil

  override val noAbstractsFormula = Nil

  def chain(position: Int, j: Int, chainID: String): String = "chain_" + position + "^" + j + ";" + chainID

  def generateChainForAtTime(E: Array[(Task, Int)], R: Array[(Task, Int)], chainID: String, position: Int, qualifierOption: Option[String]): Seq[Clause] = {
    val time0 = System.currentTimeMillis()
    // generate chain restriction for every SCC
    val f1: Seq[Clause] = E.foldLeft[(Seq[Clause], Int)]((Nil, 0))({ case ((clausesSoFar, rpos), (oi, i)) =>
      // search forward for next R
      var newR = rpos
      while (newR < R.length && R(newR)._2 <= i) newR += 1

      if (newR < R.length) {
        val newClause = qualifierOption match {
          case None            => impliesSingle(action(K - 1, position, oi), chain(position, R(newR)._2, chainID))
          case Some(qualifier) => Clause((action(K - 1, position, oi), false) :: (chain(position, R(newR)._2, chainID), true) :: (qualifier, false) :: Nil)
        }
        (clausesSoFar :+ newClause, newR)
      } else
        (clausesSoFar, newR)
                                                                   })._1
    val time1 = System.currentTimeMillis()

    val f2 = R.foldLeft[(Seq[Clause], Int)]((Nil, 0))({ case ((clausesSoFar, rpos), (ai, i)) =>
      // search forward for next R
      var newR = rpos
      while (newR < R.length && R(newR)._2 <= i) newR += 1

      if (newR < R.length) {
        val newClause = qualifierOption match {
          case None            => impliesSingle(chain(position, i, chainID), chain(position, R(newR)._2, chainID))
          case Some(qualifier) => Clause((chain(position, i, chainID), false) :: (chain(position, R(newR)._2, chainID), true) :: (qualifier, false) :: Nil)
        }
        (clausesSoFar :+ newClause, newR)
      } else
        (clausesSoFar, newR)
                                                      })._1
    val time2 = System.currentTimeMillis()

    val f3: Seq[Clause] = R map { case (ai, i) =>
      qualifierOption match {
        case None            => impliesNot(chain(position, i, chainID), action(K - 1, position, ai))
        case Some(qualifier) => Clause((chain(position, i, chainID), false) :: (action(K - 1, position, ai), false) :: (qualifier, false) :: Nil)
      }
    }
    val time3 = System.currentTimeMillis()
    //println("Chain f's " + (time1 - time0) + "ms " + (time2 - time1) + "ms " + (time3 - time2) + "ms " + E.length)

    f1 ++ f2 ++ f3
  }


  // chain for E/R, but only if qualifier is true
  def generateChainFor(E: Array[(Task, Int)], R: Array[(Task, Int)], chainID: String, qualifierOption: Option[String] = None): Seq[Clause] =
    Range(0, taskSequenceLength) flatMap { case position => generateChainForAtTime(E, R, chainID, position, qualifierOption) }


  override lazy val stateTransitionFormula: Seq[Clause] = {
    val t0001 = System.currentTimeMillis()
    // we need one chain per predicate
    val parallelismFormula = intProblem.existsStepERPerPredicate flatMap { case (e, r, chainID) => generateChainFor(e, r, chainID) }
    val t0002 = System.currentTimeMillis()
    println("Chains: " + (t0002 - t0001) + "ms")

    val invariantFormula = Range(0, taskSequenceLength + 1) flatMap { case position =>
      intProblem.symbolicInvariantArray map { case ((ap, ab), (bp, bb)) => Clause((statePredicate(K - 1, position, ap), ab) :: (statePredicate(K - 1, position, bp), bb) :: Nil) }
    }

    val t0003 = System.currentTimeMillis()
    println("ExistsStep Formula: " + (t0003 - t0002) + "ms")

    val transitionFormula = stateTransitionFormulaOfLength(taskSequenceLength)
    val t0004 = System.currentTimeMillis()
    println("State Transition Formula: " + (t0004 - t0003) + "ms")

    transitionFormula ++ parallelismFormula ++ invariantFormula
  }

  override lazy val goalState: Seq[Clause] =
    goalStateOfLength(taskSequenceLength)

  println("Exists-Step, plan length: " + taskSequenceLength)
}


trait AdditionalEdgesInDisablingGraph {
  def additionalEdges(intProblem: IntProblem)(
    predicateToAdding: Map[Predicate, Array[intProblem.IntTask]], predicateToDeleting: Map[Predicate, Array[intProblem.IntTask]],
    predicateToNeeding: Map[Predicate, Array[intProblem.IntTask]]): Seq[(intProblem.IntTask, intProblem.IntTask)]
}
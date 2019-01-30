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


import de.uniulm.ki.panda3.symbolic.domain.{ActionCost, Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class ExistsStep(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, intProblem: IntProblem,
                      taskSequenceLengthQQ: Int, maxNumberOfActions: Int,
                      ltlEncodings: Seq[AdditionalEdgesInDisablingGraph], overrideOverrideK: Option[Int] = None,
                      tasksToIgnore: Set[Task] = Set()) extends LinearPrimitivePlanEncoding {

  override def ignoreActionInStateTransition(task: Task): Boolean = tasksToIgnore(task)

  override lazy val offsetToK = 0

  override lazy val overrideK = if (overrideOverrideK.isDefined) overrideOverrideK else Some(0)

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
    val f1: Seq[Clause] = E.foldLeft[(Seq[Clause], Int)]((Nil, 0))(
      {
        case ((clausesSoFar, rpos), (oi, i)) if ignoreActionInStateTransition(oi) =>
          (clausesSoFar, rpos)
        case ((clausesSoFar, rpos), (oi, i))                                      =>
          // search forward for next R
          var newR = rpos
          while (newR < R.length && (R(newR)._2 <= i || ignoreActionInStateTransition(R(newR)._1))) newR += 1

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

    val f2 = R.foldLeft[(Seq[Clause], Int)]((Nil, 0))(
      {
        case ((clausesSoFar, rpos), (ai, i)) if ignoreActionInStateTransition(ai) =>
          (clausesSoFar, rpos)
        case ((clausesSoFar, rpos), (ai, i))                                      =>
          // search forward for next R
          var newR = rpos
          while (newR < R.length && (R(newR)._2 <= i || ignoreActionInStateTransition(R(newR)._1))) newR += 1

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

    val f3: Seq[Clause] = R collect { case (ai, i) if !ignoreActionInStateTransition(ai) =>
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

    intProblem.symbolicInvariantArray
    val t00025 = System.currentTimeMillis()
    println("Invariants: " + (t00025 - t0002) + "ms")

    val invariantFormula = Range(0, taskSequenceLength + 1) flatMap { case position =>
      intProblem.symbolicInvariantArray map { case ((ap, ab), (bp, bb)) => Clause((statePredicate(K - 1, position, ap), ab) :: (statePredicate(K - 1, position, bp), bb) :: Nil) }
    }

    val t0003 = System.currentTimeMillis()
    println("ExistsStep Formula: " + (t0003 - t00025) + "ms")

    val transitionFormula = stateTransitionFormulaOfLength(taskSequenceLength)
    val t0004 = System.currentTimeMillis()
    println("State Transition Formula: " + (t0004 - t0003) + "ms")

    val numberOfActionsRestriction = if (maxNumberOfActions == -1) Nil else {
      val allActionsAtoms = domain.primitiveTasks filter ActionCost.hasCost filterNot ignoreActionInStateTransition flatMap {
        task => Range(0, taskSequenceLength + 1) map { case position => action(K - 1, position, task) }
      }
      atMostKOf(allActionsAtoms, maxNumberOfActions)
    }
    val t0005 = System.currentTimeMillis()
    println("Number of actions Formula: " + (t0005 - t0004) + "ms")

    transitionFormula ++ parallelismFormula ++ invariantFormula ++ numberOfActionsRestriction
  }

  override lazy val goalState: Seq[Clause] =
    goalStateOfLength(taskSequenceLength)

  println("Exists-Step, plan length: " + taskSequenceLength)

  override lazy val numberOfPrimitiveTransitionSystemClauses = stateTransitionFormula.length
}


trait AdditionalEdgesInDisablingGraph {
  def additionalEdges(intProblem: IntProblem)(
    predicateToAdding: Map[Predicate, Array[intProblem.IntTask]], predicateToDeleting: Map[Predicate, Array[intProblem.IntTask]],
    predicateToNeeding: Map[Predicate, Array[intProblem.IntTask]]): Seq[(intProblem.IntTask, intProblem.IntTask)]
}

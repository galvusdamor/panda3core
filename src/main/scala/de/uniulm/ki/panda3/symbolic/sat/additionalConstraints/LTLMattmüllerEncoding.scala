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

package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.panda3.symbolic.sat.verify._

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLMattmüllerEncoding(lTLFormula: LTLFormula, id: String, improvedChains: Boolean) extends AdditionalSATConstraint with MattmüllerDisablingGraphExtension {

  private val allSubformulae : Seq[LTLFormula] = lTLFormula.allSubformulae.toSeq

  val formulaIDMap: Map[LTLFormula, Int] = allSubformulae.zipWithIndex toMap
  val idFormulaMap: Map[Int, LTLFormula] = formulaIDMap map { _.swap }

  //println(idFormulaMap.toSeq.sortBy(_._1) map { case (a, b) => a + " " + b.longInfo } mkString "\n")

  private def formulaHoldsAtTime(formula: LTLFormula, time: Int): String = id + "_holds_" + formulaIDMap(formula) + "@" + time

  private def exacltyMostOneActionAtTime(time: Int): String = id + "_at_most_one_action@" + time

  private def noneActionAfter(time: Int): String = id + "_non_action_after@" + time

  // time of an LTL formula is the time *before* actions are executed
  // LTL_0, A_0, LTL_1 , ... , A_N, LTL_(N+1)
  // Attention: N+1 == linearEncoding.taskSequenceLength
  def apply(linearEncoding: LinearPrimitivePlanEncoding): Seq[Clause] = {
    val time0 = System.currentTimeMillis()
    val x = linearEncoding match {
      case ex: ExistsStep =>
        // write for every timestep the rules
        val time00 = System.currentTimeMillis()
        val formulaTruth = Range(0, linearEncoding.taskSequenceLength + 1) flatMap { time =>
          val ltlPart = allSubformulae flatMap { sub =>
            sub match {
              case PredicateAtom(p) => linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), linearEncoding.statePredicate(linearEncoding.K - 1, time, p)) :: Nil
              case TaskAtom(t)      => assert(false, "can't handle yet"); Nil
              case LTLNot(f)        => linearEncoding.impliesNot(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f, time)) :: Nil
              case LTLAnd(l)        => linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, time) :: Nil, l.map(formulaHoldsAtTime(_, time)))
              case LTLOr(l)         => linearEncoding.impliesRightOr(formulaHoldsAtTime(sub, time) :: Nil, l.map(formulaHoldsAtTime(_, time))) :: Nil

              case LTLAlways(f) if time != linearEncoding.taskSequenceLength =>
                linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, time) :: Nil, formulaHoldsAtTime(f, time) :: formulaHoldsAtTime(sub, time + 1) :: Nil)
              case LTLAlways(f) if time == linearEncoding.taskSequenceLength =>
                linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, time) :: Nil, formulaHoldsAtTime(f, time) :: Nil)

              case LTLEventually(f) if time != linearEncoding.taskSequenceLength =>
                linearEncoding.impliesRightOr(formulaHoldsAtTime(sub, time) :: Nil, formulaHoldsAtTime(f, time) :: formulaHoldsAtTime(sub, time + 1) :: Nil) :: Nil
              case LTLEventually(f) if time == linearEncoding.taskSequenceLength =>
                linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f, time)) :: Nil

              case LTLUntil(f1, f2) if time != linearEncoding.taskSequenceLength =>
                // structure a -> 2 v (1 & n) == (-a v 2 v 1) & (-a v 2 v n)
                val a = formulaHoldsAtTime(sub, time)
                val l1 = formulaHoldsAtTime(f1, time)
                val l2 = formulaHoldsAtTime(f2, time)
                val n = formulaHoldsAtTime(sub, time + 1)
                Clause((a, false) :: (l2, true) :: (l1, true) :: Nil) :: Clause((a, false) :: (l2, true) :: (n, true) :: Nil) :: Nil
              case LTLUntil(_, f2) if time == linearEncoding.taskSequenceLength  =>
                linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f2, time)) :: Nil

              case LTLRelease(f1, f2) if time != linearEncoding.taskSequenceLength =>
                val a = formulaHoldsAtTime(sub, time)
                val l1 = formulaHoldsAtTime(f1, time)
                val l2 = formulaHoldsAtTime(f2, time)
                val n = formulaHoldsAtTime(sub, time + 1)
                linearEncoding.impliesSingle(a, l2) :: linearEncoding.impliesRightOr(a :: Nil, l1 :: n :: Nil) :: Nil
              // TODO: check this !!!!
              case LTLRelease(_, f2) if time == linearEncoding.taskSequenceLength =>
                linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f2, time)) :: Nil

              case LTLWeakNext(f) =>
                val holdsWXf = formulaHoldsAtTime(sub, time)
                val holdsFNext = formulaHoldsAtTime(f, time + 1)

                val ifWXfOnlyOneBefore = linearEncoding.impliesSingle(holdsWXf, exacltyMostOneActionAtTime(time - 1))
                val ifWXEitherOnlyOneNextOrNoneAtAll = linearEncoding.impliesRightOr(holdsWXf :: Nil, exacltyMostOneActionAtTime(time) :: noneActionAfter(time) :: Nil)

                ifWXEitherOnlyOneNextOrNoneAtAll :: ifWXfOnlyOneBefore :: linearEncoding.impliesRightOr(holdsWXf :: exacltyMostOneActionAtTime(time) :: Nil, holdsFNext :: Nil) :: Nil
              //ifWXEitherOnlyOneNextOrNoneAtAll :: ifWXfOnlyOneBefore  :: Nil
            }
          }
          val restriction =
            if (time == linearEncoding.taskSequenceLength)
              Nil
            else {
              val actionLiterals = linearEncoding.linearPlan(time).values.toSeq
              val atMostOne = linearEncoding.atMostOneOf(actionLiterals, Some(exacltyMostOneActionAtTime(time))) :+
                linearEncoding.impliesRightOr(exacltyMostOneActionAtTime(time) :: Nil, actionLiterals)
              val none = linearEncoding.impliesAllNot(noneActionAfter(time), actionLiterals) :+
                linearEncoding.impliesSingle(noneActionAfter(time), noneActionAfter(time + 1))

              atMostOne ++ none
            }

          ltlPart ++ restriction
        }
        val time01 = System.currentTimeMillis()
        println("Hold Time: " + (time01 - time00) + "ms")

        // restrict to sequential encoding for testing purposes
        /*val amo = Range(0, linearEncoding.taskSequenceLength).flatMap(position =>
                                                                        linearEncoding.atMostOneOf(linearEncoding.domain.primitiveTasks map {
                                                                          linearEncoding
                                                                            .action(linearEncoding.K - 1, position, _)
                                                                        }))*/

      val holeFormulaHolds = Clause(formulaHoldsAtTime(lTLFormula, 0)) :: Nil

        // add the chains necessary for the formulae
        val chainClauses = {
          lTLFormula.allPredicates.toSeq flatMap { m =>
            ex.intProblem.mattmüllerLTLERPerPredicates(m) flatMap { case (e,r,chainID) =>
              if (!improvedChains) ex.generateChainFor(e, r, chainID) else {
                // improved chain generation: we can disable a chain if we don't have to check a property that is related to that predicate at the moment
                // Thesis: it is only relevant, it we have to check an atomic proposition (wide speculation, but should be true)
                Range(0, linearEncoding.taskSequenceLength) flatMap { time => ex.generateChainForAtTime(e, r, chainID, time, Some(formulaHoldsAtTime(PredicateAtom(m), time + 1))) }
              }
            }
          }
        }
        val time02 = System.currentTimeMillis()
        //println("Chain Time Total: " + (time02 - time01) + "ms")


        //println("TOTAL " + " " + formulaTruth.length + " " + holeFormulaHolds.length + " " + chainClauses.length)
        formulaTruth ++ holeFormulaHolds ++ chainClauses //++ amo
      case _             => assert(false, "Mattmüller-Encoding is only compatible with existsstep"); Nil
    }
    val time1 = System.currentTimeMillis()
    println("Mattmüller Time: " + (time1 - time0) + "ms for " + x.length)
    //System exit 0
    x
  }
}


trait MattmüllerDisablingGraphExtension extends AdditionalEdgesInDisablingGraph {
  def lTLFormula : LTLFormula

   override def additionalEdges(intProblem : IntProblem)(
    predicateToAdding: Map[Predicate, Array[intProblem.IntTask]], predicateToDeleting: Map[Predicate, Array[intProblem.IntTask]],
    predicateToNeeding: Map[Predicate, Array[intProblem.IntTask]]): Seq[(intProblem.IntTask, intProblem.IntTask)] = {
    // first get relevant predicates
    val relevantPredicates = lTLFormula.allPredicates
    // TODO: so something with actions

    val affectedActions: Array[intProblem.IntTask] = relevantPredicates flatMap { p => predicateToAdding.getOrElse(p, Array()) ++ predicateToDeleting.getOrElse(p, Array()) } toArray

    val edgesFromStatePredicates = affectedActions flatMap { a1 =>
      affectedActions collect {
        case a2 if a1 != a2 && a1.hasMoreEffectsRelativeToPredicates(a2, relevantPredicates) => (a1, a2)
      }
    }

    edgesFromStatePredicates
  }
}

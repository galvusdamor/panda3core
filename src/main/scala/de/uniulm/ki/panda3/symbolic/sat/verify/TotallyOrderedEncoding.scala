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

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{And, Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser.GreedyNumberOfChildrenFromTotallyOrderedOptimiser
import de.uniulm.ki.util._

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TotallyOrderedEncoding(timeCapsule: TimeCapsule,
                                   domain: Domain, initialPlan: Plan, intProblem : IntProblem,
                                  reductionMethod: SATReductionMethod, taskSequenceLength: Int, offsetToK: Int, overrideK: Option[Int] = None,
                                  restrictionMethod: RestrictionMethod, usePDTMutexes: Boolean)
  extends TreeEncoding with EncodingWithLinearPlan with NumberOfActionsRestrictionViaAutomaton[Unit,Unit]{


  assert(domain.decompositionMethods forall { _.subPlan.orderingConstraints.fullGraph.allTotalOrderings.get.length == 1 })

  override val optimiser = GreedyNumberOfChildrenFromTotallyOrderedOptimiser

  val numberOfChildrenClauses: Int = 0

  assert(domain.isTotallyOrdered)
  assert(initialPlan.orderingConstraints.isTotalOrder())

  protected val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, methodChildrenPositions : Map[Int,Int]): Seq[Clause] = Nil

  private def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
          else
            impliesNot(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  private def stateChange(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.effect.conjuncts collect {
        // negated effect is also contained, ignore this one if it is negative
        case Literal(pred, isPositive, _) if !((task.effect.conjuncts exists { l => l.predicate == pred && l.isNegative == isPositive }) && !isPositive) =>
          // there won't be any parameters
          if (isPositive)
            impliesSingle(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  // maintains the state only if all actions are actually executed
  private def maintainState(layer: Int, position: Int): Seq[Clause] = domain.predicates flatMap {
    predicate =>
      true :: false :: Nil map {
        makeItPositive =>
          val changingActions: Seq[Task] = (if (makeItPositive) domain.primitiveChangingPredicate(predicate)._1 else domain.primitiveChangingPredicate(predicate)._2) filter
            primitivePaths(position)._2.contains

          val taskLiterals = changingActions map { pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, _) } map { (_, true) }
          Clause(taskLiterals.+:(statePredicate(layer, position, predicate), makeItPositive).+:(statePredicate(layer, position + 1, predicate), !makeItPositive))
      }
  }

  lazy val invariantFormula = Range(0, taskSequenceLength + 1) flatMap { case position =>
    intProblem.symbolicInvariantArray map { case ((ap, ab), (bp, bb)) => Clause((statePredicate(K - 1, position, ap), ab) :: (statePredicate(K - 1, position, bp), bb) :: Nil) }
  }


  override lazy val stateTransitionFormula: Seq[Clause] = {primitivePaths.indices flatMap { position =>
    primitivesApplicable(K, position) ++ stateChange(K, position) ++ maintainState(K, position)
  }} ++ numberOfActionsFormula(primitivePaths) ++ invariantFormula

  override lazy val noAbstractsFormula: Seq[Clause] =
    primitivePaths flatMap { case (position, tasks) => tasks filter { _.isAbstract } map { task => Clause((pathAction(position.length, position, task), false)) } }

  override lazy val goalState: Seq[Clause] =
    initialPlan.goal.substitutedPreconditions map { case Literal(predicate, isPos, _) => Clause((statePredicate(K, primitivePaths.length, predicate), isPos)) }

  lazy val initialState: Seq[Clause] = {
    val initiallyTruePredicates = initialPlan.init.substitutedEffects collect { case Literal(pred, true, _) => pred }

    val initTrue = initiallyTruePredicates map { predicate => Clause((statePredicate(K, 0, predicate), true)) }
    val initFalse = domain.predicates diff initiallyTruePredicates map { pred => Clause((statePredicate(K, 0, pred), false)) }

    initTrue ++ initFalse
  }


  override def linearPlan: scala.Seq[Map[Task, String]] = primitivePaths map { case (path, tasks) => tasks map { t => t -> pathAction(path.length, path, t) } toMap }

  override def linearStateFeatures = {
    // there is one more state
    Range(0, primitivePaths.length + 1) map { case i => domain.predicates map { p => p -> { statePredicate(K, i, p) } } toMap }
  }

  override lazy val givenActionsFormula: Seq[Clause] = ???

  override protected def initialPayload(possibleTasks: Set[Task], path: scala.Seq[Int]): Unit = ()

  override protected def combinePayloads(childrenPayload: Seq[Unit], intermediate: Unit): Unit = ()

  private def canBeAchieved(prec: Seq[Seq[(Int, Task)]]): Boolean = {
    def dfs(remaining: Seq[Seq[(Int, Task)]], asserted: Map[Int, Task]): Boolean = if (remaining.isEmpty) true
    else {
      // try every possibility
      remaining.head exists { case (l, task) => if (!asserted.contains(l) || asserted(l) == task) dfs(remaining.tail, asserted.+((l, task))) else false }
    }

    dfs(prec, Map())
  }


  protected def filterPrimitivesFF(sortedPaths: Array[(Seq[Int], Set[Task])], fullTest: Boolean = false): Seq[Set[Task]] = {
    // perform simple PG-style reachability on the given list of actions
    val initialPredicates = initialPlan.init.schema.effectsAsPredicateBool collect { case (predicate, true) => predicate } toSet
    val initialAchiever: Map[Predicate, Seq[(Int, Task)]] = initialPredicates map { p => p -> ((-1, ReducedTask("init", true, Nil, Nil, Nil, And(Nil), And(Nil))) :: Nil) } toMap
    val initialState = (initialPredicates, initialAchiever)

    val (finalState, tasksToRemoveFromPaths) = sortedPaths.zipWithIndex.foldLeft[((Set[Predicate], Map[Predicate, Seq[(Int, Task)]]), Seq[Set[Task]])]((initialState, Nil))(
      {
        case (((state, predicateLayers), toRemove), ((path, tasks), layer)) =>
          val (applicable, nonApplicable) = tasks partition { t => t.isPrimitive && (t.preconditionsAsPredicateBool forall { case (p, true) => state contains p }) && {
            if (fullTest) {
              // run full NP matching test
              val possibleAchievers: Seq[Seq[(Int, Task)]] = t.preconditionsAsPredicateBool.collect({ case (a, true) => a }) map predicateLayers
              canBeAchieved(possibleAchievers)
            } else true
          }
          }

          if (nonApplicable.nonEmpty)
            println("Found " + nonApplicable.size + " non applicable tasks at " + path + ". Still applicable " + applicable.size)

          val actionEffects = applicable flatMap { case task => task.effectsAsPredicateBool collect { case (predicate, true) => predicate ->(layer, task) } }

          // apply tasks
          val reachableState = state ++ actionEffects.map(_._1)

          val updatedPredicateLayer: Map[Predicate, Seq[(Int, Task)]] =
            actionEffects.foldLeft(predicateLayers)({ case (map, (p, newEntry)) => map.+((p, map.getOrElse(p, Nil) :+ newEntry)) })

          // return the reachable state
          ((reachableState, updatedPredicateLayer), toRemove :+ nonApplicable)
      })

    tasksToRemoveFromPaths
  }


  protected def filterPrimitivesH2(sortedPaths: Array[(Seq[Int], Set[Task])]): Seq[Set[Task]] = {
    // perform H2-style (i.e. look at all pairs of predicates) reachability on the given list of actions
    val simpleInitialState = initialPlan.init.schema.effectsAsPredicateBool collect { case (predicate, true) => predicate } toArray

    val h2InitialState = crossProduct(simpleInitialState, simpleInitialState) toSet

    val (_, tasksToRemoveFromPaths) = sortedPaths.foldLeft[((Set[(Predicate, Predicate)], Set[Predicate]), Seq[Set[Task]])](((h2InitialState, simpleInitialState.toSet), Nil))(
      {
        case (((h2State, h1State), toRemove), (path, tasks)) =>
          // debug output
          /*println("\n\nh2 State" + (h2State map {case (a,b) => "(" + a.name + "," + b.name + ")"} mkString " "))
          println("h1 State" + (h1State map {_.name} mkString " "))
          println("tasks: ")
          println(tasks map {case rt : ReducedTask =>
            rt.name + "\n\tprec: " + rt.precondition.conjuncts.map({_.predicate.name}) +
              "\n\tadd: " + rt.effect.conjuncts.filter(_.isPositive).map({_.predicate.name}) +
              "\n\tdel: " + rt.effect.conjuncts.filter(_.isNegative).map({_.predicate.name})
          } mkString "\n")
          */

          val (applicable, nonApplicable) = tasks partition { t =>
            if (t.isAbstract) false
            else {
              val positivePreconditions = t.preconditionsAsPredicateBool collect { case (predicate, true) => predicate } toArray
              val h2Preconditions = crossProduct(positivePreconditions, positivePreconditions)

              val nonHoldingPrecs = h2Preconditions filterNot h2State.contains

              //println("Missing Precondition for " + t.name  + ": " + (nonHoldingPrecs map {case (a,b) => "(" + a.name + "," + b.name + ")"} mkString " "))

              // the state must contain all h2 pairs
              h2Preconditions forall h2State.contains
            }
          }

          if (nonApplicable.nonEmpty)
            println("Found " + nonApplicable.size + " non applicable tasks at " + path + ". Still applicable " + applicable.size)

          // compute which h2 pairs are now reachable
          val newlyReachable: Set[(Predicate, Predicate)] = applicable flatMap { t =>
            val (adds, dels) = t.effectsAsPredicateBool partition { _._2 }
            val delSet = dels map { _._1 } toSet
            val positivePreconditions = t.preconditionsAsPredicateBool collect { case (predicate, true) => predicate } toArray

            val x: Seq[(Predicate, Predicate)] = adds flatMap { case (add, _) =>
              if (delSet contains add) Nil
              else {
                // if this action achieves both, then it is possible
                val byAdd: Seq[(Predicate, Predicate)] = adds collect { case (add2, _) if !(delSet contains add2) => (add, add2) }

                // if a predicate could be achieved on its one, then it can now together with all non-deleted things
                val byNonDel: Seq[(Predicate, Predicate)] = h1State.toSeq collect { case nondel if !(delSet contains nondel) &&
                  (positivePreconditions forall { prec => h2State contains(prec, nondel) })
                => (add, nondel) ::(nondel, add) :: Nil
                } flatten

                byAdd ++ byNonDel
              }
            }

            x
          }


          // apply tasks
          val h2reachableState = h2State ++ newlyReachable
          val h1reachableState = h1State ++ (applicable flatMap { _.effectsAsPredicateBool collect { case (predicate, true) => predicate } })

          // return the reachable state
          ((h2reachableState, h1reachableState), toRemove :+ nonApplicable)
      })

    tasksToRemoveFromPaths
  }

  protected def minimisePathDecompositionTree(pdt: PathDecompositionTree[Unit]): PathDecompositionTree[Unit] = reductionMethod match {
    case OnlyNormalise =>
      val dontRemovePrimitives: Seq[Set[Task]] = pdt.primitivePaths.toSeq map { _ => Set[Task]() }

      pdt.restrictPathDecompositionTree(dontRemovePrimitives).normalise
    case _             =>
      println("Round in minimisation")
      // get the primitive paths in the order they actually occur
      val sortedPaths = pdt.primitivePaths sortWith { case ((p1, _), (p2, _)) => PathBasedEncoding.pathSortingFunction(p1, p2) }

      val tasksToRemoveFromPaths: Seq[Set[Task]] = reductionMethod match {
        case FFReduction             => filterPrimitivesFF(sortedPaths, fullTest = false)
        case H2Reduction             => filterPrimitivesH2(sortedPaths)
        case FFReductionWithFullTest => filterPrimitivesFF(sortedPaths, fullTest = true)
      }

      val foundAnyTaskToRemove = tasksToRemoveFromPaths exists { _.nonEmpty }
      if (!foundAnyTaskToRemove) {
        // plot PDT to file
        //Dot2PdfCompiler.writeDotToFile(pdt.treeBelowAsGraph, "pdtN.pdf")

        pdt.normalise
      }
      else
        minimisePathDecompositionTree(pdt.restrictPathDecompositionTree(tasksToRemoveFromPaths))
  }

}

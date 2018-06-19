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

package de.uniulm.ki.panda3.efficient.domain.datastructures

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.util.{DirectedGraphWithAlgorithms, DirectedGraph}

import scala.collection.BitSet
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTaskSchemaTransitionGraph(domain: EfficientDomain) extends DirectedGraphWithAlgorithms[Int] {

  /** all tasks */
  override val vertices: Seq[Int] = domain.tasks.indices

  /** adjacency list of the graph */
  override val edges: Map[Int, Seq[Int]] = (vertices map { task => val decomposableInto = domain.taskToPossibleMethods(task) flatMap {
    case (method, _) =>
      val methodTasks = method.subPlan.planStepTasks
      methodTasks.slice(2, methodTasks.length)
  }
    (task, decomposableInto.toSeq.distinct)
  }).toMap


  private lazy val reversedCondensedTopologicalOrdering                         = condensation.topologicalOrdering.get.reverse
  private lazy val reversedCondensedTopologicalOrderingArray: Array[Array[Int]] = condensation.topologicalOrdering.get.reverse map { _.toArray } toArray


  lazy val taskCanSupportByDecomposition: Map[Int, Array[(Int, Boolean)]] = {
    (vertices map { task =>
      val buffer = new ArrayBuffer[(Int, Boolean)]()
      val reach = reachable(task) filterNot { _ == task }

      reach foreach { subTask => domain.tasks(subTask).effect foreach { eff => buffer append ((eff.predicate, eff.isPositive)) } }

      (task, buffer.toArray)
    }).toMap
  }

  lazy val mutexes: Array[Array[(Int, Int)]] = {
    val mutex = new Array[Array[(Int, Int)]](domain.tasks.length)
    val topOrd = condensation.topologicalOrdering.get.reverse

    def recomputeMutexesFor(task: Int): Boolean = {
      val methodTasks = domain.taskToPossibleMethods(task) map { _._1.subPlan.planStepTasks.drop(2) }
      val methodReachable = methodTasks map { ts => ts ++ (ts flatMap reachable) toSet }
      val potentialMutexes = reachable(task) flatMap { x => reachable(task) collect { case y if y > x => (x, y) } }

      val methodMutexes = methodReachable map { m =>
        m flatMap { t => mutex(t) filter { case (a, b) => m filter { _ != t } exists { ot => reachable(ot).contains(a) || reachable(ot).contains(b) } } }
      }
      val mutexesViaAnyMethod = methodMutexes map { mm =>
        mm filter { case (a, b) => methodMutexes.indices exists { mi => methodReachable(mi).contains(a) && methodReachable(mi).contains(b) && !methodMutexes(mi).contains((a, b)) } } toSet
      }

      val allMethodMutexes = mutexesViaAnyMethod flatten
      val actualMutexes = potentialMutexes filterNot { case (x, y) => methodReachable exists { m => m.contains(x) && m.contains(y) && !allMethodMutexes.contains(x, y) } }
      val allMutexes = (allMethodMutexes ++ actualMutexes).distinct

      val changed = mutex(task).length != allMutexes.length
      mutex(task) = allMutexes
      changed
    }

    topOrd foreach { scc =>
      scc foreach { t => mutex(t) = Array() }

      var changed = true
      // recomputed allowed tasks
      while (changed)
        changed = scc map recomputeMutexesFor forall { x => x }
    }

    mutex
  }


  def allowedTasksFromPrimitives(allowedPrimitives: BitSet): Array[Boolean] = {
    val allowed = new Array[Boolean](domain.tasks.length)

    def recomputeAllowed(task: Int): Boolean =
      if (domain.tasks(task).isPrimitive) {
        allowed(task) = allowedPrimitives contains task
        false
      } else {
        var newAllowed = false
        val methods = domain.taskToPossibleMethods(task)

        var i = 0
        while (i < methods.length && !newAllowed) {
          var allAllowed = true
          val containedTasks = methods(i)._1.subPlan.planStepTasks
          var j = 2 // don't look at init and goal
          while (j < containedTasks.length && allAllowed) {
            allAllowed &= allowed(containedTasks(j))
            j += 1
          }
          newAllowed |= allAllowed

          i += 1
        }

        val change = allowed(task) != newAllowed
        allowed(task) = newAllowed
        change
      }


    var sccIndex = 0
    while (sccIndex < reversedCondensedTopologicalOrderingArray.length) {
      val scc = reversedCondensedTopologicalOrderingArray(sccIndex)

      // set all elements as allowed by default
      var sccElement = 0
      while (sccElement < scc.length) {
        allowed(scc(sccElement)) = true
        sccElement += 1
      }

      var changed = true
      while (changed) {
        // reset
        changed = false

        sccElement = 0
        while (sccElement < scc.length) {
          val task = scc(sccElement)
          changed |= recomputeAllowed(task)
          sccElement += 1
        }
      }
      sccIndex += 1
    }

    allowed
  }


  def landMarkFromPrimitives(allowedPrimitives: BitSet)(allowed: Array[Boolean] = allowedTasksFromPrimitives(allowedPrimitives)): Array[BitSet] = {
    val landmarks = new Array[BitSet](domain.tasks.length)
    val topOrd = condensation.topologicalOrdering.get.reverse

    def recomputeLandMarksFor(task: Int): Boolean = {
      val subLandmarks = (domain.taskToPossibleMethods(task) map { _._1.subPlan.planStepTasks.drop(2) } filter { _ forall allowed }).flatten map landmarks
      val newLandmarks = (if (subLandmarks.isEmpty) BitSet() else subLandmarks.reduce[BitSet]({ case (a, b) => a intersect b })) + task
      val changed = if (newLandmarks.size != landmarks(task).size) true else false
      landmarks(task) = newLandmarks
      changed
    }

    topOrd foreach { scc =>
      scc foreach { t => landmarks(t) = BitSet(scc.toSeq: _*) }

      var changed = true
      while (changed)
        changed = scc map recomputeLandMarksFor exists { x => x }
    }

    landmarks
  }
}

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


  lazy val taskCanSupportByDecomposition: Map[Int, Array[(Int, Boolean)]] = {
    val startT = System.currentTimeMillis()
    (vertices map { task =>
      val buffer = new ArrayBuffer[(Int, Boolean)]()
      val reach = reachable(task) filterNot { _ == task }

      reach foreach { subTask => domain.tasks(subTask).effect foreach { eff => buffer append ((eff.predicate, eff.isPositive)) } }

      val arr = buffer.toArray
      //println("EFF " + arr.length)
      (task, arr)
    }).toMap
  }

  val mutexes: Array[Array[(Int, Int)]] = {
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


  def landMarkFromAndAllowedTasks(allowedPrimitives: BitSet): (Array[BitSet], Array[Boolean]) = {
    val landmarks = new Array[BitSet](domain.tasks.length)
    val allowed = new Array[Boolean](domain.tasks.length)
    val topOrd = condensation.topologicalOrdering.get.reverse

    def recomputeLandMarksFor(task: Int): Boolean = {
      val subLandmarks = (domain.taskToPossibleMethods(task) map { _._1.subPlan.planStepTasks.drop(2) } filter { _ forall allowed }).flatten map landmarks
      val newLandmarks = (if (subLandmarks.isEmpty) BitSet() else (subLandmarks reduce[BitSet] { case (a, b) => a intersect b })) + task
      val changed = if (newLandmarks.size != landmarks(task).size) true else false
      landmarks(task) = newLandmarks
      changed
    }

    def recomputeAllowed(task: Int): Boolean =
      if (domain.tasks(task).isPrimitive) {
        allowed(task) = allowedPrimitives contains task
        false
      } else {
        val newAllowed = domain.taskToPossibleMethods(task) exists { _._1.subPlan.planStepTasks.drop(2) forall allowed }
        val change = allowed(task) != newAllowed
        allowed(task) = newAllowed
        change
      }


    topOrd foreach { scc =>
      scc foreach { t => landmarks(t) = BitSet(scc.toSeq: _*) }
      scc foreach { t => allowed(t) = true }

      var changed = true
      // recomputed allowed tasks
      while (changed)
        changed = scc map recomputeAllowed forall { x => x }

      changed = true
      while (changed)
        changed = scc map recomputeLandMarksFor forall { x => x }
    }

    (landmarks, allowed)
  }
}
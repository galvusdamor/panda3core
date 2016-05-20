package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability

import de.uniulm.ki.panda3.symbolic.domain.datastructures.{GroundedPrimitiveReachabilityAnalysis, GroundedReachabilityAnalysis, LayeredGroundedPrimitiveReachabilityAnalysis}
import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

import collection.mutable.{HashMap, MultiMap}
import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraph(domain: Domain, initialState: Set[GroundLiteral], computeMutexes: Boolean, isSerial: Boolean, disallowedTasks: Either[Seq[GroundTask], Seq[Task]]) extends
  LayeredGroundedPrimitiveReachabilityAnalysis {


  lazy val graphSize: Int = ???
  val preconMap = new HashMap[Predicate, collection.mutable.Set[GroundLiteral]] with MultiMap[Predicate, GroundLiteral]


  // This function should compute the actual planning graph
  override protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {

    ???
  }

  protected lazy val layerWithMutexes: Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
    ???

    def buildLayer(layer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]), newPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)]): (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]) = {

    }

    def buildGraph(layer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]), newPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)]): Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
      fillPreconMap(newPropositions)
      val assignMap: Map[Variable, Constant] = Map()
      val newActions = newPropositions map { (gl: GroundLiteral) => { (domain.consumersOf.get(gl.predicate)) map { (task: ReducedTask) => createActionInstances(task, assignMap, gl, task.precondition.conjuncts) }  } }

      }
    }

    def createActionInstances(task: ReducedTask, gl: GroundLiteral) = {

    }

    def fillPreconMap(propositions: Set[GroundLiteral]): Unit = {
      propositions.foreach((p:GroundLiteral) => preconMap.addBinding(p.predicate, p) )
    }

    buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]), initialState, Set.empty[(GroundLiteral, GroundLiteral)])
  }
}

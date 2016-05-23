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

    def buildGraph(layer: (Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)]), newPropositions: Set[GroundLiteral], deletedMutexes: Set[(GroundLiteral, GroundLiteral)]): Seq[(Set[GroundTask], Set[(GroundTask, GroundTask)], Set[GroundLiteral], Set[(GroundLiteral, GroundLiteral)])] = {
      fillPreconMap(newPropositions)

      val assignMap: Map[Variable, Constant] = Map()
      //  Instantiate actions which become available because of the new propositions "union" deletion of mutexes
      val newActions: Set[GroundTask] = ((newPropositions ++ (deletedMutexes flatMap { (t: (GroundLiteral, GroundLiteral)) => Set(t._1, t._2) })) flatMap { (gl: GroundLiteral) => {
        domain.consumersOf.getOrElse(gl.predicate, Seq.empty[ReducedTask]) flatMap { (t: ReducedTask) => createActionInstances(t, assignMap, gl, (t.precondition.conjuncts find { (l: Literal) => l.predicate == gl.predicate }).get, t.precondition.conjuncts) }
      }
      })

      /*
     * TODO: Check the correctness for special cases.
     */
      def createActionInstances(task: ReducedTask, assignMap: Map[Variable, Constant], gl: GroundLiteral, l: Literal, precons: Seq[Literal]): Set[GroundTask] = {
        gl match {
          case null => Set.empty[GroundTask]
          case _ => {
            val correct: Boolean = ((l.parameterVariables zip gl.parameter) map { t: (Variable, Constant) => (assignMap.get(t._1) == t._2) }).foldLeft(false)((b1: Boolean, b2: Boolean) => b1 || b2)
            correct match {
              case true => {
                val updatedAssignMap = assignMap ++ (l.parameterVariables zip gl.parameter).toMap
                val updatedPrecons = (precons filterNot { (lit: Literal) => lit == l })
                updatedPrecons.size match {
                  case 0 => {
                    /*
                    * TODO: Convert the updatedAssignMap back to a Seq[Constant] in the correct order.
                    */
                    Set(GroundTask(task, ???))
                  }
                  case _ => (updatedPrecons flatMap { (lit: Literal) => preconMap.getOrElse(lit.predicate, Set.empty[GroundLiteral]) flatMap { (newgl: GroundLiteral) => createActionInstances(task, updatedAssignMap, newgl, lit, updatedPrecons) } }).toSet
                }
              }
              case false => Set.empty[GroundTask]
            }
          }
        }
      }

      def fillPreconMap(propositions: Set[GroundLiteral]): Unit = {
        propositions.foreach((p: GroundLiteral) => preconMap.addBinding(p.predicate, p))
      }

      buildGraph((Set.empty[GroundTask], Set.empty[(GroundTask, GroundTask)], initialState, Set.empty[(GroundLiteral, GroundLiteral)]), initialState, Set.empty[(GroundLiteral, GroundLiteral)])
    }
  }
}

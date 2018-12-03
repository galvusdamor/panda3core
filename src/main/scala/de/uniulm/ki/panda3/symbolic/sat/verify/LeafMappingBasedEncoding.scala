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

import de.uniulm.ki.panda3.symbolic.domain.Task

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait LeafMappingBasedEncoding extends VerifyEncoding{
  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

  protected def pathActive(p1: Seq[Int]) = "active!" + "_" + p1.mkString(";")

  protected def pathToPosWithTask(path: Seq[Int], position: Int, task: Task): String =
    "withTaskPathToPos_" + path.mkString(";") + "-" + position + ":" + taskIndex(task)

}

trait KautzSelmanMappingEncoding[Payload, IntermediatePayload] extends PathBasedEncoding[Payload, IntermediatePayload] with LeafMappingBasedEncoding with LinearPrimitivePlanEncoding{

  def stateTransitionFormulaProvider(): Seq[Clause] = {
    val invariantFormula = Range(0, taskSequenceLength + 1) flatMap { case position =>
      intProblem.symbolicInvariantArray map { case ((ap, ab), (bp, bb)) => Clause((statePredicate(K - 1, position, ap), ab) :: (statePredicate(K - 1, position, bp), bb) :: Nil) }
    }

    invariantFormula ++ stateTransitionFormulaOfLength(taskSequenceLength)
  }

  def restrictionPathsPerPosition(pathsPerPosition: Map[Int, Seq[(Int, Int, String)]]): Seq[Clause] =
    pathsPerPosition.toSeq flatMap { case (a, s) => atMostOneOf(s map { _._3 }) }

  def ifActionAtPositionThenConnected(actionAtoms: Seq[(String, Task)], pathsPerPosition: Map[Int, Seq[(Int, Int, String)]], position: Int): Seq[Clause] =
    atMostOneOf(actionAtoms map { _._1 })
}


trait ExsitsStepMappingEncoding[Payload, IntermediatePayload] extends PathBasedEncoding[Payload, IntermediatePayload] with LeafMappingBasedEncoding {
  def maxNumberOfActions : Int
  def additionalDisablingGraphEdges : Seq[AdditionalEdgesInDisablingGraph]

  val exsitsStepEncoding = ExistsStep(timeCapsule, domain, initialPlan, intProblem, taskSequenceLength, maxNumberOfActions, additionalDisablingGraphEdges, Some(K))

  lazy val taskOccurenceMap: Map[Int, Set[Task]] =
    domain.primitiveTasks map { t => t -> primitivePaths.count(_._2.contains(t)) } groupBy { _._2 } map { case (a, bs) => a -> bs.map(_._1).toSet }

  lazy val tasksWithOnePosition: Set[Task]           = taskOccurenceMap.getOrElse(1, Set[Task]())
  lazy val tasksOnePath        : Map[Task, Seq[Int]] = tasksWithOnePosition map { t => t -> primitivePaths.find(_._2.contains(t)).get._1 } toMap

  lazy val stateTransitionFormulaProvider: Seq[Clause] = exsitsStepEncoding.stateTransitionFormula

  def restrictionPathsPerPosition(pathsPerPosition: Map[Int, Seq[(Int, Int, String)]]): Seq[Clause] = {

    println(taskOccurenceMap map { case (a, bs) => "Occ: " + a + ": " + bs.size + " " + "actions" } mkString "\n")

    val taskToPosWithTask: Seq[Clause] = Range(0, taskSequenceLength) flatMap { case position =>
      pathsPerPosition(position) flatMap { case (pathIndex, _, connectionAtom) =>
        val path = primitivePaths(pathIndex)._1
        primitivePaths(pathIndex)._2 diff tasksWithOnePosition flatMap { t =>
          val actionAtom = pathAction(path.length, path, t)

          impliesSingle(pathToPosWithTask(path, position, t), actionAtom) ::
            impliesSingle(pathToPosWithTask(path, position, t), connectionAtom) ::
            impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, pathToPosWithTask(path, position, t)) :: Nil
        }
      }
    }

    taskToPosWithTask
  }

  def ifActionAtPositionThenConnected(actionAtoms: Seq[(String, Task)], pathsPerPosition: Map[Int, Seq[(Int, Int, String)]], position: Int): Seq[Clause] = {
    actionAtoms flatMap { case (atom, task) =>
      val possibleAchievers = pathsPerPosition(position) collect { case (pathIndex, _, _) if primitivePaths(pathIndex)._2 contains task =>
        pathToPosWithTask(primitivePaths(pathIndex)._1, position, task)
      }

      if (tasksWithOnePosition contains task) {
        assert(possibleAchievers.length == 1)
        val achieverPath = tasksOnePath(task)
        impliesSingle(atom, pathToPos(tasksOnePath(task), position)) :: impliesSingle(atom, pathAction(achieverPath.length, achieverPath, task)) :: Nil
      } else if (possibleAchievers.nonEmpty)
        atMostOneOf(possibleAchievers) :+ impliesRightOr(atom :: Nil, possibleAchievers)
      else Clause((atom, false)) :: Nil // if there is no achiever, this position can not be made true
    }
  }
}
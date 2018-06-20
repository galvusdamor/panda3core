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

import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import de.uniulm.ki.panda3.symbolic.sat.IntProblem
import de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser.{GreedyNumberOfAbstractChildrenOptimiser, GreedyNumberOfChildrenFromTotallyOrderedOptimiser}
import de.uniulm.ki.util._

import scala.collection.{Seq, mutable}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait TreeVariableOrderEncoding extends TreeEncoding with LinearPrimitivePlanEncoding {


  override val optimiser = GreedyNumberOfAbstractChildrenOptimiser
  //override val optimiser = GreedyNumberOfChildrenFromTotallyOrderedOptimiser

  override val numberOfChildrenClauses: Int = 0

  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

  protected def pathToPosWithTask(path: Seq[Int], position: Int, task: Task): String =
    "withTaskPathToPos_" + path.mkString(";") + "-" + position + ":" + taskIndex(task)

  protected def orderBefore(l: Int, p: Seq[Int], before: Int, after: Int) = {
    assert(p.length == l, p + " " + p.length + " " + l)
    "before!" + l + "_" + p.mkString(";") + "," + before + "<" + after
  }

  protected def pathActive(p1: Seq[Int]) = "active!" + "_" + p1.mkString(";")

  protected val orderFromCommonPath: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (pathAIndex, pathBIndex) =>
    val pathA = primitivePaths(pathAIndex)._1
    val pathB = primitivePaths(pathBIndex)._1
    val commonPath = pathA.zip(pathB) takeWhile { case (a, b) => a == b } map { _._1 }
    val beforeInMethod = pathA(commonPath.length)
    val afterInMethod = pathB(commonPath.length)

    orderBefore(commonPath.length, commonPath, beforeInMethod, afterInMethod)
                                                                                          })

  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, methodChildrenPositions: Map[Int, Int]): Seq[Clause] = {
    val orderings = method.subPlan.orderingConstraints.allOrderingConstraints() filterNot { _.containsAny(method.subPlan.initAndGoal: _*) }

    val orderingAtoms = orderings map { case OrderingConstraint(before, after) =>
      val beforeIndex = methodChildrenPositions(before.id)
      val afterIndex = methodChildrenPositions(after.id)

      orderBefore(layer, path, beforeIndex, afterIndex)
    }

    impliesRightAnd(methodString :: Nil, orderingAtoms)
  }

  def restrictionPathsPerPosition(pathsPerPosition: Map[Int, Seq[(Int, Int, String)]]): Seq[Clause]

  def ifActionAtPositionThenConnected(actionAtoms: Seq[(String, Task)], pathsPerPosition: Map[Int, Seq[(Int, Int, String)]], position: Int): Seq[Clause]

  override def stateTransitionFormula: Seq[Clause] = {
    println("TREE P:" + primitivePaths.length + " S: " + taskSequenceLength)
    // generate the formulas to connect the decomposition and the primitive part
    val pathAndPosition: Seq[(Int, Int, String)] =
      primitivePaths.zipWithIndex flatMap { case ((path, _), pindex) => Range(0, taskSequenceLength) map { position => (pindex, position, pathToPos(path, position)) } }

    val positionsPerPath: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._1 }
    val pathsPerPosition: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._2 }


    val atMostOneConstraints = restrictionPathsPerPosition(pathsPerPosition) ++ (positionsPerPath.toSeq flatMap { case (a, s) => atMostOneOf(s map { _._3 }) })
    println("A " + atMostOneConstraints.size)

    val selected = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val actionAtoms = tasks.toSeq map { pathAction(path.length, path, _) }
      val pathString = pathActive(path)
      notImpliesAllNot(pathString :: Nil, actionAtoms).+:(impliesRightOr(pathString :: Nil, actionAtoms))
    }
    println("B " + selected.length)

    val onlySelectableIfChosen = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val pathString = pathActive(path)
      notImpliesAllNot(pathString :: Nil, positionsPerPath(pindex) map { _._3 }) :+ impliesRightOr(pathString :: Nil, positionsPerPath(pindex) map { _._3 })
    }
    println("C " + onlySelectableIfChosen.length)

    val onlyPrimitiveIfChosen = Range(0, taskSequenceLength) flatMap { case position =>
      val actionAtoms = domain.primitiveTasks map { t => (action(K - 1, position, t), t) }
      val ifPresentConnected = ifActionAtPositionThenConnected(actionAtoms, pathsPerPosition, position)
      val onlyIfConnected = notImpliesAllNot(pathsPerPosition(position) map { _._3 }, actionAtoms map {_._1})

      ifPresentConnected ++ onlyIfConnected
    }
    println("D " + onlyPrimitiveIfChosen.length)

    val sameAction = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      tasks.toSeq map { t => (t, pathAction(path.length, path, t)) } flatMap { case (t, actionAtom) =>
        positionsPerPath(pindex) map { case (_, position, connectionAtom) =>
          impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, action(K - 1, position, t))
        }
      }
    }
    println("E " + sameAction.length)

    println("COMP: " + (taskSequenceLength * (taskSequenceLength + 1) / 2) * primitivePaths.length * (primitivePaths.length - 1))
    val orderingKept = Range(0, taskSequenceLength) flatMap { case positionBefore =>
      //println("BEF POS " + positionBefore)
      Range(positionBefore, taskSequenceLength) flatMap { case positionAfter =>
        //println("AFT POS " + positionAfter)
        pathsPerPosition(positionBefore) flatMap { case (beforeIndex, _, connectorBefore) =>
          pathsPerPosition(positionAfter) collect { case (afterIndex, _, connectorAfter) if beforeIndex != afterIndex =>
            impliesNot(connectorBefore :: connectorAfter :: Nil, orderFromCommonPath(afterIndex, beforeIndex))
          }
        }
      }
    }
    println("F " + orderingKept.length)

    //orderingKept

    stateTransitionFormulaProvider() ++ atMostOneConstraints ++ selected ++ onlySelectableIfChosen ++ onlyPrimitiveIfChosen ++ sameAction ++ orderingKept
  }

  def stateTransitionFormulaProvider(): Seq[Clause]

  override def noAbstractsFormula: Seq[Clause] = noAbstractsFormulaOfLength(taskSequenceLength)

  override def goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  override def givenActionsFormula: Seq[Clause] = ???

  override protected def initialPayload(possibleTasks: Set[Task], path: scala.Seq[Int]): Unit = ()

  override protected def combinePayloads(childrenPayload: scala.Seq[Unit], intermediate: Unit): Unit = ()

  override protected def minimisePathDecompositionTree(pdt: PathDecompositionTree[Unit]): PathDecompositionTree[Unit] = {
    val dontRemovePrimitives: Seq[Set[Task]] = pdt.primitivePaths.toSeq map { _ => Set[Task]() }

    pdt.restrictPathDecompositionTree(dontRemovePrimitives)
  }
}

case class TreeVariableOrderEncodingKautzSelman(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, intProblem : IntProblem,
                                                taskSequenceLengthQQ: Int, offsetToK: Int, overrideK: Option[Int] = None)
  extends TreeVariableOrderEncoding {

  override def stateTransitionFormulaProvider(): Seq[Clause] = stateTransitionFormulaOfLength(taskSequenceLength)


  lazy val taskSequenceLength: Int = primitivePaths.length

   override def restrictionPathsPerPosition(pathsPerPosition: Map[Int, Seq[(Int, Int, String)]]): Seq[Clause] =
    pathsPerPosition.toSeq flatMap { case (a, s) => atMostOneOf(s map { _._3 }) }

  def ifActionAtPositionThenConnected(actionAtoms: Seq[(String, Task)], pathsPerPosition: Map[Int, Seq[(Int, Int, String)]], position: Int): Seq[Clause] =
    atMostOneOf(actionAtoms map { _._1 })

}

case class TreeVariableOrderEncodingExistsStep(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan, intProblem : IntProblem,taskSequenceLengthQQ: Int, offsetToK: Int, overrideK: Option[Int] = None)
  extends TreeVariableOrderEncoding {

  // TODO: determine this size more intelligently
  lazy val taskSequenceLength: Int = Math.max(if (primitivePaths.length == 0) 0 else 1, primitivePaths.length - 0)

  val exsitsStepEncoding = ExistsStep(timeCapsule, domain, initialPlan, intProblem, taskSequenceLength, Nil)

  override def stateTransitionFormulaProvider(): Seq[Clause] = exsitsStepEncoding.stateTransitionFormula

  override def restrictionPathsPerPosition(pathsPerPosition: Map[Int, Seq[(Int, Int, String)]]): Seq[Clause] = {
    val taskToPosWithTask: Seq[Clause] = Range(0, taskSequenceLength) flatMap {case position =>
      pathsPerPosition(position) flatMap {case (pathIndex,_,connectionAtom) =>
        val path = primitivePaths(pathIndex)._1
        primitivePaths(pathIndex)._2 flatMap { t =>
          val actionAtom = pathAction(path.length,path,t)

          impliesSingle(pathToPosWithTask(path, position, t), actionAtom) ::
            impliesSingle(pathToPosWithTask(path, position, t), connectionAtom) ::
            impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, pathToPosWithTask(path, position, t)) :: Nil
        }
      }
    }

    taskToPosWithTask
  }

  override def ifActionAtPositionThenConnected(actionAtoms: Seq[(String, Task)], pathsPerPosition: Map[Int, Seq[(Int, Int, String)]], position: Int): Seq[Clause] = {
    actionAtoms flatMap { case (atom, task) =>
      val possibleAchievers = pathsPerPosition(position) collect { case (pathIndex, _, _) if primitivePaths(pathIndex)._2 contains task =>
        pathToPosWithTask(primitivePaths(pathIndex)._1, position, task)
      }

      if (possibleAchievers.nonEmpty)
        atMostOneOf(possibleAchievers) :+ impliesRightOr(atom :: Nil, possibleAchievers)
      else Nil
    }
  }

}




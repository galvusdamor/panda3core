package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.Task

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait NumberOfActionsRestrictionViaAutomaton[P, I] extends PathBasedEncoding[P, I] {

  def restrictionMethod: RestrictionMethod

  protected def actionsRemainingAtPath(path: Seq[Int], remaining: Int): String = "act_remain^" + path.mkString(";") + "_" + remaining

  protected def actionPositionChosen(path: Seq[Int], position: Int): String = "act_chosen^" + path.mkString(";") + "_" + position

  def numberOfActionsFormula(vertexOrder: Seq[(Seq[Int], Set[Task])]): Seq[Clause] = if (taskSequenceLength <= 0) Nil else {

    restrictionMethod match {
      case SlotOverTimeRestriction =>
        val notPossibleImpliesNextNotPossible = vertexOrder.zip(vertexOrder.drop(1)) flatMap { case ((path1, _), (path2, _)) =>
          Range(0, taskSequenceLength) map { l => notImpliesNot(actionsRemainingAtPath(path1, l) :: Nil, actionsRemainingAtPath(path2, l)) }
        }

        val nextNotPossible = vertexOrder.zip(vertexOrder.drop(1)) flatMap { case ((path1, _), (path2, _)) =>
          Range(0, taskSequenceLength) map { l => impliesNot(actionPositionChosen(path1, l) :: Nil, actionsRemainingAtPath(path2, l)) }
        }

        val currentPossible = vertexOrder flatMap { case (path, _) => Range(0, taskSequenceLength) map { l => impliesSingle(actionPositionChosen(path, l), actionsRemainingAtPath(path, l)) }
        }

        val selectSlot: Seq[Clause] = vertexOrder flatMap { case node@(path, tasks) =>
          val selected = Range(0, taskSequenceLength) map { l => actionPositionChosen(path, l) }
          tasks map { t => impliesRightOr(pathAction(path.length, path, t) :: Nil, selected) }
        }

        notPossibleImpliesNextNotPossible ++ selectSlot ++ nextNotPossible ++ currentPossible

      case SlotGloballyRestriction =>
        val selectSlot: Seq[Clause] = vertexOrder flatMap { case node@(path, tasks) =>
          val selected = Range(0, taskSequenceLength) map { l => actionPositionChosen(path, l) }
          tasks map { t => impliesRightOr(pathAction(path.length, path, t) :: Nil, selected) }
        }

        val currentPossible: Seq[Clause] = vertexOrder flatMap { case (thisPath, _) => Range(0, taskSequenceLength) flatMap { thisPosition =>
          vertexOrder collect { case (thatPath, _) if thisPath != thatPath =>
            impliesNot(actionPositionChosen(thisPath, thisPosition), actionPositionChosen(thatPath, thisPosition))
          }
        }
        }

        selectSlot ++ currentPossible
    }
  }
}

sealed trait RestrictionMethod

object SlotOverTimeRestriction extends RestrictionMethod

object SlotGloballyRestriction extends RestrictionMethod
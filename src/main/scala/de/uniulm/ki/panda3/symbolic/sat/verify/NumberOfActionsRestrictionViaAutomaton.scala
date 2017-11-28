package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.Task

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait NumberOfActionsRestrictionViaAutomaton[P,I] extends PathBasedEncoding[P,I] {

  protected def actionsRemainingAtPath(path: Seq[Int], remaining: Int): String = "act_remain^" + path.mkString(";") + "_" + remaining

  protected def actionPositionChosen(path: Seq[Int], position: Int): String = "act_chosen^" + path.mkString(";") + "_" + position

  def numberOfActionsFormula(vertexOrder : Seq[(Seq[Int], Set[Task])]): Seq[Clause] = if (taskSequenceLength <= 0) Nil else {

    /**/

    // we need a fixed total order on the leafs of the SOG ...
    //val vertexOrder = extendedSOG.topologicalOrdering.get.drop(1).dropRight(1)

    /*val notPossibleImpliesHigherNotPossible = vertexOrder flatMap { case node@(path, tasks) =>
      Range(1, taskSequenceLength + 1) map { l => notImpliesNot(actionsRemainingAtPath(path, l) :: Nil, actionsRemainingAtPath(path, l - 1)) }
    }*/

    val notPossibleImpliesNextNotPossible = vertexOrder.zip(vertexOrder.drop(1)) flatMap { case ((path1, _), (path2, _)) =>
      Range(0, taskSequenceLength) map { l => notImpliesNot(actionsRemainingAtPath(path1, l) :: Nil, actionsRemainingAtPath(path2, l)) }
    }

    val nextNotPossible = vertexOrder.zip(vertexOrder.drop(1)) flatMap { case ((path1, _), (path2, _)) =>
      Range(0, taskSequenceLength) map { l => impliesNot(actionPositionChosen(path1, l) :: Nil, actionsRemainingAtPath(path2, l)) }
    }

    val currentPossible = vertexOrder flatMap { case (path, _) => Range(0, taskSequenceLength) map { l => impliesSingle(actionPositionChosen(path, l), actionsRemainingAtPath(path, l)) } }

    val ifPresentPossible: Seq[Clause] = vertexOrder flatMap { case node@(path, tasks) =>
      val selected = Range(0, taskSequenceLength) map { l => actionPositionChosen(path, l) }
      tasks map { t => impliesRightOr(pathAction(path.length, path, t) :: Nil, selected) }
    }

    notPossibleImpliesNextNotPossible ++ ifPresentPossible ++ nextNotPossible ++ currentPossible
  }
}

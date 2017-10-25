package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser.{GreedyNumberOfAbstractChildrenOptimiser, GreedyNumberOfChildrenFromTotallyOrderedOptimiser}
import de.uniulm.ki.util._

import scala.collection.{Seq, mutable}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TreeVariableOrderEncoding(timeCapsule: TimeCapsule,
                                     domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int, offsetToK: Int, overrideK: Option[Int] = None)
  extends TreeEncoding with LinearPrimitivePlanEncoding {

  lazy val taskSequenceLength: Int = primitivePaths.length

  override val optimiser = GreedyNumberOfAbstractChildrenOptimiser
  //override val optimiser = GreedyNumberOfChildrenFromTotallyOrderedOptimiser

  override val numberOfChildrenClauses: Int = 0

  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

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

  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, methodChildrenPositions: Map[Int,Int]): Seq[Clause] = {
    val orderings = method.subPlan.orderingConstraints.allOrderingConstraints() filterNot { _.containsAny(method.subPlan.initAndGoal: _*) }

    val orderingAtoms = orderings map { case OrderingConstraint(before, after) =>
      val beforeIndex = methodChildrenPositions(before.id)
      val afterIndex = methodChildrenPositions(after.id)

      orderBefore(layer, path, beforeIndex, afterIndex)
    }

    impliesRightAnd(methodString :: Nil, orderingAtoms)
  }


  override def stateTransitionFormula: Seq[Clause] = {
    println("TREE P:" + primitivePaths.length + " S: " + taskSequenceLength)
    // generate the formulas to connect the decomposition and the primitive part
    val pathAndPosition: Seq[(Int, Int, String)] =
      primitivePaths.zipWithIndex flatMap { case ((path, _), pindex) => Range(0, taskSequenceLength) map { position => (pindex, position, pathToPos(path, position)) } }

    val positionsPerPath: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._1 }
    val pathsPerPosition: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._2 }

    val atMostOneConstraints = (positionsPerPath flatMap { case (a, s) => atMostOneOf(s map { _._3 }) }) ++ (pathsPerPosition flatMap { case (a, s) => atMostOneOf(s map { _._3 }) })
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
      val actionAtoms = domain.primitiveTasks map { action(K - 1, position, _) }
      val atMostOne = atMostOneOf(actionAtoms)
      val onlyIfConnected = notImpliesAllNot(pathsPerPosition(position) map { _._3 }, actionAtoms)

      atMostOne ++ onlyIfConnected
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

    stateTransitionFormulaOfLength(taskSequenceLength) ++ atMostOneConstraints ++ selected ++ onlySelectableIfChosen ++ onlyPrimitiveIfChosen ++ sameAction ++ orderingKept
  }

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
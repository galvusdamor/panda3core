package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Task, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import de.uniulm.ki.util._

import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TreeEncoding(domain: Domain, initialPlan: Plan, taskSequenceLength: Int, offsetToK: Int) extends PathBasedEncoding with LinearPrimitivePlanEncoding {
  override val numberOfChildrenClauses: Int = 0


  protected def orderBefore(l: Int, p: Seq[Int], before: Int, after: Int) = {
    assert(p.length == l + 1)
    "before!" + l + "_" + p.mkString(";") + "," + before + "<" + after
  }

  protected val orderFromCommonPath: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (pathAIndex, pathBIndex) =>
    val pathA = primitivePathArray(pathAIndex)._1
    val pathB = primitivePathArray(pathBIndex)._1
    val commonPath = pathA.zip(pathB) takeWhile { case (a, b) => a == b } map { _._1 }
    val beforeInMethod = pathA(commonPath.length)
    val afterInMethod = pathB(commonPath.length)

    orderBefore(commonPath.length - 1, commonPath, beforeInMethod, afterInMethod)
                                                                                          })

  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: scala.Seq[Task]): Seq[Clause] = {
    // assign each planStep a position in the list
    val planStepIndexMap = new mutable.HashMap[Int, Int]()

    method.subPlan.planStepsWithoutInitGoal.foldLeft(Map[Task, Int]())(
      {
        case (taskToCount, ps) =>
          val currentIndex = taskToCount.getOrElse(ps.schema, 0)
          planStepIndexMap(ps.id) = (taskOrdering.zipWithIndex filter { _._1 == ps.schema }) (currentIndex)._2

          taskToCount + (ps.schema -> (currentIndex + 1))
      })

    val orderings = method.subPlan.orderingConstraints.allOrderingConstraints() filterNot { _.containsAny(method.subPlan.initAndGoal: _*) }

    val orderingAtoms = orderings map { case OrderingConstraint(before, after) =>
      val beforeIndex = planStepIndexMap(before.id)
      val afterIndex = planStepIndexMap(after.id)

      orderBefore(layer, path, beforeIndex, afterIndex)
    }

    impliesRightAnd(methodString :: Nil, orderingAtoms)
  }

  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

  override def stateTransitionFormula: Seq[Clause] = {
    println("TREE P:" + primitivePaths.length + " S: " + taskSequenceLength)
    // generate the formulas to connect the decomposition and the primitive part
    val pathAndPosition: Seq[(Int, Int, String)] =
      primitivePaths.zipWithIndex flatMap { case ((path, _), pindex) => Range(0, taskSequenceLength) map { position => (pindex, position, pathToPos(path, position)) } }

    val positionsPerPath: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._1 }
    val pathsPerPosition: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._2 }

    val atMostOneConstraints = (positionsPerPath flatMap { case (a, s) => atMostOneOf(s map { _._3 }) }) ++ (pathsPerPosition flatMap { case (a, s) => atMostOneOf(s map { _._3 }) })
    println("A " + atMostOneConstraints.size)
    val onlySelectableIfChosen = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val actionAtoms = tasks.toSeq map { pathAction(K, path, _) }

      notImpliesAllNot(actionAtoms, positionsPerPath(pindex) map { _._3 })
    }
    println("B " + onlySelectableIfChosen.length)

    val onlyPrimitiveIfChosen = Range(0, taskSequenceLength) flatMap { case position =>
      val actionAtoms = domain.primitiveTasks map { action(K - 1, position, _) }
      val atMostOne = atMostOneOf(actionAtoms)
      val onlyIfConnected = notImpliesAllNot(pathsPerPosition(position) map { _._3 }, actionAtoms)

      atMostOne ++ onlyIfConnected
    }
    println("C " + onlyPrimitiveIfChosen.length)

    val sameAction = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      tasks.toSeq map { t => (t, pathAction(K, path, t)) } flatMap { case (t, actionAtom) =>
        positionsPerPath(pindex) map { case (_, position, connectionAtom) =>
          impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, action(K - 1, position, t))
        }
      }
    }
    println("D " + sameAction.length)

    println("COMP: " + (taskSequenceLength * (taskSequenceLength + 1) / 2) * primitivePaths.length * (primitivePaths.length - 1))
    println("COMP NEW: " + (taskSequenceLength * primitivePaths.length * (primitivePaths.length - 1)))
    val orderingKept = Range(0, taskSequenceLength) flatMap { case positionBefore =>
      println("BEF POS " + positionBefore)
      Range(positionBefore, taskSequenceLength) flatMap { case positionAfter =>
        //println("AFT POS " + positionAfter)
        pathsPerPosition(positionBefore) flatMap { case (beforeIndex, _, connectorBefore) =>
          pathsPerPosition(positionAfter) collect { case (afterIndex, _, connectorAfter) if beforeIndex != afterIndex =>
            impliesNot(connectorBefore :: connectorAfter :: Nil, orderFromCommonPath(afterIndex, beforeIndex))
          }
        }
      }
    }
    println("D " + orderingKept.length)

    stateTransitionFormulaOfLength(taskSequenceLength) ++ atMostOneConstraints ++ onlySelectableIfChosen ++ onlyPrimitiveIfChosen ++ sameAction ++ orderingKept
  }

  override def noAbstractsFormula: Seq[Clause] = noAbstractsFormulaOfLength(taskSequenceLength)

  override def goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  override def givenActionsFormula: Seq[Clause] = ???
}
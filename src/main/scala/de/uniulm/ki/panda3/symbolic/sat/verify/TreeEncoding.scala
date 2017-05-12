package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Task, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import de.uniulm.ki.util._

import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TreeEncoding(domain: Domain, initialPlan: Plan, taskSequenceLength: Int, offsetToK: Int, overrideK: Option[Int] = None)
  extends PathBasedEncoding[Unit, Unit] with LinearPrimitivePlanEncoding {
  override val numberOfChildrenClauses: Int = 0

  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

  protected def orderBefore(l: Int, p: Seq[Int], before: Int, after: Int) = {
    assert(p.length == l + 1)
    "before!" + l + "_" + p.mkString(";") + "," + before + "<" + after
  }

  protected def nextPath(p1: Seq[Int], p2: Seq[Int]) = "next!" + "_" + p1.mkString(";") + "_" + p2.mkString(";")

  protected def pathActive(p1: Seq[Int]) = "active!" + "_" + p1.mkString(";")

  protected val orderFromCommonPath: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (pathAIndex, pathBIndex) =>
    val pathA = primitivePaths(pathAIndex)._1
    val pathB = primitivePaths(pathBIndex)._1
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
      val actionAtoms = tasks.toSeq map { pathAction(path.length - 1, path, _) }
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
      tasks.toSeq map { t => (t, pathAction(path.length - 1, path, t)) } flatMap { case (t, actionAtom) =>
        positionsPerPath(pindex) map { case (_, position, connectionAtom) =>
          impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, action(K - 1, position, t))
        }
      }
    }
    println("E " + sameAction.length)

    // select a total order of the paths
    val nextPossible = primitivePathsOnlyPath.zipWithIndex flatMap { case (p1, p1i) => primitivePathsOnlyPath.zipWithIndex flatMap { case (p2, p2i) =>
      if (p1i != p2i) {
        val next = nextPath(p1, p2)
        impliesNot(next, orderFromCommonPath(p2i, p1i)) :: impliesSingle(next, pathActive(p1)) :: impliesSingle(next, pathActive(p2)) :: Nil
      } else Nil
    }
    }
    println("F " + nextPossible.length)

    val nextValid = primitivePathsOnlyPath.+:(Integer.MAX_VALUE :: Nil).+:(-1 :: Nil) flatMap { path =>
      val successor = primitivePaths.+:((Integer.MAX_VALUE :: Nil, Set[Task]())) collect { case (next, _) if next != path => nextPath(path, next) }
      val predecessor = primitivePaths.+:((-1 :: Nil, Set[Task]())) collect { case (next, _) if next != path => nextPath(next, path) }

      val atMostSuccessor: Seq[Clause] = if (path.head != Integer.MAX_VALUE) atMostOneOf(successor) else successor map { s => Clause((s, false)) }
      val atMostPredecessor: Seq[Clause] = if (path.head != -1) atMostOneOf(predecessor) else predecessor map { s => Clause((s, false)) }
      val activeCheck: Seq[Clause] =
        if (path.head != Integer.MAX_VALUE && path.head != -1) impliesRightOr(pathActive(path) :: Nil, successor) :: impliesRightOr(pathActive(path) :: Nil, predecessor) :: Nil else Nil

      atMostSuccessor ++ atMostPredecessor ++ activeCheck
    }
    println("G " + nextValid.length)

    val primitivesOrder = primitivePathsOnlyPath.zipWithIndex flatMap { case (p1, p1i) => primitivePathsOnlyPath.zipWithIndex flatMap { case (p2, p2i) =>
      if (p1i != p2i) {
        Range(0, taskSequenceLength - 1) flatMap { case pos =>
          val thisConnection = pathToPos(p1, pos)
          val next = nextPath(p1, p2)
          val nextConnection = pathToPos(p2, pos + 1)

          impliesRightAndSingle(thisConnection :: next :: Nil, nextConnection) :: impliesRightAndSingle(nextConnection :: next :: Nil, thisConnection) :: Nil
        }
      } else Nil
    }
    }
    println("H " + primitivesOrder.length)



    println("COMP: " + (taskSequenceLength * (taskSequenceLength + 1) / 2) * primitivePaths.length * (primitivePaths.length - 1))
    /*val orderingKept = Range(0, taskSequenceLength) flatMap { case positionBefore =>
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
    println("D " + orderingKept.length)*/

    //orderingKept

    stateTransitionFormulaOfLength(taskSequenceLength) ++ atMostOneConstraints ++ selected ++ onlySelectableIfChosen ++ onlyPrimitiveIfChosen ++ sameAction ++ nextPossible ++ nextValid ++
      primitivesOrder
  }

  override def noAbstractsFormula: Seq[Clause] = noAbstractsFormulaOfLength(taskSequenceLength)

  override def goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  override def givenActionsFormula: Seq[Clause] = ???

  override protected def initialPayload(possibleTasks: Set[Task], path: scala.Seq[Int]): Unit = ()

  override protected def combinePayloads(childrenPayload: scala.Seq[Unit], intermediate: Unit): Unit = ()

  override protected def computeTaskSequenceArrangement(possibleMethods: Array[DecompositionMethod],
                                                        possiblePrimitives: scala.Seq[Task]): (Array[Array[Int]], Array[Int], Array[Set[Task]], Unit) = ???

  override protected def minimisePathDecompositionTree(pdt: PathDecompositionTree[Unit]): PathDecompositionTree[Unit] = pdt
}
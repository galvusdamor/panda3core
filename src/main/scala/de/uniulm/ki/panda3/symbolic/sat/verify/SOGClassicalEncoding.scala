package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.{DirectedGraph, DirectedGraphDotOptions, Dot2PdfCompiler, TimeCapsule}

import scala.collection.{Seq, mutable}


trait SOGClassicalEncoding extends SOGEncoding {

  lazy val taskSequenceLength: Int = primitivePaths.length
  //lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

  protected def pathActive(p1: Seq[Int]) = "active!" + "_" + p1.mkString(";")

  override lazy val noAbstractsFormula: Seq[Clause] = noAbstractsFormulaOfLength(taskSequenceLength)

  protected lazy val connectionFormula: Seq[Clause] = {
    // force computation of SOG
    sog

    //////
    // select mapping
    /////

    val pathAndPosition: Seq[(Int, Int, String)] =
      primitivePaths.zipWithIndex flatMap { case ((path, _), pindex) => Range(0, taskSequenceLength) map { position => (pindex, position, pathToPos(path, position)) } }

    val positionsPerPath: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._1 }
    val pathsPerPosition: Map[Int, Seq[(Int, Int, String)]] = pathAndPosition groupBy { _._2 }

    // each position can be mapped to at most one path and vice versa
    val atMostOneConstraints = (positionsPerPath flatMap { case (a, s) => atMostOneOf(s map { _._3 }) }) ++ (pathsPerPosition flatMap { case (a, s) => atMostOneOf(s map { _._3 }) })
    println("A " + atMostOneConstraints.size)

    // if the path is part of a solution, then it must contain a task
    val selected = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val actionAtoms = tasks.toSeq map { pathAction(path.length, path, _) }
      val pathString = pathActive(path)
      notImpliesAllNot(pathString :: Nil, actionAtoms).+:(impliesRightOr(pathString :: Nil, actionAtoms))
    }
    println("B " + selected.length)

    // if a path contains an action it has to be mapped to a position
    val onlySelectableIfChosen = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val pathString = pathActive(path)
      notImpliesAllNot(pathString :: Nil, positionsPerPath(pindex) map { _._3 }) :+ impliesRightOr(pathString :: Nil, positionsPerPath(pindex) map { _._3 })
    }
    println("C " + onlySelectableIfChosen.length)

    // positions may only contain primitive tasks is mapped to a path
    val onlyPrimitiveIfChosen = Range(0, taskSequenceLength) flatMap { case position =>
      val actionAtoms = domain.primitiveTasks map { action(K - 1, position, _) }
      val atMostOne = atMostOneOf(actionAtoms)
      val onlyIfConnected = notImpliesAllNot(pathsPerPosition(position) map { _._3 }, actionAtoms)

      atMostOne ++ onlyIfConnected
    }
    println("D " + onlyPrimitiveIfChosen.length)

    // if a path contain an action, then the position it is mapped to contains the same action
    val sameAction = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      tasks.toSeq map { t => (t, pathAction(path.length, path, t)) } flatMap { case (t, actionAtom) =>
        positionsPerPath(pindex) map { case (_, position, connectionAtom) =>
          impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, action(K - 1, position, t))
        }
      }
    }
    println("E " + sameAction.length)

    val connection = atMostOneConstraints ++ selected ++ onlySelectableIfChosen ++ onlyPrimitiveIfChosen ++ sameAction

    connection.toSeq
  }
}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGClassicalForbiddenEncoding(timeCapsule: TimeCapsule,
                                         domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int, offsetToK: Int, overrideK: Option[Int] = None,
                                         useImplicationForbiddenness: Boolean) extends SOGClassicalEncoding {

  protected def pathPosForbidden(path: Seq[Int], position: Int): String = "forbidden_" + path.mkString(";") + "-" + position

  override lazy val stateTransitionFormula: Seq[Clause] = {
    // force computation of SOG
    sog

    /////////////////
    // forbid certain connections if disallowed by the SOG
    /////////////////
    val forbiddenConnections: Seq[Clause] = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val successors = if (useImplicationForbiddenness) sog.reachable.find(_._1._1 == path).get._2.toSeq else sog.edges.find(_._1._1 == path).get._2

      // start from 1 as we have to access the predecessor position
      Range(1, taskSequenceLength) flatMap { pos =>
        impliesRightAnd(pathToPos(path, pos) :: Nil, successors map { case (succP, _) => pathPosForbidden(succP, pos - 1) })
      }
    }
    println("F " + forbiddenConnections.length)

    val forbiddennessImplications: Seq[Clause] = if (useImplicationForbiddenness) Nil
    else primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val successors = if (useImplicationForbiddenness) sog.reachable.find(_._1._1 == path).get._2.toSeq else sog.edges.find(_._1._1 == path).get._2

      // start from 1 as we have to access the predecessor position
      Range(1, taskSequenceLength) flatMap { pos =>
        impliesRightAnd(pathPosForbidden(path, pos) :: Nil, successors map { case (succP, _) => pathPosForbidden(succP, pos) })
      }
    }
    println("G " + forbiddennessImplications.length)


    val forbiddennessGetsInherited: Seq[Clause] = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      Range(1, taskSequenceLength) map { pos => impliesSingle(pathPosForbidden(path, pos), pathPosForbidden(path, pos - 1)) }
    }
    println("H " + forbiddennessGetsInherited.length)

    val forbiddenActuallyDoesSomething = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      Range(0, taskSequenceLength) map { pos => impliesNot(pathPosForbidden(path, pos), pathToPos(path, pos)) }
    }
    println("I " + forbiddenActuallyDoesSomething.length)

    val forbiddenness = forbiddenConnections ++ forbiddennessImplications ++ forbiddennessGetsInherited ++ forbiddenActuallyDoesSomething


    //System exit 0

    // this generates the actual state transition formula
    val primitiveSequence = stateTransitionFormulaOfLength(taskSequenceLength)

    primitiveSequence ++ connectionFormula ++ forbiddenness
  }

}


case class SOGClassicalN4Encoding(timeCapsule: TimeCapsule,
                                  domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int, offsetToK: Int, overrideK: Option[Int] = None) extends SOGClassicalEncoding {

  override lazy val stateTransitionFormula: Seq[Clause] = {
    // force computation of SOG
    sog

    /////////////////
    // forbid certain connections if disallowed by the SOG
    /////////////////
    val forbiddenness: Array[Clause] = {
      val builder = new mutable.ArrayBuffer[Clause]()

      primitivePaths foreach { case (pathBefore, _) =>
        val successors = sog.reachable.find(_._1._1 == pathBefore).get._2.toSeq

        successors foreach { case (pathAfter, _) =>
          // all positions
          Range(0, taskSequenceLength) foreach { case position1 =>
            Range(position1 + 1, taskSequenceLength) foreach { case position2 =>
              builder append Clause((pathToPos(pathBefore, position2), false) :: (pathToPos(pathAfter, position1), false) :: Nil)
            }
          }
        }
      }
      builder.toArray
    }


    //System exit 0

    // this generates the actual state transition formula
    val primitiveSequence = stateTransitionFormulaOfLength(taskSequenceLength)

    primitiveSequence ++ connectionFormula ++ forbiddenness
  }

}


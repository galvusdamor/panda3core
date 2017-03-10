package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{Task, DecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser.{GreedyNumberOfAbstractChildrenOptimiser, OptimalBranchAndBoundOptimiser}
import de.uniulm.ki.util.{DirectedGraphDotOptions, Dot2PdfCompiler, SimpleDirectedGraph, DirectedGraph}

import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGEncoding(domain: Domain, initialPlan: Plan, taskSequenceLengthQQ: Int, offsetToK: Int, overrideK: Option[Int] = None)
  extends PathBasedEncoding[SOG, NonExpandedSOG] with LinearPrimitivePlanEncoding {

  //lazy val taskSequenceLength: Int = primitivePaths.length
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  private final val useImplicationForbiddenness = false

  assert(initialPlan.planStepsWithoutInitGoal.length == 1, "This formula is only correct if the initial plan has been replaced by an artificial top task")

  // this is only needed in the tree encoding
  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: Seq[Task]): Seq[Clause] = Nil


  protected def pathToPos(path: Seq[Int], position: Int): String = "pathToPos_" + path.mkString(";") + "-" + position

  protected def pathPosForbidden(path: Seq[Int], position: Int): String = "forbidden_" + path.mkString(";") + "-" + position

  protected def pathActive(p1: Seq[Int]) = "active!" + "_" + p1.mkString(";")


  override lazy val stateTransitionFormula: Seq[Clause] = {
    val paths = primitivePathArray
    assert(rootPayloads.length == 1)
    val sog = rootPayloads.head.ordering.transitiveReduction

    println(sog.isAcyclic)

    /*val string = sog.dotString(options = DirectedGraphDotOptions(),
                               //nodeRenderer = {case (path, tasks) => tasks map { _.name } mkString ","})
                               nodeRenderer = {case (path, tasks) => tasks.count(_.isPrimitive) + " " + path})
    Dot2PdfCompiler.writeDotToFile(string, "sog.pdf")*/

    println("TREE P:" + primitivePaths.length + " S: " + taskSequenceLength)

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
      val actionAtoms = tasks.toSeq map { pathAction(path.length - 1, path, _) }
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
      tasks.toSeq map { t => (t, pathAction(path.length - 1, path, t)) } flatMap { case (t, actionAtom) =>
        positionsPerPath(pindex) map { case (_, position, connectionAtom) =>
          impliesRightAndSingle(actionAtom :: connectionAtom :: Nil, action(K - 1, position, t))
        }
      }
    }
    println("E " + sameAction.length)

    val connection = atMostOneConstraints ++ selected ++ onlySelectableIfChosen ++ onlyPrimitiveIfChosen ++ sameAction

    /////////////////
    // forbid certain connections if disallowed by the SOG
    /////////////////
    val forbiddenConnections: Seq[Clause] = primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val successors = if (useImplicationForbiddenness) sog.reachable((path, tasks)).toSeq else sog.edges((path, tasks))

      // start from 1 as we have to access the predecessor position
      Range(1, taskSequenceLength) flatMap { pos =>
        impliesRightAnd(pathToPos(path, pos) :: Nil, successors map { case (succP, _) => pathPosForbidden(succP, pos - 1) })
      }
    }
    println("F " + forbiddenConnections.length)

    val forbiddennessImplications: Seq[Clause] = if (useImplicationForbiddenness) Nil
    else primitivePaths.zipWithIndex flatMap { case ((path, tasks), pindex) =>
      val successors = if (useImplicationForbiddenness) sog.reachable((path, tasks)).toSeq else sog.edges((path, tasks))

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

    primitiveSequence ++ connection ++ forbiddenness
  }

  override lazy val noAbstractsFormula: Seq[Clause] = noAbstractsFormulaOfLength(taskSequenceLength)

  override lazy val goalState: Seq[Clause] = goalStateOfLength(taskSequenceLength)

  //TODO: is this correct?
  override val numberOfChildrenClauses: Int = 0

  // TODO: we don't support this (yet), maybe N. Dönhoff will do it
  override def givenActionsFormula: Seq[Clause] = ???

  override protected def initialPayload(possibleTasks: Set[Task], path: Seq[Int]): SOG = SOG(SimpleDirectedGraph((path, possibleTasks) :: Nil, Nil))


  def minimiseAbstractTaskOccurencesMetric(g: DirectedGraph[Int], mapping: Seq[Map[PlanStep, Int]]): Int = if (g.vertices.isEmpty) 0
  else {
    val abstractTasks = g.vertices map { _ => new mutable.HashSet[Task]() } toArray

    mapping foreach { m => m filter { _._1.schema.isAbstract } foreach { case (ps, i) => abstractTasks(i) add ps.schema } }

    abstractTasks map { _.size } sum
  }

  def minimiseChildrenWithAbstractTasks(g: DirectedGraph[Int], mapping: Seq[Map[PlanStep, Int]]): Int = if (g.vertices.isEmpty) 0
  else {
    val abstractTasks = g.vertices map { _ => new mutable.HashSet[Task]() } toArray

    mapping foreach { m => m filter { _._1.schema.isAbstract } foreach { case (ps, i) => abstractTasks(i) add ps.schema } }

    (abstractTasks count { _.nonEmpty }) * 1000 + g.vertices.length
  }


  protected def computeTaskSequenceArrangement(possibleMethods: Array[DecompositionMethod], possiblePrimitives: Seq[Task]):
  (Array[Array[Int]], Array[Int], Array[Set[Task]], NonExpandedSOG) = {
    val methodTaskGraphs = (possibleMethods map { _.subPlan.orderingConstraints.fullGraph }) ++ (
      possiblePrimitives map { t => SimpleDirectedGraph(PlanStep(-1, t, Nil) :: Nil, Nil) })

    // TODO we are currently mapping plansteps, maybe we should prefer plansteps with identical tasks to be mapped together
    print("MINI " + possibleMethods.length + " " + possiblePrimitives.length + " ... ")
    val lb = methodTaskGraphs map { _.vertices count { _.schema.isAbstract } } max
    val optimiser =
      //OptimalBranchAndBoundOptimiser(minimiseChildrenWithAbstractTasks, lowerBound = lb) //, minimiseAbstractTaskOccurencesMetric)
      GreedyNumberOfAbstractChildrenOptimiser

    val g = optimiser.minimalSOG(methodTaskGraphs)
    //val met = minimiseChildrenWithAbstractTasks(g._1,g._2)

    //val check = OptimalBranchAndBoundOptimiser(minimiseChildrenWithAbstractTasks, lowerBound = lb).minimalSOG(methodTaskGraphs)
    //val metOp = minimiseChildrenWithAbstractTasks(check._1,check._2)

    println("done")
    //println("OP " + met + " of " + metOp)
    val minimalSuperGraph = g._1
    val planStepToIndexMappings: Seq[Map[PlanStep, Int]] = g._2

    val (methodMappings, primitiveMappings) = planStepToIndexMappings.splitAt(possibleMethods.length)

    val childrenIndicesToPossibleTasks = minimalSuperGraph.vertices map { _ => new mutable.HashSet[Task]() }

    val tasksPerMethodToChildrenMapping = methodMappings.zipWithIndex map { case (mapping, methodIndex) =>
      val methodPlanSteps = possibleMethods(methodIndex).subPlan.planStepsWithoutInitGoal
      (methodPlanSteps map { ps =>
        childrenIndicesToPossibleTasks(mapping(ps)) add ps.schema
        mapping(ps)
      }).toArray
    } toArray

    val childrenForPrimitives = primitiveMappings.zipWithIndex map { case (mapping, primitiveIndex) =>
      assert(mapping.size == 1)
      childrenIndicesToPossibleTasks(mapping.head._2) add mapping.head._1.schema
      mapping.head._2
    } toArray

    //println("\n\nGraph minisation")
    //println(childrenIndicesToPossibleTasks map {s => s map {t => t.name + " " + t.isAbstract} mkString " "} mkString "\n")

    val maxVertex = minimalSuperGraph.vertices.max
    assert(minimalSuperGraph.vertices.length - 1 == maxVertex, "SOG has " + minimalSuperGraph.vertices.length + " vertices, but maximum vertex is " + maxVertex)

    (tasksPerMethodToChildrenMapping, childrenForPrimitives, childrenIndicesToPossibleTasks map { _.toSet } toArray, NonExpandedSOG(minimalSuperGraph))
  }

  protected def combinePayloads(childrenPayload: Seq[SOG], intermediate: NonExpandedSOG): SOG = {
    val vertices = childrenPayload flatMap { _.ordering.vertices }
    val internalEdges = childrenPayload flatMap { _.ordering.edgeList }

    val connectingEdges = intermediate.ordering.edgeList flatMap { case (from, to) =>
      val fromVertices = childrenPayload(from).ordering.vertices
      val toVertices = childrenPayload(to).ordering.vertices

      for (x <- fromVertices; y <- toVertices) yield (x, y)
    }

    SOG(SimpleDirectedGraph(vertices, internalEdges ++ connectingEdges))
  }
}

case class NonExpandedSOG(ordering: DirectedGraph[Int])

case class SOG(ordering: DirectedGraph[(Seq[Int], Set[Task])]) {}
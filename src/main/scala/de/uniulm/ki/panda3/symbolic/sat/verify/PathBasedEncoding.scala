package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Task}
import de.uniulm.ki.util._

import scala.annotation.elidable
import scala.annotation.elidable._
import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait PathBasedEncoding[Payload, IntermediatePayload] extends VerifyEncoding {

  // layer, path to that action, actual task
  /*private val action: ((Int, Seq[Int], Task)) => String = memoise[(Int, Seq[Int], Task), String]({ case (l, p, t) =>
    assert(p.length == l + 1)
    "action^" + l + "_" + p.mkString(";") + "," + taskIndex(t)
                                                                                                 })*/

  protected def pathAction(l: Int, p: Seq[Int], t: Task) = {
    assert(p.length - 1 == l)
    "action!" + l + "_" + p.mkString(";") + "," + taskIndex(t)
  }

  // layer, path to that action, actual method
  protected val method: ((Int, Seq[Int], Int)) => String = memoise[(Int, Seq[Int], Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos.mkString(";") + "," + methodIdx })


  ///// methods to make the formula generation configurable

  protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: Seq[Task]): Seq[Clause]

  protected def initialPayload(possibleTasks: Set[Task], path: Seq[Int]): Payload

  protected def combinePayloads(childrenPayload: Seq[Payload], intermediate: IntermediatePayload): Payload

  protected def computeTaskSequenceArrangement(possibleMethods: Array[DecompositionMethod], possiblePrimitives: Seq[Task]):
  (Array[Array[Int]], Array[Int], Array[Set[Task]], IntermediatePayload)


  // returns all clauses needed for the decomposition and all paths to the last layer
  private def generateDecompositionFormula(tree: PathDecompositionTree[Payload]): (Seq[Clause], Set[(Seq[Int], Set[Task])]) = {
    val possibleTasks = tree.possibleTasks.toSeq
    val layer = tree.layer
    val path = tree.path

    //println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTasksToActions: Array[String] = possibleTasks map { t => pathAction(layer, path, t) } toArray

    // write myself
    val possibleTasksClauses: Seq[Clause] = atMostOneOf(possibleTasksToActions)

    if (layer == K || (possibleTasks forall { _.isPrimitive })) {
      //val notAnyNonPossibleTask = domain.tasks filterNot possibleTasks map { action(layer, path, _) } map { a => Clause((a, false) :: Nil) }
      //(possibleTasksClauses ++ notAnyNonPossibleTask, Set((path, possibleTasks)))
      //println("Terminal path with " + possibleTasks.size + " tasks")
      (possibleTasksClauses, Set((path, tree.possibleTasks)))
    } else {
      val (possibleAbstracts, possiblePrimitives) = possibleTasks.zipWithIndex partition { _._1.isAbstract }

      // compute per position all possible child tasks
      /*val possibleTasksPerChildPosition = new Array[mutable.Set[Task]](domain.maximumMethodSize)
      Range(0, domain.maximumMethodSize) foreach { possibleTasksPerChildPosition(_) = new mutable.HashSet[Task]() }
      possiblePrimitives foreach { case (primitive, _) => possibleTasksPerChildPosition(0) += primitive }*/
      val possibleMethods: Array[(DecompositionMethod, Int)] = possibleAbstracts flatMap { case (abstractTask, _) => domain.methodsWithIndexForAbstractTasks(abstractTask) } toArray


      val methodToPositions = tree.methodToPositions
      val primitivePositions = tree.primitivePositions
      val numberOfChildren = tree.children.length

      val possibleChildTasks: Array[Map[Task, String]] = Range(0, numberOfChildren) map { npos =>
        val npath = path :+ npos
        domain.tasks map { t => t -> pathAction(layer + 1, npath, t) } toMap
      } toArray


      ///////////////////////////////
      // recursive calls
      ///////////////////////////////
      // run recursive decent to get the formula for the downwards layers
      val subTreeResults = tree.children map { case child => generateDecompositionFormula(child) }

      val recursiveFormula = subTreeResults flatMap { _._1 }
      val primitivePaths = subTreeResults flatMap { _._2 } toSet


      ///////////////////////////////
      // primitives must be inherited
      ///////////////////////////////
      val keepPrimitives = primitivePositions.zipWithIndex flatMap { case (primitivePosition, primitiveIndexOnPrimitiveList) =>

        // put the primitive where we are told to put it and don't allow anything else
        val otherActions = Range(0, numberOfChildren) filter { _ != primitivePosition } flatMap { i => tree.children(i).possibleTasks map { task => possibleChildTasks(i)(task) } }

        val globalIndexOfPrimitive = possiblePrimitives(primitiveIndexOnPrimitiveList)._2
        val task = possiblePrimitives(primitiveIndexOnPrimitiveList)._1

        impliesRightAnd(possibleTasksToActions(globalIndexOfPrimitive) :: Nil, possibleChildTasks(primitivePosition)(task) :: Nil) ++
          impliesAllNot(possibleTasksToActions(globalIndexOfPrimitive), otherActions)
      }


      ///////////////////////////////
      // decomposition
      ///////////////////////////////
      // 1. part: select method if necessary
      val decomposeAbstract: Seq[Clause] = possibleAbstracts flatMap { case (abstractTask, abstractIndex) =>
        // select a method
        val possibleMethods = domain.methodsWithIndexForAbstractTasks(abstractTask) map { case (m, idx) => (m, method(layer, path, idx)) }

        // one method must be applied
        val oneMustBeApplied = impliesRightOr(possibleTasksToActions(abstractIndex) :: Nil, possibleMethods map { _._2 })
        val atMostOneCanBeApplied = atMostOneOf(possibleMethods map { _._2 })

        atMostOneCanBeApplied :+ oneMustBeApplied
      }



      // 2. part: determine children according to selected method
      val methodChildren = methodToPositions.indices flatMap { case methodIndexOnApplicableMethods =>
        val decompositionMethod = possibleMethods(methodIndexOnApplicableMethods)._1
        val methodIndex = possibleMethods(methodIndexOnApplicableMethods)._2
        val methodTasks = decompositionMethod.subPlan.planStepsWithoutInitGoal map { _.schema } toArray


        val usedPositions = new mutable.BitSet()
        val childAtoms: Seq[String] = methodToPositions(methodIndexOnApplicableMethods).zipWithIndex map {
          case (childPosition, childIndex) =>
            usedPositions add childPosition
            possibleChildTasks(childPosition)(methodTasks(childIndex))
        }
        val unusedActions = Range(0, numberOfChildren) filterNot usedPositions flatMap { case index => tree.children(index).possibleTasks map { task => possibleChildTasks(index)(task) } }

        val methodToken = method(layer, path, methodIndex)


        impliesRightAnd(methodToken :: Nil, childAtoms) ++ impliesAllNot(methodToken, unusedActions) ++
          additionalClausesForMethod(layer, path, decompositionMethod, methodToken, null) // TODO: This was originally taskOrdering)
      }

      // if there is nothing select at the current position we are not allowed to create new tasks
      val noneApplied = {
        val allChildren = Range(0, numberOfChildren) flatMap { index => tree.children(index).possibleTasks map { task => possibleChildTasks(index)(task) } }

        notImpliesAllNot(possibleTasksToActions, allChildren)
      }

      (possibleTasksClauses ++ keepPrimitives ++ decomposeAbstract ++ noneApplied ++ methodChildren ++ recursiveFormula, primitivePaths)
    }
  }


  protected def generatePathDecompositionTree(path: Seq[Int], possibleTasks: Set[Task]): PathDecompositionTree[Payload] = {
    //println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTaskOrder = possibleTasks.toSeq
    val layer = path.length - 1

    if (layer == K || (possibleTaskOrder forall { _.isPrimitive })) {
      //println("Terminal path with " + possibleTasks.size + " tasks")
      PathDecompositionTree(path, possibleTasks, Array(), Array(), initialPayload(possibleTasks, path), Array())
    } else {
      val (possibleAbstracts, possiblePrimitives) = possibleTaskOrder.zipWithIndex partition { _._1.isAbstract }

      val possibleMethods: Array[(DecompositionMethod, Int)] = possibleAbstracts flatMap { case (abstractTask, _) => domain.methodsWithIndexForAbstractTasks(abstractTask) } toArray


      val (methodToPositions, primitivePositions, positionsToPossibleTasks, intermediatePayload) =
        computeTaskSequenceArrangement(possibleMethods map { _._1 }, possiblePrimitives map { _._1 })
      val numberOfChildren = positionsToPossibleTasks.length

      ///////////////////////////////
      // recursive calls
      ///////////////////////////////
      // run recursive decent to get the formula for the downwards layers
      val subTreeResults: Array[PathDecompositionTree[Payload]] = positionsToPossibleTasks.zipWithIndex filter { _._1.nonEmpty } map { case (childPossibleTasks, index) =>
        generatePathDecompositionTree(path :+ index, childPossibleTasks.toSet)
      }

      val myPayload = combinePayloads(subTreeResults map { _.payload }, intermediatePayload)


      ///////////////////////////////
      PathDecompositionTree(path, possibleTasks, methodToPositions, primitivePositions, myPayload, subTreeResults)
    }
  }


  lazy val (computedDecompositionFormula, primitivePaths, rootPayload) = {
    assert(initialPlan.planStepsWithoutInitGoal.length == 1)
    val allOrderingsInitialPlan = initialPlan.orderingConstraints.graph.allTotalOrderings.get
    val initialPlanOrdering = allOrderingsInitialPlan.head
    assert(initialPlanOrdering.length == 1)

    // first generate the path decomposition tree
    val pathDecompositionTree = generatePathDecompositionTree(0 :: Nil, Set(initialPlanOrdering.head.schema))


    val (initialPlanClauses, paths) = generateDecompositionFormula(pathDecompositionTree)
    val assertedTask: Clause = Clause(pathAction(0, 0 :: Nil, initialPlanOrdering.head.schema))


    val payload: Payload = pathDecompositionTree.payload

    //println(dec.length)

    val pPaths = paths.toSeq sortWith { case ((p1, _), (p2, _)) => PathBasedEncoding.pathSortingFunction(p1, p2) }

    // create graph of the paths
    {
      val treeNodes: Seq[Seq[Int]] = pPaths map { _._1 } flatMap { p => p.indices map { x => p take (x + 1) } } distinct
      val edges =
        pPaths map { _._1 } flatMap { p => Range(1, p.length) map { x => (p take x, p take x + 1) } } distinct

      println(edges)

      val graph = SimpleDirectedGraph(treeNodes, edges)
      Dot2PdfCompiler.writeDotToFile(graph, "dectree.pdf")
    }

    (initialPlanClauses :+ assertedTask, pPaths, payload)
  }

  protected final lazy val primitivePathArray          = primitivePaths.toArray
  protected final lazy val primitivePathsOnlyPathArray = primitivePathArray map { _._1 }

  override lazy val decompositionFormula: Seq[Clause] = computedDecompositionFormula
}

case class PathDecompositionTree[Payload](path: Seq[Int], possibleTasks: Set[Task],
                                          methodToPositions: Array[Array[Int]],
                                          primitivePositions: Array[Int],
                                          payload: Payload,
                                          children: Array[PathDecompositionTree[Payload]]) {
  lazy val layer: Int = path.length - 1

  @elidable(ASSERTION)
  val assertion = {
    children.zipWithIndex foreach { case (child, i) => assert(child.path == path :+ i) }
  }
}

object PathBasedEncoding {
  //def pathSortingFunction(pathA: Seq[Int], pathB: Seq[Int]): Int = path.foldLeft(0)({ case (acc, v) => acc * domain.maximumMethodSize + v })
  def pathSortingFunction(pathA: Seq[Int], pathB: Seq[Int]): Boolean =
    pathA.zip(pathB).foldLeft[Option[Boolean]](None)(
      {
        case (Some(x), _)     => Some(x)
        case (None, (p1, p2)) => if (p1 < p2) Some(true) else if (p1 > p2) Some(false) else None
      }).getOrElse(false)

}

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.{DefaultLongInfo, PrettyPrintable}
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
    assert(p.length == l, "path " + p.mkString("(", ",", ")") + " does not have length " + l)
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
  private def generateDecompositionFormula(tree: PathDecompositionTree[Payload]): Seq[Clause] = {
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
      possibleTasksClauses
    } else {
      val possibleAbstracts = possibleTasks.zipWithIndex filter { _._1.isAbstract }
      val possiblePrimitives = tree.possiblePrimitives

      // compute per position all possible child tasks
      /*val possibleTasksPerChildPosition = new Array[mutable.Set[Task]](domain.maximumMethodSize)
      Range(0, domain.maximumMethodSize) foreach { possibleTasksPerChildPosition(_) = new mutable.HashSet[Task]() }
      possiblePrimitives foreach { case (primitive, _) => possibleTasksPerChildPosition(0) += primitive }*/
      val possibleMethods: Array[(DecompositionMethod, Int)] = tree.possibleMethods.zipWithIndex


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
      val recursiveFormula: Seq[Clause] = tree.children flatMap { case child => generateDecompositionFormula(child) }


      ///////////////////////////////
      // primitives must be inherited
      ///////////////////////////////
      val keepPrimitives: Seq[Clause] = primitivePositions.zipWithIndex flatMap { case (primitivePosition, primitiveIndexOnPrimitiveList) =>

        // put the primitive where we are told to put it and don't allow anything else
        val otherActions = Range(0, numberOfChildren) filter { _ != primitivePosition } flatMap { i => tree.children(i).possibleTasks map { task => possibleChildTasks(i)(task) } }

        //val globalIndexOfPrimitive = possiblePrimitives(primitiveIndexOnPrimitiveList)._2
        val task = possiblePrimitives(primitiveIndexOnPrimitiveList)
        val actionString = pathAction(layer, path, task)

        impliesRightAnd(actionString :: Nil, possibleChildTasks(primitivePosition)(task) :: Nil) ++ impliesAllNot(actionString, otherActions)
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
      val methodChildren: Seq[Clause] = methodToPositions.indices flatMap { case methodIndexOnApplicableMethods =>
        val decompositionMethod = possibleMethods(methodIndexOnApplicableMethods)._1
        val methodIndex = possibleMethods(methodIndexOnApplicableMethods)._2
        val methodTasks = decompositionMethod.subPlan.planStepSchemaArray


        val usedPositions = new mutable.BitSet()
        val childAtoms: Seq[String] = methodToPositions(methodIndexOnApplicableMethods).zipWithIndex map {
          case (childPosition, childIndex) =>
            usedPositions add childPosition
            val a = possibleChildTasks(childPosition)
            val b = methodTasks(childIndex)
            a(b)
        }
        val unusedActions = Range(0, numberOfChildren) filterNot usedPositions flatMap { case index => tree.children(index).possibleTasks map { task => possibleChildTasks(index)(task) } }

        val methodToken = method(layer, path, methodIndex)


        impliesRightAnd(methodToken :: Nil, childAtoms) ++ impliesAllNot(methodToken, unusedActions) ++
          additionalClausesForMethod(layer, path, decompositionMethod, methodToken, null) // TODO: This was originally taskOrdering)
      }

      // if there is nothing select at the current position we are not allowed to create new tasks
      val noneApplied: Seq[Clause] = {
        val allChildren = Range(0, numberOfChildren) flatMap { index => tree.children(index).possibleTasks map { task => possibleChildTasks(index)(task) } }

        notImpliesAllNot(possibleTasksToActions, allChildren)
      }

      possibleTasksClauses ++ keepPrimitives ++ decomposeAbstract ++ noneApplied ++ methodChildren ++ recursiveFormula
    }
  }


  protected def generatePathDecompositionTree(path: Seq[Int], possibleTasks: Set[Task]): PathDecompositionTree[Payload] = {
    //println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTaskOrder = possibleTasks.toSeq
    val layer = path.length - 1

    if (layer == K || (possibleTaskOrder forall { _.isPrimitive })) {
      //println("Terminal path with " + possibleTasks.size + " tasks")
      PathDecompositionTree(path, possibleTasks filter { _.isPrimitive }, Array(), Array(), Array(), Array(), initialPayload(possibleTasks, path), Array())
    } else {
      val (possibleAbstracts, possiblePrimitives) = possibleTaskOrder.toArray partition { _.isAbstract }

      val possibleMethods: Array[DecompositionMethod] = possibleAbstracts flatMap { case abstractTask => domain.methodsForAbstractTasks(abstractTask) } toArray


      val (methodToPositions, primitivePositions, positionsToPossibleTasks, intermediatePayload) =
        computeTaskSequenceArrangement(possibleMethods, possiblePrimitives)
      val numberOfChildren = positionsToPossibleTasks.length

      assert(possibleMethods.length == methodToPositions.length)

      ///////////////////////////////
      // recursive calls
      ///////////////////////////////
      // run recursive decent to get the formula for the downwards layers
      val subTreeResults: Array[PathDecompositionTree[Payload]] = positionsToPossibleTasks.zipWithIndex filter { _._1.nonEmpty } map { case (childPossibleTasks, index) =>
        generatePathDecompositionTree(path :+ index, childPossibleTasks.toSet)
      }

      val myPayload = combinePayloads(subTreeResults map { _.payload }, intermediatePayload)


      ///////////////////////////////
      PathDecompositionTree(path, possibleTasks, possiblePrimitives, possibleMethods, methodToPositions, primitivePositions, myPayload, subTreeResults)
    }
  }

  protected def minimisePathDecompositionTree(pdt: PathDecompositionTree[Payload]): PathDecompositionTree[Payload]

  lazy val (computedDecompositionFormula, primitivePaths, rootPayload) = {
    assert(initialPlan.planStepsWithoutInitGoal.length == 1)
    val allOrderingsInitialPlan = initialPlan.orderingConstraints.graph.allTotalOrderings.get
    val initialPlanOrdering = allOrderingsInitialPlan.head
    assert(initialPlanOrdering.length == 1)

    // first generate the path decomposition tree
    val initialPathDecompositionTree = generatePathDecompositionTree(Nil, Set(initialPlanOrdering.head.schema))
    val pathDecompositionTree = minimisePathDecompositionTree(initialPathDecompositionTree)

    assert(pathDecompositionTree.isNormalised)


    val initialPlanClauses = generateDecompositionFormula(pathDecompositionTree)
    val paths = pathDecompositionTree.primitivePaths
    val assertedTask: Clause = Clause(pathAction(0, Nil, initialPlanOrdering.head.schema))


    val payload: Payload = pathDecompositionTree.payload

    //println(dec.length)

    val pPaths = paths sortWith { case ((p1, _), (p2, _)) => PathBasedEncoding.pathSortingFunction(p1, p2) }

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

  protected final lazy val primitivePathsOnlyPath = primitivePaths map { _._1 }

  override lazy val decompositionFormula: Seq[Clause] = computedDecompositionFormula
}

case class PathDecompositionTree[Payload](path: Seq[Int], possibleTasks: Set[Task],
                                          possiblePrimitives: Array[Task], // needed as its order matters
                                          possibleMethods: Array[DecompositionMethod],
                                          methodToPositions: Array[Array[Int]],
                                          primitivePositions: Array[Int],
                                          payload: Payload,
                                          children: Array[PathDecompositionTree[Payload]],
                                          isNormalised: Boolean = false) extends DefaultLongInfo {
  @elidable(ASSERTION)
  val assertion = {
    children.zipWithIndex foreach { case (child, i) => assert(child.path == path :+ i) }
    assert(possibleMethods.length == methodToPositions.length)

    if (isNormalised) {
      possibleMethods.zip(methodToPositions) foreach {
        case (method, assignment) =>
          val isAcceptable = checkMethodPossibility(method, assignment, children)
          assert(isAcceptable, "method " + method.name + " is not applicable")
      }

      val childrenPossibleTasks = buildChildrenTaskTable(possibleMethods.zip(methodToPositions), possiblePrimitives.zip(primitivePositions))

      childrenPossibleTasks.zip(children) foreach { case (actuallyRemainingTasks, child) => assert(child.possibleTasks.size == actuallyRemainingTasks.size) }
    }

    possibleAbstracts foreach { at => assert(possibleMethods.exists(_.abstractTask == at)) }
  }

  lazy val layer: Int        = path.length
  lazy val possibleAbstracts = possibleTasks filter { _.isAbstract } toSeq


  val primitivePaths: Array[(Seq[Int], Set[Task])] = if (children.length == 0) Array((path, possibleTasks)) else children flatMap { _.primitivePaths }

  lazy val getAllNodesBelow: Array[PathDecompositionTree[Payload]] = (children flatMap { _.getAllNodesBelow }) :+ this

  lazy val treeBelowAsGraph: DirectedGraph[PathDecompositionTree[Payload]] = {

    val allNodes = getAllNodesBelow filter { _.possibleTasks.nonEmpty }
    val allEdges = allNodes flatMap { t => t.children map { c => (t, c) } } filter {case (a,b) => a.possibleTasks.nonEmpty && b.possibleTasks.nonEmpty}

    SimpleDirectedGraph(allNodes, allEdges)
  }

  private def buildChildrenTaskTable(methods : Array[(DecompositionMethod,Array[Int])], primitives : Array[(Task,Int)]) : Array[Set[Task]] = {
    val childrenPossibleTasks: Array[mutable.HashSet[Task]] = children.indices map { _ => new mutable.HashSet[Task]() } toArray

    // add tasks from methods
    methods foreach { case (method, childrenPositions) =>
      method.subPlan.planStepSchemaArray zip childrenPositions foreach { case (task, child) => childrenPossibleTasks(child) add task }
    }
    // add inherited primitives
    primitives foreach { case (primitive, index) => childrenPossibleTasks(index) add primitive }

    childrenPossibleTasks map {_.toSet}
  }

  def checkMethodPossibility(method: DecompositionMethod, childrenAssignment: Array[Int], reducedChildren: Array[PathDecompositionTree[Payload]]): Boolean = {
    val ordering: Seq[Task] = method.subPlan.planStepSchemaArray
    val methodPossible = ordering.zip(childrenAssignment) forall { case (task, position) => reducedChildren(position).possibleTasks contains task }
    //if (!methodPossible) println("Excluded method.") else println("Acceptable method.")
    methodPossible
  }

  def restrictPathDecompositionTree(toRemoveFromLeafPaths: Seq[Set[Task]]): PathDecompositionTree[Payload] =
    if (children.isEmpty) {
      assert(toRemoveFromLeafPaths.length == 1)
      // I am a leaf, so just execute the removal
      // TODO: do something with payloads ... this will become necessary once we will perform this operation also with SOGs
      PathDecompositionTree(path, possibleTasks -- toRemoveFromLeafPaths.head, Array(), Array(), Array(), Array(), payload, Array(), isNormalised = true)
    } else {
      // separate toRemoveToChildren

      val (empty, toRemovePerChild) = children.foldLeft[(Seq[Set[Task]], Seq[(PathDecompositionTree[Payload], Seq[Set[Task]])])](toRemoveFromLeafPaths, Nil)(
        {
          case ((remainingPrimitivePaths, result), child) =>
            val (thisChildPrimitive, remainingChildPrimitives) = remainingPrimitivePaths.splitAt(child.primitivePaths.length)


            (remainingChildPrimitives, result :+(child, thisChildPrimitive))
        })

      // we have to partition the primitive paths to all children ...
      assert(empty.isEmpty)

      val reducedChildren = toRemovePerChild.toArray map { case (child, toRemove) => child.restrictPathDecompositionTree(toRemove) }

      // now we have to recompute the tree
      // first step: check for all decomposition methods whether they are still applicable
      val remainingMethodsWithAssignments = possibleMethods.zip(methodToPositions) filter {
        case (method, assignment) => checkMethodPossibility(method, assignment, reducedChildren)
      }
      println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)

      val remainingMethods: Array[DecompositionMethod] = remainingMethodsWithAssignments map { _._1 }
      val remainingMethodSet: Set[DecompositionMethod] = remainingMethods.toSet
      val remainingMethodsAssignments: Array[Array[Int]] = remainingMethodsWithAssignments map { _._2 }


      val remainingPrimitivesWithAssignment =
        possiblePrimitives.zipWithIndex collect { case (primitive, index) if reducedChildren(primitivePositions(index)).possibleTasks contains primitive =>
          (primitive, primitivePositions(index))
        }
      val remainingPrimitives: Array[Task] = remainingPrimitivesWithAssignment map { _._1 }
      val remainingPrimitivesPositions: Array[Int] = remainingPrimitivesWithAssignment map { _._2 }

      println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)


      // check which tasks we can keep
      val (stillPossibleAbstractTasks, abstractTasksToDiscard) = possibleAbstracts partition { t => remainingMethods.exists(_.abstractTask == t) }
      val abstractTasksToDiscardSet = abstractTasksToDiscard.toSet

      // since we are discarding abstract tasks, their methods are not applicable any more
      // now we have to recompute the tree
      // first step: check for all decomposition methods whether they are still applicable
      val remainingMethodsWithAssignmentsAfterATRemoval = possibleMethods.zip(methodToPositions) filterNot {
        case (method, assignment) =>
          (abstractTasksToDiscardSet contains method.abstractTask) || !(remainingMethodSet contains method)
      }
      println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)

      val remainingMethodsAfterATRemoval: Array[DecompositionMethod] = remainingMethodsWithAssignmentsAfterATRemoval map { _._1 }
      val remainingMethodsAssignmentsAfterATRemoval: Array[Array[Int]] = remainingMethodsWithAssignmentsAfterATRemoval map { _._2 }

      // we have removed methods, so we have to re-check whether the tasks our children can have can actually be produced
      val childrenPossibleTasks: Array[Set[Task]] = buildChildrenTaskTable(remainingMethodsWithAssignmentsAfterATRemoval,remainingPrimitivesWithAssignment)

      // now we have propagated everything
      val propagatedChildren = childrenPossibleTasks.zip(reducedChildren) map { case (actuallyRemainingTasks, child) =>
        if (child.possibleTasks.size == actuallyRemainingTasks.size) child else child.restrictTo(actuallyRemainingTasks.toSet)
      }

      val stillPossibleTasks = stillPossibleAbstractTasks ++ remainingPrimitives

      PathDecompositionTree(path, stillPossibleTasks.toSet, remainingPrimitives,
                            remainingMethodsAfterATRemoval, remainingMethodsAssignmentsAfterATRemoval, remainingPrimitivesPositions, payload, propagatedChildren, isNormalised = true)
    }

  def restrictTo(restrictToTasks: Set[Task]): PathDecompositionTree[Payload] = if (restrictToTasks.size == possibleTasks) this else {
    println("Downwards ")
    // propagate the restriction to children
    val remainingMethods = possibleMethods.zip(methodToPositions) filter { restrictToTasks contains _._1.abstractTask}
    val remainingPrimitives = possiblePrimitives.zip(primitivePositions) filter { restrictToTasks contains _._1}

    val childrenPossibleTasks: Array[Set[Task]] = buildChildrenTaskTable(remainingMethods,remainingPrimitives)

    val restrictedChildren = childrenPossibleTasks.zip(children) map {case (tasks,child) => child.restrictTo(tasks)}

    PathDecompositionTree(path,restrictToTasks,remainingPrimitives map {_._1},remainingMethods map {_._1}, remainingMethods map {_._2}, remainingPrimitives map {_._2},
                          payload,restrictedChildren,isNormalised = isNormalised)
  }

  /** returns a detailed information about the object */
  override def longInfo: String = "T:" + possibleTasks.size + " P:" + path.mkString(",")
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

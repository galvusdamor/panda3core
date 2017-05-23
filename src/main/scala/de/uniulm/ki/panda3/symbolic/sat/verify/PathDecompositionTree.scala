package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Task}
import de.uniulm.ki.util._

import scala.annotation.elidable
import scala.annotation.elidable._
import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PathDecompositionTree[Payload](path: Seq[Int], possibleTasks: Set[Task],
                                          possiblePrimitives: Array[Task], // needed as its order matters
                                          possibleMethods: Array[(DecompositionMethod,Int)],
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
          val isAcceptable = checkMethodPossibility(method._1, assignment, children)
          assert(isAcceptable, "method " + method._1.name + " is not applicable")
      }

      val childrenPossibleTasks = buildChildrenTaskTable(possibleMethods.zip(methodToPositions), possiblePrimitives.zip(primitivePositions))

      childrenPossibleTasks.zip(children) foreach { case (actuallyRemainingTasks, child) => assert(child.possibleTasks.size == actuallyRemainingTasks.size) }
    }

    possibleAbstracts foreach { at => assert(possibleMethods.exists(_._1.abstractTask == at)) }
  }

  lazy val layer: Int        = path.length
  lazy val possibleAbstracts = possibleTasks filter { _.isAbstract } toSeq


  val primitivePaths: Array[(Seq[Int], Set[Task])] = if (children.length == 0 && possibleTasks.nonEmpty) Array((path, possibleTasks)) else children flatMap { _.primitivePaths }

  //////////////////////// transform to graph

  lazy val getAllNodesBelow: Array[PathDecompositionTree[Payload]] = (children flatMap { _.getAllNodesBelow }) :+ this

  lazy val treeBelowAsGraph: DirectedGraph[PathDecompositionTree[Payload]] = {

    val allNodes = getAllNodesBelow filter { _.possibleTasks.nonEmpty }
    val allEdges = allNodes flatMap { t => t.children map { c => (t, c) } } filter { case (a, b) => a.possibleTasks.nonEmpty && b.possibleTasks.nonEmpty }

    SimpleDirectedGraph(allNodes, allEdges)
  }

  ///////////////////////// additional data

  lazy val localConditionalReachable: Map[Task, Set[Task]] = if (children.length == 0) possibleTasks map { t => t -> Set(t) } toMap
  else
    possibleTasks map { t =>
      // if t is abstract
      if (t.isAbstract) {
        val applicableMethods = possibleMethods.zip(methodToPositions) filter { _._1._1.abstractTask == t }
        val applicableMethodsReachable = applicableMethods map { case (m, positions) =>
          m._1.subPlan.planStepSchemaArray zip positions flatMap { case (psSchema, pos) => children(pos).localConditionalReachable(psSchema) } toSet
        }

        val unionOfReachable = applicableMethodsReachable.reduce[Set[Task]]({ case (s1, s2) => s1 union s2 })
        t -> unionOfReachable
      } else t -> Set(t) // primitives reach themselves
    } toMap


  lazy val localConditionalLandmarks: Map[Task, Set[Task]] = if (children.length == 0) possibleTasks map { t => t -> Set(t) } toMap
  else
    possibleTasks map { t =>
      // if t is abstract
      if (t.isAbstract) {
        val applicableMethods = possibleMethods.zip(methodToPositions) filter { _._1._1.abstractTask == t }
        val applicableMethodsLandmarks = applicableMethods map { case (m, positions) =>
          m._1.subPlan.planStepSchemaArray zip positions flatMap { case (psSchema, pos) => children(pos).localConditionalLandmarks(psSchema) } toSet
        }

        val intersectionOfLocalLandMarks = applicableMethodsLandmarks.reduce[Set[Task]]({ case (s1, s2) => s1 intersect s2 })
        t -> intersectionOfLocalLandMarks
      } else t -> Set(t) // primitives are always local landmarks for themselves
    } toMap


  lazy val localConditionalMutexes: Map[Task, Set[(Task, Task)]] = if (children.length == 0) possibleTasks map { t => t -> Set[(Task, Task)]() } toMap
  else
    possibleTasks map { t =>

      if (t.isAbstract) {
        val applicableMethods = possibleMethods.zip(methodToPositions) filter { _._1._1.abstractTask == t }
        val applicableMethodsMutexes: Array[(Set[(Task, Task)], Set[Task])] = applicableMethods map { case (m, positions) =>
          val mutexesAndReachablePerPS: Array[(Set[(Task, Task)], Set[Task])] = m._1.subPlan.planStepSchemaArray zip positions map { case (psSchema, pos) =>
            val localMutexesForPS = children(pos).localConditionalMutexes(psSchema)
            val localReachableForPS = children(pos).localConditionalReachable(psSchema)

            (localMutexesForPS, localReachableForPS)
          }

          val mutexPSWithIndex = mutexesAndReachablePerPS.zipWithIndex
          // PS mutexes remain mutex if no other task can produce one of the two tasks
          val psNonBreakableMutexes: Set[(Task, Task)] = mutexPSWithIndex flatMap { case ((psMutexes, _), psIndex) =>
            psMutexes filterNot { case (a, b) =>
              a :: b :: Nil exists { case x => mutexPSWithIndex exists { case ((_, r), i) => psIndex != i && r.contains(x) } }

            }
          } toSet


          (psNonBreakableMutexes, mutexesAndReachablePerPS flatMap { _._2 } toSet)
        }
        val mutexCandidates = (applicableMethodsMutexes flatMap { _._1 }) ++
          (crossProduct(localConditionalReachable(t).toArray, localConditionalReachable(t).toArray) filter { case (a, b) => a != b })


        val verifiedMutexes = mutexCandidates filter { case (a, b) =>
          applicableMethodsMutexes forall { case (methodMutex, methodReachable) =>
            (methodMutex contains(a, b)) || (!(methodReachable.contains(a) && methodReachable.contains(b)))
          }
        }

        t -> verifiedMutexes.toSet
      } else t -> Set[(Task, Task)]()
    } toMap

  ///////////////////////// RESTRICT PDT

  lazy val normalise: PathDecompositionTree[Payload] = if (isNormalised) this
  else {
    val dontRemovePrimitives: Seq[Set[Task]] = primitivePaths.toSeq map { _ => Set[Task]() }

    restrictPathDecompositionTree(dontRemovePrimitives)
  }

  def checkMethodPossibility(method: DecompositionMethod, childrenAssignment: Array[Int], reducedChildren: Array[PathDecompositionTree[Payload]]): Boolean = {
    val ordering: Seq[Task] = method.subPlan.planStepSchemaArray
    val methodPossible = ordering.zip(childrenAssignment) forall { case (task, position) => reducedChildren(position).possibleTasks contains task }
    //if (!methodPossible) println("Excluded method.") else println("Acceptable method.")
    methodPossible
  }

  def restrictPathDecompositionTree(toRemoveFromLeafPaths: Seq[Set[Task]]): PathDecompositionTree[Payload] =
    if (children.isEmpty) {
      // I am a leaf, so just execute the removal
      // TODO: do something with payloads ... this will become necessary once we will perform this operation also with SOGs

      if (possibleTasks.nonEmpty) {
        assert(toRemoveFromLeafPaths.length == 1)
        PathDecompositionTree(path, possibleTasks -- toRemoveFromLeafPaths.head, Array(), Array(), Array(), Array(), payload, Array(), isNormalised = true)
      } else {
        assert(toRemoveFromLeafPaths.isEmpty)
        PathDecompositionTree(path, Set(), Array(), Array(), Array(), Array(), payload, Array(), isNormalised = true)
      }
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
        case (method, assignment) => checkMethodPossibility(method._1, assignment, reducedChildren)
      }
      //println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)

      val remainingMethods: Array[(DecompositionMethod,Int)] = remainingMethodsWithAssignments map { _._1 }
      val remainingMethodSet: Set[DecompositionMethod] = remainingMethods map {_._1} toSet
      val remainingMethodsAssignments: Array[Array[Int]] = remainingMethodsWithAssignments map { _._2 }


      val remainingPrimitivesWithAssignment =
        possiblePrimitives.zipWithIndex collect { case (primitive, index) if reducedChildren(primitivePositions(index)).possibleTasks contains primitive =>
          (primitive, primitivePositions(index))
        }
      val remainingPrimitives: Array[Task] = remainingPrimitivesWithAssignment map { _._1 }
      val remainingPrimitivesPositions: Array[Int] = remainingPrimitivesWithAssignment map { _._2 }

      //println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)


      // check which tasks we can keep
      val (stillPossibleAbstractTasks, abstractTasksToDiscard) = possibleAbstracts partition { t => remainingMethods.exists(_._1.abstractTask == t) }
      val abstractTasksToDiscardSet = abstractTasksToDiscard.toSet

      // since we are discarding abstract tasks, their methods are not applicable any more
      // now we have to recompute the tree
      // first step: check for all decomposition methods whether they are still applicable
      val remainingMethodsWithAssignmentsAfterATRemoval : Array[((DecompositionMethod,Int), Array[Int])] = possibleMethods.zip(methodToPositions) filterNot {
        case (method, assignment) =>
          (abstractTasksToDiscardSet contains method._1.abstractTask) || !(remainingMethodSet contains method._1)
      }
      //println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)

      val remainingMethodsAfterATRemoval: Array[(DecompositionMethod,Int)] = remainingMethodsWithAssignmentsAfterATRemoval map { _._1 }
      val remainingMethodsAssignmentsAfterATRemoval: Array[Array[Int]] = remainingMethodsWithAssignmentsAfterATRemoval map { _._2 }

      // we have removed methods, so we have to re-check whether the tasks our children can have can actually be produced
      val childrenPossibleTasks: Array[Set[Task]] = buildChildrenTaskTable(remainingMethodsWithAssignmentsAfterATRemoval, remainingPrimitivesWithAssignment)

      // now we have propagated everything
      val propagatedChildren = childrenPossibleTasks.zip(reducedChildren) map { case (actuallyRemainingTasks, child) =>
        if (child.possibleTasks.size == actuallyRemainingTasks.size) child else child.restrictTo(actuallyRemainingTasks.toSet)
      }

      val stillPossibleTasks = stillPossibleAbstractTasks ++ remainingPrimitives

      PathDecompositionTree(path, stillPossibleTasks.toSet, remainingPrimitives,
                            remainingMethodsAfterATRemoval, remainingMethodsAssignmentsAfterATRemoval, remainingPrimitivesPositions, payload, propagatedChildren, isNormalised = true)
    }

  def restrictTo(restrictToTasks: Set[Task]): PathDecompositionTree[Payload] = if (restrictToTasks.size == possibleTasks.size) this
  else {
    // propagate the restriction to children
    val remainingMethods = possibleMethods.zip(methodToPositions) filter { restrictToTasks contains _._1._1.abstractTask }
    val remainingPrimitives = possiblePrimitives.zip(primitivePositions) filter { restrictToTasks contains _._1 }

    val childrenPossibleTasks: Array[Set[Task]] = buildChildrenTaskTable(remainingMethods, remainingPrimitives)

    val restrictedChildren = childrenPossibleTasks.zip(children) map { case (tasks, child) => child.restrictTo(tasks) }

    PathDecompositionTree(path, restrictToTasks, remainingPrimitives map { _._1 }, remainingMethods map { _._1 }, remainingMethods map { _._2 }, remainingPrimitives map { _._2 },
                          payload, restrictedChildren, isNormalised = isNormalised)
  }

  //////////// INTERNAL HELPER METHODS

  private def buildChildrenTaskTable(methods: Array[((DecompositionMethod,Int), Array[Int])], primitives: Array[(Task, Int)]): Array[Set[Task]] = {
    val childrenPossibleTasks: Array[mutable.HashSet[Task]] = children.indices map { _ => new mutable.HashSet[Task]() } toArray

    // add tasks from methods
    methods foreach { case (method, childrenPositions) =>
      method._1.subPlan.planStepSchemaArray zip childrenPositions foreach { case (task, child) => childrenPossibleTasks(child) add task }
    }
    // add inherited primitives
    primitives foreach { case (primitive, index) => childrenPossibleTasks(index) add primitive }

    childrenPossibleTasks map { _.toSet }
  }


  /** returns a detailed information about the object */
  override def longInfo: String = "T:" + possibleTasks.size + " P:" + path.mkString(",")
}

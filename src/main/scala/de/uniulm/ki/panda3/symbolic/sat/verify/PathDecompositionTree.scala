// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Task}
import de.uniulm.ki.util._

import scala.annotation.elidable
import scala.annotation.elidable._
import scala.collection.{Seq, mutable}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class PathDecompositionTree[Payload](path: Seq[Int], possibleTasks: Set[Task],
                                          possiblePrimitives: Array[Task], // needed as its order matters
                                          possibleMethods: Array[(DecompositionMethod, Int)],
                                          methodToPositions: Array[Array[Int]],
                                          primitivePositions: Array[Int],
                                          payload: Payload,
                                          children: Array[PathDecompositionTree[Payload]],
                                          localExpansionPossible: Boolean,
                                          isNormalised: Boolean = false) extends DefaultLongInfo {
  /*@elidable(ASSERTION)
  def assertion() : Boolean= {
    if (children.isEmpty) assert(possibleMethods.forall(_._1.subPlan.planStepsWithoutInitGoal.isEmpty))
    children.zipWithIndex foreach { case (child, i) => assert(child.path == path :+ i) }
    assert(possibleMethods.length == methodToPositions.length)
    possibleMethods zip methodToPositions foreach {
      case (m, p) =>
        val psArray = m._1.subPlan.planStepSchemaArray

        assert(psArray.size == p.length)
        psArray zip p foreach { case (ps, pp) => assert(children(pp).possibleTasks.contains(ps) ||
                                                          (!isNormalised && children(pp).children.isEmpty && children(pp).possibleAbstracts.isEmpty))
        }
    }

    if (isNormalised) {
      possibleMethods.zip(methodToPositions) foreach {
        case (method, assignment) =>
          val isAcceptable = checkMethodPossibility(method._1, assignment, children)
          assert(isAcceptable, "method " + method._1.name + " is not applicable")
      }

      val childrenPossibleTasks = buildChildrenTaskTable(possibleMethods.zip(methodToPositions), possiblePrimitives.zip(primitivePositions))

      childrenPossibleTasks.zip(children) foreach { case (actuallyRemainingTasks, child) => assert(child.possibleTasks.size == actuallyRemainingTasks.size) }
    }

    //qqqpossibleAbstracts foreach { at => assert(possibleMethods.exists(_._1.abstractTask == at)) }

    if (isNormalised)
      assert(deepNormalised)
    true
  }
  assert(assertion())*/

  def deepNormalised: Boolean = isNormalised && children.forall(_.deepNormalised)

  lazy val layer: Int        = path.length
  lazy val possibleAbstracts = possibleTasks filter { _.isAbstract } toSeq

  val primitivePaths: Array[(Seq[Int], Set[Task])] =
    if (children.length == 0 && possibleTasks.nonEmpty && possibleTasks.exists(_.isPrimitive)) Array((path, possibleTasks)) else children flatMap { _.primitivePaths }

  lazy val expansionPossible: Boolean = localExpansionPossible || (children exists { _.expansionPossible })

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

  lazy val localConditionalMaxNumberOfLeafs: Map[Task, Int] =
    if (children.length == 0 && possibleTasks.nonEmpty && possibleTasks.exists(_.isPrimitive)) possibleTasks map { t => t -> 1 } toMap
    else
      possibleTasks map { t =>
        // if t is abstract
        if (t.isAbstract) {
          val applicableMethods = possibleMethods.zip(methodToPositions) filter { _._1._1.abstractTask == t }
          val applicableMethodsMaxLeafs = applicableMethods map { case (m, positions) =>
            m._1.subPlan.planStepSchemaArrayWithoutMethodPreconditions zip positions map { case (psSchema, pos) => children(pos).localConditionalMaxNumberOfLeafs(psSchema) } sum
          } max

          t -> applicableMethodsMaxLeafs
        } else t -> 1 // for primitives this is one
      } toMap

  ///////////////////////// RESTRICT PDT

  lazy val normalise: PathDecompositionTree[Payload] = if (isNormalised) this
  else {
    val dontRemovePrimitives: Seq[Set[Task]] = primitivePaths.toSeq map { _ => Set[Task]() }

    restrictPathDecompositionTree(dontRemovePrimitives)
  }

  def checkMethodPossibility(method: DecompositionMethod, childrenAssignment: Array[Int], reducedChildren: Array[PathDecompositionTree[Payload]]): Boolean = {
    // HACK!!!
    val ordering: Seq[Task] = method.subPlan.planStepSchemaArrayWithoutMethodPreconditions
    val methodPossible = ordering.zip(childrenAssignment) forall { case (task, position) => reducedChildren(position).possibleTasks contains task }
    //if (!methodPossible) println("Excluded method.") else println("Acceptable method.")
    methodPossible
  }

  def restrictPathDecompositionTree(toRemoveFromLeafPaths: Seq[Set[Task]]): PathDecompositionTree[Payload] =
    if (toRemoveFromLeafPaths.isEmpty && possibleTasks.forall(_.isAbstract) && children.isEmpty) this.copy(isNormalised = true, children = children map { _.normalise })
    else if (children.isEmpty) {
      // I am a leaf, so just execute the removal
      // TODO: do something with payloads ... this will become necessary once we will perform this operation also with SOGs

      if (possibleTasks.nonEmpty) {
        assert(toRemoveFromLeafPaths.length == 1, "toRemoveFromLeafPaths.length == " + toRemoveFromLeafPaths.length)
        // we may have empty methods remaining ...
        val remainingTasks = possibleTasks -- toRemoveFromLeafPaths.head
        val remainingMethods = possibleMethods filter { case (m, _) => remainingTasks.contains(m.abstractTask) && m.subPlan.planStepsWithoutInitGoal.isEmpty }

        PathDecompositionTree(path, remainingTasks, Array(), remainingMethods, remainingMethods map { _ => Array[Int]() }, Array(), payload, Array(),
                              localExpansionPossible = localExpansionPossible, isNormalised = true)
      } else {
        assert(toRemoveFromLeafPaths.isEmpty)
        PathDecompositionTree(path, Set(), Array(), Array(), Array(), Array(), payload, Array(), localExpansionPossible = localExpansionPossible, isNormalised = true)
      }
    } else {
      // separate toRemoveToChildren

      val (empty, toRemovePerChild) = children.foldLeft[(Seq[Set[Task]], Seq[(PathDecompositionTree[Payload], Seq[Set[Task]])])](toRemoveFromLeafPaths, Nil)(
        {
          case ((remainingPrimitivePaths, result), child) =>
            val (thisChildPrimitive, remainingChildPrimitives) = remainingPrimitivePaths.splitAt(child.primitivePaths.length)


            (remainingChildPrimitives, result :+ (child, thisChildPrimitive))
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

      val remainingMethods: Array[(DecompositionMethod, Int)] = remainingMethodsWithAssignments map { _._1 }
      val remainingMethodSet: Set[DecompositionMethod] = remainingMethods map { _._1 } toSet
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
      val remainingMethodsWithAssignmentsAfterATRemoval: Array[((DecompositionMethod, Int), Array[Int])] = possibleMethods.zip(methodToPositions) filterNot {
        case (method, assignment) =>
          (abstractTasksToDiscardSet contains method._1.abstractTask) || !(remainingMethodSet contains method._1)
      }
      //println("Methods: " + possibleMethods.length + " remaining " + remainingMethodsWithAssignments.length)

      val remainingMethodsAfterATRemoval: Array[(DecompositionMethod, Int)] = remainingMethodsWithAssignmentsAfterATRemoval map { _._1 }
      val remainingMethodsAssignmentsAfterATRemoval: Array[Array[Int]] = remainingMethodsWithAssignmentsAfterATRemoval map { _._2 }

      // we have removed methods, so we have to re-check whether the tasks our children can have can actually be produced
      val childrenPossibleTasks: Array[Set[Task]] = buildChildrenTaskTable(remainingMethodsWithAssignmentsAfterATRemoval, remainingPrimitivesWithAssignment)

      // now we have propagated everything
      val propagatedChildren = childrenPossibleTasks.zip(reducedChildren) map { case (actuallyRemainingTasks, child) =>
        if (child.possibleTasks.size == actuallyRemainingTasks.size) child.normalise else child.restrictTo(actuallyRemainingTasks.toSet)
      }

      val stillPossibleTasks = stillPossibleAbstractTasks ++ remainingPrimitives

      PathDecompositionTree(path, stillPossibleTasks.toSet, remainingPrimitives,
                            remainingMethodsAfterATRemoval, remainingMethodsAssignmentsAfterATRemoval, remainingPrimitivesPositions, payload, propagatedChildren,
                            localExpansionPossible = localExpansionPossible, isNormalised = true)
    }

  def restrictTo(restrictToTasks: Set[Task]): PathDecompositionTree[Payload] = if (restrictToTasks.size == possibleTasks.size) this
  else {
    assert(restrictToTasks subsetOf possibleTasks)
    // propagate the restriction to children
    val remainingMethods = possibleMethods.zip(methodToPositions) filter { restrictToTasks contains _._1._1.abstractTask }
    val remainingPrimitives = possiblePrimitives.zip(primitivePositions) filter { restrictToTasks contains _._1 }

    val childrenPossibleTasks: Array[Set[Task]] = buildChildrenTaskTable(remainingMethods, remainingPrimitives)

    val restrictedChildren = childrenPossibleTasks.zip(children) map { case (tasks, child) => child.restrictTo(tasks) }

    PathDecompositionTree(path, restrictToTasks, remainingPrimitives map { _._1 }, remainingMethods map { _._1 }, remainingMethods map { _._2 }, remainingPrimitives map { _._2 },
                          payload, restrictedChildren, localExpansionPossible = localExpansionPossible, isNormalised = isNormalised)
  }


  def possibleAssigmentsDFS(task: Task): Seq[(Seq[Int], Task)] = if (possiblePrimitives contains task) (path, task) :: Nil else {
    val subAssigments: Seq[(Seq[Int], Task)] = possibleMethods.zipWithIndex filter { _._1._1.abstractTask == task } flatMap { case ((method, _), idx) =>
      methodToPositions(idx).zip(method.subPlan.planStepSchemaArray) flatMap { case (childIndex, childTask) => children(childIndex).possibleAssigmentsDFS(childTask) }
    }

    subAssigments :+ (path, task)
  }

  lazy val assignmentImplications: Seq[(Seq[Int], ((Task, Int), Int))] = {
    val winrar: Map[(Task, Int), Int] = // number of methods for which a specific task is assigned to a position. If this 1, we can add an implication
      possibleMethods.zipWithIndex flatMap { case ((dm, mGlobalID), mindex) =>
        dm.subPlan.planStepSchemaArray.zip(methodToPositions(mindex)) map { x => (x, mGlobalID) }
      } groupBy { _._1 } filter { _._2.length == 1 } filterNot { possiblePrimitives contains _._1._1 } map { case (k, v) => k -> v.head._2 }

    children.flatMap(_.assignmentImplications) ++ winrar.toSeq.map(a => path -> a)
  }

  lazy val possibleAssignments: Seq[(Seq[Int], Task)] = possiblePrimitives.map(p => (path, p)) ++ children.flatMap(_.possibleAssignments)

  lazy val mutexes: Seq[((Seq[Int], Task), (Seq[Int], Task))] = {
    //println(possibleAssignments map { case (p, t) => p.mkString("(", ",", ")") + " " + t.name } mkString "\n")

    //println("Node")
    val possibleTasksPerMethod: Array[Array[(Int, Seq[(Seq[Int], Task)])]] = possibleMethods.zipWithIndex map { case ((method, _), methodIDX) =>
      val taskPossible = methodToPositions(methodIDX).zip(method.subPlan.planStepSchemaArray) map { case (idx, task) => (idx, children(idx).possibleAssigmentsDFS(task)) }

      //println(taskPossible map { case (cIdx, poss) => "Child " + cIdx + ": " + poss.size } mkString "\n")

      taskPossible
    }

    val possibleTasksPerMethodSet: Array[Array[Set[(Seq[Int], Task)]]] = possibleTasksPerMethod map { case indexArray =>
      children.indices map { child =>
        indexArray.find(_._1 == child) match {
          case None                   => Set[(Seq[Int], Task)]()
          case Some((_, assignments)) => assignments.toSet
        }
      } toArray
    }

    val allLocalMutexes: Seq[((Seq[Int], Task), (Seq[Int], Task))] = children.indices flatMap { child1 =>
      val possibleChild1 = children(child1).possibleAssignments

      val child2Mutexes = children.indices collect { case child2 if child1 < child2 =>
        val possibleChild2 = children(child2).possibleAssignments

        //println("Child " + child1 + "&" + child2 + ": " + possibleChild1.length + " and " + possibleChild2.length)
        //println("Child " + child1 + "&" + child2 + ": " + possibleChild1.distinct.length + " and " + possibleChild2.distinct.length)

        val foundMutexes: Seq[((Seq[Int], Task), (Seq[Int], Task))] = possibleChild1 flatMap { ass1 =>
          possibleChild2 map { ass2 =>
            // check all methods if violating
            val hasViolating = possibleTasksPerMethodSet exists { case methodPoss: Array[Set[(Seq[Int], Task)]] =>
              val can1 = methodPoss(child1) contains ass1
              val can2 = methodPoss(child2) contains ass2

              can1 && can2
            }

            ((ass1, ass2), hasViolating)
          } collect { case (x, false) => x }
        }

        foundMutexes

      }

      child2Mutexes.flatten
    }

    //println("Found " + allLocalMutexes.size + " local mutexes")
    //println(allLocalMutexes map {case ((_,t1),(_,t2)) => t1.name + " vs " + t2.name} mkString "\n")


    //println("done")

    val subMutexes = children flatMap { _.mutexes }


    //println("FOUND " + filteresCands.size)
    //val childMutexes = children map { _.mutextes }

    subMutexes ++ allLocalMutexes
  }


  //////////// INTERNAL HELPER METHODS

  private def buildChildrenTaskTable(methods: Array[((DecompositionMethod, Int), Array[Int])], primitives: Array[(Task, Int)]): Array[Set[Task]] = {
    val childrenPossibleTasks: Array[mutable.HashSet[Task]] = children.indices map { _ => new mutable.HashSet[Task]() } toArray

    // add tasks from methods
    methods foreach { case (method, childrenPositions) =>
      // TODO: Hack
      method._1.subPlan.planStepSchemaArrayWithoutMethodPreconditions zip childrenPositions foreach { case (task, child) =>
        assert(children(child).possibleTasks.contains(task) || (!isNormalised && children(child).children.isEmpty && children(child).possibleAbstracts.isEmpty))
        if (children(child).possibleTasks contains task) // if not, this is an abstract task at the end of the hierarchy .. or something strange with empty methods ..
          childrenPossibleTasks(child) add task
      }
    }
    // add inherited primitives
    primitives foreach { case (primitive, index) => childrenPossibleTasks(index) add primitive }

    childrenPossibleTasks map { _.toSet }
  }


  def walkToNode(path: Seq[Int]): PathDecompositionTree[Payload] = if (path.isEmpty) this else children(path.head).walkToNode(path.drop(1))

  /** returns a detailed information about the object */
  override def longInfo: String = "T:" + possibleTasks.size + " P:" + path.mkString(",")

  val id: Int = {
    PathDecompositionTree.globalIDCounter += 1
    PathDecompositionTree.globalIDCounter
  }
}


object PathDecompositionTree {
  var globalIDCounter: Int = 0
}
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

import de.uniulm.ki.panda3.configuration.Timings._
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

  def usePDTMutexes: Boolean

  // layer, path to that action, actual task
  /*private val action: ((Int, Seq[Int], Task)) => String = memoise[(Int, Seq[Int], Task), String]({ case (l, p, t) =>
    assert(p.length == l + 1)
    "action^" + l + "_" + p.mkString(";") + "," + taskIndex(t)
                                                                                                 })*/

  protected def pathAction(l: Int, p: Seq[Int], t: Task) = {
    assert(p.length == l, "path " + p.mkString("(", ",", ")") + " does not have length " + l)
    "pathaction!" + l + "_" + p.mkString(";") + "," + taskIndex(t)
  }

  // layer, path to that action, actual method
  protected val method: ((Int, Seq[Int], Int)) => String = memoise[(Int, Seq[Int], Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos.mkString(";") + "," + methodIdx })


  ///// methods to make the formula generation configurable

  protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, methodChildrenPositions: Map[Int, Int]): Seq[Clause]

  protected def initialPayload(possibleTasks: Set[Task], path: Seq[Int]): Payload

  protected def combinePayloads(childrenPayload: Seq[Payload], intermediate: IntermediatePayload): Payload

  protected def computeTaskSequenceArrangement(possibleMethods: Array[DecompositionMethod], possiblePrimitives: Seq[Task]):
  (Array[Array[Int]], Array[Int], Array[Set[Task]], IntermediatePayload)


  // returns all clauses needed for the decomposition and all paths to the last layer
  private def generateDecompositionFormula(tree: PathDecompositionTree[Payload]): Seq[Clause] = {
    assert(tree.isNormalised)
    val possibleTasks = tree.possibleTasks.toSeq
    val layer = tree.layer
    val path = tree.path

    //println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTasksToActions: Array[String] = possibleTasks map { t => pathAction(layer, path, t) } toArray

    // write myself
    val possibleTasksClauses: Seq[Clause] = atMostOneOf(possibleTasksToActions)

    val possiblePrimitives = tree.possiblePrimitives
    val possibleAbstracts = possibleTasks.zipWithIndex filter { _._1.isAbstract }
    val possibleMethods: Array[(DecompositionMethod, Int)] = tree.possibleMethods

    ///////////////////////////////
    // decomposition
    ///////////////////////////////
    // 1. part: select method if necessary
    val decomposeAbstract: Seq[Clause] = possibleAbstracts flatMap { case (abstractTask, abstractIndex) =>
      // select a method
      val atPossibleMethods = possibleMethods filter { _._1.abstractTask == abstractTask } map { case (m, idx) => (m, method(layer, path, idx)) }

      // one method must be applied
      val oneMustBeApplied = impliesRightOr(possibleTasksToActions(abstractIndex) :: Nil, atPossibleMethods map { _._2 })
      val applicationForcesAbstractTask = atPossibleMethods map { _._2 } map { m => impliesSingle(m, possibleTasksToActions(abstractIndex)) }
      val atMostOneCanBeApplied = atMostOneOf(atPossibleMethods map { _._2 })

      applicationForcesAbstractTask ++ atMostOneCanBeApplied :+ oneMustBeApplied
    }

    if (tree.children.isEmpty) {
      // we also end up here if all methods applicable to abstract tasks are epsilon methods
      possibleTasksClauses ++ decomposeAbstract
    } else {


      val methodToPositions = tree.methodToPositions
      val primitivePositions = tree.primitivePositions
      val numberOfChildren = tree.children.length

      val possibleChildTasks: Array[Map[Task, String]] = Range(0, numberOfChildren) map { npos =>
        val npath = path :+ npos
        tree.children(npos).possibleTasks map { t => t -> pathAction(layer + 1, npath, t) } toMap
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

        // prepare method representation for additionalClauses
        val methodChildrenPositions: Map[Int, Int] = decompositionMethod.subPlan.planStepsWithoutInitGoal map { case ps =>
          val psBefore = decompositionMethod.subPlan.planStepsWithoutInitGoal.filter(_.schema == ps.schema)
          val psIndexOnSameType = psBefore.indexOf(ps)
          val psIndex = methodTasks.zipWithIndex.filter(_._1 == ps.schema)(psIndexOnSameType)._2
          ps.id -> methodToPositions(methodIndexOnApplicableMethods)(psIndex)
        } toMap

        impliesRightAnd(methodToken :: Nil, childAtoms) ++ impliesAllNot(methodToken, unusedActions) ++
          additionalClausesForMethod(layer, path, decompositionMethod, methodToken, methodChildrenPositions) // TODO: This was originally taskOrdering)
      }

      // if there is nothing select at the current position we are not allowed to create new tasks
      val noneApplied: Seq[Clause] = {
        val allChildren = Range(0, numberOfChildren) flatMap { index => tree.children(index).possibleTasks map { task => possibleChildTasks(index)(task) } }
        val allMethods = possibleMethods map { case (_, methodIndex) => method(layer, path, methodIndex) }

        notImpliesAllNot(possibleTasksToActions, allChildren ++ allMethods)
      }

      possibleTasksClauses ++ keepPrimitives ++ decomposeAbstract ++ noneApplied ++ methodChildren ++ recursiveFormula
    }
  }


  protected def generatePathDecompositionTree(path: Seq[Int], possibleTasks: Set[Task]): PathDecompositionTree[Payload] = {
    //println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTaskOrder = possibleTasks.toSeq
    val layer = path.length

    if (layer == K || (possibleTaskOrder forall { _.isPrimitive })) {
      //println("Terminal path with " + possibleTasks.size + " tasks")
      val expansionPossible = possibleTasks exists { _.isAbstract }
      PathDecompositionTree(path, possibleTasks filter { _.isPrimitive }, Array(), Array(), Array(), Array(), initialPayload(possibleTasks, path), Array(),
                            localExpansionPossible = expansionPossible)
    } else {
      val (possibleAbstracts, possiblePrimitives) = possibleTaskOrder.toArray partition { _.isAbstract }

      val possibleMethods: Array[(DecompositionMethod, Int)] = possibleAbstracts flatMap { case abstractTask => domain.methodsWithIndexForAbstractTasks(abstractTask) } toArray


      val (methodToPositions, primitivePositions, positionsToPossibleTasks, intermediatePayload) = computeTaskSequenceArrangement(possibleMethods map { _._1 }, possiblePrimitives)
      possibleMethods.zipWithIndex foreach { case ((m, _), index) =>
        methodToPositions(index) zip m.subPlan.planStepSchemaArray foreach { case (p, t) => assert(positionsToPossibleTasks(p) contains t) }
      }

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
      PathDecompositionTree(path, possibleTasks, possiblePrimitives, possibleMethods, methodToPositions, primitivePositions, myPayload, subTreeResults, localExpansionPossible = false)
    }
  }

  protected def minimisePathDecompositionTree(pdt: PathDecompositionTree[Payload]): PathDecompositionTree[Payload]

  lazy val (computedDecompositionFormula, primitivePaths, rootPayload, expansionPossible) = if (domain.maximumMethodSize == -1) {
    val ret: (Seq[Clause], Array[(Seq[Int], Set[Task])], Payload, Boolean) = (Nil, Array(), initialPayload(Set(), Nil), false)
    ret
  }
  else {
    assert(initialPlan.planStepsWithoutInitGoal.length == 1)
    val allOrderingsInitialPlan = initialPlan.orderingConstraints.graph.allTotalOrderings.get
    val initialPlanOrdering = allOrderingsInitialPlan.head
    assert(initialPlanOrdering.length == 1)

    // first generate the path decomposition tree
    print("Generating initial PDT ... ")
    timeCapsule start GENERATE_PDT
    val initialPathDecompositionTree = generatePathDecompositionTree(Nil, Set(initialPlanOrdering.head.schema))
    timeCapsule stop GENERATE_PDT
    println("done")

    print("Checking whether the PDT can grow any more ... ")
    val expansion = initialPathDecompositionTree.expansionPossible
    if (expansion) print("yes ... ") else print("no ... ")
    println("done")


    print("Normalising and optimising PDT ... ")
    timeCapsule start NORMALISE_PDT
    val pathDecompositionTree = minimisePathDecompositionTree(initialPathDecompositionTree)
    timeCapsule stop NORMALISE_PDT
    println("done")
    assert(pathDecompositionTree.isNormalised)

    /* def dd(pdt: PathDecompositionTree[Payload]): Unit = {
      //println(pdt.localConditionalLandmarks map { case (t, s) => t.name + " -> " + (s map { _.name } mkString " ") } mkString "\n")
      //println(pdt.localConditionalReachable map { case (t, s) => t.name + " -> " + (s map { _.name } mkString " ") } mkString "\n")
      //println(pdt.localConditionalMutexes map { case (t, s) => t.name + " -> " + (s map { case (a, b) => "(" + a.name + " X " + b.name + ")" } mkString " ") } mkString "\n")
      println(pdt.localConditionalMutexes map { case (t, s) => t.name + " -> " + s.size } mkString "\n")
      println("DEC")
      pdt.children foreach dd
    }*/

    //println(pathDecompositionTree.localConditionalLandmarks map { case (t, s) => t.name + " -> " + (s map { _.name } mkString " ") } mkString "\n")
    //dd(pathDecompositionTree)

    val mutexClauses = if (usePDTMutexes) {
      print("Computeing PDT mutexes ... ")
      timeCapsule start GENERATE_MUTEXES
      val pdtMutextes: Seq[((Seq[Int], Task), (Seq[Int], Task))] = pathDecompositionTree.mutexes
      println(" found " + pdtMutextes.length + " mutexes ... generating formula ... ")
      val clauses = pdtMutextes map { case ((path1, task1), (path2, task2)) =>
        val atom1 = pathAction(path1.length, path1, task1)
        val atom2 = pathAction(path2.length, path2, task2)

        println("Mutex " + atom1 + " " + atom2)

        Clause((atom1, false) :: (atom2, false) :: Nil)
      }
      timeCapsule stop GENERATE_MUTEXES
      println("done")

      clauses
    } else Nil



    println("Assignment implications")
    val uniqueMethodResult: Seq[Clause] = if (false) Nil else pathDecompositionTree.assignmentImplications map { case (path, ((task, childIndex), methodsAndTasks)) =>
      val childPath = path :+ childIndex

      //println(path + " " + childPath)
      val taskAtChild = pathAction(childPath.length, childPath, task)
      val causes = methodsAndTasks map {
        case Left(mindex) => method(path.length, path, mindex)
        case Right(_)     => pathAction(path.length, path, task)
      }

      impliesRightOr(taskAtChild :: Nil, causes)
    }


    timeCapsule start GENERATE_CLAUSES
    print("Generating clauses representing decomposition ... ")
    val initialPlanClauses = generateDecompositionFormula(pathDecompositionTree)
    val paths = pathDecompositionTree.primitivePaths
    val assertedTask: Clause = Clause(pathAction(0, Nil, initialPlanOrdering.head.schema))
    println("done")
    timeCapsule stop GENERATE_CLAUSES

    val payload: Payload = pathDecompositionTree.payload

    //println(dec.length)

    val pPaths: Array[(Seq[Int], Set[Task])] = paths sortWith { case ((p1, _), (p2, _)) => PathBasedEncoding.pathSortingFunction(p1, p2) }

    // create graph of the paths
    /*{
      val treeNodes: Seq[Seq[Int]] = pPaths map { _._1 } flatMap { p => p.indices map { x => p take (x + 1) } } distinct
      val edges =
        pPaths map { _._1 } flatMap { p => Range(1, p.length) map { x => (p take x, p take x + 1) } } distinct

      val graph = SimpleDirectedGraph(treeNodes, edges)
      Dot2PdfCompiler.writeDotToFile(graph.dotString(DirectedGraphDotOptions(), { case n: Seq[Int] =>
        val node: PathDecompositionTree[Payload] = pathDecompositionTree.walkToNode(n)
        val tasks: Set[Task] = node.possibleTasks

        node.payload match {
          case sog: SOG =>
            //Dot2PdfCompiler.writeDotToFile(sog.ordering map {case (p,_) => pathDecompositionTree.walkToNode(p).id},"payload" + node.id + ".pdf")
            Dot2PdfCompiler.writeDotToFile(sog.ordering.transitiveReduction.map({ case (p, _) => p.take(n.length + 1) }).transitiveReduction, "payload" + node.id + ".pdf")
          case _        =>
        }

        println("\n\n" + node.id + " " + node.path.mkString(";"))
        node.possibleMethods.zipWithIndex foreach { case ((dm, mind), mi) =>

          val planSteps = dm.subPlan.orderingConstraints.graph.topologicalOrdering.get
          println("\tMind: " + mind)
          planSteps foreach { ps =>
            val pos = dm.subPlan.planStepSchemaArray.indexOf(ps.schema)

            println("\t" + node.methodToPositions(mi)(pos) + " " + ps.schema.isAbstract + " (" + ps.schema.name + "," + taskIndex(ps.schema) + ")")
          }
          println()
        }

        tasks.count(_.isPrimitive) + " " + tasks.count(_.isAbstract) + " @ " + node.id + " " + n.last
      }), "dectree.pdf")

      //System exit 0
    }*/

    val ret: (Seq[Clause], Array[(Seq[Int], Set[Task])], Payload, Boolean) = (mutexClauses ++ initialPlanClauses ++ uniqueMethodResult :+ assertedTask, pPaths, payload, expansion)
    ret
  }

  protected final lazy val primitivePathsOnlyPath = primitivePaths map { _._1 }


  override lazy val decompositionFormula: Seq[Clause] = computedDecompositionFormula
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

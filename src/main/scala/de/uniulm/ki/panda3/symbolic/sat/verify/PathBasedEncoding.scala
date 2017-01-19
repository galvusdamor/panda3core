package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.{DecompositionMethod, Task}
import de.uniulm.ki.util._

import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait PathBasedEncoding extends VerifyEncoding {

  // layer, path to that action, actual task
  /*private val action: ((Int, Seq[Int], Task)) => String = memoise[(Int, Seq[Int], Task), String]({ case (l, p, t) =>
    assert(p.length == l + 1)
    "action^" + l + "_" + p.mkString(";") + "," + taskIndex(t)
                                                                                                 })*/

  protected def pathAction(l: Int, p: Seq[Int], t: Task) = {
    assert(p.length == l + 1)
    "action!" + l + "_" + p.mkString(";") + "," + taskIndex(t)
  }

  // layer, path to that action, actual method
  protected val method: ((Int, Seq[Int], Int)) => String = memoise[(Int, Seq[Int], Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos.mkString(";") + "," + methodIdx })


  protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: Seq[Task]): Seq[Clause]

  // returns all clauses needed for the decomposition and all paths to the last layer
  private def generateDecompositionFormula(layer: Int, path: Seq[Int], possibleTasks: Set[Task]): (Seq[Clause], Set[(Seq[Int], Set[Task])]) = {
    //println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTaskOrder = possibleTasks.toSeq
    //val possibleTasksToActions = possibleTasks map { t => t -> action(layer, path, t) } toMap
    val possibleTasksToActions: Array[String] = possibleTaskOrder map { t => pathAction(layer, path, t) } toArray
    val possibleChildTasks: Array[Map[Task, String]] = Range(0, domain.maximumMethodSize) map { npos =>
      val npath = path :+ npos
      domain.tasks map { t => t -> pathAction(layer + 1, npath, t) } toMap
    } toArray

    // write myself
    val possibleTasksClauses: Seq[Clause] = atMostOneOf(possibleTasksToActions)

    if (layer == K || (possibleTaskOrder forall { _.isPrimitive })) {
      //val notAnyNonPossibleTask = domain.tasks filterNot possibleTasks map { action(layer, path, _) } map { a => Clause((a, false) :: Nil) }
      //(possibleTasksClauses ++ notAnyNonPossibleTask, Set((path, possibleTasks)))
      //println("Terminal path with " + possibleTasks.size + " tasks")
      (possibleTasksClauses, Set((path, possibleTasks)))
    } else {
      val (possibleAbstracts, possiblePrimitives) = possibleTaskOrder.zipWithIndex partition { _._1.isAbstract }

      //println(possibleAbstracts map {_._1.name})
      //println("compute children")

      // compute per position all possible child tasks
      val possibleTasksPerChildPosition = new Array[mutable.Set[Task]](domain.maximumMethodSize)
      Range(0, domain.maximumMethodSize) foreach { possibleTasksPerChildPosition(_) = new mutable.HashSet[Task]() }
      possiblePrimitives foreach { case (primitive, _) => possibleTasksPerChildPosition(0) += primitive }
      val possibleSubTaskSequences = possibleAbstracts flatMap { case (abstractTask, abstractIndex) =>
        // select a method
        val possibleMethods = domain.methodsForAbstractTasks(abstractTask)

        // if a method is applied it will have children
        possibleMethods map { decompositionMethod =>
          val allTotalOrderings = decompositionMethod.subPlan.orderingConstraints.graph.topologicalOrdering.get
          allTotalOrderings map { _.schema }
        }
      }

      // TODO TEST
      /*val allMethods = possibleAbstracts flatMap { case (abstractTask, _) => domain.methodsForAbstractTasks(abstractTask) }
      println("Number of Methods " + allMethods.size)
      val start = System.currentTimeMillis()
      val g = DirectedGraph.minimalInducedSuperGraph(allMethods map {_.subPlan.orderingConstraints.fullGraph})
      val end = System.currentTimeMillis()
      println("Time " + (end - start) + " Size " + g._1.vertices.length + " " + g._1.edgeList.size + " " + (allMethods forall {_.subPlan.orderingConstraints.isTotalOrder()}))
*/
      val maxMethodLength = optimiseTaskSequenceArrangement(possibleSubTaskSequences, possibleTasksPerChildPosition)
      val methodRange = Range(0, maxMethodLength)

      //println("done " + (possibleTasksPerChildPosition count { _.nonEmpty }))

      //Range(0, domain.maximumMethodSize) foreach { possibleTasksPerChildPosition(_) ++= domain.tasks }


      // primitives must be inherited
      val keepPrimitives = possiblePrimitives flatMap { case (primitive, primitiveIndex) =>

        // keep the primitive at position 0 and don't allow anything else
        val otherActions = Range(1, domain.maximumMethodSize) flatMap { index =>
          possibleTasksPerChildPosition(index) map { task => possibleChildTasks(index)(task) } // action(layer + 1, path :+ index, task)
        }

        //impliesRightAnd(action(layer, path, primitive) :: Nil, action(layer + 1, path :+ 0, primitive) :: Nil) ++ impliesAllNot(action(layer, path, primitive), otherActions)
        impliesRightAnd(possibleTasksToActions(primitiveIndex) :: Nil, possibleChildTasks(0)(primitive) :: Nil) ++ impliesAllNot(possibleTasksToActions(primitiveIndex), otherActions)
      }
      //println("KEEP PRIM: " + keepPrimitives.length)

      val decomposeAbstract: Seq[Clause] = possibleAbstracts flatMap { case (abstractTask, abstractIndex) =>
        // select a method
        val possibleMethods = domain.methodsWithIndexForAbstractTasks(abstractTask) map { case (m, idx) => (m, method(layer, path, idx)) }
        //println(possibleMethods.length)

        // one method must be applied
        val oneMustBeApplied = impliesRightOr(possibleTasksToActions(abstractIndex) :: Nil, possibleMethods map { _._2 })
        val atMostOneCanBeApplied = atMostOneOf(possibleMethods map { _._2 })

        // if a method is applied it will have children
        val methodChildren = possibleMethods flatMap { case (decompositionMethod, methodString) =>
          val allTotalOrderings = decompositionMethod.subPlan.orderingConstraints.graph.topologicalOrdering.get
          val taskOrdering = allTotalOrderings map { _.schema }

          val usedPositions = new mutable.BitSet()
          var childAtoms: Seq[String] = Nil

          methodRange.foldLeft(taskOrdering)(
            {
              case (Nil, _)            => Nil
              case (remainingTasks, i) =>
                if (possibleTasksPerChildPosition(i) contains remainingTasks.head) {
                  usedPositions add i
                  childAtoms = childAtoms.+:(possibleChildTasks(i)(remainingTasks.head))
                  remainingTasks.tail
                }
                else remainingTasks
            })

          //taskOrdering.zipWithIndex map { case (task, index) => possibleChildTasks(index)(task) }

          // unused are not allowed to contain anything
          val unusedActions = Range(0, domain.maximumMethodSize) flatMap { case index =>
            if (!usedPositions.contains(index)) possibleTasksPerChildPosition(index) map { task => possibleChildTasks(index)(task) } else Nil
          }

          impliesRightAnd(methodString :: Nil, childAtoms) ++ impliesAllNot(methodString, unusedActions) ++
            additionalClausesForMethod(layer, path, decompositionMethod, methodString, taskOrdering)
        }

        methodChildren ++ atMostOneCanBeApplied :+ oneMustBeApplied
      }
      //println("DECOMP: " + decomposeAbstract.length)

      // if there is nothing select at the current position we are not allowed to create new tasks
      val noneApplied = {
        val allChildren = Range(0, domain.maximumMethodSize) flatMap { index =>
          possibleTasksPerChildPosition(index) map { task => possibleChildTasks(index)(task) }
        }

        //println("NOTIMPLY: |A|=" + possibleTasksToActions.length + " |B|=" + allChildren.length)
        notImpliesAllNot(possibleTasksToActions, allChildren)
      }
      //println("NON APL: " + noneApplied.length)

      //println("Clauses possibleTasks " + possibleTasksClauses.size)
      //println("Clauses keepPrim " + keepPrimitives.size)
      //println("Clauses decomp " + decomposeAbstract.size)
      //println("Clauses nonApplied " + noneApplied.size)


      // run recursive decent to get the formula for the downwards layers
      val subFormulas = possibleTasksPerChildPosition.zipWithIndex filter { _._1.nonEmpty } map { case (childPossibleTasks, index) =>
        generateDecompositionFormula(layer + 1, path :+ index, childPossibleTasks.toSet)
      }

      val recursiveFormula = subFormulas flatMap { _._1 }
      //println("Recursive: " + path + " " + recursiveFormula.length)
      val paths = subFormulas flatMap { _._2 } toSet

      (possibleTasksClauses ++ keepPrimitives ++ decomposeAbstract ++ noneApplied ++ recursiveFormula, paths)
    }
  }


  private def optimiseTaskSequenceArrangement(taskSequences: Seq[Seq[Task]], possibleTasksPerChildPosition: Array[mutable.Set[Task]]): Int = {
    // take the longest ones first
    val sorted = taskSequences.sortBy(-_.length)
    val maxLen = if (sorted.nonEmpty) sorted.head.length else 0
    val range = Range(0, maxLen)

    sorted foreach { seq =>
      range.foldLeft(seq)(
        {
          case (Nil, _)            => Nil
          case (remainingTasks, i) => if (possibleTasksPerChildPosition(i) contains remainingTasks.head) remainingTasks.tail
          else if (remainingTasks.length == maxLen - i) {possibleTasksPerChildPosition(i) += remainingTasks.head; remainingTasks.tail }
          else remainingTasks
        })
      //zipWithIndex map { case (task, index) => possibleTasksPerChildPosition(index) += task }
    }

    maxLen
  }


  lazy val (computedDecompositionFormula, primitivePaths) = {
    val allOrderingsInitialPlan = initialPlan.orderingConstraints.graph.allTotalOrderings.get
    val initialPlanOrdering = allOrderingsInitialPlan.head

    val initialPlanClauses = initialPlanOrdering.zipWithIndex map { case (task, index) => generateDecompositionFormula(0, index :: Nil, Set(task.schema)) }
    val assertedTasks = initialPlanOrdering.zipWithIndex map { case (task, index) => Clause(pathAction(0, index :: Nil, task.schema)) }

    val paths = initialPlanClauses flatMap { _._2 }
    //    paths.foreach { p => assert(p._1.length == paths.head._1.length) }
    val dec = initialPlanClauses flatMap { _._1 }

    //println(dec.length)
    (dec ++ assertedTasks, initialPlanClauses flatMap { _._2 } sortWith { case ((p1, _), (p2, _)) => PathBasedEncoding.pathSortingFunction(p1, p2) })
  }

  protected final val primitivePathArray          = primitivePaths.toArray
  protected final val primitivePathsOnlyPathArray = primitivePathArray map { _._1 }

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

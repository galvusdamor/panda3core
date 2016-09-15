package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, DecompositionMethod, Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.{Predicate, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util._

import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TotallyOrderedEncoding(domain: Domain, initialPlan: Plan, taskSequenceLength: Int, offsetToK: Int) extends VerifyEncoding {

  val numberOfChildrenClauses: Int = 0

  assert(domain.isTotallyOrdered)

  // layer, path to that action, actual task
  /*private val action: ((Int, Seq[Int], Task)) => String = memoise[(Int, Seq[Int], Task), String]({ case (l, p, t) =>
    assert(p.length == l + 1)
    "action^" + l + "_" + p.mkString(";") + "," + taskIndex(t)
                                                                                                 })*/

  private def action(l: Int, p: Seq[Int], t: Task) = {
    assert(p.length == l + 1)
    "action^" + l + "_" + p.mkString(";") + "," + taskIndex(t)
  }

  // layer, path to that action, actual method
  private val method: ((Int, Seq[Int], Int)) => String = memoise[(Int, Seq[Int], Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos.mkString(";") + "," + methodIdx })

  protected val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  // returns all clauses needed for the decomposition and all paths to the last layer
  private def generateDecompositionFormula(layer: Int, path: Seq[Int], possibleTasks: Set[Task]): (Seq[Clause], Set[(Seq[Int], Set[Task])]) = {
    println("GENPATH: L=" + layer + " p=" + path + " |pTasks|=" + possibleTasks.size + " \\Delta=" + domain.maximumMethodSize)
    val possibleTaskOrder = possibleTasks.toSeq
    //val possibleTasksToActions = possibleTasks map { t => t -> action(layer, path, t) } toMap
    val possibleTasksToActions: Array[String] = possibleTaskOrder map { t => action(layer, path, t) } toArray
    val possibleChildTasks: Array[Map[Task, String]] = Range(0, domain.maximumMethodSize) map { npos =>
      val npath = path :+ npos
      domain.tasks map { t => t -> action(layer + 1, npath, t) } toMap
    } toArray

    // write myself
    val possibleTasksClauses: Seq[Clause] = atMostOneOf(possibleTasksToActions)

    if (layer == K) {
      //val notAnyNonPossibleTask = domain.tasks filterNot possibleTasks map { action(layer, path, _) } map { a => Clause((a, false) :: Nil) }
      //(possibleTasksClauses ++ notAnyNonPossibleTask, Set((path, possibleTasks)))
      (possibleTasksClauses, Set((path, possibleTasks)))
    } else {
      val (possibleAbstracts, possiblePrimitives) = possibleTaskOrder.zipWithIndex partition { _._1.isAbstract }

      println("compute children")

      // compute per position all possible child tasks
      val possibleTasksPerChildPosition = new Array[mutable.Set[Task]](domain.maximumMethodSize)
      Range(0, domain.maximumMethodSize) foreach { possibleTasksPerChildPosition(_) = new mutable.HashSet[Task]() }
      possiblePrimitives foreach { case (primitive, _) => possibleTasksPerChildPosition(0) += primitive }
      possibleAbstracts foreach { case (abstractTask, abstractIndex) =>
        // select a method
        val possibleMethods = domain.methodsForAbstractTasks(abstractTask)

        // if a method is applied it will have children
        possibleMethods foreach { decompositionMethod =>
          val allTotalOrderings = decompositionMethod.subPlan.orderingConstraints.graph.allTotalOrderings.get
          assert(allTotalOrderings.length == 1)
          val taskOrdering = allTotalOrderings.head map { _.schema }
          taskOrdering.zipWithIndex map { case (task, index) => possibleTasksPerChildPosition(index) += task }
        }
      }

      println("done " + (possibleTasksPerChildPosition count { _.nonEmpty }))

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
      println("KEEP PRIM: " + keepPrimitives.length)

      val decomposeAbstract: Seq[Clause] = possibleAbstracts flatMap { case (abstractTask, abstractIndex) =>
        // select a method
        val possibleMethods = domain.methodsWithIndexForAbstractTasks(abstractTask) map { case (m, idx) => (m, method(layer, path, idx)) }

        // one method must be applied
        val oneMustBeApplied = impliesRightOr(possibleTasksToActions(abstractIndex) :: Nil, possibleMethods map { _._2 })
        val atMostOneCanBeApplied = atMostOneOf(possibleMethods map { _._2 })

        // if a method is applied it will have children
        val methodChildren = possibleMethods flatMap { case (decompositionMethod, methodString) =>
          val allTotalOrderings = decompositionMethod.subPlan.orderingConstraints.graph.allTotalOrderings.get
          assert(allTotalOrderings.length == 1)
          val taskOrdering = allTotalOrderings.head map { _.schema }

          val childAtoms = taskOrdering.zipWithIndex map { case (task, index) =>
            possibleChildTasks(index)(task)
          }

          // unused are not allowed to contain anything
          val unusedActions = Range(taskOrdering.length, domain.maximumMethodSize) flatMap { index =>
            possibleTasksPerChildPosition(index) map { task => possibleChildTasks(index)(task) }
          }

          impliesRightAnd(methodString :: Nil, childAtoms) ++ impliesAllNot(methodString, unusedActions)
        }

        methodChildren ++ atMostOneCanBeApplied :+ oneMustBeApplied
      }
      println("DECOMP: " + decomposeAbstract.length)

      // if there is nothing select at the current position we are not allowed to create new tasks
      val noneApplied = {
        val allChildren = Range(0, domain.maximumMethodSize) flatMap { index =>
          possibleTasksPerChildPosition(index) map { task => possibleChildTasks(index)(task) }
        }

        println("NOTIMPLY: |A|=" + possibleTasksToActions.length + " |B|=" + allChildren.length)
        notImpliesAllNot(possibleTasksToActions, allChildren)
      }
      println("NON APL: " + noneApplied.length)

      // run recursive decent to get the formula for the downwards layers
      val subFormulas = possibleTasksPerChildPosition.zipWithIndex filter { _._1.nonEmpty } map { case (childPossibleTasks, index) =>
        generateDecompositionFormula(layer + 1, path :+ index, childPossibleTasks.toSet)
      }

      val recursiveFormula = subFormulas flatMap { _._1 }
      println("Recursive: " + path + " " + recursiveFormula.length)
      val paths = subFormulas flatMap { _._2 } toSet

      (possibleTasksClauses ++ keepPrimitives ++ decomposeAbstract ++ noneApplied ++ recursiveFormula, paths)
    }
  }


  private def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
          else
            impliesNot(action(layer, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  private def stateChange(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.effect.conjuncts collect {
        // negated effect is also contained, ignore this one if it is negative
        case Literal(pred, isPositive, _) if !((task.effect.conjuncts exists { l => l.predicate == pred && l.isNegative == isPositive }) && !isPositive) =>
          // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(action(layer, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  // maintains the state only if all actions are actually executed
  private def maintainState(layer: Int, position: Int): Seq[Clause] = domain.predicates flatMap {
    predicate =>
      true :: false :: Nil map {
        makeItPositive =>
          val changingActions: Seq[Task] = (if (makeItPositive) domain.primitiveChangingPredicate(predicate)._1 else domain.primitiveChangingPredicate(predicate)._2) filter
            primitivePaths(position)._2.contains

          val taskLiterals = changingActions map { action(layer, primitivePaths(position)._1, _) } map { (_, true) }
          Clause(taskLiterals.+:(statePredicate(layer, position, predicate), makeItPositive).+:(statePredicate(layer, position + 1, predicate), !makeItPositive))
      }
  }

  //def pathSortingFunction(pathA: Seq[Int], pathB: Seq[Int]): Int = path.foldLeft(0)({ case (acc, v) => acc * domain.maximumMethodSize + v })
  def pathSortingFunction(pathA: Seq[Int], pathB: Seq[Int]): Boolean =
    pathA.zip(pathB).foldLeft[Option[Boolean]](None)(
      {
        case (Some(x), _)     => Some(x)
        case (None, (p1, p2)) => if (p1 < p2) Some(true) else if (p1 > p2) Some(false) else None
      }).getOrElse(false)

  lazy val (computedDecompositionFormula, primitivePaths) = {
    val allOrderingsInitialPlan = initialPlan.orderingConstraints.graph.allTotalOrderings.get
    assert(allOrderingsInitialPlan.length == 1)
    val initialPlanOrdering = allOrderingsInitialPlan.head

    val initialPlanClauses = initialPlanOrdering.zipWithIndex map { case (task, index) => generateDecompositionFormula(0, index :: Nil, Set(task.schema)) }
    val assertedTasks = initialPlanOrdering.zipWithIndex map { case (task, index) => Clause(action(0, index :: Nil, task.schema)) }

    val paths = initialPlanClauses flatMap { _._2 }
    paths.foreach { p => assert(p._1.length == paths.head._1.length) }
    val dec = initialPlanClauses flatMap { _._1 }

    println(dec.length)
    (dec ++ assertedTasks, initialPlanClauses flatMap { _._2 } sortWith { case ((p1, _), (p2, _)) => pathSortingFunction(p1, p2) }) //sortBy { case (p, _) => pathSortingFunction(p) })
  }

  override lazy val decompositionFormula: Seq[Clause] = computedDecompositionFormula

  override lazy val stateTransitionFormula: Seq[Clause] = primitivePaths.indices flatMap { position =>
    primitivesApplicable(K, position) ++ stateChange(K, position) ++ maintainState(K, position)
  }

  override lazy val noAbstractsFormula: Seq[Clause] =
    primitivePaths flatMap { case (position, tasks) => tasks filter { _.isAbstract } map { task => Clause((action(K, position, task), false)) } }

  override lazy val goalState: Seq[Clause] =
    initialPlan.goal.substitutedPreconditions map { case Literal(predicate, isPos, _) => Clause((statePredicate(K, primitivePaths.length, predicate), isPos)) }

  lazy val initialState: Seq[Clause] = {
    val initiallyTruePredicates = initialPlan.init.substitutedEffects collect { case Literal(pred, true, _) => pred }

    val initTrue = initiallyTruePredicates map { predicate => Clause((statePredicate(K, 0, predicate), true)) }
    val initFalse = domain.predicates diff initiallyTruePredicates map { pred => Clause((statePredicate(K, 0, pred), false)) }

    initTrue ++ initFalse
  }

  override lazy val givenActionsFormula: Seq[Clause] = ???
}

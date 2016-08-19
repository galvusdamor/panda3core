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
  private val action: ((Int, Seq[Int], Task)) => String = memoise[(Int, Seq[Int], Task), String]({ case (l, p, t) =>
    assert(p.length == l + 1)
    "action^" + l + "_" + p.mkString(";") + "," + taskIndex(t)
                                                                                                 })

  // layer, path to that action, actual method
  private val method: ((Int, Seq[Int], Int)) => String = memoise[(Int, Seq[Int], Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos.mkString(";") + "," + methodIdx })

  protected val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  // returns all clauses needed for the decomposition and all paths to the last layer
  private def generateDecompositionFormula(layer: Int, path: Seq[Int], possibleTasks: Set[Task]): (Seq[Clause], Set[Seq[Int]]) = {
    // write myself
    val possibleTasksClauses: Seq[Clause] = atMostOneOf(possibleTasks map { t => action(layer, path, t) } toSeq)

    if (layer == K) (possibleTasksClauses, Set(path))
    else {
      val (possibleAbstracts, possiblePrimitives) = possibleTasks partition { _.isAbstract }

      // create a mutable structure to keep track of all possible tasks per position
      val possibleTasksPerChildPosition = new mutable.HashMap[Int, mutable.Set[Task]]()
      Range(0, domain.maximumMethodSize) foreach { possibleTasksPerChildPosition(_) = new mutable.HashSet[Task]() }

      // primitives must be inherited
      val keepPrimitives = possiblePrimitives.toSeq flatMap { primitive =>
        possibleTasksPerChildPosition(0) += primitive

        // keep the primitive at position 0 and don't allow anything else
        val otherActions = Range(1, domain.maximumMethodSize) flatMap { index =>
          domain.tasks map { task => action(layer + 1, path :+ index, task) }
        }

        impliesRightAnd(action(layer, path, primitive) :: Nil, action(layer + 1, path :+ 0, primitive) :: Nil) ++ impliesAllNot(action(layer, path, primitive), otherActions)
      }

      val decomposeAbstract: Seq[Clause] = possibleAbstracts.toSeq flatMap { abstractTask =>
        // select a method
        val possibleMethods = domain.decompositionMethods.zipWithIndex filter { _._1.abstractTask == abstractTask } map { case (m, idx) => (m, method(layer, path, idx)) }

        // one method must be applied
        val oneMustBeApplied = impliesRightOr(action(layer, path, abstractTask) :: Nil, possibleMethods map { _._2 })
        val atMostOneCanBeApplied = atMostOneOf(possibleMethods map { _._2 })

        // if a method is applied it will have children
        val methodChildren = possibleMethods flatMap { case (decompositionMethod, methodString) =>
          val allTotalOrderings = decompositionMethod.subPlan.orderingConstraints.graph.allTotalOrderings.get
          assert(allTotalOrderings.length == 1)
          val taskOrdering = allTotalOrderings.head map { _.schema }

          val childAtoms = taskOrdering.zipWithIndex map { case (task, index) =>
            possibleTasksPerChildPosition(index) += task
            action(layer + 1, path :+ index, task)
          }

          // unused are not allowed to contain anything
          val unusedActions = Range(taskOrdering.length, domain.maximumMethodSize) flatMap { index =>
            domain.tasks map { task => action(layer + 1, path :+ index, task) }
          }

          impliesRightAnd(methodString :: Nil, childAtoms) ++ impliesAllNot(methodString, unusedActions)
        }

        methodChildren ++ atMostOneCanBeApplied :+ oneMustBeApplied
      }

      // if there is nothing select at the current position we are not allowed to create new tasks
      val noneApplied = {
        val allActionAtoms = possibleTasks.toSeq map { action(layer, path, _) }

        val allChildren = Range(0, domain.maximumMethodSize) flatMap { index =>
          domain.tasks map { task => action(layer + 1, path :+ index, task) }
        }

        notImpliesAllNot(allActionAtoms, allChildren)
      }

      // run recursive decent to get the formula for the downwards layers
      val subFormulas = possibleTasksPerChildPosition filter { _._2.nonEmpty } map { case (index, childPossibleTasks) =>
        generateDecompositionFormula(layer + 1, path :+ index, childPossibleTasks.toSet)
      }

      val recursiveFormula = subFormulas flatMap { _._1 }
      val paths = subFormulas flatMap { _._2 } toSet

      (keepPrimitives ++ decomposeAbstract ++ noneApplied ++ recursiveFormula, paths)
    }
  }


  private def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = domain.primitiveTasks flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, primitivePaths(position), task), statePredicate(layer, position, pred))
          else
            impliesNot(action(layer, primitivePaths(position), task), statePredicate(layer, position, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  private def stateChange(layer: Int, position: Int): Seq[Clause] = domain.primitiveTasks flatMap {
    case task: ReducedTask =>
      task.effect.conjuncts collect {
        // negated effect is also contained, ignore this one if it is negative
        case Literal(pred, isPositive, _) if !((task.effect.conjuncts exists { l => l.predicate == pred && l.isNegative == isPositive }) && !isPositive) =>
          // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, primitivePaths(position), task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(action(layer, primitivePaths(position), task), statePredicate(layer, position + 1, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  // maintains the state only if all actions are actually executed
  private def maintainState(layer: Int, position: Int): Seq[Clause] = domain.predicates flatMap {
    predicate =>
      true :: false :: Nil map {
        makeItPositive =>
          val changingActions: Seq[Task] = domain.primitiveTasks filter {
            case task: ReducedTask => task.effect.conjuncts exists { l =>
              val matching = l.predicate == predicate && l.isPositive == makeItPositive

              if ((task.effect.conjuncts exists { l => l.predicate == predicate && l.isNegative == makeItPositive }) && !makeItPositive)
                false
              else matching

            }
            case _                 => noSupport(FORUMLASNOTSUPPORTED)
          }
          val taskLiterals = changingActions map { action(layer, primitivePaths(position), _) } map { (_, true) }
          Clause(taskLiterals :+(statePredicate(layer, position, predicate), makeItPositive) :+(statePredicate(layer, position + 1, predicate), !makeItPositive))
      }
  }

  def pathSortingFunction(path: Seq[Int]): Int = path.foldLeft(0)({ case (acc, v) => acc * domain.maximumMethodSize + v })

  private lazy val (computedDecompositionFormula, primitivePaths) = {
    val allOrderingsInitialPlan = initialPlan.orderingConstraints.graph.allTotalOrderings.get
    assert(allOrderingsInitialPlan.length == 1)
    val initialPlanOrdering = allOrderingsInitialPlan.head

    val initialPlanClauses = initialPlanOrdering.zipWithIndex map { case (task, index) => generateDecompositionFormula(0, index :: Nil, Set(task.schema)) }

    val paths = initialPlanClauses flatMap { _._2 }
    paths.foreach { p => assert(p.length == paths.head.length) }

    (initialPlanClauses flatMap { _._1 }, initialPlanClauses flatMap { _._2 } sortBy pathSortingFunction)
  }

  override lazy val decompositionFormula: Seq[Clause] = computedDecompositionFormula

  override lazy val stateTransitionFormula: Seq[Clause] = primitivePaths.indices flatMap { position =>
    primitivesApplicable(K, position) ++ stateChange(K, position) ++ maintainState(K, position)
  }

  override lazy val noAbstractsFormula: Seq[Clause] = primitivePaths flatMap { position => domain.abstractTasks map { task => Clause((action(K, position, task), false)) } }

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

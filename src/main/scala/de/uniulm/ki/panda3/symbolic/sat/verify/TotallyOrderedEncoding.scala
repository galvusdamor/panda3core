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
case class TotallyOrderedEncoding(domain: Domain, initialPlan: Plan, taskSequenceLength: Int, offsetToK: Int) extends PathBasedEncoding {

  val numberOfChildrenClauses: Int = 0

  assert(domain.isTotallyOrdered)
  assert(initialPlan.orderingConstraints.isTotalOrder())

  protected val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: scala.Seq[Task]): Seq[Clause] = Nil

  private def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(pathAction(layer, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
          else
            impliesNot(pathAction(layer, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
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
            impliesSingle(pathAction(layer, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(pathAction(layer, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
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

          val taskLiterals = changingActions map { pathAction(layer, primitivePaths(position)._1, _) } map { (_, true) }
          Clause(taskLiterals.+:(statePredicate(layer, position, predicate), makeItPositive).+:(statePredicate(layer, position + 1, predicate), !makeItPositive))
      }
  }

  override lazy val stateTransitionFormula: Seq[Clause] = primitivePaths.indices flatMap { position =>
    primitivesApplicable(K, position) ++ stateChange(K, position) ++ maintainState(K, position)
  }

  override lazy val noAbstractsFormula: Seq[Clause] =
    primitivePaths flatMap { case (position, tasks) => tasks filter { _.isAbstract } map { task => Clause((pathAction(K, position, task), false)) } }

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

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain.{Task, ReducedTask}
import de.uniulm.ki.panda3.symbolic.logic.{Predicate, Literal}
import de.uniulm.ki.util._

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait LinearPrimitivePlanEncoding extends VerifyEncoding {

  val action: ((Int, Int, Task)) => String = memoise[(Int, Int, Task), String]({ case (l, p, t) => "action^" + l + "_" + p + "," + taskIndex(t) })

  protected final val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  protected final def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = domain.primitiveTasks flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, position, task), statePredicate(layer, position, pred))
          else
            impliesNot(action(layer, position, task), statePredicate(layer, position, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  protected final def stateChange(layer: Int, position: Int): Seq[Clause] = domain.primitiveTasks flatMap {
    case task: ReducedTask =>
      task.effect.conjuncts collect {
        // negated effect is also contained, ignore this one if it is negative
        case Literal(pred, isPositive, _) if !((task.effect.conjuncts exists { l => l.predicate == pred && l.isNegative == isPositive }) && !isPositive) =>
          // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, position, task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(action(layer, position, task), statePredicate(layer, position + 1, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  // maintains the state only if all actions are actually executed
  protected final def maintainState(layer: Int, position: Int): Seq[Clause] = {
    domain.predicates flatMap {
      predicate =>
        true :: false :: Nil map {
          makeItPositive =>
            val changingActions: Seq[Task] = if (makeItPositive) domain.primitiveChangingPredicate(predicate)._1 else domain.primitiveChangingPredicate(predicate)._2
            val taskLiterals = changingActions map { action(layer, position, _) } map { (_, true) }
            Clause(taskLiterals :+(statePredicate(layer, position, predicate), makeItPositive) :+(statePredicate(layer, position + 1, predicate), !makeItPositive))
        }
    }
  }


  def stateTransitionFormulaOfLength(length: Int): Seq[Clause] = Range(0, length) flatMap { position =>
    primitivesApplicable(K - 1, position) ++ stateChange(K - 1, position) ++ maintainState(K - 1, position)
  }

}

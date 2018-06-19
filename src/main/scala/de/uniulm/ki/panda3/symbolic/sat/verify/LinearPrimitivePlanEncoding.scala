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

  def noAbstractsFormulaOfLength(length: Int): Seq[Clause] = Range(0, length) flatMap { position => domain.abstractTasks map { task => Clause((action(K - 1, position, task), false)) } }

  lazy val initialState: Seq[Clause] = {
    val initiallyTruePredicates = initialPlan.init.substitutedEffects collect { case Literal(pred, true, _) => pred }

    val initTrue = initiallyTruePredicates map { pred => Clause((statePredicate(K - 1, 0, pred), true)) }
    val initFalse = domain.predicates diff initiallyTruePredicates map { pred => Clause((statePredicate(K - 1, 0, pred), false)) }

    initTrue ++ initFalse
  }

  def goalStateOfLength(length: Int): Seq[Clause] =
    initialPlan.goal.substitutedPreconditions map { case Literal(pred, isPos, _) => Clause((statePredicate(K - 1, length, pred), isPos))    }

}

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

package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.logic.{EfficientGroundLiteral, EfficientLiteral}

import scala.collection.BitSet
import scala.collection.mutable.ArrayBuffer

/**
  * The representation of a task in the efficient domain.
  *
  * The parameters of this task are numbered 0..sz(parameterSorts) and the ith sort is the type of the variable i.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTask(isPrimitive: Boolean, parameterSorts: Array[Int], constraints: Array[EfficientVariableConstraint], precondition: Array[EfficientLiteral],
                         effect: Array[EfficientLiteral], allowedToInsert: Boolean, initOrGoalTask: Boolean) {

  val isAbstract = !isPrimitive

  /** given a literal of this task and the actual parameters of this task, it returns the actual arguments of the literal */
  def getArgumentsOfLiteral(taskArguments: Array[Int], literal: EfficientLiteral): Array[Int] = {
    val arguments = new Array[Int](literal.parameterVariables.length)
    var i = 0
    while (i < literal.parameterVariables.length) {
      arguments(i) = taskArguments(literal.parameterVariables(i))
      i += 1
    }
    arguments
  }


  def applyArgumentsToConstraint(taskArguments: Array[Int], constraint: EfficientVariableConstraint): EfficientVariableConstraint = {
    if (constraint.constraintType == EfficientVariableConstraint.EQUALVARIABLE || constraint.constraintType == EfficientVariableConstraint.UNEQUALVARIABLE)
      EfficientVariableConstraint(constraint.constraintType, taskArguments(constraint.variable), taskArguments(constraint.other))
    else
      EfficientVariableConstraint(constraint.constraintType, taskArguments(constraint.variable), constraint.other)
  }

  lazy val (positiveEffectPredicates, negativeEffectPredicates)             = effect partition { _.isPositive } match {case (a, b) => (a map { _.predicate }, b map { _.predicate })}
  lazy val (positivePreconditionPredicates, negativePreconditionPredicates) = precondition partition { _.isPositive } match {case (a, b) => (a map { _.predicate }, b map { _.predicate })}

}


//scalastyle:off covariant.equals
case class EfficientGroundTask(taskID: Int, arguments: Array[Int]) {

  // TODO we shouldn't do this as inefficiently as we do it .... we recomute the substituted precs and effs every time, but they can't change
  def substitutedPrecondition(preconditionIndex: Int, domain: EfficientDomain): EfficientGroundLiteral = {
    val task = domain.tasks(taskID)
    val precondition = task.precondition(preconditionIndex)
    val openPreconditionArguments = task.getArgumentsOfLiteral(arguments, precondition)
    EfficientGroundLiteral(precondition.predicate, isPositive = true, openPreconditionArguments)
  }


  def substitutedEffect(effectIndex: Int, domain: EfficientDomain): EfficientGroundLiteral = {
    val task = domain.tasks(taskID)
    val effect = task.effect(effectIndex)
    val openPreconditionArguments = task.getArgumentsOfLiteral(arguments, effect)
    EfficientGroundLiteral(effect.predicate, isPositive = effect.isPositive, openPreconditionArguments)
  }


  // we need a special equals as we use arrays
  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[EfficientGroundTask]) {
    val that = o.asInstanceOf[EfficientGroundTask]
    if (this.taskID != that.taskID) false else this.arguments sameElements that.arguments
  } else false

  override def hashCode(): Int = taskID
}

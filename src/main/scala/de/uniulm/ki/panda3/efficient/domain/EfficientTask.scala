package de.uniulm.ki.panda3.efficient.domain

import de.uniulm.ki.panda3.efficient.csp.{EfficientVariableConstraint}
import de.uniulm.ki.panda3.efficient.logic.{EfficientGroundLiteral, EfficientLiteral}

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

}


//scalastyle:off covariant.equals
case class EfficientGroundTask(taskID: Int, arguments: Array[Int]) {

  // TODO we shouldn't do this as inefficiently as we do it .... we recomute the substituted precs and effs every time, but they can't change
  def substitutedPrecondition(preconditionIndex : Int, domain : EfficientDomain) : EfficientGroundLiteral = {
    val task = domain.tasks(taskID)
    val precondition = task.precondition(preconditionIndex)
    val openPreconditionArguments = task.getArgumentsOfLiteral(arguments, precondition)
    EfficientGroundLiteral(precondition.predicate, isPositive = true, openPreconditionArguments)
  }


  def substitutedEffect(effectIndex : Int, domain : EfficientDomain) : EfficientGroundLiteral = {
    val task = domain.tasks(taskID)
    val effect = task.effect(effectIndex)
    val openPreconditionArguments = task.getArgumentsOfLiteral(arguments, effect)
    EfficientGroundLiteral(effect.predicate, isPositive = true, openPreconditionArguments)
  }



  // we need a special equals as we use arrays
  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[EfficientGroundTask]) {
    val that = o.asInstanceOf[EfficientGroundTask]
    if (this.taskID != that.taskID) false else this.arguments sameElements that.arguments
  } else false

  override def hashCode(): Int = taskID
}
package de.uniulm.ki.panda3.symbolic.search.progression

import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{PartiallyInstantiatedTask, GroundTask}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class State(trueLiterals: Seq[GroundLiteral]) {
  private val stateAsSet = trueLiterals.toSet
  trueLiterals foreach { gl => assert(!(stateAsSet contains gl.negate)) }

  def isApplicable(groundTask: GroundTask): Boolean = groundTask.substitutedPreconditions.toSet subsetOf stateAsSet

  def apply(groundTask: GroundTask): Option[State] = if (isApplicable(groundTask)) {
    val literalsToRemove = groundTask.substitutedEffects map { _.negate }
    val newStateLiterals = (stateAsSet -- literalsToRemove) ++ groundTask.substitutedEffects

    Some(State(newStateLiterals.toSeq))
  } else None

  def apply(partiallyInstantiatedTask: PartiallyInstantiatedTask): Option[State] = {
    val allInstantiations = partiallyInstantiatedTask.allInstantiations
    val successorStates = allInstantiations map apply collect { case Some(gt) => gt } distinct

    if (successorStates.isEmpty || successorStates.length >= 2) None else Some(successorStates.head)
  }
}

object State {
  def apply(plan: Plan): State = State(plan.groundedInitialState)
}

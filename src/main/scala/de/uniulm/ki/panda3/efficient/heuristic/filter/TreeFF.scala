package de.uniulm.ki.panda3.efficient.heuristic.filter

import de.uniulm.ki.panda3.efficient.domain.{EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.logic.EfficientLiteral
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan

import scala.collection.{mutable, BitSet}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TreeFF(domain: EfficientDomain) {

  // we assume that negative preconditions have been compiled and that we are ground
  assert(domain.isGround)
  assert(domain.noNegativePreconditions)

  /*domain.insertableTasks foreach {task =>
      task.effect filter {_.isNegative} foreach { eff => assert((task.effect filter {_.isPositive} filter {_.predicate == eff.predicate}).isEmpty)    }
  }*/


  private def linkViolation(links: BitSet, task: EfficientTask): Boolean = task.effect exists { case EfficientLiteral(pred, isPos, _) => !isPos && links.contains(pred) }


  /** expands the state */
  private def ff(state: BitSet, links: BitSet, usefulActions: Array[EfficientTask]): (BitSet, Array[EfficientTask]) = {
    var changed = true
    val finalState = mutable.BitSet(state.toArray: _*)

    var (linkForbiddenActions, actions) = usefulActions partition {linkViolation(links,_)}

    while (changed) {
      // applicable actions
      val (applicableActions, nonApplicableActions) = actions partition { _.precondition forall { finalState contains _.predicate } }
      changed = applicableActions.nonEmpty

      applicableActions foreach { _.effect filter { _.isPositive } foreach { finalState add _.predicate } }
      actions = nonApplicableActions
    }

    (BitSet(finalState.toArray: _*), linkForbiddenActions ++ actions)
  }


  def isPossiblySolvable(plan: EfficientPlan): Boolean = {
    // TODO using bitsets for causal links here is not totally correct (overlapping links!)
    val initial: (BitSet, BitSet, Array[EfficientTask]) = (BitSet(), BitSet(), domain.insertableTasks)

    def progress(stateAndLinks: (BitSet, BitSet, Array[EfficientTask]), ps: Int): ((BitSet, BitSet, Array[EfficientTask]), Boolean) =
      if (!plan.isPlanStepPresentInPlan(ps)) (stateAndLinks, true)
      else {
        // run ff towards the planstep
        val (reachableFacts, remainingActions) = ff(stateAndLinks._1, stateAndLinks._2, stateAndLinks._3)

        // check applicability
        val psTask = plan.taskOfPlanStep(ps)
        val applicable = psTask.precondition forall { reachableFacts contains _.predicate }


        if (!applicable) (stateAndLinks, false)
        else {
          // update the state
          val deleteEffect = psTask.effect filter { _.isNegative } map { _.predicate }
          val updatedState = (reachableFacts -- deleteEffect) ++ (psTask.effect filter { _.isPositive } map { _.predicate })

          // update the links
          // TODO count links
          val endingLinks = plan.causalLinks collect { case l if l.consumer == ps => psTask.precondition(l.conditionIndexOfConsumer).predicate }
          val startingLinks: Array[Int] = plan.causalLinks collect { case l if l.producer == ps => psTask.effect(l.conditionIndexOfProducer).predicate }
          val updatedLinks: BitSet = (stateAndLinks._2 -- endingLinks) ++ startingLinks

          // add actions that might be necessary again
          val actionsAddingDeletedEffects: Array[EfficientTask] =
            deleteEffect flatMap { p => domain.possibleProducerTasksOf(p)._1 } map { case (t, _) => domain.tasks(t) } filter { _.allowedToInsert }

          ((updatedState, updatedLinks, (remainingActions ++ actionsAddingDeletedEffects).distinct), true)
        }
      }

    plan.ordering.existsLinearisationWithPropertyFold(initial, progress)
  }
}
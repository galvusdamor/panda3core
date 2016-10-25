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
  private def ff(state: BitSet, linkProtection: Array[Int]): (BitSet, Array[Int]) = {
    var changed = true
    val finalState = mutable.BitSet(state.toArray: _*)

    val actions = new Array[Int](linkProtection.length)

    var i = 0
    while (i < linkProtection.length) {
      actions(i) = 0
      if (linkProtection(i) == 0) {
        var j = 0
        val positivePreconditions = domain.tasks(i).positivePreconditionPredicates
        while (j < positivePreconditions.length) {
          if (!finalState.contains(positivePreconditions(j)))
            actions(i) += 1
          j += 1
        }
      }
      i += 1
    }


    while (changed) {
      changed = false
      // apply applicable actions
      i = 0
      while (i < actions.length) {
        if (actions(i) == 0 && linkProtection(i) == 0) {
          // an action was applied
          changed = true
          actions(i) = -1 // already applied
          linkProtection(i) = -1 // not useful any more
          val addEffects = domain.tasks(i).positiveEffectPredicates
          var j = 0
          while (j < addEffects.length) {
            if (!finalState.contains(addEffects(j))) {
              // add new predicate to the state
              finalState.add(addEffects(j))
              // decrement respective precondition counters
              var k = 0
              while (k < actions.length) {
                if (domain.tasks(k).positivePreconditionPredicatesBitSet contains addEffects(j))
                  actions(k) -= 1
                k += 1
              }
            }
            j += 1
          }
        }
        i += 1
      }
    }

    (BitSet(finalState.toArray: _*), linkProtection)
  }


  def isPossiblySolvable(plan: EfficientPlan): Boolean = {
    // state, link-protectedLiterals, status of actions under links (0 - applicable, -1 - applied, -2 - never allowed, x > 0 - forbidden by link)
    val usefulTasks = new Array[Int](domain.tasks.length)
    var i = 0
    while (i < usefulTasks.length) {
      if (!domain.tasks(i).allowedToInsert) usefulTasks(i) = TreeFF.NEVER
      i += 1
    }
    val initial: (BitSet, Array[Int], Array[Int]) = (BitSet(), new Array[Int](domain.predicates.length), usefulTasks)

    def progress(stateAndLinks: (BitSet, Array[Int], Array[Int]), ps: Int):
    ((BitSet, Array[Int], Array[Int]), Boolean) =
      if (!plan.isPlanStepPresentInPlan(ps)) (stateAndLinks, true)
      else {
        // run ff towards the planstep
        val oldEffectProtection = new Array[Int](stateAndLinks._3.length)
        Array.copy(stateAndLinks._3, 0, oldEffectProtection, 0, oldEffectProtection.length)
        val (reachableFacts, newEffectProtection) = ff(stateAndLinks._1,oldEffectProtection)

        // check applicability
        val psTask = plan.taskOfPlanStep(ps)
        val applicable = psTask.precondition forall { reachableFacts contains _.predicate }


        if (!applicable) (stateAndLinks, false)
        else {
          // update the state
          val deleteEffect : Array[Int] = psTask.effect filter { _.isNegative } map { _.predicate }
          val updatedState = (reachableFacts -- deleteEffect) ++ (psTask.effect filter { _.isPositive } map { _.predicate })

          // update the links
          // copy
          val newLinkProtections = new Array[Int](domain.predicates.length)
          Array.copy(stateAndLinks._2, 0, newLinkProtections, 0, newLinkProtections.length)
          // links ending here
          val endingLinks: Array[Int] = plan.causalLinks collect { case l if l.consumer == ps => psTask.precondition(l.conditionIndexOfConsumer).predicate }
          var i = 0
          while (i < endingLinks.length) {
            newLinkProtections(endingLinks(i)) -= 1
            if (newLinkProtections(endingLinks(i)) == 0) {
              // if the action as the link's literal as an effect reduce the number of currently forbidden negative effects
              var j = 0
              while (j < newEffectProtection.length) {
                if (newEffectProtection(j) > 0 && domain.tasks(j).negativeEffectPredicatesBitSet.contains(endingLinks(i)))
                  newEffectProtection(j) -= 1
                j += 1
              }
            }
            i += 1
          }
          // links starting here
          val startingLinks: Array[Int] = plan.causalLinks collect { case l if l.producer == ps => psTask.effect(l.conditionIndexOfProducer).predicate }
          i = 0
          while (i < startingLinks.length) {
            if (newLinkProtections(startingLinks(i)) == 0) {
              // if the action as the link's literal as an effect increase the number of currently forbidden negative effects
              var j = 0
              while (j < newEffectProtection.length) {
                if (newEffectProtection(j) >= 0 && domain.tasks(j).negativeEffectPredicatesBitSet.contains(startingLinks(i)))
                  newEffectProtection(j) += 1
                j += 1
              }
            }
            newLinkProtections(startingLinks(i)) += 1
            i += 1
          }


          // we may have to run actions again which add effects of the now deleted state features
          i = 0
          while (i < deleteEffect.length){
            val producersOf : Array[(Int, Int)] = domain.possibleProducerTasksOf(deleteEffect(i))._1 // (taskID, effectID)
            var j = 0
            while (j < producersOf.length){
              val task = producersOf(j)._1
              if (newEffectProtection(task) == -1){
                // count causal links
                newEffectProtection(task) = 0
                val negativeEffects = domain.tasks(task).negativeEffectPredicates
                var k = 0
                while (k < negativeEffects.length){
                  newEffectProtection(task) += newLinkProtections(negativeEffects(k))
                  k += 1
                }
              }
              j += 1
            }
            i += 1
          }

          ((updatedState, newLinkProtections, newEffectProtection), true)
        }
      }

    plan.ordering.existsLinearisationWithPropertyFold(initial, progress)
  }
}

object TreeFF {
  val NEVER = -2
}
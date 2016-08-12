package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.plan.Plan

import scala.util.Random

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class RandomPlanGenerator(domain: Domain, plan: Plan) {
  // this currently only works for grounded domains
  assert(domain.predicates forall { _.argumentSorts.isEmpty })

  def randomExecutablePlan(length: Int, seed: Int): Seq[Task] = {
    val r = new Random(seed)

    def randomWalk(state: Set[Predicate], length: Int): Option[Seq[Task]] = if (length == 0) Some(Nil)
    else {
      // all applicable tasks
      val applicableTasks = domain.primitiveTasks filter {
        case rt: ReducedTask => rt.precondition.conjuncts forall { case Literal(predicate, isPositive, _) => (state contains predicate) == isPositive }
        case _               => false
      } sortBy { _ => r.nextInt() }

      if (applicableTasks.isEmpty) None
      else applicableTasks.foldLeft[Option[Seq[Task]]](None)(
        {
          case (Some(x), _) => Some(x)
          case (None, task) =>
            val delEffects = task.asInstanceOf[ReducedTask].effect.conjuncts collect { case x if !x.isPositive => x.predicate } toSet

            val addEffects = task.asInstanceOf[ReducedTask].effect.conjuncts collect { case x if x.isPositive => x.predicate } toSet

            val resultState = (state diff delEffects) ++ addEffects

            randomWalk(resultState, length - 1) match {
              case Some(seq) => Some(task +: seq)
              case None      => None
            }
        })
    }

    randomWalk(plan.init.substitutedEffects collect { case l if l.isPositive => l.predicate } toSet, length).get
  }
}

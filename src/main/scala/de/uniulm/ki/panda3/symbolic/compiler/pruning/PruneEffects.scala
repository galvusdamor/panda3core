package de.uniulm.ki.panda3.symbolic.compiler.pruning

import de.uniulm.ki.panda3.symbolic.compiler.DomainTransformer
import de.uniulm.ki.panda3.symbolic.domain.updates.{RemovePredicate, RemoveEffects}
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.{Predicate, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic._

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object PruneEffects extends DomainTransformer[(Set[Task], Set[String])] {

  /** takes a domain, an initial plan and some additional Information and transforms them */
  override def transform(domain: Domain, plan: Plan, config: (Set[Task], Set[String])): (Domain, Plan) = {
    val (reachableTasks, predicatesToProtect) = config

    //println(domain.predicates map {_.name} mkString "\n")
    //println("TO protect")
    //println(config._2 mkString "\n")

    // determine which predicates (with negation) are actually necessary
    val necessaryPredicates: Set[(Predicate, Boolean)] = ((reachableTasks + plan.goal.schema) flatMap {
      case task: ReducedTask => task.precondition.conjuncts flatMap { case Literal(predicate, isPositive, _) => (predicate, true) :: (predicate, false) :: Nil }
      case _                 => noSupport(FORUMLASNOTSUPPORTED)
    }) ++ (predicatesToProtect flatMap {p => val pp = domain.predicates.find(_.name.startsWith(p)).get; (pp,true) :: (pp,false) :: Nil})

    val unnecessaryPredicatesWithSign = Set[(Predicate,Boolean)]() //(domain.predicates flatMap { p => (p, true) :: (p, false) :: Nil } filterNot necessaryPredicates.contains).toSet
    val unnecessaryPredicates = Set[Predicate]() //domain.predicates filter { p => (p, true) :: (p, false) :: Nil forall unnecessaryPredicatesWithSign.contains } toSet
    val effectUpdate = RemoveEffects(unnecessaryPredicatesWithSign, invertedTreatment = false)
    val predicateUpdate = RemovePredicate(unnecessaryPredicates)

    val newDom = domain update effectUpdate update predicateUpdate
    val newPlan = plan update effectUpdate

    (newDom, newPlan)
  }
}

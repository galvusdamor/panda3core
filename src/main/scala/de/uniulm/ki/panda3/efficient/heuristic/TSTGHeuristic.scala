package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDecompositionMethod, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.util.SimpleAndOrGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TSTGHeuristic(domain: EfficientDomain) extends EfficientHeuristic {

  lazy val argumentRelaxedTDG = {
    val methodsToSubTasks = domain.decompositionMethods map { m => (m, m.subPlan.planStepTasks.drop(2) map domain.tasks toSet) }
    val tasksToMethos = domain.tasks.zipWithIndex map { case (t, i) => t -> (domain.decompositionMethods filter { _.abstractTask == i }).toSet }

    SimpleAndOrGraph[Any, EfficientTask, EfficientDecompositionMethod](domain.tasks.toSet, domain.decompositionMethods.toSet,
                                                                       tasksToMethos.toMap.withDefaultValue(Set()),
                                                                       methodsToSubTasks.toMap.withDefaultValue(Set()))
  }


  val taskValues: Map[Int, Int] = domain.tasks.zipWithIndex map { case (t, i) =>
    i -> argumentRelaxedTDG.minSumTraversal(t, { _.precondition.length }, 1).toInt
  } toMap


  override def computeHeuristic(plan: EfficientPlan): Double = {
    var h = 0

    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer)) h -= 1
      cl += 1
    }

    var ps = 0
    while (ps < plan.planStepTasks.length) {
      if (plan.isPlanStepPresentInPlan(ps)) h += taskValues(plan.planStepTasks(ps))
      ps += 1
    }

    h
  }
}

package de.uniulm.ki.panda3.efficient.heuristic

import de.uniulm.ki.panda3.efficient.domain.{EfficientDecompositionMethod, EfficientTask, EfficientDomain}
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.util.SimpleAndOrGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TSTGHeuristic(domain: EfficientDomain) extends EfficientHeuristic[Unit] {

  lazy val argumentRelaxedTDG = {
    // methods will be represented by negative numbers to avoid clashes
    val methodsToSubTasks = domain.decompositionMethods.zipWithIndex map { case (m, i) => (-i, m.subPlan.planStepTasks.drop(2) toSet) }
    val tasksToMethods = domain.tasks.zipWithIndex map { case (t, i) => i -> (domain.taskToPossibleMethods(i) map { -_._2 }).toSet }

    SimpleAndOrGraph[Any, Int, Int](domain.tasks.indices.toSet, domain.decompositionMethods.indices map { case m => -m } toSet ,
                                    tasksToMethods.toMap.withDefaultValue(Set()),
                                    methodsToSubTasks.toMap.withDefaultValue(Set()))
  }


  val taskValues: Map[Int, Int] = {
    println("COMPUTE HEU")
    val h = argumentRelaxedTDG.minSumTraversalMap({ domain.tasks(_).precondition.length }, 1) map { case (a, b) => a -> b.toInt }
    println("DONE")

    h
  }

  override def computeHeuristic(plan: EfficientPlan, payload: Unit, appliedModification: EfficientModification): (Double, Unit) = {
    var h = 0

    var cl = 0
    while (cl < plan.causalLinks.length) {
      val link = plan.causalLinks(cl)
      if (plan.isPlanStepPresentInPlan(link.producer) && plan.isPlanStepPresentInPlan(link.consumer)) {
        h -= 1
      }
      cl += 1
    }

    var ps = 0
    while (ps < plan.planStepTasks.length) {
      if (plan.isPlanStepPresentInPlan(ps)) {
        h += taskValues(plan.planStepTasks(ps))
      }
      ps += 1
    }

    (h,())
  }
}

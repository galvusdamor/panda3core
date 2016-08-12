package de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain.{SimpleDecompositionMethod, Task, GroundedDecompositionMethod, Domain}
import de.uniulm.ki.panda3.symbolic.domain.datastructures.GroundedPrimitiveReachabilityAnalysis
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TopDownTaskDecompositionGraph(domain: Domain, initialPlan: Plan, groundedReachabilityAnalysis: GroundedPrimitiveReachabilityAnalysis, prunePrimitive: Boolean) extends
  TaskDecompositionGraph {

  // apparently I can't annotate a type here (Map[Task, Set[GroundTask]], Map[GroundTask, Seq[GroundedDecompositionMethod]])
  lazy val (abstractTaskGroundings, groundedDecompositionMethods) = {
    // here we rely on side-effects for speed and readability

    val abstractTasksMap = new mutable.HashMap[Task, Set[GroundTask]]().withDefaultValue(Set())
    val methodsMap = new mutable.HashMap[GroundTask, Set[GroundedDecompositionMethod]]().withDefaultValue(Set())

    def dfs(currentGroundTask: GroundTask): Unit = if (!(abstractTasksMap(currentGroundTask.task) contains currentGroundTask) && currentGroundTask.task.isAbstract) {
      // add the ground task to its map
      abstractTasksMap(currentGroundTask.task) = abstractTasksMap(currentGroundTask.task) + currentGroundTask

      // if the task is abstract, we have to ground it
      // we have a partial variable binding from the abstract task
      val possibleMethods = (domain.decompositionMethods :+ topMethod) filter { _.abstractTask == currentGroundTask.task } flatMap {
        case simpleMethod: SimpleDecompositionMethod => simpleMethod.groundWithAbstractTaskGrounding(currentGroundTask)
        case _                                       => noSupport(NONSIMPLEMETHOD)
      } filterNot methodsMap(currentGroundTask).contains
      // add the new methods to the map
      methodsMap(currentGroundTask) = methodsMap(currentGroundTask) ++ possibleMethods

      // perform recursion
      possibleMethods flatMap { _.subPlanPlanStepsToGrounded.values } foreach dfs
    }

    dfs(groundedTopTask)


    (abstractTasksMap.toMap, methodsMap.toMap)
  }
}

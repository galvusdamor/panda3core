package de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.groundedplanninggraph

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.domain.datastructures.primitivereachability.groundedplanninggraph.DebuggingMode.DebuggingMode
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Kristof Mickeleit (kristof.mickeleit@uni-ulm.de)
  *
  *         Configuration for a GroundedPlanningGraph
  * @constructor Creates a new configuration with the given parameters.
  * @param computeMutexes         If True the GroundedPlanningGraph will compute task and proposition mutexes.
  * @param isSerial               If True the GroundedPlanningGraph will compute additional mutexes making actions mutex with each other.
  * @param forbiddenLiftedTasks   Set of lifted tasks the GroundedPlanningGraph is forbid to instantiate.
  * @param forbiddenGroundedTasks Set of grounded tasks the GroundedPlanningGraph is forbid to instantiate.
  * @param buckets                Determines if buckets will be used for mutex computation.
  * @param debuggingMode          Determines what will be printed during the computation of the graph.
  */
case class GroundedPlanningGraphConfiguration(computeMutexes: Boolean = true,
                                              isSerial: Boolean = false,
                                              forbiddenLiftedTasks: Set[Task] = Set.empty[Task],
                                              forbiddenGroundedTasks: Set[GroundTask] = Set.empty[GroundTask],
                                              buckets: Boolean = false,
                                              debuggingMode: DebuggingMode = DebuggingMode.Disabled) {
}

object DebuggingMode extends Enumeration {
  type DebuggingMode = Value
  val Disabled, Short, Medium, Long = Value
}

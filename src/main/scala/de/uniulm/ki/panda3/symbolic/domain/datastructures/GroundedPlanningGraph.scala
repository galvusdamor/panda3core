package de.uniulm.ki.panda3.symbolic.domain.datastructures

import de.uniulm.ki.panda3.symbolic.domain.{Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.GroundLiteral
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class GroundedPlanningGraph(domain: Domain, initialState: Set[GroundLiteral], computeMutexes: Boolean, isSerial: Boolean, disallowedTasks: Either[Seq[GroundTask], Seq[Task]]) extends
  GroundedReachabilityAnalysis {


  lazy val graphSize : Int = ???

  // This function should compute the actual planning graph
  override protected lazy val layer: Seq[(Set[GroundTask], Set[GroundLiteral])] = {

    ???
  }
}

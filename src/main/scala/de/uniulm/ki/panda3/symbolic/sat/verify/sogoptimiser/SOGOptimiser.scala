package de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser

import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.util.DirectedGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait SOGOptimiser {

  def minimalSOG(graphs: Seq[DirectedGraph[PlanStep]]) : (DirectedGraph[Int], Seq[Map[PlanStep, Int]])
}

case class OptimalBranchAndBoundOptimiser(criterion : ((DirectedGraph[Int], Seq[Map[PlanStep, Int]]) => Int), lowerBound : Int) extends SOGOptimiser {
  override def minimalSOG(graphs: Seq[DirectedGraph[PlanStep]]) : (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) =
    DirectedGraph.minimalInducedSuperGraph(graphs, criterion, lowerBoundOnMetric = lowerBound)
}
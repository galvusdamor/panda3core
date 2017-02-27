package de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser

import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.util.{SimpleDirectedGraph, DirectedGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object GreedyNumberOfAbstractChildrenOptimiser extends SOGOptimiser {

  override def minimalSOG(graphs: Seq[DirectedGraph[PlanStep]]): (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) = {


    def matchGraph(currentGraph: DirectedGraph[Int], currentMapping: Seq[Map[PlanStep, Int]], nextGraph: DirectedGraph[PlanStep])
    : (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) = {
      val myMapping = currentMapping.last
      val (mappedNodesList, unmappedNodes) = nextGraph.vertices partition { ps => myMapping.contains(ps) }
      val mappedNodes = mappedNodesList.toSet
      if (unmappedNodes.isEmpty) {
        assert(nextGraph.vertices forall { myMapping.contains })
        (currentGraph, currentMapping)
      }
      else {
        // select the planStep to be mapped next
        val planStepToMap = (nextGraph.topologicalOrdering.get partition { _.schema.isAbstract } match {case (a, b) => a ++ b}) filterNot mappedNodes head

        val currentlyMappedNodes = myMapping.values.toSet
        val currentAntiGraph: DirectedGraph[Int] = currentGraph.complementGraph
        // greedy options
        val remainingTasks = currentGraph.vertices filterNot currentlyMappedNodes filter { n =>
          // check in inserting whould do something forbidden
          val successorEdges = nextGraph.edges(planStepToMap) filter mappedNodes map { case after => (n, myMapping(after)) }
          val predecessorEdges = nextGraph.reversedEdgesSet(planStepToMap) filter mappedNodes map { case before => (myMapping(before), n) }

          (successorEdges ++ predecessorEdges) forall { case (from, to) => !currentAntiGraph.edgesSet(from).contains(to) }
        }

        val selectedTask = if (remainingTasks.nonEmpty) {
          // there are still options, use the one first one
          remainingTasks.head
        } else {
          // there is no viable task, so create a new one
          currentGraph.vertices.length
        }

        // construct new datastructures
        val newMapping = myMapping.+((planStepToMap, selectedTask))
        val newVertices = (currentGraph.vertices :+ selectedTask).distinct

        val successorEdges = nextGraph.edges(planStepToMap) filter mappedNodes map { case after => (selectedTask, myMapping(after)) }
        val predecessorEdges = nextGraph.reversedEdgesSet(planStepToMap) filter mappedNodes map { case before => (myMapping(before), selectedTask) }
        val newEdges = currentGraph.edgeList ++ (successorEdges ++ predecessorEdges)
        val newGraph = SimpleDirectedGraph(newVertices, newEdges)

        val newMappingList = currentMapping.take(currentMapping.length - 1) :+ newMapping

        matchGraph(newGraph, newMappingList, nextGraph)
      }
    }

    val graphReordering: Seq[(DirectedGraph[PlanStep], Int)] = graphs.zipWithIndex.sortBy(_._1.vertices.length).reverse
    val backMapping: Map[Int, Int] = graphReordering.zipWithIndex map {case ((_,oldIndex), newIndex) => oldIndex -> newIndex} toMap


    val (minimalGraph, mapping) = graphReordering.foldLeft[(DirectedGraph[Int], Seq[Map[PlanStep, Int]])]((SimpleDirectedGraph(Nil, Nil), Nil))(
      {
        case ((currentGraph, currentMapping), (nextGraph, _)) =>
          //println(nextGraph.vertices.length)
          matchGraph(currentGraph, currentMapping :+ Map(), nextGraph)
      })

    (minimalGraph, mapping.indices map { i => mapping(backMapping(i)) })
  }
}
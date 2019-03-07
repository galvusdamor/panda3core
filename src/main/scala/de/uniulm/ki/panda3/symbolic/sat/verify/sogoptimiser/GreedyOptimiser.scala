// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser

import de.uniulm.ki.panda3.symbolic.domain.{ConstantActionCost, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic.And
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.util.{DirectedGraph, SimpleDirectedGraph}

import scala.collection.mutable
import scala.io.Source

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object GreedyNumberOfAbstractChildrenOptimiser extends SOGOptimiser {

  def main(args: Array[String]): Unit = {
    val input: Seq[String] = Source.fromFile("largeSOGCombGraph.txt").getLines.toSeq

    val graphs = input.head.toInt

    val splittedGraphs = Range(0, graphs).foldLeft[(Seq[String], Seq[Seq[String]])]((input.drop(1), Nil))(
      {
        case ((in, gs), _) =>
          val gLines = in.head.split(" ").map(_.toInt).sum + 1

          (in.drop(gLines), gs :+ in.take(gLines))
      })._2

    val parsedGraphs = splittedGraphs map { glines =>
      val n = glines.head.split(" ").head.toInt
      val m = glines.head.split(" ")(1).toInt


      val nodes = glines.drop(1).take(n)
      val edges = glines.drop(1 + n).take(m)

      val mappi = nodes.zipWithIndex map { case (s, i) => i -> PlanStep(i, ReducedTask(s, true, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0)), Nil) } toMap

      val realEdges = edges map { s =>
        val ss = s.split(" ")
        mappi(ss.head.toInt) -> mappi(ss(1).toInt)
      }

      SimpleDirectedGraph(mappi.values.toSeq, realEdges)
    }

    minimalSOG(parsedGraphs)
  }

  override def minimalSOG(graphs: Seq[DirectedGraph[PlanStep]]): (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) = {

    /*if (graphs.length == 9615) {
      val builder = new StringBuffer()

      builder append graphs.length
      builder append "\n"

      graphs foreach { g =>
        val mappi = g.vertices.zipWithIndex.toMap

        builder append g.vertices.length
        builder append " "
        builder append g.edgeList.length
        builder append "\n"

        g.vertices foreach { ps => builder.append(ps.schema.name + "\n") }

        g.edgeList foreach { case (f, t) => builder.append(mappi(f) + " " + mappi(t) + "\n") }
      }

      de.uniulm.ki.util.writeStringToFile(builder.toString, "largeSOGCombGraph.txt")

      System exit 0
    }*/

    //val graphSizes = graphs.map(_.vertices.length)
    //println("ASKED FOR OVERLAY " + graphs.length + " " + graphSizes.min + " " + graphSizes.max + " " + (graphSizes.sum.toDouble / graphs.length))

    //println("totally ordered graphs " + graphs.count(g => g.transitiveClosure.edgeList.length == g.vertices.length * (g.vertices.length - 1) / 2))

    def matchGraph(currentGraph: DirectedGraph[Int], currentMapping: Seq[Map[PlanStep, Int]], nextGraph: DirectedGraph[PlanStep]): (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) = {
      val myMapping = currentMapping.last
      val (mappedNodesList, unmappedNodes) = nextGraph.vertices partition { ps => myMapping.contains(ps) }
      val mappedNodes = mappedNodesList.toSet
      if (unmappedNodes.isEmpty) {
        assert(nextGraph.vertices forall { myMapping.contains })
        (currentGraph, currentMapping)
      } else {
        // select the planStep to be mapped next
        val planStepToMap = (nextGraph.topologicalOrdering.get partition { _.schema.isAbstract } match {case (a, b) => a ++ b}) filterNot mappedNodes head

        val currentlyMappedNodes = myMapping.values.toSet
        val currentAntiGraph: DirectedGraph[Int] = currentGraph.complementGraph
        // greedy options
        val useableTask: Option[Int] = currentGraph.vertices filterNot currentlyMappedNodes find { n =>
          // check in inserting would do something forbidden
          val successorEdges = nextGraph.edges(planStepToMap) filter mappedNodes map { case after => (n, myMapping(after)) }
          val predecessorEdges = nextGraph.reversedEdgesSet(planStepToMap) filter mappedNodes map { case before => (myMapping(before), n) }

          (successorEdges ++ predecessorEdges) forall { case (from, to) => !currentAntiGraph.edgesSet(from).contains(to) }
        }

        val selectedTask = if (useableTask.nonEmpty) {
          // there are still options, use the one first one
          useableTask.get
        } else {
          // there is no viable task, so create a new one
          currentGraph.vertices.length
        }

        // construct new datastructures
        val newMapping = myMapping.+((planStepToMap, selectedTask))
        val successorEdges = nextGraph.edges(planStepToMap) filter mappedNodes map { case after => (selectedTask, myMapping(after)) }
        val predecessorEdges = nextGraph.reversedEdgesSet(planStepToMap) filter mappedNodes map { case before => (myMapping(before), selectedTask) }

        val newGraph = if (currentGraph.vertices.contains(selectedTask) &&
          successorEdges.forall({ case (f, t) => currentGraph.edgesSet(f).contains(t) }) &&
          predecessorEdges.forall({ case (f, t) => currentGraph.edgesSet(f).contains(t) })) {
          // no actually new edges or vertices
          currentGraph
        } else {
          val newVertices = (currentGraph.vertices :+ selectedTask).distinct
          val newEdges = currentGraph.edgeList ++ (successorEdges ++ predecessorEdges)
          SimpleDirectedGraph(newVertices, newEdges)
        }


        val newMappingList = currentMapping.take(currentMapping.length - 1) :+ newMapping

        matchGraph(newGraph, newMappingList, nextGraph)
      }
    }

    val graphReordering: Seq[(DirectedGraph[PlanStep], Int)] = graphs.zipWithIndex.sortBy(_._1.vertices.length).reverse
    val backMapping: Map[Int, Int] = graphReordering.zipWithIndex map { case ((_, oldIndex), newIndex) => oldIndex -> newIndex } toMap

    //val time0 = System.currentTimeMillis()

    val (minimalGraph, mapping) = graphReordering.foldLeft[(DirectedGraph[Int], Seq[Map[PlanStep, Int]])]((SimpleDirectedGraph(Nil, Nil), Nil))(
      {
        case ((currentGraph, currentMapping), (nextGraph, _)) =>
          //println(nextGraph.vertices.length)
          matchGraph(currentGraph, currentMapping :+ Map(), nextGraph)
      })

    //val time1 = System.currentTimeMillis()
    //println("Result " + minimalGraph.vertices.length + " " + (time1 - time0))

    (minimalGraph, mapping.indices map { i => mapping(backMapping(i)) })
  }
}


object GreedyNumberOfChildrenFromTotallyOrderedOptimiser extends SOGOptimiser {
  override def minimalSOG(graphs: Seq[DirectedGraph[PlanStep]]): (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) = {
    //assert(graphs forall { _.allTotalOrderings.get.length == 1 })

    // take the longest ones first
    val sorted = graphs.zipWithIndex.sortBy(-_._1.vertices.length)
    val maxLen = if (sorted.nonEmpty) sorted.head._1.vertices.length else 0
    val range = Range(0, maxLen)

    val possibleTasksPerChildPosition = range map { _ => new mutable.HashSet[Task]() } toArray
    val psMapping = graphs.indices map { _ => new mutable.HashMap[PlanStep, Int]() } toArray

    sorted foreach { case (g, gIndex) =>
      range.foldLeft(g.topologicalOrdering.get)(
        {
          case (Nil, _)            => Nil
          case (remainingTasks, i) =>
            if (possibleTasksPerChildPosition(i) contains remainingTasks.head.schema) {psMapping(gIndex)(remainingTasks.head) = i; remainingTasks.tail }
            else if (remainingTasks.length == maxLen - i) {possibleTasksPerChildPosition(i) += remainingTasks.head.schema; psMapping(gIndex)(remainingTasks.head) = i; remainingTasks.tail }
            else remainingTasks
        })
    }

    val supergraph = SimpleDirectedGraph(range, if (range.nonEmpty) range zip range.tail else Nil)

    (supergraph, psMapping map { _.toMap })
  }
}


object NativeOptimiser extends SOGOptimiser {
  override def minimalSOG(graphs: Seq[DirectedGraph[PlanStep]]): (DirectedGraph[Int], Seq[Map[PlanStep, Int]]) = if (graphs.isEmpty) (SimpleDirectedGraph(Nil, Nil), Nil) else {
    //assert(graphs forall { _.allTotalOrderings.get.length == 1 })

    // take the longest ones first
    val sorted = graphs.zipWithIndex.sortBy(-_._1.vertices.length)
    val maxLen = if (sorted.nonEmpty) sorted.head._1.vertices.length else 0
    val range = Range(0, maxLen)

    val possibleTasksPerChildPosition = range map { _ => new mutable.HashSet[Task]() } toArray
    val psMapping = graphs.indices map { _ => new mutable.HashMap[PlanStep, Int]() } toArray

    sorted foreach { case (g, gIndex) =>
      range.foldLeft(g.topologicalOrdering.get)(
        {
          case (Nil, _)            => Nil
          case (remainingTasks, i) =>
            possibleTasksPerChildPosition(i) += remainingTasks.head.schema
            psMapping(gIndex)(remainingTasks.head) = i
            remainingTasks.tail
        })
    }


    val supergraph = SimpleDirectedGraph(range, range zip range.tail)

    (supergraph, psMapping map { _.toMap })
  }
}

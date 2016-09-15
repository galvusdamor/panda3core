package de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability

import de.uniulm.ki.panda3.efficient.Wrapping
import de.uniulm.ki.panda3.efficient.domain.{EfficientGroundTask, EfficientGroundedDecompositionMethod}
import de.uniulm.ki.panda3.symbolic.domain.GroundedDecompositionMethod
import de.uniulm.ki.panda3.symbolic.domain.datastructures.hierarchicalreachability.TaskDecompositionGraph
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.util.{AndOrGraph, SimpleAndOrGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class EfficientTDGFromGroundedSymbolic(symbolicTDG: TaskDecompositionGraph, wrapping: Wrapping) extends EfficientGroundedTaskDecompositionGraph {

  val graph: AndOrGraph[AnyRef, EfficientGroundTask, EfficientGroundedDecompositionMethod] = {
    val symbolicGraph = symbolicTDG.taskDecompositionGraph._1

    val efficientTasks = symbolicGraph.andVertices map { case gt@GroundTask(task, args) => gt -> EfficientGroundTask(wrapping.unwrap(task), (args map wrapping.unwrap).toArray) } toMap
    val efficientMethods = symbolicGraph.orVertices map { case gdm@GroundedDecompositionMethod(method, args) =>
      val efficientMethod = wrapping.unwrap(method)
      val subPlan = wrapping.efficientDomain.decompositionMethods(efficientMethod).subPlan
      val constants = Range(0, subPlan.variableConstraints.numberOfVariables) map { wrapping.wrapVariable(_, method) } map args map wrapping.unwrap
      gdm -> EfficientGroundedDecompositionMethod(efficientMethod, constants.toArray)
    } toMap

    val andEdges = symbolicGraph.andEdges map { case (a, setO) => efficientTasks(a) -> (setO map efficientMethods) }
    val orEdges = symbolicGraph.orEdges map { case (o, setA) => efficientMethods(o) -> (setA map efficientTasks) }

    SimpleAndOrGraph(efficientTasks.values.toSet, efficientMethods.values.toSet, andEdges, orEdges)
  }
}

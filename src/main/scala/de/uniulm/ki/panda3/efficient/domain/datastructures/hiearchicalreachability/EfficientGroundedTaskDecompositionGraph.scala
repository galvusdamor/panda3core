package de.uniulm.ki.panda3.efficient.domain.datastructures.hiearchicalreachability

import de.uniulm.ki.panda3.efficient.domain.{EfficientGroundedDecompositionMethod, EfficientGroundTask}
import de.uniulm.ki.util.AndOrGraph

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientGroundedTaskDecompositionGraph {


  val graph : AndOrGraph[AnyRef, EfficientGroundTask, EfficientGroundedDecompositionMethod]
}

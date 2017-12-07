package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic.{And, Predicate}
import de.uniulm.ki.util.DirectedGraphWithAlgorithms

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait LTLAutomaton[NodeType, EdgeToType] extends DirectedGraphWithAlgorithms[NodeType] {
  def transitions: Map[NodeType, Map[(Task, Boolean, Set[Predicate]), EdgeToType]]

  /** a list of all node of the graph */
  val vertices: Seq[NodeType] = transitions.keys.toSeq

  def transitionTest(edgeTo : EdgeToType, to : NodeType) : Boolean

  override def dotEdgeStyleRenderer(from: NodeType, to: NodeType): String = {
    val trans: Seq[(Task, Boolean, Set[Predicate])] = (transitions(from) filter { x => transitionTest(x._2,to) }).keys.toSeq

    if (trans.size == 1) trans.head._1.name + " " + (if (trans.head._2) "last" else "non-last")
    else if (trans forall { _._2 }) trans.size + " last"
    else if (trans forall { !_._2 }) trans.size + " non-last"
    else "" + trans.size
  }
}

object TaskAfterLastOne extends ReducedTask("--after-last", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil))
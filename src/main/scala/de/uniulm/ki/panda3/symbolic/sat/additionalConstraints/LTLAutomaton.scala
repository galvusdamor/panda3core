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

package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.{ConstantActionCost, ReducedTask, Task}
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

object TaskAfterLastOne extends ReducedTask("--after-last", isPrimitive = true, Nil, Nil, Nil, And(Nil), And(Nil), ConstantActionCost(0))

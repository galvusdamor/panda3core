package de.uniulm.ki.panda3.progression.sasp.mergeAndShrink

import java.util

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem
import de.uniulm.ki.panda3.symbolic.DefaultLongInfo

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait NodeValue extends DefaultLongInfo {

  def sasPlusProblem: SasPlusProblem

  def isContained(state: util.BitSet): Boolean
}

case class ElementaryNode(value: Int, sasPlusProblem: SasPlusProblem) extends NodeValue {
  override def isContained(state: util.BitSet): Boolean = state get value

  override def longInfo: String = value + ": " + sasPlusProblem.factStrs(value)
}

case class MergeNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends NodeValue {
  override def isContained(state: util.BitSet): Boolean = left.isContained(state) && right.isContained(state)

  override def longInfo: String = "(" + left.longInfo + " or " + right.longInfo + ")"
}

case class ShrinkNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends NodeValue {
  override def isContained(state: util.BitSet): Boolean = left.isContained(state) || right.isContained(state)

  override def longInfo: String = "(" + left.longInfo + " and " + right.longInfo + ")"
}

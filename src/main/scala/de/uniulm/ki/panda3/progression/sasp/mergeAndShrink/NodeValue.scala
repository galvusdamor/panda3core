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

  def containsShrink() : Boolean

  var containsFactIndexes: Set[Int]

}

case class ElementaryNode(value: Int, sasPlusProblem: SasPlusProblem) extends NodeValue {
  override def isContained(state: util.BitSet): Boolean = state get value

  override def longInfo: String = value + ": " + sasPlusProblem.factStrs(value)

  override def containsShrink() : Boolean = false

  override var containsFactIndexes = Set(value)

}

case class MergeNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends NodeValue {
  override def isContained(state: util.BitSet): Boolean = left.isContained(state) && right.isContained(state)

  override def containsShrink() : Boolean = left.containsShrink() || right.containsShrink();

  override var containsFactIndexes: Set[Int] = left.containsFactIndexes | right.containsFactIndexes;

  //override def longInfo: String = "(" + left.longInfo + ", \n" + right.longInfo + ")"
  override def longInfo: String = {
/*    left match {
      case ElementaryNode(value: Int, sasPlusProblem: SasPlusProblem) =>
        right match {
          case ElementaryNode(value: Int, sasPlusProblem: SasPlusProblem) =>
            "(" +left.longInfo + ", \n" + right.longInfo + ")"
          case _ =>
            "(" +left.longInfo + "\n and \n" + right.longInfo + ")"
        }
      case _ =>
        "(" +left.longInfo + "\n and \n" + right.longInfo + ")"
    }*/

    containsShrink() match {

      case true =>
        "(" +left.longInfo + ")\n and \n(" + right.longInfo + ")"
      case _ =>
        left.longInfo + ", \n" + right.longInfo
    }

  }
}

case class ShrinkNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends NodeValue {
  override def isContained(state: util.BitSet): Boolean = left.isContained(state) || right.isContained(state)

  override def longInfo: String = "(" + left.longInfo + ")\n or \n(" + right.longInfo + ")"

  override def containsShrink() : Boolean = true;

  override var containsFactIndexes: Set[Int] = left.containsFactIndexes | right.containsFactIndexes;
}

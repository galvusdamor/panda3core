package de.uniulm.ki.panda3.progression.sasp.mergeAndShrink

import java.util

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem
import de.uniulm.ki.panda3.symbolic.DefaultLongInfo

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait NodeValue extends DefaultLongInfo {

  def sasPlusProblem: SasPlusProblem


  def containsShrink() : Boolean



  val isGoalNode: java.lang.Boolean

  def size : Long


}

trait ClassicalNodeValue extends NodeValue{

  def isContained(state: util.BitSet): Boolean

  def containsFactIndexes: Set[Int]



}

case class ElementaryNode(value: Int, sasPlusProblem: SasPlusProblem, goalNode: java.lang.Boolean) extends ClassicalNodeValue {

  override lazy val isGoalNode: java.lang.Boolean = goalNode

  override def isContained(state: util.BitSet): Boolean = state get value



  override lazy val longInfo: String = value.toString + ": " + sasPlusProblem.factStrs(value)

  override def containsShrink() : Boolean = false

  override lazy val containsFactIndexes = Set(value)

  override lazy val size : Long = 1

}

case class MergeNode(left: ClassicalNodeValue, right: ClassicalNodeValue, sasPlusProblem: SasPlusProblem) extends ClassicalNodeValue {

  override lazy val isGoalNode : java.lang.Boolean = left.isGoalNode && right.isGoalNode

  override def isContained(state: util.BitSet): Boolean = left.isContained(state) && right.isContained(state)

  override def containsShrink() : Boolean = left.containsShrink() || right.containsShrink()

  override lazy val containsFactIndexes: Set[Int] = left.containsFactIndexes | right.containsFactIndexes

  override def longInfo: String = "(" + left.longInfo + ", \n" + right.longInfo + ")"
  //override lazy val longInfo: String = size.toString
  //override lazy val longInfo: String = size.toString /*{
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



  override lazy val size : Long = left.size + right.size
}

case class ShrinkNode(left: ClassicalNodeValue, right: ClassicalNodeValue, sasPlusProblem: SasPlusProblem) extends ClassicalNodeValue {

  override lazy val isGoalNode : java.lang.Boolean = left.isGoalNode || right.isGoalNode
  override def isContained(state: util.BitSet): Boolean = left.isContained(state) || right.isContained(state)

  //override lazy val longInfo: String = size.toString //"(" + left.longInfo + ")\n or \n(" + right.longInfo + ")"
  override lazy val longInfo: String = "(" + left.longInfo + ")\n or \n(" + right.longInfo + ")"

  override def containsShrink() : Boolean = true

  override lazy val containsFactIndexes: Set[Int] = left.containsFactIndexes | right.containsFactIndexes

  override lazy val size : Long = left.size + right.size
}

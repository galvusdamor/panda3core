package de.uniulm.ki.panda3.progression.sasp.mergeAndShrink

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem

abstract class HtnNodeValue extends NodeValue {

}


case class HtnElementaryNode(sasPlusProblem: SasPlusProblem, goalNode: java.lang.Boolean) extends HtnNodeValue {

  override lazy val isGoalNode: java.lang.Boolean = goalNode

  override def containsShrink() : Boolean = false

  override lazy val longInfo: String = "Node" //+ ": " + sasPlusProblem.factStrs(value)

  override lazy val size : Long = 1

}



case class HtnMergeNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends HtnNodeValue {

  override lazy val size : Long = left.size + right.size

  override lazy val isGoalNode : java.lang.Boolean = left.isGoalNode && right.isGoalNode

  override def containsShrink() : Boolean = left.containsShrink() || right.containsShrink()

  override lazy val longInfo: String = size.toString

}


case class HtnShrinkNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends HtnNodeValue {


  override lazy val size : Long = left.size + right.size

  override lazy val isGoalNode : java.lang.Boolean = left.isGoalNode || right.isGoalNode

  override lazy val longInfo: String = size.toString //"(" + left.longInfo + ")\n or \n(" + right.longInfo + ")"

  override def containsShrink() : Boolean = true


}



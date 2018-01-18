package de.uniulm.ki.panda3.progression.sasp.mergeAndShrink

import de.uniulm.ki.panda3.progression.htn.representation.SasPlusProblem

class HtnNodeValue {

}


case class HtnElementaryNode(value: Int, sasPlusProblem: SasPlusProblem, goalNode: java.lang.Boolean) extends HtnNodeValue {

}



case class HtnMergeNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends HtnNodeValue {

}


case class HtnShrinkNode(left: NodeValue, right: NodeValue, sasPlusProblem: SasPlusProblem) extends HtnNodeValue {

}



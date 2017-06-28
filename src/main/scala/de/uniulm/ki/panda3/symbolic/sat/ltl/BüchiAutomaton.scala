package de.uniulm.ki.panda3.symbolic.sat.ltl

import de.uniulm.ki.panda3.symbolic.domain.Task

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BüchiAutomaton(numberOfStates: Int, initialState : Int, finalStates : Set[Int], transitions: Seq[(Int, Task, Int)]) {

}

object BüchiAutomaton {
  def apply(formula: String): BüchiAutomaton = {



    BüchiAutomaton(0, 0, Set(), Nil)
  }
}

package de.uniulm.ki.panda3.symbolic.sat.ltl

import de.uniulm.ki.panda3.symbolic.domain.Task
import rwth.i2.ltl2ba4j.LTL2BA4J
import rwth.i2.ltl2ba4j.model.ITransition

import scala.collection.JavaConversions

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BüchiAutomaton(numberOfStates: Int, initialState : Int, finalStates : Set[Int], transitions: Seq[(Int, Task, Int)]) {

}

object BüchiAutomaton {
  def apply(formula: String): BüchiAutomaton = {
    // use LTL2BA4j
    //val formulaFactory = new rwth.i2.ltl2ba4j.formula.impl.FormulaFactory()
    //val f = ff.G(ff.Proposition("test"))

    // TODO as of now, we are just outputting shit
    val transitions: Seq[ITransition] = JavaConversions.collectionAsScalaIterable(LTL2BA4J.formulaToBA(formula)).toSeq

    transitions foreach { t => println(t) }



    BüchiAutomaton(0, 0, Set(), Nil)
  }
}

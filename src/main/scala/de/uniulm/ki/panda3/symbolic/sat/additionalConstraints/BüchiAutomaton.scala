package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.{Domain, ReducedTask, Task}
import de.uniulm.ki.panda3.symbolic.logic.{And, Predicate}
import de.uniulm.ki.util.{DirectedGraph, DirectedGraphWithAlgorithms, Dot2PdfCompiler, SimpleDirectedGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BüchiAutomaton(initialState: LTLFormula, transitions: Map[LTLFormula, Map[(Task, Boolean, Set[Predicate]), LTLFormula]]) extends DirectedGraphWithAlgorithms[LTLFormula] {
  /** a list of all node of the graph */
  val vertices: Seq[LTLFormula] = transitions.keys.toSeq

  /** adjacency list of the graph */
  val edges: Map[LTLFormula, Seq[LTLFormula]] = transitions map { case (a, b) => a -> b.values.toSeq.distinct }

  override def dotEdgeStyleRenderer(from: LTLFormula, to: LTLFormula): String = {
    val trans = (transitions(from) filter { _._2 == to }).keys.toSeq

    if (trans.size == 1) trans.head._1.name + " " + trans.head._2
    else if (trans forall { _._2 }) trans.size + " true"
    else if (trans forall { !_._2 }) trans.size + " false"
    else "" + trans.size
  }
}

object TaskAfterLastOne extends ReducedTask("--after-last",isPrimitive = true,Nil,Nil,Nil,And(Nil),And(Nil))

object BüchiAutomaton {
  def apply(domain: Domain, formula: LTLFormula): BüchiAutomaton = {

    // construct the automaton
    def dfs(states: Set[LTLFormula], newStates: Set[LTLFormula], transitions: Map[LTLFormula, Map[(Task, Boolean, Set[Predicate]), LTLFormula]]):
    (Set[LTLFormula], Map[LTLFormula, Map[(Task, Boolean, Set[Predicate]), LTLFormula]]) = {
      // get the transitions for every new state
      val newTransitions: Map[LTLFormula, Map[(Task, Boolean, Set[Predicate]), LTLFormula]] = newStates map { state =>
        val allPrimitiveStates = state.allStates
        // iterate over all tasks that might be executed
        val transition: Map[(Task, Boolean, Set[Predicate]), LTLFormula] = (domain.primitiveTasks :+ TaskAfterLastOne) flatMap { nextTask =>
          allPrimitiveStates flatMap { primState =>
            true :: false :: Nil map { last => ((nextTask, last, primState), state.delta(nextTask, primState, last).simplify) }
          }
        } toMap

        state -> transition
      } toMap

      val targetStates = (newTransitions flatMap { _._2.values } filterNot states.contains).toSet

      if (targetStates.isEmpty) (states, transitions ++ newTransitions)
      else dfs(states ++ targetStates, targetStates.toSet, transitions ++ newTransitions)
    }

    val (states, transitions) = dfs(Set(formula), Set(formula), Map())

    val automaton = BüchiAutomaton(formula, transitions)

    // print to PDF
    Dot2PdfCompiler.writeDotToFile(automaton, "dea.pdf")

    automaton
  }
}

package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan, LinearPrimitivePlanEncoding}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class BüchiFormulaEncoding(automaton: BüchiAutomaton, id: String) extends LTLFormulaEncoding[LTLFormula, LTLFormula] {

  def apply(linearEncoding: LinearPrimitivePlanEncoding): Seq[Clause] = {
    val automatonClauses: Seq[Clause] = linearEncoding.linearPlan.zipWithIndex flatMap { case (taskMap, position) =>
      // at most one of the states of the automaton is true
      val atMostOneState: Seq[Clause] = linearEncoding.atMostOneOf(automaton.vertices map { s => state(s, position) })

      // if a task gets selected, execute the rule
      val transitionRule: Seq[Clause] = automaton.vertices flatMap { s =>
        val relevantStateFeatures = s.allPredicates
        //println("S " + allStates.length)

        s.allStatesAndCounter flatMap { case (primitiveState, negativeState) =>
          val stateTrue = primitiveState map { p => linearEncoding.linearStateFeatures(position)(p) } toSeq
          val stateFalse = negativeState map { p => linearEncoding.linearStateFeatures(position)(p) } toSeq

          taskMap map { case (task, atom) =>


            // TODO
            val nextStateNotLast = automaton.transitions(s)((task, false, primitiveState))
            val notLast = linearEncoding.impliesLeftTrueAndFalseImpliesTrue((state(s, position) :: atom :: Nil) ++ stateTrue,
                                                                            stateFalse,
                                                                            state(nextStateNotLast, position + 1))


            notLast
          }
        }
      }

      atMostOneState ++ transitionRule
    }

    val lastAutomationTransition: Seq[Clause] = {
      val position = linearEncoding.linearPlan.length
      val atMostOneState: Seq[Clause] = linearEncoding.atMostOneOf(automaton.vertices map { s => state(s, position) })

      val transitionRule: Seq[Clause] = automaton.vertices flatMap { s =>
        val relevantStateFeatures = s.allPredicates
        val allStates = s.allStates

        allStates flatMap { primitiveState =>
          val (stateTrue, stateFalse) = relevantStateFeatures map { l => (linearEncoding.linearStateFeatures(position)(l), primitiveState contains l) } partition { _._2 }


          // last Action, last state is handled differently ...
          val nextStateLast = automaton.transitions(s)((TaskAfterLastOne, true, primitiveState))
          val last = linearEncoding.impliesLeftTrueAndFalseImpliesTrue((state(s, position) :: Nil) ++ stateTrue.toSeq.map(_._1),
                                                                       stateFalse.toSeq map { _._1 },
                                                                       state(nextStateLast, position + 1))

          //notLast :: last :: Nil
          last :: Nil
        }
      }

      atMostOneState ++ transitionRule
    }

    val initAndGoal = Clause(state(automaton.initialState, 0)) :: Clause(state(LTLTrue, linearEncoding.linearPlan.length + 1)) :: Nil
    val lastStateUnique = linearEncoding.atMostOneOf(automaton.vertices map { s => state(s, linearEncoding.linearPlan.length + 1) })

    automatonClauses ++ initAndGoal ++ lastAutomationTransition ++ lastStateUnique ++ maintainStateAtNoPresent(linearEncoding)
  }
}
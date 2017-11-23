package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLFormulaEncoding(büchiAutomaton: BüchiAutomaton, id : String) extends AdditionalSATConstraint {

  val automataStatesToIndices: Map[LTLFormula, Int] = büchiAutomaton.vertices.zipWithIndex.toMap

  //println(automataStatesToIndices map {case (f,i) => i + " " + f.longInfo} mkString "\n")

  private def state(formula: LTLFormula, position: Int) = id + "_auto_state_" + automataStatesToIndices(formula) + "_" + position

  private def noPresent(position: Int) = id + "_auto_Present" + position


  def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = {
    val automatonClauses: Seq[Clause] = linearEncoding.linearPlan.zipWithIndex flatMap { case (taskMap, position) =>
      // at most one of the states of the automaton is true
      val atMostOneState: Seq[Clause] = linearEncoding.atMostOneOf(büchiAutomaton.vertices map { s => state(s, position) })

      // if a task gets selected, execute the rule
      val transitionRule: Seq[Clause] = büchiAutomaton.vertices flatMap { s =>
        val relevantStateFeatures = s.allPredicates
        //println("S " + allStates.length)

        s.allStatesAndCounter flatMap { case (primitiveState, negativeState) =>
          val stateTrue = primitiveState map { p => linearEncoding.linearStateFeatures(position)(p) } toSeq
          val stateFalse = negativeState map { p => linearEncoding.linearStateFeatures(position)(p) } toSeq

          taskMap flatMap { case (task, atom) =>


            // TODO
            val nextStateNotLast = büchiAutomaton.transitions(s)((task, false, primitiveState))
            val notLast = linearEncoding.impliesLeftTrueAndFalseImpliesTrue((state(s, position) :: /*anyAfter(position) :: */atom :: Nil) ++ stateTrue,
                                                                            stateFalse,
                                                                            state(nextStateNotLast, position + 1))


            // last Action, last state is handled differently ...
            //val nextStateLast = büchiAutomaton.transitions(s)((task, false, primitiveState))
            //val last = linearEncoding.impliesLeftTrueAndFalseImpliesTrue((state(s, position) :: noAfter(position) :: atom :: Nil) ++ stateTrue.map(_._1),
            //                                                             stateFalse map { _._1 },
            //                                                             state(nextStateLast, position + 1))

            //notLast :: last :: Nil
            notLast :: Nil
          }
        }
      }

      val setNoPresent: Seq[Clause] = (linearEncoding.notImplies(taskMap.values.toSeq, noPresent(position)) :: Nil) ++
        (taskMap.values map { a => linearEncoding.impliesNot(noPresent(position), a) })

      val noTaskRule: Seq[Clause] = büchiAutomaton.vertices map { s => linearEncoding.impliesRightAndSingle(state(s, position) :: noPresent(position) :: Nil, state(s, position + 1)) }


      atMostOneState ++ transitionRule ++ setNoPresent ++ noTaskRule
    }

    val lastAutomationTransition: Seq[Clause] = {
      val position = linearEncoding.linearPlan.length
      val atMostOneState: Seq[Clause] = linearEncoding.atMostOneOf(büchiAutomaton.vertices map { s => state(s, position) })

      val transitionRule: Seq[Clause] = büchiAutomaton.vertices flatMap { s =>
        val relevantStateFeatures = s.allPredicates
        val allStates = s.allStates

        allStates flatMap { primitiveState =>
          val (stateTrue, stateFalse) = relevantStateFeatures map { l => (linearEncoding.linearStateFeatures(position)(l), primitiveState contains l) } partition { _._2 }


          // last Action, last state is handled differently ...
          val nextStateLast = büchiAutomaton.transitions(s)((TaskAfterLastOne, true, primitiveState))
          val last = linearEncoding.impliesLeftTrueAndFalseImpliesTrue((state(s, position) :: Nil) ++ stateTrue.toSeq.map(_._1),
                                                                       stateFalse.toSeq map { _._1 },
                                                                       state(nextStateLast, position + 1))

          //notLast :: last :: Nil
          last :: Nil
        }
      }

      atMostOneState ++ transitionRule
    }

    val initAndGoal = Clause(state(büchiAutomaton.initialState, 0)) :: Clause(state(LTLTrue, linearEncoding.linearPlan.length + 1)) :: Nil
    val lastStateUnique = linearEncoding.atMostOneOf(büchiAutomaton.vertices map { s => state(s, linearEncoding.linearPlan.length + 1) })

    automatonClauses ++ initAndGoal ++ lastAutomationTransition ++ lastStateUnique
  }
}
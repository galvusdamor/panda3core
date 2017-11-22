package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan, PathBasedEncoding}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLFormulaEncoding(büchiAutomaton: BüchiAutomaton) extends AdditionalSATConstraint {

  val automataStatesToIndices: Map[LTLFormula, Int] = büchiAutomaton.vertices.zipWithIndex.toMap

  private def state(formula: LTLFormula, position: Int) = "auto_state_" + automataStatesToIndices(formula) + "_" + position

  private def noPresent(position: Int) = "auto_Present" + position

  private def noAfter(position: Int) = "auto_noAfter" + position

  private def anyAfter(position: Int) = "auto_anyAfter" + position


  def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = {
    println("START")
    val sss = System.currentTimeMillis()
    val automatonClauses: Seq[Clause] = linearEncoding.linearPlan.zipWithIndex flatMap { case (taskMap, position) =>
      // at most one of the states of the automaton is true
      val atMostOneState: Seq[Clause] = linearEncoding.atMostOneOf(büchiAutomaton.vertices map { s => state(s, position) })

      // if there is nothing after this index
      val afterClauses: Seq[Clause] = if (position + 1 == linearEncoding.linearPlan.length) {
        Clause(noAfter(position)) :: Nil
      } else {
        Seq(linearEncoding.impliesSingle(noAfter(position), noAfter(position + 1))) ++ (
          linearEncoding.linearPlan(position + 1).values map { a => linearEncoding.impliesNot(noAfter(position), a) })
      }

      // noAfter and anyAfter are inverse of each other
      val turnAround: Seq[Clause] = linearEncoding.impliesNot(noAfter(position), anyAfter(position)) :: linearEncoding.impliesNot(anyAfter(position), noAfter(position)) ::
        Clause(Array(noAfter(position), anyAfter(position))) :: Nil

      // if a task gets selected, execute the rule
      val ss = System.currentTimeMillis()
      println("T - Rule " + büchiAutomaton.vertices.length)
      val transitionRule: Seq[Clause] = büchiAutomaton.vertices flatMap { s =>
        val relevantStateFeatures = s.allPredicates
        //println("S " + allStates.length)

        s.allStatesAndCounter flatMap { case (primitiveState, negativeState) =>
          val stateTrue = primitiveState map { p => linearEncoding.linearStateFeatures(position)(p) } toSeq
          val stateFalse = negativeState map { p => linearEncoding.linearStateFeatures(position)(p) } toSeq

          taskMap flatMap { case (task, atom) =>


            // TODO
            val nextStateNotLast = büchiAutomaton.transitions(s)((task, false, primitiveState))
            val notLast = linearEncoding.impliesLeftTrueAndFalseImpliesTrue((state(s, position) :: anyAfter(position) :: atom :: Nil) ++ stateTrue,
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
      println("T - Rule - END " + (System.currentTimeMillis() - ss))

      val setNoPresent: Seq[Clause] = (linearEncoding.notImplies(taskMap.values.toSeq, noPresent(position)) :: Nil) ++
        (taskMap.values map { a => linearEncoding.impliesNot(noPresent(position), a) })

      val noTaskRule: Seq[Clause] = büchiAutomaton.vertices map { s => linearEncoding.impliesRightAndSingle(state(s, position) :: noPresent(position) :: Nil, state(s, position + 1)) }


      atMostOneState ++ afterClauses ++ turnAround ++ transitionRule ++ setNoPresent ++ noTaskRule
    }

    val lastAutomationTranstion: Seq[Clause] = {
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

      atMostOneState
    }

    val initAndGoal = Clause(state(büchiAutomaton.initialState, 0)) :: Clause(state(LTLTrue, linearEncoding.linearPlan.length + 1)) :: Nil
    val lastStateUnique = linearEncoding.atMostOneOf(büchiAutomaton.vertices map { s => state(s, linearEncoding.linearPlan.length + 1) })

    println("END " + (System.currentTimeMillis() - sss))
    automatonClauses ++ initAndGoal ++ lastAutomationTranstion ++ lastStateUnique
  }
}
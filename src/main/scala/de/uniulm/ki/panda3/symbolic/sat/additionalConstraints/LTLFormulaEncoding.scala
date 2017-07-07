package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.sat.verify.{EncodingWithLinearPlan, Clause, PathBasedEncoding}

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
      val transitionRule: Seq[Clause] = büchiAutomaton.vertices flatMap { s => taskMap flatMap { case (task, atom) =>
        val nextStateNotLast = büchiAutomaton.transitions(s)((task, false))
        val notLast = linearEncoding.impliesRightAndSingle(state(s, position) :: anyAfter(position) :: atom :: Nil, state(nextStateNotLast, position + 1))

        val nextStateLast = büchiAutomaton.transitions(s)((task, true))
        val last = linearEncoding.impliesRightAndSingle(state(s, position) :: noAfter(position) :: atom :: Nil, state(nextStateLast, position + 1))

        notLast :: last :: Nil
      }
      }

      val setNoPresent: Seq[Clause] = (linearEncoding.notImplies(taskMap.values.toSeq, noPresent(position)) :: Nil) ++
        (taskMap.values map { a => linearEncoding.impliesNot(noPresent(position), a) })

      val noTaskRule: Seq[Clause] = büchiAutomaton.vertices map { s => linearEncoding.impliesRightAndSingle(state(s, position) :: noPresent(position) :: Nil, state(s, position + 1)) }


      atMostOneState ++ afterClauses ++ turnAround ++ transitionRule ++ setNoPresent ++ noTaskRule
    }

    val initAndGoal = Clause(state(büchiAutomaton.initialState, 0)) :: Clause(state(LTLTrue, linearEncoding.linearPlan.length)) :: Nil
    val lastStateUnique = linearEncoding.atMostOneOf(büchiAutomaton.vertices map { s => state(s, linearEncoding.linearPlan.length) })

    automatonClauses ++ initAndGoal ++ lastStateUnique
  }
}
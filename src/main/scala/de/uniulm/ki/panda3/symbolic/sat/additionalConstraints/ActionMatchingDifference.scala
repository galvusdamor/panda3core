package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class ActionMatchingDifference(referencePlan: Seq[Task], maximumDifference: Int) extends MatchingBasedConstraints {


  def matchPathAndReference(pathPosition: Int, referencePosition: Int): String = "actionDifferenceMatch_" + pathPosition + "_" + referencePosition

  def matchingSizeAt(pathPosition: Int, size: Int): String = "actionDifferenceMatchingSize_" + pathPosition + "_" + size

  override val ignoreOrder: Boolean = true

  override def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = {

    // generate clauses representing the matching
    val matchingClauses: Seq[Clause] = generateMatchingClauses(linearEncoding, matchPathAndReference)

    val maximumUnmatched = linearEncoding.linearPlan.length + referencePlan.length

    // automaton for upper limit of unmatched tasks (PDT paths)
    val transitionsPathTasks: Seq[Clause] = linearEncoding.linearPlan.indices flatMap { pathPosition => Range(0, maximumUnmatched + 1) flatMap { unmatched =>
      val oldState = matchingSizeAt(pathPosition - 1, unmatched)
      val newStateSame = matchingSizeAt(pathPosition, unmatched)
      val newStateIncrease = matchingSizeAt(pathPosition, unmatched + 1)

      val matchings = referencePlan.indices map { referencePosition => matchPathAndReference(pathPosition, referencePosition) }

      val pathAtoms = linearEncoding.linearPlan(pathPosition).values.toSeq

      val noMatchingButSelected = pathAtoms map { atom => linearEncoding.impliesLeftTrueAndFalseImpliesTrue(oldState :: atom :: Nil, matchings, newStateIncrease) }

      val noMatchingNothingSelected = linearEncoding.impliesLeftTrueAndFalseImpliesTrue(pathAtoms :+ oldState, matchings, newStateIncrease)


      val noMatching = matchings map { m => linearEncoding.impliesLeftTrueAndFalseImpliesTrue(oldState :: Nil, Nil, newStateSame) }

      noMatchingButSelected ++ noMatching :+ noMatchingNothingSelected
    }
    }

    // automaton for upper limit of unmatched tasks (reference solution)
    val transitionsReferenceTasks = referencePlan.indices flatMap { referencePosition => Range(0, maximumUnmatched + 1) flatMap { unmatched =>
      val oldState = matchingSizeAt(linearEncoding.linearPlan.size + referencePosition - 1, unmatched)
      val newStateSame = matchingSizeAt(linearEncoding.linearPlan.size + referencePosition, unmatched)
      val newStateIncrease = matchingSizeAt(linearEncoding.linearPlan.size + referencePosition, unmatched + 1)

      val matchings = linearEncoding.linearPlan.indices map { pathPosition => matchPathAndReference(pathPosition, referencePosition) }

      val noMatching = linearEncoding.impliesLeftTrueAndFalseImpliesTrue(oldState :: Nil, matchings, newStateIncrease)
      val matching = matchings map { m => linearEncoding.impliesLeftTrueAndFalseImpliesTrue(oldState :: m :: Nil, Nil, newStateSame) }

      matching :+ noMatching
    }
    }

    val startClause = Clause(matchingSizeAt(-1, 0))

    // go get the minimum, at least one of them must be true
    val endClause = Range(maximumDifference + 1, maximumUnmatched + 1) map { bad => Clause((matchingSizeAt(maximumUnmatched - 1, bad), false)) }

    matchingClauses ++ transitionsPathTasks ++ transitionsReferenceTasks ++ endClause :+ startClause
  }
}

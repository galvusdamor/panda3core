package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class ActionDifference(referencePlan: Seq[Task]) extends AdditionalSATConstraint {


  def badnessAtPosition(badness: Int, position: Int): String = "badness_" + position + "_is_" + badness

  def taskOfReferenceContained(referencePosition: Int): String = "badness_referenceContained_" + referencePosition


  override def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = {

    // at the most we can have this many differences between the two plans
    val maxBadness = linearEncoding.linearPlan.length + referencePlan.length

    //// NEW SOLUTION
    val clausesForNewSolution: Seq[Clause] = Range(0, maxBadness) flatMap { baseBadness => linearEncoding.linearPlan.zipWithIndex flatMap { case (possibleTasks, position) =>
      // if there actually is a task
      val baseBadnessAtom = badnessAtPosition(baseBadness, position - 1)

      val clausesForTaskPresent: Seq[Clause] = possibleTasks map { case (t, atom) =>
        val newBadness = if (referencePlan.contains(t)) baseBadness else baseBadness + 1
        linearEncoding.impliesRightAndSingle(baseBadnessAtom :: atom :: Nil, badnessAtPosition(newBadness, position))
      } toSeq

      val clauseForNonPresentTask: Clause =
        linearEncoding.impliesLeftTrueAndFalseImpliesTrue(leftFalse = possibleTasks.values.toSeq, leftTrue = baseBadnessAtom :: Nil, right = badnessAtPosition(baseBadness, position))

      clausesForTaskPresent :+ clauseForNonPresentTask
    }
    }


    //// REFERENCE
    val referenceTaskContainedClauses = referencePlan.zipWithIndex flatMap { case (referenceTask, referencePosition) =>
      val referenceAtom = taskOfReferenceContained(referencePosition)

      // get all possibleAtoms
      val supportingAtoms = linearEncoding.linearPlan collect { case x if x.contains(referenceTask) => x(referenceTask) }

      val ifContained = linearEncoding.impliesRightOr(referenceAtom :: Nil, supportingAtoms)
      val ifNotContained = linearEncoding.notImpliesAllNot(referenceAtom :: Nil, supportingAtoms)

      ifNotContained :+ ifContained
    }

    val oldPlanOffset = linearEncoding.linearPlan.length
    val clausesForReference: Seq[Clause] = Range(0, maxBadness) flatMap { baseBadness => referencePlan.indices flatMap { case position =>
      // if there actually is a task
      val baseBadnessAtom = badnessAtPosition(baseBadness, position - 1 + oldPlanOffset)
      val nextBadnessAtomSame = badnessAtPosition(baseBadness, position + oldPlanOffset)
      val nextBadnessAtomInc = badnessAtPosition(baseBadness + 1, position + oldPlanOffset)
      val referenceAtom = taskOfReferenceContained(position)


      val taskMatched = linearEncoding.impliesLeftTrueAndFalseImpliesTrue(leftFalse = Nil, leftTrue = baseBadnessAtom :: referenceAtom :: Nil, right = nextBadnessAtomSame)
      val taskNotMatched = linearEncoding.impliesLeftTrueAndFalseImpliesTrue(leftFalse = referenceAtom :: Nil, leftTrue = baseBadnessAtom :: Nil, right = nextBadnessAtomInc)

      taskMatched :: taskNotMatched :: Nil
    }
    }

    val assertInitial = Clause(badnessAtPosition(0, -1))

    val noBadness = Range(3, maxBadness) map { bad => Clause((badnessAtPosition(bad, maxBadness - 1), false)) }

    noBadness ++ clausesForReference ++ referenceTaskContainedClauses ++ clausesForNewSolution :+ assertInitial
  }
}

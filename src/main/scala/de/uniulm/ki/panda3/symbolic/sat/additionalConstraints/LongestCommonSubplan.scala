package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * forces the minimum common subplan of the solution and the reference plan to be at least of length K
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LongestCommonSubplan(referencePlan: Seq[Task]) extends AdditionalSATConstraint {


  def matchPathAndReference(pathPosition: Int, referencePosition: Int): String = "match_" + pathPosition + "_" + referencePosition

  def matchingSizeAt(pathPosition: Int, size: Int): String = "matchingSize_" + pathPosition + "_" + size

  override def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause] = {

    // every position and path can be matched only once
    val onlyOneMatchingPerPath = linearEncoding.linearPlan.indices flatMap { pathPosition =>
      val matchings = referencePlan.indices map { referencePosition => matchPathAndReference(pathPosition, referencePosition) }
      linearEncoding.atMostOneOf(matchings)
    }

    val onlyOneMatchingPerReference = referencePlan.indices flatMap { referencePosition =>
      val matchings = linearEncoding.linearPlan.indices map { pathPosition => matchPathAndReference(pathPosition, referencePosition) }
      linearEncoding.atMostOneOf(matchings)
    }

    // if matched, the task must be in the path
    val ifMatchedTaskMustBeThere: Seq[Clause] = linearEncoding.linearPlan.zipWithIndex flatMap { case (possibleTasks, pathPosition) =>
      referencePlan.zipWithIndex map { case (referenceTask, referencePosition) =>
        if (possibleTasks contains referenceTask) {
          linearEncoding.impliesSingle(matchPathAndReference(pathPosition, referencePosition), possibleTasks(referenceTask))
        } else {
          // if the path cannot contain the position, then forbid this matching
          Clause((matchPathAndReference(pathPosition, referencePosition), false))
        }
      }
    }

    // forbid cross matchings
    val crossMatching = for (path1 <- linearEncoding.linearPlan.indices; path2 <- linearEncoding.linearPlan.indices
                             if path1 > path2;
                             reference1 <- referencePlan.indices; reference2 <- referencePlan.indices
                             if reference1 < reference2) yield
      Clause((matchPathAndReference(path1, reference1), false) ::(matchPathAndReference(path2, reference2), false) :: Nil)


    // compute matching size
    val automataTransition = linearEncoding.linearPlan.indices flatMap { pathPosition => linearEncoding.linearPlan.indices flatMap { subPlanLength =>
      val oldState = matchingSizeAt(pathPosition - 1, subPlanLength)
      val newStateSame = matchingSizeAt(pathPosition, subPlanLength)
      val newStateIncrease = matchingSizeAt(pathPosition, subPlanLength + 1)

      val matchings = referencePlan.indices map { referencePosition => matchPathAndReference(pathPosition, referencePosition) }

      // if it increases then one has to be matched
      val ifIncreasing = linearEncoding.impliesRightOr(oldState :: newStateIncrease :: Nil, matchings)
      // if it stays the same all must be false
      val ifSame = linearEncoding.impliesAllNot(oldState :: newStateSame :: Nil, matchings)
      // if the predecessor is true, at least one transition has to happen
      val hasToBeTransition = linearEncoding.impliesRightOr(oldState :: Nil, newStateIncrease :: newStateSame :: Nil)

      ifSame :+ ifIncreasing :+ hasToBeTransition :+ hasToBeTransition
    }
    }

    val onlyOneState = Range(-1, linearEncoding.linearPlan.length) flatMap { pathPosition =>
      val states = linearEncoding.linearPlan.indices map { subPlanLength => matchingSizeAt(pathPosition, subPlanLength) }
      linearEncoding.atMostOneOf(states)
    }

    val startClause = Clause(matchingSizeAt(-1,0))
    val endClause = Clause(matchingSizeAt(linearEncoding.linearPlan.length-1,15))
    onlyOneMatchingPerPath ++ onlyOneMatchingPerReference ++ ifMatchedTaskMustBeThere ++ crossMatching ++ automataTransition ++ onlyOneState :+ startClause :+ endClause
  }
}
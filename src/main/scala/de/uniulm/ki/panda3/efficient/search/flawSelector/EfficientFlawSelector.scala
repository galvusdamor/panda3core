// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.efficient.search.flawSelector

import java.util

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientAbstractPlanStep, EfficientCausalThreat, EfficientFlaw}
import de.uniulm.ki.panda3.efficient.plan.flaw._

import scala.util.Random

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientFlawSelector {

  def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int
}

trait EfficientFlawSubsetSelector extends EfficientFlawSelector {

  private lazy val sequentialFlawSelectorContainingOnlyMyself = SequentialEfficientFlawSelector(Array(this))

  final def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int =
    sequentialFlawSelectorContainingOnlyMyself.selectFlaw(plan, flaws, numberOfModifications)

  /**
    * removes flaws from consideration. removed flaws should be marked as false and the number of removed flaws should be returned
    */
  def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int
}

case class SequentialEfficientFlawSelector(subSelectors: Array[EfficientFlawSubsetSelector]) extends EfficientFlawSelector {
  def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var currentSelector = 0
    var stillActiveFlaws = flaws.length
    val active = new Array[Boolean](flaws.length)
    util.Arrays.fill(active, true)

    while (stillActiveFlaws > 1 && currentSelector < subSelectors.length) {
      // run next subselector
      stillActiveFlaws -= subSelectors(currentSelector).reduceSelection(plan, active, flaws, numberOfModifications)
      currentSelector += 1
    }

    assert(stillActiveFlaws >= 1)
    // return the first flaw that is still active, this is kind-a random, but if the user would have wanted true randomness he would have asked for it
    var i = 0
    while (i < active.length && !active(i)) i += 1
    assert(active(i))

    i
  }
}


/**
  * Select the flaw with the least number of modifications
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object LeastCostFlawRepair extends EfficientFlawSubsetSelector {

  def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var minMod = Integer.MAX_VALUE
    var flawNum = 0
    while (flawNum < flaws.length) {
      if (activeFlaws(flawNum))
        if (numberOfModifications(flawNum) < minMod)
          minMod = numberOfModifications(flawNum)

      flawNum += 1
    }

    // update active list
    flawNum = 0
    var nonActive = 0
    while (flawNum < flaws.length) {
      if (activeFlaws(flawNum))
        if (numberOfModifications(flawNum) > minMod) {
          activeFlaws(flawNum) = false
          nonActive += 1
        }

      flawNum += 1
    }

    nonActive
  }
}

case class RandomFlawSelector(random: Random) extends EfficientFlawSubsetSelector {
  def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var i = random.nextInt(flaws.length)

    while (!activeFlaws(i))
      i = random.nextInt(flaws.length)

    // update active list
    var flawNum = 0
    var nonActive = 0
    while (flawNum < flaws.length) {
      if (activeFlaws(flawNum))
        if (flawNum != i) {
          activeFlaws(flawNum) = false
          nonActive += 1
        }

      flawNum += 1
    }

    nonActive
  }
}


/**
  * UMCP's flaw selection strategy: Abstract first and LCFR as a tie-breaker
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object UMCPFlawSelection extends EfficientFlawSelector {

  def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var minFlaw = 0
    var isAbstract = flaws(minFlaw).isInstanceOf[EfficientAbstractPlanStep]
    var flawNum = 1
    while (flawNum < flaws.length) {
      val newFlawAbstract = flaws(flawNum).isInstanceOf[EfficientAbstractPlanStep]
      val better = if (isAbstract != newFlawAbstract) !isAbstract else numberOfModifications(flawNum) < numberOfModifications(minFlaw)

      if (better) {
        minFlaw = flawNum
        isAbstract = newFlawAbstract
      }

      flawNum += 1
    }

    minFlaw
  }
}

object OCFlawSelector extends EfficientFlawSubsetSelector {

  override def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var iterator = 0
    val numOfFlaws = flaws.length
    var removedFlaws = 0
    while (iterator < numOfFlaws) {
      if (activeFlaws(iterator)) {
        val flaw = flaws(iterator)
        if (!flaw.isInstanceOf[EfficientOpenPrecondition]) {
          activeFlaws(iterator) = false
          removedFlaws += 1
        }
      }
      iterator += 1
    }
    removedFlaws
  }
}

object AbstractTaskFlawSelector extends EfficientFlawSubsetSelector {

  override def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var iterator = 0
    var nonActive = 0
    val numOfFlaws = flaws.length
    while (iterator < numOfFlaws) {
      if (activeFlaws(iterator)) {
        val flaw = flaws(iterator)
        if (!flaw.isInstanceOf[EfficientAbstractPlanStep]) {
          activeFlaws(iterator) = false
          nonActive += 1
        }
      }
      iterator += 1
    }
    nonActive
  }
}

object OneModFlawSelector extends EfficientFlawSubsetSelector {

  override def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var iterator = 0
    var nonActive = 0
    val numOfFlaws = flaws.length
    while (iterator < numOfFlaws) {
      if(activeFlaws(iterator)) {
        val flaw = flaws(iterator)
        if (flaw.resolver.length != 1) {
          activeFlaws(iterator) = false
          nonActive += 1
        }
      }
      iterator += 1
    }
    nonActive
  }
}

case class AbstractFirstWithDeferred(deferred: Set[Int]) extends EfficientFlawSelector {
  override def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {

    var minFlaw = 0
    var flawNum = 1
    while (flawNum < flaws.length) {
      val currentFlaw = flaws(flawNum)

      // prefer abstract task flaws
      if (currentFlaw.getClass != flaws(minFlaw).getClass) {
        if (currentFlaw.isInstanceOf[EfficientAbstractPlanStep])
          minFlaw = flawNum
      } else {
        var alwaysWorse = false
        var alwaysBetter = false
        if (currentFlaw.isInstanceOf[EfficientAbstractPlanStep]) {
          val thisAsAbstractPS = plan.planStepTasks(currentFlaw.asInstanceOf[EfficientAbstractPlanStep].planStep)
          val bestAsAbstractPS = plan.planStepTasks(flaws(minFlaw).asInstanceOf[EfficientAbstractPlanStep].planStep)

          if (deferred contains thisAsAbstractPS) alwaysWorse = true
          if (deferred contains bestAsAbstractPS) alwaysBetter = true
        }

        if (!alwaysWorse && (alwaysBetter || numberOfModifications(flawNum) < numberOfModifications(minFlaw)))
          minFlaw = flawNum
      }

      flawNum += 1
    }

    minFlaw
  }
}

object CausalThreatSelector extends EfficientFlawSubsetSelector {
  override def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw],
                               numberOfModifications: Array[Int]): Int = {
    if (checkExistsCausalThreat(flaws))
      crossOutNonCausalThreatFlaws(activeFlaws, flaws)
    else
      0
  }

  @inline final private def checkExistsCausalThreat(flaws: Array[EfficientFlaw]): Boolean = {
    var foundCausalThreat = false
    var flawNum = 0
    while (flawNum < flaws.length && !foundCausalThreat) {
      if (flaws(flawNum).isInstanceOf[EfficientCausalThreat])
        foundCausalThreat = true
      flawNum += 1
    }
    foundCausalThreat
  }

  @inline final private def crossOutNonCausalThreatFlaws(activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw]): Int = {
    var flawNum = 0
    var numCrossed = 0
    while (flawNum < flaws.length) {
      if (!flaws(flawNum).isInstanceOf[EfficientCausalThreat]) {
        activeFlaws(flawNum) = false
        numCrossed += 1
      }
      flawNum += 1
    }
    numCrossed
  }
}

object FrontFlawFirst extends EfficientFlawSubsetSelector {

  private def getContainedTaskIndices(flaw: EfficientFlaw): Int = flaw match {
    case EfficientAbstractPlanStep(plan, ps) => ps
    case EfficientCausalThreat(plan, cl, threater, _, _) => Math.min(Math.min(cl.producer, cl.consumer), threater)
    case EfficientOpenPrecondition(plan, ps, _) => ps
    case EfficientUnboundVariable(_, _) => -1
  }

  /**
    * removes flaws from consideration. removed flaws should be marked as false and the number of removed flaws should be returned
    */
  override def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var flawNum = 0
    var excludedFlaws = 0
    while (flawNum < activeFlaws.length) {
      val thisFlawPlanStep = getContainedTaskIndices(flaws(flawNum))
      // try to find another flaw which occurs before this one
      var otherFlawNum = 0
      while (otherFlawNum < activeFlaws.length && activeFlaws(flawNum)) {
        if (activeFlaws(otherFlawNum)) {
          val thatFlawPlanStep = getContainedTaskIndices(flaws(otherFlawNum))

          if (thisFlawPlanStep == -1 && thatFlawPlanStep != -1) {
            // if I am not a Open precondition, and the other is, ignore me
            activeFlaws(flawNum) = false
            excludedFlaws += 1
          } else if (thisFlawPlanStep != -1 && thatFlawPlanStep == -1) {
            // other way around
            activeFlaws(otherFlawNum) = false
            excludedFlaws += 1
          } else if (thisFlawPlanStep != -1 && thatFlawPlanStep != -1) {
            if (plan.ordering.lt(thatFlawPlanStep, thisFlawPlanStep)) {
              activeFlaws(flawNum) = false
              excludedFlaws += 1
            }
          }
        }
        otherFlawNum += 1
      }
      flawNum += 1
    }

    excludedFlaws
  }
}


object NewestFlawFirst extends EfficientFlawSubsetSelector {

  private def getContainedTaskIndices(flaw: EfficientFlaw): Int = flaw match {
    case EfficientAbstractPlanStep(plan, ps) => plan.depthPerPlanStep(ps)
    case EfficientCausalThreat(plan, cl, threater, _, _) => Math.max(plan.depthPerCausalLink(plan.causalLinks.indexOf(cl)), plan.depthPerPlanStep(threater))
    case EfficientOpenPrecondition(plan, ps, _) => plan.depthPerPlanStep(ps)
    case EfficientUnboundVariable(_, _) => Int.MaxValue
  }

  /**
    * removes flaws from consideration. removed flaws should be marked as false and the number of removed flaws should be returned
    */
  override def reduceSelection(plan: EfficientPlan, activeFlaws: Array[Boolean], flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var flawNum = 0
    var excludedFlaws = 0
    while (flawNum < activeFlaws.length) {
      val thisFlawPlanSteps = getContainedTaskIndices(flaws(flawNum))
      // try to find another flaw which takes precedence
      var otherFlawNum = 0
      while (otherFlawNum < activeFlaws.length && activeFlaws(flawNum)) {
        if (flawNum != otherFlawNum && activeFlaws(otherFlawNum)) {
          val thatFlawPlanSteps = getContainedTaskIndices(flaws(otherFlawNum))

          val iTakePrecedence = thisFlawPlanSteps >= thatFlawPlanSteps

          if (!iTakePrecedence) {
            activeFlaws(flawNum) = false
            excludedFlaws += 1
          }
        }
        otherFlawNum += 1
      }
      flawNum += 1
    }

    excludedFlaws
  }
}

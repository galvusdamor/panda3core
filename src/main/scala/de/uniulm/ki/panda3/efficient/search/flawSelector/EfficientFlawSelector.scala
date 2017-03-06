package de.uniulm.ki.panda3.efficient.search.flawSelector

import java.util

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientAbstractPlanStep, EfficientFlaw}

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
package de.uniulm.ki.panda3.efficient.search.flawSelector

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.flaw.{EfficientAbstractPlanStep, EfficientFlaw}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientFlawSelector {

  def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int
}

/**
  * Select the flaw with the least number of modifications
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object LeastCostFlawRepair extends EfficientFlawSelector {

  def selectFlaw(plan: EfficientPlan, flaws: Array[EfficientFlaw], numberOfModifications: Array[Int]): Int = {
    var minFlaw = 0
    var flawNum = 1
    while (flawNum < flaws.length) {
      if (numberOfModifications(flawNum) < numberOfModifications(minFlaw))
        minFlaw = flawNum
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

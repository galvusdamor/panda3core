package de.uniulm.ki.panda3

import de.uniulm.ki.panda3.plan.implementation.EfficientPlan

/**
 * Main object of PANDA 3
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object Main {

  def main(args: Array[String]) {

    println("PANDA3 says \"Hello World!\".")

    println()
    println("List of Arguments")
    println("=================")
    args.zipWithIndex map (_.swap) map { case (i, x) => "Argument #" + i + ": " + x} foreach println


    val plan = new EfficientPlan()


    println(plan.planSteps()(0).id)
    println()
    println("EXITING PANDA3")
  }
}

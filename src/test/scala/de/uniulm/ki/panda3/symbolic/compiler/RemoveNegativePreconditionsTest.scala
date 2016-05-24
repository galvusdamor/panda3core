package de.uniulm.ki.panda3.symbolic.compiler

import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, Task, HasExampleProblem3}
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
object RemoveNegativePreconditionsTest extends FlatSpec with HasExampleProblem3 {

  "Removing negative preconditions" must "work correctly" in {
    val (domain, plan) = RemoveNegativePreconditions.transform(domain3, plan1WithBothCausalLinks, ())

    assert(domain.tasks.length == domain3.tasks.length)

    domain3.tasks foreach { originalTask =>
      val newTask: Task = (domain.tasks find { _.name == originalTask.name }).get
      assert(newTask.isInstanceOf[ReducedTask])
      assert(originalTask.isInstanceOf[ReducedTask])
      val reducedTask = newTask.asInstanceOf[ReducedTask]
      val reducedOriginalTask = newTask.asInstanceOf[ReducedTask]
      assert(reducedTask.precondition.conjuncts.length == reducedOriginalTask.precondition.conjuncts.length)
      assert(reducedTask.effect.conjuncts.length == 2 * reducedOriginalTask.effect.conjuncts.length)
    }


  }
}
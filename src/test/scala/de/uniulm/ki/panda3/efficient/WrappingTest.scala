package de.uniulm.ki.panda3.efficient

import de.uniulm.ki.panda3.symbolic.domain.HasExampleProblem4
import org.scalatest.FlatSpec

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
class WrappingTest extends FlatSpec with HasExampleProblem4 {

  var wrapper : Wrapping = null

  "Creating a wrapper" must "not crash" in {
    wrapper = new Wrapping(domain4,plan2WithTwoLinks)
  }

  "Computing the efficient Representation of the domain" must "not crash" in {
    val efficientDomain = wrapper.efficientDomain
  }

  "Unwrapping a plan" must "not crash" in {
    val initialPlan = wrapper.unwrap(plan2WithTwoLinks)
  }

  "Wrapping a plan" must "not crash" in {
    val initialPlan = wrapper.unwrap(plan2WithTwoLinks)

    val wrappedInitialPlan = wrapper.wrap(initialPlan)
  }

}

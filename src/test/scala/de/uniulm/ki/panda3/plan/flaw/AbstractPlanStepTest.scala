package de.uniulm.ki.panda3.plan.flaw

import de.uniulm.ki.panda3.domain.HasExampleProblem3
import org.scalatest.FlatSpec

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
class AbstractPlanStepTest extends FlatSpec with HasExampleProblem3 {

  "Finding all decompositions for an abstract plan step" must "be possible" in {
    val possibleDecompositions = AbstractPlanStep(plan1WithBothCausalLinks, psAbstract1).resolvents(domain3)
    assert(possibleDecompositions.size == 2)
  }
}

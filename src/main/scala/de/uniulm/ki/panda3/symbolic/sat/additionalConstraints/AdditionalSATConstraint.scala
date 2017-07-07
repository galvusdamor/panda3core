package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, EncodingWithLinearPlan}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait AdditionalSATConstraint {

  def apply(linearEncoding: EncodingWithLinearPlan): Seq[Clause]
}

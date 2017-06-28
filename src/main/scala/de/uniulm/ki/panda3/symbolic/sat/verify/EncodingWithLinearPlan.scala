package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic.domain.Task

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EncodingWithLinearPlan extends VerifyEncoding {

  def linearPlan: Seq[Map[Task, String]]
}

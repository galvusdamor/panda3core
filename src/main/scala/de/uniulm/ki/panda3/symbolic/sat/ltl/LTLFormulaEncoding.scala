package de.uniulm.ki.panda3.symbolic.sat.ltl

import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, PathBasedEncoding}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLFormulaEncoding(buechiAutomaton: BüchiAutomaton) {

  def apply[P,I](pathBasedEncoding: PathBasedEncoding[P,I]) : Seq[Clause] = Nil
}

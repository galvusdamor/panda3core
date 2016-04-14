package de.uniulm.ki.panda3.symbolic.search

import de.uniulm.ki.panda3.symbolic.plan.flaw.{AbstractPlanStep, Flaw}
import de.uniulm.ki.panda3.symbolic.plan.modification.Modification

/**
  * Functions that determine how the search is to be conducted
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */


abstract class IsModificationAllowed extends (Modification => Boolean) {}

object NoModifications extends IsModificationAllowed {
  override def apply(v1: Modification): Boolean = false
}

object AllModifications extends IsModificationAllowed {
  override def apply(v1: Modification): Boolean = true
}

case class ModificationsByClass(allowedModifications: Class[_]*) extends IsModificationAllowed {
  override def apply(v1: Modification): Boolean = allowedModifications contains v1.getClass
}

abstract class IsFlawAllowed extends (Flaw => Boolean) {}

object NoFlaws extends IsFlawAllowed {
  override def apply(v1: Flaw): Boolean = false
}

object AllFlaws extends IsFlawAllowed {
  override def apply(v1: Flaw): Boolean = true
}

case class FlawsByClass(allowedFlaws: Class[_]*) extends IsFlawAllowed {
  override def apply(v1: Flaw): Boolean = allowedFlaws contains v1.getClass
}

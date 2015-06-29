package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
trait LogicalConnector extends Formula {

}

case class Not(formula: Formula) extends LogicalConnector {
  override def update(domainUpdate: DomainUpdate): Formula = Not(formula.update(domainUpdate))
}

case class And(left: Formula, right: Formula) extends LogicalConnector {
  override def update(domainUpdate: DomainUpdate): And = And(left.update(domainUpdate), right.update(domainUpdate))
}

case class Or(left: Formula, right: Formula) extends LogicalConnector {
  override def update(domainUpdate: DomainUpdate): Or = Or(left.update(domainUpdate), right.update(domainUpdate))
}


case class Implies(left: Formula, right: Formula) extends LogicalConnector {
  override def update(domainUpdate: DomainUpdate): Implies = Implies(left.update(domainUpdate), right.update(domainUpdate))
}

case class Equivalence(left: Formula, right: Formula) extends LogicalConnector {
  override def update(domainUpdate: DomainUpdate): Equivalence = Equivalence(left.update(domainUpdate), right.update(domainUpdate))
}
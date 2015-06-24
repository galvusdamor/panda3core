package de.uniulm.ki.panda3.logic

import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.DomainUpdate

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class DecompositionAxiom(abstractPredicate: Predicate, parameterVariables: Seq[Variable], rightHandSide: Formula) extends DomainUpdatable {
  override def update(domainUpdate: DomainUpdate): DecompositionAxiom = DecompositionAxiom(abstractPredicate.update(domainUpdate), parameterVariables map {_.update(domainUpdate)},
                                                                                           rightHandSide.update(domainUpdate))
}

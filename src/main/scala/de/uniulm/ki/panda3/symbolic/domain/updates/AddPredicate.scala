package de.uniulm.ki.panda3.symbolic.domain.updates

import de.uniulm.ki.panda3.symbolic.logic.Predicate

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class AddPredicate(newPredicates: Seq[Predicate]) {
}

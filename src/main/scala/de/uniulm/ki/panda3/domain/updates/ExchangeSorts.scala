package de.uniulm.ki.panda3.domain.updates

import de.uniulm.ki.panda3.logic.Sort

/**
 * exchange the sorts of a domain
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class ExchangeSorts(exchangeMap: Map[Sort, Sort]) extends DomainUpdate {
}
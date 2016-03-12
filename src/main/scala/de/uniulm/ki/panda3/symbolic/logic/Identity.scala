package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate
import de.uniulm.ki.util.HashMemo

/**
 *
 *
 * @author Daniel Höller (daniel.hoeller@uni-ulm.de)
 */
trait Identity extends Formula with PrettyPrintable{
  override def update(domainUpdate: DomainUpdate): Formula
}

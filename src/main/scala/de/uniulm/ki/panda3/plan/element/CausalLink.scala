package de.uniulm.ki.panda3.plan.element

import de.uniulm.ki.panda3.PrettyPrintable
import de.uniulm.ki.panda3.domain.DomainUpdatable
import de.uniulm.ki.panda3.domain.updates.{DomainUpdate, ExchangePlanStep}
import de.uniulm.ki.panda3.logic.Literal

/**
 *
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
case class CausalLink(producer: PlanStep, consumer: PlanStep, condition: Literal) extends DomainUpdatable with PrettyPrintable {
  def containsOne(pss: PlanStep*): Boolean = (pss foldLeft false) { case (b, ps) => b || contains(ps) }

  def contains(ps: PlanStep): Boolean = ps == producer || ps == consumer

  override def update(domainUpdate: DomainUpdate): CausalLink = domainUpdate match {
    case ExchangePlanStep(oldPS, newPS) => CausalLink(if (oldPS == producer) newPS else producer, if (oldPS == consumer) newPS else consumer, condition)
    case _                              => CausalLink(producer.update(domainUpdate), consumer.update(domainUpdate), condition.update(domainUpdate))
  }

  /** returns a short information about the object */
  override def shortInfo: String = "CL: " + producer.shortInfo + " -> " + consumer.shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + " @ " + condition.shortInfo
}
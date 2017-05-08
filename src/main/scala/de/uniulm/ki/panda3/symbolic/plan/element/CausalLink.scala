package de.uniulm.ki.panda3.symbolic.plan.element

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangePlanSteps, DomainUpdate}
import de.uniulm.ki.panda3.symbolic.logic.Literal

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class CausalLink(producer: PlanStep, consumer: PlanStep, condition: Literal) extends DomainUpdatable with PrettyPrintable {
  assert(producer.schema.isAbstract || producer != consumer)


  def containsOne(pss: PlanStep*): Boolean = (pss foldLeft false) { case (b, ps) => b || contains(ps) }

  def containsOnly(pss: PlanStep*): Boolean = pss.contains(producer) && pss.contains(consumer)

  def contains(ps: PlanStep): Boolean = ps == producer || ps == consumer

  override def update(domainUpdate: DomainUpdate): CausalLink = domainUpdate match {
    case ExchangePlanSteps(exchangeMap) => CausalLink(if (exchangeMap contains producer) exchangeMap(producer) else producer,
                                                      if (exchangeMap contains consumer) exchangeMap(consumer) else consumer, condition)
    case _                              => CausalLink(producer.update(domainUpdate), consumer.update(domainUpdate), condition.update(domainUpdate))
  }

  /** returns a short information about the object */
  override def shortInfo: String = "CL: " + producer.shortInfo + " -> " + consumer.shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + " @ " + condition.shortInfo
}
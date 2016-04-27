package de.uniulm.ki.panda3.efficient.plan.flaw

import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.efficient.plan.modification.EfficientModification
import de.uniulm.ki.panda3.symbolic.PrettyPrintable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait EfficientFlaw extends PrettyPrintable{
  /**
    * The plan in which this flaw is valid
    */
  val plan : EfficientPlan

  val resolver : Array[EfficientModification]

  val estimatedNumberOfResolvers : Int

  def severLinkToPlan : EfficientFlaw

  def equalToSeveredFlaw(flaw : EfficientFlaw) : Boolean

  /** returns a detailed information about the object */
  override def longInfo: String = shortInfo

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo
}
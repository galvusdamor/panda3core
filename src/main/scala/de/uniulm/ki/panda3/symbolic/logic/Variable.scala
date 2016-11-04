package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp.CSP
import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeVariable, DomainUpdate}
import de.uniulm.ki.util.HashMemo

/**
  * Represents variables of a [[CSP]].
  * Each variable has a name and it belongs to some [[Sort]].
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class Variable(id: Int, name: String, sort: Sort) extends Value with PrettyPrintable with HashMemo {
  /** the map must contain EVERY sort of the domain, even if does not change */
  override def update(domainUpdate: DomainUpdate): Variable = domainUpdate match {
    case ExchangeVariable(oldVariable, newVariable) => if (this == oldVariable) newVariable else this
    case _                                          => Variable(id, name, sort.update(domainUpdate))
  }

  override val isConstant: Boolean = false

  /** returns a short information about the object */
  override def shortInfo: String = name + ":" + id

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = shortInfo + ":" + sort.shortInfo

  override def equals(o: scala.Any): Boolean = if (o.isInstanceOf[Variable]){
    val ov = o.asInstanceOf[Variable]
    if (this.id != ov.id) false
    else if (this.hashCode != o.hashCode()) false
    else this.name == ov.name
    //else this.sort == ov.sort
  } else false
}

object Variable {
  private var varCounter: Long = 0

  def nextFreeVariableID(): Long = this.synchronized({ varCounter += 1; varCounter })
}
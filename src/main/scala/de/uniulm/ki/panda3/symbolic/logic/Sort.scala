package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.domain.DomainUpdatable
import de.uniulm.ki.panda3.symbolic.domain.updates.{DomainUpdate, ExchangeSorts}
import de.uniulm.ki.util.HashMemo

/**
  * Sorts aggregate constants of First Order Logic
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off covariant.equals
case class Sort(name: String, elements: Seq[Constant], subSorts: Seq[Sort]) extends DomainUpdatable with PrettyPrintable with HashMemo {

  lazy val elementSet : Set[Constant] = elements toSet

  lazy val allElements: Seq[Constant] = elements ++ (subSorts flatMap {_.allElements})

  override def update(domainUpdate: DomainUpdate): Sort = (domainUpdate match {
    case ExchangeSorts(map) => if (map.contains(this)) map(this) else this
    case _                  => this
  }) match {
    case Sort(n, e, ss) => Sort(n, e, ss map { _ update domainUpdate })
  }

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo + ": " + (elements map { _.shortInfo }).mkString(", ") + "; subsorts: " + (subSorts map { _.shortInfo }).mkString(", ")

  override def equals(o: scala.Any): Boolean = if (!o.isInstanceOf[Sort]) false
  else if (this.hashCode == o.hashCode()) true
  else {
    val oSort = o.asInstanceOf[Sort]
    if (this.name != oSort.name) false else this.elements.sameElements(oSort.elements) && this.subSorts.sameElements(oSort.subSorts)
  }
}

object Sort {
  def allPossibleInstantiations(sorts: Seq[Sort]): Seq[Seq[Constant]] =
    sorts.foldLeft[Seq[Seq[Constant]]](Nil :: Nil)({ case (args, sort) => sort.elements flatMap { c => args map { _ :+ c } } })

  def allPossibleInstantiationsWithVariables(varsWithValues: Seq[(Variable, Seq[Constant])]): Seq[Seq[(Variable, Constant)]] =
    varsWithValues.foldLeft[Seq[Seq[(Variable, Constant)]]](Nil :: Nil)({ case (args, (vari, values)) => values flatMap { c => args map { _ :+(vari, c) } } })
}
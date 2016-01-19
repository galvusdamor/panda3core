package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.domain.updates.DomainUpdate

/**
  *
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait LogicalConnector extends Formula {

}

trait Quantor extends Formula {

}

// TODO: include more and better reductions

// TODO: implement better info strings
case class Not(formula: Formula) extends LogicalConnector with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): Formula =
    formula.update(domainUpdate) match {
      case Not(sub) => sub
      case f        => Not(f)
    }

  override def longInfo: String = "!" + formula.longInfo

  override val isEmpty: Boolean = formula.isEmpty
}

case class And[SubFormulas <: Formula](conjuncts: Seq[SubFormulas]) extends LogicalConnector with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): And[Formula] = {
    val subreduced = conjuncts map { _ update domainUpdate }
    val flattenedSubs = subreduced flatMap {
      case And(sub) => sub
      case f        => f :: Nil
    }
    And[Formula](flattenedSubs)
  }


  override def longInfo: String = (conjuncts map { _.longInfo }).mkString("\n")

  override val isEmpty: Boolean = conjuncts forall { _.isEmpty }
}

case class Or[SubFormulas <: Formula](disjuncts: Seq[SubFormulas]) extends LogicalConnector with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): Or[Formula] = Or[Formula](disjuncts map { _ update domainUpdate })

  override def longInfo: String = (disjuncts map { _.longInfo }).mkString("\n")

  override val isEmpty: Boolean = disjuncts forall { _.isEmpty }
}

case class Implies(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): Implies = Implies(left.update(domainUpdate), right.update(domainUpdate))

  override def longInfo: String = left.longInfo + " => " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty
}

case class Equivalence(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): Equivalence = Equivalence(left.update(domainUpdate), right.update(domainUpdate))

  override def longInfo: String = left.longInfo + " == " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty
}

case class Exists(v: Variable, formula: Formula) extends Quantor with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): Exists = Exists(v.update(domainUpdate), formula.update(domainUpdate))

  override def longInfo: String = "exists " + v.shortInfo + " in (" + formula.longInfo + ")"

  override val isEmpty: Boolean = formula.isEmpty
}

case class Forall(v: Variable, formula: Formula) extends Quantor with DefaultLongInfo {
  override def update(domainUpdate: DomainUpdate): Forall = Forall(v.update(domainUpdate), formula.update(domainUpdate))

  override def longInfo: String = "forall " + v.shortInfo + " in (" + formula.longInfo + ")"

  override val isEmpty: Boolean = formula.isEmpty
}
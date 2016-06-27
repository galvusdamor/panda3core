package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo

//import de.uniulm.ki.panda3.symbolic.domain.updates.{ReduceFormula, DomainUpdate}

import de.uniulm.ki.panda3.symbolic.domain.updates.{ReduceFormula, DomainUpdate}
import de.uniulm.ki.util.HashMemo

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
case class Not(formula: Formula) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Formula =
    formula.update(domainUpdate) match {
      case Literal(predicate, isPositive, parameters) => Literal(predicate, !isPositive, parameters)
      case Not(sub) => sub // eliminate double negation
      case f => Not(f)
    }

  lazy val containedVariables: Set[Variable] = formula.containedVariables

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = formula.containedPredicatesWithSign

  override def longInfo: String = "!" + formula.longInfo

  override val isEmpty: Boolean = formula.isEmpty
}

/*
case class And[SubFormulas <: Formula](conjuncts: Seq[SubFormulas]) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): And[Formula] = {
    val subreduced = conjuncts map {
      _ update domainUpdate
    }
    val flattenedSubs = subreduced flatMap {
      case And(sub) => sub
      case f => f :: Nil
    }
    And[Formula](flattenedSubs)
  }


  }*/

case class And[SubFormulas <: Formula](conjuncts: Seq[SubFormulas]) extends LogicalConnector with DefaultLongInfo with HashMemo {
  assert(conjuncts forall {_ != null})
  override def update(domainUpdate: DomainUpdate): Formula = {
    val subreduced = conjuncts map {
      _ update domainUpdate
    }

    domainUpdate match {
      case ReduceFormula() =>
        val identitiesRemoved = subreduced filter {
          case Identity() => false
          case _ => true
        }

        val flattenedSubs = identitiesRemoved flatMap {
          case And(sub) => sub
          case f => f :: Nil
        }
        And[Formula](flattenedSubs)
      case _ => And[Formula](subreduced)
    }
  }

  lazy val containedVariables: Set[Variable] = conjuncts.flatMap(_.containedVariables).toSet

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = conjuncts flatMap {case f : Formula => f.containedPredicatesWithSign} toSet

  override def longInfo: String = (conjuncts map {
    _.longInfo
  }).mkString("\n")

  override val isEmpty: Boolean = conjuncts forall {
    _.isEmpty
  }

  lazy val containsOnlyLiterals = conjuncts forall { case l: Literal => true; case _ => false}
}

case class Identity[SubFormulas <: Formula]() extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Identity[Formula] = new Identity[Formula]()

  lazy val containedVariables: Set[Variable] = Set[Variable]()

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = Set[(Predicate,Boolean)]()

  override def longInfo: String = "Identity"

  override val isEmpty: Boolean = true
}

case class Or[SubFormulas <: Formula](disjuncts: Seq[SubFormulas]) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Or[Formula] = Or[Formula](disjuncts map {
    _ update domainUpdate
  })

  lazy val containedVariables: Set[Variable] = disjuncts.flatMap(_.containedVariables).toSet

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = disjuncts flatMap {case f : Formula => f.containedPredicatesWithSign} toSet

  override def longInfo: String = (disjuncts map {
    _.longInfo
  }).mkString("\n")

  override val isEmpty: Boolean = disjuncts forall {
    _.isEmpty
  }
}

case class Implies(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Implies = Implies(left.update(domainUpdate), right.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = left.containedVariables ++ right.containedVariables

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = left.containedPredicatesWithSign ++ right.containedPredicatesWithSign

  override def longInfo: String = left.longInfo + " => " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty
}

case class Equivalence(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Equivalence = Equivalence(left.update(domainUpdate), right.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = left.containedVariables ++ right.containedVariables

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = left.containedPredicatesWithSign ++ right.containedPredicatesWithSign

  override def longInfo: String = left.longInfo + " == " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty
}

case class Exists(v: Variable, formula: Formula) extends Quantor with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Exists = Exists(v.update(domainUpdate), formula.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = formula.containedVariables - v

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = formula.containedPredicatesWithSign

  override def longInfo: String = "exists " + v.shortInfo + " in (" + formula.longInfo + ")"

  override val isEmpty: Boolean = formula.isEmpty
}

case class Forall(v: Variable, formula: Formula) extends Quantor with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Forall = Forall(v.update(domainUpdate), formula.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = formula.containedVariables - v

  lazy val containedPredicatesWithSign : Set[(Predicate,Boolean)] = formula.containedPredicatesWithSign

  override def longInfo: String = "forall " + v.shortInfo + " in (" + formula.longInfo + ")"

  override val isEmpty: Boolean = formula.isEmpty
}
package de.uniulm.ki.panda3.symbolic.logic

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.csp.VariableConstraint

//import de.uniulm.ki.panda3.symbolic.domain.updates.{ReduceFormula, DomainUpdate}

import de.uniulm.ki.panda3.symbolic.domain.updates.{ExchangeVariable, ReduceFormula, DomainUpdate}
import de.uniulm.ki.util.{Internable, HashMemo}

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
      case Not(sub)                                   => sub // eliminate double negation
      case f                                          => Not(f)
    }

  lazy val containedVariables: Set[Variable] = formula.containedVariables

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = formula.containedPredicatesWithSign map { case (a, b, c) => (a, b, !c) }

  override def longInfo: String = "!" + formula.longInfo

  override val isEmpty: Boolean = formula.isEmpty

  def compileQuantors(): (Formula, Seq[Variable]) = {
    val (inner, vars) = formula.compileQuantors()

    (Not(inner), vars)
  }
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
  assert(!(conjuncts contains null))

  override def update(domainUpdate: DomainUpdate): Formula = {
    val subreduced = conjuncts map {
      _ update domainUpdate
    }

    domainUpdate match {
      case ReduceFormula() =>
        val identitiesRemoved = subreduced filter {
          case Identity() => false
          case _          => true
        }

        val flattenedSubs = identitiesRemoved flatMap {
          case And(sub) => sub
          case f        => f :: Nil
        }
        And.intern(flattenedSubs)
      case _               => And.intern(subreduced)
    }
  }

  lazy val containedVariables: Set[Variable] = conjuncts.flatMap(_.containedVariables).toSet

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = conjuncts flatMap { case f: Formula => f.containedPredicatesWithSign } toSet

  override def longInfo: String = (conjuncts map { _.longInfo }).mkString("\n")

  override val isEmpty: Boolean = conjuncts forall { _.isEmpty }

  lazy val containsOnlyLiterals = conjuncts forall { case l: Literal => true; case _ => false }

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    val (newconj, newvars) = conjuncts map { _.compileQuantors() } unzip

    (And.intern(newconj), newvars flatten)
  }
}

object And extends Internable[Seq[Formula], And[Formula]] {
  override protected val applyTuple: Seq[Formula] => And[Formula] = { f => And.apply(f) }
}

case class Identity[SubFormulas <: Formula]() extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Identity[Formula] = new Identity[Formula]()

  lazy val containedVariables: Set[Variable] = Set[Variable]()

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = Set[(Predicate, Seq[Variable], Boolean)]()

  override def longInfo: String = "Identity"

  override val isEmpty: Boolean = true

  override def compileQuantors(): (Formula, Seq[Variable]) = (this, Nil)
}

case class Or[SubFormulas <: Formula](disjuncts: Seq[SubFormulas]) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Or[Formula] = Or[Formula](disjuncts map {
    _ update domainUpdate
  })

  lazy val containedVariables: Set[Variable] = disjuncts.flatMap(_.containedVariables).toSet

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = disjuncts flatMap { case f: Formula => f.containedPredicatesWithSign } toSet

  override def longInfo: String = (disjuncts map {
    _.longInfo
  }).mkString("\n")

  override val isEmpty: Boolean = disjuncts forall {
    _.isEmpty
  }

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    val (newdis, newvars) = disjuncts map { _.compileQuantors() } unzip

    (Or(newdis), newvars flatten)
  }
}

case class Implies(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Implies = Implies(left.update(domainUpdate), right.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = left.containedVariables ++ right.containedVariables

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = left.containedPredicatesWithSign ++ right.containedPredicatesWithSign

  override def longInfo: String = left.longInfo + " => " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    val (lForm, lVars) = left.compileQuantors()
    val (rForm, rVars) = right.compileQuantors()

    (Implies(lForm, rForm), lVars ++ rVars)
  }
}

case class When(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): When = When(left.update(domainUpdate), right.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = left.containedVariables ++ right.containedVariables

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = left.containedPredicatesWithSign ++ right.containedPredicatesWithSign

  override def longInfo: String = left.longInfo + " ~> " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    val (lForm, lVars) = left.compileQuantors()
    val (rForm, rVars) = right.compileQuantors()

    (Implies(lForm, rForm), lVars ++ rVars)
  }
}

case class Equivalence(left: Formula, right: Formula) extends LogicalConnector with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Equivalence = Equivalence(left.update(domainUpdate), right.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = left.containedVariables ++ right.containedVariables

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = left.containedPredicatesWithSign ++ right.containedPredicatesWithSign

  override def longInfo: String = left.longInfo + " == " + right.longInfo

  override val isEmpty: Boolean = left.isEmpty && right.isEmpty

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    val (lForm, lVars) = left.compileQuantors()
    val (rForm, rVars) = right.compileQuantors()

    (Equivalence(lForm, rForm), lVars ++ rVars)
  }
}

case class Exists(v: Variable, formula: Formula) extends Quantor with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Exists = Exists(v.update(domainUpdate), formula.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = formula.containedVariables - v

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = formula.containedPredicatesWithSign

  override def longInfo: String = "exists " + v.shortInfo + " in (" + formula.longInfo + ")"

  override val isEmpty: Boolean = formula.isEmpty

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    val (innerFormula, innerVars) = formula.compileQuantors()

    // create for instance for the quantifier
    val newVar = v.copy(name = v.name + "_compiled_" + Variable.nextFreeVariableID())

    (innerFormula update ExchangeVariable(v, newVar), innerVars :+ newVar)
  }
}

object Exists {
  def apply(vs: Seq[Variable], f: Formula): Formula = {
    if (vs.isEmpty) {f } else {Exists(vs.head, Exists(vs.tail, f)) }
  }
}

case class Forall(v: Variable, formula: Formula) extends Quantor with DefaultLongInfo with HashMemo {
  override def update(domainUpdate: DomainUpdate): Forall = Forall(v.update(domainUpdate), formula.update(domainUpdate))

  lazy val containedVariables: Set[Variable] = formula.containedVariables - v

  lazy val containedPredicatesWithSign: Set[(Predicate, Seq[Variable], Boolean)] = formula.containedPredicatesWithSign

  override def longInfo: String = "forall " + v.shortInfo + " in (" + formula.longInfo + ")"

  override val isEmpty: Boolean = formula.isEmpty

  override def compileQuantors(): (Formula, Seq[Variable]) = {
    // create for instance for the quantifier
    val newForlumaeAndVars = v.sort.elements map { c =>
      val (innerFormula, innerVars) = formula.compileQuantors()
      val newSort = Sort(v.sort.name, c :: Nil, Nil)
      val newVar = v.copy(name = v.name + "_compiled_" + Variable.nextFreeVariableID() + c.name, sort = newSort)

      (innerFormula update ExchangeVariable(v, newVar), innerVars :+ newVar)
    }

    (And(newForlumaeAndVars map { _._1 }), newForlumaeAndVars flatMap { _._2 })
  }
}

object Forall {
  def apply(vs: Seq[Variable], f: Formula): Formula = {
    if (vs.isEmpty) {f } else {Forall(vs.head, Forall(vs.tail, f)) }
  }
}


// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
sealed trait LTLFormula extends DefaultLongInfo {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String]

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula

  def simplify: LTLFormula

  def nnf: LTLFormula

  def negate: LTLFormula

  def allPredicates: Set[Predicate]

  def allPredicatesNames: Set[String]

  def allSubformulae: Set[LTLFormula]

  def toPositiveBooleanFormula: PositiveBooleanFormula

  lazy val allStates          : Seq[Set[Predicate]]                   = de.uniulm.ki.util.allSubsets(allPredicates.toSeq) map { _.toSet }
  lazy val allStatesAndCounter: Seq[(Set[Predicate], Set[Predicate])] = allStates map { s => (s, allPredicates -- s) }
}

//case class GroundTaskAtom(groundedTask: GroundTask) extends LTLFormula


//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////
// LTL only for parsing
//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////


/**
  * Task Atom as String
  */
case class TaskNameAtom(task: String, arguments: Seq[String]) extends LTLFormula {
  assert(!task.contains("["))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val fullName = task + arguments.map({ a => if (a.startsWith("?")) variableContext(a) else a }).mkString("[", ",", "]")

    domain.tasks.find(_.name == fullName) match {
      case Some(t) => TaskAtom(t)
      case None    => LTLFalse
    }
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = if (!(arguments contains variable)) Nil
  else {
    val matchingTasks = domain.primitiveTasks filter { _.name startsWith (task + "[") }

    matchingTasks flatMap { t =>
      val consts = t.name.split('[')(1).dropRight(1).split(',')

      // check with variable context ...
      val isCompatibleWithConstants = arguments zip consts forall { case (a, b) => a.startsWith("?") || b.startsWith("?") || a == b }
      val isCompatibleWithContext = variableContext forall { case (v, c) => arguments.zipWithIndex forall { case (a, i) => a != v || consts(i) == c } }

      val allValuesForVar = arguments.zipWithIndex collect { case (a, i) if a == variable => consts(i) } distinct

      // only possible if unique value ...
      if (allValuesForVar.size == 1 && isCompatibleWithConstants && isCompatibleWithContext) allValuesForVar else Nil
    }
  }

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = task + arguments.mkString("(", ",", ")")

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)

  lazy val allPredicates: Set[Predicate] = ???

  lazy val allPredicatesNames: Set[String] = Set()

  lazy val allSubformulae: Set[LTLFormula] = ???

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}


case class TaskNameAtomAnyArgument(task: String) extends LTLFormula {
  assert(!task.contains("["))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val allTasks = domain.tasks filter { _.name startsWith task }

    if (allTasks.isEmpty) LTLFalse else LTLOr(allTasks map { t => TaskAtom(t) })
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = task

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)

  lazy val allPredicates: Set[Predicate] = ???

  lazy val allPredicatesNames: Set[String] = Set()

  lazy val allSubformulae: Set[LTLFormula] = ???

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}

case class PredicateNameAtom(predicate: String, arguments: Seq[String]) extends LTLFormula {
  assert(!predicate.contains("["))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val fullName = predicate + arguments.map({ a => if (a.startsWith("?")) variableContext(a) else a }).mkString("[", ",", "]")
    //println("PRED " + fullName)
    //println(domain.predicates.map(_.name).mkString("\n"))

    domain.predicates.find(_.name == fullName) match {
      case Some(p) => PredicateAtom(p)
      case None    => LTLFalse
    }
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = if (!(arguments contains variable)) Nil
  else {
    val matchingPredicates = domain.predicates filter { p => p.name.startsWith(predicate + "[") || p.name.startsWith("-" + predicate + "[") }
    //println("Preds: " + matchingPredicates.map(_.name).mkString("\n"))

    matchingPredicates flatMap { t =>
      val consts = t.name.split('[')(1).dropRight(1).split(',')

      // check with variable context ...
      val isCompatibleWithConstants = arguments zip consts forall { case (a, b) => a.startsWith("?") || b.startsWith("?") || a == b }
      val isCompatibleWithContext = variableContext forall { case (v, c) => arguments.zipWithIndex forall { case (a, i) => a != v || consts(i) == c } }

      val allValuesForVar = arguments.zipWithIndex collect { case (a, i) if a == variable => consts(i) } distinct

      // only possible if unique value ...
      if (allValuesForVar.size == 1 && isCompatibleWithConstants && isCompatibleWithContext) allValuesForVar else Nil
    }
  }

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = predicate + arguments.mkString("(", ",", ")")

  lazy val nnf: LTLFormula = if (predicate.startsWith("+") || predicate.startsWith("-")) this else this.copy(predicate = "+" + predicate)

  lazy val negate: LTLFormula = LTLNot(this)

  lazy val allPredicates: Set[Predicate] = ???

  lazy val allPredicatesNames: Set[String] = Set(predicate)

  lazy val allSubformulae: Set[LTLFormula] = ???

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}


case class LTLForall(variable: String, sort: String, subFormula: LTLFormula) extends LTLFormula {
  assert(variable.startsWith("?"))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val values = liftedDomain.sorts.find({ _.name == sort }).get.elements map { _.name }
    println("∀ Variable " + variable + ": " + values.mkString(", "))

    LTLAnd(values map { v => subFormula.parseAndGround(domain, liftedDomain, variableContext + ((variable, v))) })
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    if (variable == this.variable) Nil else subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = "∀" + variable + " : " + subFormula.longInfo

  lazy val nnf: LTLFormula = this.copy(subFormula = subFormula.nnf)

  lazy val negate: LTLFormula = LTLExists(variable, sort, subFormula.negate)

  lazy val allPredicates: Set[Predicate] = ???

  lazy val allPredicatesNames: Set[String] = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = ???

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}

case class LTLExists(variable: String, sort: String, subFormula: LTLFormula) extends LTLFormula {
  assert(variable.startsWith("?"))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val values = subFormula.valuesForVariable(domain, variable, variableContext)
    println("Variable " + variable + ": " + values.mkString(", "))

    LTLOr(values map { v => subFormula.parseAndGround(domain, liftedDomain, variableContext + ((variable, v))) })
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    if (variable == this.variable) Nil else subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = "∃ " + variable + " : " + subFormula.longInfo

  lazy val nnf: LTLFormula = this.copy(subFormula = subFormula.nnf)

  lazy val negate: LTLFormula = LTLForall(variable, sort, subFormula.negate)

  lazy val allPredicates: Set[Predicate] = ???

  lazy val allPredicatesNames: Set[String] = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = ???

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}


//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////
// Actual LTL constructs
//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////

/**
  * Task Atom
  */
case class TaskAtom(task: Task) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = this

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = if (task == currentTask) LTLTrue else LTLFalse

  lazy val simplify: LTLFormula = this

  override def longInfo: String = task.name //.split('[').head

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)

  lazy val allPredicates: Set[Predicate] = Set()

  lazy val allPredicatesNames: Set[String] = ???

  lazy val allSubformulae: Set[LTLFormula] = Set(this)

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * Task Atom
  */
case class PredicateAtom(predicate: Predicate) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = this

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = if (state contains predicate) LTLTrue else LTLFalse

  lazy val simplify: LTLFormula = this

  override def longInfo: String = predicate.name //.split('[').head

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)

  lazy val allPredicates: Set[Predicate] = Set(predicate)

  lazy val allPredicatesNames: Set[String] = ???

  lazy val allSubformulae: Set[LTLFormula] = Set(this)

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}


/**
  * Not
  */
case class LTLNot(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLNot(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = subFormula match {
    case TaskAtom(task)           => if (task == currentTask) LTLFalse else LTLTrue
    case PredicateAtom(predicate) => if (state contains predicate) LTLFalse else LTLTrue
  }

  lazy val simplify: LTLFormula = subFormula.simplify match {
    case LTLTrue   => LTLFalse
    case LTLFalse  => LTLTrue
    case LTLNot(x) => x // double negation
    case x         => LTLNot(x)
  }

  lazy val nnf: LTLFormula = subFormula match {
    case x: TaskAtom                => LTLNot(x)
    case x: TaskNameAtom            => LTLNot(x)
    case x: TaskNameAtomAnyArgument => LTLNot(x)
    case x: PredicateAtom           => LTLNot(x)
    case x: PredicateNameAtom       => x.copy(predicate = "-" + x.predicate)
    case _                          => subFormula.negate.nnf
  }

  lazy val negate: LTLFormula = subFormula

  override def longInfo: String = "!" + subFormula.longInfo

  lazy val allPredicates: Set[Predicate] = subFormula.allPredicates

  lazy val allPredicatesNames: Set[String] = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * And
  */
case class LTLAnd(subFormulae: Seq[LTLFormula]) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLAnd(subFormulae.map(_.parseAndGround(domain, liftedDomain, variableContext)))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    subFormulae flatMap { _.valuesForVariable(domain, variable, variableContext) } distinct

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = LTLAnd(subFormulae map { _.delta(currentTask, state, last) })

  lazy val simplify: LTLFormula = {
    val simplifiedSubformulae = subFormulae map { _.simplify } flatMap {
      case LTLAnd(x) => x
      case x         => x :: Nil
    } filterNot { _ == LTLTrue } distinct

    if (simplifiedSubformulae.isEmpty) LTLTrue
    else if (simplifiedSubformulae contains LTLFalse) LTLFalse
    else if (simplifiedSubformulae.length == 1) simplifiedSubformulae.head
    else LTLAnd(simplifiedSubformulae)
  }

  lazy val nnf: LTLFormula = LTLAnd(subFormulae map { _.nnf })

  lazy val negate: LTLFormula = LTLOr(subFormulae map { _.negate })

  override def longInfo: String = "(" + subFormulae.map(_.longInfo).mkString(" & ") + ")"

  lazy val allPredicates: Set[Predicate] = subFormulae flatMap { _.allPredicates } toSet

  lazy val allPredicatesNames: Set[String] = subFormulae.flatMap(_.allPredicatesNames).toSet

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormulae.flatMap(_.allSubformulae)

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveAnd(subFormulae map { _.toPositiveBooleanFormula })
}

/**
  * Or
  */
case class LTLOr(subFormulae: Seq[LTLFormula]) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLOr(subFormulae.map(_.parseAndGround(domain, liftedDomain, variableContext)))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    subFormulae flatMap { _.valuesForVariable(domain, variable, variableContext) } distinct

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = LTLOr(subFormulae map { _.delta(currentTask, state, last) })

  lazy val simplify: LTLFormula = {
    val simplifiedSubformulae = subFormulae map { _.simplify } flatMap {
      case LTLOr(x) => x
      case x        => x :: Nil
    } filterNot { _ == LTLFalse } distinct

    if (simplifiedSubformulae.isEmpty) LTLFalse
    else if (simplifiedSubformulae contains LTLTrue) LTLTrue
    else if (simplifiedSubformulae.length == 1) simplifiedSubformulae.head

    else LTLOr(simplifiedSubformulae)
  }

  lazy val nnf: LTLFormula = LTLOr(subFormulae map { _.nnf })

  lazy val negate: LTLFormula = LTLAnd(subFormulae map { _.negate })

  override def longInfo: String = "(" + subFormulae.map(_.longInfo).mkString(" v ") + ")"

  lazy val allPredicates     : Set[Predicate] = subFormulae flatMap { _.allPredicates } toSet
  lazy val allPredicatesNames: Set[String]    = subFormulae flatMap { _.allPredicatesNames } toSet

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormulae.flatMap(_.allSubformulae)

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveOr(subFormulae map { _.toPositiveBooleanFormula })
}

case class LTLImply(left: LTLFormula, right: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLImply(left.parseAndGround(domain, liftedDomain, variableContext), right.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (left.valuesForVariable(domain, variable, variableContext) ++ right.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ??? // not needed

  lazy val simplify: LTLFormula = ???

  lazy val nnf: LTLFormula = LTLOr(LTLNot(left).nnf :: right.nnf :: Nil)

  lazy val negate: LTLFormula = ???

  override def longInfo: String = "(" + left.longInfo + " -> " + right.longInfo + ")"

  lazy val allPredicates: Set[Predicate] = ???

  lazy val allPredicatesNames: Set[String] = left.allPredicatesNames ++ right.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ left.allSubformulae ++ right.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}


case class LTLEquiv(left: LTLFormula, right: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLEquiv(left.parseAndGround(domain, liftedDomain, variableContext), right.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (left.valuesForVariable(domain, variable, variableContext) ++ right.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = ??? // not needed

  lazy val simplify: LTLFormula = ???

  lazy val nnf: LTLFormula = LTLAnd(LTLImply(left, right).nnf :: LTLImply(right, left).nnf :: Nil)

  lazy val negate: LTLFormula = ???

  override def longInfo: String = "(" + left.longInfo + " <-> " + right.longInfo + ")"

  lazy val allPredicates     : Set[Predicate] = ???
  lazy val allPredicatesNames: Set[String]    = ???

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ left.allSubformulae ++ right.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = ???
}

/**
  * Next
  */
case class LTLNext(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLNext(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = if (last) LTLFalse else subFormula

  lazy val simplify: LTLFormula = LTLNext(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLNext(subFormula.nnf)

  lazy val negate: LTLFormula = LTLWeakNext(subFormula.negate)

  override def longInfo: String = "X " + subFormula.longInfo

  lazy val allPredicates     : Set[Predicate] = subFormula.allPredicates
  lazy val allPredicatesNames: Set[String]    = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * Weak Next
  */
case class LTLWeakNext(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLWeakNext(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = if (last) LTLTrue else subFormula

  lazy val simplify: LTLFormula = LTLWeakNext(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLWeakNext(subFormula.nnf)

  lazy val negate: LTLFormula = LTLNext(subFormula.negate)

  override def longInfo: String = "WX " + subFormula.longInfo

  lazy val allPredicates     : Set[Predicate] = subFormula.allPredicates
  lazy val allPredicatesNames: Set[String]    = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * Always
  */
case class LTLAlways(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLAlways(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = LTLAnd(subFormula.delta(currentTask, state, last) ::
                                                                                            LTLWeakNext(this).delta(currentTask, state, last) :: Nil)

  lazy val simplify: LTLFormula = LTLAlways(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLAlways(subFormula.nnf)

  lazy val negate: LTLFormula = LTLEventually(subFormula.negate)

  override def longInfo: String = "[] " + subFormula.longInfo

  lazy val allPredicates     : Set[Predicate] = subFormula.allPredicates
  lazy val allPredicatesNames: Set[String]    = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * Eventually
  */
case class LTLEventually(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLEventually(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = LTLOr(subFormula.delta(currentTask, state, last) ::
                                                                                           LTLNext(this).delta(currentTask, state, last) :: Nil)

  lazy val simplify: LTLFormula = LTLEventually(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLEventually(subFormula.nnf)

  lazy val negate: LTLFormula = LTLAlways(subFormula.negate)

  override def longInfo: String = "<> " + subFormula.longInfo

  lazy val allPredicates     : Set[Predicate] = subFormula.allPredicates
  lazy val allPredicatesNames: Set[String]    = subFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ subFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * Until
  */
case class LTLUntil(leftFormula: LTLFormula, rightFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLUntil(leftFormula.parseAndGround(domain, liftedDomain, variableContext), rightFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (leftFormula.valuesForVariable(domain, variable, variableContext) ++ rightFormula.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula =
    LTLOr(rightFormula.delta(currentTask, state, last) ::
            LTLAnd(leftFormula.delta(currentTask, state, last) :: LTLNext(this).delta(currentTask, state, last) :: Nil) :: Nil
         )

  lazy val simplify: LTLFormula = LTLUntil(leftFormula.simplify, rightFormula.simplify)

  lazy val nnf: LTLFormula = LTLUntil(leftFormula.nnf, rightFormula.nnf)

  lazy val negate: LTLFormula = LTLRelease(leftFormula.negate, rightFormula.negate)

  override def longInfo: String = "(" + leftFormula.longInfo + " U " + rightFormula.longInfo + ")"

  lazy val allPredicates     : Set[Predicate] = leftFormula.allPredicates ++ rightFormula.allPredicates
  lazy val allPredicatesNames: Set[String]    = leftFormula.allPredicatesNames ++ rightFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ leftFormula.allSubformulae ++ rightFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

object LTLWeakUntil {
  def apply(left: LTLFormula, right: LTLFormula): LTLFormula = LTLUntil(left, LTLOr(right :: LTLAlways(left) :: Nil))
}

/**
  * Release
  */
case class LTLRelease(leftFormula: LTLFormula, rightFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLRelease(leftFormula.parseAndGround(domain, liftedDomain, variableContext), rightFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (leftFormula.valuesForVariable(domain, variable, variableContext) ++ rightFormula.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula =
    LTLAnd(rightFormula.delta(currentTask, state, last) ::
             LTLAnd(leftFormula.delta(currentTask, state, last) :: LTLWeakNext(this).delta(currentTask, state, last) :: Nil) :: Nil
          )


  lazy val simplify: LTLFormula = LTLRelease(leftFormula.simplify, rightFormula.simplify)

  lazy val nnf: LTLFormula = LTLRelease(leftFormula.nnf, rightFormula.nnf)

  lazy val negate: LTLFormula = LTLUntil(leftFormula.negate, rightFormula.negate)

  override def longInfo: String = "(" + leftFormula.longInfo + " R " + rightFormula.longInfo + ")"

  lazy val allPredicates     : Set[Predicate] = leftFormula.allPredicates ++ rightFormula.allPredicates
  lazy val allPredicatesNames: Set[String]    = leftFormula.allPredicatesNames ++ rightFormula.allPredicatesNames

  lazy val allSubformulae: Set[LTLFormula] = Set(this) ++ leftFormula.allSubformulae ++ rightFormula.allSubformulae

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * True
  */
object LTLTrue extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLTrue

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = LTLTrue

  lazy val simplify: LTLFormula = this

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLFalse

  override def longInfo: String = "T"

  lazy val allPredicates     : Set[Predicate] = Set()
  lazy val allPredicatesNames: Set[String]    = Set()

  lazy val allSubformulae: Set[LTLFormula] = Set(this)

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

/**
  * False
  */
object LTLFalse extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLTrue

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, state: Set[Predicate], last: Boolean): LTLFormula = LTLFalse

  lazy val simplify: LTLFormula = this

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLTrue

  override def longInfo: String = "F"

  lazy val allPredicates     : Set[Predicate] = Set()
  lazy val allPredicatesNames: Set[String]    = Set()

  lazy val allSubformulae: Set[LTLFormula] = Set(this)

  lazy val toPositiveBooleanFormula: PositiveBooleanFormula = PositiveElementary(this)
}

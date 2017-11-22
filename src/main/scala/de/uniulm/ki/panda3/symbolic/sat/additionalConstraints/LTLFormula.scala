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

  def delta(currentTask: Task, last: Boolean): LTLFormula

  def simplify: LTLFormula

  def nnf: LTLFormula

  def negate: LTLFormula
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

  def delta(currentTask: Task, last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = task + arguments.mkString("(", ",", ")")

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)
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
    val matchingPredicates = domain.predicates filter { p => p.name.startsWith("+" + predicate + "[") || p.name.startsWith("-" + predicate + "[") }
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

  def delta(currentTask: Task, last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = predicate + arguments.mkString("(", ",", ")")

  lazy val nnf: LTLFormula = this.copy(predicate = "+" + predicate)

  lazy val negate: LTLFormula = LTLNot(this)
}


case class LTLForall(variable: String, sort : String, subFormula: LTLFormula) extends LTLFormula {
  assert(variable.startsWith("?"))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val values = liftedDomain.sorts.find({_.name == sort}).get.elements map {_.name}
    println("∀ Variable " + variable + ": " + values.mkString(", "))

    LTLAnd(values map { v => subFormula.parseAndGround(domain, liftedDomain, variableContext + ((variable, v))) })
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    if (variable == this.variable) Nil else subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = "∀" + variable + " : " + subFormula.longInfo

  lazy val nnf: LTLFormula = this.copy(subFormula = subFormula.nnf)

  lazy val negate: LTLFormula = LTLExists(variable,sort,subFormula.negate)
}

case class LTLExists(variable: String, sort : String, subFormula: LTLFormula) extends LTLFormula {
  assert(variable.startsWith("?"))

  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = {
    val values = subFormula.valuesForVariable(domain, variable, variableContext)
    println("Variable " + variable + ": " + values.mkString(", "))

    LTLOr(values map { v => subFormula.parseAndGround(domain, liftedDomain, variableContext + ((variable, v))) })
  }

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    if (variable == this.variable) Nil else subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = ???

  override def longInfo: String = "∃ " + variable + " : " + subFormula.longInfo

  lazy val nnf: LTLFormula = this.copy(subFormula = subFormula.nnf)

  lazy val negate: LTLFormula = LTLForall(variable,sort,subFormula.negate)
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

  def delta(currentTask: Task, last: Boolean): LTLFormula = if (task == currentTask) LTLTrue else LTLFalse

  lazy val simplify: LTLFormula = this

  override def longInfo: String = task.name//.split('[').head

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)
}

/**
  * Task Atom
  */
case class PredicateAtom(predicate: Predicate) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = this

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = this

  override def longInfo: String = predicate.name//.split('[').head

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLNot(this)
}


/**
  * Not
  */
case class LTLNot(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLNot(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = subFormula match {
    case TaskAtom(task)           => if (task == currentTask) LTLFalse else LTLTrue
    case PredicateAtom(predicate) => ???
  }

  lazy val simplify: LTLFormula = subFormula.simplify match {
    case LTLTrue   => LTLFalse
    case LTLFalse  => LTLTrue
    case LTLNot(x) => x // double negation
    case x         => LTLNot(x)
  }

  lazy val nnf: LTLFormula = subFormula match {
    case x: TaskAtom          => LTLNot(x)
    case x: TaskNameAtom      => LTLNot(x)
    case x: PredicateAtom     => LTLNot(x)
    case x: PredicateNameAtom => x.copy(predicate = "-" + x.predicate)
    case _                    => subFormula.negate.nnf
  }

  lazy val negate: LTLFormula = subFormula

  override def longInfo: String = "-" + subFormula.longInfo
}

/**
  * And
  */
case class LTLAnd(subFormulae: Seq[LTLFormula]) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLAnd(subFormulae.map(_.parseAndGround(domain, liftedDomain, variableContext)))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    subFormulae flatMap { _.valuesForVariable(domain, variable, variableContext) } distinct

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLAnd(subFormulae map { _.delta(currentTask, last) })

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
}

/**
  * Or
  */
case class LTLOr(subFormulae: Seq[LTLFormula]) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLOr(subFormulae.map(_.parseAndGround(domain, liftedDomain, variableContext)))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    subFormulae flatMap { _.valuesForVariable(domain, variable, variableContext) } distinct

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLOr(subFormulae map { _.delta(currentTask, last) })

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
}

case class LTLImply(left: LTLFormula, right: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLImply(left.parseAndGround(domain, liftedDomain, variableContext), right.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (left.valuesForVariable(domain, variable, variableContext) ++ right.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, last: Boolean): LTLFormula = ??? // not needed

  lazy val simplify: LTLFormula = ???

  lazy val nnf: LTLFormula = LTLOr(LTLNot(left).nnf :: right.nnf :: Nil)

  lazy val negate: LTLFormula = ???

  override def longInfo: String = "(" + left.longInfo + " -> " + right.longInfo + ")"
}


case class LTLEquiv(left: LTLFormula, right: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLEquiv(left.parseAndGround(domain, liftedDomain, variableContext), right.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (left.valuesForVariable(domain, variable, variableContext) ++ right.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, last: Boolean): LTLFormula = ??? // not needed

  lazy val simplify: LTLFormula = ???

  lazy val nnf: LTLFormula = LTLAnd(LTLImply(left, right).nnf :: LTLImply(right, left).nnf :: Nil)

  lazy val negate: LTLFormula = ???

  override def longInfo: String = "(" + left.longInfo + " <-> " + right.longInfo + ")"
}

/**
  * Next
  */
case class LTLNext(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLNext(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = if (last) LTLFalse else subFormula

  lazy val simplify: LTLFormula = LTLNext(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLNext(subFormula.nnf)

  lazy val negate: LTLFormula = LTLWeakNext(subFormula.negate)

  override def longInfo: String = "X " + subFormula.longInfo
}

/**
  * Weak Next
  */
case class LTLWeakNext(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLWeakNext(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = if (last) LTLTrue else subFormula

  lazy val simplify: LTLFormula = LTLWeakNext(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLWeakNext(subFormula.nnf)

  lazy val negate: LTLFormula = LTLNext(subFormula.negate)

  override def longInfo: String = "WX " + subFormula.longInfo
}

/**
  * Always
  */
case class LTLAlways(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLAlways(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLAnd(subFormula.delta(currentTask, last) ::
                                                                     LTLWeakNext(this).delta(currentTask, last) :: Nil)

  lazy val simplify: LTLFormula = LTLAlways(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLAlways(subFormula.nnf)

  lazy val negate: LTLFormula = LTLEventually(subFormula.negate)

  override def longInfo: String = "[] " + subFormula.longInfo
}

/**
  * Eventually
  */
case class LTLEventually(subFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLEventually(subFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = subFormula.valuesForVariable(domain, variable, variableContext)

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLOr(subFormula.delta(currentTask, last) ::
                                                                    LTLNext(this).delta(currentTask, last) :: Nil)

  lazy val simplify: LTLFormula = LTLEventually(subFormula.simplify)

  lazy val nnf: LTLFormula = LTLEventually(subFormula.nnf)

  lazy val negate: LTLFormula = LTLAlways(subFormula.negate)

  override def longInfo: String = "<> " + subFormula.longInfo
}

/**
  * Until
  */
case class LTLUntil(leftFormula: LTLFormula, rightFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLUntil(leftFormula.parseAndGround(domain, liftedDomain, variableContext), rightFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (leftFormula.valuesForVariable(domain, variable, variableContext) ++ rightFormula.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, last: Boolean): LTLFormula =
    LTLOr(rightFormula.delta(currentTask, last) ::
            LTLAnd(leftFormula.delta(currentTask, last) :: LTLNext(this).delta(currentTask, last) :: Nil) :: Nil
         )

  lazy val simplify: LTLFormula = LTLUntil(leftFormula.simplify, rightFormula.simplify)

  lazy val nnf: LTLFormula = LTLUntil(leftFormula.nnf, rightFormula.nnf)

  lazy val negate: LTLFormula = LTLRelease(leftFormula.negate, rightFormula.negate)

  override def longInfo: String = "(" + leftFormula.longInfo + " U " + rightFormula.longInfo + ")"
}

/**
  * Release
  */
case class LTLRelease(leftFormula: LTLFormula, rightFormula: LTLFormula) extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula =
    LTLRelease(leftFormula.parseAndGround(domain, liftedDomain, variableContext), rightFormula.parseAndGround(domain, liftedDomain, variableContext))

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] =
    (leftFormula.valuesForVariable(domain, variable, variableContext) ++ rightFormula.valuesForVariable(domain, variable, variableContext)) distinct

  def delta(currentTask: Task, last: Boolean): LTLFormula =
    LTLAnd(rightFormula.delta(currentTask, last) ::
             LTLAnd(leftFormula.delta(currentTask, last) :: LTLWeakNext(this).delta(currentTask, last) :: Nil) :: Nil
          )


  lazy val simplify: LTLFormula = LTLRelease(leftFormula.simplify, rightFormula.simplify)

  lazy val nnf: LTLFormula = LTLRelease(leftFormula.nnf, rightFormula.nnf)

  lazy val negate: LTLFormula = LTLUntil(leftFormula.negate, rightFormula.negate)

  override def longInfo: String = "(" + leftFormula.longInfo + " R " + rightFormula.longInfo + ")"
}

/**
  * True
  */
object LTLTrue extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLTrue

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLTrue

  lazy val simplify: LTLFormula = this

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLFalse

  override def longInfo: String = "T"
}

/**
  * False
  */
object LTLFalse extends LTLFormula {
  def parseAndGround(domain: Domain, liftedDomain: Domain, variableContext: Map[String, String]): LTLFormula = LTLTrue

  def valuesForVariable(domain: Domain, variable: String, variableContext: Map[String, String]): Seq[String] = Nil

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLFalse

  lazy val simplify: LTLFormula = this

  lazy val nnf: LTLFormula = this

  lazy val negate: LTLFormula = LTLTrue

  override def longInfo: String = "F"
}
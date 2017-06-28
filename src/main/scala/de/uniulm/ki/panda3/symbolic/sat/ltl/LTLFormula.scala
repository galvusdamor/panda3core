package de.uniulm.ki.panda3.symbolic.sat.ltl

import de.uniulm.ki.panda3.symbolic.DefaultLongInfo
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.plan.element.{GroundTask, PlanStep}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
sealed trait LTLFormula extends DefaultLongInfo {
  def parse(domain: Domain): LTLFormula

  def delta(currentTask: Task, last: Boolean): LTLFormula

  def simplify: LTLFormula
}

//case class GroundTaskAtom(groundedTask: GroundTask) extends LTLFormula

/**
  * Task Atom
  */
case class TaskAtom(task: Task) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = this

  def delta(currentTask: Task, last: Boolean): LTLFormula = if (task == currentTask) LTLTrue else LTLFalse

  lazy val simplify: LTLFormula = this

  override def longInfo: String = task.name.split('[').head
}

/**
  * Task Atom as String
  */
case class TaskNameAtom(task: String) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = TaskAtom(domain.tasks.find(_.name == task).get)

  def delta(currentTask: Task, last: Boolean): LTLFormula = ???

  lazy val simplify: LTLFormula = this

  override def longInfo: String = task
}

/**
  * Not
  */
case class LTLNot(subFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLNot(subFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula = subFormula match {
    case TaskAtom(task) => if (task == currentTask) LTLFalse else LTLTrue
  }

  lazy val simplify: LTLFormula = subFormula.simplify match {
    case LTLTrue   => LTLFalse
    case LTLFalse  => LTLTrue
    case LTLNot(x) => x // double negation
    case x         => LTLNot(x)
  }

  override def longInfo: String = "-" + subFormula.longInfo
}

/**
  * And
  */
case class LTLAnd(subFormulae: Seq[LTLFormula]) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLAnd(subFormulae.map(_.parse(domain)))

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

  override def longInfo: String = "(" + subFormulae.map(_.longInfo).mkString(" & ") + ")"
}

/**
  * Or
  */
case class LTLOr(subFormulae: Seq[LTLFormula]) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLOr(subFormulae.map(_.parse(domain)))

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

  override def longInfo: String = "(" + subFormulae.map(_.longInfo).mkString(" v ") + ")"
}

/**
  * Next
  */
case class LTLNext(subFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLNext(subFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula = if (last) LTLFalse else subFormula

  lazy val simplify: LTLFormula = LTLNext(subFormula.simplify)

  override def longInfo: String = "X " + subFormula.longInfo
}

/**
  * Weak Next
  */
case class LTLWeakNext(subFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLWeakNext(subFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula = if (last) LTLTrue else subFormula

  lazy val simplify: LTLFormula = LTLWeakNext(subFormula.simplify)

  override def longInfo: String = "WX " + subFormula.longInfo
}

/**
  * Always
  */
case class LTLAlways(subFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLAlways(subFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLAnd(subFormula.delta(currentTask, last) ::
                                                                     LTLWeakNext(this).delta(currentTask, last) :: Nil)

  lazy val simplify: LTLFormula = LTLAlways(subFormula.simplify)

  override def longInfo: String = "[] " + subFormula.longInfo
}

/**
  * Eventually
  */
case class LTLEventually(subFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLEventually(subFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLOr(subFormula.delta(currentTask, last) ::
                                                                    LTLNext(this).delta(currentTask, last) :: Nil)

  lazy val simplify: LTLFormula = LTLEventually(subFormula.simplify)

  override def longInfo: String = "<> " + subFormula.longInfo
}

/**
  * Until
  */
case class LTLUntil(leftFormula: LTLFormula, rightFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLUntil(leftFormula.parse(domain), rightFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula =
    LTLOr(rightFormula.delta(currentTask, last) ::
            LTLAnd(leftFormula.delta(currentTask, last) :: LTLNext(this).delta(currentTask, last) :: Nil) :: Nil
         )

  lazy val simplify: LTLFormula = LTLUntil(leftFormula.simplify, rightFormula.simplify)

  override def longInfo: String = "(" + leftFormula.longInfo + " U " + rightFormula.longInfo + ")"
}

/**
  * Release
  */
case class LTLRelease(leftFormula: LTLFormula, rightFormula: LTLFormula) extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLRelease(leftFormula.parse(domain), rightFormula.parse(domain))

  def delta(currentTask: Task, last: Boolean): LTLFormula =
    LTLAnd(rightFormula.delta(currentTask, last) ::
             LTLAnd(leftFormula.delta(currentTask, last) :: LTLWeakNext(this).delta(currentTask, last) :: Nil) :: Nil
          )


  lazy val simplify: LTLFormula = LTLRelease(leftFormula.simplify, rightFormula.simplify)

  override def longInfo: String = "(" + leftFormula.longInfo + " R " + rightFormula.longInfo + ")"
}

/**
  * True
  */
object LTLTrue extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLTrue

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLTrue

  lazy val simplify: LTLFormula = this

  override def longInfo: String = "T"
}

/**
  * False
  */
object LTLFalse extends LTLFormula {
  def parse(domain: Domain): LTLFormula = LTLTrue

  def delta(currentTask: Task, last: Boolean): LTLFormula = LTLFalse

  lazy val simplify: LTLFormula = this

  override def longInfo: String = "F"
}
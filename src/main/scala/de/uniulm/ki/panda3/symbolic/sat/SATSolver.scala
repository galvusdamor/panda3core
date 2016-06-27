package de.uniulm.ki.panda3.symbolic.sat

import de.uniulm.ki.panda3.efficient.domain.EfficientDomain
import de.uniulm.ki.panda3.efficient.plan.EfficientPlan
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.plan.Plan
import org.sat4j.core.VecInt
import org.sat4j.minisat.SolverFactory
import org.sat4j.specs.{ContradictionException, ISolver}
import org.sat4j.tools.ModelIterator

import scala.collection.mutable

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
trait StatefulSATSolver {
  def addClause(literals: Seq[(String, Boolean)])

  def addAtLeast(literals: Seq[String], num: Int)

  def addAtMost(literals: Seq[String], num: Int)

  def addExactly(literals: Seq[String], num: Int)

  def addImplyAllRight(left: Seq[String], right: Seq[String])

  def isSatisfiable(): Boolean

  def getModel(): Option[Seq[String]]
}


class SAT4JStatefulSolver extends StatefulSATSolver {
  protected val solv               : ISolver                                   = new ModelIterator(SolverFactory.newDefault())
  private   val atomMap            : scala.collection.mutable.Map[String, Int] = new mutable.HashMap[String, Int]()
  private   val atomBackMap        : scala.collection.mutable.Map[Int, String] = new mutable.HashMap[Int, String]()
  private   var contradictoryClause: Boolean                                   = false

  private def getAtomID(atom: String): Int = atomMap.get(atom) match {
    case Some(id) => id
    case None     =>
      val id = atomMap.size + 1
      solv.newVar(id) // add a variable to the solver
      atomMap(atom) = id
      atomBackMap(id) = atom
      id
  }

  private def getAtomID(atom: String, isPositive: Boolean): Int = if (isPositive) getAtomID(atom) else -getAtomID(atom)

  private def getAtomID(atom: (String, Boolean)): Int = if (atom._2) getAtomID(atom._1) else -getAtomID(atom._1)


  override def addClause(literals: Seq[(String, Boolean)]): Unit = try {
    solv.addClause(new VecInt(literals.toArray map getAtomID))
  } catch {case e: ContradictionException => contradictoryClause = true}

  override def addAtLeast(literals: Seq[String], num: Int): Unit = try {
    solv.addAtLeast(new VecInt(literals.toArray map getAtomID), num)
  } catch {case e: ContradictionException => contradictoryClause = true}

  override def addAtMost(literals: Seq[String], num: Int): Unit = try {
    solv.addAtMost(new VecInt(literals.toArray map getAtomID), num)
  } catch {case e: ContradictionException => contradictoryClause = true}

  override def addExactly(literals: Seq[String], num: Int): Unit = try {
    solv.addExactly(new VecInt(literals.toArray map getAtomID), num)
  } catch {case e: ContradictionException => contradictoryClause = true}

  override def addImplyAllRight(left: Seq[String], right: Seq[String]): Unit = try {
    right foreach { consequence =>
      solv.addClause(new VecInt(((left map { getAtomID(_, isPositive = false) }) :+ getAtomID(consequence)).toArray))
    }
  } catch {case e: ContradictionException => contradictoryClause = true}


  override def isSatisfiable(): Boolean = !contradictoryClause && solv.isSatisfiable

  def getModel(): Option[Seq[String]] = if (contradictoryClause || !isSatisfiable()) None
  else Some(solv.model() collect { case x if x > 0 => atomBackMap(x) })
}

trait Clause

case class SimpleClause(literals: Seq[(String, Boolean)]) extends Clause

case class AtLeastClause(literals: Seq[String], num: Int) extends Clause

case class AtMostClause(literals: Seq[String], num: Int) extends Clause

case class ExactlyClause(literals: Seq[String], num: Int) extends Clause

case class ImplyAllRightClause(left: Seq[String], right: Seq[String]) extends Clause

case class StatelessSATSolver[SOLVERTYPE <: StatefulSATSolver](clauses: Seq[Clause], statefulSolver: SOLVERTYPE) {
  clauses foreach {
    case SimpleClause(lits)               => statefulSolver.addClause(lits)
    case AtLeastClause(lits, num)         => statefulSolver.addAtLeast(lits, num)
    case AtMostClause(lits, num)          => statefulSolver.addAtLeast(lits, num)
    case ExactlyClause(lits, num)         => statefulSolver.addAtLeast(lits, num)
    case ImplyAllRightClause(left, right) => statefulSolver.addImplyAllRight(left, right)
  }
}


object SAT4JSAT {
  def main(args: Array[String]) {
    val sat = new SAT4JStatefulSolver
    sat.addClause(("aa", true) ::("bb", true) :: Nil)
    sat.addClause(("cc", true) ::("dd", true) :: Nil)
    sat.addClause(("cc", true) :: Nil)
    sat.addClause(("cc", false) ::("dd", true) :: Nil)
    //sat.addClause(("cc", false) ::("dd", false) :: Nil)
    println(sat.getModel())
  }
}
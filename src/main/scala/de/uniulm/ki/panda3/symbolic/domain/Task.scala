package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.updates.{ReduceTasks, DomainUpdate, ExchangeTask}
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.util.HashMemo

/**
  * Tasks are blue-prints for actions, actually contained in plans, i.e. they describe which variables a [[de.uniulm.ki.panda3.symbolic.plan.element.PlanStep]] of their type must have and
  * which
  * preconditions and effects it has.
  *
  * Additionally Tasks can either be primitive or abstract. The first kind can be executed directly, while the latter must be decomposed further during the planning process.
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
// TODO: check, whether the parameter constraints of a task schema are always observed correctly
trait Task extends DomainUpdatable with PrettyPrintable {
  val name                : String
  val isPrimitive         : Boolean
  val parameters          : Seq[Variable]
  val parameterConstraints: Seq[VariableConstraint]
  val precondition        : Formula
  val effect              : Formula

  lazy val taskCSP: CSP = CSP(parameters.toSet, parameterConstraints)

  def substitute(literal: Literal, newParameter: Seq[Variable]): Literal = {
    val sub = PartialSubstitution(parameters, newParameter)
    Literal(literal.predicate, literal.isPositive, literal.parameterVariables map sub)
  }


  override def update(domainUpdate: DomainUpdate): Task = domainUpdate match {
    case ExchangeTask(map) => if (map.contains(this)) map(this) else this
    case ReduceTasks()     =>
      val wrappedPrecondition: Formula = precondition match {case l: Literal => And[Literal](l :: Nil); case x => x}
      val wrappedEffect: Formula = effect match {case l: Literal => And[Literal](l :: Nil); case x => x}
      (wrappedPrecondition, wrappedEffect) match {
        // the test for And[Literal] is not possible due to java's type erasure
        case (precAnd: And[Formula], effAnd: And[Formula]) =>
          if ((precAnd.conjuncts forall { case l: Literal => true case _ => false }) && (effAnd.conjuncts forall { case l: Literal => true case _ => false })) {
            ReducedTask(name, isPrimitive = isPrimitive, parameters, parameterConstraints, precAnd.asInstanceOf[And[Literal]], effAnd.asInstanceOf[And[Literal]])
          } else {
            this
          }
        case _                                             => this
      }

    case _ =>
      val newPrecondition = precondition.update(domainUpdate)
      val newEffect = effect.update(domainUpdate)
      (newPrecondition, newEffect) match {
        // the type parameter will be erased by the compiler, so we have to check it again
        case (pre: And[Literal], eff: And[Literal]) if pre.containsOnlyLiterals && eff.containsOnlyLiterals =>
          ReducedTask(name, isPrimitive, parameters map { _.update(domainUpdate) }, parameterConstraints map { _.update(domainUpdate) }, pre.asInstanceOf[And[Literal]],
                      eff.asInstanceOf[And[Literal]])
        case _                                                                                              =>
          GeneralTask(name, isPrimitive, parameters map { _.update(domainUpdate) }, parameterConstraints map { _.update(domainUpdate) }, newPrecondition, newEffect)
      }
  }

  lazy val instantiateGround: Seq[GroundTask] = {
    val allInstantiations = Sort.allPossibleInstantiations(parameters map { _.sort })
    // check all constraints
    val allValidInstantiations = allInstantiations filter { params =>
      def constForVar(v: Variable): Constant = params(parameters indexOf v)
      parameterConstraints forall {
        case Equal(v1, c: Constant)     => constForVar(v1) == c
        case Equal(v1, v2: Variable)    => constForVar(v2) == constForVar(v2)
        case NotEqual(v1, c: Constant)  => constForVar(v1) != c
        case NotEqual(v1, v2: Variable) => constForVar(v2) != constForVar(v2)
        case OfSort(v, s)               => s.elements contains constForVar(v)
        case NotOfSort(v, s)            => !(s.elements contains constForVar(v))
      }
    }
    allInstantiations map { params => GroundTask(this, params) }
  }

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + (parameters map { _.shortInfo }).mkString("(", ", ", ")")

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo + "\npreconditions:\n" + precondition.shortInfo + "\n" +
    "\neffects:\n" + effect.shortInfo + "\n"

  val preconditionsAsPredicateBool: Seq[(Predicate, Boolean)]
  val effectsAsPredicateBool     : Seq[(Predicate, Boolean)]
}

case class GeneralTask(name: String, isPrimitive: Boolean, parameters: Seq[Variable], parameterConstraints: Seq[VariableConstraint], precondition: Formula, effect: Formula)
  extends Task with HashMemo {

  override lazy val preconditionsAsPredicateBool: Seq[(Predicate, Boolean)] = noSupport(FORUMLASNOTSUPPORTED)
  override lazy val effectsAsPredicateBool     : Seq[(Predicate, Boolean)] = noSupport(FORUMLASNOTSUPPORTED)
}

case class ReducedTask(name: String, isPrimitive: Boolean, parameters: Seq[Variable], parameterConstraints: Seq[VariableConstraint], precondition: And[Literal], effect: And[Literal])
  extends Task with HashMemo {
  /*if (!((precondition.conjuncts ++ effect.conjuncts) forall { l => l.parameterVariables forall parameters.contains })){
    (precondition.conjuncts ++ effect.conjuncts) foreach {l => l.parameterVariables foreach { v =>
      println("VARIABLE " + v)
      println("CONTAINS " + parameters contains v)
    }
    }
  }*/
  assert((precondition.conjuncts ++ effect.conjuncts) forall { l => l.parameterVariables forall parameters.contains })

  lazy val preconditionsAsPredicateBool: Seq[(Predicate, Boolean)] = (precondition.conjuncts map { case Literal(p, isP, _) => (p, isP) }).distinct
  lazy val effectsAsPredicateBool     : Seq[(Predicate, Boolean)] = (effect.conjuncts map { case Literal(p, isP, _) => (p, isP) }).distinct
}
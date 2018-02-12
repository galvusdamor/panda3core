package de.uniulm.ki.panda3.symbolic.domain

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.PrettyPrintable
import de.uniulm.ki.panda3.symbolic.csp._
import de.uniulm.ki.panda3.symbolic.domain.updates._
import de.uniulm.ki.panda3.symbolic.logic._
import de.uniulm.ki.panda3.symbolic.plan.element.GroundTask
import de.uniulm.ki.util.{Internable, HashMemo}

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
trait Task extends DomainUpdatable with PrettyPrintable with Ordered[Task] {
  val name                                     : String
  val isPrimitive                              : Boolean
  val parameters                               : Seq[Variable]
  val artificialParametersRepresentingConstants: Seq[Variable]
  val parameterConstraints                     : Seq[VariableConstraint]
  val precondition                             : Formula
  val effect                                   : Formula

  lazy val isAbstract: Boolean = !isPrimitive
  lazy val taskCSP   : CSP     = CSP(parameters.toSet, parameterConstraints)

  def substitute(literal: Literal, sub: PartialSubstitution[Variable]): Literal = Literal(literal.predicate, literal.isPositive, literal.parameterVariables map sub)

  override def compare(that: Task): Int = this.name compare that.name

  override def update(domainUpdate: DomainUpdate): Task = domainUpdate match {
    case ExchangeTask(map)                                => if (map.contains(this)) map(this) else this
    case ReduceTasks()                                    =>
      val wrappedPrecondition: Formula = precondition match {case l: Literal => And[Literal](l :: Nil); case x => x}
      val wrappedEffect: Formula = effect match {case l: Literal => And[Literal](l :: Nil); case x => x}
      (wrappedPrecondition, wrappedEffect) match {
        // the test for And[Literal] is not possible due to java's type erasure
        case (precAnd: And[Formula], effAnd: And[Formula]) =>
          if ((precAnd.conjuncts forall { case l: Literal => true case _ => false }) && (effAnd.conjuncts forall { case l: Literal => true case _ => false })) {
            ReducedTask.intern((name, isPrimitive, parameters, artificialParametersRepresentingConstants, parameterConstraints, precAnd.asInstanceOf[And[Literal]],
                                 effAnd.asInstanceOf[And[Literal]]))
          } else {
            this
          }
        case _                                             => this
      }
    case ExchangeLiteralsByPredicate(exchangeMap, invert) =>
      this match {
        case ReducedTask(_, _, _, _, _, preconditionAnd, effectAnd) =>
          val newPositivePrecondition = preconditionAnd.conjuncts map { _ update domainUpdate }
          val newNegativePrecondition = preconditionAnd.conjuncts map { _.negate update domainUpdate } map { _.negate }
          val newPositiveEffects = effectAnd.conjuncts map { _ update domainUpdate }
          val newNegativeEffects = effectAnd.conjuncts map { _.negate update domainUpdate } map { _.negate }

          val newPrecondition = newPositivePrecondition ++ (if (invert) newNegativePrecondition else Nil)
          val newEffects = newPositiveEffects ++ (if (invert) Nil else newNegativeEffects)

          assert(isPrimitive || !invert)

          if (!invert) {
            assert(newEffects.length == 2 * effectAnd.conjuncts.length)
            assert(newPrecondition.length == preconditionAnd.conjuncts.length)
          } else {
            assert(newEffects.length == effectAnd.conjuncts.length)
            assert(newPrecondition.length == 2 * preconditionAnd.conjuncts.length)
          }

          ReducedTask.intern(name, isPrimitive, parameters, artificialParametersRepresentingConstants, parameterConstraints, And(newPrecondition), And(newEffects))
        case _                                                      => noSupport(FORUMLASNOTSUPPORTED)
      }
    case RemoveEffects(unnecessaryPredicates, isInverted) =>
      this match {
        case ReducedTask(_, _, _, _, _, preconditionAnd, effectAnd) =>
          val newEffects = effectAnd.conjuncts filterNot { case Literal(predicate, isPositive, _) => unnecessaryPredicates contains ((predicate, isPositive)) }
          val newPreconditions = preconditionAnd.conjuncts filterNot { case Literal(predicate, isPositive, _) => unnecessaryPredicates contains ((predicate, isPositive)) }

          if (!isInverted) ReducedTask.intern(name, isPrimitive, parameters, artificialParametersRepresentingConstants, parameterConstraints, preconditionAnd, And(newEffects))
          else ReducedTask.intern(name, isPrimitive, parameters, artificialParametersRepresentingConstants, parameterConstraints, And(newPreconditions), effectAnd)

        case _ => noSupport(FORUMLASNOTSUPPORTED)
      }
    case RemovePredicate(unnecessaryPredicates)           =>
      this match {
        case ReducedTask(_, _, _, _, _, preconditionAnd, effectAnd) =>
          val newEffects = effectAnd.conjuncts filterNot { case Literal(predicate, _, _) => unnecessaryPredicates contains predicate }
          val newPreconditions = preconditionAnd.conjuncts filterNot { case Literal(predicate, _, _) => unnecessaryPredicates contains predicate }

          ReducedTask(name, isPrimitive, parameters, artificialParametersRepresentingConstants, parameterConstraints, And(newPreconditions), And(newEffects))

        case _ => noSupport(FORUMLASNOTSUPPORTED)
      }

    case _ =>
      val newPrecondition = precondition.update(domainUpdate)
      val newEffect = effect.update(domainUpdate)
      (newPrecondition, newEffect) match {
        // the type parameter will be erased by the compiler, so we have to check it again
        case (pre: And[Literal], eff: And[Literal]) if pre.containsOnlyLiterals && eff.containsOnlyLiterals &&
          (!domainUpdate.isInstanceOf[SetExpandVariableConstraintsInPlans] || this.isInstanceOf[ReducedTask]) =>
          ReducedTask.intern(name, isPrimitive, parameters map { _.update(domainUpdate) }, artificialParametersRepresentingConstants map { _.update(domainUpdate) },
                             parameterConstraints map { _.update(domainUpdate) }, pre.asInstanceOf[And[Literal]],
                             eff.asInstanceOf[And[Literal]])
        case _                                                                                                =>
          GeneralTask(name, isPrimitive, parameters map { _.update(domainUpdate) }, artificialParametersRepresentingConstants map { _.update(domainUpdate) },
                      parameterConstraints map { _.update(domainUpdate) }, newPrecondition, newEffect)
      }
  }

  lazy val instantiateGround: Seq[GroundTask] = {
    val allInstantiations = Sort.allPossibleInstantiations(parameters map { _.sort })
    // check all constraints
    val allValidInstantiations = allInstantiations filter { params =>
      def constForVar(v: Variable): Constant = params(parameters indexOf v)

      parameterConstraints forall {
        case Equal(v1, c: Constant)     => constForVar(v1) == c
        case Equal(v1, v2: Variable)    => constForVar(v1) == constForVar(v2)
        case NotEqual(v1, c: Constant)  => constForVar(v1) != c
        case NotEqual(v1, v2: Variable) => constForVar(v1) != constForVar(v2)
        case OfSort(v, s)               => s.elements contains constForVar(v)
        case NotOfSort(v, s)            => !(s.elements contains constForVar(v))
      }
    }
    allValidInstantiations map { params => GroundTask(this, params) }
  }

  /** returns a short information about the object */
  override def shortInfo: String = name

  /** returns a string that can be utilized to define the object */
  override def mediumInfo: String = shortInfo + ((parameters filterNot artificialParametersRepresentingConstants.contains) map { _.shortInfo }).mkString("(", ", ", ")")

  /** returns a more detailed information about the object */
  override def longInfo: String = mediumInfo + "\npreconditions:\n" + precondition.shortInfo + "\n" +
    "\neffects:\n" + effect.shortInfo + "\n"

  val preconditionsAsPredicateBool: Seq[(Predicate, Boolean)]
  val effectsAsPredicateBool      : Seq[(Predicate, Boolean)]
  lazy val addEffectsAsPredicate         = effectsAsPredicateBool collect { case (p, true) => p }
  lazy val addEffectsAsPredicateSet      = addEffectsAsPredicate toSet
  lazy val delEffectsAsPredicate         = effectsAsPredicateBool collect { case (p, false) => p }
  lazy val delEffectsAsPredicateSet      = delEffectsAsPredicate toSet
  lazy val posPreconditionAsPredicate    = preconditionsAsPredicateBool collect { case (p, true) => p }
  lazy val posPreconditionAsPredicateSet = posPreconditionAsPredicate.toSet

  def areParametersAllowed(parameterConstants: Seq[Constant]): Boolean = parameterConstraints forall {
    case Equal(var1, var2: Variable)     => parameterConstants(parameters indexOf var1) == parameterConstants(parameters indexOf var2)
    case Equal(vari, const: Constant)    => parameterConstants(parameters indexOf vari) == const
    case NotEqual(var1, var2: Variable)  => parameterConstants(parameters indexOf var1) != parameterConstants(parameters indexOf var2)
    case NotEqual(vari, const: Constant) => parameterConstants(parameters indexOf vari) != const
    case OfSort(vari, sort)              => sort.elements contains parameterConstants(parameters indexOf vari)
    case NotOfSort(vari, sort)           => !(sort.elements contains parameterConstants(parameters indexOf vari))
  }

  def arePartialParametersAllowed(partialParameterConstants: Seq[Constant]): Boolean =
    parameterConstraints filter { _.getVariables forall { v => (parameters indexOf v) < partialParameterConstants.length } } forall {
      case Equal(var1, var2: Variable)     => partialParameterConstants(parameters indexOf var1) == partialParameterConstants(parameters indexOf var2)
      case Equal(vari, const: Constant)    => partialParameterConstants(parameters indexOf vari) == const
      case NotEqual(var1, var2: Variable)  => partialParameterConstants(parameters indexOf var1) != partialParameterConstants(parameters indexOf var2)
      case NotEqual(vari, const: Constant) => partialParameterConstants(parameters indexOf vari) != const
      case OfSort(vari, sort)              => sort.elements contains partialParameterConstants(parameters indexOf vari)
      case NotOfSort(vari, sort)           => !(sort.elements contains partialParameterConstants(parameters indexOf vari))
    }

}

case class GeneralTask(name: String, isPrimitive: Boolean, parameters: Seq[Variable], artificialParametersRepresentingConstants: Seq[Variable],
                       parameterConstraints: Seq[VariableConstraint], precondition: Formula, effect: Formula) extends Task with HashMemo {

  assert(artificialParametersRepresentingConstants forall parameters.contains)
  override lazy val preconditionsAsPredicateBool: Seq[(Predicate, Boolean)] = noSupport(FORUMLASNOTSUPPORTED)
  override lazy val effectsAsPredicateBool      : Seq[(Predicate, Boolean)] = noSupport(FORUMLASNOTSUPPORTED)

}

case class ReducedTask(name: String, isPrimitive: Boolean, parameters: Seq[Variable], artificialParametersRepresentingConstants: Seq[Variable],
                       parameterConstraints: Seq[VariableConstraint], precondition: And[Literal], effect: And[Literal]) extends Task with HashMemo {
  /*if (!((precondition.conjuncts ++ effect.conjuncts) forall { l => l.parameterVariables forall parameters.contains })){
    (precondition.conjuncts ++ effect.conjuncts) foreach {l => l.parameterVariables foreach { v =>
      println("VARIABLE " + v)
      println("CONTAINS " + parameters contains v)
    }
    }
  }*/
  assert(artificialParametersRepresentingConstants forall parameters.contains)
  assert((precondition.conjuncts ++ effect.conjuncts) forall { l => l.parameterVariables forall parameters.contains })
  assert(parameters.distinct.size == parameters.size)

  if (parameters.isEmpty) {
    // if ground, don't have something both in the add and del effects!
    effectsAsPredicateBool filterNot { _._2 } foreach { case (p, false) =>
      assert(!effectsAsPredicateBoolSet.contains((p, true)))
    }
  }

  lazy val preconditionsAsPredicateBool: Seq[(Predicate, Boolean)] = (precondition.conjuncts map { case Literal(p, isP, _) => (p, isP) }).distinct
  lazy val effectsAsPredicateBool      : Seq[(Predicate, Boolean)] = (effect.conjuncts map { case Literal(p, isP, _) => (p, isP) }).distinct
  lazy val effectsAsPredicateBoolSet   : Set[(Predicate, Boolean)] = effectsAsPredicateBool.toSet

  override def equals(o: scala.Any): Boolean =
    if (o.isInstanceOf[ReducedTask] && this.hashCode == o.hashCode()) {productIterator.sameElements(o.asInstanceOf[ReducedTask].productIterator) } else false
}

object ReducedTask extends Internable[(String, Boolean, Seq[Variable], Seq[Variable], Seq[VariableConstraint], And[Literal], And[Literal]), ReducedTask] {
  override protected val applyTuple = (ReducedTask.apply _).tupled
}
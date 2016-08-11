package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.compiler.{ExpandSortHierarchy, ClosedWorldAssumption, SHOPMethodCompiler, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.util._

import scala.collection._
import scala.io.Source

/**
  * This works only if grounded ....
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off number.of.methods
case class VerifyEncoding(domain: Domain, initialPlan: Plan, taskSequence: Seq[Task])(val K: Int = VerifyEncoding.computeTheoreticalK(domain, initialPlan, taskSequence)) {

  domain.tasks foreach { t => assert(t.parameters.isEmpty) }
  domain.predicates foreach { p => assert(p.argumentSorts.isEmpty) }
  //assert(initialPlan.init.substitutedEffects.length == domain.predicates.length)

  val DELTA = domain.maximumMethodSize
  lazy val numberOfLayers          = K
  lazy val numberOfActionsPerLayer = taskSequence.length


  private val taskIndices          : Map[Task, Int]                = domain.tasks.zipWithIndex.toMap.withDefaultValue(-1)
  // TODO hack!!
  private val predicateIndices     : Map[Predicate, Int]           = domain.predicates.zipWithIndex.toMap
  private val methodIndices        : Map[DecompositionMethod, Int] = domain.decompositionMethods.zipWithIndex.toMap
  private val methodPlanStepIndices: Map[Plan, Map[PlanStep, Int]] = (domain.decompositionMethods map { _.subPlan } map { plan =>
    (plan, plan.planStepsWithoutInitGoal.zipWithIndex.toMap)
  }).toMap

  def taskIndex(task: Task): Int = taskIndices(task)


  // ATOMS
  private def methodIndex(method: DecompositionMethod): Int = methodIndices(method)

  def predicateIndex(predicate: Predicate): Int = predicateIndices(predicate)

  def action(layer: Int, position: Int, task: Task): String = "action^" + layer + "_" + position + "," + taskIndex(task)

  private def actionUsed(layer: Int, position: Int): String = "actionUsed^" + layer + "_" + position

  private def actionAbstract(layer: Int, position: Int): String = "actionAbstract^" + layer + "_" + position

  def childWithIndex(layer: Int, position: Int, father: Int, indexOnMethod: Int): String = "child^" + layer + "_" + position + "," + father + "," + indexOnMethod

  private def childOf(layer: Int, position: Int, father: Int): String = "childof^" + layer + "_" + position + "," + father

  private def before(layer: Int, beforeIndex: Int, afterIndex: Int): String = "before^" + layer + "," + beforeIndex + "," + afterIndex

  private def method(layer: Int, position: Int, method: DecompositionMethod): String = "method^" + layer + "_" + position + "," + methodIndex(method)

  private def statePredicate(layer: Int, position: Int, predicate: Predicate): String = "predicate^" + layer + "_" + position + "," + predicateIndex(predicate)


  // LOGICALS ABBREVIATIONS
  private def atLeastOneOf(atoms: Seq[String]): Clause = Clause(atoms map { (_, true) })

  private def atMostOneOf(atoms: Seq[String]): Seq[Clause] = for (i <- atoms.indices; j <- Range(i + 1, atoms.length)) yield Clause((atoms(i), false) ::(atoms(j), false) :: Nil)

  private def exactlyOneOf(atoms: Seq[String]): Seq[Clause] = atMostOneOf(atoms) :+ atLeastOneOf(atoms)

  private def impliesNot(left: String, right: String): Clause = Clause((left, false) ::(right, false) :: Nil)

  private def impliesTrueAntNotToNot(leftTrue: String, leftFalse: String, right: String): Seq[Clause] = Clause((leftTrue, false) ::(leftFalse, true) ::(right, false) :: Nil) :: Nil

  private def impliesAllNot(left: String, right: Seq[String]): Seq[Clause] = right map { impliesNot(left, _) }

  private def impliesSingle(left: String, right: String): Clause = Clause((left, false) ::(right, true) :: Nil)


  private def impliesRightAnd(leftConjunct: Seq[String], rightConjunct: Seq[String]): Seq[Clause] = {
    val negLeft = leftConjunct map { (_, false) }
    rightConjunct map { r => Clause(negLeft :+(r, true)) }
  }

  private def impliesRightOr(leftConjunct: Seq[String], rightConjunct: Seq[String]): Clause = {
    val negLeft = leftConjunct map { (_, false) }
    Clause(negLeft ++ (rightConjunct map { x => (x, true) }))
  }

  private def allImply(left: Seq[String], target: String): Seq[Clause] = left flatMap { x => impliesRightAnd(x :: Nil, target :: Nil) }


  // FORMULA STRUCTURE

  private def noActionForLayerFrom(layer: Int, firstNoAction: Int, numberOfInstances: Int): Seq[Clause] = Range(firstNoAction, numberOfInstances) flatMap { pos =>
    val actionAtoms: Seq[String] = domain.tasks map { task => action(layer, pos, task) }
    (actionAtoms map { at => Clause((at, false) :: Nil) }) :+ Clause((actionUsed(layer, pos), false) :: Nil) :+ Clause((actionAbstract(layer, pos), false) :: Nil)
  }


  private def selectActionsForLayer(layer: Int, position: Int): Seq[Clause] = {
    val actionAtoms: Seq[String] = domain.tasks map { task => action(layer, position, task) }
    val abstractActions: Seq[String] = domain.abstractTasks map { task => action(layer, position, task) }
    atMostOneOf(actionAtoms) ++ allImply(actionAtoms, actionUsed(layer, position)) ++ allImply(abstractActions, actionAbstract(layer, position)) :+
      impliesRightOr(actionAbstract(layer, position) :: Nil, abstractActions) :+ impliesRightOr(actionUsed(layer, position) :: Nil, actionAtoms)
  }


  private def transitiveOrderForLayer(layer: Int): Seq[Clause] =
    (for (i <- Range(0, numberOfActionsPerLayer); j <- Range(0, numberOfActionsPerLayer) if i != j; k <- Range(0, numberOfActionsPerLayer) if i != k && j != k) yield
      impliesRightAnd(before(layer, i, j) :: before(layer, j, k) :: Nil, before(layer, i, k) :: Nil)).flatten

  private def consistentOrderForLayer(layer: Int): Seq[Clause] =
    for (i <- Range(0, numberOfActionsPerLayer); j <- Range(0, numberOfActionsPerLayer) if i != j) yield impliesNot(before(layer, i, j), before(layer, j, i))

  // the method applied _to_ the layer
  private def applyMethod(layer: Int, position: Int): Seq[Clause] = {
    val methodRestrictsAT = domain.decompositionMethods map { decompositionMethod =>
      impliesSingle(method(layer, position, decompositionMethod), action(layer, position, decompositionMethod.abstractTask))
    }
    val methodMustBeApplied = impliesRightOr(actionAbstract(layer, position) :: Nil, domain.decompositionMethods map { m => method(layer, position, m) })

    methodRestrictsAT :+ methodMustBeApplied
  }

  private def notTwoMethods(layer: Int, position: Int): Seq[Clause] = atMostOneOf(domain.decompositionMethods map { method(layer, position, _) })

  private def childImpliesChildOf(layer: Int, position: Int): Seq[Clause] = {
    Range(0, numberOfActionsPerLayer) flatMap { father =>
      val childrenPredicates = Range(0, DELTA) map { childWithIndex(layer, position, father, _) }

      (childrenPredicates map { child => impliesSingle(child, childOf(layer, position, father)) }) :+
        impliesRightOr(childOf(layer, position, father) :: Nil, childrenPredicates)
    }
  }


  private def mustBeChildOf(layer: Int, position: Int): Seq[Clause] = {
    val fathers = Range(0, numberOfActionsPerLayer) flatMap {
      father => Range(0, DELTA) map {
        mPos => childWithIndex(layer, position, father, mPos)
      }
    }
    val children: Seq[Seq[String]] = Range(0, DELTA) map {
      mPos => Range(0, numberOfActionsPerLayer) map {
        childPos => childWithIndex(layer, childPos, position, mPos)
      }
    }

    (children flatMap atMostOneOf) ++ atMostOneOf(fathers) :+ impliesRightOr(actionUsed(layer, position) :: Nil, fathers)
  }


  private def fatherMustExist(layer: Int, position: Int): Seq[Clause] = {
    Range(0, numberOfActionsPerLayer) flatMap {
      father =>
        Range(0, DELTA) flatMap {
          mPos =>
            val mustHaveAnyFather = impliesRightAnd(childWithIndex(layer, position, father, mPos) :: Nil, actionUsed(layer - 1, father) :: Nil)
            val ifNotFirstFatherMustBeAbstract = if (mPos != 0 || father != position)
              impliesSingle(childWithIndex(layer, position, father, mPos), actionAbstract(layer - 1, father)) :: Nil
            else Nil

            mustHaveAnyFather ++ ifNotFirstFatherMustBeAbstract
        }
    }
  }

  private def methodMustHaveChildren(layer: Int, fatherPosition: Int): Seq[Clause] = {
    domain.decompositionMethods flatMap {
      case m@SimpleDecompositionMethod(_, subPlan, _) =>
        // those selected
        val presentChildren: Seq[Clause] = subPlan.planStepsWithoutInitGoal.zipWithIndex flatMap {
          case (ps, childNumber) =>
            val mustChildren: Clause = impliesRightOr(method(layer, fatherPosition, m) :: Nil,
                                                      Range(0, numberOfActionsPerLayer) map { childPos => childWithIndex(layer + 1, childPos, fatherPosition, childNumber) })
            // types of the children
            val childrenType: Seq[Clause] = Range(0, numberOfActionsPerLayer) flatMap {
              childPos =>
                impliesRightAnd(childWithIndex(layer + 1, childPos, fatherPosition, childNumber) :: method(layer, fatherPosition, m) :: Nil, action(layer + 1, childPos, ps.schema) :: Nil)
            }
            childrenType :+ mustChildren
        }

        // order of the children
        val minimalOrdering = subPlan.orderingConstraints.minimalOrderingConstraints() filterNot {
          _.containsAny(m.subPlan.initAndGoal: _*)
        }
        val childrenOrder: Seq[Clause] = minimalOrdering flatMap {
          case OrderingConstraint(beforePS, afterPS) =>
            val beforePos: Int = methodPlanStepIndices(subPlan)(beforePS) //subPlan.planStepsWithoutInitGoal indexOf beforePS
          val afterPos: Int = methodPlanStepIndices(subPlan)(afterPS) // subPlan.planStepsWithoutInitGoal indexOf afterPS
            Range(0, numberOfActionsPerLayer) flatMap {
              childBeforePos => Range(0, numberOfActionsPerLayer) flatMap {
                childAfterPos =>
                  impliesRightAnd(method(layer, fatherPosition, m) :: childWithIndex(layer + 1, childBeforePos, fatherPosition, beforePos) ::
                                    childWithIndex(layer + 1, childAfterPos, fatherPosition, afterPos) :: Nil, before(layer + 1, childBeforePos, childAfterPos) :: Nil)
              }
            }
        }
        val nonPresentChildren: Seq[Clause] = Range(subPlan.planStepsWithoutInitGoal.length, DELTA) flatMap {
          childNumber =>
            impliesAllNot(method(layer, fatherPosition, m), Range(0, numberOfActionsPerLayer) map {
              childPos => childWithIndex(layer + 1, childPos, fatherPosition, childNumber)
            })
        }
        presentChildren ++ nonPresentChildren ++ childrenOrder
    }
  }

  private def maintainPrimitive(layer: Int, position: Int): Seq[Clause] =
    domain.primitiveTasks flatMap {
      task => impliesSingle(action(layer - 1, position, task), action(layer, position, task)) ::
        impliesSingle(action(layer - 1, position, task), childWithIndex(layer, position, position, 0)) :: Nil
    }

  private def maintainOrdering(layer: Int, parentBeforePos: Int): Seq[Clause] =
    Range(0, numberOfActionsPerLayer) flatMap {
      parentAfterPos =>
        Range(0, numberOfActionsPerLayer) flatMap {
          childBeforePos => Range(0, numberOfActionsPerLayer) flatMap {
            childAfterPos =>
              impliesRightAnd(childOf(layer, childBeforePos, parentBeforePos) :: childOf(layer, childAfterPos, parentAfterPos) ::
                                before(layer - 1, parentBeforePos, parentAfterPos) :: Nil, before(layer, childBeforePos, childAfterPos) :: Nil)
          }
        }
    }

  private def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = domain.primitiveTasks flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, position, task), statePredicate(layer, position, pred))
          else
            impliesNot(action(layer, position, task), statePredicate(layer, position, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  private def stateChange(layer: Int, position: Int): Seq[Clause] = domain.primitiveTasks flatMap {
    case task: ReducedTask =>
      task.effect.conjuncts collect {
        // negated effect is also contained, ignore this one if it is negative
        case Literal(pred, isPositive, _) if !((task.effect.conjuncts exists { l => l.predicate == pred && l.isNegative == isPositive }) && !isPositive) =>
          // there won't be any parameters
          if (isPositive)
            impliesSingle(action(layer, position, task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(action(layer, position, task), statePredicate(layer, position + 1, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  // maintains the state only if all actions are actually executed
  // TODO deal with actions that have both (add p) and (del p) as effects
  private def weakMaintainState(layer: Int, position: Int): Seq[Clause] = domain.predicates flatMap {
    predicate =>
      true :: false :: Nil flatMap {
        isPositive => domain.primitiveTasks flatMap {
          case task: ReducedTask =>
            if (!(task.effect.conjuncts exists {
              l => l.predicate == predicate && l.isPositive == isPositive
            })) {
              if (isPositive) // if there is no effect making it true then anything that is false stays false
                impliesTrueAntNotToNot(action(layer, position, task), statePredicate(layer, position, predicate), statePredicate(layer, position + 1, predicate))
              else // anything that is true stays true
                impliesRightAnd(action(layer, position, task) :: statePredicate(layer, position, predicate) :: Nil, statePredicate(layer, position + 1, predicate) :: Nil)
            } else Nil
          case _                 => noSupport(FORUMLASNOTSUPPORTED)
        }
      }
  }

  // maintains the state only if all actions are actually executed
  private def maintainState(layer: Int, position: Int): Seq[Clause] = domain.predicates flatMap {
    predicate =>
      true :: false :: Nil map {
        makeItPositive =>
          val changingActions: Seq[Task] = domain.primitiveTasks filter {
            case task: ReducedTask => task.effect.conjuncts exists { l =>
              val matching = l.predicate == predicate && l.isPositive == makeItPositive

              if ((task.effect.conjuncts exists { l => l.predicate == predicate && l.isNegative == makeItPositive }) && !makeItPositive)
                false
              else matching

            }
            case _                 => noSupport(FORUMLASNOTSUPPORTED)
          }
          val taskLiterals = changingActions map { action(layer, position, _) } map { (_, true) }
          Clause(taskLiterals :+(statePredicate(layer, position, predicate), makeItPositive) :+(statePredicate(layer, position + 1, predicate), !makeItPositive))
      }
  }


  lazy val decompositionFormula: Seq[Clause] = {
    // can't deal with this yet
    initialPlan.planStepsWithoutInitGoal foreach {
      ps => assert(!ps.schema.isPrimitive)
    }
    val layerMinusOneActions: Seq[Clause] = initialPlan.planStepsWithoutInitGoal.zipWithIndex flatMap {
      case (ps, i) => Clause(action(-1, i, ps.schema)) :: Clause(actionUsed(-1, i)) :: Clause(actionAbstract(-1, i)) :: Nil
    }
    val layerMinusOneNoOtherAction: Seq[Clause] = noActionForLayerFrom(-1, initialPlan.planStepsWithoutInitGoal.length, numberOfActionsPerLayer)
    val layerMinusOneOrdering: Seq[Clause] = initialPlan.orderingConstraints.minimalOrderingConstraints() filterNot {
      _.containsAny(initialPlan.initAndGoal: _*)
    } map {
      case OrderingConstraint(beforePS, afterPS) => Clause(before(-1, initialPlan.planStepsWithoutInitGoal indexOf beforePS, initialPlan.planStepsWithoutInitGoal indexOf afterPS))
    }
    val generalConstraintsLayerMinusOne = Range(0, numberOfActionsPerLayer) flatMap { position =>
      applyMethod(-1, position) ++ notTwoMethods(-1, position) ++ methodMustHaveChildren(-1, position) ++ selectActionsForLayer(-1, position)
    }

    val layerMinusOne = layerMinusOneActions ++ layerMinusOneNoOtherAction ++ layerMinusOneOrdering ++ generalConstraintsLayerMinusOne

    val ordinaryLayers: Seq[Clause] = Range(0, K) flatMap { layer => (Range(0, numberOfActionsPerLayer) flatMap { position =>
      selectActionsForLayer(layer, position) ++ maintainOrdering(layer, position) ++
        applyMethod(layer, position) ++ notTwoMethods(layer, position) ++ methodMustHaveChildren(layer, position) ++
        mustBeChildOf(layer, position) ++ fatherMustExist(layer, position) ++ childImpliesChildOf(layer, position) ++
        maintainPrimitive(layer, position)
    }) ++ transitiveOrderForLayer(layer) ++ consistentOrderForLayer(layer)
    }

    val primitiveOrdering: Seq[Clause] = Range(0, taskSequence.length - 1) map {
      case index => Clause(before(K - 1, index, index + 1))
    }

    layerMinusOne ++ ordinaryLayers ++ primitiveOrdering
  }

  lazy val givenActionsFormula: Seq[Clause] = taskSequence.zipWithIndex map { case (task, index) => Clause(action(K - 1, index, task)) }

  lazy val noAbstractsFormula: Seq[Clause] = Range(0, numberOfActionsPerLayer) flatMap { position => domain.abstractTasks map { task => Clause((action(K - 1, position, task), false)) } }

  lazy val stateTransitionFormula: Seq[Clause] = Range(0, numberOfActionsPerLayer) flatMap { position =>
    primitivesApplicable(K - 1, position) ++ stateChange(K - 1, position) ++ maintainState(K - 1, position)
  }

  lazy val initialAndGoalState: Seq[Clause] = {
    val initiallyTruePredicates = initialPlan.init.substitutedEffects collect { case Literal(pred, true, _) => pred }

    val initTrue = initiallyTruePredicates map {pred => Clause((statePredicate(K - 1, 0, pred), true))}
    val initFalse = domain.predicates diff initiallyTruePredicates map {pred => Clause((statePredicate(K - 1, 0, pred), false))}

    val goal = initialPlan.goal.substitutedPreconditions map {
      case Literal(pred, isPos, _) => Clause((statePredicate(K - 1, numberOfActionsPerLayer, pred), isPos))
    }

    initTrue ++ initFalse ++ goal
  }

  lazy val atoms      : Seq[String]      = ((decompositionFormula ++ givenActionsFormula ++ stateTransitionFormula ++ initialAndGoalState) flatMap { _.disjuncts map { _._1 } }).distinct
  lazy val atomIndices: Map[String, Int] = atoms.zipWithIndex.toMap


  def miniSATString(formulas: Seq[Clause]*): String = {
    val flatFormulas = formulas.flatten
    val header = "p cnf " + atoms.length + " " + flatFormulas.length + "\n"

    val stringBuffer = new StringBuffer()

    flatFormulas foreach {
      case Clause(lits) =>
        lits foreach { case (atom, isPos) =>
          val atomInt = (atomIndices(atom) + 1) * (if (isPos) 1 else -1)
          stringBuffer append atomInt
          stringBuffer append ' '
        }
        stringBuffer append "0\n"
    }

    header + stringBuffer.toString
  }
}

object VerifyEncoding {
  def computeTheoreticalK(domain: Domain, plan: Plan, taskSequence: Seq[Task]): Int = {
    val icapsPaperLimit = 2 * taskSequence.length * (domain.abstractTasks.length + 1)
    val TSTGPath = domain.taskSchemaTransitionGraph.longestPathLength
    val minimumMethodSize = domain.minimumMethodSize

    println("PATH: " + TSTGPath)
    println("min \\Delta:" + minimumMethodSize)
    icapsPaperLimit
  }
}

case class Clause(disjuncts: Seq[(String, Boolean)]) {}

object Clause {
  def apply(atom: String): Clause = Clause((atom, true) :: Nil)

  def apply(literal: (String, Boolean)): Clause = Clause(literal :: Nil)
}
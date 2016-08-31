package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain.{ReducedTask, SimpleDecompositionMethod, Task, Domain}
import de.uniulm.ki.panda3.symbolic.logic.{Predicate, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import de.uniulm.ki.util._

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class GeneralEncoding(domain: Domain, initialPlan: Plan, taskSequence: Seq[Task], offsetToK: Int) extends VerifyEncoding {

  lazy val taskSequenceLength      = taskSequence.length
  lazy val numberOfActionsPerLayer = taskSequence.length

  ///////////////////////////
  // STRING GENERATORS
  ///////////////////////////

  val action: ((Int, Int, Task)) => String = memoise[(Int, Int, Task), String]({ case (l, p, t) => "action^" + l + "_" + p + "," + taskIndex(t) })

  protected val actionUsed: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (l, p) => "actionUsed^" + l + "_" + p })

  protected val actionAbstract: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (l, p) => "actionAbstract^" + l + "_" + p })

  val childWithIndex: ((Int, Int, Int, Int)) => String = memoise[(Int, Int, Int, Int), String]({ case (l, p, f, idx) => "child^" + l + "_" + p + "," + f + "," + idx })

  protected val childOf: ((Int, Int, Int)) => String = memoise[(Int, Int, Int), String]({ case (l, p, f) => "childof^" + l + "_" + p + "," + f })

  protected val before: ((Int, Int, Int)) => String = memoise[(Int, Int, Int), String]({ case (l, before, after) => "before^" + l + "," + before + "," + after })

  protected val method: ((Int, Int, Int)) => String = memoise[(Int, Int, Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos + "," + methodIdx })

  protected val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })

  // FORMULA STRUCTURE

  private def noActionForLayerFrom(layer: Int, firstNoAction: Int, numberOfInstances: Int): Seq[Clause] = Range(firstNoAction, numberOfInstances) flatMap { pos =>
    val actionAtoms: Seq[String] = domain.tasks map { task => action(layer, pos, task) }
    (actionAtoms map { at => Clause((at, false) :: Nil) }) :+ Clause((actionUsed(layer, pos), false) :: Nil) :+ Clause((actionAbstract(layer, pos), false) :: Nil)
  }


  private def selectActionsForLayer(layer: Int, position: Int): Seq[Clause] = {
    val (possibleActionOnLayer, impossibleActions): (Seq[Task], Seq[Task]) = possibleAndImpossibleActionsPerLayer(layer)

    //println("SELECT " + domain.taskSchemaTransitionGraph.isAcyclic + " " + possibleActionOnLayer.length + " " + impossibleActions.length)

    val actionAtoms: Seq[String] = possibleActionOnLayer map { task => action(layer, position, task) }
    val abstractActions: Seq[String] = possibleActionOnLayer collect { case task if task.isAbstract => action(layer, position, task) }
    (atMostOneOf(actionAtoms) ++ allImply(actionAtoms, actionUsed(layer, position)) ++ allImply(abstractActions, actionAbstract(layer, position)) :+
      impliesRightOr(actionAbstract(layer, position) :: Nil, abstractActions) :+ impliesRightOr(actionUsed(layer, position) :: Nil, actionAtoms)) ++
      (impossibleActions map { case task: Task => Clause((action(layer, position, task), false)) })
  }


  private def transitiveOrderForLayer(layer: Int): Seq[Clause] =
    (for (i <- Range(0, numberOfActionsPerLayer); j <- Range(0, numberOfActionsPerLayer) if i != j; k <- Range(0, numberOfActionsPerLayer) if i != k && j != k) yield
      impliesRightAnd(before(layer, i, j) :: before(layer, j, k) :: Nil, before(layer, i, k) :: Nil)).flatten

  private def consistentOrderForLayer(layer: Int): Seq[Clause] =
    for (i <- Range(0, numberOfActionsPerLayer); j <- Range(0, numberOfActionsPerLayer) if i != j) yield impliesNot(before(layer, i, j), before(layer, j, i))

  // the method applied _to_ the layer
  private def applyMethod(layer: Int, position: Int): Seq[Clause] = {
    val methodRestrictsAT = possibleMethodsWithIndexPerLayer(layer)._1 map { case (decompositionMethod, methodIdx) =>
      impliesSingle(method(layer, position, methodIdx), action(layer, position, decompositionMethod.abstractTask))
    }
    val methodMustBeApplied = impliesRightOr(actionAbstract(layer, position) :: Nil, possibleMethodsWithIndexPerLayer(layer)._1 map { case (m, mIdx) => method(layer, position, mIdx) })

    val nonApplicableMethods: Seq[Clause] = possibleMethodsWithIndexPerLayer(layer)._2 map { case (m, mIdx: Int) => Clause((method(layer, position, mIdx), false)) }

    methodRestrictsAT ++ nonApplicableMethods :+ methodMustBeApplied
  }

  private def notTwoMethods(layer: Int, position: Int): Seq[Clause] = atMostOneOf(domain.decompositionMethods.zipWithIndex map { case (_, mIdx) => method(layer, position, mIdx) })

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
        childPos => childWithIndex(layer + 1, childPos, position, mPos)
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
    possibleMethodsWithIndexPerLayer(layer)._1 flatMap {
      case (m@SimpleDecompositionMethod(_, subPlan, _), methodIdx) =>
        // those selected
        val presentChildren: Seq[Clause] = subPlan.planStepsWithoutInitGoal.zipWithIndex flatMap {
          case (ps, childNumber) =>
            val mustChildren: Clause = impliesRightOr(method(layer, fatherPosition, methodIdx) :: Nil,
                                                      Range(0, numberOfActionsPerLayer) map { childPos => childWithIndex(layer + 1, childPos, fatherPosition, childNumber) })
            // types of the children
            val childrenType: Seq[Clause] = Range(0, numberOfActionsPerLayer) map {
              childPos =>
                impliesRightAndSingle(childWithIndex(layer + 1, childPos, fatherPosition, childNumber) :: method(layer, fatherPosition, methodIdx) :: Nil,
                                action(layer + 1, childPos, ps.schema))
            }
            childrenType :+ mustChildren
        }

        // order of the children
        val minimalOrdering = subPlan.orderingConstraints.minimalOrderingConstraints() filterNot {
          _.containsAny(m.subPlan.initAndGoal: _*)
        }

        val childrenOrder: Seq[Clause] = minimalOrdering flatMap {
          case OrderingConstraint(beforePS, afterPS) =>
            val beforePos: Int = methodPlanStepIndices(methodIdx)(beforePS) //subPlan.planStepsWithoutInitGoal indexOf beforePS

            val afterPos: Int = methodPlanStepIndices(methodIdx)(afterPS) // subPlan.planStepsWithoutInitGoal indexOf afterPS
            Range(0, numberOfActionsPerLayer) flatMap {
              childBeforePos => Range(0, numberOfActionsPerLayer) map {
                childAfterPos =>
                  impliesRightAndSingle(method(layer, fatherPosition, methodIdx) :: childWithIndex(layer + 1, childBeforePos, fatherPosition, beforePos) ::
                                    childWithIndex(layer + 1, childAfterPos, fatherPosition, afterPos) :: Nil, before(layer + 1, childBeforePos, childAfterPos))
              }
            }
        }
        val nonPresentChildren: Seq[Clause] = Range(subPlan.planStepsWithoutInitGoal.length, DELTA) flatMap {
          childNumber =>
            impliesAllNot(method(layer, fatherPosition, methodIdx), Range(0, numberOfActionsPerLayer) map {
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
  private def maintainState(layer: Int, position: Int): Seq[Clause] = {
    domain.predicates flatMap {
      predicate =>
        true :: false :: Nil map {
          makeItPositive =>
            val changingActions: Seq[Task] = if (makeItPositive) domain.primitiveChangingPredicate(predicate)._1 else domain.primitiveChangingPredicate(predicate)._2
            val taskLiterals = changingActions map { action(layer, position, _) } map { (_, true) }
            Clause(taskLiterals :+(statePredicate(layer, position, predicate), makeItPositive) :+(statePredicate(layer, position + 1, predicate), !makeItPositive))
        }
    }
  }

  var numberOfChildrenClauses = 0

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
      numberOfChildrenClauses += methodMustHaveChildren(-1, position).length

      applyMethod(-1, position) ++ notTwoMethods(-1, position) ++ methodMustHaveChildren(-1, position) ++ selectActionsForLayer(-1, position)
    }

    val layerMinusOne = layerMinusOneActions ++ layerMinusOneNoOtherAction ++ layerMinusOneOrdering ++ generalConstraintsLayerMinusOne

    println("initial layer done")

    val ordinaryLayers: Seq[Clause] = Range(0, K) flatMap { layer => (Range(0, numberOfActionsPerLayer) flatMap { position =>
      println("Layer " + layer + " " + position)
      val selectAction = selectActionsForLayer(layer, position)
      val maintainOrder = maintainOrdering(layer, position)
      val selectMethod = applyMethod(layer, position)
      val atMostOneMethod = notTwoMethods(layer, position)
      val methodsProduce = methodMustHaveChildren(layer, position)
      val noInsertion = mustBeChildOf(layer, position)
      val father = fatherMustExist(layer, position)
      val setChildOf = childImpliesChildOf(layer, position)
      val keepPrimitive = maintainPrimitive(layer, position)

      numberOfChildrenClauses += methodsProduce.length

      //println(selectAction.length + " " + maintainOrder.length + " " + selectMethod.length + " " + atMostOneMethod.length + " " + methodsProduce.length + " " +
      //         noInsertion.length + " " + father.length + " " + setChildOf.length + " " + keepPrimitive.length)

      selectAction ++ maintainOrder ++ selectMethod ++ atMostOneMethod ++ methodsProduce ++ noInsertion ++ father ++ setChildOf ++ keepPrimitive
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

  lazy val initialState: Seq[Clause] = {
    val initiallyTruePredicates = initialPlan.init.substitutedEffects collect { case Literal(pred, true, _) => pred }

    val initTrue = initiallyTruePredicates map { pred => Clause((statePredicate(K - 1, 0, pred), true)) }
    val initFalse = domain.predicates diff initiallyTruePredicates map { pred => Clause((statePredicate(K - 1, 0, pred), false)) }

    initTrue ++ initFalse
  }

  lazy val goalState: Seq[Clause] = initialPlan.goal.substitutedPreconditions map { case Literal(pred, isPos, _) => Clause((statePredicate(K - 1, numberOfActionsPerLayer, pred), isPos)) }

}

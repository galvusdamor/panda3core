package de.uniulm.ki.panda3.symbolic.sat.verify

import java.io.{BufferedWriter, OutputStream, File, FileInputStream}

import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.compiler.{ExpandSortHierarchy, ClosedWorldAssumption, SHOPMethodCompiler, ToPlainFormulaRepresentation}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.{OrderingConstraint, PlanStep}
import de.uniulm.ki.util._

import scala.collection._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * This works only if grounded ....
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off number.of.methods
case class VerifyEncoding(domain: Domain, initialPlan: Plan, taskSequence: Seq[Task], offsetToK: Int) {

  val K: Int = VerifyEncoding.computeTheoreticalK(domain, initialPlan, taskSequence) + offsetToK

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
  private val methodPlanStepIndices: Map[Int, Map[PlanStep, Int]]  = (domain.decompositionMethods map { method =>
    (methodIndices(method), method.subPlan.planStepsWithoutInitGoal.zipWithIndex.toMap)
  }).toMap

  def taskIndex(task: Task): Int = taskIndices(task)


  // ATOMS
  private def methodIndex(method: DecompositionMethod): Int = methodIndices(method)

  def predicateIndex(predicate: Predicate): Int = predicateIndices(predicate)

  ///////////////////////////
  // STRING GENERATORS
  ///////////////////////////

  val action: ((Int, Int, Task)) => String = memoise[(Int, Int, Task), String]({ case (l, p, t) => "action^" + l + "_" + p + "," + taskIndex(t) })

  private val actionUsed: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (l, p) => "actionUsed^" + l + "_" + p })

  private val actionAbstract: ((Int, Int)) => String = memoise[(Int, Int), String]({ case (l, p) => "actionAbstract^" + l + "_" + p })

  val childWithIndex: ((Int, Int, Int, Int)) => String = memoise[(Int, Int, Int, Int), String]({ case (l, p, f, idx) => "child^" + l + "_" + p + "," + f + "," + idx })

  private val childOf: ((Int, Int, Int)) => String = memoise[(Int, Int, Int), String]({ case (l, p, f) => "childof^" + l + "_" + p + "," + f })

  private val before: ((Int, Int, Int)) => String = memoise[(Int, Int, Int), String]({ case (l, before, after) => "before^" + l + "," + before + "," + after })

  private val method: ((Int, Int, Int)) => String = memoise[(Int, Int, Int), String]({ case (l, pos, methodIdx) => "method^" + l + "_" + pos + "," + methodIdx })

  private val statePredicate: ((Int, Int, Predicate)) => String = memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  // LOGICALS ABBREVIATIONS
  private def atLeastOneOf(atoms: Seq[String]): Clause = Clause(atoms map { (_, true) })


  private var atMostCounter = 0

  private def atMostOneOf(atoms: Seq[String]): Seq[Clause] = {
    val buffer = new ArrayBuffer[Clause]()
    val numberOfBits: Int = Math.ceil(Math.log(atoms.length) / Math.log(2)).toInt
    val bits = Range(0, numberOfBits) map { b => ("atMost_" + atMostCounter + "_" + b, b) }

    atoms.zipWithIndex foreach { case (atom, index) =>
      bits foreach { case (bitString, b) =>
        if ((index & (1 << b)) == 0) buffer append Clause((atom, false) ::(bitString, false) :: Nil)
        else buffer append Clause((atom, false) ::(bitString, true) :: Nil)
      }
    }

    atMostCounter += 1
    buffer.toSeq

    /*val atomArray = atoms.toArray
    val buffer = new ArrayBuffer[Clause]()

    var i = 0
    while (i < atomArray.length) {
      var j = i + 1
      while (j < atomArray.length) {
        buffer append Clause((atomArray(i), false) ::(atomArray(j), false) :: Nil)
        j += 1
      }
      i += 1
    }
    println("AT MOST " + atoms.length)
    buffer.toSeq*/
  }

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


  def possibleAndImpossibleActionsPerLayer(layer: Int): (Seq[Task], Seq[Task]) = if (domain.taskSchemaTransitionGraph.isAcyclic) {
    val actionsInDistance = initialPlan.planStepsWithoutInitGoal map { _.schema } flatMap { domain.taskSchemaTransitionGraph.getVerticesInDistance(_, layer + 1) }
    val possibleActions = (actionsInDistance ++ domain.primitiveTasks).distinct
    (possibleActions, domain.tasks filterNot possibleActions.contains)
  } else
    (domain.tasks, Nil)

  def possibleMethodsWithIndexPerLayer(layer: Int): (Seq[(DecompositionMethod, Int)], Seq[(DecompositionMethod, Int)]) = {
    val possibleActions = possibleAndImpossibleActionsPerLayer(layer)._1
    domain.decompositionMethods.zipWithIndex partition { case (m, _) => possibleActions contains m.abstractTask }
  }

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
    possibleMethodsWithIndexPerLayer(layer)._1 flatMap {
      case (m@SimpleDecompositionMethod(_, subPlan, _), methodIdx) =>
        // those selected
        val presentChildren: Seq[Clause] = subPlan.planStepsWithoutInitGoal.zipWithIndex flatMap {
          case (ps, childNumber) =>
            val mustChildren: Clause = impliesRightOr(method(layer, fatherPosition, methodIdx) :: Nil,
                                                      Range(0, numberOfActionsPerLayer) map { childPos => childWithIndex(layer + 1, childPos, fatherPosition, childNumber) })
            // types of the children
            val childrenType: Seq[Clause] = Range(0, numberOfActionsPerLayer) flatMap {
              childPos =>
                impliesRightAnd(childWithIndex(layer + 1, childPos, fatherPosition, childNumber) :: method(layer, fatherPosition, methodIdx) :: Nil,
                                action(layer + 1, childPos, ps.schema) :: Nil)
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
              childBeforePos => Range(0, numberOfActionsPerLayer) flatMap {
                childAfterPos =>
                  impliesRightAnd(method(layer, fatherPosition, methodIdx) :: childWithIndex(layer + 1, childBeforePos, fatherPosition, beforePos) ::
                                    childWithIndex(layer + 1, childAfterPos, fatherPosition, afterPos) :: Nil, before(layer + 1, childBeforePos, childAfterPos) :: Nil)
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

  lazy val goalState: Seq[Clause] = {
    initialPlan.goal.substitutedPreconditions map {
      case Literal(pred, isPos, _) => Clause((statePredicate(K - 1, numberOfActionsPerLayer, pred), isPos))
    }
  }

  //lazy val atoms: Seq[String] = ((decompositionFormula ++ givenActionsFormula ++ stateTransitionFormula ++ initialState ++ goalState) flatMap { _.disjuncts map { _._1 } }).distinct

  //lazy val atomIndices: Map[String, Int] = atoms.zipWithIndex.toMap

  def miniSATString(formulasSeq: Seq[Clause], writer: BufferedWriter): Map[String, Int] = {
    val formulas = formulasSeq.toArray
    println("NUMBER OF CLAUSES " + formulasSeq.length)

    // generate the atoms to int map
    val atomIndices = new mutable.HashMap[String, Int]()
    var i = 0
    while (i < formulas.length) {
      val lits = formulas(i).disjuncts
      var j = 0
      while (j < lits.length) {
        val l = lits(j)._1
        if (!(atomIndices contains l))
          atomIndices(l) = atomIndices.size
        j += 1
      }
      i += 1
    }

    // generate the DIMACS string

    val header = "p cnf " + atomIndices.size + " " + formulas.length + "\n"

    //val stringBuffer = new StringBuffer()
    //stringBuffer append header
    writer write header

    i = 0
    while (i < formulas.length) {
      val lits = formulas(i).disjuncts
      var j = 0
      while (j < lits.length) {
        val atomInt = (atomIndices(lits(j)._1) + 1) * (if (lits(j)._2) 1 else -1)
        //stringBuffer append atomInt
        writer write ("" + atomInt)
        //stringBuffer append ' '
        writer write ' '
        j += 1
      }
      //stringBuffer append "0\n"
      writer write "0\n"
      i += 1
    }

    //stringBuffer.toString
    atomIndices.toMap
  }
}

object VerifyEncoding {

  def computeICAPSK(domain: Domain, plan: Plan, taskSequence: Seq[Task]): Int = 2 * taskSequence.length * (domain.abstractTasks.length + 1)

  def computeTSTGK(domain: Domain, plan: Plan, taskSequence: Seq[Task]): Int = domain.taskSchemaTransitionGraph.longestPathLength match {case Some(x) => x; case _ => Integer.MAX_VALUE }

  def computeMethodSize(domain: Domain, plan: Plan, taskSequence: Seq[Task]): Int = {
    // recognize the case where only top has a unit method
    val (minMethodSize, heightIncrease) = if (plan.planStepsWithoutInitGoal.size == 1) {
      val (topFromMethods, otherMethods) = domain.decompositionMethods partition { _.abstractTask == plan.planStepsWithoutInitGoal.head.schema }
      val topToMethods = domain.decompositionMethods filter { _.subPlan.planStepsWithoutInitGoal exists { _.schema == plan.planStepsWithoutInitGoal.head.schema } }


      if (topFromMethods.size == 1 && topToMethods.isEmpty) (otherMethods map { _.subPlan.planStepsWithoutInitGoal.length } min, 1) else (domain.minimumMethodSize, 0)
    } else (domain.minimumMethodSize, 0)


    if (minMethodSize >= 2) Math.ceil(Math.log(taskSequence.length) / Math.log(domain.minimumMethodSize)).toInt + heightIncrease else Integer.MAX_VALUE
  }

  def computeTheoreticalK(domain: Domain, plan: Plan, taskSequence: Seq[Task]): Int = {
    val icapsPaperLimit = computeICAPSK(domain, plan, taskSequence)
    val TSTGPath = computeTSTGK(domain, plan, taskSequence)
    val minimumMethodSize = computeMethodSize(domain, plan, taskSequence)

    Math.min(icapsPaperLimit, Math.min(TSTGPath, minimumMethodSize))
  }
}

case class Clause(disjuncts: Seq[(String, Boolean)]) {}

object Clause {
  def apply(atom: String): Clause = Clause((atom, true) :: Nil)

  def apply(literal: (String, Boolean)): Clause = Clause(literal :: Nil)
}
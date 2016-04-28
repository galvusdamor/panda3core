package de.uniulm.ki.panda3.symbolic.sat

import java.io.{File, FileInputStream}

import de.uniulm.ki.panda3.symbolic.compiler.{ToPlainFormulaRepresentation, SHOPMethodCompiler, ClosedWorldAssumption}
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{Literal, Predicate}
import de.uniulm.ki.panda3.symbolic.parser.hddl.HDDLParser
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.OrderingConstraint
import scala.collection._
import de.uniulm.ki.util._
import de.uniulm.ki.panda3.symbolic._

import scala.io.Source

/**
  * This works only if grounded ....
  *
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
//scalastyle:off number.of.methods
case class VerifyEncoding(domain: Domain, initialPlan: Plan, taskSequence: Seq[Task])(val K: Int = 2 * taskSequence.length * (domain.abstractTasks.length + 1)) {

  domain.tasks foreach { _.parameters.isEmpty }
  domain.predicates foreach { _.argumentSorts.isEmpty }
  assert(initialPlan.init.substitutedEffects.length == domain.predicates.length)

  val DELTA = domain.decompositionMethods map { _.subPlan.planStepsWithoutInitGoal.length } max

  def taskIndex(task: Task): Int = domain.tasks indexOf task


  // ATOMS
  private def methodIndex(method: DecompositionMethod): Int = domain.decompositionMethods indexOf method

  private def predicateIndex(predicate: Predicate): Int = domain.predicates indexOf predicate

  private def action(layer: Int, position: Int, task: Task): String = "action^" + layer + "_" + position + "," + taskIndex(task)

  private def actionUsed(layer: Int, position: Int): String = "actionUsed^" + layer + "_" + position

  private def actionAbstract(layer: Int, position: Int): String = "actionAbstract^" + layer + "_" + position

  private def childWithIndex(layer: Int, position: Int, father: Int, indexOnMethod: Int): String = "child^" + layer + "_" + position + "," + father + "," + indexOnMethod

  private def childOf(layer: Int, position: Int, father: Int): String = "childof^" + layer + "_" + position + "," + father

  private def before(layer: Int, beforeIndex: Int, afterIndex: Int): String = "before^" + layer + "," + beforeIndex + "," + afterIndex

  private def method(layer: Int, position: Int, method: DecompositionMethod): String = "method^" + layer + "_" + position + "," + methodIndex(method)

  private def statePredicate(layer: Int, position: Int, predicate: Predicate): String = "predicate^" + layer + "_" + position + "," + predicateIndex(predicate)


  // LOGICALS ABBREVIATIONS
  private def atLeastOneOf(atoms: Seq[String]): Seq[Clause] = Clause(atoms map { (_, true) }) :: Nil

  private def atMostOneOf(atoms: Seq[String]): Seq[Clause] = for (i <- atoms.indices; j <- Range(i + 1, atoms.length)) yield Clause((atoms(i), false) ::(atoms(j), false) :: Nil)

  private def exactlyOneOf(atoms: Seq[String]): Seq[Clause] = atMostOneOf(atoms) ++ atLeastOneOf(atoms)

  private def impliesNot(left: String, right: String): Seq[Clause] = Clause((left, false) ::(right, false) :: Nil) :: Nil

  private def impliesTrueAntNotToNot(leftTrue: String, leftFalse: String, right: String): Seq[Clause] = Clause((leftTrue, false) ::(leftFalse, true) ::(right, false) :: Nil) :: Nil

  private def impliesAllNot(left: String, right: Seq[String]): Seq[Clause] = right flatMap { impliesNot(left, _) }

  private def impliesRightAnd(leftConjunct: Seq[String], rightConjunct: Seq[String]): Seq[Clause] = {
    val negLeft = leftConjunct map { (_, false) }
    rightConjunct map { r => Clause(negLeft :+(r, true)) }
  }

  private def impliesRightOr(leftConjunct: Seq[String], rightConjunct: Seq[String]): Seq[Clause] = {
    val negLeft = leftConjunct map { (_, false) }
    Clause(negLeft ++ (rightConjunct map { x => (x, true) }))
  } :: Nil

  private def allImply(left: Seq[String], target: String): Seq[Clause] = left flatMap { x => impliesRightAnd(x :: Nil, target :: Nil) }


  // FORMULA STRUCTURE
  private def selectActionsForLayer(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap { pos =>
    val actionAtoms: Seq[String] = domain.tasks map { task => action(layer, pos, task) }
    val abstractActions: Seq[String] = domain.abstractTasks map { task => action(layer, pos, task) }
    atMostOneOf(actionAtoms) ++ allImply(actionAtoms, actionUsed(layer, pos)) ++ allImply(abstractActions, actionAbstract(layer, pos)) ++
      impliesRightOr(actionAbstract(layer, pos) :: Nil, abstractActions) ++ impliesRightOr(actionUsed(layer, pos) :: Nil, actionAtoms)
  }

  private def noActionForLayerFrom(layer: Int, firstNoAction: Int, numberOfInstances: Int): Seq[Clause] = Range(firstNoAction, numberOfInstances) flatMap { pos =>
    val actionAtoms: Seq[String] = domain.tasks map { task => action(layer, pos, task) }
    (actionAtoms map { at => Clause((at, false) :: Nil) }) :+ Clause((actionUsed(layer, pos), false) :: Nil) :+ Clause((actionAbstract(layer, pos), false) :: Nil)
  }


  private def transitiveOrderForLayer(layer: Int, numberOfInstances: Int): Seq[Clause] =
    (for (i <- Range(0, numberOfInstances); j <- Range(0, numberOfInstances) if i != j; k <- Range(0, numberOfInstances) if i != k && j != k) yield
      impliesRightAnd(before(layer, i, j) :: before(layer, j, k) :: Nil, before(layer, i, k) :: Nil)).flatten

  private def consistentOrderForLayer(layer: Int, numberOfInstances: Int): Seq[Clause] =
    (for (i <- Range(0, numberOfInstances); j <- Range(0, numberOfInstances) if i != j) yield impliesNot(before(layer, i, j), before(layer, j, i))).flatten

  // the method applied _to_ the layer
  private def applyMethod(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap { pos =>
    val methodRestrictsAT = domain.decompositionMethods flatMap { decompositionMethod =>
      impliesRightAnd(method(layer, pos, decompositionMethod) :: Nil, action(layer, pos, decompositionMethod.abstractTask) :: Nil)
    }
    val methodMustBeApplied = impliesRightOr(actionAbstract(layer, pos) :: Nil, domain.decompositionMethods map { m => method(layer, pos, m) })

    methodRestrictsAT ++ methodMustBeApplied
  }

  private def notTwoMethods(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap { pos =>
    atMostOneOf(domain.decompositionMethods map { method(layer, pos, _) })
  }

  private def childImpliesChildOf(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap { pos =>
    Range(0, numberOfInstances) flatMap { father =>
      val childrenPredicates = Range(0, DELTA) map { childWithIndex(layer, pos, father, _) }

      (childrenPredicates flatMap { child => impliesRightAnd(child :: Nil, childOf(layer, pos, father) :: Nil) }) ++
        impliesRightOr(childOf(layer, pos, father) :: Nil, childrenPredicates)


    }
  }


  private def mustBeChildOf(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos =>
      val fathers = Range(0, numberOfInstances) flatMap {
        father => Range(0, DELTA) map {
          mPos => childWithIndex(layer, pos, father, mPos)
        }
      }
      val children: Seq[Seq[String]] = Range(0, DELTA) map {
        mPos => Range(0, numberOfInstances) map {
          childPos => childWithIndex(layer, childPos, pos, mPos)
        }
      }

      impliesRightOr(actionUsed(layer, pos) :: Nil, fathers) ++ atMostOneOf(fathers) ++ (children flatMap atMostOneOf)
  }


  private def fatherMustExist(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos => Range(0, numberOfInstances) flatMap {
      father =>
        Range(0, DELTA) flatMap {
          mPos =>
            val mustHaveAnyFather = impliesRightAnd(childWithIndex(layer, pos, father, mPos) :: Nil, actionUsed(layer - 1, father) :: Nil)
            val ifNotFirstFatherMustBeAbstract = if (mPos != 0 || father != pos)
              impliesRightAnd(childWithIndex(layer, pos, father, mPos) :: Nil, actionAbstract(layer - 1, father) :: Nil)
            else Nil

            mustHaveAnyFather ++ ifNotFirstFatherMustBeAbstract
        }
    }
  }

  private def methodMustHaveChildren(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    fatherPos =>
      domain.decompositionMethods flatMap {
        case m@SimpleDecompositionMethod(_, subPlan) =>
          // those selected
          val presentChildren: Seq[Clause] = subPlan.planStepsWithoutInitGoal.zipWithIndex flatMap {
            case (ps, childNumber) =>
              val mustChildren: Seq[Clause] = impliesRightOr(method(layer, fatherPos, m) :: Nil,
                                                             Range(0, numberOfInstances) map {
                                                               childPos => childWithIndex(layer + 1, childPos, fatherPos, childNumber)
                                                             })
              // types of the children
              val childrenType: Seq[Clause] = Range(0, numberOfInstances) flatMap {
                childPos =>
                  impliesRightAnd(childWithIndex(layer + 1, childPos, fatherPos, childNumber) :: method(layer, fatherPos, m) :: Nil, action(layer + 1, childPos, ps.schema) :: Nil)
              }
              mustChildren ++ childrenType
          }

          // order of the children
          val minimalOrdering = subPlan.orderingConstraints.minimalOrderingConstraints() filterNot {
            _.containsAny(m.subPlan.initAndGoal: _*)
          }
          val childrenOrder: Seq[Clause] = minimalOrdering flatMap {
            case OrderingConstraint(beforePS, afterPS) =>
              val beforePos: Int = subPlan.planStepsWithoutInitGoal indexOf beforePS
              val afterPos: Int = subPlan.planStepsWithoutInitGoal indexOf afterPS
              Range(0, numberOfInstances) flatMap {
                childBeforePos => Range(0, numberOfInstances) flatMap {
                  childAfterPos =>
                    impliesRightAnd(method(layer, fatherPos, m) :: childWithIndex(layer + 1, childBeforePos, fatherPos, beforePos) :: childWithIndex(layer + 1, childAfterPos, fatherPos,
                                                                                                                                                     afterPos) :: Nil,
                                    before(layer + 1, childBeforePos, childAfterPos) :: Nil)
                }
              }
          }
          val nonPresentChildren: Seq[Clause] = Range(subPlan.planStepsWithoutInitGoal.length, DELTA) flatMap {
            childNumber =>
              impliesAllNot(method(layer, fatherPos, m), Range(0, numberOfInstances) map {
                childPos => childWithIndex(layer + 1, childPos, fatherPos, childNumber)
              })
          }
          presentChildren ++ nonPresentChildren ++ childrenOrder
      }
  }

  private def maintainPrimitive(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos => domain.primitiveTasks flatMap {
      task =>
        impliesRightAnd(action(layer - 1, pos, task) :: Nil, action(layer, pos, task) :: Nil) ++ impliesRightAnd(action(layer - 1, pos, task) :: Nil,
                                                                                                                 childWithIndex(layer, pos, pos, 0) :: Nil)
    }
  }

  private def maintainOrdering(layer: Int, numberOfInstances: Int): Seq[Clause] =
    Range(0, numberOfInstances) flatMap {
      parentBeforePos => Range(0, numberOfInstances) flatMap {
        parentAfterPos =>
          Range(0, numberOfInstances) flatMap {
            childBeforePos => Range(0, numberOfInstances) flatMap {
              childAfterPos =>
                impliesRightAnd(childOf(layer, childBeforePos, parentBeforePos) :: childOf(layer, childAfterPos, parentAfterPos) ::
                                  before(layer - 1, parentBeforePos, parentAfterPos) :: Nil, before(layer, childBeforePos, childAfterPos) :: Nil)
            }
          }
      }
    }

  private def primitivesApplicable(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos => domain.primitiveTasks flatMap {
      case task: ReducedTask =>
        task.precondition.conjuncts flatMap {
          case Literal(pred, isPositive, _) => // there won't be any parameters
            if (isPositive)
              impliesRightAnd(action(layer, pos, task) :: Nil, statePredicate(layer, pos, pred) :: Nil)
            else
              impliesNot(action(layer, pos, task), statePredicate(layer, pos, pred))
        }
      case _                 => noSupport(FORUMLASNOTSUPPORTED)
    }
  }

  private def stateChange(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos => domain.primitiveTasks flatMap {
      case task: ReducedTask =>
        task.effect.conjuncts flatMap {
          case Literal(pred, isPositive, _) => // there won't be any parameters
            if (isPositive)
              impliesRightAnd(action(layer, pos, task) :: Nil, statePredicate(layer, pos + 1, pred) :: Nil)
            else
              impliesNot(action(layer, pos, task), statePredicate(layer, pos + 1, pred))
        }
      case _                 => noSupport(FORUMLASNOTSUPPORTED)
    }
  }

  // maintains the state only if all actions are actually executed
  private def weakMaintainState(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos => domain.predicates flatMap {
      predicate =>
        true :: false :: Nil flatMap {
          isPositive => domain.primitiveTasks flatMap {
            case task: ReducedTask =>
              if (!(task.effect.conjuncts exists {
                l => l.predicate == predicate && l.isPositive == isPositive
              })) {
                if (isPositive) // if there is no effect making it true then anything that is false stays false
                  impliesTrueAntNotToNot(action(layer, pos, task), statePredicate(layer, pos, predicate), statePredicate(layer, pos + 1, predicate))
                else // anything that is true stays true
                  impliesRightAnd(action(layer, pos, task) :: statePredicate(layer, pos, predicate) :: Nil, statePredicate(layer, pos + 1, predicate) :: Nil)
              } else Nil
            case _                 => noSupport(FORUMLASNOTSUPPORTED)
          }
        }
    }
  }

  // maintains the state only if all actions are actually executed
  private def maintainState(layer: Int, numberOfInstances: Int): Seq[Clause] = Range(0, numberOfInstances) flatMap {
    pos => domain.predicates flatMap {
      predicate =>
        true :: false :: Nil map {
          makeItPositive =>
            val changingActions: Seq[Task] = domain.primitiveTasks filter {
              case task: ReducedTask => task.effect.conjuncts exists {
                l => l.predicate == predicate && l.isPositive == makeItPositive
              }
              case _                 => noSupport(FORUMLASNOTSUPPORTED)
            }
            val taskLiterals = changingActions map {
              action(layer, pos, _)
            } map {
              (_, true)
            }
            Clause(taskLiterals :+(statePredicate(layer, pos, predicate), makeItPositive) :+(statePredicate(layer, pos + 1, predicate), !makeItPositive))
        }
    }
  }

  lazy val numberOfLayers          = K
  lazy val numberOfActionsPerLayer = taskSequence.length

  lazy val decompositionFormula: Seq[Clause] = {
    // can't deal with this yet
    initialPlan.planStepsWithoutInitGoal foreach {
      ps => assert(!ps.schema.isPrimitive)
    }
    val layerMinusOne: Seq[Clause] = (initialPlan.planStepsWithoutInitGoal.zipWithIndex flatMap {
      case (ps, i) =>
        // assert specific actions
        Clause(action(-1, i, ps.schema)) :: Clause(actionUsed(-1, i)) :: Clause(actionAbstract(-1, i)) :: Nil
    }) ++ (initialPlan.orderingConstraints.minimalOrderingConstraints() filterNot {
      _.containsAny(initialPlan.initAndGoal: _*)
    } map {
      case OrderingConstraint(beforePS, afterPS) => Clause(before(-1, initialPlan.planStepsWithoutInitGoal indexOf beforePS, initialPlan.planStepsWithoutInitGoal indexOf afterPS))
    }) ++ applyMethod(-1, initialPlan.planStepsWithoutInitGoal.length) ++ notTwoMethods(-1, initialPlan.planStepsWithoutInitGoal.length) ++
      methodMustHaveChildren(-1, initialPlan.planStepsAndRemovedPlanSteps.length) ++
      selectActionsForLayer(-1, initialPlan.planStepsWithoutInitGoal.length) ++ noActionForLayerFrom(-1, initialPlan.planStepsWithoutInitGoal.length, numberOfActionsPerLayer)

    val ordinaryLayers: Seq[Clause] = Range(0, K) flatMap {
      layer =>
        val actionSelect = selectActionsForLayer(layer, numberOfActionsPerLayer)
        val order = transitiveOrderForLayer(layer, numberOfActionsPerLayer) ++ consistentOrderForLayer(layer, numberOfActionsPerLayer) ++ maintainOrdering(layer, numberOfActionsPerLayer)
        val methods = applyMethod(layer, numberOfActionsPerLayer) ++ notTwoMethods(layer, numberOfActionsPerLayer) ++ methodMustHaveChildren(layer, numberOfActionsPerLayer)
        val childOf = mustBeChildOf(layer, numberOfActionsPerLayer) ++ fatherMustExist(layer, numberOfActionsPerLayer) ++ childImpliesChildOf(layer, numberOfActionsPerLayer)
        val childrenType = maintainPrimitive(layer, numberOfActionsPerLayer)

        actionSelect ++ order ++ methods ++ childOf ++ childrenType
    }

    val primitiveOrdering: Seq[Clause] = Range(0, taskSequence.length - 1) map {
      case index => Clause(before(K - 1, index, index + 1))
    }

    layerMinusOne ++ ordinaryLayers ++ primitiveOrdering
  }

  lazy val givenActionsFormula: Seq[Clause] = taskSequence.zipWithIndex map { case (task, index) => Clause(action(K - 1, index, task)) }

  lazy val stateTransitionFormula: Seq[Clause] = primitivesApplicable(K - 1, numberOfActionsPerLayer) ++ stateChange(K - 1, numberOfActionsPerLayer) ++
    maintainState(K - 1, numberOfActionsPerLayer)

  lazy val initialAndGoalState: Seq[Clause] = {
    val init = initialPlan.init.substitutedEffects map {
      case Literal(pred, isPos, _) => Clause((statePredicate(K - 1, 0, pred), isPos))
    }
    val goal = initialPlan.goal.substitutedPreconditions map {
      case Literal(pred, isPos, _) => Clause((statePredicate(K - 1, numberOfActionsPerLayer, pred), isPos))
    }

    init ++ goal
  }

  lazy val atoms: Seq[String] = ((decompositionFormula ++ givenActionsFormula ++ stateTransitionFormula ++ initialAndGoalState) flatMap { _.disjuncts map { _._1 } }).distinct

  def miniSATString(formulas: Seq[Clause]*): String = {
    val flatFormulas = formulas.flatten
    val header = "p cnf " + atoms.length + " " + flatFormulas.length + "\n"

    val formString = flatFormulas map {
      case Clause(lits) => ((lits map { case (atom, isPos) => ((atoms indexOf atom) + 1) * (if (isPos) 1 else -1) }) :+ 0) mkString " "
    } mkString "\n"

    header + formString
  }
}

case class Clause(disjuncts: Seq[(String, Boolean)]) {}

object Clause {
  def apply(atom: String): Clause = Clause((atom, true) :: Nil)

  def apply(literal: (String, Boolean)): Clause = Clause(literal :: Nil)
}

object VerifyEncoding {

  import sys.process._

  def main(args: Array[String]) {
    val domFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/sat/simpleDomain.hddl"
    val probFile = "src/test/resources/de/uniulm/ki/panda3/symbolic/sat/simpleProblem.hddl"
    val domAndInitialPlan: (Domain, Plan) = HDDLParser.parseDomainAndProblem(new FileInputStream(domFile), new FileInputStream(probFile))

    val sortExpansion = domAndInitialPlan._1.expandSortHierarchy()

    val parsedDom = domAndInitialPlan._1.update(sortExpansion)
    val parsedProblem = domAndInitialPlan._2.update(sortExpansion)

    // apply the CWA
    val cwaApplied = ClosedWorldAssumption.transform(parsedDom, parsedProblem, ())
    val simpleMethod = SHOPMethodCompiler.transform(cwaApplied, ())
    val flattened = ToPlainFormulaRepresentation.transform(simpleMethod, ())

    val (dom, iniPlan) = flattened

    println(dom.statisticsString)
    val p1 = dom.primitiveTasks.find({ _.name == "p1" }).get
    val p2 = dom.primitiveTasks.find({ _.name == "p2" }).get
    val p3 = dom.primitiveTasks.find({ _.name == "p3" }).get

    val verifySeq = p2 :: p2 :: p2 :: p2 :: p2 :: p1 :: p3 :: p3 :: Nil

    val encoder = VerifyEncoding(dom, iniPlan, verifySeq)(10)

    println("K " + encoder.K + " DELTA " + encoder.DELTA)

    val usedFormula = encoder.decompositionFormula ++ encoder.stateTransitionFormula ++ encoder.initialAndGoalState //++ encoder.givenActionsFormula
    val encodedString = encoder.miniSATString(usedFormula)

    println("Variables : " + encoder.atoms.length + " Constraints: " + encoder.decompositionFormula.length)

    writeStringToFile(usedFormula mkString "\n", new File("/home/gregor/formula"))
    writeStringToFile(encodedString, new File("/home/gregor/foo"))

    try {
      println("Starting minisat")
      "minisat /home/gregor/foo /home/gregor/res.txt" !
    } catch {
      case rt: RuntimeException => println("Minisat exitcode problem ...")
    }
    val minisatOutput = Source.fromFile("/home/gregor/res.txt").mkString
    val minisatResult = minisatOutput.split("\n")(0)
    println("MiniSAT says: " + minisatResult)
    if (minisatResult == "SAT") {
      val minisatAssignment = minisatOutput.split("\n")(1)
      val literals = (minisatAssignment.split(" ") filter { _ != 0 } map { _.toInt }).toSet

      // iterate through layers
      val nodes = Range(-1, encoder.numberOfLayers) flatMap { layer => Range(0, encoder.numberOfActionsPerLayer) map { pos =>
        dom.tasks map { task =>
          val actionString = encoder.action(layer, pos, task)
          val isPres = if (encoder.atoms contains actionString) literals contains (1 + (encoder.atoms indexOf actionString)) else false
          (actionString, isPres)
        } find { _._2 }
      } filter { _.isDefined } map { _.get._1 }
      }

      val edges: Seq[(String, String)] = Range(-1, encoder.numberOfLayers) flatMap { layer => Range(0, encoder.numberOfActionsPerLayer) flatMap { pos => Range(0, encoder
        .numberOfActionsPerLayer) flatMap {
        father =>
          Range(0, encoder.DELTA) flatMap { childIndex =>
            val childString = encoder.childWithIndex(layer, pos, father, childIndex)
            if ((encoder.atoms contains childString) && (literals contains (1 + (encoder.atoms indexOf childString)))) {
              // find parent and myself
              val fatherStringOption = nodes find { _.startsWith("action^" + (layer - 1) + "_" + father) }
              assert(fatherStringOption.isDefined, "action^" + (layer - 1) + "_" + father + " is not present but is a fathers")
              val childStringOption = nodes find { _.startsWith("action^" + layer + "_" + pos) }
              assert(childStringOption.isDefined, "action^" + layer + "_" + pos + " is not present but is a child")
              (fatherStringOption.get, childStringOption.get) :: Nil
            } else Nil
            //literals contains (1 + (encoder.atoms indexOf actionString)) else false
            //(actionString, isPres)
          }
      }
      }
      }

      val decompGraph = SimpleDirectedGraph(nodes, edges)
      Dot2PdfCompiler.writeDotToFile(decompGraph, "/home/gregor/decomp.pdf")


      val allTrueAtoms = encoder.atoms.zipWithIndex filter { case (atom, index) => literals contains (index + 1) } map { _._1 }

      writeStringToFile(allTrueAtoms mkString "\n", new File("/home/gregor/true.txt"))
    }

    // print action mapping to numbers:
    println(dom.tasks map { t => t.name + " -> " + encoder.taskIndex(t) } mkString "\n")
    println(dom.predicates map { t => t.name + " -> " + encoder.predicateIndex(t) } mkString "\n")

    //println(encoder.atoms mkString "\n")

    //println(encodedString)
  }
}

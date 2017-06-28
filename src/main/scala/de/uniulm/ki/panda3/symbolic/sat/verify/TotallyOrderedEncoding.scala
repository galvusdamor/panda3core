package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.configuration._
import de.uniulm.ki.panda3.symbolic._
import de.uniulm.ki.panda3.symbolic.domain._
import de.uniulm.ki.panda3.symbolic.logic.{And, Predicate, Literal}
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.plan.element.PlanStep
import de.uniulm.ki.panda3.symbolic.sat.verify.sogoptimiser.{NativeOptimiser, GreedyNumberOfChildrenFromTotallyOrderedOptimiser, GreedyNumberOfAbstractChildrenOptimiser}
import de.uniulm.ki.util._

import scala.annotation.elidable
import scala.annotation.elidable._
import scala.collection.{mutable, Seq}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class TotallyOrderedEncoding(timeCapsule: TimeCapsule,
                                  domain: Domain, initialPlan: Plan, reductionMethod: SATReductionMethod, taskSequenceLength: Int, offsetToK: Int, overrideK: Option[Int] = None)
  extends PathBasedEncoding[Unit, Unit] with EncodingWithLinearPlan {

  val numberOfChildrenClauses: Int = 0

  assert(domain.isTotallyOrdered)
  assert(initialPlan.orderingConstraints.isTotalOrder())

  protected val statePredicate: ((Int, Int, Predicate)) => String =
    memoise[(Int, Int, Predicate), String]({ case (l, pos, pred) => "predicate^" + l + "_" + pos + "," + predicateIndex(pred) })


  override protected def additionalClausesForMethod(layer: Int, path: Seq[Int], method: DecompositionMethod, methodString: String, taskOrdering: scala.Seq[Task]): Seq[Clause] = Nil

  private def primitivesApplicable(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.precondition.conjuncts map {
        case Literal(pred, isPositive, _) => // there won't be any parameters
          if (isPositive)
            impliesSingle(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
          else
            impliesNot(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  private def stateChange(layer: Int, position: Int): Seq[Clause] = primitivePaths(position)._2.toSeq filter { _.isPrimitive } flatMap {
    case task: ReducedTask =>
      task.effect.conjuncts collect {
        // negated effect is also contained, ignore this one if it is negative
        case Literal(pred, isPositive, _) if !((task.effect.conjuncts exists { l => l.predicate == pred && l.isNegative == isPositive }) && !isPositive) =>
          // there won't be any parameters
          if (isPositive)
            impliesSingle(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
          else
            impliesNot(pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, task), statePredicate(layer, position + 1, pred))
      }
    case _                 => noSupport(FORUMLASNOTSUPPORTED)
  }

  // maintains the state only if all actions are actually executed
  private def maintainState(layer: Int, position: Int): Seq[Clause] = domain.predicates flatMap {
    predicate =>
      true :: false :: Nil map {
        makeItPositive =>
          val changingActions: Seq[Task] = (if (makeItPositive) domain.primitiveChangingPredicate(predicate)._1 else domain.primitiveChangingPredicate(predicate)._2) filter
            primitivePaths(position)._2.contains

          val taskLiterals = changingActions map { pathAction(primitivePaths(position)._1.length, primitivePaths(position)._1, _) } map { (_, true) }
          Clause(taskLiterals.+:(statePredicate(layer, position, predicate), makeItPositive).+:(statePredicate(layer, position + 1, predicate), !makeItPositive))
      }
  }

  override lazy val stateTransitionFormula: Seq[Clause] = primitivePaths.indices flatMap { position =>
    primitivesApplicable(K, position) ++ stateChange(K, position) ++ maintainState(K, position)
  }

  override lazy val noAbstractsFormula: Seq[Clause] =
    primitivePaths flatMap { case (position, tasks) => tasks filter { _.isAbstract } map { task => Clause((pathAction(position.length, position, task), false)) } }

  override lazy val goalState: Seq[Clause] =
    initialPlan.goal.substitutedPreconditions map { case Literal(predicate, isPos, _) => Clause((statePredicate(K, primitivePaths.length, predicate), isPos)) }

  lazy val initialState: Seq[Clause] = {
    val initiallyTruePredicates = initialPlan.init.substitutedEffects collect { case Literal(pred, true, _) => pred }

    val initTrue = initiallyTruePredicates map { predicate => Clause((statePredicate(K, 0, predicate), true)) }
    val initFalse = domain.predicates diff initiallyTruePredicates map { pred => Clause((statePredicate(K, 0, pred), false)) }

    initTrue ++ initFalse
  }


  override def linearPlan: scala.Seq[Map[Task, String]] = primitivePaths map { case (path, tasks) => tasks map { t => t -> pathAction(path.length, path, t) } toMap }

  override lazy val givenActionsFormula: Seq[Clause] = ???

  override protected def initialPayload(possibleTasks: Set[Task], path: scala.Seq[Int]): Unit = ()

  override protected def combinePayloads(childrenPayload: Seq[Unit], intermediate: Unit): Unit = ()

  override protected def computeTaskSequenceArrangement(possibleMethods: Array[DecompositionMethod],
                                                        possiblePrimitives: scala.Seq[Task]): (Array[Array[Int]], Array[Int], Array[Set[Task]], Unit) = {
    val methodTaskGraphs = (possibleMethods map { _.subPlan.orderingConstraints.fullGraph }) ++ (
      possiblePrimitives map { t => SimpleDirectedGraph(PlanStep(-1, t, Nil) :: Nil, Nil) })

    assert(methodTaskGraphs forall { _.allTotalOrderings.get.length == 1 })

    // TODO we are currently mapping plansteps, maybe we should prefer plansteps with identical tasks to be mapped together
    //println("MINI " + possibleMethods.length + " " + possiblePrimitives.length)
    val lb = if (methodTaskGraphs.nonEmpty) methodTaskGraphs map { _.vertices count { _.schema.isAbstract } } max else 0
    // TODO what to do?
    //val g = DirectedGraph.minimalInducedSuperGraph[PlanStep](methodTaskGraphs) //, minimiseAbstractTaskOccurencesMetric)
    //val g = GreedyNumberOfAbstractChildrenOptimiser.minimalSOG(methodTaskGraphs)
    val g = GreedyNumberOfChildrenFromTotallyOrderedOptimiser.minimalSOG(methodTaskGraphs)
    //val g = NativeOptimiser.minimalSOG(methodTaskGraphs)
    //println("done")
    val minimalSuperGraph = g._1
    val planStepToIndexMappings: Seq[Map[PlanStep, Int]] = g._2
    val topologicalOrdering = minimalSuperGraph.topologicalOrdering.get

    val (methodMappings, primitiveMappings) = planStepToIndexMappings map { m => m map { case (ps, node) => (ps, topologicalOrdering.indexOf(node)) } } splitAt possibleMethods.length

    val childrenIndicesToPossibleTasks = minimalSuperGraph.vertices map { _ => new mutable.HashSet[Task]() }

    val tasksPerMethodToChildrenMapping = methodMappings.zipWithIndex map { case (mapping, methodIndex) =>
      val methodPlanSteps = possibleMethods(methodIndex).subPlan.planStepsWithoutInitGoal
      (methodPlanSteps map { ps =>
        childrenIndicesToPossibleTasks(mapping(ps)) add ps.schema
        mapping(ps)
      }).toArray
    } toArray

    val childrenForPrimitives = primitiveMappings.zipWithIndex map { case (mapping, primitiveIndex) =>
      assert(mapping.size == 1)
      childrenIndicesToPossibleTasks(mapping.head._2) add mapping.head._1.schema
      mapping.head._2
    } toArray

    //println("\n\nGraph minisation")
    //println(childrenIndicesToPossibleTasks map {s => s map {t => t.name + " " + t.isAbstract} mkString " "} mkString "\n")

    (tasksPerMethodToChildrenMapping, childrenForPrimitives, childrenIndicesToPossibleTasks map { _.toSet } toArray, ())
  }


  private def canBeAchieved(prec: Seq[Seq[(Int, Task)]]): Boolean = {
    def dfs(remaining: Seq[Seq[(Int, Task)]], asserted: Map[Int, Task]): Boolean = if (remaining.isEmpty) true
    else {
      // try every possibility
      remaining.head exists { case (l, task) => if (!asserted.contains(l) || asserted(l) == task) dfs(remaining.tail, asserted.+((l, task))) else false }
    }

    dfs(prec, Map())
  }


  protected def filterPrimitivesFF(sortedPaths: Array[(Seq[Int], Set[Task])], fullTest: Boolean = false): Seq[Set[Task]] = {
    // perform simple PG-style reachability on the given list of actions
    val initialPredicates = initialPlan.init.schema.effectsAsPredicateBool collect { case (predicate, true) => predicate } toSet
    val initialAchiever: Map[Predicate, Seq[(Int, Task)]] = initialPredicates map { p => p -> ((-1, ReducedTask("init", true, Nil, Nil, Nil, And(Nil), And(Nil))) :: Nil) } toMap
    val initialState = (initialPredicates, initialAchiever)

    val (finalState, tasksToRemoveFromPaths) = sortedPaths.zipWithIndex.foldLeft[((Set[Predicate], Map[Predicate, Seq[(Int, Task)]]), Seq[Set[Task]])]((initialState, Nil))(
      {
        case (((state, predicateLayers), toRemove), ((path, tasks), layer)) =>
          val (applicable, nonApplicable) = tasks partition { t => t.isPrimitive && (t.preconditionsAsPredicateBool forall { case (p, true) => state contains p }) && {
            if (fullTest) {
              // run full NP matching test
              val possibleAchievers: Seq[Seq[(Int, Task)]] = t.preconditionsAsPredicateBool.collect({ case (a, true) => a }) map predicateLayers
              canBeAchieved(possibleAchievers)
            } else true
          }
          }

          if (nonApplicable.nonEmpty)
            println("Found " + nonApplicable.size + " non applicable tasks at " + path + ". Still applicable " + applicable.size)

          val actionEffects = applicable flatMap { case task => task.effectsAsPredicateBool collect { case (predicate, true) => predicate ->(layer, task) } }

          // apply tasks
          val reachableState = state ++ actionEffects.map(_._1)

          val updatedPredicateLayer: Map[Predicate, Seq[(Int, Task)]] =
            actionEffects.foldLeft(predicateLayers)({ case (map, (p, newEntry)) => map.+((p, map.getOrElse(p, Nil) :+ newEntry)) })

          // return the reachable state
          ((reachableState, updatedPredicateLayer), toRemove :+ nonApplicable)
      })

    tasksToRemoveFromPaths
  }


  protected def filterPrimitivesH2(sortedPaths: Array[(Seq[Int], Set[Task])]): Seq[Set[Task]] = {
    // perform H2-style (i.e. look at all pairs of predicates) reachability on the given list of actions
    val simpleInitialState = initialPlan.init.schema.effectsAsPredicateBool collect { case (predicate, true) => predicate } toArray

    val h2InitialState = crossProduct(simpleInitialState, simpleInitialState) toSet

    val (_, tasksToRemoveFromPaths) = sortedPaths.foldLeft[((Set[(Predicate, Predicate)], Set[Predicate]), Seq[Set[Task]])](((h2InitialState, simpleInitialState.toSet), Nil))(
      {
        case (((h2State, h1State), toRemove), (path, tasks)) =>
          // debug output
          /*println("\n\nh2 State" + (h2State map {case (a,b) => "(" + a.name + "," + b.name + ")"} mkString " "))
          println("h1 State" + (h1State map {_.name} mkString " "))
          println("tasks: ")
          println(tasks map {case rt : ReducedTask =>
            rt.name + "\n\tprec: " + rt.precondition.conjuncts.map({_.predicate.name}) +
              "\n\tadd: " + rt.effect.conjuncts.filter(_.isPositive).map({_.predicate.name}) +
              "\n\tdel: " + rt.effect.conjuncts.filter(_.isNegative).map({_.predicate.name})
          } mkString "\n")
          */

          val (applicable, nonApplicable) = tasks partition { t =>
            if (t.isAbstract) false
            else {
              val positivePreconditions = t.preconditionsAsPredicateBool collect { case (predicate, true) => predicate } toArray
              val h2Preconditions = crossProduct(positivePreconditions, positivePreconditions)

              val nonHoldingPrecs = h2Preconditions filterNot h2State.contains

              //println("Missing Precondition for " + t.name  + ": " + (nonHoldingPrecs map {case (a,b) => "(" + a.name + "," + b.name + ")"} mkString " "))

              // the state must contain all h2 pairs
              h2Preconditions forall h2State.contains
            }
          }

          if (nonApplicable.nonEmpty)
            println("Found " + nonApplicable.size + " non applicable tasks at " + path + ". Still applicable " + applicable.size)

          // compute which h2 pairs are now reachable
          val newlyReachable: Set[(Predicate, Predicate)] = applicable flatMap { t =>
            val (adds, dels) = t.effectsAsPredicateBool partition { _._2 }
            val delSet = dels map { _._1 } toSet
            val positivePreconditions = t.preconditionsAsPredicateBool collect { case (predicate, true) => predicate } toArray

            val x: Seq[(Predicate, Predicate)] = adds flatMap { case (add, _) =>
              if (delSet contains add) Nil
              else {
                // if this action achieves both, then it is possible
                val byAdd: Seq[(Predicate, Predicate)] = adds collect { case (add2, _) if !(delSet contains add2) => (add, add2) }

                // if a predicate could be achieved on its one, then it can now together with all non-deleted things
                val byNonDel: Seq[(Predicate, Predicate)] = h1State.toSeq collect { case nondel if !(delSet contains nondel) &&
                  (positivePreconditions forall { prec => h2State contains(prec, nondel) })
                => (add, nondel) ::(nondel, add) :: Nil
                } flatten

                byAdd ++ byNonDel
              }
            }

            x
          }


          // apply tasks
          val h2reachableState = h2State ++ newlyReachable
          val h1reachableState = h1State ++ (applicable flatMap { _.effectsAsPredicateBool collect { case (predicate, true) => predicate } })

          // return the reachable state
          ((h2reachableState, h1reachableState), toRemove :+ nonApplicable)
      })

    tasksToRemoveFromPaths
  }

  protected def minimisePathDecompositionTree(pdt: PathDecompositionTree[Unit]): PathDecompositionTree[Unit] = reductionMethod match {
    case OnlyNormalise =>
      val dontRemovePrimitives: Seq[Set[Task]] = pdt.primitivePaths.toSeq map { _ => Set[Task]() }

      pdt.restrictPathDecompositionTree(dontRemovePrimitives).normalise
    case _             =>
      println("Round in minimisation")
      // get the primitive paths in the order they actually occur
      val sortedPaths = pdt.primitivePaths sortWith { case ((p1, _), (p2, _)) => PathBasedEncoding.pathSortingFunction(p1, p2) }

      val tasksToRemoveFromPaths: Seq[Set[Task]] = reductionMethod match {
        case FFReduction             => filterPrimitivesFF(sortedPaths, fullTest = false)
        case H2Reduction             => filterPrimitivesH2(sortedPaths)
        case FFReductionWithFullTest => filterPrimitivesFF(sortedPaths, fullTest = true)
      }

      val foundAnyTaskToRemove = tasksToRemoveFromPaths exists { _.nonEmpty }
      if (!foundAnyTaskToRemove) {
        // plot PDT to file
        Dot2PdfCompiler.writeDotToFile(pdt.treeBelowAsGraph, "pdtN.pdf")

        pdt.normalise
      }
      else
        minimisePathDecompositionTree(pdt.restrictPathDecompositionTree(tasksToRemoveFromPaths))
  }

}
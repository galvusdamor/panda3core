package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.sat.verify.{Clause, ExistsStep, LinearPrimitivePlanEncoding}

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLOnParallelEncoding(lTLFormula: LTLFormula, id: String) extends AdditionalSATConstraint {

  val formulaIDMap: Map[LTLFormula, Int] = lTLFormula.allSubformulae.zipWithIndex toMap
  val idFormulaMap: Map[Int, LTLFormula] = formulaIDMap map { _.swap }

  println(idFormulaMap.toSeq.sortBy(_._1) map { case (a, b) => a + " " + b.longInfo } mkString "\n")

  private def formulaHoldsAtTime(formula: LTLFormula, position: Int, time: Int): String = id + "_holds_" + formulaIDMap(formula) + "_" + position + "@" + time

  private def predicateTrueAtPosition(predicate: Int, position: Int, time: Int): String = id + "_pred_" + predicate + "_pos_" + position + "@" + time

  override def apply(linearEncoding: LinearPrimitivePlanEncoding): Seq[Clause] = linearEncoding match {
    case e: ExistsStep =>
      val relevantPredicates = lTLFormula.allPredicates
      // determine places of intermediate steps in one exists-step phase. We have to compute the full relevant state whenever one action altering said state has been executed
      // position i here means that we have to check *after* action i was executed
      val ltlCheckPositions = e.disablingGraphTotalOrder.zipWithIndex.foldLeft[(Set[Predicate], Seq[Int])]((Set(), Nil))(
        { case ((currentPredicates, breaksSoFar), (task, taskIndex)) =>
          val currentEffects = task.effectAsPredicateSet intersect relevantPredicates

          // we can continue the sequence started by the last task
          val taskIndices = if (currentEffects.subsetOf(currentPredicates) && taskIndex != 0) breaksSoFar else breaksSoFar :+ (taskIndex - 1)

          (currentEffects, taskIndices)
        })._2 :+ (e.disablingGraphTotalOrder.length - 1) // we always have to check after the last position ...

      println(ltlCheckPositions mkString ", ")


      val predicatesWithIndex: Seq[(Predicate, Int)] = relevantPredicates.toSeq.zipWithIndex
      val predicatesToIndex: Map[Predicate, Int] = predicatesWithIndex.toMap

      val predicateTruth = Range(0, linearEncoding.taskSequenceLength) flatMap {
        case time =>
          // the first time-point has to be identical to the actual state before executing the actions
          val firstTimeStep: Seq[Clause] = predicatesWithIndex flatMap { case (p, pi) =>
            linearEncoding.impliesSingle(predicateTrueAtPosition(pi, -1, time), linearEncoding.statePredicate(linearEncoding.K - 1, time, p)) ::
              linearEncoding.impliesSingle(linearEncoding.statePredicate(linearEncoding.K - 1, time, p), predicateTrueAtPosition(pi, -1, time)) :: Nil
          }

          // the first time-point has to be identical to the actual state before executing the actions
          val lastTimeStep = predicatesWithIndex flatMap { case (p, pi) =>
            linearEncoding.impliesSingle(predicateTrueAtPosition(pi, e.disablingGraphTotalOrder.length - 1, time), linearEncoding.statePredicate(linearEncoding.K - 1, time + 1, p)) ::
              linearEncoding.impliesSingle(linearEncoding.statePredicate(linearEncoding.K - 1, time + 1, p), predicateTrueAtPosition(pi, e.disablingGraphTotalOrder.length - 1, time)) :: Nil
          }


          // first add clauses tracing the truth of the predicates relevant to the formula
          val actionsDoSomething: Seq[Clause] = e.disablingGraphTotalOrder.zipWithIndex.foldLeft[(Seq[Clause], Seq[Int], Seq[(Task, Int)], Int)]((Nil, ltlCheckPositions.drop(1), Nil, -1))(
            {
              case ((clausesSoFar, positionsToProcess, unprocessedTasks, previousPosition), nextTask) =>
                val (lastTask, taskPosition) = nextTask
                val tasksToProcess = unprocessedTasks :+ nextTask
                if (taskPosition == positionsToProcess.head) {
                  // essentially the classical encoding by Kautz&Selman with a bit of the \exists-step semantics. We ensure by choosing the in-between-steps that the LTL-truth is ok, and
                  // since we are use the \exists-step encoding underneath, the sequence is also executable and produces a uniquely determined state
                  val effectsMustHold = tasksToProcess flatMap { case (task, _) =>
                    predicatesWithIndex filter { task.addEffectsAsPredicate contains _._1 } map { case (p, pi) =>
                      linearEncoding.impliesSingle(linearEncoding.linearPlan(time)(task), predicateTrueAtPosition(pi, taskPosition, time))
                    }
                  }

                  // add standard frame axioms
                  val frameAxioms = predicatesWithIndex flatMap { case (p, pi) =>
                    true :: false :: Nil map {
                      makeItPositive =>
                        val changingActions: Seq[(Task, Int)] =
                          if (makeItPositive) tasksToProcess.filter(_._1.addEffectsAsPredicateSet contains p)
                          else tasksToProcess.filter(_._1.delEffectsAsPredicateSet contains p)
                        val taskLiterals = changingActions map { case (t, _) => linearEncoding.linearPlan(time)(t) } map { (_, true) }
                        Clause(taskLiterals :+ (predicateTrueAtPosition(pi, previousPosition, time), makeItPositive) :+ (predicateTrueAtPosition(pi, taskPosition, time), !makeItPositive))
                    }
                  }


                  (clausesSoFar ++ effectsMustHold ++ frameAxioms, positionsToProcess.drop(1), Nil, taskPosition)
                } else {
                  // base case: just agglumerate the tasks that might have been executed
                  (clausesSoFar, positionsToProcess, tasksToProcess, previousPosition)
                }
            })._1


          firstTimeStep ++ lastTimeStep ++ actionsDoSomething
      }


      val formulaTruth = Range(0, linearEncoding.taskSequenceLength) flatMap { time =>
        // formulas that don't need temporal expressions to be evaluated
        val nonTimeDependentPart = ltlCheckPositions flatMap { case thisPosition =>
          val last = time == linearEncoding.taskSequenceLength - 1 && thisPosition == e.disablingGraphTotalOrder.length - 1
          lTLFormula.allSubformulae flatMap { sub =>
            sub match {
              case PredicateAtom(p) => linearEncoding.impliesSingle(formulaHoldsAtTime(sub, thisPosition, time), predicateTrueAtPosition(predicatesToIndex(p), thisPosition, time)) :: Nil
              case TaskAtom(t)      => assert(false, "can't handle yet"); Nil
              case LTLNot(f)        => linearEncoding.impliesNot(formulaHoldsAtTime(sub, thisPosition, time), formulaHoldsAtTime(f, thisPosition, time)) :: Nil
              case LTLAnd(l)        => linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, thisPosition, time) :: Nil, l.map(formulaHoldsAtTime(_, thisPosition, time)))
              case LTLOr(l)         => linearEncoding.impliesRightOr(formulaHoldsAtTime(sub, thisPosition, time) :: Nil, l.map(formulaHoldsAtTime(_, thisPosition, time))) :: Nil

              case LTLAlways(f) if last =>
                linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, thisPosition, time) :: Nil, formulaHoldsAtTime(f, thisPosition, time) :: Nil)

              case LTLEventually(f) if last =>
                linearEncoding.impliesSingle(formulaHoldsAtTime(sub, thisPosition, time), formulaHoldsAtTime(f, thisPosition, time)) :: Nil

              case LTLUntil(_, f2) if last =>
                linearEncoding.impliesSingle(formulaHoldsAtTime(sub, thisPosition, time), formulaHoldsAtTime(f2, thisPosition, time)) :: Nil

              case LTLRelease(_, f2) if last =>
                linearEncoding.impliesSingle(formulaHoldsAtTime(sub, thisPosition, time), formulaHoldsAtTime(f2, thisPosition, time)) :: Nil

              case _ => Nil // Anything else is time dependent
            }
          }
        }

        // parts of the formula that are actual temporal operators
        val ltlPartBetween = ltlCheckPositions.zip(ltlCheckPositions.drop(1)) flatMap { case (thisPosition, nextPosition) =>
          lTLFormula.allSubformulae flatMap { sub =>
            sub match {
              case LTLAlways(f)  =>
                linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, thisPosition, time) :: Nil,
                                               formulaHoldsAtTime(f, thisPosition, time) :: formulaHoldsAtTime(sub, nextPosition, time) :: Nil)

              case LTLEventually(f) =>
                linearEncoding.impliesRightOr(formulaHoldsAtTime(sub, thisPosition, time) :: Nil,
                                              formulaHoldsAtTime(f, thisPosition, time) :: formulaHoldsAtTime(sub, nextPosition, time) :: Nil) :: Nil

              case LTLUntil(f1, f2) =>
                // structure a -> 2 v (1 & n) == (-a v 2 v 1) & (-a v 2 v n)
                val a = formulaHoldsAtTime(sub, thisPosition, time)
                val l1 = formulaHoldsAtTime(f1, thisPosition, time)
                val l2 = formulaHoldsAtTime(f2, thisPosition, time)
                val n = formulaHoldsAtTime(sub, nextPosition, time)
                Clause((a, false) :: (l2, true) :: (l1, true) :: Nil) :: Clause((a, false) :: (l2, true) :: (n, true) :: Nil) :: Nil

              case LTLRelease(f1, f2) =>
                val a = formulaHoldsAtTime(sub, thisPosition, time)
                val l1 = formulaHoldsAtTime(f1, thisPosition, time)
                val l2 = formulaHoldsAtTime(f2, thisPosition, time)
                val n = formulaHoldsAtTime(sub, nextPosition, time)
                linearEncoding.impliesSingle(a, l2) :: linearEncoding.impliesRightOr(a :: Nil, l1 :: n :: Nil) :: Nil
              // TODO: check this !!!!

              case _ => Nil // Anything else is not time dependent
            }
          }
        }


        nonTimeDependentPart ++ ltlPartBetween
      }

      val holeFormulaHolds = Clause(formulaHoldsAtTime(lTLFormula, -1, 0)) :: Nil

      // assert that formulas in between are true
      val timestepConnectors = Range(0, linearEncoding.taskSequenceLength) flatMap { case time =>
        lTLFormula.allSubformulae flatMap { sub =>
          linearEncoding.impliesSingle(formulaHoldsAtTime(sub, e.disablingGraphTotalOrder.length - 1, time), formulaHoldsAtTime(sub, -1, time + 1)) ::
          linearEncoding.impliesSingle(formulaHoldsAtTime(sub, - 1, time + 1), formulaHoldsAtTime(sub, e.disablingGraphTotalOrder.length - 1, time)) ::
          Nil
        }
      }


      predicateTruth ++ formulaTruth ++ holeFormulaHolds ++ timestepConnectors

  }
}

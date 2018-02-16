package de.uniulm.ki.panda3.symbolic.sat.additionalConstraints

import de.uniulm.ki.panda3.symbolic.domain.Task
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.sat.verify._

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class LTLMattmüllerEncoding(lTLFormula: LTLFormula, id: String) extends AdditionalSATConstraint with AdditionalEdgesInDisablingGraph {

  val formulaIDMap: Map[LTLFormula, Int] = lTLFormula.allSubformulae.zipWithIndex toMap
  val idFormulaMap: Map[Int, LTLFormula] = formulaIDMap map { _.swap }

  println(idFormulaMap.toSeq.sortBy(_._1) map { case (a, b) => a + " " + b.longInfo } mkString "\n")

  private def formulaHoldsAtTime(formula: LTLFormula, time: Int): String = id + "_holds_" + formulaIDMap(formula) + "@" + time


  def apply(linearEncoding: LinearPrimitivePlanEncoding): Seq[Clause] = linearEncoding match {
    case e: ExistsStep =>
      // write for every timestep the rules
      val formulaTruth = Range(0, linearEncoding.taskSequenceLength + 1) flatMap { time =>
        lTLFormula.allSubformulae flatMap { sub =>
          sub match {
            case PredicateAtom(p) => linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), linearEncoding.statePredicate(linearEncoding.K - 1, time, p)) :: Nil
            case TaskAtom(t)      => assert(false, "can't handle yet"); Nil
            case LTLNot(f)        => linearEncoding.impliesNot(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f, time)) :: Nil
            case LTLAnd(l)        => linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, time) :: Nil, l.map(formulaHoldsAtTime(_, time)))
            case LTLOr(l)         => linearEncoding.impliesRightOr(formulaHoldsAtTime(sub, time) :: Nil, l.map(formulaHoldsAtTime(_, time))) :: Nil

            case LTLAlways(f) if time != linearEncoding.taskSequenceLength =>
              linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, time) :: Nil, formulaHoldsAtTime(f, time) :: formulaHoldsAtTime(sub, time + 1) :: Nil)
            case LTLAlways(f) if time == linearEncoding.taskSequenceLength =>
              linearEncoding.impliesRightAnd(formulaHoldsAtTime(sub, time) :: Nil, formulaHoldsAtTime(f, time) :: Nil)

            case LTLEventually(f) if time != linearEncoding.taskSequenceLength =>
              linearEncoding.impliesRightOr(formulaHoldsAtTime(sub, time) :: Nil, formulaHoldsAtTime(f, time) :: formulaHoldsAtTime(sub, time + 1) :: Nil) :: Nil
            case LTLEventually(f) if time == linearEncoding.taskSequenceLength =>
              linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f, time)) :: Nil

            case LTLUntil(f1, f2) if time != linearEncoding.taskSequenceLength =>
              // structure a -> 2 v (1 & n) == (-a v 2 v 1) & (-a v 2 v n)
              val a = formulaHoldsAtTime(sub, time)
              val l1 = formulaHoldsAtTime(f1, time)
              val l2 = formulaHoldsAtTime(f2, time)
              val n = formulaHoldsAtTime(sub, time + 1)
              Clause((a, false) :: (l2, true) :: (l1, true) :: Nil) :: Clause((a, false) :: (l2, true) :: (n, true) :: Nil) :: Nil
            case LTLUntil(_, f2) if time == linearEncoding.taskSequenceLength  =>
              linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f2, time)) :: Nil

            case LTLRelease(f1, f2) if time != linearEncoding.taskSequenceLength =>
              val a = formulaHoldsAtTime(sub, time)
              val l1 = formulaHoldsAtTime(f1, time)
              val l2 = formulaHoldsAtTime(f2, time)
              val n = formulaHoldsAtTime(sub, time + 1)
              linearEncoding.impliesSingle(a, l2) :: linearEncoding.impliesRightOr(a :: Nil, l1 :: n :: Nil) :: Nil
            // TODO: check this !!!!
            case LTLRelease(_, f2) if time == linearEncoding.taskSequenceLength =>
              linearEncoding.impliesSingle(formulaHoldsAtTime(sub, time), formulaHoldsAtTime(f2, time)) :: Nil
          }
        }
      }
      // restrict to sequential encoding for testing purposes
      /*val amo = Range(0, linearEncoding.taskSequenceLength).flatMap(position =>
                                                                      linearEncoding.atMostOneOf(linearEncoding.domain.primitiveTasks map {
                                                                        linearEncoding
                                                                          .action(linearEncoding.K - 1, position, _)
                                                                      }))*/

      val holeFormulaHolds = Clause(formulaHoldsAtTime(lTLFormula, 0)) :: Nil

      // add the chains necessary for the formulae
      val chainClauses = {
        lTLFormula.allPredicates flatMap { m =>
          val E: Array[(Task, Int)] = e.disablingGraphTotalOrder.zipWithIndex filter { case (a, _) => !a.delEffectsAsPredicateSet.contains(m) && !a.addEffectsAsPredicateSet.contains(m) }
          val R: Array[(Task, Int)] = e.disablingGraphTotalOrder.zipWithIndex filter { case (a, _) => a.delEffectsAsPredicateSet.contains(m) || a.addEffectsAsPredicateSet.contains(m) }
          val chainID: String = "ltl_" + e.predicateIndex(m)

          e.generateChainFor(E, R, chainID)
        }
      }

      formulaTruth ++ holeFormulaHolds ++ chainClauses //++ amo
    case _             => assert(false, "Mattmüller-Encoding is only compatible with existsstep"); Nil
  }

  override def additionalEdges(encoding: ExistsStep)(
    predicateToAdding: Map[Predicate, Array[encoding.IntTask]], predicateToDeleting: Map[Predicate, Array[encoding.IntTask]],
    predicateToNeeding: Map[Predicate, Array[encoding.IntTask]]): Seq[(encoding.IntTask, encoding.IntTask)] = {
    // first get relevant predicates
    val relevantPredicates = lTLFormula.allPredicates
    // TODO: so something with actions

    val affectedActions: Array[encoding.IntTask] = relevantPredicates flatMap { p => predicateToAdding.getOrElse(p, Array()) ++ predicateToDeleting.getOrElse(p, Array()) } toArray

    val edgesFromStatePredicates = affectedActions flatMap { a1 =>
      affectedActions collect { case a2 if a1 != a2 && a1.hasMoreEffectsRelativeToPredicates(a2, relevantPredicates) => (a1, a2)
      }
    }

    edgesFromStatePredicates
  }
}
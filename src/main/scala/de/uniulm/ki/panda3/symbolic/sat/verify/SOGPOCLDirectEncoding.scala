package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.configuration.SATReductionMethod
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGPOCLDirectEncoding(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan,
                                 taskSequenceLengthQQ: Int, reductionMethod: SATReductionMethod, offsetToK: Int, overrideK: Option[Int] = None,
                                 restrictionMethod: RestrictionMethod) extends SOGPOCLEncoding {
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ


  def causalThreatsFormula(supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)]): Seq[Clause] = {
    val time1 = System.currentTimeMillis()

    val supporterPairs = supporterLiterals map { case (a, b, _) => (a, b) } distinct
    val nonOrderedMap: Map[(Seq[Int], Seq[Int]), Seq[(Seq[Int], Set[Task])]] = supporterPairs map { case (p1, p2) =>
      val nonord = extendedSOG.vertices filterNot { case (p, _) => p == p1 || p == p2 || onlyPathSOG.reachable(p2).contains(p) || onlyPathSOG.reachable(p).contains(p1) }
      (p1, p2) -> nonord
    } toMap
    val time2 = System.currentTimeMillis()
    println("Unordered prepared " + (time2 - time1).toDouble./(1000))

    // no causal threats
    val potentialThreader: Seq[(Seq[Int], Seq[Int], Task, Seq[Int], Predicate, String)] = supporterLiterals flatMap { case (p1, p2, prec) =>
      val nonOrdered = nonOrderedMap((p1, p2))
      val supporterLiteral = supporter(p1, p2, prec)

      nonOrdered flatMap { case (pt, ts) =>
        val threadingTasks = ts filter { t => t.effectsAsPredicateBool exists { case (p, s) => !s && p == prec } }

        threadingTasks map { t => (p1, p2, t, pt, prec, supporterLiteral) }
      }
    }
    val time3 = System.currentTimeMillis
    println("Potential threader determined: " + potentialThreader.length + " in " + (time3 - time2).toDouble./(1000))

    val noCausalThreat: Seq[Clause] = potentialThreader map { case (p1, p2, t, pt, prec, supporterLiteral) =>
      impliesRightOr(supporterLiteral :: pathAction(pt.length, pt, t) :: Nil, before(pt, p1) :: before(p2, pt) :: Nil)
    }
    val time4 = System.currentTimeMillis()
    println("No causal threats: " + noCausalThreat.length + " clauses, time needed " + (time4 - time3).toDouble./(1000))

    noCausalThreat
  }
}

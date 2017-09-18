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
                            taskSequenceLengthQQ: Int, reductionMethod: SATReductionMethod, offsetToK: Int, overrideK: Option[Int] = None) extends SOGPOCLEncoding {
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ


  def causalThreatsFormula(supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)]) : Seq[Clause] = {
    val startTime = System.currentTimeMillis()
    // no causal threats
    val potentialThreader: Seq[(Seq[Int], Seq[Int], Task, Seq[Int], Predicate)] = supporterLiterals flatMap { case (p1, p2, prec) =>
      val nonOrdered = extendedSOG.vertices filterNot { case (p, _) => p == p1 || p == p2 || onlyPathSOG.reachable(p2).contains(p) || onlyPathSOG.reachable(p).contains(p1) }

      nonOrdered flatMap { case (pt, ts) =>
        val threadingTasks = ts filter { t => t.effectsAsPredicateBool exists { case (p, s) => !s && p == prec } }

        threadingTasks map { t => (p1, p2, t, pt, prec) }
      }
    }
    val middleTime = System.currentTimeMillis

    val noCausalThreat: Seq[Clause] = potentialThreader map { case (p1, p2, t, pt, prec) =>
      impliesRightOr(supporter(p1, p2, prec) :: pathAction(pt.length, pt, t) :: Nil, before(pt, p1) :: before(p2, pt) :: Nil)
    }
    val endTime = System.currentTimeMillis()
    println("No causal threats: " + noCausalThreat.length + " clauses, time needed " + (middleTime - startTime).toDouble./(1000) + " and " + (endTime - middleTime).toDouble./(1000))

    noCausalThreat
  }
}

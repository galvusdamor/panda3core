package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.configuration.SATReductionMethod
import de.uniulm.ki.panda3.symbolic.domain.Domain
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.TimeCapsule

import scala.collection.Seq

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGPOCLDeleteEncoding(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan,
                                 taskSequenceLengthQQ: Int, reductionMethod: SATReductionMethod, offsetToK: Int, overrideK: Option[Int] = None,
                                 restrictionMethod: RestrictionMethod) extends SOGPOCLEncoding {
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  protected def deletes(path: Seq[Int], precondition: Predicate): String = "del^" + path.mkString(";") + "_" + precondition.name

  def causalThreatsFormula(supporterLiterals: Seq[(Seq[Int], Seq[Int], Predicate)]): Seq[Clause] = {

    val willDelete: Seq[Clause] = extendedSOG.vertices flatMap { case (path, tasks) =>
      tasks flatMap { t => t.effectsAsPredicateBool filterNot { _._2 } map { case (p, _) => impliesSingle(pathAction(path.length, path, t), deletes(path, p)) } }
    }
    println("Paths may delete: " + willDelete.length + " clauses")

    val startTime = System.currentTimeMillis()
    // no causal threats
    val potentialThreader: Seq[(Seq[Int], Seq[Int], Seq[Int], Predicate, String)] = supporterLiterals flatMap { case (p1, p2, prec) =>
      val nonOrdered = extendedSOG.vertices filterNot { case (p, _) => p == p1 || p == p2 || onlyPathSOG.reachable(p2).contains(p) || onlyPathSOG.reachable(p).contains(p1) }
      val supporterLiteral = supporter(p1, p2, prec)

      nonOrdered collect { case (pt, ts) if ts exists { t => t.effectsAsPredicateBool exists { case (p, s) => !s && p == prec } } =>
        (p1, p2, pt, prec, supporterLiteral)
      }
    }
    val middleTime = System.currentTimeMillis

    val noCausalThreat = potentialThreader map { case (p1, p2, pt, prec, supporterLiteral) =>
      impliesRightOr(supporterLiteral :: deletes(pt, prec) :: Nil, before(pt, p1) :: before(p2, pt) :: Nil)
    }
    val endTime = System.currentTimeMillis()
    println("No causal threats: " + noCausalThreat.length + " clauses, time needed " + (middleTime - startTime).toDouble./(1000) + " and " + (endTime - middleTime).toDouble./(1000))

    willDelete ++ noCausalThreat
  }

}

package de.uniulm.ki.panda3.symbolic.sat.verify

import de.uniulm.ki.panda3.configuration.SATReductionMethod
import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.util.{DirectedGraph, TimeCapsule, memoise}

import scala.collection.Seq
import scala.collection.mutable.ArrayBuffer

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class SOGPOREncoding(timeCapsule: TimeCapsule, domain: Domain, initialPlan: Plan,
                          taskSequenceLengthQQ: Int, reductionMethod: SATReductionMethod, offsetToK: Int, overrideK: Option[Int] = None) extends SOGPartialNoPath {
  lazy val taskSequenceLength: Int = taskSequenceLengthQQ

  protected val directlyBefore: ((Seq[Int], Seq[Int])) => String =
    memoise[(Seq[Int], Seq[Int]), String]({ case (pathA: Seq[Int], pathB: Seq[Int]) => assert(pathA != pathB); "direct_before_" + pathA.mkString(";") + "_" + pathB.mkString(";") })

  protected val between: ((Seq[Int], Seq[Int], Seq[Int])) => String =
    memoise[(Seq[Int], Seq[Int], Seq[Int]), String]({
                                                      case (pathA: Seq[Int], between: Seq[Int], pathB: Seq[Int]) =>
                                                        assert(pathA != pathB)
                                                        "between_" + pathA.mkString(";") + "_" + between.mkString(";") + "_" + pathB.mkString(";")
                                                    })

  protected def trueBefore(pathA: Seq[Int], precondition: Predicate): String = "true^" + pathA.mkString(";") + "_" + precondition.name

  protected def supporter(pathA: Seq[Int], pathB: Seq[Int], precondition: Predicate): String = "supp^" + pathA.mkString(";") + "_" + pathB.mkString(";") + "_" + precondition.name

  override lazy val stateTransitionFormula: Seq[Clause] = {
    // init and goal must be contaiend in the final plan
    val initAndGoalMustBePresent = Clause(pathAction(1, initVertex._1, initVertex._2.head)) :: Clause(pathAction(1, goalVertex._1, goalVertex._2.head)) :: Nil


    // guess a correct transitive order
    var startTime = System.currentTimeMillis()
    val orderMustBeTransitive: Array[Clause] = transitiveOrderClauses
    var endTime = System.currentTimeMillis()
    println("Order is transitive and respects SOG: " + orderMustBeTransitive.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))

    // infer the direct predecessors
    startTime = System.currentTimeMillis()
    val directPredecessors: Array[Clause] = {
      val clauses = new ArrayBuffer[Clause]
      var i = 0
      while (i < pathsWithInitAndGoal.length) {
        var k = 0
        while (k < pathsWithInitAndGoal.length) {
          if (i != k) {
            var j = 0
            val betweens = new ArrayBuffer[String]
            while (j < pathsWithInitAndGoal.length) {
              if (j != i && j != k) {
                clauses append impliesRightNotAll(directlyBefore(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j)) :: Nil,
                                                  before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(k)) :: before(pathsWithInitAndGoal(k), pathsWithInitAndGoal(j)) :: Nil)

                betweens append between(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j), pathsWithInitAndGoal(k))

                clauses appendAll impliesRightAnd(between(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j), pathsWithInitAndGoal(k)) :: Nil,
                                                  before(pathsWithInitAndGoal(i), pathsWithInitAndGoal(j)) ::
                                                    before(pathsWithInitAndGoal(j), pathsWithInitAndGoal(k)) :: Nil
                                                 )
                //clauses append Clause((before(a, b), false) :: (directlyBefore(a, b), true) :: (before(a, c), true) :: Nil)
                //clauses append Clause((before(a, b), false) :: (directlyBefore(a, b), true) :: (before(c, b), true) :: Nil)
              }
              j += 1
            }
            val a = pathsWithInitAndGoal(i)
            val b = pathsWithInitAndGoal(k)
            clauses append impliesRightOr(before(a, b) :: Nil,betweens.toArray :+ directlyBefore(a,b))
            clauses append impliesSingle(directlyBefore(a,b), before(a,b))

          }
          k += 1
        }
        i += 1
      }
      clauses.toArray
    }
    endTime = System.currentTimeMillis()
    println("Directly before inferred correctly: " + directPredecessors.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))

    // if present, then preconditions must be true ...
    startTime = System.currentTimeMillis()
    val trueIfPrecondition: Array[Clause] = extendedSOG.vertices flatMap { case node@(path, tasks) => tasks flatMap { t =>
      t.preconditionsAsPredicateBool map { case (prec, true) => impliesSingle(pathAction(path.length, path, t), trueBefore(path, prec)) }
    }
    } toArray

    endTime = System.currentTimeMillis()
    println("Preconditions must be true: " + trueIfPrecondition.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))

    // if true, there must be either a direct predecessor where it is true, or a direct predecessor that produces it
    startTime = System.currentTimeMillis()
    val trueMustBeSupported = extendedSOG.vertices flatMap { case node@(path, tasks) =>
      domain.predicates map { p =>
        val possibleSupporters = extendedSOG.vertices filter { _ != node } map { other => supporter(other._1, path, p) }
        impliesRightOr(trueBefore(path, p) :: Nil, possibleSupporters)
      }
    }
    endTime = System.currentTimeMillis()
    println("True must be supported: " + trueMustBeSupported.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))

    // support implies direct order and either effect or previous trueness
    startTime = System.currentTimeMillis()
    val supportImplies: Seq[Clause] = extendedSOG.vertices flatMap { case node@(path, tasks) =>
      domain.predicates flatMap { p =>
        extendedSOG.vertices filter { _ != node } flatMap { other =>
          val support = supporter(path, other._1, p)

          val pathActionSupporter: Seq[String] = tasks filter { _.effectsAsPredicateBool contains ((p, true)) } map { t => pathAction(path.length, path, t) } toSeq

          impliesRightOr(support :: Nil, pathActionSupporter :+ trueBefore(path, p)) :: impliesSingle(support, directlyBefore(path, other._1)) :: Nil
        }
      }
    }
    endTime = System.currentTimeMillis()
    println("Support implies things: " + supportImplies.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))


    // being true implies that there can be nothing inhibiting
    startTime = System.currentTimeMillis()
    val threaterOrdered: Seq[Clause] = extendedSOG.vertices flatMap { case node@(path, _) =>
      domain.predicates flatMap { p =>
        val trueAtom = trueBefore(path, p)
        extendedSOG.vertices filter { _ != node } flatMap { case (threater, tasks) =>
          tasks filter { _.effectsAsPredicateBool contains ((p, false)) } flatMap { t =>
            impliesRightNotAll(trueAtom :: pathAction(threater.length, threater, t) :: Nil, directlyBefore(threater,path) :: Nil) ::
              impliesRightOr(trueAtom :: pathAction(threater.length, threater, t) :: Nil, before(threater, path) :: before(path, threater) :: Nil) :: Nil
          }
        }
      }
    }
    endTime = System.currentTimeMillis()
    println("Threater must be ordered: " + threaterOrdered.length + " clauses, time needed " + (endTime - startTime).toDouble./(1000))

    initAndGoalMustBePresent ++ orderMustBeTransitive ++ directPredecessors ++ trueIfPrecondition ++ trueMustBeSupported ++ supportImplies ++ threaterOrdered
  }
}

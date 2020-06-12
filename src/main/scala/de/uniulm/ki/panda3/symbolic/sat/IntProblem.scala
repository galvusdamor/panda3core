// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.sat

import java.util

import de.uniulm.ki.panda3.symbolic.domain.{Domain, Task}
import de.uniulm.ki.panda3.symbolic.logic.Predicate
import de.uniulm.ki.panda3.symbolic.plan.Plan
import de.uniulm.ki.panda3.symbolic.sat.additionalConstraints.WithRelevantPredicates
import de.uniulm.ki.panda3.symbolic.sat.verify.AdditionalEdgesInDisablingGraph
import de.uniulm.ki.util.{DirectedGraph, Dot2PdfCompiler, SimpleDirectedGraph}

/**
  * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
  */
case class IntProblem(domain: Domain, initialPlan: Plan,
                      additionalEdgesInDisablingGraph: Seq[AdditionalEdgesInDisablingGraph],
                     withRelevantPredicates : Seq[WithRelevantPredicates]) {

  // this is expensive, but *never* turn it off. Not using invariants makes SAT formulae essentially unusable
  val useInvariants: Boolean = true

  lazy val pMap: Map[Predicate, Int] = domain.predicates.zipWithIndex map { case (p, i) => (p, i + 1) } toMap
  lazy val mapP: Map[Int, Predicate] = pMap map { _.swap }

  case class IntTask(task: Task) {
    val preList: Array[Int] = task.posPreconditionAsPredicate.map(pMap).toArray
    val addList: Array[Int] = task.addEffectsAsPredicate.map(pMap).toArray
    val delList: Array[Int] = task.delEffectsAsPredicate.map(pMap).toArray

    val pre: util.BitSet = preList.foldLeft(new util.BitSet())({ case (b, i) => b.set(i); b })
    val add: util.BitSet = addList.foldLeft(new util.BitSet())({ case (b, i) => b.set(i); b })
    val del: util.BitSet = delList.foldLeft(new util.BitSet())({ case (b, i) => b.set(i); b })

    val changed: util.BitSet = {
      val r = new util.BitSet()
      r.or(add)
      r.or(del)
      r
    }

    val deletesPosP: Array[Boolean] = domain.predicates.indices map { i => del get (i + 1) } toArray
    val deletesNegP: Array[Boolean] = domain.predicates.indices map { i => add get (i + 1) } toArray

    val invertedEffects: Array[Int] = (addList map { p1 => -p1 }) ++ delList

    def hasMoreEffectsRelativeToPredicates(other: IntTask, preds: Set[Predicate]): Boolean = preds exists { p =>
      val pi: Int = pMap(p)

      (add.get(pi) && !other.add.get(pi)) || (del.get(pi) && !other.del.get(pi))
    }
  }

  lazy val intTasks: Array[IntTask] = domain.primitiveTasks map { IntTask } toArray

  lazy val invariants: Set[(Int, Int)] = if (!useInvariants) Set() else {
    println("Computing invariants [Rintanen]")
    val v0: Seq[(Int, Int)] =
      domain.predicates flatMap { p1 =>
        val l1 = (p1, initialPlan.init.schema.addEffectsAsPredicateSet contains p1)
        domain.predicates collect { case p2 if p1 != p2 => (l1, (p2, true)) :: (l1, (p2, false)) :: Nil } flatten
      } map { case ((ap, ab), (bp, bb)) if pMap(ap) < pMap(bp) => ((ap, ab), (bp, bb)); case ((ap, ab), (bp, bb)) if pMap(bp) < pMap(ap) => ((bp, bb), (ap, ab)) } map {
        case ((ap, ab), (bp, bb)) => (pMap(ap) * (if (ab) 1 else -1), pMap(bp) * (if (bb) 1 else -1))
      } distinct

    assert(v0 forall {case (a,b) => Math.abs(a) < Math.abs(b)})


    println("candidates build " + v0.size)

    def filter(invar: Array[(Int, Int)], tasks: Seq[IntTask]): Array[(Int, Int)] = {
      // marked for deleteion
      val toDelete = new Array[Boolean](invar.length)

      // sort to relevant ones
      val invarsPerPredicate: Array[util.BitSet] = new Array[util.BitSet](domain.predicates.length + 1)
      invarsPerPredicate.indices foreach { i => invarsPerPredicate(i) = new util.BitSet() }

      invar.zipWithIndex foreach { case ((a, b), i) =>
        val ap = Math.abs(a)
        val bp = Math.abs(b)

        invarsPerPredicate(ap).set(i)
        invarsPerPredicate(bp).set(i)
      }

      var nc = 0

      tasks.zipWithIndex foreach { case (task, tIndex) =>
        val (posInferredPreconditions, negInferredPreconditions) = {
          val pbs = new util.BitSet()
          val nbs = new util.BitSet()

          var c = task.pre.nextSetBit(0)
          while (c >= 0) {
            val candis = invarsPerPredicate(c)

            var cand = candis.nextSetBit(0)
            while (cand >= 0) {
              if (!toDelete(cand)) {
                invar(cand) match {
                  case (a, b) if task.pre.get(Math.abs(a)) && a < 0 => if (b > 0) pbs.set(b) else nbs.set(-b)
                  case (a, b) if task.pre.get(Math.abs(b)) && b < 0 => if (a > 0) pbs.set(a) else nbs.set(-a)
                  case _                                            =>
                }
              }
              cand = candis.nextSetBit(cand + 1)
            }
            c = task.pre.nextSetBit(c + 1)
          }


          (pbs, nbs)
        }

        val ensuresPosP: Array[Boolean] = domain.predicates.indices map { i =>
          val ap = i + 1
          (task.add get ap) || (!task.del.get(ap) && (task.pre.get(ap) || posInferredPreconditions.get(ap)))
        } toArray

        val ensuresNegP: Array[Boolean] = domain.predicates.indices map { i =>
          val ap = i + 1
          (task.del get ap) || (!task.add.get(ap) && negInferredPreconditions.get(ap))
        } toArray

        var c = task.changed.nextSetBit(0)
        while (c >= 0) {
          val candis = invarsPerPredicate(c)

          var cand = candis.nextSetBit(0)
          while (cand >= 0) {
            if (!toDelete(cand)) {
              invar(cand) match {
                case (a, b) if !(task.changed.get(Math.abs(a)) || task.changed.get(Math.abs(b))) =>
                  //println("This should not happen")
                  //System exit 0
                  true
                case (a, b)                                                                      =>
                  val ap = Math.abs(a)
                  val bp = Math.abs(b)
                  val ab = a > 0
                  val bb = b > 0

                  val deletesA = if (ab) task.deletesPosP(ap - 1) else task.deletesNegP(ap - 1)
                  val deletesB = if (bb) task.deletesPosP(bp - 1) else task.deletesNegP(bp - 1)
                  val ensuresA = if (ab) ensuresPosP(ap - 1) else ensuresNegP(ap - 1)
                  val ensuresB = if (bb) ensuresPosP(bp - 1) else ensuresNegP(bp - 1)

                  if (!((!deletesA || ensuresB) && (!deletesB || ensuresA))) {toDelete(cand) = true; nc += 1 }
              }

            }

            cand = candis.nextSetBit(cand + 1)
          }
          c = task.changed.nextSetBit(c + 1)
        }
        if (tIndex % 500 == 0) println("Size " + (invar.length - nc) + " at " + tIndex + "/" + domain.primitiveTasks.size)
      }

      //println(invar.size + " " + nc)
      invar zip toDelete collect { case (i, false) => i }
      //inv collect { case (i, false) => i }
    }

    //println(domain.predicates.sorted mkString "\n")

    def reduce(invar: Array[(Int, Int)]): Array[(Int, Int)] = {
      val r = filter(invar, intTasks)
      //println(t.name)
      //if (v.size == 1) println(v.head._1._1.name + " " + v.head._1._2 + " v " + v.head._2._1.name + " " + v.head._2._2)
      //if (i % 100 == 0) {
      println("Size " + r.size)
      //}
      if (r.size == invar.size) r else reduce(r)
    }

    val time001 = System.currentTimeMillis()
    val res = reduce(v0.toArray).toSet[(Int, Int)]
    val time002 = System.currentTimeMillis()
    println("Invariant time: " + (time002 - time001) / 1000.0)
    println("Number of invariants: " + res.size)
    //println(res map {case (a,b) => a + " v " + b} mkString "\n")
    //println(res map {case (a,b) => (if (a < 0) "not " else "") + mapP(Math.abs(a)).name + " v " + (if (b < 0) "not " else "") + mapP(Math.abs(b)).name} mkString "\n")

    res
  }

  private lazy val groupedInvariantsOffset               = domain.predicates.length + 2
  private lazy val groupedInvariants: Array[util.BitSet] = {
    val group = invariants.groupBy(_._1)
    // 0 is useless
    Range(0, 2 * domain.predicates.length + 5) map { case p =>
      val bs = new util.BitSet()
      group.getOrElse(p - groupedInvariantsOffset, Nil) foreach { case (_, i) => bs set (i + groupedInvariantsOffset) }
      bs
    } toArray
  }

  def checkInvariant(a: Int, b: Int): Boolean =
    if (Math.abs(a) < Math.abs(b)) groupedInvariants(a + groupedInvariantsOffset).get(b + groupedInvariantsOffset)
    else if (Math.abs(a) > Math.abs(b)) groupedInvariants(b + groupedInvariantsOffset).get(a + groupedInvariantsOffset)
    else false

  lazy val symbolicInvariants    : Set[((Predicate, Boolean), (Predicate, Boolean))]   = invariants map { case (a, b) => ((mapP(Math.abs(a)), a > 0), (mapP(Math.abs(b)), b > 0)) }
  lazy val symbolicInvariantArray: Array[((Predicate, Boolean), (Predicate, Boolean))] = symbolicInvariants.toArray

  // the non-extended disabling graph will contain only those edges implies by the problem itself, not by additional constraints (like LTL)
  lazy val (disablingGraph, nonExtendedDisablingGraph): (DirectedGraph[Task], DirectedGraph[Task]) = {
    println("Computing disabling graph")
    val time1 = System.currentTimeMillis()

    def applicable(task1: IntTask, task2: IntTask): Boolean = {
      //println("\n\n")
      //println(task1.task.name + " " + task2.task.name)
      var counter = false
      // incompatibe preconditions via invariants
      var i = 0
      while (!counter && i < task1.preList.length) {
        var j = 0
        while (!counter && j < task2.preList.length) {
          counter |= checkInvariant(-task1.preList(i), -task2.preList(j))
          //println("Checking: " + task1.preList(i) + " " + task2.preList(j) + " " + counter)
          //println("Checking: " + mapP(task1.preList(i)).name + " " + mapP(task2.preList(j)).name + " " + counter)
          j += 1
        }
        i += 1
      }
      // incompatible effects via invariants
      i = 0
      while (!counter && i < task1.invertedEffects.length) {
        var j = 0
        while (!counter && j < task2.invertedEffects.length) {
          counter |= checkInvariant(task1.invertedEffects(i), task2.invertedEffects(j))
          j += 1
        }
        i += 1
      }
      // are applicable if we have not found a counter example
      !counter
    }

    def affects(task1: Task, task2: Task): Boolean = task1.delEffectsAsPredicate exists task2.posPreconditionAsPredicateSet.contains

    // compute affection
    val predicateToAdding: Map[Predicate, Array[IntTask]] =
      intTasks flatMap { t => t.task.addEffectsAsPredicate map { e => (t, e) } } groupBy (_._2) map { case (p, as) => p -> as.map(_._1) }

    val predicateToDeleting: Map[Predicate, Array[IntTask]] =
      intTasks flatMap { t => t.task.delEffectsAsPredicate map { e => (t, e) } } groupBy (_._2) map { case (p, as) => p -> as.map(_._1) }

    val predicateToNeeding: Map[Predicate, Array[IntTask]] =
      intTasks flatMap { t => t.task.posPreconditionAsPredicate map { e => (t, e) } } groupBy (_._2) map { case (p, as) => p -> as.map(_._1) }

    val edgesWithDuplicats: Seq[(IntTask, IntTask)] =
      predicateToDeleting.toSeq flatMap { case (p, as) => predicateToNeeding.getOrElse(p, new Array(0)) flatMap { n => as map { d => (d, n) } } }
    val alwaysEdges: Seq[(IntTask, IntTask)] = (edgesWithDuplicats groupBy { _._1 } toSeq) flatMap { case (t1, t2) => (t2 map {
      _._2
    } distinct) collect { case t if t != t1 => (t1, t) }
    }
    val additionalEdges = additionalEdgesInDisablingGraph.flatMap(_.additionalEdges(this)(predicateToAdding, predicateToDeleting, predicateToNeeding))
    val time12 = System.currentTimeMillis()
    println("Candidates (" + alwaysEdges.length + " & " + additionalEdges.length + ") generated: " + ((time12 - time1) / 1000))

    val alwaysApplicableEdges = alwaysEdges collect { case (a, b) if applicable(a, b) => (a.task, b.task) }
    val additionalApplicableEdges = additionalEdges collect { case (a, b) if applicable(a, b) => (a.task, b.task) }

    val fullDG = SimpleDirectedGraph(domain.primitiveTasks, (alwaysApplicableEdges ++ additionalApplicableEdges).distinct)
    val nonExtendedDG = SimpleDirectedGraph(domain.primitiveTasks, alwaysApplicableEdges.distinct)

    val time2 = System.currentTimeMillis()
    println("EDGELIST " + fullDG.edgeList.length + " of " + fullDG.vertices.size * (fullDG.vertices.size - 1) + " in " + (time2 - time1) / 1000.0)
    val allSCCS = fullDG.stronglyConnectedComponents
    val time3 = System.currentTimeMillis()
    println(((allSCCS map { _.size } groupBy { x => x }).toSeq.sortBy(_._1) map { case (k, s) => s.size + "x" + k } mkString ", ") + " in " + (time3 - time2) / 1000.0)

    //println(fullDG.edgeList map {case (t1,t2) => t1.name + " -> " + t2.name} mkString "\n")

    (fullDG, nonExtendedDG)
  }

  lazy val disablingGraphSCCOrdering         : Array[Array[Task]]        = disablingGraph.condensation.topologicalOrdering.get.reverse map { scc =>
    // try to find a hint to the order in the non-extended disabling graph
    val reducedGraph = SimpleDirectedGraph(scc.toSeq, nonExtendedDisablingGraph.edgesSet collect { case (from, to) if scc contains from => from -> (scc intersect to).toSeq })
    reducedGraph.condensation.topologicalOrdering.get.reverse flatMap { _.toSeq } toArray
  } toArray
  lazy val disablingGraphSCCOrderingWithIndex: Array[Array[(Task, Int)]] = disablingGraphSCCOrdering map { _.zipWithIndex }
  lazy val disablingGraphTotalOrderSCCIndex  : Array[Int]                = disablingGraphSCCOrdering.zipWithIndex.flatMap({ case (ts, i) => ts map { _ => i } }).toArray
  lazy val disablingGraphTotalOrder          : Array[Task]               = disablingGraphSCCOrdering.flatten.toArray

  //Dot2PdfCompiler.writeDotToFile(disablingGraph, "disablingGraph.pdf")
  //println("Disabling Graph Order:\n" + disablingGraphTotalOrder.map(_.name).mkString("\n"))

  //println("Non trivial SCCs")
  //println(scc.filter(_.size > 1) map {s => s.map(_.name).mkString(", ")} mkString("\n"))

  //System exit 0


  lazy val existsStepERPerPredicate: Seq[(Array[(Task, Int)], Array[(Task, Int)], String)] = (domain.predicates flatMap { case m =>
    disablingGraphSCCOrderingWithIndex.zipWithIndex map { case (disOrder, disIndex) =>
      val E: Array[(Task, Int)] = disOrder filter { _._1.delEffectsAsPredicateSet contains m }
      val R: Array[(Task, Int)] = disOrder filter { _._1.posPreconditionAsPredicateSet contains m }
      val chainID: String = "p_" + m.name + "_scc_" + disIndex
      (E, R, chainID)
    }
  }) filter { case (e, r, _) => e.nonEmpty && r.nonEmpty }

  lazy val mattmüllerLTLERPerPredicates: Map[Predicate, Array[(Array[(Task, Int)], Array[(Task, Int)], String)]] = domain.predicates map { m =>
    m -> (disablingGraphSCCOrderingWithIndex.zipWithIndex map { case (disOrder, disIndex) =>
      val E: Array[(Task, Int)] = disOrder filter { case (a, _) => !a.delEffectsAsPredicateSet.contains(m) && !a.addEffectsAsPredicateSet.contains(m) }
      val R: Array[(Task, Int)] = disOrder filter { case (a, _) => a.delEffectsAsPredicateSet.contains(m) || a.addEffectsAsPredicateSet.contains(m) }
      val chainID: String = "ltl_" + m.name + "_scc_" + disIndex

      (E, R, chainID)
    } filter { case (e, r, _) => e.nonEmpty && r.nonEmpty })
  } toMap


  // on parallel LTL encoding
  // determine places of intermediate steps in one exists-step phase. We have to compute the full relevant state whenever one action altering said state has been executed
  // position i here means that we have to check *after* action i was executed
  lazy val ltlCheckPositions : Map[WithRelevantPredicates, Seq[Int]] = withRelevantPredicates map { rel =>
    val res = disablingGraphTotalOrder.zipWithIndex.foldLeft[(Set[Predicate], Seq[Int])]((Set(), Nil))(
      { case ((currentPredicates, breaksSoFar), (task, taskIndex)) =>
        val currentEffects = task.effectAsPredicateSet intersect rel.relevantPredicates

        // we can continue the sequence started by the last task
        val taskIndices = if (currentEffects.subsetOf(currentPredicates) && taskIndex != 0) breaksSoFar else breaksSoFar :+ (taskIndex - 1)

        (currentEffects, taskIndices)
      })._2 :+ (disablingGraphTotalOrder.length - 1) // we always have to check after the last position ...
    println(res mkString ", ")
    //println(disablingGraphTotalOrder.zipWithIndex map { case (t,i) => i + " " + t.name } mkString "\n")
    rel -> res
  } toMap

}
